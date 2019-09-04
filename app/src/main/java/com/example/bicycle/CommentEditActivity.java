package com.example.bicycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class CommentEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_edit);

        Button C_editButton=findViewById(R.id.C_editButton);
        final EditText C_edit=findViewById(R.id.C_edit);


        C_edit.setText(getIntent().getStringExtra("Text"));

        C_editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(C_edit.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"내용을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(),"수정완료",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("Text",C_edit.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }
        });



    }
}
