package com.example.bicycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.example.bicycle.MainHomeActivity1.DivisionID;


public class CommentActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor spedt;
    public static String cd1;
    public static String id1;
    private Button Comment_Button;
    private EditText Comment_EditText;

    public static CommentActivity commentActivity=null;//다른 화면에서 꺼주기 위해

    RecyclerView Comment_RecyclerView=null;
    CommentAdapter CAdapter=null;
    ArrayList<CommentItem> Clist=new ArrayList<CommentItem>();
    CommentItem comI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentActivity=this;//다른 화면에서 꺼주기 위해



        cd1=getIntent().getStringExtra("Code");
      //  Toast.makeText(getApplicationContext(),cd1,Toast.LENGTH_SHORT).show();
        id1=getIntent().getStringExtra("ID");
      //  Toast.makeText(getApplicationContext(),id1,Toast.LENGTH_SHORT).show();



        loadData();
        //댓글 정보를 저장할 셰어드 프리퍼런스
        sp=getSharedPreferences("CommentData",MODE_PRIVATE);
        spedt=sp.edit();

        //레이아웃매칭
        Comment_Button=findViewById(R.id.Comment_Button);
        Comment_EditText=findViewById(R.id.Comment_EditText);

        final ImageView pencil=findViewById(R.id.pencil);
        final TextView pencilText=findViewById(R.id.pencilText);


        //리사이클러뷰 설정
        Comment_RecyclerView=findViewById(R.id.Comment_RecyclerView);
        CAdapter=new CommentAdapter(Clist);
        Comment_RecyclerView.setAdapter(CAdapter);


        Comment_RecyclerView.setLayoutManager( new LinearLayoutManager(this));

        CAdapter.setOnItemClickListener(new CommentAdapter.onCommentClickListener() {
            @Override
            public void onCommentClicked(CommentItem model) {
                if(model.getCommentID().equals(DivisionID)) { //아이디가 로그인된 아이디와 같다면?

                    Toast.makeText(getApplicationContext(),"내가 작성한 게시물 입니다.",Toast.LENGTH_SHORT).show();

                }

                else{ //로그인된 아이디와 다르다면
                    Toast.makeText(getApplicationContext(),"해당 유저의 프로필로 이동합니다.",Toast.LENGTH_SHORT).show();

                    //만약 기존에 프로필 액티비티가 떠있다면 그거 종료시킨다.
                    if(ProfileActivity.profileActivity!=null){
                        ProfileActivity.profileActivity.finish();
                    }

                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                    intent.putExtra("ID",model.getCommentID());
                    startActivity(intent);

                }



            }

            @Override
            public void onXClicked(final CommentItem model) {
                //다이얼로그 띄우기//
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                builder.setMessage("해당 댓글을 삭제하시겠습니까?\n삭제된 댓글은 복원되지 않습니다.");//내용세팅

                //우측버튼 만들기.
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"댓글이 삭제됩니다",Toast.LENGTH_SHORT).show();
                                Clist.remove(model);
                                CAdapter.notifyDataSetChanged();

                                if(Clist.size()==0){
                                    pencil.setVisibility(View.VISIBLE);
                                    pencilText.setVisibility(View.VISIBLE);
                                }


                            }
                        });
                //좌측버튼 만들기
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();



            }

            @Override
            public void onEditClicked(CommentItem model) {
                Toast.makeText(getApplicationContext(),"댓글을 수정합니다.",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),CommentEditActivity.class);
                intent.putExtra("Text",model.getCommentText());
                comI=model;
                startActivityForResult(intent,555);

            }
        });


        Comment_Button.setOnClickListener(new View.OnClickListener() {//글쓰기 버튼 클릭시
            @Override
            public void onClick(View view) {

                String T= Comment_EditText.getText().toString();

                if(T.length()==0){
                    Toast.makeText(getApplicationContext(),"아무것도 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                long now = System.currentTimeMillis ();
                Date date=new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH시 mm분");
                String getTime = sdf.format(date);

                Comment_EditText.setText("");
                additem(DivisionID,T,getTime);
                CAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"댓글을 등록합니다.",Toast.LENGTH_SHORT).show();


                    pencil.setVisibility(View.INVISIBLE);
                    pencilText.setVisibility(View.INVISIBLE);



            }
        });

        if(Clist.size()==0){
            pencil.setVisibility(View.VISIBLE);
            pencilText.setVisibility(View.VISIBLE);
        }





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==555&&resultCode==RESULT_OK){
            comI.setCommentText(data.getStringExtra("Text"));
            CAdapter.notifyDataSetChanged();
        }
    }

    public void additem(String ID, String Text, String Date){

        CommentItem item=new CommentItem();


       item.setCommentID(ID);
       item.setCommentText(Text);
       item.setCommentDate(Date);


        Clist.add(item);
    }


    /////////////////////////////저장과 관련된 부분/////////////////////////////////////////////////
    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를
        SharedPreferences sharedPreferences = getSharedPreferences("CommentData", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();


        ///제목, 내용, 날짜, 유저이름을 담을 리스트를 새로 만든다. 그리고 각 요소를 담아준다.
        ArrayList<String> ID=new ArrayList<String>();
        for(int i =0; i<Clist.size();i++){
            ID.add(Clist.get(i).getCommentID());}

        ArrayList<String> TEXT=new ArrayList<String>();
        for(int i =0; i<Clist.size();i++){
            TEXT.add(Clist.get(i).getCommentText());}

        ArrayList<String> Date =new ArrayList<String>();
        for(int i =0; i<Clist.size();i++){
            Date.add(Clist.get(i).getCommentDate());}

        //리스트를 문자열로 변환//
        convertToString(ID);
        convertToString(TEXT);
        convertToString(Date);


//     editor.putString(key,value); //key, value를 이용하여 저장하는 형태
        //에디터로 키값으로 데이터를 저장, 문자열로 변환한 데이터를 넣어준다.//
        editor.putString(cd1+"ID",convertToString(ID));
        editor.putString(cd1+"Text",convertToString(TEXT));
        editor.putString(cd1+"Date",convertToString(Date));


        editor.apply(); //최종 저장 editor.apply();

    }

    private void loadData() {
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sharedPreferences = getSharedPreferences("CommentData", MODE_PRIVATE);
        //task list라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 null 반환

        String ID = sharedPreferences.getString(cd1+"ID", "");
        String TEXT = sharedPreferences.getString(cd1+"Text", "");
        String Date = sharedPreferences.getString(cd1+"Date", "");


        ///저장할때 String으로 변환한 리스트를 다시 리스트로 변환시킨다.
        convertToArray(ID);
        convertToArray(TEXT);
        convertToArray(Date);



        ///변환된 리스트의 길이는 모두 같으니 한개를 지정해서, 그사이즈만큼 반복하면서 아이템리스트에 다시 값을 넣어준다.
        for (int i = 0; i < convertToArray(ID).size(); i++) {
            additem(convertToArray(ID).get(i),convertToArray(TEXT).get(i),convertToArray(Date).get(i));
        }//

        //만약 데이터 리스트의 0번째 값의 유저이름이 없다면 리스트를 새로만든다. 처음에 아무것도 없는 아이템이 생겨서 추가함.
        if(Clist.get(0).getCommentID()==("")){
            Clist.clear();
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

    ///비트맵을 스트링으로 변환해주는 함수(URI)
    public String BitMapToString(Bitmap bitmap){


        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return path;

    }

    //Uri를 비트맵으로 변환하는 함수
    public Bitmap StringURIToBitmap(String URI){

        try {

            Bitmap Bm = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(URI));

            return Bm;

        }catch (Exception E){
            E.printStackTrace();
            return  null;
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

    @Override
    protected void onPause() {
        super.onPause();
        saveData();

    }
}
