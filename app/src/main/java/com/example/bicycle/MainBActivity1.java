package com.example.bicycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class MainBActivity1 extends AppCompatActivity {
    private ImageView homeButton, searchButton, bicycleButton, userButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_b);


        homeButton=findViewById(R.id.homeButton);
        searchButton=findViewById(R.id.searchButton);
        userButton=findViewById(R.id.userButton);

        //////////////////////////////////자전거관련 내에서 버튼 클릭시 이동/////////////////////
        //중고상점
        Button shopbutton=findViewById(R.id.shopbutton);

        shopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainB_Shop.class);
                startActivity(intent);
            }
        });
        //일정메모
        Button memobutton=findViewById(R.id.Memo_button);

        memobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), MainB_Memo.class);
                startActivity(intent);


            }
        });

        //자전거타기
        Button Exercise_Button=findViewById(R.id.Exercise_Button);

        Exercise_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainB_Riding.class);
                startActivity(intent);
            }
        });

        //주변 수리점 확인

        Button BicycleShop_Button=findViewById(R.id.BicycleShop_Button);

        BicycleShop_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MapEXAM.class);
                startActivity(intent);
            }
        });


//////////////////////////이동메뉴//////////////////////////
        //홈버튼 클릭 시 이동
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainHomeActivity1.class);
                startActivity(intent);
                finish();
            }
        });
        //돋보기 클릭 시 이동
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainSearchActivity1.class);
                startActivity(intent);
                finish();
            }
        });

        //유저버튼 클릭시 이동
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity1.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
