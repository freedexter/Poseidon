package com.example.administrator.poseidon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.database.QueryDataBaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class AddOrderActivity extends ActionBarActivity implements View.OnTouchListener{

    Intent i;
    EditText date,editText;
    TextView textView;
    Button bt;
    ArrayAdapter<String> soureadapter=null,dateadapter=null,tooladapter=null;
    Spinner sourcespinner=null,datespinner=null,toolspinner=null;
    ArrayAdapter<String> tmpadapter = null;
    String username,sourcefrom,toolname,trandate,currency,ordername,orderdate;
    String flag=null,toolname1=null,trandate1=null;
    Double totprice, transprice,exchangerate,buyprice,rmb_singprice,rmb_totprice;
    QueryDataBaseAdapter queryDataBaseAdapter;
    ArrayList<String> data_list;
    ArrayList<HashMap<String, Object>> listData;
    View layout1 = null;
    View layout2 = null;
    boolean ret=true;
    Integer restnum=0,buynum,soldnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);


        i = new Intent();
        i = this.getIntent();
        Bundle b = i.getExtras();
        username = b.getString("username");
        flag = b.getString("flag");



        final TextView textView = (TextView) this.findViewById(R.id.username);
        textView.setText(username);

        initActionBar();
        date = (EditText) this.findViewById(R.id.orderdate);
        date.setOnTouchListener(this);
        if(flag.equals("add")==true){
            ordername =  b.getString("ordername");
            editText = (EditText) this.findViewById(R.id.ordername);
            editText.setText(ordername);
            editText.setEnabled(false);
            orderdate =  b.getString("orderdate");
            date.setText(orderdate);
            date.setEnabled(false);
        }

        initSpinner();
        LayoutInflater inflater = this.getLayoutInflater();
        layout1 = inflater.inflate(R.layout.activity_add_order, null);
        layout2 = inflater.inflate(R.layout.activity_add_order_dtl, null);
        bt = (Button) findViewById(R.id.clearbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag.equals("add")==false) {
                    editText = (EditText) findViewById(R.id.ordername);
                    editText.setText("");
                    date.setText("");
                }
                sourcespinner.setSelection(0, true);
                sourcefrom = "";
                if (datespinner != null) {
                    datespinner.setAdapter(tmpadapter);
                    trandate = "";
                }
                if (tooladapter != null) {
                    toolspinner.setAdapter(tmpadapter);
                    toolname="";
                }
            }
        });

        bt = (Button) findViewById(R.id.qrybutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ret = qryorder();
                if( ret == true ){
                    setContentView(layout2);
                    showtooldtl();

                }
            }
        });

    }
    private void showtooldtl(){
        textView  = (TextView) findViewById(R.id.toolname);
        textView.setText(toolname);
        textView  = (TextView) findViewById(R.id.restnum);
        textView.setText( restnum.toString() );
  //      textView  = (TextView) findViewById(R.id.totprice);
    //    textView.setText( totprice.toString() );
        textView  = (TextView) findViewById(R.id.transprice);
        textView.setText(transprice.toString());
        textView  = (TextView) findViewById(R.id.totpriceflag);
        textView.setText(currency.toString());
        textView  = (TextView) findViewById(R.id.rmb_singprice);
        textView.setText( rmb_singprice.toString() );

        bt = (Button) findViewById(R.id.clearbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = (EditText) findViewById(R.id.exchangerate);
                editText.setText("");
                editText.requestFocus();
          //      editText = (EditText) findViewById(R.id.buyprice);
            //    editText.setText("");
                editText = (EditText) findViewById(R.id.buynum);
                editText.setText("");
            }
        });
        bt = (Button) findViewById(R.id.confimbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confimOrder();
            }
        });



    }
    private void confimOrder(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();

        if( ((EditText) findViewById(R.id.exchangerate)).getText().toString().isEmpty() ){
            Toast.makeText(AddOrderActivity.this, "请填写交易汇率！", Toast.LENGTH_SHORT).show();
        }else{
            editText = (EditText) findViewById(R.id.exchangerate);
            exchangerate = Double.valueOf(editText.getText().toString());

            if( ((EditText) findViewById(R.id.buynum)).getText().toString().isEmpty() ) {
                Toast.makeText(AddOrderActivity.this, "请填写购买数量！", Toast.LENGTH_SHORT).show();
            }else {
                editText = (EditText) findViewById(R.id.buynum);
                buynum = Integer.valueOf(editText.getText().toString());

                if (buynum > restnum) {
                    Toast.makeText(AddOrderActivity.this, "购买数量不得大于剩余数量！", Toast.LENGTH_SHORT).show();
                } else {
                    buyprice = roundDouble( exchangerate * totprice * buynum ,2 );
                    map.put("customer", ordername);
                    map.put("orderdate", orderdate);
                    map.put("toolsource", sourcefrom);
                    map.put("toolname", toolname);
                    map.put("trandate", trandate);
                    map.put("number", buynum);
                    map.put("rmb_totprice", rmb_totprice);
                    map.put("exchangerate", exchangerate);
                    map.put("toolbuyprice", buyprice);
                    listData.add(map);

                    Dialog alertDialog = new AlertDialog.Builder(this).
                            setTitle("新增订单").
                            setMessage(toolname+"购买价格"+buyprice+"人民币！\n确认提交订单信息").
                            setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    dialog.cancel();
                                }
                            }).
                            setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ret = queryDataBaseAdapter.insertToolOrder(username, listData);
                                    if (ret == false) {
                                        Toast.makeText(AddOrderActivity.this, "当日该商品订单已存在！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        queryDataBaseAdapter.updateRestNumToolDtl(username, toolname, sourcefrom, trandate, soldnum + buynum);
                                        new AlertDialog.Builder(AddOrderActivity.this).
                                                setTitle("成功添加").
                                                setMessage("您已经成功添加订单！\n是否继续添加\n否则进行订单结算").
                                                setPositiveButton(
                                                        "继续添加",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                // 这里是你点击确定之后可以进行的操作
                                                                i = new Intent();
                                                                i = getIntent();
                                                                Bundle b = new Bundle();
                                                                b.putString("flag", "add");
                                                                b.putString("ordername", ordername);
                                                                b.putString("orderdate", orderdate);
                                                                //  i.setClass(getApplicationContext(), AddOrderActivity.class);
                                                                i.putExtras(b);

                                                                setResult(RESULT_FIRST_USER, i);
                                                                finish();


                                                                startActivity(i);
                                                                dialog.cancel();
                                                            }
                                                        }).
                                                setNegativeButton(
                                                        "订单结算",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                // 这里是你点击确定之后可以进行的操作
                                                                i = new Intent();
                                                                Bundle b = new Bundle();
                                                                b.putString("flag", "add");
                                                                b.putString("username", username);
                                                                b.putString("ordername", ordername);
                                                                b.putString("orderdate", orderdate);
                                                                i.setClass(getApplicationContext(), CountOrderActivity.class);
                                                                i.putExtras(b);
                                                                startActivity(i);
                                                                setResult(RESULT_FIRST_USER, i);
                                                                finish();
                                                            }
                                                        }).show();
                                    }

                                }
                            }).create();
                    alertDialog.show();
                }
            }
        }


    }

    private boolean qryorder(){
        //HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        Integer totnum=0;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        editText = (EditText) findViewById(R.id.ordername);
        ordername = editText.getText().toString();
        editText = (EditText) findViewById(R.id.orderdate);
        orderdate = editText.getText().toString();

        if(ordername.isEmpty()){
            Toast.makeText(AddOrderActivity.this, "请填写订单人姓名！", Toast.LENGTH_SHORT).show();
        }else{
            if( orderdate.isEmpty()){
                Toast.makeText(AddOrderActivity.this, "请填写订单日期！", Toast.LENGTH_SHORT).show();
            }else{
                if(sourcefrom.isEmpty()){
                    Toast.makeText(AddOrderActivity.this, "请选择商品来源！", Toast.LENGTH_SHORT).show();
                }else {
                    if (trandate.isEmpty()) {
                        Toast.makeText(AddOrderActivity.this, "请选择下单日期！", Toast.LENGTH_SHORT).show();
                    }else{
                        if (toolname.isEmpty()){
                            Toast.makeText(AddOrderActivity.this, "请选择商品名称！", Toast.LENGTH_SHORT).show();
                        }else {
                            listData = queryDataBaseAdapter.getToolDtlDtl(username, toolname, sourcefrom, trandate);
                            totnum = Integer.valueOf(listData.get(0).get("number").toString());
                            soldnum = Integer.valueOf(listData.get(0).get("soldnumber").toString());
                            totprice = Double.valueOf(listData.get(0).get("totprice").toString());
                            transprice = Double.valueOf(listData.get(0).get("transprice").toString());
                            rmb_singprice = Double.valueOf(listData.get(0).get("rmb_singprice").toString());
                            rmb_totprice = Double.valueOf(listData.get(0).get("rmb_totprice").toString());
                            currency = listData.get(0).get("currency").toString();
                            restnum =  totnum - soldnum ;
                            if( restnum <= 0 ){
                                Toast.makeText(AddOrderActivity.this, "该商品已没有存货！", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            return true;
                            // getmodidtl();
                        }
                    }
                }
            }
        }

        return false;

    }

    private void initSpinner() {
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        sourcespinner = (Spinner) findViewById(R.id.sourcefrom);

        datespinner = (Spinner) findViewById(R.id.trandate);
        datespinner.setClickable(false);
        toolspinner = (Spinner) findViewById(R.id.toolname);
        toolspinner.setClickable(false);

        data_list = new ArrayList< String >();
        data_list = queryDataBaseAdapter.getSourceFrom(username);
        //适配器
        soureadapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        soureadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sourcespinner.setAdapter(soureadapter);
        sourcespinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        sourcefrom = soureadapter.getItem(position).toString();
                        if(!sourcefrom.equals("")) {
                            // Log.v("tag", trandate+" "+trandate1);
                            datespinner.setClickable(true);
                            data_list = new ArrayList<String>();
                            data_list = queryDataBaseAdapter.getTranDate(username, sourcefrom);
                            if (dateadapter != null) {
                                datespinner.setAdapter(tmpadapter);
                            }
                            if (tooladapter != null) {
                                toolspinner.setAdapter(tmpadapter);
                            }
                            setTrandateSpinner(data_list, datespinner);
                        }

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void setTrandateSpinner( ArrayList<String> item_list,Spinner spinner ){
        dateadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, item_list);
        //设置样式
        dateadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(dateadapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        trandate = dateadapter.getItem(position).toString();
                        if(!trandate.equals("")) {
                            // Log.v("tag", sourcefrom + " " + sourcefrom1);
                            toolspinner.setClickable(true);
                            data_list = new ArrayList<String>();
                            data_list = queryDataBaseAdapter.getToolName(username, sourcefrom, trandate);
                            if (tooladapter != null) {
                                toolspinner.setAdapter(tmpadapter);
                            }
                            setToolnameSpinner(data_list, toolspinner);
                        }

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }


    private void setToolnameSpinner( ArrayList<String> item_list,Spinner spinner ){
        tooladapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, item_list);
        //设置样式
        tooladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(tooladapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        toolname = tooladapter.getItem(position).toString();
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
        getMenuInflater().inflate(R.menu.menu_add_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if ( id == android.R.id.home  ){
            setResult( RESULT_FIRST_USER, i );
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
