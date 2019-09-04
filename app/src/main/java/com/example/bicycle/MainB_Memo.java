package com.example.bicycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.example.bicycle.MainHomeActivity1.DivisionID;
import static com.example.bicycle.MainHomeActivity1.ID;

public class MainB_Memo extends AppCompatActivity {

    RecyclerView memoRecycler=null;
    MemoAdapter memoAdapter=null;
    ArrayList<MemoItem> memoitems=new ArrayList<MemoItem>();///이부분 써주지 않으면 오류뜨는데.... 왜그럴까?
    EditText Memosearch;
    Button memofind;

    int pos;//포지션값 저장을 위해

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_b__memo);

        loadData(); //데이터 로드

        ///리사이클러뷰 부분
       memoRecycler=findViewById(R.id.memo_rec);
       memoAdapter=new MemoAdapter(memoitems);
       memoRecycler.setAdapter(memoAdapter);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        memoRecycler.setLayoutManager(mLayoutManager);





            ////새로운 매모 작성////
        Button Newmemo=findViewById(R.id.memo_new);
        Newmemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Memo_new.class);
                startActivityForResult(intent,787);
                Toast.makeText(getApplicationContext(),"새로운 메모를 작성합니다.",Toast.LENGTH_SHORT).show();
            }
        });

       memoAdapter.setOnItemClickListener(new MemoAdapter.OnItemClickListener() {
           @Override
           public Void onItemClick(View v, int position) {
               Intent intent=new Intent(getApplicationContext(),Memo_new.class);
               pos=position;
               //기존 값을 메모로 보내기.
               intent.putExtra("title",memoitems.get(position).getTitle().toString());
               intent.putExtra("text",memoitems.get(position).getText().toString());
              startActivityForResult(intent,112);





               return null;
           }
       });





        memoAdapter.notifyDataSetChanged();
    }

    public void additem(String title, String text, String date){

        MemoItem mitem=new MemoItem();

        mitem.setTitle(title);
        mitem.setText(text);
       mitem.setDate(date);

        memoitems.add(mitem);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        long now = System.currentTimeMillis ();
        Date date=new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH시 mm분");
        String getTime = sdf.format(date);

        if(requestCode==787&&resultCode==RESULT_OK){

            MemoItem item=new MemoItem();
            item.setTitle(data.getStringExtra("title"));
            item.setText(data.getStringExtra("text"));
            item.setDate(getTime);
            Toast.makeText(getApplicationContext(),"작성완료!",Toast.LENGTH_SHORT).show();
            memoitems.add(item);
            memoAdapter.notifyDataSetChanged();
        }

        if(requestCode==112&&resultCode==RESULT_OK){//수정시
            MemoItem item=new MemoItem();
            item.setTitle(data.getStringExtra("title"));
            item.setText(data.getStringExtra("text"));
            item.setDate(getTime);
            Toast.makeText(getApplicationContext(),"수정완료!",Toast.LENGTH_SHORT).show();
            memoitems.set(pos,item);
            memoAdapter.notifyDataSetChanged();

        }
    }




    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를 memoshared, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences(DivisionID+"MemoData", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(memoitems); //사용자가 입력한 저장할 데이터,
        editor.putString("task list", json); //key, value를 이용하여 저장하는 형태
        editor.apply(); //최종 저장
    }

    private void loadData() {
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sharedPreferences = getSharedPreferences(DivisionID+"MemoData", MODE_PRIVATE);
        Gson gson = new Gson();
        //task list라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 null 반환
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<MemoItem>>() {}.getType();
        memoitems = gson.fromJson(json, type);

        //Task list가 없어 null이 뜬다면 새로운 ArrayList를 만들어준다.
        if (memoitems == null) {
            memoitems = new ArrayList<>();
        }
    }




    @Override
    protected void onStop() {///온스탑에 지정 시 종료시 자동으로 저장된다.
        super.onStop();
        saveData();
    }
}

