package com.example.bicycle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bicycle.CommentActivity.id1;
import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
 //어댑터이름, 뷰홀더이름

    private Context context;


    //인터페이스-------------------------------------------
   public interface onCommentClickListener {
        void onCommentClicked(CommentItem model);//아이템이름
        void onXClicked(CommentItem model);
        void onEditClicked(CommentItem model);
    }

    private onCommentClickListener mListener;

    public void setOnItemClickListener(onCommentClickListener listener) {
        this.mListener = listener ;
    }

    //인터페이스-------------------------------------------

    private ArrayList<CommentItem> mItems = new ArrayList<>(); //아이템리스트생성

    public CommentAdapter(ArrayList<CommentItem> mItems) { ///생성자생성
        this.mItems = mItems;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        final CommentViewHolder viewHolder = new CommentViewHolder(view);
        //아이템클릭
        view.setOnClickListener(new View.OnClickListener() { //뷰홀더의 아이템뷰 클릭 시 만약 오류나면 ViewHolder.ItemView.setonClickListener로 바꿔보자.
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    final CommentItem item = mItems.get(viewHolder.getAdapterPosition());
                    mListener.onCommentClicked(item);
                }
            }
        });
        //X버튼클릭
        viewHolder.comment_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    final CommentItem item = mItems.get(viewHolder.getAdapterPosition());
                    mListener.onXClicked(item);
                }
            }
        });
        //수정버튼클릭
        viewHolder.comment_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    final CommentItem item = mItems.get(viewHolder.getAdapterPosition());
                    mListener.onEditClicked(item);
                }
            }
        });



        return viewHolder;



    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem item = mItems.get(position);

        holder.Comment_ID.setText(item.getCommentID());
        holder.Comment_Date.setText(item.getCommentDate());
        holder.Comment_Text.setText(item.getCommentText());


        String id=holder.Comment_ID.getText().toString();

        SharedPreferences preferences = context.getSharedPreferences("RegisteredUser", MODE_PRIVATE);
        String BitmapString=preferences.getString(id+"Image","");//그중 셰어드를 보고 아이디를 확인 후 이름에 맞는
        Bitmap userImage=StringToBitMap(BitmapString);//이미지를 다시 비트맵으로 변환하고


        if(BitmapString.equals("")){
        }else {
            holder.Comment_image.setImageBitmap(userImage);//넣어준다.

        }


        ///Comment_ID: 댓글아이디, id1: CommentActivity들어갈때 인텐트로 보내주는 그 글의 글쓴이 아이디. DivisionID 로그인 아이디/
        if(holder.Comment_ID.getText().toString().equals(DivisionID)){ //댓글아이디가 로그인아이디와 같다면
        holder.comment_edt.setVisibility(View.VISIBLE);}    //수정버튼활성화
        else{
            holder.comment_edt.setVisibility(View.INVISIBLE); //아니면 비활성화
        }
        if(id1.equals(DivisionID)||holder.Comment_ID.getText().toString().equals(DivisionID)){ //글쓴이 아이디가 내아이디와 같거나 댓글 아이디가 내아이디와 같다면 보이게
            holder.comment_x.setVisibility(View.VISIBLE);
        }
        else {
            holder.comment_x.setVisibility(View.INVISIBLE); //아니라면 안보이게
        }








    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder { //만약 뭔가 오류가있다면 public static class로 바꿔보자.
        ImageView Comment_image, comment_edt, comment_x;
        TextView Comment_ID, Comment_Text, Comment_Date;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            Comment_ID=itemView.findViewById(R.id.Comment_ID);
            Comment_Text=itemView.findViewById(R.id.Comment_Text);
            Comment_Date=itemView.findViewById(R.id.Comment_Date);
            Comment_image=itemView.findViewById(R.id.Comment_image);
            comment_edt=itemView.findViewById(R.id.comment_edt);
            comment_x=itemView.findViewById(R.id.comment_x);



        }
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

}
