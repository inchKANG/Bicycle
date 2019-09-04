package com.example.bicycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

            Handler hand = new Handler();

            hand.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences sf=getSharedPreferences("AutoLogin",0);
                    //만약 로고 액티비티에서 셰어드에 저장된 아이디값을 가져왔는데 그 길이가 0이 아니라면 로그인되게!
                    //그리고 메인에 아이디, 성별, 비밀번호, 이름 값은 다 전달, 인텐트로.
                    if(sf.getBoolean("autoLogin",false)){
                        Intent intent=new Intent(getApplicationContext(), MainHomeActivity1.class);
                        intent.putExtra("id",sf.getString("id",""));
                        intent.putExtra("password",sf.getString("password",""));
                        intent.putExtra("sex",sf.getString("sex",""));
                        intent.putExtra("name",sf.getString("name",""));

                        Toast.makeText(getApplicationContext(),"자동로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                        //화면전환하기
                        startActivity(intent);
                        finish();
                    }
                    else{
                    Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();}
                }
            }, 3000);//2초동안 로고 띄우기

    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onRestart() {

        super.onRestart();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
