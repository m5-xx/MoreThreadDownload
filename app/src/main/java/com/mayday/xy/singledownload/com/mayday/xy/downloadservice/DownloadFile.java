package com.mayday.xy.singledownload.com.mayday.xy.downloadservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mayday.xy.singledownload.com.mayday.xy.com.mayday.xy.sqlutil.ThreadSqlDao;
import com.mayday.xy.singledownload.com.mayday.xy.toolutils.FileInfo;
import com.mayday.xy.singledownload.com.mayday.xy.toolutils.ThreadInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载任务类
 * Created by xy-pc on 2017/3/17.
 */

public class DownloadFile {
    private Context context;
    private FileInfo fileInfo;
    private ThreadSqlDao threadDao;
    private long mFinished = 0;
    //暂停变量
    boolean isPause = false;
    private int threadCount = 1;//设置线程的下载个数(默认为1)
    public boolean allFishied = false;//判断是否下载完毕
    private List<mDownThread> dList = null;//管理下载线程
    private static final String TAG = DownloadFile.class.getSimpleName();
    private static long time = 0l;

    public DownloadFile(Context context, FileInfo fileInfo, int threadCount) {
        this.context = context;
        this.fileInfo = fileInfo;
        this.threadCount = threadCount;
        threadDao = new ThreadSqlDao(context);
    }

    //开启下载线程方法
    public void download() {
        //读取数据库的线程信息
        List<ThreadInfo> threadInfos = threadDao.selectData(fileInfo.getUrl());
        if (threadInfos.size() == 0) {
            ThreadInfo threadInfo = null;
            int length = fileInfo.getLength() / threadCount;
            for (int i = 0; i < threadCount; i++) {
                //分配给每个线程信息的长度
                threadInfo = new ThreadInfo(i, fileInfo.getUrl(), i * length, (i + 1) * length - 1, 0);
                if (i == threadCount - 1) {
                    threadInfo.setEnd(fileInfo.getLength());
                }
                threadInfos.add(threadInfo);
                //如果不存在则向线程数据库中插入线程信息
                threadDao.insertData(threadInfo);
            }
        }
        dList = new ArrayList<>();
        //遍历线程信息(某几个线程信息)，启动多个线程任务进行下载并管理这些线程
        for (ThreadInfo info : threadInfos) {
            Log.i(TAG, "开启线程的分别是: " + info.toString());
            mDownThread thread = new mDownThread(info);
            thread.start();
            dList.add(thread);
        }

    }

    /**
     * 判断是否所有的线程都下载完毕
     * 同步方法保证每次只有一个线程可以进入
     */

    public synchronized void allthreaddownloadfinished() {
        allFishied = true;
        //遍历每一个下载线程，
        for (mDownThread threads : dList) {
            if (!threads.isFinished) {
                allFishied = false;
                break;
            }
        }
        //如果所有的线程都下载完毕，就发送广播给Activity通知UI下载完毕
        if (allFishied) {
            //下载完成后删除所有线程信息
            threadDao.deleData(fileInfo.getUrl());
            Intent intent = new Intent(DownloadService.DOWN_END);
            intent.putExtra("fileinfo", fileInfo);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 下载线程
     */
    class mDownThread extends Thread {
        private ThreadInfo mThreadInfo;

        public boolean isFinished = false;//每一个线程是否执行完毕

        public mDownThread(ThreadInfo threadInfo) {
            this.mThreadInfo = threadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            InputStream input = null;
            RandomAccessFile raf = null;
            try {
                //请求网络连接
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                //设置当前下载位置
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                //从当前的start到结束
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                //设置文件写入位置
                File file = new File(DownloadService.SDPath, fileInfo.getFileName());
                //随机访问文件的一个类
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent intent = new Intent(DownloadService.DOWN_FINISHED);
                //获取当前进度(把当前进度通过广播发送给Activity来更新SeekBar)
                mFinished += mThreadInfo.getFinished();
                //开始下载
                if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                    //读取数据
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = input.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        //把下载进度通过广播发送给Activity(整个文件内容)
                        mFinished += len;
                        //保存每一个下载线程的进度
                        mThreadInfo.setFinished(mThreadInfo.getFinished() + len);
                        //每隔1s发送一次广播(降低UI界面的负荷,不然可能会出现卡屏)
                        if (System.currentTimeMillis() - time > 1000l) {
                            time = System.currentTimeMillis();
                            //这里应该把当前完成的进度类型设置为long
                            intent.putExtra("finished", mFinished * 100 / fileInfo.getLength());
                            //把当前某个任务的id发送给Activity
                            intent.putExtra("id", fileInfo.getId());
                            context.sendBroadcast(intent);
                        }

                        //在下载暂停时，保存下载进度
                        if (isPause) {
                            //保存当前线程进度，并跳出循环
                            threadDao.updatable(mThreadInfo.getId(), mThreadInfo.getUrl(), mThreadInfo.getFinished());
                            return;
                        }
                    }
                    //当前线程执行完毕设值为true
                    isFinished = true;
                    allthreaddownloadfinished();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    input.close();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
