package com.example.bicycle;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


import static com.example.bicycle.MainHomeActivity1.Copylist;
import static com.example.bicycle.MainHomeActivity1.DivisionID;
import static com.example.bicycle.MainHomeActivity1.ID;
import static com.example.bicycle.MainHomeActivity1.NAME;
import static com.example.bicycle.MainHomeActivity1.SEX;


public class UserActivity1 extends AppCompatActivity {

    private TextView main_id,main_sex,main_name,main_introduce,main_Email;
    private ImageButton main_edit;
    private ImageView homeButton,searchButton,bicycleButton,userButton;
    private static final int REQUEST_EDT=132;
    private static final int REQUEST_P=134;
    private ImageView main_picture,userSetting;
    private Button LogoutButton;
    private String ImageFilePath;
    private Uri PhotoUri;
   private TextView noText;



    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    RecyclerView mRecyclerView=null; //
    SearchAdapter UserAdapter=null; //찾기 어댑터와 동일해도됨
    ArrayList<MainHomeItem> MList=new ArrayList<MainHomeItem>(); // 사용되는 아이템은 모두 동일

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadData();
       // MList.addAll(Copylist);//메인리스트에서의 정보를 옮겨준다.

       preferences = getSharedPreferences("RegisteredUser", MODE_PRIVATE);
       editor=preferences.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        homeButton=findViewById(R.id.homeButton);
        searchButton=findViewById(R.id.searchButton);
        bicycleButton=findViewById(R.id.bicycleButton);
        LogoutButton=findViewById(R.id.logout);

         //상태창 정보
        main_id=findViewById(R.id.main_id);
        main_sex=findViewById(R.id.main_sex);
        main_name=findViewById(R.id.main_name);
        main_edit=findViewById(R.id.main_edit);
        userSetting=findViewById(R.id.UserSetting);
        main_Email=findViewById(R.id.main_Email);
        //소개 변경시
        main_edit=findViewById(R.id.main_edit);
        main_introduce=findViewById(R.id.main_introduce);
        main_picture=findViewById(R.id.main_picture);


        //셰어드에 저장된 값 받기.
        main_id.setText(preferences.getString(DivisionID+"ID",""));
        main_sex.setText(preferences.getString(DivisionID+"Sex",""));
        main_name.setText(preferences.getString(DivisionID+"Name",""));
        main_introduce.setText(preferences.getString(DivisionID+"introduce",""));
        if(preferences.getString(DivisionID+"Image","").equals("")){}
        else{
        main_picture.setImageBitmap(StringToBitMap(preferences.getString(DivisionID+"Image","")));}//문자열 비트맵으로 변경해서 넣어주자.
        main_Email.setText(preferences.getString(DivisionID+"Email",""));

        //리사이클러뷰 설정

        mRecyclerView=findViewById(R.id.UserRecycler);
        UserAdapter=new SearchAdapter(MList);
        mRecyclerView.setAdapter(UserAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager); //리사이클러뷰에 레이아웃 매니저 세팅

        UserAdapter.filterOnlyID(DivisionID);   //아이디로 글 필터링

        UserAdapter.setOnItemClickListener(new SearchAdapter.onSearchItemClickListener() {
            @Override
            public void onSearchItemClicked(MainHomeItem model) {
                Intent intent=new Intent(getApplicationContext(),HomeAll.class);
                intent.putExtra("Code",model.getHomeCode());
                startActivity(intent);
            }
        });


        //introduce에 있는 값 편집창으로 전달하고 다시 받아서 바꾸기
        main_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), UserEdtActivity.class);

                intent.putExtra("introduce",main_introduce.getText().toString());

                startActivityForResult(intent,REQUEST_EDT);
            }
        });

        //로그아웃 버튼 클릭 시 로그인시 저장되는 모든 정보 삭제,
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                SharedPreferences sf=getSharedPreferences("AutoLogin",0);
                SharedPreferences.Editor editor=sf.edit();
                editor.clear();
                editor.apply();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); ///현재 실행중인 모든 액티비티 종료 후 새로운 액티비티 하나 띄운다.
                startActivity(intent);
            }
        });

        //카메라 권한체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();


        //사진 클릭 시 갤러리에서 이미지 가져오기

        //갤러리에서 사진 불러오기
        main_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup= new PopupMenu(getApplicationContext(),view);
                    getMenuInflater().inflate(R.menu.usermenu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.n1: //갤러리
                                    Toast.makeText(getApplicationContext(), "이미지변경", Toast.LENGTH_SHORT).show();
                                    //그림을 불러온다.
                                    Intent intent = new Intent();
                                    intent.setAction(intent.ACTION_GET_CONTENT);
                                     intent.setType("image/*");
                                      startActivityForResult(Intent.createChooser(intent,"Select Picture"),REQUEST_P);
                                    break;

                                case R.id.n2: //카메라
                                    Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if(intent1.resolveActivity(getPackageManager())!=null){
                                        File photoFile=null;
                                        try {
                                            photoFile=createImageFile();
                                        }catch (IOException e){

                                        }

                                        if(photoFile!=null){
                                            PhotoUri= FileProvider.getUriForFile(getApplicationContext(),getPackageName(),photoFile);
                                            intent1.putExtra(MediaStore.EXTRA_OUTPUT,PhotoUri);
                                            startActivityForResult(intent1,7654);
                                        }
                                    }
                                    break;

                                case R.id.n3: //기본이미지 변경
                                        main_picture.setImageResource(R.drawable.my);//이미지 기본으로 보여주고
                                        editor.putString(DivisionID+"Image","");//이미지 공백 넣어줌
                                        editor.apply();
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



        noText=findViewById(R.id.noText);

        if(MList.size()==0){
            noText.setVisibility(View.VISIBLE);
        }
        else{
            noText.setVisibility(View.INVISIBLE);
        }


        userSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),UserSetting.class);
                startActivity(intent);//TODO 유저세팅
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

        // 자전거 클릭 시 이동
        bicycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainBActivity1.class);
                startActivity(intent);
                finish();
            }
        });

        if(main_id.getText().toString().contains("@")){
            userSetting.setVisibility(View.INVISIBLE);
        }

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK&&requestCode==REQUEST_EDT){
            main_introduce.setText(data.getStringExtra("introduceok"));/// 소개를 변경
            editor.putString(DivisionID+"introduce",main_introduce.getText().toString());
            editor.apply();
        }

        if(requestCode==REQUEST_P&&resultCode==RESULT_OK&&data!=null){
            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                // 이미지 표시
                main_picture.setImageBitmap(img);
                //이미지 세팅이 끝난 후 이미지를 셰어드에 저장해준다.

                Bitmap BT;
                if(img.getByteCount()>1000000){
                BT=Bitmap.createScaledBitmap(img,img.getWidth()/4,img.getHeight()/4,true);}//용량감소시키고
                else{
                    BT=img;
                }
                String btString=BitMapToString(BT);//문자열로 변환시킨 후
                editor.putString(DivisionID+"Image",btString);//넣어준다.
                editor.apply();

                //glide 사용
//           Uri uri=data.getData();
//
//            Glide.with(getApplicationContext()).load(uri.toString()).into(main_picture);//Glide.with(this).load("이미지 URL" or "이미지 리소스").into(이미지뷰);

            } catch (Exception e) {
                e.printStackTrace();
            }



        }

        if(requestCode==7654&&resultCode==RESULT_OK){ //카메라 이미지 받기
            Bitmap bitmap = BitmapFactory.decodeFile(ImageFilePath);
            ExifInterface exif=null;
            try{
                exif=new ExifInterface(ImageFilePath);
            }catch (IOException e){
                e.printStackTrace();
            }

            int exifOrientation;
            int exitDegree;

            if(exif !=null){
                exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exitDegree=exifOrientationToDegress(exifOrientation);
            }else{
                exitDegree=0;
            }
            Bitmap BT;

            if(bitmap.getByteCount()>1000000){
                BT=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/4,bitmap.getHeight()/4,true);}//용량감소시키고
            else{
                BT=bitmap;
            }
            String btString=BitMapToString(BT);//문자열로 변환시킨 후
            editor.putString(DivisionID+"Image",btString);//넣어준다.
            editor.apply();

            main_picture.setImageBitmap(rotate(BT,exitDegree));




        }

    }
                ////카메라 관련////
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix=new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        }else if (exifOrientation==ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if (exifOrientation==ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }


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


//////////////////////////////아이템 추가//////////////////////////
public void additem(Bitmap image, String user, String date, String text, String code){

    MainHomeItem mhi=new MainHomeItem();


    mhi.setHomeImage(image);
    mhi.setHomeDate(date);
    mhi.setHomeUser(user);
    mhi.setHomeText(text);
    mhi.setHomeCode(code);

        MList.add(mhi);
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
        if(MList.get(0).getHomeUser().equals("")){
         //   MList = new ArrayList<>();
            MList.clear();              //만약 유저리스트에서 지웠을 때 다지운다면 new일때는 데이터가 남는다. 이유는 모르겠다.
                                        // 기존리스트주소참조중인데 새걸 만들면 바로 적용이 안되나?? 정도로 생각한다
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



    private File createImageFile() throws IOException {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String ImageFileName="TEST_"+timeStamp+"_";
        File StorageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image= File.createTempFile(
                ImageFileName,
                ".jpg",
                StorageDir
        );
        ImageFilePath=image.getAbsolutePath();
        return image;
    }
    PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() {//퍼미션 허용일때의 액션
            //     Toast.makeText(getApplicationContext(),"권한이 허용되었습니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {//퍼미션 거부일때 액션
            //     Toast.makeText(getApplicationContext(),"권한이 거부되었습니다.",Toast.LENGTH_SHORT).show();
        }
    };




    @Override
    protected void onRestart() {
        super.onRestart();
        MList.clear();
        loadData(); //데이터를 불러온다.

        ArrayList<MainHomeItem> MListCopy=new ArrayList<MainHomeItem>();
        MListCopy.addAll(MList);
        Log.e("mlistcopy", String.valueOf(MListCopy.size()));
        MList.clear();
        Log.e("mlist", String.valueOf(MList.size()));
        for( MainHomeItem item : MListCopy) //필터안된 리스트에서 하나씩 뺴주고
        {

            if(item.getHomeUser().equals(DivisionID))   //만약 유저이름이 텍스트와 같다면
            {
                MList.add(item);             //아이템에 추가

            }
        }

        UserAdapter.notifyDataSetChanged();
       // UserAdapter.filterOnlyID(DivisionID);



        if(MList.size()==0){
            noText.setVisibility(View.VISIBLE);
        }
        else{
            noText.setVisibility(View.INVISIBLE);
        }


    }

}
