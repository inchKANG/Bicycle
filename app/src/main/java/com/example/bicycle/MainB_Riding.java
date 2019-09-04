package com.example.bicycle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class MainB_Riding extends AppCompatActivity implements OnMapReadyCallback {

    private LinearLayout riding1,riding2,startedOption,exe_time,exe_long;
    private Button EXE_CheckButton,EXE_SavedButton,exe_start,exe_pause,exe_stop,exe_save;
    private TextView riding_time,riding_distance;
    private Boolean isRunning = false;

    private Handler Hand=new Handler();
    private TimeThread timethread=null; //타임쓰레드 객체 선언
    private int t;//시간을 담기 위한 변수 t 선언
    private Bitmap bm=null;




    RecyclerView mRidingRecyclerView=null;
    RidingAdapter mRidingAdapter=null;
    ArrayList<RidingItem> mRidinglist=new ArrayList<RidingItem>();


    //////////지도관련부분///////////////

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 10000;  // 10초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 5000; // 5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;

    //마커 폴리라인
    private double cur_lat,cur_long, bef_lat,bef_long;

    private LatLng ex_point;

    private LatLng current_point;
    int sum_dist = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main_b__riding);
        loadData();

        Toast.makeText(getApplicationContext(),"지도를 불러오는 중입니다.\n 잠시만 기다려 주세요.",Toast.LENGTH_LONG).show();

        //레이아웃매칭
        riding1=findViewById(R.id.riding1); //첫번째 레이아웃
        riding2=findViewById(R.id.riding2); //리사이클러뷰 레이아웃
        startedOption=findViewById(R.id.startedOption); //측정시작 누른 이후 나오는 메뉴
        exe_time=findViewById(R.id.exe_time);   //측정시작 이후 시간쟤는부분
        exe_long=findViewById(R.id.exe_long);   //측정시작 이후 거리쟤는부분

        //버튼매칭
        EXE_CheckButton=findViewById(R.id.EXE_CheckButton); //측정하기버튼
        EXE_SavedButton=findViewById(R.id.EXE_SavedButton); //기록확인버튼
        exe_start=findViewById(R.id.exe_start); //측정시작버튼
        exe_pause=findViewById(R.id.exe_pause); //일시정지버튼
        exe_stop=findViewById(R.id.exe_stop);   //중지버튼
        exe_save=findViewById(R.id.exe_save);   //저장버튼

        //텍스트뷰 매칭
        riding_time =findViewById(R.id.riding_time);    //시간나오는부분
        riding_distance=findViewById(R.id.riding_distance); //거리나오는부분




        riding_distance.setText("0");

        //리사이클러뷰 설정
        mRidingRecyclerView=findViewById(R.id.ridingRecycler);
        mRidingAdapter=new RidingAdapter(mRidinglist);
        mRidingRecyclerView.setAdapter(mRidingAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRidingRecyclerView.setLayoutManager(mLayoutManager);

        mRidingAdapter.setOnItemClickListener(new RidingAdapter.onRidingItemClickListener() {
            @Override
            public void onRidingItemClicked(RidingItem model) {


            }
        });










        //측정, 기록 버튼 클릭시

        EXE_CheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                riding1.setVisibility(View.VISIBLE);
                riding2.setVisibility(View.INVISIBLE);


            }
        });

        EXE_SavedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                riding1.setVisibility(View.INVISIBLE);
                riding2.setVisibility(View.VISIBLE);
            }
        });


        //운동시작버튼 클릭 시 //TODO  운동시작버튼
        exe_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"운동이 시작됩니다",Toast.LENGTH_SHORT).show();
                mMap.clear();//맵의 모든 마커를 지우기
                exe_start.setVisibility(View.GONE);
                startedOption.setVisibility(View.VISIBLE);
                exe_time.setVisibility(View.VISIBLE);
                exe_long.setVisibility(View.VISIBLE);
                isRunning=true;

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);

                if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED ) {
                    Log.d("GPS사용", "찍힘 : " );


                    double latitude = location.getLatitude();//위도
                    double longitude = location.getLongitude();//경도
                    LatLng latLng = new LatLng(latitude, longitude);
                    ex_point=latLng;
                    Log.e("로그이", String.valueOf(latLng));
                    //마커설정

                    MarkerOptions optFirst = new MarkerOptions();

                    optFirst.anchor(0.5f, 0.5f);

                    optFirst.position(latLng);// 위도 • 경도

                    optFirst.title("라이딩 시작지점");

                    mMap.addMarker(optFirst).showInfoWindow();

                    bef_lat = latitude;
                    bef_long = longitude;



                    timethread = new TimeThread();
                    timethread.start();//타임쓰레드 시작



                }
            }
        });


        //중지버튼 클릭 시 //TODO  중지버튼
        exe_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ad = new AlertDialog.Builder(MainB_Riding.this);

                ad.setMessage("정말로 중지하시겠습니까?\n중지시 데이터는 저장되지 않습니다.");   // 내용 설정
                ad.setCancelable(false); //다이알로그가 화면 밖을 눌러도 꺼지지 않도록

                isRunning=false;//잠시 일시정지 상태로 바꿔준다.

                // 확인 버튼 설정
                ad.setPositiveButton("중지하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        Toast.makeText(getApplicationContext(),"운동을 중지합니다.",Toast.LENGTH_SHORT).show();

                        exe_start.setVisibility(View.VISIBLE);
                        startedOption.setVisibility(View.GONE);
                        exe_time.setVisibility(View.GONE);
                        exe_long.setVisibility(View.GONE);

                        double latitude=location.getLatitude();//위도
                        double longitude=location.getLongitude();//경도

                        LatLng latLng=new LatLng(latitude,longitude);

                        MarkerOptions optFirst = new MarkerOptions();

                        optFirst.anchor(0.5f, 0.5f);

                        optFirst.position(latLng);// 위도 • 경도

                        optFirst.title("라이딩 중지지점");

                        mMap.addMarker(optFirst).showInfoWindow();



                        sum_dist = 0;// 총 라이딩 거리

                        riding_distance.setText("0 m");
                        riding_time.setText("00:00:00"); //시간초기화 시켜준다.
                        timethread.interrupt();         //현재 실행중인 스레드 종료
                        dialog.dismiss();     //닫기

                        // Event
                    }
                });


                // 취소 버튼 설정
                ad.setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"운동을 계속합니다.",Toast.LENGTH_SHORT).show();
                        isRunning=true;//다시 true상태로 변환
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });



                // 창 띄우기
                ad.show();


            }
        });
        //일시정지 버튼 클릭 시 쓰레드는 계속살아서 동작하지만 아무것도 하지 않도록한다.
        exe_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning=!isRunning;
                if(isRunning) { //달리는 도중일시
                    Toast.makeText(getApplicationContext(), "운동을 계속해서 수행합니다.", Toast.LENGTH_SHORT).show();
                    exe_pause.setText("일시정지");
                }
                else{       //일시정지상태일시
                    Toast.makeText(getApplicationContext(), "운동을 일시정지합니다.\n일시정지 상태에서 이동량이 많으면\n측정이 정확하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show();
                    exe_pause.setText("계속");
                }


            }
        });

        //저장버튼 클릭 시
            //TODO  기록버튼
        exe_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double latitude=location.getLatitude();//위도
                double longitude=location.getLongitude();//경도

                LatLng latLng=new LatLng(latitude,longitude);

                MarkerOptions optFirst = new MarkerOptions();

                optFirst.anchor(0.5f, 0.5f);

                optFirst.position(latLng);// 위도 • 경도

                optFirst.title("라이딩 기록지점");

                mMap.addMarker(optFirst).showInfoWindow();




//                final int w     = riding1.getWidth();
//                final int h     = riding1.getHeight();
//
//                view.clearFocus();
//                view.setPressed(false);
//                view.invalidate();

               /* Bitmap bm = Bitmap.createBitmap(riding1.getWidth(), riding1.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                Drawable bgDrawable = riding1.getBackground();
                if (bgDrawable != null) {
                    bgDrawable.draw(canvas);
                } else {
                    canvas.drawColor(Color.WHITE);
                }
                riding1.draw(canvas);*/ //이거대로 한다면 지도가 안찍힌다.



                GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {

                        long now = System.currentTimeMillis ();
                        Date date=new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH시 mm분");
                        String getTime = sdf.format(date);

                        Toast.makeText(getApplicationContext(),"지금까지의 기록이 저장됩니다.",Toast.LENGTH_SHORT).show();//이부분에는 리스트에 저장하는 부분을 넣어야한다.
                        additem(bitmap,getTime,riding_time.getText().toString(),riding_distance.getText().toString());
                        mRidingAdapter.notifyDataSetChanged();
                    }
                };

                mMap.snapshot(callback);
               // Log.e("bm이미지2", String.valueOf(bm));



            }
        });

        //////////지도관련부분
        mLayout=findViewById(R.id.riding1);

        locationRequest = new LocationRequest()     //현재 위치를 요청
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);///현재 위치를 가져온다.


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);    //지정한 아이디를 갖는 프래그먼트의 핸들 가져옴

        mapFragment.getMapAsync(this); /*구글맵 객체가 준비될 때 실행될 콜백등록. 이를 위해 프래그먼트 핸들 가져와야함.
                                                               반드시 메인에서 호출되어야 한다.*/






    }////////onCreate끝



    //시간을 쟤는 쓰레드//TODO 쓰레드
    private class TimeThread extends Thread{

        @Override
        public void run() {
            t=0;
            super.run();
            while (true){
                while(isRunning){//일시정지 누르면 멈추도록
                    t++;
                    Log.e("GPS사용", "찍힘 : " + t);
                    Hand.post(new Runnable() {
                        @Override
                        public void run() {
                            int sec=t%60; //60초 될때마다 초기화
                            int min=(t/60)%60; //
                            int hour=(t/3600);
                            String result=String.format("%02d:%02d:%02d", hour,min,sec);
                            riding_time.setText(result);
                            if(sum_dist<1000){
                                riding_distance.setText(sum_dist+ " m");}
                            else{
                                riding_distance.setText(((double)sum_dist/1000)+ " km");
                            }

                            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION);
                            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION);

                            if(t%3==0){
                                if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED ){


                                    double latitude=location.getLatitude();//위도
                                    double longitude=location.getLongitude();//경도

                                    cur_lat=latitude;
                                    cur_long=longitude;



                                    //이전의 정보와 현재의 정보로 거리를 구한다.

                                    CalDistance calDistance=new CalDistance(bef_lat,bef_long,cur_lat,cur_long);

                                    double dist=calDistance.getDistance();

                                    dist=(int)(dist*100)/100.0;// 소수점 둘째 자리 계산


                                    sum_dist+=dist;

                                    bef_long=cur_long;
                                    bef_lat=cur_lat;

                                    LatLng latlng=new LatLng(latitude,longitude);


                                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

                                    //  mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                                    //이전과 현재의 포인트로 폴리라인을 긋는다

                                    LatLng CurrentPoint=latlng;
                                    mMap.addPolyline(new PolylineOptions().color(0xFFFF0000).width(15.0f).geodesic(true).add(latlng).add(ex_point));

                                    ex_point = latlng;



                                    // 마커 설정.

                                    MarkerOptions optFirst = new MarkerOptions();

                                    optFirst.alpha(0.5f);

                                    optFirst.anchor(0.5f, 0.5f);
                                    optFirst.visible(false);
                                    optFirst.position(latlng);// 위도 • 경도



                                    mMap.addMarker(optFirst).showInfoWindow();








                                }

                            }




                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;//인터럽트 받을 경우 리턴? 무슨의민지 잚 모르겠다.
                    }

                }



            }
        }
    }

    public void additem(Bitmap image, String date, String time, String distance){

        RidingItem item=new RidingItem();


        item.setSavedMap(image);
        item.setSavedDate(date);
        item.setSavedTime(time);
        item.setSavedDistance(distance);


        mRidinglist.add(item);
    }


    /////////////////////////////저장과 관련된 부분/////////////////////////////////////////////////
    private void saveData() {
        // Activity를 저장한다.
        //SharedPreferences를
        SharedPreferences sharedPreferences = getSharedPreferences(DivisionID+"Riding", MODE_PRIVATE);
        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();


        ///제목, 내용, 날짜, 유저이름을 담을 리스트를 새로 만든다. 그리고 각 요소를 담아준다.
        ArrayList<String> Date=new ArrayList<String>();
        for(int i =0; i<mRidinglist.size();i++){
            Date.add(mRidinglist.get(i).getSavedDate());}

        ArrayList<String> Time=new ArrayList<String>();
        for(int i =0; i<mRidinglist.size();i++){
            Time.add(mRidinglist.get(i).getSavedTime());}

        ArrayList<String> Distance =new ArrayList<String>();
        for(int i =0; i<mRidinglist.size();i++){
            Distance.add(mRidinglist.get(i).getSavedDistance());}

        ArrayList<String> Image =new ArrayList<String>();
        for(int i =0; i<mRidinglist.size();i++){            //여기서 용량감소시켜야한다.
            Image.add(BitMapToString(mRidinglist.get(i).getSavedMap()));} //이미지를 리스트에 저장할 때 스트링으로 변환하여 담는다.

        //리스트를 문자열로 변환//
        convertToString(Date);
        convertToString(Time);
        convertToString(Distance);
        convertToString(Image);

//     editor.putString(key,value); //key, value를 이용하여 저장하는 형태
        //에디터로 키값으로 데이터를 저장, 문자열로 변환한 데이터를 넣어준다.//
        editor.putString("Date",convertToString(Date));
        editor.putString("Time",convertToString(Time));
        editor.putString("Distance",convertToString(Distance));
        editor.putString("Image",convertToString(Image));


        editor.apply(); //최종 저장 editor.apply();

    }

    private void loadData() {
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sharedPreferences = getSharedPreferences(DivisionID+"Riding", MODE_PRIVATE);
        //task list라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 null 반환

        String Date = sharedPreferences.getString("Date", "");
        String Time = sharedPreferences.getString("Time", "");
        String Distance = sharedPreferences.getString("Distance", "");
        String Image = sharedPreferences.getString("Image", "");

        ///저장할때 String으로 변환한 리스트를 다시 리스트로 변환시킨다.
        convertToArray(Date);
        convertToArray(Time);
        convertToArray(Distance);
        convertToArray(Image);


        ///변환된 리스트의 길이는 모두 같으니 한개를 지정해서, 그사이즈만큼 반복하면서 아이템리스트에 다시 값을 넣어준다.
        for (int i = 0; i < convertToArray(Time).size(); i++) {
            additem(StringURIToBitmap(convertToArray(Image).get(i)),convertToArray(Date).get(i),convertToArray(Time).get(i),convertToArray(Distance).get(i));
        }//

        //만약 데이터 리스트의 0번째 값의 유저이름이 없다면 리스트를 새로만든다. 처음에 아무것도 없는 아이템이 생겨서 추가함.
        if(mRidinglist.get(0).getSavedDate()==("")){
            mRidinglist = new ArrayList<>();
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

    /*
    지도와 관련된 부분

    */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MainB_Riding.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            // Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            //  Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            //  Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.nowbicycle);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.nowbicycle);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainB_Riding.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    @Override
    protected void onPause() { //화면 이동할때 다음 화면에 데이터를 보여주기 위해서 무조건 이때 저장해야 한다.
        super.onPause();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isRunning){//쓰레드가 동작중이면
            timethread.interrupt();
        }

    }
}
