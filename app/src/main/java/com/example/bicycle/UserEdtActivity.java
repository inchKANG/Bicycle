package com.example.bicycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserEdtActivity extends AppCompatActivity {
   Button edt_ok;
   EditText edt_edt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edt);

        edt_edt=findViewById(R.id.edt_edt);
        edt_ok=findViewById(R.id.edt_ok);

        //main에서 소개 보낸값 받기
        edt_edt.setText(getIntent().getStringExtra("introduce"));

        //확인버튼 누룰 시 정보가 메인으로 가도록!
        edt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(), UserActivity1.class);
                intent.putExtra("introduceok",edt_edt.getText().toString());//edt안에있는값을 인트로듀스 이름값으로 보낸다.
                setResult(RESULT_OK,intent);
                Toast.makeText(getApplicationContext(),"수정완료",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }





}
