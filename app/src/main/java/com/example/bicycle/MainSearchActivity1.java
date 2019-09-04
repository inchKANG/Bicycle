package com.example.bicycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.bicycle.MainHomeActivity1.Copylist;
import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class MainSearchActivity1 extends AppCompatActivity {
   private ImageView homeButton, searchbutton, bicycleButton, userButton;
   private EditText Search_EDT;
   private ImageButton Search_Search;

   String SpinnerText;

    RecyclerView SearchRecyclerview=null;
    SearchAdapter searchAdapter=null;
    ArrayList<MainHomeItem> searchlist=new ArrayList<MainHomeItem>();

    private TextView noText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        Log.e("서치액티비티 생명주기", "onCreate");


               loadData();



        homeButton=findViewById(R.id.homeButton);
        bicycleButton=findViewById(R.id.bicycleButton);
        userButton=findViewById(R.id.userButton);
        Search_Search=findViewById(R.id.Search_search);
        Search_EDT=findViewById(R.id.Search_edt);
        noText=findViewById(R.id.notextkk);

        if(searchlist.size()==0){
            noText.setVisibility(View.VISIBLE);
        }
        else{
            noText.setVisibility(View.INVISIBLE);
        }


        //스피너 설정
        String[] item=new String[]{"전체검색","내용검색","ID검색"};

        Spinner Searchspinner=findViewById(R.id.Searchspinner);

        Searchspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             SpinnerText=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(searchlist.size()==0){
            Toast.makeText(getApplicationContext(),"검색할 게시물이 존재하지 않습니다",Toast.LENGTH_SHORT).show();
        }


        //리사이클러뷰 설정
       SearchRecyclerview=findViewById(R.id.SearchRecyclerView);
       searchAdapter=new SearchAdapter(searchlist);
       SearchRecyclerview.setAdapter(searchAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        SearchRecyclerview.setLayoutManager(mLayoutManager);


        //텍스트 검색버튼
        Search_Search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if(SpinnerText.equals("내용")){
                searchAdapter.filterText(Search_EDT.getText().toString());

                }

                else if(SpinnerText.equals("ID")){
                    searchAdapter.filterID(Search_EDT.getText().toString());

                }

                else{
                    searchAdapter.filterall(Search_EDT.getText().toString());

                }

                if(searchlist.size()==0){
                    Toast.makeText(getApplicationContext(),"검색결과가 없습니다",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"검색완료",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Search_EDT.addTextChangedListener(new TextWatcher() { //길이가 0일때만 모두다 보이게 해준다.
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String Text=Search_EDT.getText().toString();
                if(Text.length()==0){
                searchAdapter.filterID("");}

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //아이템 클릭 시
       searchAdapter.setOnItemClickListener(new SearchAdapter.onSearchItemClickListener() {
           @Override
           public void onSearchItemClicked(MainHomeItem model) {
              Intent intent=new Intent(getApplicationContext(),HomeAll.class);
              intent.putExtra("Code",model.getHomeCode());
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


        // 자전거 클릭 시 이동
        bicycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainBActivity1.class);
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

    private void loadData() {
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sharedPreferences = getSharedPreferences("MainData", MODE_PRIVATE);
        //task list라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 null 반환

        String User = sharedPreferences.getString("User", "");
        String Date = sharedPreferences.getString("Date", "");
        String Text = sharedPreferences.getString("Text", "");
        String Image = sharedPreferences.getString("Image", "");
        String Code = sharedPreferences.getString("Code", "");

        ///저장할때 String으로 변환한 리스트를 다시 리스트로 변환시킨다.
        convertToArray(User); //유저
        convertToArray(Date); // 날짜
        convertToArray(Text); //텍스트
        convertToArray(Code); //구분코드
        convertToArray(Image); //이미지



        ///변환된 리스트의 길이는 모두 같으니 한개를 지정해서, 그사이즈만큼 반복하면서 아이템리스트에 다시 값을 넣어준다.
        for (int i = 0; i < convertToArray(User).size(); i++) {
            additem(StringToBitMap(convertToArray(Image).get(i)),convertToArray(User).get(i),convertToArray(Date).get(i),convertToArray(Text).get(i),convertToArray(Code).get(i));
        }// n

        //만약 데이터 리스트의 0번째 값의 유저이름이 없다면 리스트를 새로만든다. 처음에 아무것도 없는 아이템이 생겨서 추가함.
        if(searchlist.get(0).getHomeUser().equals("")){
//            searchlist = new ArrayList<>(); //새로 만들어주면 안지워지는데 이유를 모르겠다....
            searchlist.clear();             //하지만 클리어로 해서 해결!
        }

    }

    public void additem(Bitmap image, String user, String date, String text, String code){

        MainHomeItem mhi=new MainHomeItem();


        mhi.setHomeImage(image);
        mhi.setHomeDate(date);
        mhi.setHomeUser(user);
        mhi.setHomeText(text);
        mhi.setHomeCode(code);

       searchlist.add(mhi);
    }


    ////리스트를 문자열로 변환
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

    ///비트맵을 스트링으로 변환해주는 함수
    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();

        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;


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

    @Override
    protected void onRestart() {
        super.onRestart();
        //지웠을때 남아있는 문제발생. 필터안하면 잘 지워지는데 필터만 하면 이상해짐. 어댑터의 메소드를 사용하지 않고 직접 적어줌
        searchlist.clear();
        loadData();
        ArrayList<MainHomeItem> searchlistCopy=new ArrayList<MainHomeItem>();
        searchlistCopy.addAll(searchlist);
        searchlist.clear();

        if(SpinnerText.equals("내용")){
        //    searchAdapter.filterText(Search_EDT.getText().toString());

            if(Search_EDT.getText().toString().length()==0){           //필터에 아무입력이 없다면
                searchlist.addAll(searchlistCopy);
            }
            else
            {
                for( MainHomeItem item : searchlistCopy) //필터안된 리스트에서 하나씩 뺴주고
                {

                    if(item.getHomeText().contains(Search_EDT.getText().toString()))   //만약 내용이 텍스트에 포함되어있다면
                    {
                        searchlist.add(item);             //아이템에 추가

                    }
                }
            }


        }

        else if(SpinnerText.equals("ID")){
        //    searchAdapter.filterID(Search_EDT.getText().toString());

            if(Search_EDT.getText().toString().length()==0){           //필터에 아무입력이 없다면
                searchlist.addAll(searchlistCopy);

            }
            else
            {
                for( MainHomeItem item : searchlistCopy) //필터안된 리스트에서 하나씩 뺴주고
                {

                    if(item.getHomeUser().contains(Search_EDT.getText().toString()))   //만약 유저이름이 텍스트에 포함되어있다면
                    {
                        searchlist.add(item);             //아이템에 추가

                    }
                }
            }

        }

        else{
         //   searchAdapter.filterall(Search_EDT.getText().toString());

            if(Search_EDT.getText().toString().length()==0){           //필터에 아무입력이 없다면
                searchlist.addAll(searchlistCopy);
            }
            else
            {
                for( MainHomeItem item : searchlistCopy) //필터안된 리스트에서 하나씩 뺴주고
                {

                    if(item.getHomeUser().contains(Search_EDT.getText().toString())||item.getHomeText().contains(Search_EDT.getText().toString()))   //만약 유저이름이 텍스트에 포함되어있다면
                    {
                        searchlist.add(item);             //아이템에 추가

                    }
                }
            }

        }

        searchAdapter.notifyDataSetChanged();







        if(searchlist.size()==0){
            noText.setVisibility(View.VISIBLE);
        }
        else{
            noText.setVisibility(View.INVISIBLE);
        }



    }

}
