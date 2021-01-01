package com.dip.squadsecurity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{

    private List<SearchGuardList> person;
    private Context context;


      public MyRecyclerViewAdapter(List<SearchGuardList> person,Context context){
        this.person=person;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final SearchGuardList SGList= person.get(position);
        holder.txtname.setText(SGList.getName());
        holder.txtphno.setText(SGList.getPhno());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context=view.getContext();
                Intent intent=new Intent();
                        intent =  new Intent(context, DetailViewForm.class);
                        intent.putExtra("name",SGList.getName());
                        intent.putExtra("contact",SGList.getPhno());
                        intent.putExtra("uid",SGList.getUid());
                        context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return person.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtname,txtphno;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtname=(TextView) itemView.findViewById(R.id.textViewsearchname);
            txtphno=(TextView) itemView.findViewById(R.id.textViewsearchno);
        }


    }

}
