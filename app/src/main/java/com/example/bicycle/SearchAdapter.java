package com.example.bicycle;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bicycle.MainHomeActivity1.DivisionID;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{


    //어댑터이름, 뷰홀더이름

    //인터페이스-------------------------------------------
   public interface onSearchItemClickListener {
        void onSearchItemClicked(MainHomeItem model);//아이템이름
    }

    private onSearchItemClickListener mListener;

    public void setOnItemClickListener(onSearchItemClickListener listener) {
        this.mListener = listener ;
    }

    //인터페이스-------------------------------------------

    //리스트생성
    private ArrayList<MainHomeItem> filteredlist = new ArrayList<>(); //필터된 리스트
    private ArrayList<MainHomeItem> unfilteredlist = new ArrayList<>(); //필터되지 않은 리스트
    private ArrayList<CommentItem> Clist=new ArrayList<CommentItem>(); //코멘트리스트생성 //댓글갯수 표시 위해

    Context context;
    String CODE;
    public SearchAdapter(ArrayList<MainHomeItem> list) { ///생성자생성
        this.filteredlist=list;
        this.unfilteredlist.addAll(list); // 깊은복사와 얕은 복사의 개념
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        final SearchViewHolder viewHolder = new SearchViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() { //뷰홀더의 아이템뷰 클릭 시 만약 오류나면 ViewHolder.ItemView.setonClickListener로 바꿔보자.
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    final MainHomeItem item = filteredlist.get(viewHolder.getAdapterPosition());
                    mListener.onSearchItemClicked(item);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        MainHomeItem item = filteredlist.get(position);



            holder.Search_name.setText(item.getHomeUser());
            holder.Search_date.setText(item.getHomeDate());
            holder.Search_Text.setText(item.getHomeText());
            holder.Search_image.setImageBitmap(item.getHomeImage());

        CODE=item.getHomeCode();
        Log.e("CODE", CODE );

        Clist.clear();
        loadComment();
        Log.e("CODE", String.valueOf(Clist.size()));

        String size=String.valueOf(Clist.size());

        holder.SearchComentCount.setText(size+"개");



    }

    @Override
    public int getItemCount() {
        return filteredlist.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder { //만약 뭔가 오류가있다면 public static class로 바꿔보자.
        TextView Search_name;
        TextView Search_date;
        TextView Search_Text;
        ImageView Search_image;
        TextView SearchComentCount;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            Search_name=itemView.findViewById(R.id.Search_name);
            Search_date=itemView.findViewById(R.id.Search_date);
            Search_Text=itemView.findViewById(R.id.search_Text);
            Search_image=itemView.findViewById(R.id.Search_image);
            SearchComentCount=itemView.findViewById(R.id.SearchComentCount);
        }
    }



    public void filterID(String text){ /*필터시 중요한 것은 리스트를 생성자에서 받을 시 =가 아닌 add로 해줘야 한다.
                                         =로 할때는 얕은복사. 같은주소참조됨, 필터리스트 수정시 필터안된리스트도 수정된다*/

    this.filteredlist.clear();
    if(text.length()==0){           //필터에 아무입력이 없다면
        filteredlist.addAll(unfilteredlist);
        Log.e("필터리스트", String.valueOf(filteredlist.size()));
        Log.e("언필터리스트", String.valueOf(unfilteredlist.size()));
    }
    else
    {
        for( MainHomeItem item : unfilteredlist) //필터안된 리스트에서 하나씩 뺴주고
        {

            if(item.getHomeUser().contains(text))   //만약 유저이름이 텍스트에 포함되어있다면
            {
                filteredlist.add(item);             //아이템에 추가

            }
        }
    }
        notifyDataSetChanged();
    }

    public void filterOnlyID(String text){ //정확한 아이디로 필터, 음 친구목록에서 빼올때나, 자신이 쓴 글
        notifyDataSetChanged();
        this.filteredlist.clear();
        if(text.length()==0){           //필터에 아무입력이 없다면
            filteredlist.addAll(unfilteredlist);
            Log.e("필터리스트", String.valueOf(filteredlist.size()));
            Log.e("언필터리스트", String.valueOf(unfilteredlist.size()));


        }
        else
        {
            for( MainHomeItem item : unfilteredlist) //필터안된 리스트에서 하나씩 뺴주고
            {

                if(item.getHomeUser().equals(text))   //만약 유저이름이 텍스트와 같다면
                {
                    filteredlist.add(item);             //아이템에 추가

                }
            }
        }
        notifyDataSetChanged();
    }


    public void filterall(String text){// 모든 값에서 필터
        notifyDataSetChanged();
        this.filteredlist.clear();
        if(text.length()==0){           //필터에 아무입력이 없다면
            filteredlist.addAll(unfilteredlist);
        }
        else
        {
            for( MainHomeItem item : unfilteredlist) //필터안된 리스트에서 하나씩 뺴주고
            {

                if(item.getHomeUser().contains(text)||item.getHomeText().contains(text))   //만약 유저이름이 텍스트에 포함되어있다면
                {
                    filteredlist.add(item);             //아이템에 추가

                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterText(String text){// 모든 값에서 필터
        notifyDataSetChanged();
        this.filteredlist.clear();
        if(text.length()==0){           //필터에 아무입력이 없다면
            filteredlist.addAll(unfilteredlist);
        }
        else
        {
            for( MainHomeItem item : unfilteredlist) //필터안된 리스트에서 하나씩 뺴주고
            {

                if(item.getHomeText().contains(text))   //만약 유저이름이 텍스트에 포함되어있다면
                {
                    filteredlist.add(item);             //아이템에 추가

                }
            }
        }
        notifyDataSetChanged();
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
