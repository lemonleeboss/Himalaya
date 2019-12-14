package com.example.a76780.himalaya.admins;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a76780.himalaya.DataBaseHelpers.Databasehelper;
import com.example.a76780.himalaya.R;

public class Reg extends AppCompatActivity implements View.OnClickListener{

    EditText reg_user_name,reg_user_pwd;
    String reg_username,reg_pwd;
    Button btn_reg2,btn_reset;
    Databasehelper helper;
    SQLiteDatabase database;
    TextView tv_back,tv_main_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        reg_user_name=(EditText)findViewById(R.id.reg_user_name);
        reg_user_pwd=(EditText)findViewById(R.id.reg_user_pwd);
        btn_reg2=(Button)findViewById(R.id.btn_reg2);
        btn_reset=(Button)findViewById(R.id.btn_reset);
        helper=new Databasehelper(this);
        database=helper.getReadableDatabase();
        tv_main_title= (TextView) findViewById(R.id.tv_main_title);
        tv_back= (TextView) findViewById(R.id.tv_back);
        //返回键的点击事件
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录界面销毁
                Reg.this.finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reg2:
                reg_username=reg_user_name.getText().toString();
                reg_pwd=reg_user_pwd.getText().toString();
                add(reg_username,reg_pwd);
                Toast.makeText(this, "恭喜你注册成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,login.class);
                intent.putExtra("username",reg_username);
                intent.putExtra("pwd",reg_pwd);
                startActivity(intent);
                break;
            case R.id.btn_reset:
                reg_user_name.setText("");
                reg_user_pwd.setText("");
                Toast.makeText(this, "恭喜你重置成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void add(String username,String pwd){
        ContentValues values=new ContentValues();
        values.put("username",username);
        values.put("pwd",pwd);
        try {
            database.insert("users",null,values);
        }catch (Exception e){
            Log.v("error",e.getMessage());
        }
    }
}
