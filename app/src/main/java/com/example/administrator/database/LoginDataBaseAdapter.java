package com.example.administrator.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LoginDataBaseAdapter{

    private DataBaseHelper dbHelper;
    public LoginDataBaseAdapter(Context context){
        dbHelper=new DataBaseHelper(context);
    }

    //��¼��
    public String login(String username,String password ){
        String ret;
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="select * from user where username=?";
        Cursor cursor=sdb.rawQuery(sql, new String[]{username});
        if(cursor.moveToFirst()==true){
            if( password.equals(cursor.getString(cursor.getColumnIndex("password"))) == false ){
                cursor.close();
                sdb.close();
                ret = "False";
                return ret;
            }
            ret = cursor.getString(cursor.getColumnIndex("stat"));
            Log.v("tag", username+ret);
        }else{
            ret = "10";
            User user=new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setStat(ret);
            this.register(user);
        }
        Log.v("tag", username+ret);
        cursor.close();
        sdb.close();
        return ret;
    }

    public String getRemberUser(){
        String username=null;
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="select * from user where substr(stat,2,2)='1'";
        Cursor cursor=sdb.rawQuery(sql, null);
        if(cursor.moveToFirst()==true){
            username =cursor.getString(  cursor.getColumnIndex("username") );
        }
        cursor.close();
        sdb.close();
        return username;
    }
    public String remberUser(String username){
        String sql,string1;
        String stat=null;
        //Object[] arrayOfObject = new Object[2];
        SQLiteDatabase sdb;
        Cursor cursor;
        char[] a;

        sdb =dbHelper.getReadableDatabase();
        sql="select * from user where username = '"+username+"'";
        cursor=sdb.rawQuery(sql, null);
        if(cursor.moveToFirst()==true){
            stat =cursor.getString(  cursor.getColumnIndex("stat") );
        }
        string1 = stat.substring( 0,1) + "1";
       // Log.v("tag",stat);
       // Log.v("tag",string1);
        sdb=dbHelper.getWritableDatabase();
        sql="update user set stat='"+string1+"' where username ='"+username+"'";
        sdb.execSQL(sql);

        cursor.close();
        sdb.close();
        return username;
    }

    public String unremberUser(String username){
        String sql,string1;
        String stat=null;
        //Object[] arrayOfObject = new Object[2];
        SQLiteDatabase sdb;
        Cursor cursor;
        char[] a;

        sdb =dbHelper.getReadableDatabase();
        sql="select * from user where username = '"+username+"'";
        cursor=sdb.rawQuery(sql, null);
        if(cursor.moveToFirst()==true){
            stat =cursor.getString(  cursor.getColumnIndex("stat") );
        }
        string1 = stat.substring( 0,1) + "0";
      //  Log.v("tag",stat);
       // Log.v("tag",string1);
        sdb=dbHelper.getWritableDatabase();
        sql="update user set stat='"+string1+"' where username ='"+username+"'";
        sdb.execSQL(sql);

        cursor.close();
        sdb.close();
        return username;
    }

    //注册用户
    public boolean register(User user){
        SQLiteDatabase sdb=dbHelper.getReadableDatabase();
        String sql="insert into user(username,password,stat) values(?,?,?)";
        Object obj[]={user.getUsername(),user.getPassword(),user.getStat()};
        sdb.execSQL(sql, obj);
        sdb.close();
        return true;
    }

}
