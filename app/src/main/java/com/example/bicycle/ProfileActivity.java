package com.example.bicycle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.bicycle.MainHomeActivity1.Copylist;
import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private TextView Profile_id,Profile_sex,Profile_name,Profile_introduce,noTextProfile;
    private ImageView Profile_picture;

    public static ProfileActivity profileActivity=null;//다른 화면에서 꺼주기 위해

    RecyclerView ProfileRecyclerView=null; //
    SearchAdapter ProfileAdapter=null; //찾기 어댑터와 동일
    ArrayList<MainHomeItem> PList=new ArrayList<MainHomeItem>(); // 사용되는 아이템은 모두 동일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileActivity=this;//다른 화면에서 꺼주기 위해

        PList.addAll(Copylist);

        ///보낸 아이디와 코드를 받는다.
        String profileid=getIntent().getStringExtra("ID");
        String ProfileCode=getIntent().getStringExtra("Code");


        ///프로필 정보를 가져오기 위해서 유저 정보가 저장된 셰어드 생성
        preferences = getSharedPreferences("RegisteredUser", MODE_PRIVATE);
        editor=preferences.edit();


        Profile_id=findViewById(R.id.Profile_id);
        Profile_sex=findViewById(R.id.Profile_sex);
        Profile_name=findViewById(R.id.Profile_name);
        Profile_introduce=findViewById(R.id.Profile_introduce);
        Profile_picture=findViewById(R.id.Profile_picture);
        TextView ProfilenoText=findViewById(R.id.ProfilenoText);
        noTextProfile=findViewById(R.id.noTextProfile);
        ///값세팅
        Profile_id.setText(preferences.getString(profileid+"ID",""));
        Profile_sex.setText(preferences.getString(profileid+"Sex",""));
        Profile_name.setText(preferences.getString(profileid+"Name",""));
        Profile_introduce.setText(preferences.getString(profileid+"introduce",""));
        if(preferences.getString(profileid+"Image","").equals("")){}
        else{
            Profile_picture.setImageBitmap(StringToBitMap(preferences.getString(profileid+"Image","")));}//문자열 비트맵으로 변경해서 넣어주자.

        if(PList.size()==0){
            ProfilenoText.setVisibility(View.VISIBLE);
        }
        else{
            ProfilenoText.setVisibility(View.INVISIBLE);
        }

        //리사이클러뷰 세팅

        ProfileRecyclerView=findViewById(R.id.ProfileRecycler);
        ProfileAdapter=new SearchAdapter(PList);
        ProfileRecyclerView.setAdapter(ProfileAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        ProfileRecyclerView.setLayoutManager(mLayoutManager); //리사이클러뷰에 레이아웃 매니저 세팅

        ProfileAdapter.filterOnlyID(profileid);   //아이디로 글 필터링

        ProfileAdapter.setOnItemClickListener(new SearchAdapter.onSearchItemClickListener() {
            @Override
            public void onSearchItemClicked(MainHomeItem model) {

                //만약 기존에 모두보기가 켜진게 있다면 꺼준다.
                if(HomeAll.homeAll!=null){
                    HomeAll.homeAll.finish();
                }

                Intent intent=new Intent(getApplicationContext(),HomeAll.class);
                intent.putExtra("Code",model.getHomeCode());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        if(PList.size()==0){
            noTextProfile.setVisibility(View.VISIBLE);

        }


    }


    //String을 비트맵으로 변환해주는 함수
    public Bitmap StringToBitMap(String encodedString){

        try{

            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);

            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            return bitmap;

        }catch(Exception e){

            e.getMessage();

            return null;

        }

    }
}
