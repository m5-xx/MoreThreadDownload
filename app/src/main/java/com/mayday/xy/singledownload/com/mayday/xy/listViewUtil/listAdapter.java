package com.mayday.xy.singledownload.com.mayday.xy.listViewUtil;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.query.In;
import com.mayday.xy.singledownload.R;
import com.mayday.xy.singledownload.com.mayday.xy.downloadservice.DownloadService;
import com.mayday.xy.singledownload.com.mayday.xy.toolutils.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy-pc on 2017/3/19.
 */

public class listAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FileInfo> fileInfos;
    private viewHolder holder = null;

//    private static final String TAG=listAdapter.class.getSimpleName();

    public listAdapter(Context context, ArrayList<FileInfo> fileInfo) {
        this.context = context;
        this.fileInfos = fileInfo;
    }

    @Override
    public int getCount() {
        return fileInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return fileInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final FileInfo fileInfo = fileInfos.get(i);

        if (view == null) {

            holder = new viewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_view, null);

            holder.filename = (TextView) view.findViewById(R.id.filename);
            holder.seekBar = (SeekBar) view.findViewById(R.id.seekbar);
            holder.start = (Button) view.findViewById(R.id.start);
            holder.stop = (Button) view.findViewById(R.id.stop);

            holder.filename.setText(fileInfo.getFileName());
            holder.seekBar.setMax(100);//进度的最大值设为100/线程个数

            holder.start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.DOWN_START);
                    intent.putExtra("fileinfo", fileInfo);
                    context.startService(intent);
                }
            });

            holder.stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, fileInfo.getId() + " 暂停按钮触发", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(context, DownloadService.class);
                    intent1.setAction(DownloadService.DOWN_STOP);
                    intent1.putExtra("fileinfo", fileInfo);
                    context.startService(intent1);
                }
            });

            view.setTag(holder);
        }else {
            holder = (viewHolder) view.getTag();
        }
        //这里seekbar的进度则是fineinfo.getFinisher()
        holder.seekBar.setProgress((int) fileInfo.getFinisher());
        return view;
    }

    //更新某个任务的进度条
    public void updataProgress(int id, long prog) {
        FileInfo fileInfo = fileInfos.get(id);//返回一个文件对象
        fileInfo.setFinisher(prog);
        notifyDataSetChanged();
    }

    static class viewHolder {
        TextView filename;
        SeekBar seekBar;
        Button start;
        Button stop;
    }
}
