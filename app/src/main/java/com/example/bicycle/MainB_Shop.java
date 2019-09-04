package com.example.bicycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicycle.MemoAdapter.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.example.bicycle.MainHomeActivity1.ID;
import static com.example.bicycle.MainHomeActivity1.NAME;

//중고거래 액티비티 리사이클러뷰 포함이기에 ShopData, ShopAdapter과 하나의 묶음이다./
public class MainB_Shop extends AppCompatActivity {

    RecyclerView shoprecyclerview=null;
    ShopAdapter mshopAdapter = null;
    ArrayList<ShopData> shopDatalist = new ArrayList<ShopData>();  //액티비티의 데이터 리스트는 원본 데이터를 어댑터로 넘겨주고 어댑터의 데이터리스트는 이를 매칭시켜서 만든다.

    private static final int Request_shopbutton = 12345;

    int pos; //포지션값 받기 위해
    private static final String TAG = "로그";   // 처음 Tag객체생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_b__shop);

        loadData();

        //리사이클러뷰에 ShopAdapter 객체 지정
        shoprecyclerview = findViewById(R.id.shop_recycler);
        mshopAdapter = new ShopAdapter(shopDatalist);// ShopAdapter를 생성하고
        shoprecyclerview.setAdapter(mshopAdapter);// 리사이클러뷰에 셋팅한다. 어뎁터가 있어야 리사이클러뷰에 각각의 아이템 표시

        //리사이클러뷰에 리니어 레이아웃 객체 지정
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);


        shoprecyclerview.setLayoutManager(mLayoutManager); //수직방향 리사이클러뷰
        Button shop_new = findViewById(R.id.shop_new);
        Button shop_search=findViewById(R.id.shop_find);


  //      글쓰기 버튼 클릭시 작성창으로 이동.
  //      값을 다시 받아와야 하기에 startActivityforResult로 받는다.
        shop_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Shop_new.class);
                startActivityForResult(intent, Request_shopbutton);

            }
        });
/////나중에 더 추가하자,
        mshopAdapter.setOnItemClickListener(new ShopAdapter.OnItemClickListener() {
            @Override
            public Void onItemClick(View v, int position) {
                Intent intent=new Intent(getApplicationContext(),Shop_new.class);
                pos=position;

                intent.putExtra("title",shopDatalist.get(position).getShop_title().toString());
                intent.putExtra("contents",shopDatalist.get(position).getShop_contents().toString());
                startActivityForResult(intent,1122);


                return null;
            }
        });


    }











    //아이템을 추가하는 메소드, 더미데이터 만들때 이용하자.
    //스토어 아이템 객체 생성, 제목과 내용 설정 후 이를 리스트에 추가해주는 메소드.
    public void additem(String title, String contents, String date, String user) {
        ShopData item = new ShopData();
        item.setShop_title(title);
        item.setShop_contents(contents);
        item.setShop_date(date);
        item.setShop_user(user);

        shopDatalist.add(item);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //현재 시간을 나타내는 방법
        long now = System.currentTimeMillis ();
        Date date=new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH시 mm분");
        String getTime = sdf.format(date);

            //글쓰기 완료값을 받는!
        if (requestCode == Request_shopbutton && resultCode == RESULT_OK) {

            Toast.makeText(getApplicationContext(), "글쓰기를 완료했습니다!", Toast.LENGTH_SHORT).show();

            ShopData item = new ShopData();
            item.setShop_title(data.getStringExtra("title"));
            item.setShop_contents(data.getStringExtra("contents"));
            item.setShop_date(getTime); // 현재시간을 받아서 올린다.
            item.setShop_user(ID);//
            // additem("안녕", "안녕하세요 강인철 입니다.", "20190802", "user1");

            shopDatalist.add(item);
            mshopAdapter.notifyDataSetChanged();//이걸 해주지 않으면 새롭게 갱신하지 않는다. 삽질말자

        }
        ////수정시 수정값을 받는
        if(requestCode==1122&&resultCode==RESULT_OK){
            ShopData item=new ShopData();
            item.setShop_title(data.getStringExtra("title"));
            item.setShop_contents(data.getStringExtra("contents"));
            item.setShop_date(getTime);
            item.setShop_user(NAME);
            Toast.makeText(getApplicationContext(),"수정완료!",Toast.LENGTH_SHORT).show();
            shopDatalist.set(pos,item);
            mshopAdapter.notifyDataSetChanged();

        }
    }

    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를 memoshared, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("shopData", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();


        ///제목, 내용, 날짜, 유저이름을 담을 리스트를 새로 만든다. 그리고 각 요소를 담아준다.
        ArrayList<String> title=new ArrayList<String>();
        for(int i =0; i<shopDatalist.size();i++){
            title.add(shopDatalist.get(i).getShop_title());}

        ArrayList<String> contents=new ArrayList<String>();
        for(int i =0; i<shopDatalist.size();i++){
            contents.add(shopDatalist.get(i).getShop_contents());}

        ArrayList<String> date=new ArrayList<String>();
        for(int i =0; i<shopDatalist.size();i++){
            date.add(shopDatalist.get(i).getShop_date());}

        ArrayList<String> user=new ArrayList<String>();
        for(int i =0; i<shopDatalist.size();i++){
            user.add(shopDatalist.get(i).getShop_user());}


        //리스트를 문자열로 변환//
        convertToString(title);
        convertToString(contents);
        convertToString(date);
        convertToString(user);

//     editor.putString(key,value); //key, value를 이용하여 저장하는 형태
        //에디터로 키값으로 데이터를 저장, 문자열로 변환한 데이터를 넣어준다.//
        editor.putString("title",convertToString(title));
        editor.putString("contents",convertToString(contents));
        editor.putString("date",convertToString(date));
        editor.putString("user",convertToString(user));


        editor.commit(); //최종 저장 editor.apply();

    }

    private void loadData() {
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sharedPreferences = getSharedPreferences("shopData", MODE_PRIVATE);
        //task list라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 null 반환
        String tt = sharedPreferences.getString("title", "");
        String cc = sharedPreferences.getString("contents", "");
        String dd = sharedPreferences.getString("date", "");
        String uu = sharedPreferences.getString("user", "");

        ///저장할때 String으로 변환한 리스트를 다시 리스트로 변환시킨다.
        convertToArray(tt); //제목
        convertToArray(cc); // 타이틀
        convertToArray(dd); //날짜
        convertToArray(uu); //유저이름


        ///변환된 리스트의 길이는 모두 같으니 한개를 지정해서, 그사이즈만큼 반복하면서 아이템리스트에 다시 값을 넣어준다.
        for (int i = 0; i < convertToArray(uu).size(); i++) {
            additem(convertToArray(tt).get(i), convertToArray(cc).get(i), convertToArray(dd).get(i), convertToArray(uu).get(i));
        }

        //만약 데이터 리스트의 0번째 값의 유저이름이 없다면 리스트를 새로만든다. 처음에 아무것도 없는 아이템이 생겨서 추가함.
        if(shopDatalist.get(0).getShop_user()==("")){
            shopDatalist = new ArrayList<>();
        }

    }

///문자열로 리스트를 변환해주는 메소드

    /*  StringBuilder, StringBuffer을 이용하면 리스트의 값을 하나씩 String으로 변환해서 넣어주는 것이 가능하다
                여기서는 for each구문을 이용한다. 여기서는 구분자넣고, 첫번째 값을 꺼내서 넣고, 구분자넣고, .....반복한다.
                아이템값을 String으로 변환한다면 전체를 String으로 변환할 수 있을거같은데,,,*/
    private String convertToString(ArrayList<String> list) {

        StringBuilder sb = new StringBuilder();
        String delim = "";
        //리스트의 값을 하나씩 문자열에 넣어준다. 구분자를 주면서,
        for (String s : list)
        {
            sb.append(delim);
            sb.append(s);;
            delim = ",";
        }
        return sb.toString();
    }

    ///리스트로 값을 변환 해주는 메소드
    private ArrayList<String> convertToArray(String string) {

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
        return list;
    }


    ////////////////액티비티가 이동하면서 onStop될때 데이터를 저장시킨다.
    @Override
    protected void onStop() {
        super.onStop();
        //데이터를 저장시킨다.
        saveData();
    }



    }