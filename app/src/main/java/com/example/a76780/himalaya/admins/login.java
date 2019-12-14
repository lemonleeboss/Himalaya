package com.example.a76780.himalaya.admins;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a76780.himalaya.DataBaseHelpers.Databasehelper;
import com.example.a76780.himalaya.DetailActivity;
import com.example.a76780.himalaya.MainActivity;
import com.example.a76780.himalaya.R;
import com.example.a76780.himalaya.fragments.HistoryFragment;
import com.example.a76780.himalaya.fragments.RecommendFragment;
import com.example.a76780.himalaya.fragments.SubscriptionFragment;

public class login extends Activity implements View.OnClickListener {
    Button btn_login;
    TextView btn_reg,tv_back;
    EditText logname,logpwd;
    Databasehelper helper;
    SQLiteDatabase database;
    String username,pwd;
    TextView tv_main_title;//标题
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        btn_login.setOnClickListener(this);
        btn_reg.setOnClickListener(this);
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        pwd=intent.getStringExtra("pwd");
        logname.setText(username);
        logpwd.setText(pwd);
    }
    public void bindViews(){
        btn_login=findViewById(R.id.btn_login);
        btn_reg=findViewById(R.id.btn_reg);
        logname=findViewById(R.id.logname);
        logpwd=findViewById(R.id.logpwd);
        tv_main_title=findViewById(R.id.tv_main_title);
        tv_back=findViewById(R.id.tv_back);
        //返回键的点击事件
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录界面销毁
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }



    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                String u=logname.getText().toString();
                String p=logpwd.getText().toString();
                share(u,p);
                break;
            case R.id.btn_reg:
                Intent intent1=new Intent(login.this,Reg.class);
                startActivity(intent1);
                break;
        }
    }
    public void share(String username1,String pwd1){
        helper=new Databasehelper(this);
        database=helper.getReadableDatabase();

        Cursor cursor =  database.rawQuery("SELECT pwd FROM users WHERE username = ?",
                new String[]{username1});
        String userpwd=null;
        if(cursor!=null && cursor.moveToFirst())
        {
            userpwd = cursor.getString(cursor.getColumnIndex("pwd"));
            if(userpwd.equals(pwd1)){
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                SharedPreferences m=getSharedPreferences("user",MODE_PRIVATE);
                SharedPreferences.Editor editor=m.edit();
                editor.putString("username",username1);
                editor.putString("pwd",pwd1);
                editor.apply();
                Intent intent=new Intent(login.this, HistoryFragment.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "用户名和密码不匹配", Toast.LENGTH_SHORT).show();
        }
        cursor.close();

    }

}