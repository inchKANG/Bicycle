package com.example.bicycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder>{

   private ArrayList<MemoItem> memolist=null;
   private Context context;

    public MemoAdapter(ArrayList<MemoItem> memolist) {
        this.memolist = memolist;
    }

     //메인에서 클릭이벤트 사용을 위한 어댑터
    public interface OnItemClickListener{
        Void onItemClick(View v,int position);

    }


    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;


    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    //뷰홀더 객체
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView memotitle;
        TextView memotext;
        TextView memodate;
        View IV;

        public ViewHolder( View itemView) {
            super(itemView);

            memotitle=itemView.findViewById(R.id.memo_title);
            memotext=itemView.findViewById(R.id.memo_text);
            memodate=itemView.findViewById(R.id.memo_date);
            IV=itemView;


            //아이템뷰에서 아이템 클릭 시 커스텀 이벤트 메서드를 호출
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            Toast.makeText(context,"메모를 수정합니다.",Toast.LENGTH_SHORT).show();
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });


        }
        void onBind(MemoItem data){


            memotitle.setText(data.getTitle());
            memotext.setText(data.getText());
            memodate.setText(data.getDate());


        }


    }


    @Override
    public MemoAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        this.context=parent.getContext();//전역변수 context 값 설정

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item,parent,false); //(1)여기서 부모뷰XML를 세팅, 이를 뷰홀더로 넘긴다.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MemoAdapter.ViewHolder holder, final int position) { //여기서 정보를 매칭시킨다. 클릭이벤트를 발생시키려면 여기서 혹은 뷰홀더 내에서 적용해서 가져오는 방법 존재

           holder.onBind(memolist.get(position));


//            holder.IV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Toast.makeText(context,"내용클릭!",Toast.LENGTH_SHORT).show();
////                    Intent intent=new Intent(context,Memo_new.class);
////                    context.startActivity(intent);
//                    mListener.onItemClick(holder,position);
//
//                }
//            });
            holder.IV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    //다이얼로그 띄우기//
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("경고"); //제목세팅
                    builder.setMessage("해당 메모를 삭제하시겠습니까?\n삭제된 메모는 복원되지 않습니다.");//내용세팅

                    //우측버튼 만들기.
                    builder.setPositiveButton("삭제",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context,"내용을 삭제합니다",Toast.LENGTH_SHORT).show();
                                    memolist.remove(position);
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

                   /* Toast.makeText(context,"내용을 삭제합니다",Toast.LENGTH_SHORT).show();
                    memolist.remove(position);
                    notifyDataSetChanged();*/

//                    notifyItemRemoved(position);//제거됨을 알리고
//                    notifyItemRangeChanged(position,memolist.size());//범위가 변경됨을 알려야한다.
                    return true;//true시 클릭끝나면 이벤트완료, false시 이벤트계속진행
                }
            });
    }

    @Override
    public int getItemCount() {
        return memolist.size();
    }






}
