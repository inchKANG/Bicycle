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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RidingAdapter extends RecyclerView.Adapter<RidingAdapter.RidingViewHolder> {
 //어댑터이름, 뷰홀더이름
    private Context context;

    //인터페이스-------------------------------------------
   public interface onRidingItemClickListener {
        void onRidingItemClicked(RidingItem model);//아이템이름
    }

    private onRidingItemClickListener mListener;

    public void setOnItemClickListener(onRidingItemClickListener listener) {
        this.mListener = listener ;
    }

    //인터페이스-------------------------------------------

    private ArrayList<RidingItem> mItems = new ArrayList<>(); //아이템리스트생성

    public RidingAdapter(ArrayList<RidingItem> mItems) { ///생성자생성
        this.mItems = mItems;
    }


    @NonNull
    @Override
    public RidingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.riding_item, parent, false);
        final RidingViewHolder viewHolder = new RidingViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    final RidingItem item = mItems.get(viewHolder.getAdapterPosition());
                    mListener.onRidingItemClicked(item);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RidingViewHolder holder, final int position) {
        RidingItem item = mItems.get(position);
        holder.Map.setImageBitmap(item.getSavedMap());

        holder.date.setText(item.getSavedDate());
        holder.time.setText(item.getSavedTime());
        holder.distance.setText(item.getSavedDistance());

        holder.ItemVIew.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //다이얼로그 띄우기//
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("해당 기록을 삭제하시겠습니까?\n삭제된 기록은 복원되지 않습니다.");//내용세팅

                //우측버튼 만들기.
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"기록을 삭제합니다",Toast.LENGTH_SHORT).show();
                                mItems.remove(position);
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

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class RidingViewHolder extends RecyclerView.ViewHolder { //만약 뭔가 오류가있다면 public static class로 바꿔보자.
        ImageView Map;
        TextView date;
        TextView time;
        TextView distance;
        View ItemVIew;

        public RidingViewHolder(@NonNull View itemView) {
            super(itemView);
            Map=itemView.findViewById(R.id.RSavedImage);
            date= itemView.findViewById(R.id.RSavedDate);
            time=itemView.findViewById(R.id.RSavedTime);
            distance=itemView.findViewById(R.id.RSavedDistance);

            ItemVIew=itemView;

        }
    }
}
