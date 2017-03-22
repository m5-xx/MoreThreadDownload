package com.mayday.xy.singledownload.com.mayday.xy.toolutils;

import java.io.Serializable;

/**
 * 文件信息类
 * Created by xy-pc on 2017/3/14.
 */

public class FileInfo implements Serializable{
    private int id;
    //下载文件的Url地址
    private String url;
    private String fileName;
    //文件的总长度
    private int length;
    //文件的当前进度
    private long finisher;

    public FileInfo() {
        super();
    }

    public FileInfo(int id, String url, String fileName, int length, long finisher) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.finisher = finisher;
    }

    public long getFinisher() {
        return finisher;
    }

    public void setFinisher(long finisher) {
        this.finisher = finisher;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finisher=" + finisher +
                '}';
    }
}
