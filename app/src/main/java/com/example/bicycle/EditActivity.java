package com.example.bicycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

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

public class EditActivity extends AppCompatActivity {

    private ImageView edit_image;
    private EditText edit_text;
    private Button editButton;
    public static MainHomeItem EditItem =new MainHomeItem();
    private String ImageFilePath;
    private Uri PhotoUri;
    private ArrayList<MainHomeItem> Elist=new ArrayList<MainHomeItem>();
    private String CD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        loadData();


        //보낸 구분값을 받는다
       CD=getIntent().getStringExtra("Code");



        for( MainHomeItem item : Elist) //리스트에서 뺏거
        {

            if(item.getHomeCode().equals(CD))   //만약 코드가 텍스트에 포함되어있다면
            {
                EditItem=item;            //아이템에 추가    아이템 주소값 참조. 여기서 아이템 수정하면 따로 집어넣어주지 않아도 리스트 저장하면 저장될거다.
                Log.e("item", String.valueOf(item));
            }
        }


        //레이아웃 매칭
        edit_image =findViewById(R.id.edit_image);
        edit_text=findViewById(R.id.edit_text);
        editButton=findViewById(R.id.editButton);

        edit_image.setImageBitmap(EditItem.getHomeImage());
        edit_text.setText(EditItem.getHomeText());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_image==null){
                    Toast.makeText(getApplicationContext(),"이미지를 등록하세요",Toast.LENGTH_SHORT).show(); //아마 쓸일없을듯
                    return;
                }

                if(edit_text.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"내용을 입력하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                EditItem.setHomeText(edit_text.getText().toString());
                EditItem.setHomeImage(((BitmapDrawable)edit_image.getDrawable()).getBitmap());
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();



            }
        });


        //////카메라, 이미지 가져오기////////

        //카메라 권한체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup= new PopupMenu(getApplicationContext(),view);
                getMenuInflater().inflate(R.menu.usermenu2, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {


                        switch (menuItem.getItemId()){

                            case R.id.nn1: //갤러리
                                Toast.makeText(getApplicationContext(), "이미지변경", Toast.LENGTH_SHORT).show();
                                //그림을 불러온다.
                                Intent intent = new Intent();
                                intent.setAction(intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Select Picture"),6666);
                                break;

                            case R.id.nn2: //카메라
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
                                        startActivityForResult(intent1,5555);
                                    }
                                }
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






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==6666&&resultCode==RESULT_OK&&data!=null){
            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                // 이미지 표시


                Bitmap BT;
                if(img.getByteCount()>1000000){
                    BT=Bitmap.createScaledBitmap(img,img.getWidth()/4,img.getHeight()/4,true);}//용량감소시키고
                else{
                    BT=img;
                }
                edit_image.setImageBitmap(BT);


            } catch (Exception e) {
                e.printStackTrace();
            }



        }

        if(requestCode==5555&&resultCode==RESULT_OK){ //카메라 이미지 받기
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

            edit_image.setImageBitmap(rotate(BT,exitDegree));


        }





    }

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

    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를 memoshared, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("MainData", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();

//        for( MainHomeItem item : Elist) //리스트에서 뺏거
//        {
//
//            if(item.getHomeCode().equals(CD))   //만약 코드가 텍스트에 포함되어있다면
//            {
//                EditItem=item;            //아이템에 추가
//                Log.e("item", String.valueOf(item));
//            }
//        }


        ///제목, 내용, 날짜, 유저이름을 담을 리스트를 새로 만든다. 그리고 각 요소를 담아준다.
        ArrayList<String> User=new ArrayList<String>();
        for(int i =0; i<Elist.size();i++){
            User.add(Elist.get(i).getHomeUser());}

        ArrayList<String> Date=new ArrayList<String>();
        for(int i =0; i<Elist.size();i++){
            Date.add(Elist.get(i).getHomeDate());}

        ArrayList<String> Text =new ArrayList<String>();
        for(int i =0; i<Elist.size();i++){
            Text.add(Elist.get(i).getHomeText());}

        ArrayList<String> Code =new ArrayList<String>();
        for(int i =0; i<Elist.size();i++){
            Code.add(Elist.get(i).getHomeCode());}

        ArrayList<String> Image =new ArrayList<String>();
        for(int i =0; i<Elist.size();i++){
            Image.add(BitMapToString(Elist.get(i).getHomeImage()));} //이미지를 리스트에 저장할 때 스트링으로 변환하여 담는다.

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
    ///비트맵을 스트링으로 변환해주는 함수
    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();

        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;


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
        if(Elist.get(0).getHomeUser()==("")){
            Elist = new ArrayList<>();
        }

    }


    public void additem(Bitmap image, String user, String date, String text, String code){

        MainHomeItem mhi=new MainHomeItem();


        mhi.setHomeImage(image);
        mhi.setHomeDate(date);
        mhi.setHomeUser(user);
        mhi.setHomeText(text);
        mhi.setHomeCode(code);

        Elist.add(mhi);
    }


    ///리스트로 값을 변환 해주는 메소드
    private ArrayList<String> convertToArray(String string) {

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
        return list;
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
        saveData(); //창이 꺼질때 데이터를 저장해 준다. 주소값참조라 특별히 인텐트로 보내고 넣어주고 할 필요가 없다.
    }
}
