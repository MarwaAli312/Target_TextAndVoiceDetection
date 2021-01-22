package com.example.detectiontext.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.detectiontext.HistoryItemClickListener;
import com.example.detectiontext.R;
import com.example.detectiontext.models.ImgTT;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class ImgTTAdapter extends RecyclerView.Adapter<ImgTTAdapter.MyHolder> implements Filterable {
    Context context;
    ArrayList<ImgTT> data,datafull;
    HistoryItemClickListener onImgTTListener;


    public ImgTTAdapter(Context context, ArrayList<ImgTT> data,HistoryItemClickListener onImgTTListener) {
        this.context = context;
        this.onImgTTListener=onImgTTListener;
        this.data=data;
        datafull=new ArrayList<ImgTT>();
        Iterator<ImgTT> iterator = data.iterator();
        while(iterator.hasNext()){
            ImgTT item=iterator.next();
            ImgTT toadd=new ImgTT(item.getText(),item.getImage(),item.getTimestamp());
            datafull.add(toadd);
        }

    }

    @NonNull
    @Override
    public ImgTTAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_to_text, parent, false);
        return new MyHolder(view,onImgTTListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgTTAdapter.MyHolder holder, int position) {

        ImgTT item=data.get(position);
        holder.edtext.setText(item.getText());

        Glide.with(context)
                .load(item.getImage())
                .into(holder.imageView);


        String time=item.getTimestamp();
        //convert timestamp to DateTime dd/mm/yyyy hh:mm:aa
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm:aa",cal).toString();

        holder.edtimestamp.setText(dateTime);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Context getContext() {
        return context;
    }



    public ArrayList<ImgTT> getData() {
        return data;
    }



    public void removeItem(int position) {
        ImgTT mRecentlyDeletedItem=data.get(position);
        int mRecentlyDeletedItemPosition=position;
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(ImgTT item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }



    class MyHolder extends  RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        ImageView imageView;
        TextView edtext,edtimestamp;
        HistoryItemClickListener onImgTTListener;
        CardView cardView;

        public MyHolder(@NonNull View itemView, HistoryItemClickListener onImgTTListener) {

            super(itemView);
            imageView=itemView.findViewById(R.id.image_item);
            edtext=itemView.findViewById(R.id.text_item);
            edtimestamp=itemView.findViewById(R.id.timestamp_item);
            cardView=itemView.findViewById(R.id.cardview);

            this.onImgTTListener=onImgTTListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onImgTTListener.onHistoryItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onImgTTListener.onLongClick(getAdapterPosition());
            return false;
        }
    }

    @Override
    public Filter getFilter() {
        return imgttfilter;
    }

    private Filter imgttfilter =new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ImgTT> filteredlist= new ArrayList<ImgTT>();
            if(constraint ==null || constraint.length()==0){
                filteredlist.addAll(datafull);
            } else {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(ImgTT item: datafull){
                    if (item.getText().toLowerCase().contains(filterPattern)){
                        filteredlist.add(item);
                    }
                }
            }


            FilterResults results=new FilterResults();
            results.values=filteredlist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }


    };


}
