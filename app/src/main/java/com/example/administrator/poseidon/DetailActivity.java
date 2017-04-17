package com.example.administrator.poseidon;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.database.QueryDataBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends ActionBarActivity {

    private String username;
    private String toolname;
    private String sourcefrom;
    private String trandate;
    private TextView editName=null;
    ArrayList<HashMap<String, Object>> listData;
    QueryDataBaseAdapter queryDataBaseAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        initActionBar();
        initExtras();
        selectDtl();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
     //   Log.v("tag",String.valueOf(id));
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if ( id == android.R.id.home  ){
            //Intent i=new Intent();
            //i.setClass(getApplicationContext(), InitActivity.class);
            //startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initExtras(){

        editName = (TextView) findViewById(R.id.editName);
        Intent i = new Intent();
        i = this.getIntent();
        Bundle b = i.getExtras();
        username = b.getString("username");
        toolname = b.getString("toolname");
        sourcefrom = b.getString("sourcefrom");
        trandate= b.getString("trandate");
        //  Toast.makeText(DetailActivity.this, username+toolname+sourcefrom+trandate, Toast.LENGTH_SHORT).show();
        editName.setText(username);
    }

    private void initActionBar(){

        getSupportActionBar().setHomeButtonEnabled(true);//actionbar��������Ա����
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//��ʾ�����ͼ��

    }

    private  void selectDtl(){

        Button bt;
        TextView textView=null;
        queryDataBaseAdapter = new QueryDataBaseAdapter(this);
        listData = queryDataBaseAdapter.getToolDtlDtl( username,toolname,sourcefrom,trandate );
      //  Log.v("tag", listData.get(0).get("exchangerate").toString());
        textView = (TextView) findViewById(R.id.toolname);
        textView.setText(toolname);

        textView = (TextView) findViewById(R.id.sourcefrom);
        textView.setText(sourcefrom);

        textView = (TextView) findViewById(R.id.trandate);
        textView.setText(trandate);

        textView = (TextView) findViewById(R.id.currenttype);
        textView.setText(listData.get(0).get("currency").toString());

        textView = (TextView) findViewById(R.id.number);
        textView.setText(listData.get(0).get("number").toString());

        textView = (TextView) findViewById(R.id.soldnumber);
        textView.setText(listData.get(0).get("soldnumber").toString());

        textView = (TextView) findViewById(R.id.totprices);
        textView.setText(listData.get(0).get("totprice").toString());

        textView = (TextView) findViewById(R.id.feepercents);
        textView.setText(listData.get(0).get("feepercent").toString());

        textView = (TextView) findViewById(R.id.feeprices);
        textView.setText(listData.get(0).get("feeprice").toString());

        textView = (TextView) findViewById(R.id.exchangerates);
        textView.setText(listData.get(0).get("exchangerate").toString());

        textView = (TextView) findViewById(R.id.rmb_totprices);
        textView.setText(listData.get(0).get("rmb_totprice").toString());

        textView = (TextView) findViewById(R.id.rmb_singprices);
        textView.setText(listData.get(0).get("rmb_singprice").toString());

        textView = (TextView) findViewById(R.id.transprices);
        textView.setText(listData.get(0).get("transprice").toString());

        textView = (TextView) findViewById(R.id.singlesums);
        textView.setText(listData.get(0).get("singlesum").toString());

        textView = (TextView) findViewById(R.id.weights);
        textView.setText(listData.get(0).get("weight").toString());
        textView = (TextView) findViewById(R.id.totweights);
        textView.setText(listData.get(0).get("totweight").toString());
        bt = (Button) findViewById(R.id.backbutton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
        }
        });


    }


}
