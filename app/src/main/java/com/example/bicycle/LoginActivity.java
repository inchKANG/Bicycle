package com.example.bicycle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.net.PasswordAuthentication;
import java.util.Properties;

public class LoginActivity extends AppCompatActivity {

   private TextInputEditText login_id, login_password;
   private Button login_loginButton,login_findpassword,login_register;
   private static final int REQUEST_CODE=1234;//상수값 선언
   private ImageView login_image;
   private String login_sex,login_name;
   private SignInButton login_google;
   private CheckBox autoLogin;
   private Boolean loginChecked=false;

   SharedPreferences pre;//회원가입때 저장된 정보를 불러오기 위함
    SharedPreferences.Editor edt;

    SharedPreferences sf;//자동로그인에 저장된 정보
    SharedPreferences.Editor editor;


    testTask task;


    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //값을 가져오는 과정
        login_id=findViewById(R.id.login_id);
        login_password=findViewById(R.id.login_password);
        login_loginButton=findViewById(R.id.login_loginButton);
        login_google=findViewById(R.id.login_google);
        login_findpassword=findViewById(R.id.login_findid); // 현재 구현불가
        login_register=findViewById(R.id.login_register);
        login_image=findViewById(R.id.login_image);
        autoLogin=findViewById(R.id.chk_auto);//오토로그인 저장

       pre=getSharedPreferences("RegisteredUser",0);   //회원가입때 저장된 정보를 불러오기 위함
       edt=pre.edit();

       sf=getSharedPreferences("AutoLogin",0); //자동로그인에 저장된 정보
        editor=sf.edit();
        //구글로그인




        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              switch (view.getId()){
                  case R.id.login_google:
                      Log.e("구글로그인", "구글!" );
                        signIn();
                        break;
              }
            }
        });


        //아이디찾기
        login_findpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Intent intent=new Intent(getApplicationContext(),findid.class);
              startActivity(intent);


            }
        });





       ///자동로그인 버튼을 클릭한다면 로그인을 체크하는 불린변수에 true로 저장, 클릭되어있지 않다면 저장하지 않는다.
       autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               if(isChecked){
                  loginChecked=true;// 자동로그인 체크가 되어있다면 loginChecked는 트루;
                  }

               else{
                   loginChecked=false; //체크가 안되어 있다면 false이다.
                   /*editor.clear();
                   editor.commit();*/ }
           }
       });




        //1.값을 가져온다 -//조건검사도 나중에 공부하자 비밀번호 아이디 동일할 시만 로그인.
        //2.클릭을 감지한다.
        //3.클릭의 값을 다음 액티비티로 넘긴다.

        //메인화면 이동
        login_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(login_id.length()==0&&login_password.length()==0){
                    Toast.makeText(getApplicationContext(),"아무것도 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(login_id.length()==0){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(login_password.length()==0){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(login_id.getText().toString().equals(pre.getString(login_id.getText().toString()+"ID",""))  //아이디가 이미 저장된 아이디 이고
                &&login_password.getText().toString().equals(pre.getString(login_id.getText().toString()+"Password",""))) //그 아이디에 저장된 패스워드가 입력된 패스워드와 같다면.
                { Toast.makeText(getApplicationContext(),"로그인성공",Toast.LENGTH_SHORT).show();}

                else{
                    Toast.makeText(getApplicationContext(),"아이디와 패스워드를 확인하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }





                //만약 로그인 체크값이 트루라면 로그인 버튼 클릭 시 회원가입시 입력한 모든값을 저장
                //자동로그인이 있어도 처음에는 로그인버튼을 눌러야 로그인이 되니깐 이때 저장해도 된다.
                if(loginChecked) {
                    // if autoLogin Checked, save values
                    editor.clear();
                    editor.putString("id", login_id.getText().toString());
                    editor.putString("pw", login_password.getText().toString());
                    editor.putString("sex",login_sex);
                    editor.putString("name",login_name);
                    editor.putBoolean("autoLogin", true);
                    editor.apply();
                }
                else{
                    editor.clear();
                    editor.putString("id", login_id.getText().toString());
                    editor.putString("pw", login_password.getText().toString());
                    editor.putString("sex",login_sex);
                    editor.putString("name",login_name);
                    editor.putBoolean("autoLogin", false);
                    editor.apply();
                }



                Intent intent=new Intent(LoginActivity.this, MainHomeActivity1.class);

                //인텐트값저장
                intent.putExtra("id",login_id.getText().toString());
                intent.putExtra("password",login_password.getText().toString());


                Toast.makeText(getApplicationContext(),"로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                //화면전환하기
                startActivity(intent);
                if(loginChecked){
                    finish();
                }
            }
        });

        //회원가입 창으로 이동
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }



    //회원가입 결과를 받는 창
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);


    if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        login_id.setText(data.getStringExtra("id"));
        login_password.setText(data.getStringExtra("password"));
        login_name=data.getStringExtra("name");
        login_sex=data.getStringExtra("sex");


    }

    if (requestCode == RC_SIGN_IN) { //통신이 제대로 되었으면 결과를 받는다. 여기서 HandleSignResult를 통해!

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        handleSignInResult(task);
    }

}

    private void updateImage(){


    }



    class testTask extends AsyncTask<String, Integer, Integer> {    //현재 여기에 넣는 값의 의미가 없다.

        int value=0;

        @Override   //자동실행, 쓰레드안에 넣는 코드 여기에
        protected Integer doInBackground(String... strings) {
            while (true){

            //    publishProgress(value);
                value++;
                publishProgress(value); //이부분이 있어야 UI와 연결시키지만 아직은 했갈리는 부분이 있다. AsyncTask 복습하자.
                try {
                    Thread.sleep(3000); //3초동안 지연시켜준다. 이미지를 3초마다 변경
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
              //  Log.e("value", String.valueOf(value));

                if(isCancelled()){      //백그라운드에서 도는 쓰레드 부분은 앱을 종료해도 죽지 않는 경우가 발생. 이를 막으려면 캔슬되었을시 브레이크!
                                        //나중에 지도할때는 종료되도 살아있는 AsyncTask로 해보자.
                    break;
                }
            }

            return null;

        }

        @Override   //UI업데이트 해주는 부분
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            int mod = value % 5;//값이 증가할때마다 이미지가 변경된다.

            switch (mod) {
                case 0:
                    login_image.setImageResource(R.drawable.bi1);
                    break;
                case 1:
                    login_image.setImageResource(R.drawable.bi2);
                    break;
                case 2:
                    login_image.setImageResource(R.drawable.bi3);
                    break;
                case 3:
                    login_image.setImageResource(R.drawable.bi4);
                    break;
                case 4:
                    login_image.setImageResource(R.drawable.bi5);
                    break;
            }



        }


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {//TODO handleSignResult
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            ///구글아이디에서 받아온 정보 꺼냄
           String email=account.getEmail();//이메일
           String name=account.getDisplayName();//이름
            String ID=account.getId();//아이디-번호값이니 그냥 페스워드로 사용
            Log.e("handle", "handleSignInResult: "+email);
            Log.e("handle", "handleSignInResult: "+name);
            Log.e("handle", "handleSignInResult: "+ID);

               //회원가입된 정보에 저장할 정보
            edt.putString(email+"ID",email);//아이디는 이메일
            edt.putString(email+"Password",ID);//비밀번호는 아이디(가져오는 아이디는 번호값)
            edt.putString(email+"Name",name);//이름은 이름

            edt.apply();


            if(loginChecked) {//로그인된 정보에 저장할 정보
                // if autoLogin Checked, save values
                editor.clear();
                editor.putString("id", email);
                editor.putString("pw", ID);
                editor.putString("name",name);
                editor.putBoolean("autoLogin", true);
                editor.apply();
            }
            else{
                editor.clear();
                editor.putString("id", email);
                editor.putString("pw", ID);
                editor.putString("name",name);
                editor.putBoolean("autoLogin", false);
                editor.apply();
            }

            Intent intent=new Intent(LoginActivity.this, MainHomeActivity1.class);

            Toast.makeText(getApplicationContext(),"로그인 되었습니다.",Toast.LENGTH_SHORT).show();
            //화면전환하기
            startActivity(intent);
            if(loginChecked){
                finish();
            }




        } catch (ApiException e) {
            Log.d("API오류", "handleSignResult오류" );

        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        task=new testTask();
        task.execute();

     GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
     if(account!=null){//만약 이전에 구글로그인했던 아이디가 있다면 그정보로 바로되지않게,(signin 정보 지우기)
         signOut();
     }


    }



    @Override
    protected void onPause() {
        super.onPause();

        task.cancel(true); /*AsyncTask를 종료시켜도 Background쓰레드는 계속 돈다. 그렇다면 이를 해결하려면 어떻게 해야하는가
                                                 doinBackground부분에서 메소드 종료시킬 수 있도록 추가시키자!*/

        }



}
