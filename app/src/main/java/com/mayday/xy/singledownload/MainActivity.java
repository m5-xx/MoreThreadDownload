package com.mayday.xy.singledownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.mayday.xy.singledownload.com.mayday.xy.downloadservice.DownloadService;
import com.mayday.xy.singledownload.com.mayday.xy.listViewUtil.listAdapter;
import com.mayday.xy.singledownload.com.mayday.xy.toolutils.FileInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    private ListView listView;
    private listAdapter adapter;
    private ArrayList<FileInfo> list;


    public static final String KGMusicPath="http://download.kugou.com/download/kugou_pc";
    public static final String KGDownloadFileName="kugou8141.exe";
    public static final String MukewangAPK="http://www.imooc.com/mobile/mukewang.apk";
    public static final String MKDownloadFileName="mukewang.apk";
    public static final String CSDNAPK="http://csdn-app.csdn.net/csdn.apk";
    public static final String CSDNDownloadFilename="csdn.apk";
    public static final String QQPC="http://dldir1.qq.com/qqfile/qq/QQ8.9/20029/QQ8.9.exe";
    public static final String QQDownloadFilename="qq8.9.exe";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= (ListView) findViewById(R.id.listview);

        list=new ArrayList<>();
        //初始化FielInfo
        initFileinfo();
        adapter=new listAdapter(MainActivity.this,list);
        listView.setAdapter(adapter);

        /**
         * 注册广播接收器
         */
        IntentFilter filter=new IntentFilter();
        //当前进度
        filter.addAction(DownloadService.DOWN_FINISHED);
        //下载完毕
        filter.addAction(DownloadService.DOWN_END);
        registerReceiver(mReceiver,filter);
    }

    private void initFileinfo() {
        FileInfo fileinfo1=new FileInfo(0,KGMusicPath,KGDownloadFileName,0,0l);
        FileInfo fileinfo2=new FileInfo(1,MukewangAPK,MKDownloadFileName,0,0l);
        FileInfo fileinfo3=new FileInfo(2,CSDNAPK,CSDNDownloadFilename,0,0l);
        FileInfo fileinfo4=new FileInfo(3,QQPC,QQDownloadFilename,0,0l);
        list.add(fileinfo1);
        list.add(fileinfo2);
        list.add(fileinfo3);
        list.add(fileinfo4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭广播
        unregisterReceiver(mReceiver);
    }

    /**
     * 定义广播接收器
     * 通过传过来的参数进行更新seekbar的进度以及某个任务结束的id
     */
    BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.DOWN_FINISHED.equals(intent.getAction())){
                long finished = intent.getLongExtra("finished", 0l);
                int id=intent.getIntExtra("id",0);//如果没找到，默认为0
                adapter.updataProgress(id,finished);
            }else if(DownloadService.DOWN_END.equals(intent.getAction())){
                //如果是结束就设置进度条为0
                //拿到fileinfo的数据
                FileInfo fileinfo= (FileInfo) intent.getSerializableExtra("fileinfo");
                adapter.updataProgress(fileinfo.getId(),0l);
                //提示哪一个文件下载完毕
                Toast.makeText(context,list.get(fileinfo.getId()).getFileName()+"下载完成",Toast.LENGTH_SHORT).show();
            }
        }
    };

}
