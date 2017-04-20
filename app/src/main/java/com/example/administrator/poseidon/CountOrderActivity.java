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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.database.QueryDataBaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class CountOrderActivity extends ActionBarActivity implements View.OnTouchListener{
    Intent i;
    String username,ordername,orderdate,flag;
    Double intransprice,orderrealpay,totorderprice,orderneedpay;
    String intransdate,intransnumber,paymode,messages;
    Integer totordernum;
    TextView textView;
    ArrayAdapter<String> onameadapter=null,odatedapter=null;
    Spinner onamespinner=null,odatespinner=null;
    ArrayAdapter<String> tmpadapter = null,tmpadapter1=null;
    ArrayList<String> data_list,date_list1;
    QueryDataBaseAdapter queryDataBaseAdapter;
    MyAdapter mAdapter;
    Button bt;
    View layout1 = null;
    View layout2 = null;
    View layout3 = null;
    ListView listView;
    EditText editText,date;
    ArrayList<HashMap<String, Object>> listItem;
    private ProgressDialog mProgressDialog;
    GetItemDateTask mtask=null;
    boolean ret=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_order);
        initActionBar();
        initintent();
        initSpinner();
        LayoutInflater inflater = this.getLayoutInflater();
        layout1 = inflater.inflate(R.layout.activity_count_order, null);
        layout2 = inflater.inflate(R.layout.activity_count_order_dtl, null);
        layout3 = inflater.inflate(R.layout.activity_count_order_sum, null);
        bt = (Button) findViewById(R.id.countbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ordername.isEmpty()) {
                    Toast.makeText(CountOrderActivity.this, "请选择订单人姓名！", Toast.LENGTH_SHORT).show();
                } else {
                    if (orderdate.isEmpty()) {
                        Toast.makeText(CountOrderActivity.this, "请选择订单日期！", Toast.LENGTH_SHORT).show();
                    } else {
                        setContentView(layout2);
                        showorderlist();

                    }
                }

            }
        });

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

            if (v.getId() == R.id.intransdate) {
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
    private void showorderlist(){
        textView= (TextView) findViewById(R.id.ordername);
        textView.setText(ordername);

        listView= (ListView) findViewById(R.id.listView);

        mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
        listView.setAdapter(mAdapter);//为ListView绑定Adapter
        mtask = new GetItemDateTask();
        try {
            listItem = mtask.execute( username, ordername, orderdate ).get();
        } catch (Exception e) {
            listItem=null;
        }

        bt = (Button) findViewById(R.id.nextbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(layout2);
                setContentView(layout3);

                getOrderSum();
            }
        });


    }

    private void getOrderSum() {
        date = (EditText) this.findViewById(R.id.intransdate);
        date.setOnTouchListener(CountOrderActivity.this);


        listItem = new ArrayList<HashMap<String, Object>>();
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( CountOrderActivity.this );
        listItem = qd.getToolOrderSum(username,ordername, orderdate);

        textView = (TextView) findViewById(R.id.totorderprice);
        totorderprice = Double.valueOf( listItem.get(0).get("totprice").toString());
        textView.setText( totorderprice.toString() );

        textView = (TextView) findViewById(R.id.totordernum);
        totordernum  = Integer.valueOf( listItem.get(0).get("totnum").toString());
        textView.setText( totordernum.toString() );

        bt = (Button) findViewById(R.id.clearbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = (EditText) findViewById(R.id.intransprice);
                editText.setText("");
                editText.requestFocus();
                editText = (EditText) findViewById(R.id.intransdate);
                editText.setText("");

                editText = (EditText) findViewById(R.id.intransnumber);
                editText.setText("");

                editText = (EditText) findViewById(R.id.orderrealpay);
                editText.setText("");

                editText = (EditText) findViewById(R.id.paymode);
                editText.setText("");

            }
        });

        bt = (Button) findViewById(R.id.countbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confrOrder();
            }
        });


    }

    private  void  confrOrder(){
        listItem = new ArrayList<HashMap<String, Object>>();
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        if( ((EditText) findViewById(R.id.intransprice)).getText().toString().isEmpty() ){
            Toast.makeText(CountOrderActivity.this, "请输入快递金额！", Toast.LENGTH_SHORT).show();
        }else{
            editText = (EditText) findViewById(R.id.intransprice);
            intransprice = Double.valueOf( editText.getText().toString() );
            if( ((EditText) findViewById(R.id.intransdate)).getText().toString().isEmpty() ) {
                Toast.makeText(CountOrderActivity.this, "请选择快递日期！", Toast.LENGTH_SHORT).show();
            }else {
                editText = (EditText) findViewById(R.id.intransdate);
                intransdate =  editText.getText().toString() ;
                if( ((EditText) findViewById(R.id.intransnumber)).getText().toString().isEmpty()){
                    Toast.makeText(CountOrderActivity.this, "请填入快递单号！", Toast.LENGTH_SHORT).show();
                }else{
                    editText = (EditText) findViewById(R.id.intransnumber);
                    intransnumber =  editText.getText().toString() ;
                    if(((EditText) findViewById(R.id.orderrealpay)).getText().toString().isEmpty() ){
                        Toast.makeText(CountOrderActivity.this, "请填入实付金额！", Toast.LENGTH_SHORT).show();
                    }else {
                        editText = (EditText) findViewById(R.id.orderrealpay);
                        orderrealpay = Double.valueOf( editText.getText().toString() );
                        if( ((EditText) findViewById(R.id.paymode)).getText().toString().isEmpty() ){
                            Toast.makeText(CountOrderActivity.this, "请填入支付方式！", Toast.LENGTH_SHORT).show();
                        }else {
                            editText = (EditText) findViewById(R.id.paymode);
                            paymode =  editText.getText().toString() ;
                            orderneedpay  = roundDouble( intransprice + totorderprice,2);
                            if( roundDouble(orderneedpay - orderrealpay,2) > 0.0 ){
                                messages = "订单已结算！"+ordername+"仍有未结清金额："+ roundDouble(orderneedpay - orderrealpay,2);
                            }else{
                                messages = "订单已结算！"+ordername+"金额已结清！";
                            }
                            String[] detail =  new String[10];
                            detail[0] = "订单者姓名："+ordername;
                            detail[1] = "订单日期："+orderdate;
                            detail[2] = "订单商品总数："+totordernum.toString();
                            detail[3] = "订单总金额："+totorderprice.toString();
                            detail[4] = "订单应付金额："+orderneedpay.toString();
                            detail[5] = "订单实付金额："+orderrealpay.toString();
                            detail[6] = "支付渠道："+paymode;
                            detail[7] = "国内快递费用："+intransprice.toString();
                            detail[8] = "快递单号："+intransnumber;
                            detail[9] = "快递日期："+intransdate;

                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("customer", ordername);
                            map.put("orderdate", orderdate ) ;
                            map.put("number",totordernum ) ;
                            map.put("totprice",totorderprice );
                            map.put("needprice", orderneedpay );
                            map.put("realprice", orderrealpay );
                            map.put("paychnl", paymode );
                            map.put("exprprice",intransprice );
                            map.put("exprnum", intransnumber );
                            map.put("exprdate", intransdate );
                            map.put("user", username );
                            listItem.add(map);

                            Dialog alertDialog = new AlertDialog.Builder(this).
                                    setTitle("订单结算详情").
                                    setItems(detail, null).
                                    setPositiveButton("取消", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            dialog.cancel();
                                        }
                                    }).
                                    setNegativeButton("确认", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            ret = queryDataBaseAdapter.insertCustomOrder(username, listItem);
                                            new AlertDialog.Builder(CountOrderActivity.this).
                                                    setTitle("成功更新").
                                                    setMessage( messages ).
                                                    setPositiveButton(
                                                            "完成",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    // 这里是你点击确定之后可以进行的操作
                                                                    dialog.cancel();
                                                                    setResult(RESULT_FIRST_USER, i);
                                                                    finish();
                                                                }
                                                            }).show();
                                            //dialog.cancel();

                                        }
                                    }).create();
                            alertDialog.show();

                        }
                    }
                }
            }

        }

    }



    /** 新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
      //  QueryDataBaseAdapter qd =new QueryDataBaseAdapter( CountOrderActivity.this );

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
                convertView = mInflater.inflate( R.layout.olist ,null);
                holder = new ViewHolder();
                // Log.v("tag", String.valueOf( position));
                /**得到各个控件的对象*/
                //holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.toolname = (TextView) convertView.findViewById(R.id.toolname);
                holder.buynum = (TextView) convertView.findViewById(R.id.buynums);
                holder.buyprice = (TextView) convertView.findViewById(R.id.buyprices);
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
            holder.buynum.setText(listItem.get(position).get("number").toString());
            holder.buyprice.setText(listItem.get(position).get("toolbuyprice").toString());

            return convertView;
        }

    }



    /**存放控件*/
    public final class ViewHolder{
        public TextView toolname;
        public TextView buynum;
        public TextView buyprice;
        public ImageButton button_detail;
    }

    private void initintent(){
        i = new Intent();
        i = this.getIntent();
        Bundle b = i.getExtras();
        username = b.getString("username");
        textView = (TextView) this.findViewById(R.id.username);
        textView.setText(username);

        flag = b.getString("flag");
        ordername = b.getString("ordername");
        orderdate = b.getString("orderdate");

        // Log.v("tag",username);

    }


    private void initSpinner() {
        if( flag.equals("add")==true ) {

            date_list1 = new ArrayList<String>();
            date_list1.add(ordername);
            onamespinner = (Spinner) findViewById(R.id.ordername);
            tmpadapter1 =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, date_list1);
            onamespinner.setAdapter(tmpadapter1);
            onamespinner.setClickable(false);

            date_list1 = new ArrayList<String>();
            date_list1.add(orderdate);
            odatespinner = (Spinner) findViewById(R.id.orderdate);
            tmpadapter1 =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, date_list1);
            odatespinner.setAdapter(tmpadapter1);
            odatespinner.setClickable(false);
        }else {
            queryDataBaseAdapter = new QueryDataBaseAdapter(this);
            onamespinner = (Spinner) findViewById(R.id.ordername);

            odatespinner = (Spinner) findViewById(R.id.orderdate);
            odatespinner.setClickable(false);

            data_list = new ArrayList<String>();
            data_list = queryDataBaseAdapter.getOrderName(username);
            //适配器
            onameadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
            //设置样式
            onameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            onamespinner.setAdapter(onameadapter);
            onamespinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(
                                AdapterView<?> parent, View view, int position, long id) {
                            ordername = onameadapter.getItem(position).toString();
                            if (!ordername.equals("")) {
                                // Log.v("tag", trandate+" "+trandate1);
                                odatespinner.setClickable(true);
                                data_list = new ArrayList<String>();
                                data_list = queryDataBaseAdapter.getOrderDate(username, ordername);
                                if (odatedapter != null) {
                                    odatespinner.setAdapter(tmpadapter);
                                }
                                setOrderdateSpinner(data_list, odatespinner);
                            }

                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
        }
    }

    private void setOrderdateSpinner( ArrayList<String> item_list,Spinner spinner ){
        odatedapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, item_list);
        //设置样式
        odatedapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(odatedapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        orderdate = odatedapter.getItem(position).toString();

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }


    private void initActionBar(){
        //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setHomeButtonEnabled(true);//actionbar��������Ա����
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//��ʾ�����ͼ��

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == android.R.id.home  ){
            setResult(RESULT_FIRST_USER, i);
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
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                mProgressDialog = new ProgressDialog(CountOrderActivity.this);
                mProgressDialog.setMessage("正在获取数据");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return mProgressDialog;
        }
        return null;
    }
    class GetItemDateTask extends AsyncTask< String ,  Integer,  ArrayList<HashMap<String, Object>>> {//获取图片仍采用AsyncTask，这里的优化放到下篇再讨论

        String name;
        ArrayList<HashMap<String, Object>> listDate;
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( CountOrderActivity.this );

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
            listDate = qd.getToolOrder(params[0], params[1], params[2]);
            Log.v("tag", "查询子线程：：" + String.valueOf(Thread.currentThread().getId()));
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


    /**
     * 小数 四舍五入
     *
     * @param val
     * @param precision
     * @return
     */

    public static Double roundDouble(double val, int precision)
    {
        Double ret = null;
        try
        {
            double factor = Math.pow(10, precision);
            ret = Math.floor(val * factor + 0.5) / factor;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }




}
