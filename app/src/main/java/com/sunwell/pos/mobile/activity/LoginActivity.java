package com.sunwell.pos.mobile.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ProgressDialogFragment;
import com.sunwell.pos.mobile.model.Tenant;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

public class LoginActivity extends AppCompatActivity {

    private static final String PROGRESS_LOGIN = "progressLogin";
    private ProgressDialogFragment dialogFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setup();
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setup() {
        dialogFragment = new ProgressDialogFragment();
        setContentView(R.layout.activity_login);
        final Button btnContinue = (Button)findViewById(R.id.button_continue);
        final EditText inputEmail = (EditText)findViewById(R.id.input_email);
        inputEmail.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(inputEmail.getText().length() > 0)
                        btnContinue.setEnabled(true);
                    else
                        btnContinue.setEnabled(false);
                }
            }
        );

        final ResultWatcher<Tenant> listener = new ResultWatcher<Tenant>() {
            @Override
            public void onResult(Object source, Tenant tenant) throws Exception {
//                Thread.sleep(2000);
//                dialogFragment.dismiss();
                Util.stopDialog(PROGRESS_LOGIN);
                Intent intent = new Intent(LoginActivity.this, LoginActivity2.class);
                intent.putExtra("companyName", tenant.getName());
                startActivity(intent);
            }

            @Override
            public void onError(Object source, int errCode) throws Exception {
//                Thread.sleep(2000);
//                dialogFragment.dismiss();
                Util.stopDialog(PROGRESS_LOGIN);
                Toast.makeText(LoginActivity.this, R.string.incorrect_company_name, Toast.LENGTH_SHORT).show();
            }
        };

        btnContinue.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Util.showDialog(getSupportFragmentManager(), PROGRESS_LOGIN);
//                            dialogFragment.setCancelable(false);
//                            dialogFragment.show(getSupportFragmentManager(), "LOADING");
                            LoginService.companyLogin(inputEmail.getText().toString(), listener);
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
        );

        btnContinue.setEnabled(false);
    }
}
