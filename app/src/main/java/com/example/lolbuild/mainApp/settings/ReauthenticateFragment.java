package com.example.lolbuild.mainApp.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lolbuild.R;
import com.example.lolbuild.authentication.AuthenticationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;


public class ReauthenticateFragment extends Fragment {

    @NotEmpty
    @Email
    private EditText emailEditText;
    @NotEmpty
    private EditText passwordEditText;
    private FirebaseAuth auth;
    private Validator validator;
    private NavController navController;

    public ReauthenticateFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reauthenticate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        Button signInButton = view.findViewById(R.id.signInButton);
        TextView forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);

        navController = Navigation.findNavController(view);

        auth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();

        ReauthenticateFragmentArgs args = ReauthenticateFragmentArgs.fromBundle(getArguments());
        String newEmail = args.getNewEmail();
        String newPassword = args.getNewPassword();
        boolean deleteAccount = args.getDeleteAccount();

        validator = new Validator(this);
        Validator.ValidationListener validationListener = new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                String email = emailEditText.getText().toString();
                String pwd = passwordEditText.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(email, pwd);
                auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (newEmail != null) {
                                auth.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            navController.navigate(R.id.action_reauthenticateFragment_to_settingsFragment);
                                            Toast.makeText(getContext(), "Successfully updated email address.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                navController.popBackStack();
                                                Toast.makeText(getContext(), "Invalid email address.", Toast.LENGTH_SHORT).show();
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                navController.popBackStack();
                                                Toast.makeText(getContext(), "The email address has already been taken.", Toast.LENGTH_SHORT).show();
                                            } catch (FirebaseAuthInvalidUserException e) {
                                                Intent myIntent = new Intent(getContext(), AuthenticationActivity.class);
                                                getActivity().finish();
                                                startActivity(myIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                navController.popBackStack();
                                                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            } else if (newPassword != null) {
                                auth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            navController.navigate(R.id.action_reauthenticateFragment_to_settingsFragment);
                                            Toast.makeText(getContext(), "Successfully updated password..", Toast.LENGTH_SHORT).show();
                                        } else {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                navController.popBackStack();
                                                Toast.makeText(getContext(), "Provided password is too weak.", Toast.LENGTH_SHORT).show();
                                            } catch (FirebaseAuthInvalidUserException e) {
                                                Intent myIntent = new Intent(getContext(), AuthenticationActivity.class);
                                                getActivity().finish();
                                                startActivity(myIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                navController.popBackStack();
                                                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                            } else if (deleteAccount) {
                                auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Successfully deleted your account. Wait 5 second to sign you out.", Toast.LENGTH_SHORT).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    auth.signOut();
                                                    Intent myIntent = new Intent(getContext(), AuthenticationActivity.class);
                                                    getActivity().finish();
                                                    startActivity(myIntent);
                                                }
                                            }, 5000);
                                        }
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidUserException e) {
                                            Intent myIntent = new Intent(getContext(), AuthenticationActivity.class);
                                            getActivity().finish();
                                            startActivity(myIntent);
                                        } catch (Exception e) {
                                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                            navController.popBackStack();
                                        }
                                    }
                                });
                            }
                            navController.popBackStack();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                emailEditText.setError("Such user doesn't exist.");
                                passwordEditText.setError("Such user doesn't exist.");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                passwordEditText.setError("Incorrect password.");
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                Log.e("Auth Exception", e.getMessage());
                            }
                        }
                    }
                });
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    View view = error.getView();
                    String message = error.getCollatedErrorMessage(getContext());
                    if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                    }
                }
            }
        };
        validator.setValidationListener(validationListener);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_singInFragment_to_resetPasswordFragment);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }
}
