package com.mayday.xy.singledownload.com.mayday.xy.toolutils;

import com.j256.ormlite.field.DatabaseField;

/**
 * 线程信息类
 * Created by xy-pc on 2017/3/14.
 */

public class ThreadInfo {
    //自增长模式
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "url")
    private String url;
    //线程的开始
    @DatabaseField(columnName = "start")
    private int start;
    //线程的结束
    @DatabaseField(columnName = "end")
    private int end;
    //线程的进度
    @DatabaseField(columnName = "finished")
    private int finished;

    public ThreadInfo() {
        super();
    }

    public ThreadInfo(int id, String url, int start, int end, int finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", finished='" + finished + '\'' +
                '}';
    }
}
