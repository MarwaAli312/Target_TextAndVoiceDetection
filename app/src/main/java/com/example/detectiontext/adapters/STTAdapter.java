package com.example.detectiontext.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detectiontext.HistoryItemClickListener;
import com.example.detectiontext.R;
import com.example.detectiontext.models.STT;
import com.example.detectiontext.models.STT;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class STTAdapter extends RecyclerView.Adapter<STTAdapter.MyHolder> implements Filterable {
    Context context;
    ArrayList<STT> data,datafull;
    HistoryItemClickListener onSTTListener;


    public STTAdapter(Context context, ArrayList<STT> data, HistoryItemClickListener onSTTListener) {
        this.context = context;
        this.onSTTListener=onSTTListener;
        this.data = data;
        datafull=new ArrayList<STT>();
        Iterator<STT> iterator = data.iterator();
        while(iterator.hasNext()){
            STT item=iterator.next();
            STT toadd=new STT(item.getText(),item.getTimestamp());
            datafull.add(toadd);
        }
    }

    @NonNull
    @Override
    public STTAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_speech_to_text, parent, false);
        return new STTAdapter.MyHolder(view,onSTTListener);
    }

    @Override
    public void onBindViewHolder(@NonNull STTAdapter.MyHolder holder, int position) {

        String text=data.get(position).getText();
        holder.edtext.setText(text);

        String time=data.get(position).getTimestamp();
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

    public ArrayList<STT> getData() {
        return data;
    }



    public void removeItem(int position) {
        STT mRecentlyDeletedItem=data.get(position);
        int mRecentlyDeletedItemPosition=position;
        data.remove(position);
        System.out.println("Adapter ;" +data.size());
        notifyItemRemoved(position);
    }

    public void restoreItem(STT item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }


    class MyHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView edtext,edtimestamp;
        HistoryItemClickListener onSTTListener;


        public MyHolder(@NonNull View itemView, HistoryItemClickListener onSTTListener) {

            super(itemView);
            edtext=itemView.findViewById(R.id.text_itemstt);
            edtimestamp=itemView.findViewById(R.id.timestamp_itemstt);
            this.onSTTListener=onSTTListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            this.onSTTListener.onHistoryItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onSTTListener.onHistoryItemClick(getAdapterPosition());
            return false;
        }
    }

    @Override
    public Filter getFilter() {
        return sttfilter;
    }

    private Filter sttfilter =new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<STT> filteredlist= new ArrayList<STT>();
            if(constraint ==null || constraint.length()==0){
                filteredlist.addAll(datafull);
            } else {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(STT item: datafull){
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
