package com.example.administrator.poseidon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.example.administrator.database.QueryDataBaseAdapter;

public class AddActivity extends ActionBarActivity
        implements View.OnTouchListener{
    private EditText date;
    private String username;
    private Spinner currentArrary;
    //private String curr;
    private Button bt;
    EditText editText;
    Integer ret=0;
    ArrayList<HashMap<String, Object>> listData;
    QueryDataBaseAdapter queryDataBaseAdapter ;
    Intent i;
    String sourcefrom, toolname, trandate, curry;
    Double totprice,feepercent,exchangerate,totprices,feetotprice,rmbprice,rmbtotprice,feeprice;
    Integer totnum,weight,totweight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        i = new Intent();
        i = this.getIntent();
        Bundle b = i.getExtras();
        username = b.getString("username");
        TextView textView = (TextView) this.findViewById(R.id.username);
        textView.setText(username);

        initActionBar();

        date = (EditText) this.findViewById(R.id.shopdate);
        date.setOnTouchListener(this);

       // curr  = getcurrent();

        bt = (Button) findViewById(R.id.backbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showaddshop();
            }
        });

    }

    private String getcurrent(){
        String current = null;
        String cur=null;
        this.currentArrary = (Spinner) super
                .findViewById(R.id.spinnercurrarray);
        this.currentArrary
                .setOnItemSelectedListener(new OnItemSelectedListenerImpl());
        current = currentArrary.getSelectedItem().toString();
        if(current.equals("人民币") == true)
            cur =  "CNY";
        if(current.equals("美元") == true)
            cur =  "USD";
        if(current.equals("日元") == true)
            cur = "JPY";
        if(current.equals("英镑") == true)
            cur =  "GBP";
        if(current.equals("港币") == true)
            cur =  "HKD";
        return cur;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if ( id == android.R.id.home  ){
            setResult(RESULT_OK, i);
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

    private void initActionBar(){

      //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setHomeButtonEnabled(true);//actionbar��������Ա����
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//��ʾ�����ͼ��

    }
    //下拉框选择事件
    private class OnItemSelectedListenerImpl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String city = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }

    }

    private void showaddshop() {


        HashMap<String, Object> map = new HashMap<String, Object>();
        listData = new ArrayList<HashMap<String, Object>>();
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);


        editText = (EditText) this.findViewById(R.id.sourcefrom);
        sourcefrom = editText.getText().toString();
        if(sourcefrom.isEmpty() ){
            Toast.makeText(AddActivity.this, "请输入商品来源！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }

        editText = (EditText) this.findViewById(R.id.shopdate);
        trandate = editText.getText().toString();
        if(trandate.isEmpty() ){
            Toast.makeText(AddActivity.this, "请选择下单日期！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
      //  Log.v("tag",sourcefrom);
        editText = (EditText) this.findViewById(R.id.toolname);
        toolname = editText.getText().toString();
        if(toolname.isEmpty() ){
            Toast.makeText(AddActivity.this, "请输入商品名称！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
       // Log.v("tag",toolname);

       // Log.v("tag",trandate);
        editText = (EditText) this.findViewById(R.id.totprice);
        if(editText.getText().toString().isEmpty() ){
            Toast.makeText(AddActivity.this, "请输入官网价格！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
        totprice = Double.valueOf(editText.getText().toString()) ;

        curry=getcurrent();

        editText = (EditText) this.findViewById(R.id.toolnum);
        if(editText.getText().toString().isEmpty() ){
            Toast.makeText(AddActivity.this, "请输入商品个数！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
        totnum = Integer.valueOf(editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.feepercent);
        if(editText.getText().toString().isEmpty() ){
            Toast.makeText(AddActivity.this, "请输入折扣率！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
        feepercent = Double.valueOf(editText.getText().toString());

        editText = (EditText) this.findViewById(R.id.exchangerate);
        if(editText.getText().toString().isEmpty() ){
            Toast.makeText(AddActivity.this, "请输入汇率！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
        exchangerate = Double.valueOf(editText.getText().toString()) ;

        editText = (EditText) this.findViewById(R.id.weight);
        if(editText.getText().toString().isEmpty() || editText.getText().toString().equals("0") ){
            Toast.makeText(AddActivity.this, "请输入商品单个重量！", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
        weight = Integer.valueOf(editText.getText().toString());

        totweight = totnum * weight;

        totprices = roundDouble( totnum * totprice, 2);

        feeprice = roundDouble( totprice * ( feepercent / 100 ), 2);

        feetotprice =roundDouble( feeprice * totnum , 2);

        rmbprice = roundDouble( feeprice * exchangerate, 2 );

        rmbtotprice =roundDouble( rmbprice * totnum , 2 );

        String[] detail =  new String[13];
        detail[0] = "商品名称："+toolname;
        detail[1] = "商品来源："+sourcefrom;
        detail[2] = "下单日期："+trandate;
        detail[3] = "商品个数："+totnum.toString();
        detail[4] = "官网价格："+curry+" "+totprice.toString();
        detail[5] = "折扣率："+feepercent.toString()+"%";
        detail[6] = "交易汇率："+exchangerate.toString();
        detail[7] = "商品单个重量(g)："+weight.toString();
        detail[8] = "商品合重(g)："+totweight.toString();
        detail[9] = "税前总价格(g)："+curry+" "+totprices.toString();
        detail[10] = "税后总价格(g)："+curry+" "+feetotprice.toString();
        detail[11] = "人民币单价："+rmbprice.toString() +"元/个";
        detail[12] = "人民币总价："+rmbtotprice.toString() +"元";

        map.put("sourcefrom", sourcefrom);
        map.put("toolname", toolname ) ;
        map.put("trandate",trandate ) ;
        map.put("currency",curry );
        map.put("number", totnum );
        map.put("soldnumber",  0 );
        map.put("totprice", totprice );
        map.put("feepercent", feepercent );
        map.put("feeprice",feeprice );
        map.put("exchangerate", exchangerate );
        map.put("rmb_totprice", rmbtotprice );
        map.put("rmb_singprice", rmbprice );
        map.put("transprice", 0.00 );
        map.put("singlesum", rmbprice );
        map.put("weight", weight );
        map.put("totweight", totweight );
        map.put("beizhu", "" );
        map.put("user", username );
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
                        ret = queryDataBaseAdapter.insertToolDtl(username, listData);
                        if (ret == -100) {
                            //editText.setSelection(3);
                        } else {
                           // Toast.makeText(AddActivity.this, "商品已经被成功添加！", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder( AddActivity.this).
                                    setTitle("成功添加").
                                    setMessage("您已经成功添加该商品！\n是否继续添加\n否则进行结算").
                                    setPositiveButton(
                                            "继续添加",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // 这里是你点击确定之后可以进行的操作
                                                    dialog.cancel();
                                                    setEditMode();
                                                }
                                            }).
                                    setNegativeButton(
                                            "商品结算",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // 这里是你点击确定之后可以进行的操作
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
                                            }).show();
                        }
                        dialog.cancel();

                    }
                }).create();
        alertDialog.show();


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

    private void setEditMode(){

        editText = (EditText) this.findViewById(R.id.sourcefrom);
        editText.setEnabled(false);

        //  Log.v("tag",sourcefrom);
        editText = (EditText) this.findViewById(R.id.shopdate);
        editText.setEnabled(false);
        // Log.v("tag",toolname);
        editText = (EditText) this.findViewById(R.id.toolname);
        editText.setText("");
        editText.requestFocus();
       // editText.setSelection(R.id.toolname);
        // Log.v("tag",trandate);
        editText = (EditText) this.findViewById(R.id.totprice);
        editText.setText("");

        editText = (EditText) this.findViewById(R.id.toolnum);
        editText.setText("");

        editText = (EditText) this.findViewById(R.id.feepercent);
        editText.setText("");

        editText = (EditText) this.findViewById(R.id.exchangerate);
        editText.setText("");

        editText = (EditText) this.findViewById(R.id.weight);
        editText.setText("");
    }
}
