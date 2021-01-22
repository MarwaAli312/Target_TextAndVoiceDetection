package com.example.detectiontext.historyfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.detectiontext.HistoryItemClickListener;
import com.example.detectiontext.R;
import com.example.detectiontext.adapters.ImgTTAdapter;
import com.example.detectiontext.models.DataManager;
import com.example.detectiontext.models.ImgTT;

import com.github.chrisbanes.photoview.PhotoView;
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


public class HistoryImgTTFragment extends Fragment implements HistoryItemClickListener {

    RecyclerView recyclerView;
    ImgTTAdapter imgTTAdapter;
    ArrayList<ImgTT> data;
    Boolean deleteinvoked;
    ImageView imageView;
    EditText edsearch;
    int position;
    ImgTT item;
    LinearLayout linearLayout;

    public HistoryImgTTFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        data = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_history_imgtt, container, false);
        imageView=view.findViewById(R.id.imagePreview);
        deleteinvoked=false;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), recyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_imgtt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        edsearch=view.findViewById(R.id.search_imgtt);
        linearLayout=view.findViewById(R.id.layout_empty);

        setupSwipeActions();
        linearLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("ImageToText");
        DatabaseReference dbref2 = dbref.child(uid);


//import data
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ImgTT item = ds.getValue(ImgTT.class);
                    data.add(item);
                    imgTTAdapter = new ImgTTAdapter(getActivity(), data,HistoryImgTTFragment.this);
                    recyclerView.setAdapter(imgTTAdapter);
                    imgTTAdapter.notifyDataSetChanged();
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
                if(imgTTAdapter!=null){
                    imgTTAdapter.getFilter().filter(s);
                    imgTTAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (deleteinvoked) {
            DataManager dataManager = new DataManager();
            dataManager.deleteImgTTitem(item);
            deleteinvoked = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback swipeCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                item = imgTTAdapter.getData().get(position);
                switch (direction){
                    case ItemTouchHelper.LEFT: // right to left : delete
                        deleteinvoked=true;
                        imgTTAdapter.removeItem(position);
                        imgTTAdapter.notifyItemRemoved(position);
                        imgTTAdapter.notifyDataSetChanged();


                        Snackbar snackbardelete = Snackbar
                                .make(getView(), "Item was removed from the list.", Snackbar.LENGTH_LONG);
                        snackbardelete.setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                imgTTAdapter.restoreItem(item, position);
                                recyclerView.scrollToPosition(position);
                                deleteinvoked=false;
                            }
                        }).addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    DataManager dataManager=new DataManager();
                                    dataManager.deleteImgTTitem(item);
                                    deleteinvoked=false;
                                    Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                                    if(data.isEmpty()){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                }

                            }
                        });



                       // imgTTAdapter = new ImgTTAdapter(getActivity(), data);
                        recyclerView.setAdapter(imgTTAdapter);
                       // imgTTAdapter.notifyItemRemoved(position);

                        snackbardelete.setActionTextColor(Color.YELLOW);
                        snackbardelete.show();
                        break;




                    case ItemTouchHelper.RIGHT: //left to right: copy


                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("simple text", item.getText());
                        clipboard.setPrimaryClip(clip);
                        Snackbar snackbarcopy = Snackbar
                                .make(getView(), "Item copied to clipboard", Snackbar.LENGTH_LONG);
                        snackbarcopy.show();

                        //imgTTAdapter = new ImgTTAdapter(getActivity(), data);*/

                        recyclerView.setAdapter(imgTTAdapter);
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
        View mView = getLayoutInflater().inflate(R.layout.historyimg_dialog,null);
        final TextView txt_inputText = (TextView) mView.findViewById(R.id.txt_input);
        PhotoView imageView= mView.findViewById(R.id.imgdialog_history);
        txt_inputText.setText(data.get(position).getText());
        Glide.with(getContext())
                .load(data.get(position).getImage())
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                d.setCancelable(true);
                PhotoView photoView=new PhotoView(getContext());
                Glide.with(getContext())
                        .load(data.get(position).getImage())
                        .into(photoView);
                d.setContentView(photoView);
                d.show();
            }
        });

        /*text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", data.get(position).getText());
                clipboard.setPrimaryClip(clip);
                Snackbar snackbarcopy = Snackbar
                        .make(getView(), "Item copied to clipboard", Snackbar.LENGTH_LONG);
                snackbarcopy.show();
                return true;
            }
        });*/
        builder.setView(mView);
        AlertDialog alert = builder.create();
        alert.show();


    }

    @Override
    public void onLongClick(int position) {
        Dialog d = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        d.setCancelable(true);
        PhotoView photoView=new PhotoView(getContext());
        Glide.with(getContext())
                .load(data.get(position).getImage())

                .into(photoView);
//.placeholder(R.drawable.full_logo_horiz)
        d.setContentView(photoView);
        d.show();
    }


}