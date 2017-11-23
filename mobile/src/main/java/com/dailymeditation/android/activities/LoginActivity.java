package com.dailymeditation.android.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dailymeditation.android.R;
import com.dailymeditation.android.utils.LogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verification_in_progress";
    private static final String STATE_SIGNIN_SUCCESS = "SIGNIN_SUCCESS";
    private static final String STATE_SIGNIN_FAILED = "SIGNIN_FAILED";
    private static final String STATE_VERIFICATION_FAILED = "VERIF_FAILED";
    private static final String STATE_COLLECT_CODE = "COLLECT_CODE";
    private static boolean COLLECT_PHONE_STATE = true;

    @BindView(R.id.login_title) TextView mTitle;
    @BindView(R.id.login_phone_field) EditText mPhoneNumber;
    @BindView(R.id.login_continue_button) TextView mSendCode;

    private boolean mVerificationInProgress = false;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog mDialog;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            mDialog.hide();
            mVerificationInProgress = false;
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            mDialog.hide();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                LogUtils.logE("TAG", e);
            } else if (e instanceof FirebaseTooManyRequestsException) {
                LogUtils.logE("TAG", e);
            }
            updateUI(STATE_VERIFICATION_FAILED, e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            mDialog.hide();
            COLLECT_PHONE_STATE = false;
            mVerificationId = s;
            mResendToken = forceResendingToken;
            updateUI(STATE_COLLECT_CODE, null);
        }
    };
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUnbinder = ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mSendCode.setOnClickListener(this);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumber.getText().toString());
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumber.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        mDialog.setMessage("Sending code...");
        mDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        mVerificationInProgress = true;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mDialog.setMessage("Validating...");
        mDialog.show();
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.hide();
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(STATE_SIGNIN_SUCCESS, null);
                        } else {
                            updateUI(STATE_SIGNIN_FAILED, task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateUI(String state, String err) {
        switch (state) {
            case STATE_SIGNIN_SUCCESS:
                mSendCode.setVisibility(View.GONE);
                mTitle.setText("Welcome to Daily Meditation!");
                mPhoneNumber.setVisibility(View.GONE);
                break;
            case STATE_COLLECT_CODE:
                mSendCode.setText("VALIDATE");
                mTitle.setText("Enter the code you received");
                mPhoneNumber.setHint("Validation Code");
                break;
            case STATE_VERIFICATION_FAILED:
            case STATE_SIGNIN_FAILED:
                mTitle.setText(err);
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    public void onClick(View view) {
        if (COLLECT_PHONE_STATE) {
            if (validatePhoneNumber()) {
                startPhoneNumberVerification(mPhoneNumber.getText().toString());
            }
        } else {
            verifyPhoneNumberWithCode(mVerificationId, mPhoneNumber.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
