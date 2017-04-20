package com.example.administrator.poseidon;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.database.LoginDataBaseAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
;import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import com.example.administrator.database.QueryDataBaseAdapter;

public class InitActivity extends ActionBarActivity
        implements MenuListFragment.OnHeadlineSelectedListener{
    private String username;
    private String stat;
    private TextView editName=null;
    private SlidingMenu menu;
    private ListView listView;
    private ProgressDialog dialog;
    MyAdapter mAdapter;
    private PullToRefreshListView mPullRefreshListView;
    // SQLiteDatabase mDb;
    private ProgressDialog mProgressDialog;
    GetItemDateTask dTask=null;
    // 存储数据的数组列表
    ArrayList<HashMap<String, Object>> listData=null;
    // 适配器
    SimpleAdapter listItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);


        initActionBar();
        initSlidingMenu();
        initExtras();



       initListview();


    }



    public void initListview(){
       listView= (ListView) findViewById(R.id.listView);
      //  mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listView);
     //   mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);



        Log.v("tag","线程："+String.valueOf( Thread.currentThread().getId()));
        mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
       // listView.setAdapter(mAdapter);//为ListView绑定Adapter
        listView.setAdapter(mAdapter);
        dTask = new GetItemDateTask();
        try {
            listData = dTask.execute(username).get();
        } catch (Exception e) {
            listData=null;
        }
       // Log.v("tag",String.valueOf( Thread.currentThread().getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_init, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if ( id == android.R.id.home ){
            if( menu.isMenuShowing() )
                menu.showContent();
            else
                menu.showMenu();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if ( dTask != null && dTask.getStatus() == AsyncTask.Status.RUNNING ){
                dTask.cancel(true);
                dTask =null;
            }
            System.exit(0);
        }



        return super.onOptionsItemSelected(item);
    }


    private void initActionBar(){

/*

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_layout);

        View customView = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, new LinearLayout(this), false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setCustomView(customView);
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   if (!listView.isStackFromBottom()) {
                //       listView.setStackFromBottom(true);
                //     }
                //   listView.setStackFromBottom(false);
                Toast.makeText(InitActivity.this, "....", Toast.LENGTH_SHORT).show();
            }
        });*/
        listView= (ListView) findViewById(R.id.listView);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setHomeButtonEnabled(true);//actionbar��������Ա����
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//��ʾ�����ͼ��
        View customView = LayoutInflater.from(this).inflate(R.layout.actionbar_layout, new LinearLayout(this), false);
        //getSupportActionBar().setCustomView(LayoutInflater.from(this).inflate(R.layout.actionbar_layout, new LinearLayout(this), false) );
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(customView);
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listView.isStackFromBottom()) {
                    listView.setStackFromBottom(true);
                }
                listView.setStackFromBottom(false);
               // Toast.makeText(InitActivity.this, "....", Toast.LENGTH_SHORT).show();
            }
        });





    }
    private void initExtras(){

        editName = (TextView) findViewById(R.id.editName);
        Intent i = new Intent();
        i = this.getIntent();
        Bundle b = i.getExtras();
        username = b.getString("username");
        stat = b.getString("stat");
        //  Toast.makeText(InitActivity.this, "hello! "+username, Toast.LENGTH_SHORT).show();
        editName.setText(username);
    }
    /**
     * ��ʼ�������˵�
     */
    private void initSlidingMenu() {
        // ������������ͼ
    //    setContentView(R.layout.content_frame);
      //  getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MenuListFragment()).commit();

        // ���û����˵�������ֵ
         menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // ���û����˵�����ͼ����
        menu.setMenu(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuListFragment()).commit();



    }

    @Override
    public void onBackPressed() {
        //������ؼ�رջ����˵�
        if (menu.isMenuShowing()) {
            menu.showContent();
        } else {
            super.onBackPressed();
        }
    }



    /** 新建一个类继承BaseAdapter，实现视图与数据的绑定
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( InitActivity.this );
       // ArrayList<HashMap<String, Object>> listItem;

        /**构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
       //     listItem = qd.getToolDtlData(username);
        }

       @Override
        public int getCount() {
           int ret=0;
           if ( listData!=null ){
               ret = listData.size();
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
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.vlist ,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                //holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.toolname = (TextView) convertView.findViewById(R.id.toolname);
                holder.sourcefrom = (TextView) convertView.findViewById(R.id.sourcefrom);
                holder.singlesum = (TextView) convertView.findViewById(R.id.singlesum);
                holder.button_detail = (ImageButton) convertView.findViewById(R.id.button_detail);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.toolname.setText(listData.get(position).get("toolname").toString());
            holder.sourcefrom.setText(listData.get(position).get("sourcefrom").toString());
            holder.singlesum.setText(listData.get(position).get("singlesum").toString());
            if( !listData.get(position).get("sourcefrom").toString().equals("******")) {
                holder.button_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInfo(position, listData);
                    }
                });
            }

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
        b.putString("username",username);
        b.putString("toolname",listItem.get(position).get("toolname").toString());
        b.putString("sourcefrom",listItem.get(position).get("sourcefrom").toString());
        b.putString("trandate",listItem.get(position).get("trandate").toString());

        i.putExtras(b);

        startActivity(i);
    }

    public void onArticleSelected(int position) {
        // 用户选择了HeadlinesFragment中的头标题后
        Intent intent=new Intent();;
        Bundle b = new Bundle();
        b.putString("username", username);
        switch(position) {
            case 0:
                intent.setClass(getApplicationContext(), QryActivity.class);
                break;
            case 1:
                intent.setClass(getApplicationContext(), AddActivity.class);
                break;
            case 2:
                intent.setClass(getApplicationContext(), ModiActivity.class);
                break;
            case 3:
                b.putString("flag", "init");
                intent.setClass(getApplicationContext(), CountActivity.class);;
                break;
            case 4:
                intent.setClass(getApplicationContext(), QryOrderActivity.class);
                break;
            case 5:
                b.putString("flag", "init");
                b.putString("ordername", "");
                b.putString("orderdate", "");
                intent.setClass(getApplicationContext(), AddOrderActivity.class);
                break;
            case 6:
                intent.setClass(getApplicationContext(), ModiOrderActivity.class);
                break;
            case 7:
                b.putString("flag", "init");
                b.putString("ordername", "");
                b.putString("orderdate", "");
                intent.setClass(getApplicationContext(), CountOrderActivity.class);
                break;
        }
        intent.putExtras(b);
        startActivityForResult(intent, 0);
        menu.showContent();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                initListview();
                break;
            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                mProgressDialog = new ProgressDialog(InitActivity.this);
                mProgressDialog.setMessage("正在获取数据");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return mProgressDialog;

        }
        return null;
    }
    class GetItemDateTask extends AsyncTask< String ,  Integer,  ArrayList<HashMap<String, Object>>> {//获取图片仍采用AsyncTask，这里的优化放到下篇再讨论

        String name;
        ArrayList<HashMap<String, Object>> listItem;
        QueryDataBaseAdapter qd =new QueryDataBaseAdapter( InitActivity.this );

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
            listItem = qd.getToolDtlData(params[0]);
            Log.v("tag","线程："+ String.valueOf(Thread.currentThread().getId()));
            return listItem;
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
