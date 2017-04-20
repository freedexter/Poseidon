package com.example.administrator.poseidon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.database.QueryDataBaseAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class QryOrderActivity extends ActionBarActivity implements View.OnTouchListener{
    Intent i;
    String username,ordername,orderdate,toolname,sourcefrom,customer_name,order_date,flag="1";
    TextView textView;
    Button bt;
    GetItemDateTask mtask=null;
    EditText editText,date;
    View layout1 = null;
    View layout2 = null;
    View layout3 = null;
    QueryDataBaseAdapter queryDataBaseAdapter;
    ListView listView;
    MyAdapter mAdapter,mAdapter1;
    ArrayList<HashMap<String, Object>> listItem,listItem1;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qry_order);
        LayoutInflater inflater = LayoutInflater.from(this);
        layout1 = inflater.inflate(R.layout.activity_qry_order, null);
        layout2 = inflater.inflate(R.layout.activity_qry_order_dtl, null);
        layout3 = inflater.inflate(R.layout.activity_qry_toolorder_dtl, null);

        initintent();
        initActionBar();
        bt = (Button) findViewById(R.id.clearbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditView();
            }
        });
        bt = (Button) findViewById(R.id.qrybutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qryOrderDtl();
            }
        });

    }

    public void qryOrderDtl(){

        Integer count=0;

        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        //Double rmb_singprice,tottransprice;
        editText = (EditText) this.findViewById(R.id.ordername);
        ordername = editText.getText().toString().trim();
        editText = (EditText) this.findViewById(R.id.orderdate);
        orderdate = editText.getText().toString().trim();
        /*
        editText = (EditText) this.findViewById(R.id.toolname);
        toolname = editText.getText().toString().trim();
        editText = (EditText) this.findViewById(R.id.sourcefrom);
        sourcefrom = editText.getText().toString().trim();*/

        count = queryDataBaseAdapter.qrycustomordercount(username, ordername, orderdate);
        if(count == 0 ){
            Toast.makeText(QryOrderActivity.this, "没有找到任何订单！", Toast.LENGTH_SHORT).show();
            return;
        }else{
            setContentView(layout2);
            flag="1";
            // Log.v("tag",layout2.toString() );
            listView= (ListView) findViewById(R.id.listView);

            Log.v("tag", "查询UI线程：" + String.valueOf(Thread.currentThread().getId()));


            mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
            listView.setAdapter(mAdapter);//为ListView绑定Adapter
            // Log.v("tag", listView.toString());
            mtask = new GetItemDateTask();
            try {
                listItem = mtask.execute( username, ordername, orderdate,"custom_order"  ).get();
            } catch (Exception e) {
                listItem=null;
            }

            bt = (Button) findViewById(R.id.returnbutton);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //setContentView(layout2);
                    setResult( RESULT_FIRST_USER, i);
                    if ( mtask != null && mtask.getStatus() == AsyncTask.Status.RUNNING ){
                        mtask.cancel(true);
                        mtask =null;
                    }
                    finish();
                }
            });
        }


    }
    private void initintent(){
        i = new Intent();
        i = this.getIntent();
        Bundle b = i.getExtras();
        username = b.getString("username");
        textView = (TextView) this.findViewById(R.id.username);
        textView.setText(username);

        // Log.v("tag",username);

    }


    private void initActionBar(){
        //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setHomeButtonEnabled(true);//actionbar��������Ա����
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//��ʾ�����ͼ��

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qry_order, menu);
        return true;
    }

    private void clearEditView(){
        editText = (EditText) this.findViewById(R.id.ordername);
        editText.setText("");
        editText.requestFocus();

        editText = (EditText) this.findViewById(R.id.orderdate);
        editText.setText("");
/*
        editText = (EditText) this.findViewById(R.id.toolname);
        editText.setText("");


        editText = (EditText) this.findViewById(R.id.sourcefrom);
        editText.setText("");
*/

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.date_dialog, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);

            builder.setView(view);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            if (v.getId() == R.id.orderdate) {
                final int inType = date.getInputType();
                date.setInputType(InputType.TYPE_NULL);
                date.onTouchEvent(event);
                date.setInputType(inType);
                date.setSelection(date.getText().length());

                builder.setTitle("请选择日期");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d%02d%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        date.setText(sb);
                        dialog.cancel();
                    }
                });

            }
            Dialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == android.R.id.home  ){
            setResult( RESULT_FIRST_USER, i);
            if ( mtask != null && mtask.getStatus() == AsyncTask.Status.RUNNING ){
                mtask.cancel(true);
                mtask =null;
            }
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** 新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
      //  QueryDataBaseAdapter qd =new QueryDataBaseAdapter( QryOrderActivity.this );


        /**构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            // listItem = qd.qryToolDtlData(username, toolname, sourcefrom, trandate);
            //  Log.v("tag",String.valueOf( listItem.size()) );
        }

        @Override
        public int getCount() {
            int ret=0;
            if(flag.equals("1")) {
                if (listItem != null) {
                    ret = listItem.size();
                }
            }else {
                if (listItem1 != null) {
                    ret = listItem1.size();
                }
            }
            return ret;//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**书中详细解释该方法*/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            ViewHolder1 holder1;
            //观察convertView随ListView滚动情况
            // Log.v("tag", String.valueOf( position));
            if(flag.equals("1")) {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.clist, null);
                    holder = new ViewHolder();
                    // Log.v("tag", String.valueOf( position));
                    /**得到各个控件的对象*/
                    //holder.img = (ImageView) convertView.findViewById(R.id.img);
                    holder.customer = (TextView) convertView.findViewById(R.id.customer);
                    holder.orderdate = (TextView) convertView.findViewById(R.id.orderdate);
                    holder.totprice = (TextView) convertView.findViewById(R.id.totprice);
                    holder.button_detail = (ImageButton) convertView.findViewById(R.id.button_detail);
                    convertView.setTag(holder);//绑定ViewHolder对象
                    // Log.v("tag", String.valueOf(position));
                } else {
                    holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
                }
                // Log.v("tag", convertView.toString() );
                /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
                holder.customer.setText(listItem.get(position).get("customer").toString());
                holder.orderdate.setText(listItem.get(position).get("orderdate").toString());
                holder.totprice.setText(listItem.get(position).get("totprice").toString());

                holder.button_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfo(position, listItem);
                    }
                });
            }else {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.dlist, null);
                    holder1 = new ViewHolder1();
                    // Log.v("tag", String.valueOf( position));
                    /**得到各个控件的对象*/
                    //holder.img = (ImageView) convertView.findViewById(R.id.img);
                    holder1.toolname = (TextView) convertView.findViewById(R.id.toolname);
                    holder1.toolsource = (TextView) convertView.findViewById(R.id.sources);
                    holder1.toolbuyprice = (TextView) convertView.findViewById(R.id.buyprices);
                    holder1.button_detail = (ImageButton) convertView.findViewById(R.id.button_detail);
                    convertView.setTag(holder1);//绑定ViewHolder对象
                    // Log.v("tag", String.valueOf(position));
                } else {
                    holder1 = (ViewHolder1) convertView.getTag();//取出ViewHolder对象
                }
                // Log.v("tag", convertView.toString() );
                /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
                holder1.toolname.setText(listItem1.get(position).get("toolname").toString());
                holder1.toolsource.setText(listItem1.get(position).get("toolsource").toString());
                holder1.toolbuyprice.setText(listItem1.get(position).get("toolbuyprice").toString());

                holder1.button_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfo1(position, listItem1);
                    }
                });
            }
            return convertView;
        }

    }
    /**存放控件*/
    public final class ViewHolder{
        public TextView customer;
        public TextView orderdate;
        public TextView totprice;
        public ImageButton button_detail;
    }
    public final class ViewHolder1{
        public TextView toolname;
        public TextView toolsource;
        public TextView toolbuyprice;
        public ImageButton button_detail;
    }

    public void showInfo(int position,ArrayList<HashMap<String, Object>> listItem){

        String orderdate,customer;
        ArrayList<HashMap<String, Object>> list_date =null;
        orderdate = listItem.get(position).get("orderdate").toString();
        customer = listItem.get(position).get("customer").toString();
        customer_name = customer;
        order_date = orderdate;

        list_date =  queryDataBaseAdapter.getCustomOrderDtl(username,customer,orderdate);

        String[] detail =  new String[10];
        detail[0] = "订单者姓名："+customer;
        detail[1] = "订单日期："+orderdate;
        detail[2] = "订单商品总数："+list_date.get(0).get("number").toString();
        detail[3] = "订单总金额："+list_date.get(0).get("totprice").toString();
        detail[4] = "订单应付金额："+list_date.get(0).get("needprice").toString();
        detail[5] = "订单实付金额："+list_date.get(0).get("realprice").toString();
        detail[6] = "支付渠道："+list_date.get(0).get("paychnl").toString();
        detail[7] = "国内快递费用："+list_date.get(0).get("exprprice").toString();
        detail[8] = "快递单号："+list_date.get(0).get("exprnum").toString();
        detail[9] = "快递日期："+list_date.get(0).get("exprdate").toString();

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("订单详情").
                setItems(detail, null).
                setPositiveButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).
                setNegativeButton("订单商品明细", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        setContentView(layout3);
                        flag="2";
                        getToolOrderDtl();
                    }
                }).create();
        alertDialog.show();
    }


    public void showInfo1(int position,ArrayList<HashMap<String, Object>> listItem){

        String toolname,toolsource;
        ArrayList<HashMap<String, Object>> list_date =null;
        toolname = listItem.get(position).get("toolname").toString();
        toolsource = listItem.get(position).get("toolsource").toString();

        list_date =  queryDataBaseAdapter.getToolOrderDtl( username, customer_name,order_date,toolsource,toolname);

        String[] detail =  new String[9];
        detail[0] = "订单者姓名："+customer_name;
        detail[1] = "订单日期："+order_date;
        detail[2] = "商品来源："+toolsource;
        detail[3] = "商品名称："+toolname;
        detail[4] = "交易日期："+list_date.get(0).get("trandate").toString();
        detail[5] = "购买数量："+list_date.get(0).get("number").toString();
        detail[6] = "商品金额："+list_date.get(0).get("toolprice").toString();
        detail[7] = "交易汇率："+list_date.get(0).get("exchangerate").toString();
        detail[8] = "购买金额："+list_date.get(0).get("toolbuyprice").toString();

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("订单商品详情").
                setItems(detail, null).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }


    private void getToolOrderDtl(){

        textView = (TextView)findViewById(R.id.customer);
        textView.setText(customer_name);
        listView= (ListView) findViewById(R.id.listView1);
        mAdapter1 = new MyAdapter(this);//得到一个MyAdapter对象
        listView.setAdapter(mAdapter1);//为ListView绑定Adapter
        // Log.v("tag", listView.toString());
        mtask = new GetItemDateTask();
        try {
            listItem1 = mtask.execute( username, customer_name, order_date,"tool_order"  ).get();
        } catch (Exception e) {
            listItem1=null;
        }

        bt = (Button) findViewById(R.id.returnbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(layout2);
                flag="1";
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                mProgressDialog = new ProgressDialog(QryOrderActivity.this);
                mProgressDialog.setMessage("正在获取数据");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return mProgressDialog;

        }
        return null;
    }
    class GetItemDateTask extends AsyncTask< String ,  Integer,  ArrayList<HashMap<String, Object>>> {//获取图片仍采用AsyncTask，这里的优化放到下篇再讨论

        String name;
        ArrayList<HashMap<String, Object>> listDate;
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( QryOrderActivity.this );

        @Override
        protected void onPreExecute() {
            //第一个执行方法
            super.onPreExecute();
            showDialog(1);//打开等待对话框
        }
     /*  GetItemDateTask(String name) {
            this.name = name;
        }*/

        @Override
        protected  ArrayList<HashMap<String, Object>> doInBackground(String... params) {

            if( params[3].equals("custom_order")) {
                // listDate = qd.getToolDtlData(params[0]);
                listDate = qd.qryCustomOrderData(params[0], params[1], params[2]);
            }else {
                listDate = qd.qryToolOrderData(params[0], params[1], params[2]);
            }
            Log.v("tag","查询子线程：："+ String.valueOf(Thread.currentThread().getId()));
            return listDate;
        }
        protected void onPostExecute (ArrayList<HashMap<String, Object>> result) {
          /*  if(result != null) {
                Toast.makeText(InitActivity.this, "成功获取数据", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(InitActivity.this, "获取数据失败", Toast.LENGTH_LONG).show();
            }*/
Log.v("tag",flag);
          if(flag.equals("1")) {
                mAdapter.notifyDataSetChanged();//通知ui界面更新
            }else{
                mAdapter1.notifyDataSetChanged();//通知ui界面更新
            }
            //  listView.setAdapter(mAdapter);
            dismissDialog(1);//关闭等待对话框
        }

    }


}
