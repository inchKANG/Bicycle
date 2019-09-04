package com.example.bicycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class findid extends AppCompatActivity {

    EditText findid,findEmail;
    Button findPasswordButton;

    SharedPreferences pre;//회원가입때 저장된 정보를 불러오기 위함
    SharedPreferences.Editor edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findid);

       findid=findViewById(R.id.findid);
       findEmail=findViewById(R.id.findEmail);
       findPasswordButton=findViewById(R.id.findpasswordButton);

        pre=getSharedPreferences("RegisteredUser",0);   //회원가입때 저장된 정보를 불러오기 위함
        edt=pre.edit();


       findPasswordButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (findid.getText().toString().length()==0)//길이가0
               {
                   Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                   return;
               }

               if (!findid.getText().toString().equals(pre.getString(findid.getText().toString() + "ID", "")))//만약 저장시 입력한 아이디가 아니라면?
               {
                   Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                   return;
               }
               if (findEmail.getText().toString().length()==0)//길이가0
               {
                   Toast.makeText(getApplicationContext(), "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                   return;
               }
               if (!findEmail.getText().toString().equals(pre.getString(findid.getText().toString() + "Email", "")))//만약 저장시 입력한 이메일이 아니라면?
               {
                   Toast.makeText(getApplicationContext(), "이메일이 틀립니다.", Toast.LENGTH_SHORT).show();
                   return;
               } else {
                   Toast.makeText(getApplicationContext(), "이메일로 패스워드를 전송합니다.", Toast.LENGTH_SHORT).show();

                   GMailSender sender = new GMailSender("", ""); // SUBSTITUTE HERE //이메일패스워드 입력
                   try {
                       Log.e("SendMail", "보냄 ㅎㅎ");
                       sender.sendMail(
                               findid.getText().toString()+"님 비밀번호 찾기 결과입니다.",//제목
                               findid.getText().toString()+"님의 비밀번호는 "+pre.getString(findid.getText().toString() + "Password", "")+"입니다.",//내용
                               "dlscjfrkd12@gmail.com",//보낸이
                              findEmail.getText().toString()//받는이

                       );//TODO 이메일
                   } catch (Exception e) {
                       Log.e("SendMail", e.getMessage(), e);
                   }
                   finish();
               }
           }
       });



    }
}
