package com.example.bicycle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class MainHomeAdapter extends RecyclerView.Adapter<MainHomeAdapter.ViewHolder> {


    private ArrayList<MainHomeItem> homeitemlist=null;
    private ArrayList<CommentItem> Clist=new ArrayList<CommentItem>(); //코멘트리스트생성 //댓글갯수 표시 위해
    private Context context;
        String CODE;

    public MainHomeAdapter(ArrayList<MainHomeItem> homeitemlist) {
        this.homeitemlist = homeitemlist;
    }

    public interface OnItemClickListener{
        void onItemClick(View v,int position);
        void onSeeClick (View v, int position);
        void onUserImageClick(View v, int position);

    }



    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;


    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView homeUser;
        TextView homeText;
        TextView homeDate;
        ImageView homeImage;
        ImageView homeUserImage;
        Button homesee; // 받아오는 애들이 아닌 더보기 기능
        ImageView home_edt;
        TextView home_Comment;
        public ViewHolder(View itemView) {
            super(itemView);

            homeUser=itemView.findViewById(R.id.home_user);
            homeText=itemView.findViewById(R.id.home_text);
            homeDate=itemView.findViewById(R.id.home_date);
            homeImage=itemView.findViewById(R.id.home_image);
            homesee=itemView.findViewById(R.id.home_see);
            homeUserImage=itemView.findViewById(R.id.home_userImage);
            home_edt=itemView.findViewById(R.id.home_edit);
            home_Comment=itemView.findViewById(R.id.home_ComentCount);

            home_edt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(view, pos) ;
                        }
                    }

                }
            });
            homesee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onSeeClick(view,pos); ;
                        }
                    }
                }
            });

            homeUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            // 리스너 객체의 메서드 호출.
                            if (mListener != null) {
                                mListener.onUserImageClick(view, pos);
                                ;
                            }
                        }
                    }
                });



        }

        void onBind(MainHomeItem data){//온바인드뷰홀더에서도 다 할수 있지만 나눠서 진행해준다. 예제에서 그렇게 하기에. 공부개념으로다가

            homeUser.setText(data.getHomeUser());
            homeDate.setText(data.getHomeDate());
            homeImage.setImageBitmap(data.getHomeImage());
            homeText.setText(data.getHomeText());
            SharedPreferences preferences = context.getSharedPreferences("RegisteredUser", MODE_PRIVATE);



             String ID=homeUser.getText().toString();//바인드되는 아이디는 여러개

            if(ID.equals(DivisionID)){
                home_edt.setVisibility(View.VISIBLE);
            }
            else if(DivisionID.equals("incheol")){
                home_edt.setVisibility(View.VISIBLE);
            }

            else {
                home_edt.setVisibility(View.INVISIBLE);
            }


             String BitmapString=preferences.getString(ID+"Image","");//그중 셰어드를 보고 아이디를 확인 후 이름에 맞는
             Bitmap userImage=StringToBitMap(BitmapString);//이미지를 다시 비트맵으로 변환하고


            if(BitmapString.equals("")){
            }else {
                homeUserImage.setImageBitmap(userImage);//넣어준다.

            }

            CODE=data.getHomeCode();
            Log.e("CODE", CODE );

            Clist.clear();
            loadComment();
            Log.e("CODE", String.valueOf(Clist.size()));

            String size=String.valueOf(Clist.size());

            home_Comment.setText(size+"개");






        }
    }



    @Override
    public MainHomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item,parent,false); //(1)여기서 부모뷰XML를 세팅, 이를 뷰홀더로 넘긴다.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainHomeAdapter.ViewHolder holder, int position) {

       holder.onBind(homeitemlist.get(position));



    }

    @Override
    public int getItemCount() {
        return homeitemlist.size();
    }



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
    private void loadComment() {//댓글리스트를 불러오기 위해
        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        SharedPreferences sharedPreferences = context.getSharedPreferences("CommentData", MODE_PRIVATE);
        //task list라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 null 반환

        String ID = sharedPreferences.getString(CODE+"ID", "");
        String TEXT = sharedPreferences.getString(CODE+"Text", "");
        String Date = sharedPreferences.getString(CODE+"Date", "");


        ///저장할때 String으로 변환한 리스트를 다시 리스트로 변환시킨다.
        convertToArray(ID);
        convertToArray(TEXT);
        convertToArray(Date);



        ///변환된 리스트의 길이는 모두 같으니 한개를 지정해서, 그사이즈만큼 반복하면서 아이템리스트에 다시 값을 넣어준다.
        for (int i = 0; i < convertToArray(ID).size(); i++) {
            addCommentitem(convertToArray(ID).get(i),convertToArray(TEXT).get(i),convertToArray(Date).get(i));
        }//

        //만약 데이터 리스트의 0번째 값의 유저이름이 없다면 리스트를 새로만든다. 처음에 아무것도 없는 아이템이 생겨서 추가함.
        if(Clist.get(0).getCommentID()==("")){
            Clist.clear();
        }

    }
    ///리스트로 값을 변환 해주는 메소드
    private ArrayList<String> convertToArray(String string) {

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
        return list;
    }

    public void addCommentitem(String ID, String Text, String Date){ //코멘트아이템 추가 댓글갯수 불러오기 위해서

        CommentItem item=new CommentItem();


        item.setCommentID(ID);
        item.setCommentText(Text);
        item.setCommentDate(Date);


        Clist.add(item);
    }



}

