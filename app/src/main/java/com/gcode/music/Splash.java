package com.gcode.music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.Objects;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        Objects.requireNonNull(getSupportActionBar()).hide();//隐藏标题栏
        setContentView(R.layout.activity_splash);
        //创建子线程
        Thread myThread= new Thread(() -> {
            try{
                Thread.sleep(1000);//使程序休眠五秒
                Intent it=new Intent(getApplicationContext(),MainActivity.class);//启动MainActivity
                startActivity(it);
                finish();//关闭当前活动
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        myThread.start();//启动线程
    }
}