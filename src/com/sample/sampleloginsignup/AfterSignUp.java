package com.sample.sampleloginsignup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talentelgia.littlepicasso.UserData.AddInvitation;
import com.talentelgia.littlepicasso.UserData.UserDetail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfterSignUp extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Bundle savedInstanceState;

    private String mParam1,first_name,last_name;
    private String mParam2;
    Button sign_up_detail;
    String email,password;
    TextView signup;
    LoginActivity context;
    Fragment fragment = null;
    private FirebaseAuth auth;
    DataSnapshot dataSnapshot;
    private ProgressBar spinner;
    //a list to store all the artist from firebase database
    List<UserDetail> artists;

    //our database reference object
    DatabaseReference databaseArtists;

    EditText user_firstname,user_lastname;

    private OnFragmentInteractionListener mListener;
    public void addUserId(String key, Context context) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("authentication", MODE_PRIVATE).edit();
            editor.putString("userid", key);
            editor.commit();
        }
        catch (Exception ex){

        }
    }
    public AfterSignUp() {
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
        context=(LoginActivity) getActivity();
        databaseArtists = FirebaseDatabase.getInstance().getReference("Users");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Login.pagestatus=2;
    }
    public void userEmailAcc(String key) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences("emailacc", MODE_PRIVATE).edit();
            editor.putString("email", key);
            editor.commit();
        }
        catch (Exception ex){

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
								final Bundle savedInstanceState) {

        this.savedInstanceState=savedInstanceState;
        Bundle bundle=getArguments();

 email=bundle.getString("email","no value");
        password=bundle.getString("password","no password");
        auth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_sign__up2, container, false);
        spinner=(ProgressBar)v.findViewById(R.id.progressBar);

        CardView imageView1=(CardView)v.findViewById(R.id.back);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment signUp = new SignUp();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.mainFrame, signUp);
                transaction.commit();
            }
        });

        sign_up_detail=(Button)v.findViewById(R.id.sign_up_detail);
        user_firstname=(EditText) v.findViewById(R.id.user_firstname);
        user_lastname=(EditText)v.findViewById(R.id.user_lastname);

        sign_up_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                first_name = user_firstname.getText().toString().trim();
                last_name = user_lastname.getText().toString().trim();


                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    addUserId(task.getResult().getUser().getUid(), getActivity());
                                    userEmailAcc(task.getResult().getUser().getUid());
                                }
                                catch (Exception ex){
                                    spinner.setVisibility(View.GONE);
                                }
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Already have an account",
                                            Toast.LENGTH_SHORT).show();
                                    spinner.setVisibility(View.GONE);
                                } else {
                                    addEmail(email);
                                    addUser(task.getResult().getUser().getUid());

                Fragment addChild=new AddChild();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.mainFrame, addChild);
                transaction.commit();

                                    spinner.setVisibility(View.GONE);
                                }
                            }
                        });
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


    ///Add user profile

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AfterSignUp.this.dataSnapshot=dataSnapshot;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addEmail(String email){
        SharedPreferences.Editor editor =getActivity().getSharedPreferences("useremail", MODE_PRIVATE).edit();
        editor.putString("email",email);
    }

    private void addUser(String str) {

        if (!TextUtils.isEmpty(first_name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();
            SharedPreferences.Editor editor1 = getActivity().getSharedPreferences("userdetail", MODE_PRIVATE).edit();
            editor1.putString("firstname",first_name);
            editor1.putString("lastname",last_name);
            editor1.putString("userid",str);
            editor1.commit();

            UserDetail artist1 = new UserDetail(first_name, last_name,str,email,new SimpleDateFormat("dd:MMM:yyyy").format(new Date()),"user","android");

            //Saving the Artist
            databaseArtists.child(str).setValue(artist1);

            //setting edittext to blank again
			user_firstname.setText("");

            DatabaseReference dR1 = FirebaseDatabase.getInstance().getReference("InvitedList");
            String child_id = dR1.push().getKey();

            SharedPreferences editor2 = getActivity().getSharedPreferences("userdetail", MODE_PRIVATE);
            AddInvitation addInvitation=new AddInvitation();
            addInvitation.setFirstName(first_name);
            addInvitation.setLastName(last_name);
            addInvitation.setInvitedUserEmail(email);
            addInvitation.setFromUserId(str);
            addInvitation.setToUserId(str);
            addInvitation.setFromFname(first_name);
            addInvitation.setFromLname(last_name);
            addInvitation.setInvitedStatus("");
                addInvitation.setPermissionType("ReadandWrite");

            dR1.child(child_id).setValue(addInvitation);
        } else {
            Log.d("Invalid credential","Please eneter unique username");
        }
    }

}
