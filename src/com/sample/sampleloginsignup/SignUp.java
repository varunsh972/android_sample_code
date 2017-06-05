package com.sample.sampleloginsignup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Bundle savedInstanceState;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button sign_up_main;
    TextView signup,signin;
    LoginActivity context;
    Fragment fragment = null;

    EditText email_address,user_password;
    DataSnapshot dataSnapshot;
    DatabaseReference databaseArtists;

    private OnFragmentInteractionListener mListener;

    public SignUp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseArtists = FirebaseDatabase.getInstance().getReference("userdetail");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Login.pagestatus=1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        this.savedInstanceState=savedInstanceState;



        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_sign__up1, container, false);
        signin=(TextView)v.findViewById(R.id.signin);
        sign_up_main=(Button)v.findViewById(R.id.sign_up_main);
        email_address=(EditText)v.findViewById(R.id.email_address);
        user_password=(EditText)v.findViewById(R.id.user_password);
       // signup=(TextView)v.findViewById(R.id.signup);


        user_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    boolean isvalidEmail = emailValidator(email_address.getText().toString());

                    if(isvalidEmail!=true) {

                        email_address.setError("invalid email");
                    }


                }
            }
        });

signin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        fragment = new Login();

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.mainFrame, fragment);
        transaction.commit();
    }
});

        sign_up_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new AfterSignUp();
                String email = email_address.getText().toString().trim();
                String password = user_password.getText().toString().trim();
                boolean isvalidEmail = emailValidator(email);

if(isvalidEmail==true){
    if(dataSnapshot!=null) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {

            String userName = String.valueOf(data.child("userEmail").getValue());
            if (userName.equalsIgnoreCase(email)) {
                //Toast.makeText(getActivity(), "Already have account::", Toast.LENGTH_SHORT).show();
                email = "";
                password = "";
                email_address.setError("Already have account");

            }

        }

    }
                if (!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))) {

                    //getting a unique id using push().getKey() method
                    //it will create a unique id and we will use it as the Primary Key for our Artist
                    if (password.length()<8){

                        user_password.setError("should be 8 digit");

                        //Toast.makeText(getActivity(),"password should be at least 8 digit",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (savedInstanceState == null) {
                            Bundle bundle = new Bundle();
                            bundle.putCharSequence("email", email);
                            bundle.putCharSequence("password", password);
                            fragment.setArguments(bundle);
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.mainFrame, fragment);
                            transaction.commit();
                        }
                    }
                } else {
                    //if the value is not given displaying a toast
                    Toast.makeText(getActivity(), "Please enter unique email......", Toast.LENGTH_LONG).show();
                }


            }

            else{

    Toast.makeText(getActivity(), "Please Provide valid email id......", Toast.LENGTH_LONG).show();
}

            }
        });


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {

        this.context=(LoginActivity)context;


        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
/////Validate email method



    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


             SignUp.this.dataSnapshot=dataSnapshot;

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



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
