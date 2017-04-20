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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class QryActivity extends ActionBarActivity implements View.OnTouchListener{

    Intent i;
    ListView listView;
    TextView textView;
    String username,sourcefrom,toolname,trandate;
    EditText date,editText;
    View layout1 = null;
    View layout2 = null;
    Button bt;
    QueryDataBaseAdapter queryDataBaseAdapter;
    MyAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    GetItemDateTask mtask=null;
    ArrayList<HashMap<String, Object>> listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qry);
        initintent();
        initActionBar();
        date = (EditText) this.findViewById(R.id.shopdate);
        date.setOnTouchListener(QryActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        layout1 = inflater.inflate(R.layout.activity_qry, null);
        layout2 = inflater.inflate(R.layout.activity_qry_dtl, null);

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
                qryDtl();
            }
        });
    }

    public void qryDtl(){

        Integer count=0;

        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        //Double rmb_singprice,tottransprice;
        editText = (EditText) this.findViewById(R.id.toolname);
        toolname = editText.getText().toString().trim();
        editText = (EditText) this.findViewById(R.id.sourcefrom);
        sourcefrom = editText.getText().toString().trim();
        editText = (EditText) this.findViewById(R.id.shopdate);
        trandate = editText.getText().toString().trim();

        count = queryDataBaseAdapter.getqrycount( username, toolname,sourcefrom, trandate );
        if(count == 0 ){
            Toast.makeText(QryActivity.this, "没有找到任何商品！", Toast.LENGTH_SHORT).show();
            return;
        }else{
            setContentView(layout2);
           // Log.v("tag",layout2.toString() );
            listView= (ListView) findViewById(R.id.listView);

            Log.v("tag","查询UI线程："+String.valueOf( Thread.currentThread().getId()));


            mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
            listView.setAdapter(mAdapter);//为ListView绑定Adapter
           // Log.v("tag", listView.toString());
            mtask = new GetItemDateTask();
            try {
                listItem = mtask.execute( username, toolname, sourcefrom, trandate ).get();
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
        getMenuInflater().inflate(R.menu.menu_qry, menu);
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

            if (v.getId() == R.id.shopdate) {
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

    private void clearEditView(){

        editText = (EditText) this.findViewById(R.id.toolname);
        editText.setText("");
        editText.requestFocus();

        editText = (EditText) this.findViewById(R.id.sourcefrom);
        editText.setText("");

        editText = (EditText) this.findViewById(R.id.shopdate);
        editText.setText("");

    }
    /** 新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( QryActivity.this );


        /**构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
           // listItem = qd.qryToolDtlData(username, toolname, sourcefrom, trandate);
          //  Log.v("tag",String.valueOf( listItem.size()) );
        }

        @Override
        public int getCount() {
            int ret=0;
            if ( listItem!=null ){
                ret = listItem.size();
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
            //观察convertView随ListView滚动情况
           // Log.v("tag", String.valueOf( position));
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.vlist ,null);
                holder = new ViewHolder();
               // Log.v("tag", String.valueOf( position));
                /**得到各个控件的对象*/
                //holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.toolname = (TextView) convertView.findViewById(R.id.toolname);
                holder.sourcefrom = (TextView) convertView.findViewById(R.id.sourcefrom);
                holder.singlesum = (TextView) convertView.findViewById(R.id.singlesum);
                holder.button_detail = (ImageButton) convertView.findViewById(R.id.button_detail);
                convertView.setTag(holder);//绑定ViewHolder对象
               // Log.v("tag", String.valueOf(position));
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
           // Log.v("tag", convertView.toString() );
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.toolname.setText(listItem.get(position).get("toolname").toString());
            holder.sourcefrom.setText(listItem.get(position).get("sourcefrom").toString());
            holder.singlesum.setText(listItem.get(position).get("singlesum").toString());

            holder.button_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInfo(position,listItem);
                }
            });

            return convertView;
        }

    }

    /**存放控件*/
    public final class ViewHolder{
        public TextView toolname;
        public TextView sourcefrom;
        public TextView singlesum;
        public ImageButton button_detail;
    }

    public void showInfo(int position,ArrayList<HashMap<String, Object>> listItem){

        Intent i=new Intent();
        i.setClass(getApplicationContext(), DetailActivity.class);
        Bundle b = new Bundle();
        b.putString("username", username);
        b.putString("toolname", listItem.get(position).get("toolname").toString());
        b.putString("sourcefrom",listItem.get(position).get("sourcefrom").toString());
        b.putString("trandate", listItem.get(position).get("trandate").toString());

        i.putExtras(b);

        startActivity(i);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                mProgressDialog = new ProgressDialog(QryActivity.this);
                mProgressDialog.setMessage("正在获取数据");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return mProgressDialog;

        }
        return null;
    }
    class GetItemDateTask extends AsyncTask< String ,  Integer,  ArrayList<HashMap<String, Object>>> {//获取图片仍采用AsyncTask，这里的优化放到下篇再讨论

        String name;
        ArrayList<HashMap<String, Object>> listDate;
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( QryActivity.this );

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
           // listDate = qd.getToolDtlData(params[0]);
            listDate = qd.qryToolDtlData( params[0], params[1], params[2], params[3] );
            Log.v("tag","查询子线程：："+ String.valueOf(Thread.currentThread().getId()));
            return listDate;
        }
        protected void onPostExecute (ArrayList<HashMap<String, Object>> result) {
          /*  if(result != null) {
                Toast.makeText(InitActivity.this, "成功获取数据", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(InitActivity.this, "获取数据失败", Toast.LENGTH_LONG).show();
            }*/
            mAdapter.notifyDataSetChanged();//通知ui界面更新
            //  listView.setAdapter(mAdapter);
            dismissDialog(1);//关闭等待对话框
        }

    }




}
