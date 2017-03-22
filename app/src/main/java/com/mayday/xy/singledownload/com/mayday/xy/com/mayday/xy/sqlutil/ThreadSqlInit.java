package com.mayday.xy.singledownload.com.mayday.xy.com.mayday.xy.sqlutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mayday.xy.singledownload.com.mayday.xy.toolutils.ThreadInfo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 对线程数据库的创建
 * Created by xy-pc on 2017/3/14.
 */

public class ThreadSqlInit extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME="ThreadManager.db";
    private static int Version=1;
    private static ThreadSqlInit sqlInit;
    private Map<String ,Dao> maps=new HashMap<>();

    public ThreadSqlInit(Context context) {
        super(context, DATABASE_NAME, null, Version);
    }

    public static synchronized ThreadSqlInit sqlInstance(Context context){
        if(sqlInit==null){
            synchronized (ThreadSqlInit.class){
                if(sqlInit==null){
                    sqlInit=new ThreadSqlInit(context);
                }
            }

        }
        return sqlInit;
    }

    /**
     * 获取实体类的对象
     * @param cls
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class cls) throws SQLException {
        Dao dao=null;
        String clsName = cls.getSimpleName();
        //如果此映射包含对于指定键的映射关系，则返回 true
        if(maps.containsKey(clsName)){
            dao=maps.get(clsName);
        }else {
            dao=super.getDao(cls);
            maps.put(clsName,dao);
        }
        return dao;
    }

    /**
     * 关闭对对象的访问
     */
    public void close(){
        super.close();
        for(String str:maps.keySet()){
            Dao dao = maps.get(str);
            dao=null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            //第二个参数是表的名称
            TableUtils.createTable(connectionSource, ThreadInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }
}
