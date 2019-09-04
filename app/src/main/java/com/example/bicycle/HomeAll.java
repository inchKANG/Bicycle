package com.example.bicycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.bicycle.EditActivity.EditItem;
import static com.example.bicycle.MainHomeActivity1.Copylist;
import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class HomeAll extends AppCompatActivity {

    ArrayList<MainHomeItem> alllist=new ArrayList<MainHomeItem>();
    private TextView id,Date,Text;
    private ImageView Image, edt,all_userImage;
    private Button comButton;
    MainHomeItem thisitem=new MainHomeItem();
    public static MainHomeItem RemoveItem=new MainHomeItem();


    public static HomeAll homeAll=null;//다른 화면에서 꺼주기 위해
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_all);

        homeAll=this;//다른 화면에서 꺼주기 위해

        preferences = getSharedPreferences("RegisteredUser", MODE_PRIVATE);
        editor=preferences.edit();


        alllist=Copylist;
        final String CD;
        CD=getIntent().getStringExtra("Code");


        for( MainHomeItem item : alllist) //리스트에서 구분코드 정보에 대한 값만 빼준다.
        {

            if(item.getHomeCode().equals(CD))
            {
               thisitem=item;

            }
        }

        id=findViewById(R.id.all_user);
       Date= findViewById(R.id.all_date);
       Image= findViewById(R.id.all_image);
       Text= findViewById(R.id.all_text);
       comButton= findViewById(R.id.comButton);
       edt= findViewById(R.id.all_edit);
        all_userImage=findViewById(R.id.all_userImage);

       //////////////값세팅///////////////////////
        id.setText(thisitem.getHomeUser());
        Date.setText(thisitem.getHomeDate());
        Image.setImageBitmap(thisitem.getHomeImage());
        Text.setText(thisitem.getHomeText());
        //프로필이미지세팅.
        if(preferences.getString(id.getText().toString()+"Image","").equals("")){} //셰어드에서 꺼낸 이미지가 아무것도없다면 아무것도 하지마.
        else{
            all_userImage.setImageBitmap(StringToBitMap(preferences.getString(id.getText().toString()+"Image","")));}// 아니면 문자열 비트맵으로 변경해서 넣어주자.


        //아이디가
        if(!id.getText().toString().equals(DivisionID)){
            edt.setVisibility(View.INVISIBLE);
        }

        all_userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().equals(DivisionID)) { //아이디가 로그인된 아이디와 같다면?

                    Toast.makeText(getApplicationContext(),"내가 작성한 게시물 입니다.",Toast.LENGTH_SHORT).show();

                }

                else{ //로그인된 아이디와 다르다면
                    Toast.makeText(getApplicationContext(),"해당 유저의 프로필로 이동합니다.",Toast.LENGTH_SHORT).show();

                    //만약 기존에 프로필 액티비티가 떠있다면 그거 종료시킨다.
                    if(ProfileActivity.profileActivity!=null){
                        ProfileActivity.profileActivity.finish();
                    }
                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                    intent.putExtra("Code",CD);
                    intent.putExtra("ID",id.getText().toString());
                    startActivity(intent);
                   // finish();
                }

            }
        });

            ///수정버튼 클릭시 이동 수정과 삭제가 가능하다.
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup= new PopupMenu(getApplicationContext(),view);
                getMenuInflater().inflate(R.menu.menu1, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.m1:
                                Toast.makeText(getApplication(),"해당 글을 수정합니다.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(), EditActivity.class);
                                intent.putExtra("Code",CD);
                                startActivityForResult(intent,222);

                                break;

                            case R.id.m2:
                                //다이얼로그 띄우기//
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeAll.this);
                                builder.setMessage("해당 게시물을 삭제하시겠습니까?\n삭제된 게시물은 복원되지 않습니다.");//내용세팅

                                //우측버튼 만들기.
                                builder.setPositiveButton("삭제",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(),"게시물을 삭제합니다.",Toast.LENGTH_SHORT).show();
                                                Copylist.remove(thisitem); //카피리스트에서 지금 글을 지우고
                                                saveData();     //저장해주면 이전단계에서 저장해서 보내준 데이터가 카피리스트니깐 메인데이터에서 글지워진상태로 저장한거나 같지
                                                finish();

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
        });
            //댓글창으로 이동
        comButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //만약 댓글창이 기존에 켜져있다면 종료시킨다.(댓글창 -유저이미지클릭- 프로필 -댓글창 이런식이면.
                if(CommentActivity.commentActivity!=null){
                    CommentActivity.commentActivity.finish();
                }

                Intent intent=new Intent(getApplicationContext(),CommentActivity.class);
                intent.putExtra("Code",CD);
                intent.putExtra("ID",id.getText().toString());
                startActivity(intent);
            }
        });









    }


    @Override/////여기서 수정버튼 클릭 후 변화 나타나는 부분
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==222&&resultCode==RESULT_OK){/////여기서 수정버튼 클릭 후 수정완료시 변화 나타나는 부분 이부분은 받아주기만 하면 되니 저장하지 말자.
            id.setText(EditItem.getHomeUser());
            Date.setText(EditItem.getHomeDate());
            Image.setImageBitmap(EditItem.getHomeImage());
            Text.setText(EditItem.getHomeText());
         //   thisitem=EditItem;

        }




    }

    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를 memoshared, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("MainData", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();


        ///제목, 내용, 날짜, 유저이름을 담을 리스트를 새로 만든다. 그리고 각 요소를 담아준다.
        ArrayList<String> User=new ArrayList<String>();
        for(int i =0; i<Copylist.size();i++){
            User.add(Copylist.get(i).getHomeUser());}

        ArrayList<String> Date=new ArrayList<String>();
        for(int i =0; i<Copylist.size();i++){
            Date.add(Copylist.get(i).getHomeDate());}

        ArrayList<String> Text =new ArrayList<String>();
        for(int i =0; i<Copylist.size();i++){
            Text.add(Copylist.get(i).getHomeText());}

        ArrayList<String> Code =new ArrayList<String>();
        for(int i =0; i<Copylist.size();i++){
            Code.add(Copylist.get(i).getHomeCode());}

        ArrayList<String> Image =new ArrayList<String>();
        for(int i =0; i<Copylist.size();i++){
            Image.add(BitMapToString(Copylist.get(i).getHomeImage()));} //이미지를 리스트에 저장할 때 스트링으로 변환하여 담는다.

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
}
