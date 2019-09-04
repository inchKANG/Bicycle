package com.example.bicycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainHome_add extends AppCompatActivity {

    private Button home_Share;
    private ImageView home_addImage,home_addbycamera,home_addbygallary;
    private EditText home_addText;
    private static final int Request_image_capture=465;
    private String ImageFilePath;
    private Uri PhotoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_add);

        //권한체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();

        home_Share=findViewById(R.id.home_Share);
        home_addImage=findViewById(R.id.home_addImage);
        home_addbycamera=findViewById(R.id.home_addbycamera); //카메라는 아직
        home_addbygallary=findViewById(R.id.home_addbygallary);
        home_addText=findViewById(R.id.home_addText);


        SharedPreferences sp=getSharedPreferences("HomeAddData",MODE_PRIVATE);
       final SharedPreferences.Editor editor=sp.edit();





        //공유버튼 클릭시
        home_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(home_addImage.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"사진을 등록하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(home_addText.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"내용을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent();


                /*3. 게시글 작성이 완료되면 SharedPreference에 텍스트와 이미지 값을 저장해 준다. 그 후 홈액티비티로 다시 돌아가면
                        거기서 다시 셰어드값을 꺼내서 세팅해줘야겠지?*/
                setResult(RESULT_OK,intent);
                Toast.makeText(getApplicationContext(),"게시물 작성이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                //비트맵으로 이미지를 저장하는 방법
                Bitmap bm=((BitmapDrawable)home_addImage.getDrawable()).getBitmap();// 이미지뷰에서 비트맵 가져오기,
                Log.e("비트맵사이즈", String.valueOf(bm.getByteCount()));
                Bitmap resized;//비트맵 용량 줄이기 만약 가로세로가 512보다 크다면 나누기4하고 아니면 그대로/.

                if(bm.getByteCount()>1000000){ //비트맵  용량이 1메가가 넘으면 가로세로를 나누기 4해서 다시만들고
                    resized=Bitmap.createScaledBitmap(bm,bm.getWidth()/2,bm.getHeight()/2,true);
                    if(resized.getByteCount()>1000000){ //바꾼것도 크면 다시줄인다. //깨지지만 일단 이렇게하자.
                        resized=Bitmap.createScaledBitmap(bm,bm.getWidth()/4,bm.getHeight()/4,true);
                    }
                }
//                else if(bm.getByteCount()>2000000){
//                    resized=Bitmap.createScaledBitmap(bm,bm.getWidth()/4,bm.getHeight()/4,true);
//                }
                else{ //아니면 그대로의 사이즈
                    resized=bm;
                }


                Log.e("비트맵사이즈", String.valueOf(resized.getByteCount()));
                String BitSTR=BitMapToString(resized);//가져온 비트맵을 문자열로 변환하기

                editor.putString("Image",BitSTR); //이미지 비트맵 값을 String으로 변환.
                editor.putString("Text",home_addText.getText().toString());//입력한 글자값
                editor.apply();
                finish();
            }
        });


        //카메라로 사진 찍기
        home_addbycamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null){
                    File photoFile=null;
                    try {
                        photoFile=createImageFile();
                    }catch (IOException e){

                    }

                    if(photoFile!=null){
                        PhotoUri= FileProvider.getUriForFile(getApplicationContext(),getPackageName(),photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,PhotoUri);
                        startActivityForResult(intent,Request_image_capture);
                    }
                }
            }
        });


        //갤러리에서 사진 불러오기 1. 이미지를 선택한다.
        home_addbygallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //그림을 불러온다.
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),777);
            }
        });

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

    ///비트맵을 스트링으로 변환해주는 함수
    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();

        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                ////2. 이미지를 불러온다.
        ///갤러리에서 보낸 이미지를 받기
        if(requestCode==777&&resultCode==RESULT_OK&&data!=null){
            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                // 이미지 표시
                home_addImage.setImageBitmap(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
          /*//glide 사용
           Imageuri=data.getData();

            Glide.with(getApplicationContext()).load(Imageuri.toString()).into(home_addImage);//Glide.with(this).load("이미지 URL" or "이미지 리소스").into(이미지뷰);*/
        }
            //카메라에서 보낸 이미지 받기
        if(requestCode==Request_image_capture&&resultCode==RESULT_OK){
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

            ((ImageView)findViewById(R.id.home_addImage)).setImageBitmap(rotate(bitmap,exitDegree));

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


}
