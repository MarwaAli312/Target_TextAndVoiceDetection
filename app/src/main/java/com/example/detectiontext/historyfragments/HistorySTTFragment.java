package com.example.detectiontext.historyfragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.detectiontext.HistoryItemClickListener;
import com.example.detectiontext.R;
import com.example.detectiontext.adapters.STTAdapter;
import com.example.detectiontext.models.DataManager;
import com.example.detectiontext.models.STT;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class HistorySTTFragment extends Fragment implements HistoryItemClickListener {
    //views
    RecyclerView recyclerView;
    ArrayList<STT> data;
    STTAdapter adapter;
    EditText edsearch;
    Boolean deleteinvoked;
    STT item;
    LinearLayout linearLayout;



    public HistorySTTFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        data=new ArrayList<>();
        deleteinvoked=false;




        View view= inflater.inflate(R.layout.fragment_history_stt, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerview_stt);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext(), recyclerView.VERTICAL,true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        edsearch=view.findViewById(R.id.search_stt);
        linearLayout=view.findViewById(R.id.layout_empty_stt);



        setupSwipeActions();

        //import data

        linearLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=firebaseUser.getUid();
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("SpeechToText");
        DatabaseReference dbref2=dbref.child(uid);
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    STT item=ds.getValue(STT.class);
                    data.add(item);
                    adapter= new STTAdapter(getActivity(), data,HistorySTTFragment.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if (!data.isEmpty()){
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        edsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(adapter!=null){
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                }
                else{
                    //List is empty


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (deleteinvoked) {
            DataManager dataManager = new DataManager();
            dataManager.deleteSTTitem(item);
            deleteinvoked = false;
        }
    }

    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback swipeCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                item = adapter.getData().get(position);
                switch (direction){
                    case ItemTouchHelper.LEFT: // right to left : delete
                        deleteinvoked=true;
                        adapter.removeItem(position);
                       // System.out.println("Fragment :" + data.size());
                        adapter.notifyItemRemoved(position);
                        adapter.notifyDataSetChanged();


                        Snackbar snackbar = Snackbar
                                .make(getView(), "Item was removed from the list.", Snackbar.LENGTH_LONG);
                        snackbar.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                adapter.restoreItem(item, position);
                                recyclerView.scrollToPosition(position);
                                deleteinvoked=false;
                            }
                        }).addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    DataManager dataManager=new DataManager();
                                    dataManager.deleteSTTitem(item);
                                    Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                                    deleteinvoked=false;
                                  if(data.isEmpty()){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }

                                }
                            }
                        });

                       // adapter = new STTAdapter(getActivity(), data,HistorySTTFragment.this);
                        recyclerView.setAdapter(adapter);

                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                        break;




                    case ItemTouchHelper.RIGHT: //left to right: copy

                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("simple text", item.getText());
                        clipboard.setPrimaryClip(clip);
                        Snackbar snackbarcopy = Snackbar
                                .make(getView(), "Item copied to clipboard", Snackbar.LENGTH_LONG);
                        snackbarcopy.show();

                        recyclerView.setAdapter(adapter);
                        break;
                }

            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.red))
                        .addSwipeLeftActionIcon(R.drawable.ic_clear)
                        .addSwipeLeftLabel("Delete")
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorSecondary))
                        .addSwipeRightActionIcon(R.drawable.ic_copy)
                        .addSwipeRightLabel("Copy text")
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };



        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onHistoryItemClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        String time=data.get(position).getTimestamp();
        cal.setTimeInMillis(Long.parseLong(time));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm:aa",cal).toString();
        builder.setTitle(dateTime);
        View mView = getLayoutInflater().inflate(R.layout.history_dialog,null);
        final TextView txt_inputText = (TextView) mView.findViewById(R.id.txt_input);
        txt_inputText.setText(data.get(position).getText());
        builder.setView(mView);
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onLongClick(int position) {
        System.out.println("Clicked");


    }
}