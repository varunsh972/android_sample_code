package com.sample.sampleloginsignup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.antonyt.infiniteviewpager.CommonInterface;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.talentelgia.littlepicasso.UserData.UserDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   static Profile profile;
    Bundle savedInstanceState;
    String facebookemail="";
    private ProgressBar spinner;
    private DatabaseReference mDatabase;
    private DatabaseReference invitedref;
    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();

    StorageReference storageRef = firebaseStorage.getReference();
    DataSnapshot dataSnapshot;
    public static int pagestatus=0;

    public String getDefaults() {
        try {
            {
            }
            SharedPreferences editor = getActivity().getSharedPreferences("authentication", MODE_PRIVATE);
            return editor.getString("userid", "");
        }
        catch (Exception ex){
            return "";
        }
    }

    public void addEmail(String email){
        try {
            {
            }
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("useremail", MODE_PRIVATE).edit();
            editor.putString("email", email);
            editor.commit();
        }
        catch (Exception ex){

        }

    }

    private void prepareMovieData() {
        mDatabase =  FirebaseDatabase.getInstance().getReference();
      invitedref=  mDatabase.child("Posts");
        invitedref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    final AddMomentPojo note = noteSnapshot.getValue(AddMomentPojo.class);
                    // Log.d("OnCancelledCalling ","aaa1"+ note.getSenderid().equals(addUserId()));
//                   if(note.getParentid().equals(getDefaults()))
                    if(note.getPostImageOrVideoUrl().isEmpty()){

                    }
                    else
                    {
                        StorageReference islandRef = storageRef.child(note.getPostImageOrVideoUrl());


                        final long ONE_MEGABYTE = 1024 * 1024;
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                CommonInterface.bitmapArrayList.add(bmp);
                                CommonInterface.stringArrayList.add(note.getPostDate());
                                //Toast.makeText(getActivity(),"message",Toast.LENGTH_SHORT).show();
//                                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(),
//                                            imageView.getHeight(), false));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });

                    }
                    //arrayList.add(note);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("On Cancelled Calling ",databaseError.toString());

            }


        });


        // mAdapter.notifyDataSetChanged();
    }


//    public void addUserId(String key, Context context) {
//        try {
//            SharedPreferences.Editor editor = context.getSharedPreferences("authentication", MODE_PRIVATE).edit();
//            editor.putString("userid", key);
//            editor.commit();
//        }
//        catch (Exception ex){
//
//        }
//    }
    public boolean getFacebookAcc() {
        try {
            SharedPreferences editor =getActivity().getSharedPreferences("facebookacc", MODE_PRIVATE);
            if(editor.getString("facebook","").isEmpty())
                return false;
            else
                return true;
        }
        catch (Exception ex){
return false;
        }
    }
    public boolean getGoogleAcc() {
        try {
            SharedPreferences editor = getActivity().getSharedPreferences("googleacc", MODE_PRIVATE);
           if(editor.getString("google","").isEmpty())
               return false;
            else
                return true;
        }
        catch (Exception ex){
return false;
        }
    }
    public boolean getEmailAcc() {
        try {
            SharedPreferences editor = getActivity().getSharedPreferences("emailacc", MODE_PRIVATE);

            if(editor.getString("email","").isEmpty())
                return false;
            else
                return true;

        }
        catch (Exception ex){
return false;
        }
    }
    private void sharePhotoToFacebook(){
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);
        Toast.makeText(getActivity(),"shared",Toast.LENGTH_SHORT).show();

    }
    public void userFacebookAcc(String key) {
        try {
            SharedPreferences.Editor editor =getActivity().getSharedPreferences("facebookacc", MODE_PRIVATE).edit();
            editor.putString("facebook", key);
            editor.commit();
        }
        catch (Exception ex){

        }
    }

    public void userGoogleAcc(String key) {
        try {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("googleacc", MODE_PRIVATE).edit();
            editor.putString("google", key);
            editor.commit();
        }
        catch (Exception ex){

        }
    }
    public void userEmailAcc(String key) {
        try {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("emailacc", MODE_PRIVATE).edit();
            editor.putString("email", key);
            editor.commit();
        }
        catch (Exception ex){

        }
    }
    private static final String TAG = "GoogleActivity";

    SharedPreferences sharedPreferences;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private FirebaseAuth auth;


    private LoginButton loginButton;
    com.facebook.login.LoginManager fbLoginManager;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
   // List<String> permissionNeeds= Arrays.asList("email","public_profile");
    DatabaseReference databaseArtists;
    Button sign_in;
boolean connected;
    LinearLayout fb_auth_login;
    TextView signup,fb_text;
  LoginActivity context;
    Fragment fragment = null;
    EditText email,password;
    String first_name,last_name,email_fb,user_id;

    private OnFragmentInteractionListener mListener;

    private TextView fpwd;
    private FirebaseAuth mAuth1;

    private SignInButton signInButton;
    private CallbackManager callbackManager;
    //Signing Options
    private GoogleSignInOptions gso;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

int count=0;
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

    public void setTextDate(){





        final ArrayList<ChildPojo> childPojoArrayList=new ArrayList<>();
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        invitedref=  mDatabase.child("Posts");
        invitedref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    final AddMomentPojo note = noteSnapshot.getValue(AddMomentPojo.class);
                    // Log.d("whatisid",note.getParentId()+","+userid);
                    //Toast.makeText(getActivity(),""+note.getParentId()+","+userid,Toast.LENGTH_SHORT).show();
                    if(note.getUserId().equals(getDefaults())){
                        CommonInterface.date.add(note.getPostDate());
                    }
                    //arrayList.add(note);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("On Cancelled Calling ",databaseError.toString());

            }


        });

//
//        TextPostDatabase textPostDatabase=new TextPostDatabase(this);
//        CommonInterface.date=textPostDatabase.getAllDate();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseArtists = FirebaseDatabase.getInstance().getReference("Users");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        setTextDate();
       prepareMovieData();
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        auth = FirebaseAuth.getInstance();
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        callbackManager = CallbackManager.Factory.create();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

       // accessTokenTracker.startTracking();
       // profileTracker.startTracking();

        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


               Login.this.dataSnapshot=dataSnapshot;



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        this.savedInstanceState=savedInstanceState;



        // Inflate the layout for this fragment

View v=inflater.inflate(R.layout.fragment_login, container, false);
        spinner=(ProgressBar)v.findViewById(R.id.progressBar);
        sign_in=(Button)v.findViewById(R.id.sign_in);
        signup=(TextView)v.findViewById(R.id.signup);
        fpwd=(TextView)v.findViewById(R.id.fpwd);
        email=(EditText)v.findViewById(R.id.email);
        fb_text=(TextView)v.findViewById(R.id.fb_text);
        password=(EditText)v.findViewById(R.id.password);
        fb_auth_login=(LinearLayout)v.findViewById(R.id.fb_auth_login);
        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        //google signin
        signInButton = (SignInButton)v.findViewById(R.id.sign_in_button);



        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    boolean isvalidEmail = emailValidator(email.getText().toString());

                    if(isvalidEmail!=true) {

                        email.setError("not valid email");
                    }


                }
            }
        });



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        Log.d("whatdata",gso+""+Auth.GOOGLE_SIGN_IN_API);
        sharedPreferences=getActivity().getSharedPreferences("facebookgoogledata", MODE_PRIVATE);


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(LoginActivity.loginActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        signInButton.setOnClickListener(this);



        mAuth1 = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(connected==true) {
    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            Log.d("onsuccess", "onsuccess1");

            getEmail(loginResult);
            if (Profile.getCurrentProfile() == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        //Log.v("facebook - profile", profile2.getFirstName());
                        Login.profile = profile2;
                        handleFacebookAccessToken(loginResult.getAccessToken(), Login.profile);
                       // Toast.makeText(getActivity(), "" + profile2.getFirstName(), Toast.LENGTH_SHORT).show();
                        //profileTracker.stopTracking();

                    }
                };

                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
            } else {

                Profile profile = Profile.getCurrentProfile();

                handleFacebookAccessToken(loginResult.getAccessToken(), profile);
                //Toast.makeText(getActivity(), "" + profile.getFirstName(), Toast.LENGTH_SHORT).show();
                Log.v("facebook - profile", profile.getFirstName());
            }


        }

        @Override
        public void onCancel() {
            Log.d("onsuccess", "onsuccess");
        }

        @Override
        public void onError(FacebookException exception) {
            Log.d("onsuccess", "onsuccess");
        }
    });


}
else
    Toast.makeText(getActivity(),"no internet connection",Toast.LENGTH_SHORT).show();

            }
        });



sign_in.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(connected==true) {
            if (!email.getText().toString().isEmpty() || !password.getText().toString().isEmpty()) {
                if (password.length() < 8)
                    password.setError("8 digit password");
                else {
                    spinner.setVisibility(View.VISIBLE);

                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        Toast.makeText(getActivity(), "invalid email/password", Toast.LENGTH_LONG).show();
                                       spinner.setVisibility(View.GONE);

                                    } else {
                                        FirebaseUser user = task.getResult().getUser();
                                        addEmail(email.getText().toString());
                                        Log.d(TAG, "onComplete: uid=" + user.getUid());
                                        //Toast.makeText(getActivity(), "" + user.getUid(), Toast.LENGTH_SHORT).show();
//                                    if (sharedPreferences.getString("child", null) == null) {
                                        SharedPreferences.Editor editor1 = getActivity().getSharedPreferences("userdetail", MODE_PRIVATE).edit();
                                        editor1.putString("firstname",task.getResult().getUser().getDisplayName());
                                        editor1.putString("lastname",task.getResult().getUser().getDisplayName());
                                        editor1.putString("userid",task.getResult().getUser().getUid());
                                        editor1.commit();





                                        SharedPreferences.Editor editor =getActivity().getSharedPreferences("authentication", MODE_PRIVATE).edit();
                                        editor.putString("userid",task.getResult().getUser().getUid());
                                        editor.commit();
                                        if (getEmailAcc() == false) {
                                            userEmailAcc(task.getResult().getUser().getUid());
                                            //addUserId(task.getResult().getUser().getUid(),getActivity());
                                            Fragment addChild = new AddChild();
                                            FragmentManager manager = getFragmentManager();
                                            FragmentTransaction transaction = manager.beginTransaction();
                                            transaction.replace(R.id.mainFrame, addChild);
                                            transaction.commit();
                                            spinner.setVisibility(View.GONE);
                                            //Toast.makeText(getActivity(), task.getResult().getUser().getUid(), Toast.LENGTH_LONG).show();
//
                                        } else {
                                            userEmailAcc(task.getResult().getUser().getUid());
                                            Intent intent = new Intent(getActivity(), DashBoard.class);
                                            startActivity(intent);
                                            spinner.setVisibility(View.GONE);

                                            //Toast.makeText(getActivity(), task.getResult().getUser().getUid(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                            });
                }
            }
            else
                Toast.makeText(getActivity(), "enter email/pwd", Toast.LENGTH_SHORT).show();

            }



//
//        boolean exists = false;
//
//      // Toast.makeText(getActivity(),dataSnapshot.,Toast.LENGTH_SHORT).show();
//try {
//    for (DataSnapshot data : dataSnapshot.getChildren()) {
//        //    i++;
//        String userName = String.valueOf(data.child("userEmail").getValue());
//
//
//        Map<String, Object> model = (Map<String, Object>) data.getValue();
//        try {
//            if (model.get("userEmail").equals(email.getText().toString().trim()) && model.get("userPassword").equals(password.getText().toString().trim())) {
//                exists = true;
//
//                email_fb = model.get("userEmail").toString();
//                first_name = model.get("userName").toString();
//                last_name = model.get("lastName").toString();
//                user_id = model.get("userId").toString();
//                setDefaults("email", "user_firstname", "user_lastname", "userId", email_fb, first_name, last_name, user_id, getActivity());
//                Toast.makeText(getActivity(), getDefaults("userId", getActivity()), Toast.LENGTH_SHORT).show();
//                break;
//            }
//        } catch (Exception ex) {
//
//        }
//    }
//}
//catch (Exception ex){
//
//}
//        if(exists) {
//
//            Toast.makeText(getActivity(),"successfully logged in...",Toast.LENGTH_SHORT).show();
//
//          Intent intent=new Intent(getActivity(),DashBoard.class);
//            startActivity(intent);
//
//
//            // This user already exists in firebase.
//        }
//        else {
//
//            Toast.makeText(getActivity(),"User not found...",Toast.LENGTH_SHORT).show();
//            // This user doesn't exists in firebase.
//        }
//

    }
});

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new SignUp();

                if(savedInstanceState == null) {
if(connected==true) {

    FragmentManager manager = getFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    transaction.replace(R.id.mainFrame, fragment);
    transaction.commit();
}
else
    Toast.makeText(getActivity(),"no internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        fpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new ForgotPassword();

                if(savedInstanceState == null) {

                    if (connected == true) {
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.mainFrame, fragment);
                        transaction.commit();
                    }
                }
                else
                    Toast.makeText(getActivity(),"no internet connection",Toast.LENGTH_SHORT).show();
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
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        }

//        else
//            signOut();

    }


    private void handleFacebookAccessToken(final AccessToken token, final Profile profile) {
        Log.d("handleFacebookAcce", "handleFacebookAccessToken:" + token);




        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                           // Log.d("signInWithemail",task.getResult().getUser().getEmail());
                            updateUI(user);

                            UserDetail artist1 = new UserDetail(profile.getFirstName(),profile.getLastName(),task.getResult().getUser().getUid(),facebookemail,new SimpleDateFormat("dd:MMM:yyyy").format(new Date()),"user","android");
                            SharedPreferences.Editor editor1 = getActivity().getSharedPreferences("userdetail", MODE_PRIVATE).edit();
                            editor1.putString("firstname",profile.getFirstName());
                            editor1.putString("lastname",profile.getLastName());
                            editor1.putString("userid",task.getResult().getUser().getUid());
                            editor1.commit();
                            //Saving the Artist
                            databaseArtists.child(task.getResult().getUser().getUid()).setValue(artist1);
                            displayMessage(profile);
                            userFacebookAcc(task.getResult().getUser().getUid());
                            SharedPreferences.Editor editor =getActivity().getSharedPreferences("authentication", MODE_PRIVATE).edit();
                            editor.putString("userid",task.getResult().getUser().getUid());
                            editor.commit();
                            //addUserId(task.getResult().getUser().getUid(),getActivity());
                           // Toast.makeText(getActivity(),user.getEmail(),Toast.LENGTH_SHORT).show();
//
//                            mAuth.createUserWithEmailAndPassword(profile.getId(),profile.getId())
//                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<AuthResult> task) {
//                                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                                            // If sign in fails, display a message to the user. If sign in succeeds
//                                            // the auth state listener will be notified and logic to handle the
//                                            // signed in user can be handled in the listener.
//                                            if (!task.isSuccessful()) {
//                                                Toast.makeText(getActivity(), "success",
//                                                        Toast.LENGTH_SHORT).show();
//                                            }
//
//                                            // ...
//                                        }
//                                    });





                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        addUser();
//
//    }








    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        //attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


               Login.this.dataSnapshot=dataSnapshot;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();


    }

//    private void addUser() {
//        //getting the values to save
//
////        }
//        String id1 = databaseArtists.push().getKey();
//
//
//
//            //getting a unique id using push().getKey() method
//            //it will create a unique id and we will use it as the Primary Key for our Artist
//            String id = databaseArtists.push().getKey();
//
//
//
//
//
//            //creating an Artist Object
//            UserDetail artist1 = new UserDetail(id, first_name, first_name,email_fb," ");
//
//            //Saving the Artist
//            databaseArtists.child(id).setValue(artist1);
//
//            //setting edittext to blank again
//         //   user_firstname.setText("");
//
//            //displaying a success toast
//            //Toast.makeText(getActivity(), "User added", Toast.LENGTH_LONG).show();
//
//    }

    private void displayMessage(Profile profile){
        if(profile != null) {

            first_name = profile.getFirstName();
            last_name = profile.getLastName();
            email_fb = profile.getId();

            if (getFacebookAcc()==false) {
                //Toast.makeText(getActivity(), "displayMessage", Toast.LENGTH_SHORT).show();
                Fragment addChild = new AddChild();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.mainFrame, addChild);
                transaction.commit();

            }
else
            {
                Intent intent=new Intent(getActivity(),DashBoard.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }

        }

    }

    @Override
    public void onStop() {
        super.onStop();
       // accessTokenTracker.stopTracking();
//        profileTracker.stopTracking();
//                mGoogleApiClient.stopAutoManage(LoginActivity.loginActivity);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
      //  displayMessage(profile);
    }

//
//    public void RequestData(){
//        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//
//                JSONObject json = response.getJSONObject();
//                Log.d("json:::",json.toString());
//                try {
//                    if(json != null){
//                        String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
//                       // details_txt.setText(Html.fromHtml(text));
//                        first_name=json.getString("name");
//                        email_fb=json.getString("email");
//                        user_id=json.getString("id");
//                        String id1 = databaseArtists.push().getKey();
//
//
//
//                        //getting a unique id using push().getKey() method
//                        //it will create a unique id and we will use it as the Primary Key for our Artist
//                        String id = databaseArtists.push().getKey();
//
//
//                        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                            String userName = String.valueOf(data.child("userEmail").getValue());
//                            if (userName.equalsIgnoreCase(email_fb)) {
//                               // Toast.makeText(getActivity(), "Already have account::", Toast.LENGTH_SHORT).show();
//
//email_fb="";
//                            }
//
//                        }
//
//
//                        if (!(TextUtils.isEmpty(email_fb) )) {
//
//
//                            UserDetail artist1 = new UserDetail(id, first_name, first_name,email_fb," ");
//
//                            //Saving the Artist
//                            databaseArtists.child(id).setValue(artist1);
//
//
//                            setDefaults("email","user_firstname","user_lastname","userId",email_fb,first_name,last_name,user_id,getActivity());
//                        } else {
//                            //if the value is not given displaying a toast
//                            //Toast.makeText(getActivity(), "Please Sign in with unique email......", Toast.LENGTH_LONG).show();
//                        }
//
//
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,link,email,picture");
//        request.setParameters(parameters);
//        request.executeAsync();
//    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        //Log.d("firebaseAuthWithGoogle:",acct.getIdToken()+","+acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
//if(connected==true) {
    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(Login.this.getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        //updateUI(user);
                        Log.d("firebaseAuthWithGoogle:",task.getResult().getUser().getUid()+"" +user.getUid());
//Toast.makeText(getActivity(),task.getResult().getUser().getUid(),Toast.LENGTH_SHORT).show();
                        UserDetail artist1 = new UserDetail(task.getResult().getUser().getDisplayName(),task.getResult().getUser().getDisplayName(),task.getResult().getUser().getUid(), user.getEmail(), new SimpleDateFormat("dd:MMM:yyyy").format(new Date()),"user","android");
                        addEmail(user.getEmail());
                        //Saving the Artist
                        databaseArtists.child(task.getResult().getUser().getUid()).setValue(artist1);
                        //addUserId(task.getResult().getUser().getUid(), getActivity());
try {
    {
    }
    SharedPreferences.Editor editor1 = getActivity().getSharedPreferences("userdetail", MODE_PRIVATE).edit();
    editor1.putString("firstname", task.getResult().getUser().getDisplayName());
    editor1.putString("lastname", task.getResult().getUser().getDisplayName());
    editor1.putString("userid", task.getResult().getUser().getUid());
    editor1.commit();
}
catch (Exception ex){

}
                        if (getGoogleAcc()==false) {

                            try {
                                Fragment addChild = new AddChild();
                                FragmentManager manager = getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.add(R.id.mainFrame, addChild);
                                transaction.commit();
                            }
                            catch(Exception ex){

                            }

                        } else {
                            try {

                                Intent intent = new Intent(getActivity(), DashBoard.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            } catch (Exception ex) {

                            }
                        }
                        userGoogleAcc(task.getResult().getUser().getUid());

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // [START_EXCLUDE]
                    //hideProgressDialog();
                    // [END_EXCLUDE]
                }
            });
//}
//else
//    Toast.makeText(getActivity(),"no internet connection",Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        // Firebase sign out
       // mAuth.signOut();
       // LoginManager.getInstance().logOut();
        // Google sign out
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        updateUI(null);
//                    }
//                });
    }

    private void revokeAccess() {
        // Firebase sign out
       // mAuth.signOut();

        // Google revoke access
//        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        updateUI(null);
//                    }
//                });
    }

    private void updateUI(FirebaseUser user) {
       // hideProgressDialog();
        if (user != null) {

        } else {

        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
//if(connected==true) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
//Toast.makeText(getActivity(),""+account.getId(),Toast.LENGTH_SHORT).show();

                String id = databaseArtists.push().getKey();
                //creating an Artist Object


                if (sharedPreferences.getString("child", null) == null) {


                    Fragment addChild = new AddChild();
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.mainFrame, addChild);
                    transaction.commit();

//
                } else {
                    Intent intent = new Intent(getActivity(), DashBoard.class);
                    startActivity(intent);
                }
            }
//        else
//
//            //addUser();
//    }
//}
//else
//    Toast.makeText(getActivity(),"no internet connection",Toast.LENGTH_SHORT).show();
        }
    }
    //setting login data method

    public static void setDefaults(String key,String key1,String key2, String key3,String value1, String value2,String value3,String value4,Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value1);
        editor.putString(key1, value2);
        editor.putString(key2,value3);
        editor.putString(key3,value4);
        editor.commit();
    }


    //getting login info from device

//    public static String getDefaults(String key, Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getString(key, "No detail");
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(LoginActivity.loginActivity);
        mGoogleApiClient.disconnect();
    }


    public void getEmail(LoginResult loginResult){


        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.i("LoginActivity", response.toString());
                // Get facebook data from login
                Bundle bFacebookData = getFacebookData(object);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();







    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                facebookemail=object.getString("email");
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        return null;
    }
}
