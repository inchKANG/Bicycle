package com.example.bicycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Shop_new extends AppCompatActivity {
    private EditText sn_title;
    private EditText sn_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_new);

        Button sn_ok=findViewById(R.id.sn_ok);

        sn_title=findViewById(R.id.sn_title);
        sn_contents=findViewById(R.id.sn_contents);


        //수정시 인텐트로 보낸 값을 받기 위해
        sn_title.setText(getIntent().getStringExtra("title"));
        sn_contents.setText(getIntent().getStringExtra("contents"));


        //확인 버튼 클릭 시 글 등록완료, 거래창으로 값을 보내줘야한다.
        sn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sn_title.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"제목을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sn_contents.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"내용을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                //값 보내기
                Intent intent=new Intent();
                intent.putExtra("title",sn_title.getText().toString());
                intent.putExtra("contents",sn_contents.getText().toString());

                //결과세팅
                setResult(RESULT_OK,intent);
                Toast.makeText(getApplicationContext(),"글 작성이 완료되었습니다",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
