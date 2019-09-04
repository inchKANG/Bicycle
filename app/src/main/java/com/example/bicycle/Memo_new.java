package com.example.bicycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Memo_new extends AppCompatActivity {

    EditText nmemo_title,nmeme_text;
    Button nmemo_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_new);

        nmemo_title=findViewById(R.id.nmemo_title);
        nmeme_text=findViewById(R.id.nmemo_text);
        nmemo_save=findViewById(R.id.nmemo_save);

        //수정시 인텐트로 보낸 값을 받기 위해
        nmemo_title.setText(getIntent().getStringExtra("title"));
        nmeme_text.setText(getIntent().getStringExtra("text"));


        nmemo_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nmemo_title.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"제목을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nmeme_text.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"내용이 너무 짧습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent();
                intent.putExtra("title",nmemo_title.getText().toString());
                intent.putExtra("text",nmeme_text.getText().toString());

                setResult(RESULT_OK,intent);
                finish();
            }
        });



    }


}
