package com.example.bicycle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class zzz extends AppCompatActivity {

    private ImageButton home,search,mychoice,setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zzz);





        home=findViewById(R.id.home4);
        search=findViewById(R.id.search4);
        mychoice=findViewById(R.id.mychoice4);
        setting=findViewById(R.id.setting4);

        //메뉴 눌렀을 시 이동
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), UserActivity1.class);
                startActivity(intent);
            }
        });



        Button mailButton=findViewById(R.id.mailButton);
        Button homepageButton=findViewById(R.id.homepageButton);
        Button youtubeButton=findViewById(R.id.youtubeButton);
        Button urlimage=findViewById(R.id.urlimage);


        urlimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("geo:37.484910, 126.970673");
                Intent it = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);


            }
        });



        //암시적 인텐트로 메일보내기
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:dlscjfrkd12@gmail.com"));
                startActivity(intent);

            }
        });

        ///암시적 인텐트로 홈페이지 접속
        homepageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.naver.com/"));
                startActivity(intent);
            }
        });

        //유튜브접속


        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/results?search_query=%EC%B7%A8%EB%AF%B8"));
                startActivity(intent);
            }
        });


    }
}
