package com.example.bicycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class UserSetting extends AppCompatActivity {

    private Button checkButton,ChangeButton;
    private EditText befP,befPCheck,aftP,aftPCheck;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LinearLayout nowLayout;
    LinearLayout changeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //패스워드 수정에 필요한 버튼, EDT초기화
        setContentView(R.layout.activity_user_setting);
        checkButton=findViewById(R.id.befPasswordCheckButton);
        ChangeButton=findViewById(R.id.PasswordChangeButton);
        befP=findViewById(R.id.befPassword);
        befPCheck=findViewById(R.id.befPasswordCheck);
        aftP=findViewById(R.id.aftPassword);
        aftPCheck=findViewById(R.id.aftPasswordCheck);

       nowLayout=findViewById(R.id.nowlayout);
        changeLayout=findViewById(R.id.changelayout);

        nowLayout.setVisibility(View.VISIBLE);
        changeLayout.setVisibility(View.GONE);


        preferences = getSharedPreferences("RegisteredUser", MODE_PRIVATE);
        editor=preferences.edit();//가입 아이디 정보를 가져올 셰어드 생성


            //기존 비밀번호 확인
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(befP.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!befP.getText().toString().equals(preferences.getString(DivisionID+"Password",""))){
                    Toast.makeText(getApplicationContext(),"기존 비밀번호와 입력 비밀번호가 다릅니다",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(befPCheck.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"비밀번호 확인을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!befPCheck.getText().toString().equals(befP.getText().toString())){
                    Toast.makeText(getApplicationContext(),"비밀번호 확인을 잘못 입력하셨습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Toast.makeText(getApplicationContext(),"확인이 완료되었습니다. 변경할 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();

                    nowLayout.setVisibility(View.GONE);
                    changeLayout.setVisibility(View.VISIBLE);

                }
             //   aftP,aftPCheck;
                ChangeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(aftP.getText().toString().length()==0){
                            Toast.makeText(getApplicationContext(),"변경할 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(aftP.getText().toString().length()<8){
                            Toast.makeText(getApplicationContext(),"8자리 이상의 비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(aftPCheck.getText().toString().length()==0){
                            Toast.makeText(getApplicationContext(),"변경할 비밀번호 확인을 입력하세요",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!aftP.getText().toString().equals(aftPCheck.getText().toString())){
                            Toast.makeText(getApplicationContext(),"변경할 비밀번호 확인을 잘못 입력하셨습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"비밀번호가 변경되었습니다. 다시 로그인해 주세요.",Toast.LENGTH_SHORT).show();
                            editor.putString(DivisionID+"Password",aftP.getText().toString());
                            editor.apply();

                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                            SharedPreferences sf=getSharedPreferences("AutoLogin",0);
                            SharedPreferences.Editor ed=sf.edit();
                            ed.clear();
                            ed.apply();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); ///현재 실행중인 모든 액티비티 종료 후 새로운 액티비티 하나 띄운다.
                            startActivity(intent);

                        }


                    }
                });


            }
        });






    }
}
