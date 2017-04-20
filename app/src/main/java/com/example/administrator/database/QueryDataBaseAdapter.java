package com.example.administrator.database;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.Toast;

import com.example.administrator.poseidon.AddActivity;
import com.example.administrator.poseidon.R;
import com.example.administrator.database.LoginDataBaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/9/8 0008.
 */
public class QueryDataBaseAdapter {
    private DataBaseHelper dbHelper;
    private SQLiteDatabase sdb;
    ArrayList<HashMap<String, Object>> listData;
    String stat;
    LoginDataBaseAdapter loginDataBaseAdapter;

    public QueryDataBaseAdapter(Context context){
        dbHelper=new DataBaseHelper(context);
    }

    public ArrayList<HashMap<String, Object>>  getToolDtlData( String username   ){
        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
     Log.v("tag", stat);
        if ( stat.substring( 0,1).equals("0") ){
            sql="select * from  tool_dtl order by trandate desc";
        }else{
            sql="select * from  tool_dtl  where user=\'"+username+"\' order by trandate desc";
        }
      Log.v("tag",sql);
        Cursor c = sdb.rawQuery( sql,null);

        int count = c.getCount();
        Log.v("tag",String.valueOf(count));

        listData = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        if( count == 0 ){
          //  map.put("img", R.drawable.shop);
            map.put("toolname", "您名下没有任何商品!");
            map.put("sourcefrom", "******");
            map.put("singlesum", "");
            listData.add(map);
        }else {
            // 获取表的内容
           // c.moveToFirst();
            while ( c.moveToNext()) {
                map = new HashMap<String, Object>();
              //  map.put("img", R.drawable.shop);
                map.put("toolname", c.getString(1));
                map.put("sourcefrom", c.getString(0) ) ;
                map.put("trandate", c.getString(2) ) ;
                map.put("singlesum", c.getString(13) );
               // map.put("button_detail", R.drawable.align_right);
                listData.add(map);
            }
        }
        c.close();
        sdb.close();
        return listData;
    }

    public ArrayList<HashMap<String, Object>>  getToolDtlDtl( String username, String toolname, String sourcefrom, String trandate   ){
        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
        //   Log.v("tag", username + stat);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "select * from  tool_dtl  where toolname = '" + toolname + "' and sourcefrom = '" + sourcefrom +
                    "' and trandate = '" + trandate +"'";
        }else {
            sql = "select * from  tool_dtl  where toolname = '" + toolname + "' and sourcefrom = '" + sourcefrom +
                    "' and trandate = '" + trandate +
                    "' and user='" + username + "'";
        }

       // Log.v("tag",sql);
        Cursor cursor = sdb.rawQuery( sql,null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        if(cursor.moveToFirst()==true){
            map = new HashMap<String, Object>();
            map.put("currency", cursor.getString(3));
            map.put("number", cursor.getInt(4) ) ;
            map.put("soldnumber", cursor.getInt(5) ) ;
            map.put("totprice", cursor.getDouble(6) );
            map.put("feepercent", cursor.getInt(7) );
            map.put("feeprice", cursor.getDouble(8));
            map.put("exchangerate", cursor.getDouble(9) );
            map.put("rmb_totprice", cursor.getDouble(10) );
            map.put("rmb_singprice", cursor.getDouble(11) );
            map.put("transprice", cursor.getDouble(12) );
            map.put("singlesum", cursor.getDouble(13) );
            map.put("weight", cursor.getInt(14) );
            map.put("totweight", cursor.getInt(15) );
            map.put("beizhu", cursor.getString(16) );
            map.put("user", cursor.getString(17) );
            listData.add(map);
        }
        cursor.close();
        sdb.close();
        return listData;
    }



    public ArrayList<HashMap<String, Object>>  getToolOrder( String username,String customer,String orderdate  ){
        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
        //   Log.v("tag", username + stat);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "select * from  tool_order  where customer = '" + customer + "' and orderdate = '" + orderdate +"'" ;
        }else {
            sql = "select * from  tool_order  where customer = '" + customer + "' and orderdate = '" + orderdate +
                    "' and user='" + username + "'";
        }

        // Log.v("tag",sql);
        Cursor cursor = sdb.rawQuery( sql,null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        while ( cursor.moveToNext()) {
            map = new HashMap<String, Object>();
            map.put("toolsource", cursor.getString(2));
            map.put("toolname", cursor.getString(3));
            map.put("trandate", cursor.getString(4));
            map.put("number", cursor.getInt(5) ) ;
            map.put("toolprice", cursor.getDouble(6) ) ;
            map.put("exchangerate", cursor.getDouble(7) );
            map.put("toolbuyprice", cursor.getDouble(8) );
            map.put("toolflag", cursor.getString(9));
            map.put("user", cursor.getString(10) );
            listData.add(map);
        }
        cursor.close();
        sdb.close();
        return listData;
    }

    public ArrayList<HashMap<String, Object>>  getToolOrderSum( String username,String customer,String orderdate  ){
        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
        //   Log.v("tag", username + stat);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "select sum(number) as totnum, sum(toolbuyprice) as totprice from  tool_order  where customer = '" + customer + "' and orderdate = '" + orderdate +"'" ;
        }else {
            sql = "select sum(number) as totnum, sum(toolbuyprice) as totprice from  tool_order  where customer = '" + customer + "' and orderdate = '" + orderdate +
                    "' and user='" + username + "'";
        }

        // Log.v("tag",sql);
        Cursor cursor = sdb.rawQuery( sql,null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        if(cursor.moveToFirst()==true){
            map = new HashMap<String, Object>();
            map.put("totnum", cursor.getInt(0));
            map.put("totprice", cursor.getDouble(1));
            listData.add(map);
        }
        cursor.close();
        sdb.close();
        return listData;
    }

    public ArrayList<HashMap<String, Object>>  getToolOrderDtl( String username,String customer,String orderdate,String toolsource,String toolname  ){
        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
        //   Log.v("tag", username + stat);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "select * from  tool_order  where customer = '" + customer + "' and orderdate = '" + orderdate +
                    "' and toolsource = '" + toolsource +"' and toolname = '" + toolname +"'" ;
        }else {
            sql = "select * from  tool_order  where customer = '" + customer + "' and orderdate = '" + orderdate +
                    "' and toolsource = '" + toolsource +"' and toolname = '" + toolname +
                    "' and user='" + username + "'";
        }

        // Log.v("tag",sql);
        Cursor cursor = sdb.rawQuery( sql,null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        if(cursor.moveToFirst()==true){
            map = new HashMap<String, Object>();
            map.put("trandate", cursor.getString(4));
            map.put("number", cursor.getInt(5) ) ;
            map.put("toolprice", cursor.getDouble(6) ) ;
            map.put("exchangerate", cursor.getDouble(7) );
            map.put("toolbuyprice", cursor.getDouble(8) );
            map.put("toolflag", cursor.getString(9));
            map.put("user", cursor.getString(10) );
            listData.add(map);
        }
        cursor.close();
        sdb.close();
        return listData;
    }

    public ArrayList<HashMap<String, Object>>  getCustomOrderDtl( String username,String customer,String orderdate  ){
        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
        //   Log.v("tag", username + stat);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "select * from  custom_order  where customer = '" + customer + "' and orderdate = '" + orderdate +"'" ;
        }else {
            sql = "select * from  custom_order  where customer = '" + customer + "' and orderdate = '" + orderdate +
                    "' and user='" + username + "'";
        }

        // Log.v("tag",sql);
        Cursor cursor = sdb.rawQuery( sql,null);
        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        if(cursor.moveToFirst()==true){
            map = new HashMap<String, Object>();
            map.put("number", cursor.getString(2));
            map.put("totprice", cursor.getInt(3) ) ;
            map.put("needprice", cursor.getDouble(4) ) ;
            map.put("realprice", cursor.getDouble(5) );
            map.put("paychnl", cursor.getString(6) );
            map.put("exprprice", cursor.getDouble(7));
            map.put("exprnum", cursor.getString(8));
            map.put("exprdate", cursor.getString(9));
            map.put("user", cursor.getString(10) );
            listData.add(map);
        }
        cursor.close();
        sdb.close();
        return listData;
    }

    public boolean insertCustomOrder(String username, ArrayList<HashMap<String, Object>> listData ){
        boolean ret = true;
        String customer,orderdate,stat;
        ArrayList<HashMap<String, Object>> tooldtl;
        Object[] arrayOfObject = new Object[11];



        customer = listData.get(0).get("customer").toString();
        orderdate = listData.get(0).get("orderdate").toString();

        tooldtl = getCustomOrderDtl(username, customer, orderdate);
        sdb = dbHelper.getWritableDatabase();
        if( !tooldtl.isEmpty() ){

            arrayOfObject[0] = Integer.valueOf(listData.get(0).get("number").toString());
            arrayOfObject[1] = Double.valueOf(listData.get(0).get("totprice").toString());
            arrayOfObject[2] = Double.valueOf(listData.get(0).get("needprice").toString());
            arrayOfObject[3] = Double.valueOf(listData.get(0).get("realprice").toString());
            arrayOfObject[4] = listData.get(0).get("paychnl").toString();
            arrayOfObject[5] = Double.valueOf(listData.get(0).get("exprprice").toString());
            arrayOfObject[6] = listData.get(0).get("exprnum").toString();
            arrayOfObject[7] = listData.get(0).get("exprdate").toString();
            arrayOfObject[8] = customer;
            arrayOfObject[9] = orderdate;
            arrayOfObject[10] = listData.get(0).get("user").toString();

            sdb.execSQL("update custom_order set number=?,totprice=?,needprice=?,realprice=?,paychnl=?,exprprice=?,exprnum=?,exprdate=? " +
                    "where customer=? and orderdate=? and user=?", arrayOfObject);

        }else {
            arrayOfObject[0] = customer;
            arrayOfObject[1] = orderdate;
            arrayOfObject[2] = Integer.valueOf(listData.get(0).get("number").toString());
            arrayOfObject[3] = Double.valueOf(listData.get(0).get("totprice").toString());
            arrayOfObject[4] = Double.valueOf(listData.get(0).get("needprice").toString());
            arrayOfObject[5] = Double.valueOf(listData.get(0).get("realprice").toString());
            arrayOfObject[6] = listData.get(0).get("paychnl").toString();
            arrayOfObject[7] = Double.valueOf(listData.get(0).get("exprprice").toString());
            arrayOfObject[8] = listData.get(0).get("exprnum").toString();
            arrayOfObject[9] = listData.get(0).get("exprdate").toString();
            arrayOfObject[10] = listData.get(0).get("user").toString();

            sdb.execSQL("Insert into custom_order values( ?,?,?,?,?,?,?,?,?,?,?)", arrayOfObject);
        }
        sdb.close();
        return ret;
    }

     public Integer insertToolDtl(String username, ArrayList<HashMap<String, Object>> listData ){
         Integer ret = 0;
         String toolname,sourcefrom,trandate,stat;
         ArrayList<HashMap<String, Object>> tooldtl;
         Object[] arrayOfObject = new Object[18];

         toolname = listData.get(0).get("toolname").toString();
         sourcefrom = listData.get(0).get("sourcefrom").toString();
         trandate = listData.get(0).get("trandate").toString();

         tooldtl = getToolDtlDtl(username, toolname, sourcefrom, trandate);
         if( tooldtl.isEmpty() == false ){
             return ( -100 );
         }

         sdb = dbHelper.getWritableDatabase();
         arrayOfObject[0] = sourcefrom ;
         arrayOfObject[1] = toolname ;
         arrayOfObject[2] = trandate ;
         arrayOfObject[3] = listData.get(0).get("currency").toString(); ;
         arrayOfObject[4] = Integer.valueOf( listData.get(0).get("number").toString());
         arrayOfObject[5] =  Integer.valueOf( listData.get(0).get("soldnumber").toString());
         arrayOfObject[6] =  Double.valueOf( listData.get(0).get("totprice").toString()); ;
         arrayOfObject[7] =  Double.valueOf( listData.get(0).get("feepercent").toString()); ;
         arrayOfObject[8] =  Double.valueOf( listData.get(0).get("feeprice").toString()); ;
         arrayOfObject[9] =  Double.valueOf( listData.get(0).get("exchangerate").toString()); ;
         arrayOfObject[10] =  Double.valueOf( listData.get(0).get("rmb_totprice").toString()); ;
         arrayOfObject[11] =  Double.valueOf( listData.get(0).get("rmb_singprice").toString()); ;
         arrayOfObject[12] =  Double.valueOf( listData.get(0).get("transprice").toString()); ;
         arrayOfObject[13] =  Double.valueOf( listData.get(0).get("singlesum").toString()); ;
         arrayOfObject[14] =  Integer.valueOf( listData.get(0).get("weight").toString()); ;
         arrayOfObject[15] =  Integer.valueOf(listData.get(0).get("totweight").toString()); ;
         arrayOfObject[16] =  listData.get(0).get("beizhu").toString();
         arrayOfObject[17] =  username;

         sdb.execSQL("Insert into tool_dtl values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", arrayOfObject );

         sdb.close();
         return ret;
    }

    public boolean insertToolOrder (String username, ArrayList<HashMap<String, Object>> listData ){
        boolean ret = true;
        String toolname,toolsource,trandate,customer,orderdate;
        ArrayList<HashMap<String, Object>> toolorderdtl;
        Object[] arrayOfObject = new Object[11];

        customer = listData.get(0).get("customer").toString();
        orderdate = listData.get(0).get("orderdate").toString();
        toolsource = listData.get(0).get("toolsource").toString();
        toolname = listData.get(0).get("toolname").toString();

        toolorderdtl = getToolOrderDtl( username, customer, orderdate,toolsource, toolname );
        if( toolorderdtl.isEmpty() == false ){
            return ( false );
        }
        sdb = dbHelper.getWritableDatabase();
        arrayOfObject[0] = customer ;
        arrayOfObject[1] = orderdate ;
        arrayOfObject[2] = toolsource ;
        arrayOfObject[3] = toolname;
        arrayOfObject[4] = listData.get(0).get("trandate").toString();
        arrayOfObject[5] =  Integer.valueOf( listData.get(0).get("number").toString());
        arrayOfObject[6] =  Double.valueOf( listData.get(0).get("rmb_totprice").toString());
        arrayOfObject[7] =  Double.valueOf( listData.get(0).get("exchangerate").toString());
        arrayOfObject[8] =  Double.valueOf( listData.get(0).get("toolbuyprice").toString());
        arrayOfObject[9] =  "0";
        arrayOfObject[10] =  username;

        sdb.execSQL("Insert into tool_order values( ?,?,?,?,?,?,?,?,?,?,? )", arrayOfObject );

        sdb.close();
        return ret;
    }

    private boolean upRestNumToolDtl( String username, String toolname, String sourcefrom, String trandate,Integer soldnumber ){
        Integer ret = 0;
        String sql;

        Object[] arrayOfObject = new Object[5];

       // sdb = dbHelper.getWritableDatabase();

        arrayOfObject[0]=soldnumber;
        arrayOfObject[1]=sourcefrom;
        arrayOfObject[2]=toolname;
        arrayOfObject[3]=trandate;
        arrayOfObject[4]=username;

        sql = "update tool_dtl set soldnumber=? "+
                "where sourcefrom = ? and  toolname = ? and trandate = ? and user = ? ";
        Log.v("tag", sql);
        sdb.execSQL(sql, arrayOfObject);
       // sdb.close();
        // Log.v("tag", sql);
        return true;
    }

    public boolean updateRestNumToolDtl( String username, String toolname, String sourcefrom, String trandate,Integer soldnumber ){
        Integer ret = 0;
        String sql;

        Object[] arrayOfObject = new Object[5];

        sdb = dbHelper.getWritableDatabase();

        arrayOfObject[0]=soldnumber;
        arrayOfObject[1]=sourcefrom;
        arrayOfObject[2]=toolname;
        arrayOfObject[3]=trandate;
        arrayOfObject[4]=username;

        sql = "update tool_dtl set soldnumber=? "+
                "where sourcefrom = ? and  toolname = ? and trandate = ? and user = ? ";
        Log.v("tag", sql);
        sdb.execSQL(sql, arrayOfObject);
        sdb.close();
        // Log.v("tag", sql);
        return true;
    }

    public int updateModiToolDtl(String username, ArrayList<HashMap<String, Object>> listData ){
        Integer ret = 0;
        String toolname,sourcefrom,trandate,beizhu,sql;
        Integer number,weight,totweight;
        Double totprice,feepercent,feeprice,exchangerate,rmb_totprice,rmb_singprice,transprice,singlesum;

        Object[] arrayOfObject = new Object[16];

        toolname = listData.get(0).get("toolname").toString();
        sourcefrom = listData.get(0).get("sourcefrom").toString();
        trandate = listData.get(0).get("trandate").toString();

        sdb = dbHelper.getWritableDatabase();

        number = Integer.valueOf(listData.get(0).get("number").toString());
        arrayOfObject[0]=number;
        totprice = Double.valueOf(listData.get(0).get("totprice").toString());
        arrayOfObject[1]=totprice;
        feepercent = Double.valueOf(listData.get(0).get("feepercent").toString()); ;
        arrayOfObject[2]=feepercent;
        feeprice = Double.valueOf( listData.get(0).get("feeprice").toString());
        arrayOfObject[3]=feeprice;
        exchangerate = Double.valueOf(listData.get(0).get("exchangerate").toString());
        arrayOfObject[4]=exchangerate;
        rmb_totprice =  Double.valueOf(listData.get(0).get("rmb_totprice").toString());
        arrayOfObject[5]=rmb_totprice;
        rmb_singprice =  Double.valueOf( listData.get(0).get("rmb_singprice").toString());
        arrayOfObject[6]=rmb_singprice;
        transprice = Double.valueOf( listData.get(0).get("transprice").toString());
        arrayOfObject[7]=transprice;
        singlesum = Double.valueOf( listData.get(0).get("singlesum").toString());
        arrayOfObject[8]=singlesum;
        weight = Integer.valueOf(listData.get(0).get("weight").toString());
        arrayOfObject[9]=weight;
        totweight = Integer.valueOf(listData.get(0).get("totweight").toString());
        arrayOfObject[10]=totweight;
        beizhu = listData.get(0).get("beizhu").toString();
        arrayOfObject[11]=beizhu;
        arrayOfObject[12]=sourcefrom;
        arrayOfObject[13]=toolname;
        arrayOfObject[14]=trandate;
        arrayOfObject[15]=username;


      /*  sql = "update tool_dtl set number="+number+",totprice="+totprice +",feepercent="+feepercent+
                      ",feeprice="+feeprice +",exchangerate="+exchangerate+",rmb_totprice="+rmb_totprice+
                       ",rmb_singprice="+rmb_singprice+",transprice="+transprice+",singlesum="+singlesum+
                       ",weight="+weight+",totweight="+totweight+",beizhu='"+beizhu+"' " +
                "where sourcefrom = '"+sourcefrom+"' and  toolname = '"+toolname+"' and trandate = '"+trandate+"' and user = '"+username+"' ";*/
        sql = "update tool_dtl set number=?,totprice=?,feepercent=?,feeprice=?,exchangerate=?,rmb_totprice=?,rmb_singprice=?,transprice=?,singlesum=?,weight=?,totweight=?,beizhu=? "+
                "where sourcefrom = ? and  toolname = ? and trandate = ? and user = ? ";
        Log.v("tag", sql);
        sdb.execSQL(sql, arrayOfObject);
        sdb.close();
       // Log.v("tag", sql);
        return ret;
    }

    public void updateModiToolOrder(String username, ArrayList<HashMap<String, Object>> listData ){
        Integer ret = 0;
        String ordername,orderdate,sourcefrom,toolname,trandate,sql;
        Integer number;
        Double toolprice,exchangerate,toolbuyprice;

        Object[] arrayOfObject = new Object[9];
        ordername = listData.get(0).get("customer").toString();
        orderdate = listData.get(0).get("orderdate").toString();
        toolname = listData.get(0).get("toolname").toString();
        sourcefrom = listData.get(0).get("toolsource").toString();

        sdb = dbHelper.getWritableDatabase();

        number = Integer.valueOf(listData.get(0).get("number").toString());
        arrayOfObject[0]=number;
        toolprice = Double.valueOf(listData.get(0).get("toolprice").toString()); ;
        arrayOfObject[1]=toolprice;
        exchangerate = Double.valueOf( listData.get(0).get("exchangerate").toString());
        arrayOfObject[2]=exchangerate;
        toolbuyprice = Double.valueOf(listData.get(0).get("toolbuyprice").toString());
        arrayOfObject[3]=toolbuyprice;
        arrayOfObject[4]=ordername;
        arrayOfObject[5]=orderdate;
        arrayOfObject[6]=sourcefrom;
        arrayOfObject[7]=toolname;
        arrayOfObject[8]=username;

        sql = "update tool_order set number=?,toolprice=?,exchangerate=?,toolbuyprice=? "+
                "where customer = ? and  orderdate = ? and toolsource = ? and toolname = ? and user = ? ";
        Log.v("tag", sql);
        sdb.execSQL(sql, arrayOfObject);
        sdb.close();
        // Log.v("tag", sql);
        return ;
    }
    public ArrayList<HashMap<String, Object>> getCountDtl(String username, String sourcefrom,String trandate ){

        sdb = dbHelper.getReadableDatabase();
        String sql;
        stat = getStat(username);
        if ( stat.substring( 0,1).equals("0") ) {
            if (sourcefrom.isEmpty() == true) {
                sql = "SELECT * FROM tool_dtl WHERE trandate='" + trandate + "'";

            } else {
                if (trandate.isEmpty() == true) {
                    sql = "SELECT * FROM tool_dtl WHERE sourcefrom='" + sourcefrom + "'";

                } else {
                    sql = "SELECT * FROM tool_dtl WHERE trandate='" + trandate + "' AND sourcefrom='" + sourcefrom + "'";

                }
            }
        }else{
            if (sourcefrom.isEmpty() == true) {
                sql = "SELECT * FROM tool_dtl WHERE trandate='" + trandate + "' AND user ='" + username + "'";

            } else {
                if (trandate.isEmpty() == true) {
                    sql = "SELECT * FROM tool_dtl WHERE sourcefrom='" + sourcefrom + "' AND user ='" + username + "'";

                } else {
                    sql = "SELECT * FROM tool_dtl WHERE trandate='" + trandate + "' AND sourcefrom='" + sourcefrom + "' AND user ='" + username + "'";

                }
            }
        }
        Log.v("tag" ,sql);
        Cursor cursor = sdb.rawQuery( sql,null);
        listData = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        Integer count = cursor.getCount();
        Log.v("tag" ,count.toString());
        if( count > 0 ){
            // 获取表的内容
            // c.moveToFirst();
            while ( cursor.moveToNext()) {
                map = new HashMap<String, Object>();
                //  map.put("img", R.drawable.shop);
                map.put("toolname", cursor.getString(1));
                map.put("sourcefrom", cursor.getString(0) ) ;
                map.put("trandate", cursor.getString(2) ) ;
                map.put("number", cursor.getInt(4)) ;
                map.put("rmb_singprice", cursor.getDouble(11) );
                map.put("weight", cursor.getInt(14) );
                map.put("user", cursor.getString(17) );
               // map.put("totweight", cursor.getInt(15) );
                // map.put("button_detail", R.drawable.align_right);
                listData.add(map);
            }
        }
        cursor.close();
        sdb.close();
        return listData;
    }



    public Integer getCountDtlSum(String username, String sourcefrom,String trandate ){

        sdb = dbHelper.getReadableDatabase();
        String sql;
        Integer ret = 0;
        stat = getStat(username);
        if ( stat.substring( 0,1).equals("0") ) {
            if (sourcefrom.isEmpty() == true) {
                sql = "SELECT sum(totweight) FROM tool_dtl WHERE trandate='" + trandate + "'";
            } else {
                if (trandate.isEmpty() == true) {
                    sql = "SELECT sum(totweight) FROM tool_dtl WHERE sourcefrom='" + sourcefrom + "'";
                } else {
                    sql = "SELECT sum(totweight) FROM tool_dtl WHERE trandate='" + trandate + "' AND sourcefrom='" + sourcefrom + "'";
                }
            }
        }else {
            if (sourcefrom.isEmpty() == true) {
                sql = "SELECT sum(totweight) FROM tool_dtl WHERE trandate='" + trandate + "' AND user ='" + username + "'";
            } else {
                if (trandate.isEmpty() == true) {
                    sql = "SELECT sum(totweight) FROM tool_dtl WHERE sourcefrom='" + sourcefrom + "' AND user ='" + username + "'";
                } else {
                    sql = "SELECT sum(totweight) FROM tool_dtl WHERE trandate='" + trandate + "' AND sourcefrom='" + sourcefrom + "' AND user ='" + username + "'";
                }
            }
        }
        Log.v("tag" ,sql);
        Cursor cursor = sdb.rawQuery(sql, null);
        if(cursor.moveToFirst()==true){
            ret = cursor.getInt(0);
        }
        Log.v("tag" ,ret.toString());
        cursor.close();
        sdb.close();
        return ret;
    }


    public Integer getqrycount(String username,String toolname,String sourcefrom,String trandate ){

        sdb = dbHelper.getReadableDatabase();
        String sql,sql1="",sql2="",sql3="",sql4;
        Integer ret = 0;
        stat = getStat(username);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "SELECT count(*) FROM tool_dtl WHERE 1=1";
        }else{
            sql = "SELECT count(*) FROM tool_dtl WHERE user='" + username + "' ";
        }
        if( toolname.isEmpty() == false )
             sql1=" AND toolname like '%"+toolname+"%' ";
        if( sourcefrom.isEmpty() == false )
             sql2=" AND sourcefrom like '%"+sourcefrom+"%' ";
        if( trandate.isEmpty() == false )
            sql3=" AND trandate like '%"+trandate+"%' ";
        sql4 = sql+sql1+sql2+sql3;

        Log.v("tag" ,sql4);
        Cursor cursor = sdb.rawQuery(sql4, null);
        if(cursor.moveToFirst()==true){
            ret = cursor.getInt(0);
        }
        Log.v("tag" ,ret.toString());
        cursor.close();
        sdb.close();
        return ret;
    }

    public Integer qrycustomordercount(String username,String ordername,String orderdate ){

        sdb = dbHelper.getReadableDatabase();
        String sql,sql1="",sql2="",sql5;
        Integer ret = 0;
        stat = getStat(username);
        if ( stat.substring( 0,1).equals("0") ) {
            sql = "SELECT count(*) FROM custom_order WHERE 1=1";
        }else{
            sql = "SELECT count(*) FROM custom_order WHERE user='" + username + "' ";
        }
        if( ordername.isEmpty() == false )
            sql1=" AND customer like '%"+ordername+"%' ";
        if( orderdate.isEmpty() == false )
            sql2=" AND orderdate like '%"+orderdate+"%' ";
        sql5 = sql+sql1+sql2;

        Log.v("tag" ,sql5);
        Cursor cursor = sdb.rawQuery(sql5, null);
        if(cursor.moveToFirst()==true){
            ret = cursor.getInt(0);
        }
        Log.v("tag" ,ret.toString());
        cursor.close();
        sdb.close();
        return ret;
    }

    public ArrayList<HashMap<String, Object>>  qryCustomOrderData( String username,String ordername ,String orderdate  ){
        sdb = dbHelper.getReadableDatabase();
        String sql,sql1="",sql2="",sql3,sql6;
        stat = getStat(username);
        Log.v("tag", stat);
        if ( stat.substring( 0,1).equals("0") ){
            sql="select * from  custom_order where 1=1 ";
        }else{
            sql="select * from  custom_order  where user=\'"+username+"\' ";
        }
        if( ordername.isEmpty() == false )
            sql1=" AND customer like '%"+ordername+"%' ";
        if( orderdate.isEmpty() == false )
            sql2=" AND orderdate like '%"+orderdate+"%' ";
        sql3 = "  order by orderdate desc";
        sql6 = sql+sql1+sql2+sql3;
        Log.v("tag",sql6);
        Cursor c = sdb.rawQuery( sql6,null);

        listData = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while ( c.moveToNext()) {
            map = new HashMap<String, Object>();
            map.put("customer", c.getString(0));
            map.put("orderdate", c.getString(1) ) ;
            map.put("totprice", c.getString(3) ) ;
         //   map.put("realprice", c.getString(5) );
            listData.add(map);
        }
        c.close();
        sdb.close();
        return listData;
    }


    public ArrayList<HashMap<String, Object>>  qryToolOrderData( String username,String ordername ,String orderdate  ){
        sdb = dbHelper.getReadableDatabase();
        String sql,sql1="",sql2="",sql3,sql6;
        stat = getStat(username);
        Log.v("tag", stat);
        if ( stat.substring( 0,1).equals("0") ){
            sql="select * from  tool_order where 1=1 ";
        }else{
            sql="select * from  tool_order  where user=\'"+username+"\' ";
        }
        if( ordername.isEmpty() == false )
            sql1=" AND customer = '"+ordername+"' ";
        if( orderdate.isEmpty() == false )
            sql2=" AND orderdate = '"+orderdate+"' ";
        sql3 = "  order by orderdate desc";
        sql6 = sql+sql1+sql2+sql3;
        Log.v("tag",sql6);
        Cursor c = sdb.rawQuery( sql6,null);

        listData = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while ( c.moveToNext()) {
            map = new HashMap<String, Object>();
            map.put("toolname", c.getString(3));
            map.put("toolsource", c.getString(2) ) ;
            map.put("toolbuyprice", c.getDouble(8) ) ;
            //   map.put("realprice", c.getString(5) );
            listData.add(map);
        }
        c.close();
        sdb.close();
        return listData;
    }


    public ArrayList<HashMap<String, Object>>  qryToolDtlData( String username,String toolname ,String sourcefrom ,String trandate   ){
        sdb = dbHelper.getReadableDatabase();
        String sql,sql1="",sql2="",sql3="",sql4,sql5;
        stat = getStat(username);
        Log.v("tag", stat);
        if ( stat.substring( 0,1).equals("0") ){
            sql="select * from  tool_dtl where 1=1 ";
        }else{
            sql="select * from  tool_dtl  where user=\'"+username+"\' ";
        }
        if( toolname.isEmpty() == false )
            sql1=" AND toolname like '%"+toolname+"%' ";
        if( sourcefrom.isEmpty() == false )
            sql2=" AND sourcefrom like '%"+sourcefrom+"%' ";
        if( trandate.isEmpty() == false )
            sql3=" AND trandate like '%"+trandate+"%' ";
        sql5 = "  order by trandate desc";
        sql4 = sql+sql1+sql2+sql3+sql5;
        Log.v("tag",sql4);
        Cursor c = sdb.rawQuery( sql4,null);

        listData = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while ( c.moveToNext()) {
                map = new HashMap<String, Object>();
                map.put("toolname", c.getString(1));
                map.put("sourcefrom", c.getString(0) ) ;
                map.put("trandate", c.getString(2) ) ;
                map.put("singlesum", c.getString(13) );
                listData.add(map);
        }
        c.close();
        sdb.close();
        return listData;
    }

    public boolean updateCountDtl( String username, ArrayList<HashMap<String, Object>> listData ){
        Integer ret=0;
        String sql;
        String toolname,sourcefrom,trandate;
        Double transprice,singlesum,rmb_totprice;
        sdb = dbHelper.getWritableDatabase();
        stat = getStat(username);
        if( listData.isEmpty() == false ){
            for( int i = 0; i< listData.size(); i ++ ){
                toolname =  listData.get(i).get("toolname").toString();
                sourcefrom = listData.get(i).get("sourcefrom").toString();
                trandate = listData.get(i).get("trandate").toString();
                transprice = Double.valueOf(listData.get(i).get("transprice").toString());
                singlesum = Double.valueOf(listData.get(i).get("singlesum").toString());
                rmb_totprice = Double.valueOf(listData.get(i).get("rmb_totprice").toString());

                Log.v("tag",toolname+sourcefrom+trandate+transprice.toString()+singlesum.toString()+rmb_totprice.toString() );
                if ( stat.substring( 0,1).equals("0") ) {
                    sql = "UPDATE tool_dtl SET transprice=" + transprice + ",singlesum=" + singlesum + ",rmb_totprice="+ rmb_totprice +
                            " WHERE toolname='" + toolname + "' AND sourcefrom ='" + sourcefrom + "' AND trandate ='" + trandate + "' AND user='" + username + "'";
                }else {
                    sql = "UPDATE tool_dtl SET transprice=" + transprice + ",singlesum=" + singlesum + ",rmb_totprice="+ rmb_totprice +
                            " WHERE toolname='" + toolname + "' AND sourcefrom ='" + sourcefrom + "' AND trandate ='" + trandate + "'";
                }
                Log.v("tag", sql);
                try {
                    sdb.execSQL(sql);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
        sdb.close();
        return true;
    }

    public String getStat( String username ){
        String ret=null;
       // sdb=dbHelper.getReadableDatabase();
        String sql="select * from user where username=?";
        Cursor cursor=sdb.rawQuery(sql, new String[]{username});
        if(cursor.moveToFirst()==true){
            ret = cursor.getString(cursor.getColumnIndex("stat"));
            // Log.v("tag", username+ret);
        }
         //Log.v("tag", username+ret);
        cursor.close();
        //sdb.close();
        return ret;
    }


    public ArrayList<String> getTranDate( String username ,String sourcefrom){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select distinct trandate from  tool_dtl where sourcefrom='"+sourcefrom+"' order by trandate";
        }else{
            sql="select distinct trandate from tool_dtl  where user='"+username+"' and sourcefrom='"+sourcefrom+"' order by trandate";
        }
        Cursor cursor=sdb.rawQuery(sql, null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("trandate")));
        }
        cursor.close();
        sdb.close();
        return list;
    }

    public Integer getToolRestnum( String username ,String toolname ,String sourcefrom ,String trandate){
        Integer soldnum=0,totnum=0,restnum=0;
        String sql;
        sdb = dbHelper.getReadableDatabase();

        sql = "select number,soldnumber from  tool_dtl  where toolname = '" + toolname + "' and sourcefrom = '" + sourcefrom +
                "' and trandate = '" + trandate +
                "' and user='" + username + "'";

        Cursor cursor=sdb.rawQuery(sql, null);
        if ( cursor.moveToFirst()==true){
            totnum = cursor.getInt( cursor.getColumnIndex("number"));
            soldnum = cursor.getInt( cursor.getColumnIndex("soldnumber"));
            //   Log.v("tag","soldnum="+soldnum.toString());
        }
        cursor.close();
        sdb.close();
        restnum = totnum - soldnum;
        // Log.v("tag","soldnum="+soldnum.toString());
        return restnum;
    }

    public Integer getToolSoldnum( String username ,String toolname ,String sourcefrom ,String trandate){
        Integer soldnum=0,totnum=0,restnum=0;
        String sql;
        sdb = dbHelper.getReadableDatabase();
       // stat = getStat(username).trim();
       // if ( stat.substring( 0,1).equals("0") ) {
        //    sql = "select soldnumber from  tool_dtl  where toolname = '" + toolname + "' and sourcefrom = '" + sourcefrom +
       //             "' and trandate = '" + trandate +"'";
       // }else {
            sql = "select soldnumber from  tool_dtl  where toolname = '" + toolname + "' and sourcefrom = '" + sourcefrom +
                    "' and trandate = '" + trandate +
                    "' and user='" + username + "'";
       // }
    //    Log.v("tag",sql);
        Cursor cursor=sdb.rawQuery(sql, null);
        if ( cursor.moveToFirst()==true){
            soldnum = cursor.getInt( cursor.getColumnIndex("soldnumber"));
         //   Log.v("tag","soldnum="+soldnum.toString());
        }
        cursor.close();
        sdb.close();
       // Log.v("tag","soldnum="+soldnum.toString());
        return soldnum;
    }

    public Integer getOrderCount( String username ,String ordername ,String orderdate){
        Integer  totnum=0;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        sql = "select count(*) as ordercount from  tool_order  where customer = '" + ordername + "' and orderdate = '" + orderdate +
                    "' and user='" + username + "'";

        Cursor cursor=sdb.rawQuery(sql, null);
        if ( cursor.moveToFirst()==true){
            totnum = cursor.getInt( cursor.getColumnIndex("ordercount"));
        }
        cursor.close();
        sdb.close();
        return totnum;
    }


    public Integer getdtlCount( String username ,String sourcefrom ,String trandate){
        Integer  totnum=0;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        sql = "select count(*) as dtlcount from  tool_dtl  where sourcefrom = '" + sourcefrom +
                "' and trandate ='" + trandate + "' and user='" + username + "'";

        Cursor cursor=sdb.rawQuery(sql, null);
        if ( cursor.moveToFirst()==true){
            totnum = cursor.getInt( cursor.getColumnIndex("dtlcount"));
        }
        cursor.close();
        sdb.close();
        return totnum;
    }

    public ArrayList<String> getSourceFrom( String username ){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select distinct sourcefrom from  tool_dtl order by sourcefrom";
        }else{
            sql="select distinct sourcefrom from tool_dtl  where user='"+username+"' order by sourcefrom ";
        }
        Cursor cursor=sdb.rawQuery(sql,null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("sourcefrom")));
        }
        cursor.close();
        sdb.close();
        return list;
    }

    public ArrayList<String> getToolName( String username,String sourcefrom,String trandate ){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select toolname from  tool_dtl where sourcefrom='"+sourcefrom+"' And trandate='"+trandate+"' order by toolname";
        }else{
            sql="select toolname from tool_dtl  where user='"+username+"' and sourcefrom='"+sourcefrom+"' AND trandate='"+trandate+"' order by toolname";
        }
        Cursor cursor=sdb.rawQuery(sql,null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("toolname")));
        }
        cursor.close();
        sdb.close();
        return list;
    }



    public ArrayList<String> getOrderName( String username ){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select distinct customer from  tool_order order by customer";
        }else{
            sql="select distinct customer from tool_order  where user='"+username+"' order by customer ";
        }
        Cursor cursor=sdb.rawQuery(sql,null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("customer")));
        }
        cursor.close();
        sdb.close();
        return list;
    }


    public ArrayList<String> getOrderDate( String username ,String ordername){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select distinct orderdate from  tool_order where customer='"+ordername+"' order by orderdate";
        }else{
            sql="select distinct orderdate from tool_order  where user='"+username+"' and customer='"+ordername+"' order by orderdate";
        }
        Cursor cursor=sdb.rawQuery(sql, null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("orderdate")));
        }
        cursor.close();
        sdb.close();
        return list;
    }

    public ArrayList<String> getToolSource( String username ,String ordername, String orderdate ){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select distinct toolsource from  tool_order where customer='"+ordername+"' and orderdate='"+orderdate+"' order by toolsource";
        }else{
            sql="select distinct toolsource from tool_order  where user='"+username+"' and customer='"+ordername+"' and orderdate='"+orderdate+"' order by toolsource";
        }
        Cursor cursor=sdb.rawQuery(sql, null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("toolsource")));
        }
        cursor.close();
        sdb.close();
        return list;
    }
    public ArrayList<String> getToolname( String username ,String ordername, String orderdate, String toolsource ){
        ArrayList<String> list=null;
        String sql;
        sdb = dbHelper.getReadableDatabase();
        stat = getStat(username).trim();
        if ( stat.substring( 0,1).equals("0") ){
            sql="select distinct toolname from  tool_order where customer='"+ordername+"' and orderdate='"+orderdate+"' and toolsource ='"+toolsource+"' order by toolname";
        }else{
            sql="select distinct toolname from tool_order  where user='"+username+"' and customer='"+ordername+"' and orderdate='"+orderdate+"' and toolsource ='"+toolsource+"' order by toolname";
        }
        Cursor cursor=sdb.rawQuery(sql, null);
        list = new ArrayList<String>();
        list.add("");
        while ( cursor.moveToNext()){
            list.add(cursor.getString(cursor.getColumnIndex("toolname")));
        }
        cursor.close();
        sdb.close();
        return list;
    }


    public boolean DeleteTooldtl( String username,String sourcefrom,String toolname,String trandate ){
        Integer ret = 0;
        String sql;
        Object[] arrayOfObject = new Object[4];
        sdb = dbHelper.getWritableDatabase();
        arrayOfObject[0]=sourcefrom;
        arrayOfObject[1]=toolname;
        arrayOfObject[2]=trandate;
        arrayOfObject[3]=username;

        sql = "delete from tool_dtl "+
                "where sourcefrom = ? and  toolname = ? and trandate = ? and user = ? ";
        Log.v("tag", sql);
        sdb.execSQL(sql, arrayOfObject);

        sdb.close();

        return true;
    }

    public boolean DeleteToolOrder( String username, String ordername ,String orderdate, String sourcefrom,String toolname,String trandate, Integer number ){
        Integer ret = 0;
        Integer soldnum=0,restnum=0;
        String sql;
       // SQLiteDatabase sdb1;
      //  soldnum = getToolSoldnum(username, toolname, sourcefrom, trandate);
      //  Log.v("tag", "soldnum="+soldnum.toString());
        Object[] arrayOfObject = new Object[5];

        sdb = dbHelper.getWritableDatabase();

        arrayOfObject[0]=ordername;
        arrayOfObject[1]=orderdate;
        arrayOfObject[2]=sourcefrom;
        arrayOfObject[3]=toolname;
        arrayOfObject[4]=username;

        sql = "delete from tool_order "+
                "where customer = ? and  orderdate = ? and toolsource = ? and toolname = ? and user = ? ";
        Log.v("tag", sql);
        sdb.execSQL(sql, arrayOfObject);




    /*    restnum = soldnum - number;
        if( restnum < 0 ){
            restnum = 0;
        }
        Log.v("tag", "restnum=" + restnum.toString());

        upRestNumToolDtl(username, toolname, sourcefrom, trandate, restnum);*/
        sdb.close();

        // Log.v("tag", sql);
        return true;
    }

}
