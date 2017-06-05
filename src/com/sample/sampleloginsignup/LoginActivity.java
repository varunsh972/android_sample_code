package com.sample.sampleloginsignup;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity {

    Fragment fragment = null;

    public static FrameLayout mainFrame;
    static LoginActivity loginActivity;
    public static DataSnapshot dataSnapshot;
    DatabaseReference databaseArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        fragment = new Login();
        databaseArtists = FirebaseDatabase.getInstance().getReference("userdetail");
        this.loginActivity = this;
        Intent intent = getIntent();
//        if(intent.getCharSequenceExtra("addchild")!=null){
//
//            fragment=new AddChild();
//
//            FragmentManager manager = getFragmentManager();
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.replace(R.id.mainFrame, fragment);
//            transaction.commit();
//
//        }
//        SendInvitation SendMail= new SendInvitation(this,"artimishra726@gmail.com", "Little Picasso", "aaa");
//
//        //Executing sendmail to send email
//        SendMail.execute();
//

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.talentelgia.littlepicasso",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if (savedInstanceState == null) {


            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.mainFrame, fragment);
            transaction.commit();
        }

    }

    public int mainLayout() {


        return R.id.mainFrame;
    }


    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                LoginActivity.this.dataSnapshot = dataSnapshot;
                //        for (DataSnapshot data : dataSnapshot.getChildren()) {
//            String userName = String.valueOf(data.child("userEmail").getValue());
//           // Toast.makeText(getActivity(),userName,Toast.LENGTH_SHORT).show();
//            if(userName.equalsIgnoreCase(email.getText().toString().trim())){
//                Toast.makeText(getActivity(),"successfully logged in...",Toast.LENGTH_SHORT).show();}
//
//            else{
//
//                Toast.makeText(getActivity(),"User not found...",Toast.LENGTH_SHORT).show();
//            }
//            // driverlist.add(userName);
//        }

                //      Toast.makeText(getActivity(),dataSnapshot.toString(),Toast.LENGTH_SHORT).show();

//              getActivity().this.dataSnapshot=dataSnapshot;
//
//                //clearing the previous artist list
//                artists.clear();
//
//                //iterating through all the nodes
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    //getting artist
//                    Artist artist = postSnapshot.getValue(Artist.class);
//                    //adding artist to the list
//                    artists.add(artist);
//                }
//
//                //creating adapter
//                ArtistList artistAdapter = new ArtistList(MainActivity.this, artists);
//                //attaching adapter to the listview
//                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111) {

            Toast.makeText(this, "you clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        if (Login.pagestatus == 0)
            super.onBackPressed();
        else if (Login.pagestatus == 1) {
            Fragment fragment1 = new Login();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.mainFrame, fragment1);
            transaction.commit();
        } else if (Login.pagestatus == 2) {
            Fragment fragment1 = new SignUp();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.mainFrame, fragment1);
            transaction.commit();
        }
    }
}
