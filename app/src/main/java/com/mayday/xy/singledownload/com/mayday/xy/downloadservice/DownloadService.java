package com.mayday.xy.singledownload.com.mayday.xy.downloadservice;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mayday.xy.singledownload.com.mayday.xy.toolutils.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadService extends Service {

    public static final String TAG = DownloadService.class.getSimpleName();
    public static final int THREAD_COUNT_NUM=3;
    public static final String DOWN_START = "DOWN_START";
    public static final String DOWN_STOP = "DOWN_STOP";
    public static final String DOWN_FINISHED="DOWN_FINISHED";
    public static final String DOWN_END="DOWN_END";
    public static final int Mess_NUM = 0x1;
    private OkHttpClient client;
    //管理下载任务的集合(因为我们这里是对多个文件进行下载)
    private Map<Integer,DownloadFile> map=new LinkedHashMap<>();

    public static final String SDPath = Environment.getExternalStorageDirectory().getPath()+"/downloads/";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FileInfo fileinfo=null;
        if (DOWN_START.equals(intent.getAction())) {
            //拿到fileinfo的数据（此时拿到的只有id,url和文件的name)
            fileinfo = (FileInfo) intent.getSerializableExtra("fileinfo");
            Log.i(TAG, "start" + fileinfo.toString());
            new myDownThread(fileinfo).start();
        } else if (DOWN_STOP.equals(intent.getAction())) {
             fileinfo = (FileInfo) intent.getSerializableExtra("fileinfo");
            //暂停下载
            DownloadFile downloadFile = map.get(fileinfo.getId());
            if(downloadFile !=null){
                downloadFile.isPause=true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 对文件的长度进行捕获，然后启动下载任务进行处理
     */
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Mess_NUM:
                    //这里拿到文件的长度(url,name,length)
                    FileInfo fileinfo= (FileInfo) msg.obj;
                    //启动下载任务
                    DownloadFile downloadFile=new DownloadFile(DownloadService.this,fileinfo,THREAD_COUNT_NUM);
                    downloadFile.download();
                    //把下载任务添加到集合中,因为我们这里是对多文件的一个下载
                    map.put(fileinfo.getId(),downloadFile);
                    break;
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 子线程的获取下载文件长度
     */
    class myDownThread extends Thread {
        private FileInfo fileinfo;
//        RandomAccessFile randomAccessFile=null;
        public myDownThread(FileInfo fileinfo) {
            this.fileinfo = fileinfo;
        }

        @Override
        public void run() {
            try{
                client=new OkHttpClient();

                Request request = new Request.Builder().url(fileinfo.getUrl()).build();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int fileLength=-1;
                        if (!response.isSuccessful()) {
                        } else {
                            if(response!=null){
                                //获取到文件的长度
                                fileLength = (int) response.body().contentLength();
                            }
                        }
                        //在本地创建文件
                        File file=new File(SDPath);
                        if(!file.exists()){
                            file.mkdir();
                        }
                        //保存在该目录下
                        File file1=new File(file,fileinfo.getFileName());
                        //随机访问的文件，在文件的任意一个位置进行写入操作(断点续传)
//                        randomAccessFile=new RandomAccessFile(file1,"rwd");
//                        randomAccessFile.setLength(fileLength);
                        //给本地文件设置一个长度
                        fileinfo.setLength(fileLength);
                        //发送给service
//                        myHandler.obtainMessage(Mess_NUM,fileinfo).sendToTarget();
                        //获得Message对象
//                        Message message = myHandler.obtainMessage();
                        Message message=new Message();
                        //获得唯一标识符
                        message.what=Mess_NUM;
                        //传入要发送的文件信息
                        message.obj=fileinfo;
                        //发送给Handler处理
                        myHandler.sendMessage(message);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
