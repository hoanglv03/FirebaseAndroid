package com.example.firebaseandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginSighUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginSighUpFragment extends Fragment {

    private EditText edtEmail, edtPassword;
    private FirebaseAuth fAuth;
    private ProgressDialog mProgressDialog;
    public LoginSighUpFragment() {
        // Required empty public constructor
    }


    public static LoginSighUpFragment newInstance() {
        LoginSighUpFragment fragment = new LoginSighUpFragment();

        return fragment;
    }
    private void mFragmentTransaction(Fragment mFragment) {
        FragmentManager fm = getFragmentManager();
        Fragment oldFragment =
                fm.findFragmentByTag(Bai2.FRAGMENT_TAG);
        FragmentTransaction ft = fm.beginTransaction();
        if (oldFragment != null)
            ft.remove(oldFragment);
        ft.replace(R.id.content, mFragment, Bai2.FRAGMENT_TAG);
        ft.addToBackStack("replace");
        ft.commit();
    }
    private void showProgressDialog(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.setMessage(message);
        else
            mProgressDialog = ProgressDialog.show(getActivity(), title,
                    message, true, false);
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_sighup, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//get firebase auth instance
        fAuth = FirebaseAuth.getInstance();
//To get the currently signed-in user by calling getCurrentUser
        if (fAuth.getCurrentUser() != null) {
            mFragmentTransaction(HomeFragment.newInstance());
        }
        edtEmail = (EditText) getView().findViewById(R.id.email_edt);
        edtPassword = (EditText) getView().findViewById(R.id.pwd_edt);
//To reset forgotten password with recovery email id
        getView().findViewById(R.id.pwd_forgot_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoveryForgottenPasswordDialog();
            }
        });
        getView().findViewById(R.id.signup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog("Creating user account", "Loading..");
//create user with email and password

                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new
                                OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful(),
                                        Toast.LENGTH_SHORT).show();
                                hideProgressDialog();


// If sign in fails, display a message to the user. If sign in succeeds
// the auth state listener will be notified and logic to handle the
// signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "SignUp failed:: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                mFragmentTransaction(HomeFragment.newInstance());
                            }
                            }});
            }
        });
        getView().findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "Enter password!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog("Signing user account", "Loading..");//authenticate user with email and password
                fAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new
                                OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
// If sign in fails, display a message to the user. If sign in succeeds
// the auth state listener will be notified and logic to handle the
// signed in user can be handled in the listener.
                                hideProgressDialog();
                                if (task.isSuccessful()) {
                                    mFragmentTransaction(HomeFragment.newInstance());
                                } else {
                                    Toast.makeText(getActivity(), "Login failed:: " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
    public void showRecoveryForgottenPasswordDialog() {
        AlertDialog.Builder dialogBuilder = new
                AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView =
                inflater.inflate(R.layout.dialog_change_user_credential, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("Recovery Forgotten Password");
        dialogBuilder.setMessage("Enter your registered email id");
        dialogBuilder.setPositiveButton("Done", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//passing the email id to sendPasswordResetEmail method
                        recoveryForgottenPassword(edt.getText().toString());
                    }
                });
        dialogBuilder.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//cancel dialog
                    }
                });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    private void recoveryForgottenPassword(String currentEmailId) {
        if (TextUtils.isEmpty(currentEmailId)) {
            Toast.makeText(getActivity(), "Enter your registered email id",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("Resetting password", "Loading..");
//You can send a password reset email to a user with the sendPasswordResetEmail method.
        fAuth.sendPasswordResetEmail(currentEmailId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
}