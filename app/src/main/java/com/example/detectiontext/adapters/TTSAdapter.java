package com.example.detectiontext.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detectiontext.HistoryItemClickListener;
import com.example.detectiontext.R;
import com.example.detectiontext.models.ImgTT;
import com.example.detectiontext.models.TTS;
import com.example.detectiontext.models.TTS;
import com.example.detectiontext.models.TTS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TTSAdapter extends RecyclerView.Adapter<TTSAdapter.MyHolder> implements Filterable {
    Context context;
    ArrayList<TTS> data, datafull;
    HistoryItemClickListener onTTSListener;

    public TTSAdapter(Context context, ArrayList<TTS> data, HistoryItemClickListener onTTSListener) {
        this.context = context;
        this.data = data;
        this.onTTSListener=onTTSListener;
        datafull=new ArrayList<TTS>();
        Iterator<TTS> iterator = data.iterator();
        while(iterator.hasNext()){
            TTS item=iterator.next();
            TTS toadd=new TTS(item.getText(),item.getTimestamp());
            datafull.add(toadd);
        }
    }

    @NonNull
    @Override
    public TTSAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_text_to_speech, parent, false);
        return new MyHolder(view,onTTSListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TTSAdapter.MyHolder holder, int position) {

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

    public ArrayList<TTS> getData() {
        return data;
    }



    public void removeItem(int position) {
        TTS mRecentlyDeletedItem=data.get(position);
        int mRecentlyDeletedItemPosition=position;
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(TTS item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }


    class MyHolder extends  RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        TextView edtext,edtimestamp;
        HistoryItemClickListener onTTSListener;

        public MyHolder(@NonNull View itemView,HistoryItemClickListener onTTSListener) {

            super(itemView);
            edtext=itemView.findViewById(R.id.text_itemtts);
            edtimestamp=itemView.findViewById(R.id.timestamp_itemtts);
            this.onTTSListener=onTTSListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTTSListener.onHistoryItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onTTSListener.onLongClick(getAdapterPosition());
            return false;
        }
    }

    @Override
    public Filter getFilter() {
        return ttsfilter;
    }

    private Filter ttsfilter =new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TTS> filteredlist= new ArrayList<TTS>();
            if(constraint ==null || constraint.length()==0){
                filteredlist.addAll(datafull);
            } else {
                String filterPattern=constraint.toString().toLowerCase().trim();

                for(TTS item: datafull){
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
