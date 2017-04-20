package com.example.administrator.poseidon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


public class CountActivity extends ActionBarActivity  implements View.OnTouchListener{
    Intent i;
    String username,sourcefrom,trandate,flag;
    EditText date,editText;
    Button bt;
    QueryDataBaseAdapter queryDataBaseAdapter ;
    ArrayList<HashMap<String, Object>> listData;
    ListView listView;
    TextView textView;
    Double totTranPrices,transfee;
    Integer totweight;
    View layout1 = null;
    View layout2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        initintent();
        initActionBar();
        date = (EditText) this.findViewById(R.id.shopdate);
        date.setOnTouchListener(CountActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        layout1 = inflater.inflate(R.layout.activity_count, null);
        layout2 = inflater.inflate(R.layout.activity_count_dtl, null);

        bt = (Button) findViewById(R.id.countbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(layout2);

                countdtl();
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

        flag = b.getString("flag");
      if( flag.equals("add")==true ) {
            sourcefrom = b.getString("sourcefrom");
            trandate = b.getString("trandate");

            editText = (EditText) this.findViewById(R.id.sourcefrom);
            editText.setText(sourcefrom);
            editText.setEnabled(false);

            editText = (EditText) this.findViewById(R.id.shopdate);
            editText.setText(trandate);
            editText.setEnabled(false);

            editText = (EditText) this.findViewById(R.id.tottransprices);
            editText.requestFocus();

        }

       // Log.v("tag",username);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count, menu);
        return true;
    }

    private void initActionBar(){
        //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setHomeButtonEnabled(true);//actionbar��������Ա����
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//��ʾ�����ͼ��

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


    private  void  countdtl(){

        int ret=0;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        //Double rmb_singprice,tottransprice;
        editText = (EditText) this.findViewById(R.id.sourcefrom);
        sourcefrom = editText.getText().toString().trim();
        editText = (EditText) this.findViewById(R.id.shopdate);
        trandate = editText.getText().toString().trim();
       // Log.v("tag",sourcefrom+" "+trandate );

        editText = (EditText) this.findViewById(R.id.tottransprices);
        if( editText.getText().toString().isEmpty() ){
            Toast.makeText( CountActivity.this, "运费总价格不能为空！", Toast.LENGTH_SHORT ).show();
            return;
        }
        totTranPrices = Double.valueOf(editText.getText().toString());
        totweight = queryDataBaseAdapter.getCountDtlSum( username, sourcefrom, trandate );

        Log.v("tag",totweight.toString());

        if( totweight == 0 ){
            //setContentView(layout1);
            Toast.makeText( CountActivity.this, "没有可供结算的商品！", Toast.LENGTH_SHORT ).show();
            return;
        }else{
            setContentView(layout2);


            transfee =  roundDouble(totTranPrices / totweight, 5);

            listView= (ListView) findViewById(R.id.listView);
            textView = (TextView) this.findViewById(R.id.totweights);
            textView.setText(totweight.toString());

            textView = (TextView) this.findViewById(R.id.transfees);
            textView.setText(transfee.toString());

           // Log.v("tag",totTranPrices.toString());
            MyAdapter mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
            listView.setAdapter(mAdapter);//为ListView绑定Adapter

            bt = (Button) findViewById(R.id.returnbutton);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult( RESULT_OK, i);
                    finish();
                }
            });
            bt = (Button) findViewById(R.id.countbutton);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatecount();
                }
            });

        }



    }

    private void updatecount(){
        boolean ret=true;
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( CountActivity.this );
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();;
        HashMap<String, Object> map = new HashMap<String, Object>();
        Double transprices,rmb_singprice,singlesum,rmb_totprice;
        Integer number;
        Log.v("tag", String.valueOf( listData.size())+"个");
        for( int i=0; i< listData.size(); i++ ){
            map = new HashMap<String, Object>();
            map.put("toolname", listData.get(i).get("toolname").toString());
            map.put("sourcefrom", listData.get(i).get("sourcefrom").toString());
            map.put("trandate", listData.get(i).get("trandate").toString());
            transprices = roundDouble(transfee * Double.valueOf(listData.get(i).get("weight").toString()), 2);
            //Log.v("tag",transprices.toString());
            map.put("transprice", transprices);
            rmb_singprice = Double.valueOf(listData.get(i).get("rmb_singprice").toString());
           // Log.v("tag",rmb_singprice.toString());
            singlesum =roundDouble(rmb_singprice + transprices, 2);
            number = Integer.valueOf(listData.get(i).get("number").toString());
            map.put("singlesum", singlesum );
            map.put("rmb_totprice", roundDouble( singlesum * number, 2 )  );
            username = listData.get(i).get("user").toString();
            listItem.add(map);
        }
        ret = qd.updateCountDtl(username,listItem);
        if( ret == true ){
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("商品结算").
                    setMessage("商品结算已完成！").
                    setPositiveButton(
                            "确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    // 这里是你点击确定之后可以进行的操作
                                    dialog.cancel();
                                    setResult(RESULT_OK, i);
                                    finish();
                                }
                            }).create();
            alertDialog.show();
        }

    }

    /** 新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( CountActivity.this );
        ArrayList<HashMap<String, Object>> listItem;
        Double transprices ;

        /**构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
           // Log.v("tag",sourcefrom+" "+trandate );
            listData = qd.getCountDtl(username, sourcefrom, trandate);
           // Log.v("tag", String.valueOf( listItem.size()) +" "+ totweight.toString() );

            //Log.v("tag",transfee.toString());
        }

        @Override
        public int getCount() {
            return listData.size();//返回数组的长度
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
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.tlist ,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                //holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.toolname = (TextView) convertView.findViewById(R.id.toolname);
                holder.rmb_singprices = (TextView) convertView.findViewById(R.id.rmb_singprices);
                holder.transprices = (TextView) convertView.findViewById(R.id.transprices);
                holder.button_detail = (ImageButton) convertView.findViewById(R.id.button_detail);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.toolname.setText(listData.get(position).get("toolname").toString());
            holder.rmb_singprices.setText(listData.get(position).get("rmb_singprice").toString());
            transprices = roundDouble( transfee * Double.valueOf(listData.get(position).get("weight").toString()), 2 );
            //Log.v("tag",transprices.toString());
            holder.transprices.setText(transprices.toString());
            holder.button_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showInfo(position,listItem);
                }
            });

            return convertView;
        }

    }

    /**存放控件*/
    public final class ViewHolder{
        public TextView toolname;
        public TextView rmb_singprices;
        public TextView transprices;
        public ImageButton button_detail;
    }

    public void showInfo(int position,ArrayList<HashMap<String, Object>> listItem){

        Intent i=new Intent();
        i.setClass(getApplicationContext(), DetailActivity.class);
        Bundle b = new Bundle();
        b.putString("username",username);
        b.putString("toolname",listItem.get(position).get("toolname").toString());
        b.putString("sourcefrom",listItem.get(position).get("sourcefrom").toString());
        b.putString("trandate",listItem.get(position).get("trandate").toString());

        i.putExtras(b);

        startActivity(i);
    }

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
