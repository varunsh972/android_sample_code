package com.sample.sampleloginsignup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForgotPassword.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForgotPassword#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPassword extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    Button sendvcode;
    private FirebaseAuth auth;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Fragment fragment = null;
    private OnFragmentInteractionListener mListener;
    private ProgressBar spinner;
    public ForgotPassword() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPassword.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPassword newInstance(String param1, String param2) {
        ForgotPassword fragment = new ForgotPassword();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_forgot_password, container, false);
        spinner=(ProgressBar)v.findViewById(R.id.progressBar);
        sendvcode=(Button)v.findViewById(R.id.sendvcode);
		final EditText editText=(EditText)v.findViewById(R.id.quesmark);
		TextView click=(TextView)v.findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new SignUp();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });
        auth = FirebaseAuth.getInstance();
        sendvcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		if(!editText.getText().toString().isEmpty()) {

		spinner.setVisibility(View.VISIBLE);
		auth.sendPasswordResetEmail(editText.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                       Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        fragment = new Login();
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.mainFrame, fragment);
                        transaction.commit();
                        spinner.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                    }
                }
            });
}
else
    Toast.makeText(getActivity(),"enter email",Toast.LENGTH_SHORT).show();
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
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //OTP generation method
    static char[] OTP(int len)
    {
        System.out.println("Generating OTP using random() : ");


        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();

        char[] otp = new char[4];

        for (int i = 0; i < 4; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        System.out.print("You OTP is : "+otp.toString());
        return otp;
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

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "No detail");
    }

    public static void setDefaults(String key, String value,Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);

        editor.commit();
    }

}
