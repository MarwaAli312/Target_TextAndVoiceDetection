package com.example.detectiontext;

import android.app.Activity;


public class MainActivity2 extends Activity {
    /*

    RecyclerView recyclerView;
    ImgTTAdapter imgTTAdapter;
    ArrayList<ImgTT> data;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.fragment_history_imgtt);
        data = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, recyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_imgtt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        imgTTAdapter = new ImgTTAdapter(this, data,MainActivity2.this);
        recyclerView.setAdapter(imgTTAdapter);


        getHistory();
    }



    public void getHistory() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        DatabaseReference dbref2 = dbref.child("imagetotext");
        dbref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ImgTT item = ds.getValue(ImgTT.class);
                    data.add(item);
                    ImgTTAdapter adapter = new ImgTTAdapter(MainActivity2.this, data,MainActivity2.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
*/
}