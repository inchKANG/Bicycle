package com.example.bicycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//어뎁터
//뷰홀더: 뷰들을 홀더에 꼽아놓듯 보관하는 객체

//중고거래 리사이클러뷰 어댑터
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    Context context;


    //shopData가 들어갈 리스트 생성
    private ArrayList<ShopData> mShopData=null;
    //생성자에서 데이터 리스트 객체를 전달받음
    public ShopAdapter(ArrayList<ShopData> mShopData) {
        this.mShopData = mShopData;
    }

    public interface OnItemClickListener{
        Void onItemClick(View v,int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    //아이템 클릭 리스너 생성
    //onCreateViewHolder 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    //inflater을 통해 하나의 객체처럼 만들 수 있다.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item,parent,false); //(1)여기서 부모뷰XML를 세팅, 이를 뷰홀더로 넘긴다.
        return new ViewHolder(view);

    }


    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    //(3) 새로 아이템이 추가될때마다 매칭작업이 반복되겠지. 그런데 그러한 매칭을 해주는 애가 온바인드뷰홀더다.
    //뷰홀더가 반복되면서 온바인드뷰홀더에서 셋팅!!
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //shopData클래스의 객체, 여기서 각 데이터를 위치에 따라 하나씩 매칭한다.
       ShopData shopdata=mShopData.get(position);
        //뷰홀더에 만들어진 객체들을 참조
        holder.shop_title.setText("제목: "+shopdata.getShop_title());
        holder.shop_contents.setText(shopdata.getShop_contents());
        holder.shop_date.setText("작성시간: "+ shopdata.getShop_date());
        holder.shop_user.setText("작성자: "+ shopdata.getShop_user());

        ///길게클릭시 아이템 제거///
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Toast.makeText(context,"내용을 삭제합니다",Toast.LENGTH_SHORT).show();
//                mShopData.remove(position);
//                notifyDataSetChanged();
//

                //다이얼로그 띄우기//
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("경고"); //제목세팅
                builder.setMessage("해당 게시물을 삭제하시겠습니까?\n삭제된 게시물은 복원되지 않습니다.");//내용세팅

                //우측버튼 만들기.
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"게시물을 삭제합니다",Toast.LENGTH_SHORT).show();
                                mShopData.remove(position);
                                notifyDataSetChanged();

                            }
                        });
                //좌측버튼 만들기
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();



                return true;//true시 클릭끝나면 이벤트완료, false시 이벤트계속진행

            }
        });

    }


    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mShopData.size();
    }


    //뷰홀더 만들기 아이템뷰를 저장한다.
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView shop_title;
        TextView shop_contents;
        TextView shop_user;
        TextView shop_date;
        ImageView shop_image;

        public ViewHolder(View itemView) {
            super(itemView);
           //(2) 넘김받음, inflater에서 셋팅한 부모뷰를 여기서 화면과 매칭시킨다. 새로 생길때마다 반복되면서 매칭되겠지

            shop_title=itemView.findViewById(R.id.shop_title);
            shop_contents=itemView.findViewById(R.id.shop_contents);
            shop_user=itemView.findViewById(R.id.shop_user);
            shop_date=itemView.findViewById(R.id.shop_date);
            shop_image=itemView.findViewById(R.id.shop_image);


            //아이템뷰에서 아이템 클릭 시 커스텀 이벤트 메서드를 호출
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            Toast.makeText(context,"수정",Toast.LENGTH_SHORT).show();
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });


        }
    }


}
