package com.example.bicycle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.example.bicycle.EditActivity.EditItem;
import static com.example.bicycle.HomeAll.RemoveItem;

public class MainHomeActivity1 extends AppCompatActivity {


    public static String DivisionID;
    public static String ID;
    public static String PASSWORD;
    public static String SEX;
    public static String NAME;
    private ImageView homeButton, searchButton, bicycleButton, userButton;
    private ImageView addButton;
    private final int addRequest=22222;
    //리사이클러뷰 객체 생성
    RecyclerView mRecyclerview=null;
    MainHomeAdapter mHomeAdapter=null;
    ArrayList<MainHomeItem> mhomelist=new ArrayList<MainHomeItem>();
    public static ArrayList<MainHomeItem> Copylist=new ArrayList<MainHomeItem>();
    FrameLayout Frame1;
    private final int edtcode=1215;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);


        loadData();
        //메인화면 나오기 전단계에서 보낸 것들을 저장.



        SharedPreferences SP1=getSharedPreferences("AutoLogin",MODE_PRIVATE); // 회원가입시 보내준 아이디정보
        DivisionID=SP1.getString("id","");//구분을 위한 아이디


        SharedPreferences SP2=getSharedPreferences("RegisteredUser",MODE_PRIVATE);   //저장된 모든 아이디의 정보


//

        //셰어드에 저장된 정보를 아이디에 매칭
        ID = SP2.getString(DivisionID+"ID","");
        PASSWORD = SP2.getString(DivisionID+"Password","");
        SEX = SP2.getString(DivisionID+"Sex","");
        NAME =SP2.getString(DivisionID+"Name","");


        searchButton=findViewById(R.id.searchButton);
        bicycleButton=findViewById(R.id.bicycleButton);
        userButton=findViewById(R.id.userButton);
        addButton=findViewById(R.id.HomeAdd);
        Frame1=findViewById(R.id.Frame1);


        //리사이클러뷰 관련
        mRecyclerview=findViewById(R.id.homerecycler); //리사이클러뷰 레이아웃 매칭
        mHomeAdapter=new MainHomeAdapter(mhomelist); // 리사이클러뷰에 들어갈 리스트 매칭
        mRecyclerview.setAdapter(mHomeAdapter); // 리사이클러뷰 어댑터 설정.


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerview.setLayoutManager(mLayoutManager);


        mHomeAdapter.setOnItemClickListener(new MainHomeAdapter.OnItemClickListener() {
            //수정버튼 클릭 시
            @Override
            public void onItemClick(View v, final int position) { //수정, 삭제, 작성자 프로필보기

                pos=position;

                PopupMenu popup= new PopupMenu(getApplicationContext(),v);
                getMenuInflater().inflate(R.menu.menu1, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.m1:
                                Toast.makeText(getApplication(),"해당 글을 수정합니다.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MainHomeActivity1.this, EditActivity.class);
                                intent.putExtra("Code",mhomelist.get(position).getHomeCode());
                                startActivityForResult(intent,edtcode);

                                break;
                            case R.id.m2:
                                //다이얼로그 띄우기//
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainHomeActivity1.this);
                                builder.setMessage("해당 게시물을 삭제하시겠습니까?\n삭제된 게시물은 복원되지 않습니다.");//내용세팅

                                //우측버튼 만들기.
                                builder.setPositiveButton("삭제",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(),"게시물을 삭제합니다.",Toast.LENGTH_SHORT).show();
                                                mhomelist.remove(position);
                                                saveData(); //메인창에서 글 삭제시!
                                                mHomeAdapter.notifyDataSetChanged();

                                            }
                                        });
                                //좌측버튼 만들기
                                builder.setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                builder.show();





                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
            //더보기 클릭 시
            @Override
            public void onSeeClick(View v, int position) {
                Intent intent=new Intent(MainHomeActivity1.this,HomeAll.class);
                intent.putExtra("Code",mhomelist.get(position).getHomeCode());
                startActivity(intent);


            }

            @Override   //유저 이미지 클릭시 이동. 내글 클릭시 반응없음, 상대방 이미지클릭시만 이동.
            public void onUserImageClick(View v, int position) {

                if(mhomelist.get(position).getHomeUser().equals(DivisionID)) {

                    Toast.makeText(getApplicationContext(),"내가 작성한 게시물 입니다.",Toast.LENGTH_SHORT).show();

                }

                    else{
                    Toast.makeText(getApplicationContext(),"해당 유저의 프로필로 이동합니다.",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
            intent.putExtra("Code",mhomelist.get(position).getHomeCode());
            intent.putExtra("ID",mhomelist.get(position).getHomeUser());
            startActivity(intent);}


            }


        });







        ///추가버튼 클릭시 이동

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainHome_add.class);
                startActivityForResult(intent,addRequest);
            }
        });





////////////////////////이동메뉴//////////////////////////

        //돋보기 클릭 시 이동
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainSearchActivity1.class);
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

        if(mhomelist.size()==0){
                                Frame1.setVisibility(View.VISIBLE);
                            }
                            else{
                                Frame1.setVisibility(View.INVISIBLE);
                            }


    }////////////onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ///4. 새로운 글 작성완료, 이미지와 텍스트를 셰어드에 저장한 상태, 아이템에 셰어드에서 값을 꺼내서 넣고, 아이템을 추가해줘야 한다.
        if(requestCode==addRequest&&resultCode==RESULT_OK){

            //현재시간
            long now = System.currentTimeMillis ();
            Date date=new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH시 mm분");
            String getTime = sdf.format(date);

            Toast.makeText(getApplicationContext(),"등록완료",Toast.LENGTH_SHORT).show();
            SharedPreferences sp=getSharedPreferences("HomeAddData",MODE_PRIVATE);
            //Shared에 저장된 값을 빼준다. String으로 보낸 이미지 비트맵 변환
            //

            String BitmapString=sp.getString("Image",null); //이미지를 String으로 보내준값


            Bitmap bm=StringToBitMap(BitmapString);
         //   Drawable image=new BitmapDrawable(getResources(),bm);
            Long Time=System.currentTimeMillis();
            String codetime=String.valueOf(System.currentTimeMillis());
            additem(bm,ID,getTime,sp.getString("Text",""),codetime);
            Frame1.setVisibility(View.INVISIBLE);

            saveData(); //새로운 글 작성 시
            mhomelist.clear();
            loadData();

            mHomeAdapter.notifyDataSetChanged();
        }

        if(requestCode==edtcode&&resultCode==RESULT_OK){    //글 수정 결과
//            Log.e("글수정 메인", String.valueOf(requestCode));
//            Log.e("글수정 메인", "여기안옴?");
//            mhomelist.set(pos,EditItem);
//
//            EditItem=null;
//
//            saveData(); //메인에서 글 수정 시
//            mhomelist.clear();
//            loadData();

            mHomeAdapter.notifyDataSetChanged();


        }




    }

    public void additem(Bitmap image, String user, String date, String text, String code){

        MainHomeItem mhi=new MainHomeItem();


        mhi.setHomeImage(image);
        mhi.setHomeDate(date);
        mhi.setHomeUser(user);
        mhi.setHomeText(text);
        mhi.setHomeCode(code);

        mhomelist.add(mhi);
    }


    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를 memoshared, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("MainData", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();


        ///제목, 내용, 날짜, 유저이름을 담을 리스트를 새로 만든다. 그리고 각 요소를 담아준다.
        ArrayList<String> User=new ArrayList<String>();
        for(int i =0; i<mhomelist.size();i++){
            User.add(mhomelist.get(i).getHomeUser());}

        ArrayList<String> Date=new ArrayList<String>();
        for(int i =0; i<mhomelist.size();i++){
            Date.add(mhomelist.get(i).getHomeDate());}

        ArrayList<String> Text =new ArrayList<String>();
        for(int i =0; i<mhomelist.size();i++){
            Text.add(mhomelist.get(i).getHomeText());}

        ArrayList<String> Code =new ArrayList<String>();
        for(int i =0; i<mhomelist.size();i++){
            Code.add(mhomelist.get(i).getHomeCode());}

        ArrayList<String> Image =new ArrayList<String>();
        for(int i =0; i<mhomelist.size();i++){
            Image.add(BitMapToString(mhomelist.get(i).getHomeImage()));} //이미지를 리스트에 저장할 때 스트링으로 변환하여 담는다.

        //리스트를 문자열로 변환//
        convertToString(User);
        convertToString(Date);
        convertToString(Text);
        convertToString(Image);
        convertToString(Code);

//     editor.putString(key,value); //key, value를 이용하여 저장하는 형태
        //에디터로 키값으로 데이터를 저장, 문자열로 변환한 데이터를 넣어준다.//
        editor.putString("User",convertToString(User));
        editor.putString("Date",convertToString(Date));
        editor.putString("Text",convertToString(Text));
        editor.putString("Image",convertToString(Image));
        editor.putString("Code", convertToString(Code));

        editor.apply(); //최종 저장 editor.apply();

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
        }//

        //만약 데이터 리스트의 0번째 값의 유저이름이 없다면 리스트를 새로만든다. 처음에 아무것도 없는 아이템이 생겨서 추가함.
        if(mhomelist.get(0).getHomeUser().equals("")){
            mhomelist = new ArrayList<>();
        }

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

        mhomelist.clear();
        loadData();

       mHomeAdapter.notifyDataSetChanged();//재시작 할때마다 데이터의 변화를 알려주기 위해
    }

    @Override
    protected void onPause() { //화면 이동할때 다음 화면에 데이터를 보여주기 위해서 무조건 이때 저장해야 한다.
        super.onPause();
      //  saveData();

        Copylist.clear();//카피리스트 리스트 다 지워주고
        Copylist.addAll(mhomelist); //모든걸 추가해줌   이건 모든 아이템 보기에서 삭제를 위해서!
    }



}