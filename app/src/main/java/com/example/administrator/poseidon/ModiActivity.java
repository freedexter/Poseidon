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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.database.QueryDataBaseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ModiActivity extends ActionBarActivity {

    Intent i;
    ListView listView;
    TextView textView;
    String username,sourcefrom,toolname,trandate,curry;
    String sourcefrom1=null,toolname1=null,trandate1=null;
    EditText date,editText;
    View layout1 = null;
    View layout2 = null;
    Button bt;
    ArrayAdapter<String> soureadapter=null,dateadapter=null,tooladapter=null;
    Spinner sourcespinner=null,datespinner=null,toolspinner=null;
    ArrayAdapter<String> tmpadapter = null;
    ArrayList<String> data_list;
    ArrayList<HashMap<String, Object>> listData;
    int ret=0;

    QueryDataBaseAdapter queryDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modi);

        initintent();
        initActionBar();
        initSpinner();
        LayoutInflater inflater = this.getLayoutInflater();
        layout1 = inflater.inflate(R.layout.activity_modi, null);
        layout2 = inflater.inflate(R.layout.activity_modi_dtl, null);
        bt = (Button) findViewById(R.id.clearbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourcespinner.setSelection(0, true);
                if (datespinner != null) {
                    datespinner.setAdapter(tmpadapter);
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
                if(sourcefrom.isEmpty()){
                    Toast.makeText(ModiActivity.this, "请选择商品来源！", Toast.LENGTH_SHORT).show();
                }else {
                    if (trandate.isEmpty()) {
                        Toast.makeText(ModiActivity.this, "请选择下单日期！", Toast.LENGTH_SHORT).show();
                    }else{
                        if (toolname.isEmpty()){
                            Toast.makeText(ModiActivity.this, "请选择商品名称！", Toast.LENGTH_SHORT).show();
                        }else {
                            setContentView(layout2);
                            getmodidtl();
                        }
                    }
                }

            }
        });
    }

    private void getmodidtl(){
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        listData = new ArrayList<HashMap<String, Object>>();
    //    Log.v("tag",username+sourcefrom+toolname+trandate);
        listData = queryDataBaseAdapter.getToolDtlDtl(username,toolname,sourcefrom,trandate);
//Log.v("tag",String.valueOf(listData.size()));
        textView = (TextView) findViewById(R.id.toolname);
        textView.setText(toolname);

        editText = (EditText) findViewById(R.id.totprices);
        editText.setText(listData.get(0).get("totprice").toString());

        textView = (TextView) findViewById(R.id.currenttype);
        curry = listData.get(0).get("currency").toString();
        textView.setText(curry);

        editText = (EditText) findViewById(R.id.number);
        editText.setText(listData.get(0).get("number").toString());

        editText = (EditText) findViewById(R.id.feepercents);
        editText.setText(listData.get(0).get("feepercent").toString());

        editText = (EditText) findViewById(R.id.exchangerates);
        editText.setText(listData.get(0).get("exchangerate").toString());

        editText = (EditText) findViewById(R.id.transprices);
        editText.setText(listData.get(0).get("transprice").toString());

        editText = (EditText) findViewById(R.id.weights);
        editText.setText(listData.get(0).get("weight").toString());

        username = listData.get(0).get("user").toString();

        bt = (Button) findViewById(R.id.backbuttom);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletetool();
              //  setResult( RESULT_OK, i);
             //   finish();
            }
        });
        bt = (Button) findViewById(R.id.countbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showaddshop();
            }
        });



    }

    private void deletetool(){
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("删除商品").
                setMessage("确认删除该商品信息").
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
                        deletetooldtl();
                    }
                }).create();
        alertDialog.show();
    }

    private void deletetooldtl(){
        Integer num=0;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        queryDataBaseAdapter.DeleteTooldtl( username, sourcefrom, toolname, trandate  );

        num=getdtlcount();
        if( num <= 0) {
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("删除商品").
                    setMessage("商品" + toolname + "已删除！").
                    setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            setResult(RESULT_OK, i);
                            finish();
                        }
                    }).create();
            alertDialog.show();
        }else{
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("删除商品").
                    setMessage("商品" + toolname + "已删除！\n"+sourcefrom+"相关商品结算金额已改变，请重新结算！").
                    setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            i = new Intent();
                            Bundle b = new Bundle();
                            b.putString("flag", "add");
                            b.putString("username", username);
                            b.putString("sourcefrom", sourcefrom);
                            b.putString("trandate", trandate);
                            i.setClass(getApplicationContext(), CountActivity.class);
                            i.putExtras(b);
                            startActivity(i);
                            setResult(RESULT_OK, i);
                            finish();
                        }
                    }).create();
            alertDialog.show();
        }
    }
    private Integer getdtlcount(){
        Integer totnum=0;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        totnum = queryDataBaseAdapter.getdtlCount(username, sourcefrom,  trandate);
        return totnum;
    }

    private void showaddshop() {
        //int ret=0;
        listData = new ArrayList<HashMap<String, Object>>();
       queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        Double totprice,totprices,exchangerates,transprices,feeprice,feetotprice,rmbprice,rmbtotprice,singlesum,feepercent;
        Integer number,feepercents, weights,totweight;
        HashMap<String, Object> map = new HashMap<String, Object>();

        editText = (EditText) this.findViewById(R.id.totprices);
        totprice = Double.valueOf( editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.number);
        number = Integer.valueOf( editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.exchangerates);
        exchangerates = Double.valueOf( editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.transprices);
        transprices = Double.valueOf( editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.feepercents);
        feepercent = Double.valueOf( editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.weights);
        weights = Integer.valueOf(editText.getText().toString());

        totweight =  weights * number;

        totprices = roundDouble(totprice * number, 2);

        feeprice =roundDouble( totprice * ( feepercent / 100 ),2);

        feetotprice = roundDouble( feeprice * number , 2);

        rmbprice = roundDouble( feeprice * exchangerates, 2 );

        singlesum = roundDouble( rmbprice + transprices, 2);

        rmbtotprice =roundDouble( singlesum * number , 2 );

        SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyyMMdd");
        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);

        String[] detail =  new String[16];
        detail[0] = "商品名称："+toolname;
        detail[1] = "商品来源："+sourcefrom;
        detail[2] = "下单日期："+trandate;
        detail[3] = "商品个数："+number.toString();
        detail[4] = "官网价格："+curry+" "+totprice.toString();
        detail[5] = "折扣率："+feepercent.toString()+"%";
        detail[6] = "交易汇率："+exchangerates.toString();
        detail[7] = "商品单个重量(g)："+weights.toString();
        detail[8] = "商品合重(g)："+totweight.toString();
        detail[9] = "税前总价格(g)："+curry+" "+totprices.toString();
        detail[10] = "税后总价格(g)："+curry+" "+feetotprice.toString();
        detail[11] = "人民币单价："+rmbprice.toString() +"元/个";
        detail[12] = "海运费价："+transprices.toString() +"元";
        detail[13] = "海运费+商品单价："+singlesum.toString() +"元";
        detail[14] = "人民币总价："+rmbtotprice.toString() +"元";
        detail[15] = "更新日期："+str;

        map.put("sourcefrom", sourcefrom);
        map.put("toolname", toolname ) ;
        map.put("trandate",trandate ) ;
        map.put("currency",curry );
        map.put("number", number );
        map.put("totprice", totprice );
        map.put("feepercent", feepercent );
        map.put("feeprice",feeprice );
        map.put("exchangerate", exchangerates );
        map.put("rmb_totprice", rmbtotprice );
        map.put("rmb_singprice", rmbprice );
        map.put("transprice", transprices );
        map.put("singlesum", singlesum );
        map.put("weight", weights );
        map.put("totweight", totweight );
        map.put("beizhu", str );
        //map.put("user", username );
        listData.add(map);

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("商品明细").
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

                        ret = queryDataBaseAdapter.updateModiToolDtl(username, listData);
                       // Log.v("tag", "456456");
                        // Toast.makeText(AddActivity.this, "商品已经被成功添加！", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(ModiActivity.this).
                                setTitle("成功更新").
                                setMessage("商品已成功更新！").
                                setPositiveButton(
                                        "完成",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                // 这里是你点击确定之后可以进行的操作
                                                dialog.cancel();
                                                setResult(RESULT_OK, i);
                                                finish();
                                            }
                                        }).show();
                        //dialog.cancel();

                    }
                }).create();
        alertDialog.show();


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
        getMenuInflater().inflate(R.menu.menu_modi, menu);
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
