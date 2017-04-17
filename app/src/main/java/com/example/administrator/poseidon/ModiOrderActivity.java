package com.example.administrator.poseidon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.database.QueryDataBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class ModiOrderActivity extends ActionBarActivity {

    Intent i;
    String username,ordername,orderdate,source,toolname,trandate;
    Integer number,restnum,buynum;
    Double exchangerate;
    TextView textView;
    QueryDataBaseAdapter queryDataBaseAdapter;
    ArrayAdapter<String> ordernameadapter=null,orderdateadapter=null,sourceadapter=null,tooladapter=null;
    Spinner ordernamespinner=null,orderdatepinner=null,sourcepinner=null,toolspinner=null;
    ArrayAdapter<String> tmpadapter = null;
    ArrayList<String> data_list;
    ArrayList<HashMap<String, Object>> listData;
    Button bt;
    View layout1 = null;
    View layout2 = null;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modi_order);



        initintent();
        initActionBar();
        initSpinner();

        LayoutInflater inflater = this.getLayoutInflater();
        layout1 = inflater.inflate(R.layout.activity_modi_order, null);
        layout2 = inflater.inflate(R.layout.activity_modi_order_dtl, null);

        bt = (Button) findViewById(R.id.clearbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ordernamespinner.setSelection(0, true);
                if (orderdatepinner != null) {
                    orderdatepinner.setAdapter(tmpadapter);
                }
                if (sourcepinner != null) {
                    sourcepinner.setAdapter(tmpadapter);
                }
                if (tooladapter != null) {
                    toolspinner.setAdapter(tmpadapter);
                }

            }
        });
        bt = (Button) findViewById(R.id.qrybutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ordername.isEmpty()){
                    Toast.makeText(ModiOrderActivity.this, "请选择订单人姓名！", Toast.LENGTH_SHORT).show();
                }else {
                    if (orderdate.isEmpty()) {
                        Toast.makeText(ModiOrderActivity.this, "请选择订单日期！", Toast.LENGTH_SHORT).show();
                    }else{
                        if (source.isEmpty()){
                            Toast.makeText(ModiOrderActivity.this, "请选择商品来源！", Toast.LENGTH_SHORT).show();
                        }else {
                            if (toolname.isEmpty()){
                                Toast.makeText(ModiOrderActivity.this, "请选择商品名称！", Toast.LENGTH_SHORT).show();
                            }else {
                                setContentView(layout2);
                                getmodiorderdtl();
                            }
                        }
                    }
                }

            }
        });


    }


    private void getmodiorderdtl(){
        String date;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        listData = new ArrayList<HashMap<String, Object>>();
        //    Log.v("tag",username+sourcefrom+toolname+trandate);
        listData = queryDataBaseAdapter.getToolOrderDtl(username,ordername,orderdate,source,toolname);
//Log.v("tag",String.valueOf(listData.size()));
        textView = (TextView) findViewById(R.id.ordername);
        textView.setText(ordername);
        textView = (TextView) findViewById(R.id.orderdate);
        date = orderdate.substring(0,4)+"年"+orderdate.substring(4,6)+"月"+orderdate.substring(6,8)+"日";
        textView.setText(date);
        textView = (TextView) findViewById(R.id.toolname);
        textView.setText(toolname);

        textView = (TextView) findViewById(R.id.rmb_totprice);
        textView.setText(listData.get(0).get("toolprice").toString());

        textView = (TextView) findViewById(R.id.buyprice);
        textView.setText(listData.get(0).get("toolbuyprice").toString());

        editText = (EditText) findViewById(R.id.buynum);
        number = Integer.valueOf( listData.get(0).get("number").toString() );
        editText.setText( number.toString() );

        editText = (EditText) findViewById(R.id.buyexcrate);
        exchangerate = Double.valueOf( listData.get(0).get("exchangerate").toString() );
        editText.setText( exchangerate.toString() );

        trandate = listData.get(0).get("trandate").toString();

        bt = (Button) findViewById(R.id.deletebuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrderDtl();

            }
        });
        bt = (Button) findViewById(R.id.confimbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modiOrderDtl();
            }
        });



    }
    private void modiOrderDtl(){
        Integer restnum=0, buynum=0,newnum=0,num1=0;
        editText = (EditText) findViewById(R.id.buynum);
        buynum = Integer.valueOf( editText.getText().toString());

        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        restnum = queryDataBaseAdapter.getToolRestnum(   username ,  toolname ,  source  ,  trandate);
        newnum = restnum + number - buynum;
        num1= restnum + number;
        if( newnum < 0 ){
            Toast.makeText(ModiOrderActivity.this, "购买数量"+buynum.toString()+"超过商品的剩余数量"+ num1.toString()+"个！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
        }else {
            countOrder();
        }

    }

    private void countOrder(){
        Double totprice=0.00,buyexcrate=0.00,totprices=0.00,buyprice=0.00;

        String currency;
        ArrayList<HashMap<String, Object>> datalist;
        HashMap<String, Object> map = new HashMap<String, Object>();
        datalist = new  ArrayList<HashMap<String, Object>>();
        editText = (EditText) findViewById(R.id.buynum);
        buynum = Integer.valueOf( editText.getText().toString());
        editText = (EditText) findViewById(R.id.buyexcrate);
        buyexcrate = Double.valueOf(editText.getText().toString());

        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        datalist = queryDataBaseAdapter.getToolDtlDtl(username, toolname, source, trandate);
        currency = datalist.get(0).get("currency").toString();
        totprice = Double.valueOf(datalist.get(0).get("totprice").toString());
        totprices = roundDouble(totprice * buynum, 2);
        buyprice = roundDouble( totprice * buyexcrate * buynum, 2 );


        String[] detail =  new String[10];
        detail[0] = "定单人："+ordername;
        detail[1] = "定单日期："+orderdate;
        detail[2] = "商品来源："+source;
        detail[3] = "商品名称："+toolname;
        detail[4] = "下单日期："+trandate;
        detail[5] = "购买数量："+buynum.toString();
        detail[6] = "商品单价："+totprice.toString()+" "+currency;
        detail[7] = "商品总价："+totprices.toString()+" "+currency;
        detail[8] = "购买汇率："+buyexcrate.toString();
        detail[9] = "购买总价："+buyprice.toString()+" 人民币";
        listData = new  ArrayList<HashMap<String, Object>>();
        map.put("customer", ordername);
        map.put("orderdate", orderdate);
        map.put("toolsource", source);
        map.put("toolname", toolname);
        map.put("trandate", trandate);
        map.put("number", buynum);
        map.put("toolprice", totprices ) ;
        map.put("exchangerate",buyexcrate ) ;
        map.put("toolbuyprice",buyprice );
        listData.add(map);

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("更新订单明细").
                setItems(detail, null).
                setPositiveButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).
                setNegativeButton("确认提交", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // Log.v("tag","123123");

                        queryDataBaseAdapter.updateModiToolOrder(username, listData);
                        restnum = getToolRestNum();
                        restnum = restnum + buynum;
                        updateToolRestNum(restnum);
                        new AlertDialog.Builder(ModiOrderActivity.this).
                                setTitle("成功更新").
                                setMessage("订单已成功更新！" + ordername + "相关订单总金额已改变，请重新结算！").
                                setPositiveButton(
                                        "确认",
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
                        //dialog.cancel();

                    }
                }).create();
        alertDialog.show();



    }

    private  void  deleteOrderDtl(){
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("删除订单").
                setMessage("确认删除订单信息").
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
                        // TODO Auto-generated method stub
                        deletetoolorder();
                        restnum = getToolRestNum();
                        updateToolRestNum(restnum);
                    }
                }).create();
        alertDialog.show();
    }

    private void deletetoolorder(){
        Integer count=0;

        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        queryDataBaseAdapter.DeleteToolOrder( username, ordername ,orderdate,   source,  toolname,trandate,number );
        count = getordercount();
        if(count <= 0 ){
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("删除订单").
                    setMessage( ordername+"订单已删除！").
                    setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            setResult(RESULT_FIRST_USER, i);
                            finish();
                        }
                    }).create();
            alertDialog.show();
        }else {
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("删除订单").
                    setMessage("该笔订单已删除！\n" + ordername + "相关订单总金额已改变，请重新结算！").
                    setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
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
                    }).create();
            alertDialog.show();
        }

    }

    private Integer getordercount(){
        Integer totnum=0;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        totnum = queryDataBaseAdapter.getOrderCount(username,ordername,orderdate);
        return totnum;
    }

    private Integer getToolRestNum(){
        Integer soldnum=0,restnum=0;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        soldnum =  queryDataBaseAdapter.getToolSoldnum(  username ,  toolname ,  source ,  trandate);
        restnum = soldnum -  number;
       // Log.v("tag", "restnum=" + restnum.toString());
        return restnum;
    }
    private void updateToolRestNum( Integer restnum ){
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
      //  Log.v("tag", "haha" );
        queryDataBaseAdapter.updateRestNumToolDtl(username, toolname, source, trandate, restnum );

    }


    private void initSpinner() {
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        ordernamespinner = (Spinner) findViewById(R.id.ordername);

        orderdatepinner = (Spinner) findViewById(R.id.orderdate);
        orderdatepinner.setClickable(false);
        sourcepinner = (Spinner) findViewById(R.id.source);
        sourcepinner.setClickable(false);
        toolspinner = (Spinner) findViewById(R.id.toolname);
        toolspinner.setClickable(false);

        data_list = new ArrayList< String >();
        data_list = queryDataBaseAdapter.getOrderName(username);
        //适配器
        ordernameadapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        ordernameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        ordernamespinner.setAdapter(ordernameadapter);
        ordernamespinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        ordername = ordernameadapter.getItem(position).toString();
                        if(!ordername.equals("")) {
                            // Log.v("tag", trandate+" "+trandate1);
                            orderdatepinner.setClickable(true);
                            data_list = new ArrayList<String>();
                            data_list = queryDataBaseAdapter.getOrderDate(username, ordername);
                            if (orderdateadapter != null) {
                                orderdatepinner.setAdapter(tmpadapter);
                            }
                            if (sourceadapter != null) {
                                sourcepinner.setAdapter(tmpadapter);
                            }
                            if (tooladapter != null) {
                                toolspinner.setAdapter(tmpadapter);
                            }
                            setOrderdateSpinner(data_list, orderdatepinner);
                        }

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void setOrderdateSpinner( ArrayList<String> item_list,Spinner spinner ){
        orderdateadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, item_list);
        //设置样式
        orderdateadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(orderdateadapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        orderdate = orderdateadapter.getItem(position).toString();
                        if(!orderdate.equals("")) {
                            // Log.v("tag", sourcefrom + " " + sourcefrom1);
                            sourcepinner.setClickable(true);
                            data_list = new ArrayList<String>();
                            data_list = queryDataBaseAdapter.getToolSource(username, ordername, orderdate);
                            if (sourceadapter != null) {
                                sourcepinner.setAdapter(tmpadapter);
                            }
                            if (tooladapter != null) {
                                toolspinner.setAdapter(tmpadapter);
                            }
                           setSourceSpinner(data_list, sourcepinner);
                        }

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }


    private void setSourceSpinner( ArrayList<String> item_list,Spinner spinner ){
        sourceadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, item_list);
        //设置样式
        sourceadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(sourceadapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        source = sourceadapter.getItem(position).toString();
                        if(!source.equals("")) {
                            // Log.v("tag", sourcefrom + " " + sourcefrom1);
                            toolspinner.setClickable(true);
                            data_list = new ArrayList<String>();
                            data_list = queryDataBaseAdapter.getToolname(username, ordername, orderdate, source);
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
        getMenuInflater().inflate(R.menu.menu_modi_order, menu);
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
            finish();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
