package com.example.bicycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText register_id,register_password,register_password2,register_name,register_Email;
    private Button register_registerButton;
    private CheckBox register_male,register_female;
    public static String codename;//로그인할때 구분지어주기위해
    private Button SameCheck,Emailcheck;
    private boolean samecheckValue=false;
    private boolean EValue=false;
    private String okID;// 중복검사 후 아이디를 다른걸로 변경시 중복되도 가입되는 것을 막기 위해
    private String okEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_id =findViewById(R.id.register_id);
        register_password=findViewById(R.id.register_password);
        register_password2=findViewById(R.id.register_password2);
        register_name=findViewById(R.id.register_name) ;
        register_registerButton=findViewById(R.id.register_registerButton);
        register_male=findViewById(R.id.register_male);
        register_female=findViewById(R.id.register_female);
        SameCheck=findViewById(R.id.samecheck);
        Emailcheck=findViewById(R.id.Emailcheck);
        register_Email=findViewById(R.id.register_Email);

        final SharedPreferences preferences = getSharedPreferences("RegisteredUser", MODE_PRIVATE);
        final SharedPreferences.Editor editor=preferences.edit();


            //아이디 중복검사 버튼//
        SameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register_id.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요!",Toast.LENGTH_SHORT).show();
                    samecheckValue=false;
                }
                //이 값이 이미 저장되어 있는지 확인
                if(register_id.getText().toString().equals(preferences.getString(register_id.getText().toString()+"ID",""))){
                    Toast.makeText(getApplicationContext(),"이미 가입된 아이디입니다",Toast.LENGTH_SHORT).show();
                   // register_id.setText(""); 아이디를 지우도록
                    samecheckValue=false;
                    return;
                }
               if(register_id.getText().toString().length()<6){
                    Toast.makeText(RegisterActivity.this, "아이디의 길이가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    samecheckValue=false;
                   return;
                }
                if(register_id.getText().toString().contains("@")){
                    Toast.makeText(RegisterActivity.this, "아이디에 해당 특수문자를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    samecheckValue=false;
                    return;
                }

              if(register_id.getText().toString().length()>15){
                    Toast.makeText(RegisterActivity.this, "아이디의 길이가 너무 깁니다.", Toast.LENGTH_SHORT).show();
                    samecheckValue=false;
                  return;
                }

                else{       // 아이디의 길이가 0이 아니고
                    Toast.makeText(getApplicationContext(),"가입이 가능한 아이디입니다.",Toast.LENGTH_SHORT).show();
                    okID=register_id.getText().toString();
                    samecheckValue=true;
                }
            }
        });

        //이메일 중복검사 버튼//
        Emailcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(register_Email.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Email을 입력하세요!",Toast.LENGTH_SHORT).show();
                    EValue=false;
                    return;
                }
                if (!checkEmail(register_Email.getText().toString())){
                    Toast.makeText(getApplicationContext(),"알맞은 이메일 형식을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                    EValue=false;
                    return;
                }
                //이 값이 이미 저장되어 있는지 확인
                if(register_Email.getText().toString().equals(preferences.getString(register_id.getText().toString()+"Email",""))){
                    Toast.makeText(getApplicationContext(),"이미 가입된 Email입니다.",Toast.LENGTH_SHORT).show();
                    // register_id.setText(""); 아이디를 지우도록
                    EValue=false;
                    return;
                }

                else{
                    Toast.makeText(getApplicationContext(),"가입이 가능한 이메일입니다.\n이메일은 비밀번호를 찾기 위해\n 사용됩니다.",Toast.LENGTH_SHORT).show();
                    okEmail=register_Email.getText().toString();
                    EValue=true;
                }
            }
        });



        //회원가입버튼 클릭 시
        register_registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(register_id.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this, "아이디를 입력하세요!", Toast.LENGTH_SHORT).show();
                    return; //리턴을 붙이면 if 조건문이 true일때 이벤트가 다음으로 넘어가지 않는다.
                }
                if(register_id.getText().toString().length()<6){
                    Toast.makeText(RegisterActivity.this, "아이디의 길이가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(register_id.getText().toString().contains("@")){
                    Toast.makeText(RegisterActivity.this, "아이디에 해당 특수문자를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(register_id.getText().toString().length()>15){
                    Toast.makeText(RegisterActivity.this, "아이디의 길이가 너무 깁니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(register_Email.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Email을 입력하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkEmail(register_Email.getText().toString())){
                    Toast.makeText(getApplicationContext(),"알맞은 이메일 형식을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                //이 값이 이미 저장되어 있는지 확인
                if(register_Email.getText().toString().equals(preferences.getString(register_id.getText().toString()+"Email",""))){
                    Toast.makeText(getApplicationContext(),"이미 가입된 Email입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(register_password.getText().toString().length()==0){
                    Toast.makeText(RegisterActivity.this,"비밀번호를 입력하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(register_password.getText().toString().length()<8){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 너무 짧습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(register_password2.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"비밀번호를 확인하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!register_password.getText().toString().equals(register_password2.getText().toString())){
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                    register_password.setText("");
                    register_password2.setText("");
                    return;

                }

                if(register_name.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"이름을 입력하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(register_male.isChecked()&&register_female.isChecked()){
                    Toast.makeText(getApplicationContext(),"하나의 성별만 선택하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!register_male.isChecked()&&!register_female.isChecked()){
                    Toast.makeText(getApplicationContext(),"성별을 선택하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!samecheckValue){
                    Toast.makeText(getApplicationContext(),"ID중복검사를 수행하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!okID.equals(register_id.getText().toString())){
                    Toast.makeText(getApplicationContext(),"ID중복검사를 수행하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!EValue){
                    Toast.makeText(getApplicationContext(),"Email중복검사를 수행하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!okEmail.equals(register_Email.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Email중복검사를 수행하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                codename=register_id.getText().toString();
                //입력한 값들 로그인창으로 보내기. SP에 대부분 저장되어 있어서 다른값까지 보낼 필요가 없다.
                Intent intent=new Intent();
                intent.putExtra("id",register_id.getText().toString());
                intent.putExtra("password",register_password.getText().toString());
                /*intent.putExtra("name",register_name.getText().toString());

                if(register_male.isChecked()){
                    intent.putExtra("sex",register_male.getText().toString());
                }
                else if(register_female.isChecked()){
                    intent.putExtra("sex",register_female.getText().toString());
                }*/



                //SP에 값저장, RegisterUser이란 폴더에 저장한다.
                //아이디로 각자의 아이디 비밀번호 이름 성별등 모든것을 구분.


                editor.putString(codename+"ID",register_id.getText().toString());
                editor.putString(codename+"Password",register_password.getText().toString());
                editor.putString(codename+"Name",register_name.getText().toString());
                editor.putString(codename+"Email",register_Email.getText().toString());


                String Sex="";
                if(register_male.isChecked()){
                    Sex=register_male.getText().toString();
                }
                else if(register_female.isChecked()){
                    Sex=register_female.getText().toString();
                }
                editor.putString(codename+"Sex",Sex);
                editor.apply();


                setResult(RESULT_OK,intent);
                Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }




}
