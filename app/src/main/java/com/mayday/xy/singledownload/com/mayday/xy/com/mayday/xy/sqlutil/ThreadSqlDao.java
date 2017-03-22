package com.mayday.xy.singledownload.com.mayday.xy.com.mayday.xy.sqlutil;

import android.content.Context;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.mayday.xy.singledownload.com.mayday.xy.toolutils.ThreadInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * 线程数据库的访问
 * Created by xy-pc on 2017/3/14.
 */

public class ThreadSqlDao {
    private Context context;
    private ThreadSqlInit sqlInit;
    public Dao<ThreadInfo,Integer> dao;

    public ThreadSqlDao(Context context) {
        this.context=context;
        sqlInit=sqlInit.sqlInstance(context);
        try {
            dao=sqlInit.getDao(ThreadInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加ThreadInfo数据到数据库
     * @param threadInfo
     */
    public void insertData(ThreadInfo threadInfo){
        try {
            dao.create(threadInfo);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 通过id和url来删除某一线程
     * @param url
     */
    public void deleData(String url){
        DeleteBuilder builder = dao.deleteBuilder();
        try {
            //delete from ThreadInfo where id=? and url=?;
            builder.where().eq("url",url);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过id来对某一个线程进行进度的更新
     * @param Threadid
     * @param url
     * @param finished
     * @throws SQLException
     */
    public void updatable(int Threadid,String url,long finished) throws SQLException {
        UpdateBuilder builder = dao.updateBuilder();
        builder.updateColumnValue("url",url);
        builder.updateColumnValue("finished",finished);
        builder.where().eq("id",Threadid);
        builder.update();
    }

    /**
     * 查询此时的某一个url被几个线程加载
     * @param url
     * @return
     */
    public List<ThreadInfo> selectData(String url){
        List<ThreadInfo> list = null;

        ThreadInfo threadInfo=new ThreadInfo();
        QueryBuilder<ThreadInfo,Integer> builder = dao.queryBuilder();
        try {
            list= builder.where().eq("url", url).query();
            for(ThreadInfo lis:list){
                threadInfo.setId(lis.getId());
                threadInfo.setUrl(lis.getUrl());
                threadInfo.setStart(lis.getStart());
                threadInfo.setEnd(lis.getEnd());
                threadInfo.setFinished(lis.getFinished());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 通过线程id查询是否存在该线程
     * @param url
     * @param Threadid
     * @return
     */
    public boolean isExist(String url,int Threadid) throws SQLException {
        List<ThreadInfo> list=null;
        QueryBuilder builder = dao.queryBuilder();
        //select * from ThreadInfo where url=url and id = Threadid;
        list=builder.where().eq("url", url).and().eq("id", Threadid).query();
        if(list.size()==0){
            return false;
        }
        return true;
    }

}
