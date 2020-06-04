package com.sunwell.pos.mobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ProgressDialogFragment;
import com.sunwell.pos.mobile.model.Tenant;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

public class LoginActivity2 extends AppCompatActivity
{
    private static final String PROGRESS_LOGIN_COMPANY = "progressLoginCompany";
    private ProgressDialogFragment dialogFragment;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private EditText inputEmail ;
    private EditText inputPassword ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setup();
        }
        catch (Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setup()
    {
        setContentView(R.layout.activity_login2);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        Button btnLogin = (Button) findViewById(R.id.button_login);
        TextView textCompany = (TextView) findViewById(R.id.text_company);
        final CheckBox cbRemember = (CheckBox) findViewById(R.id.checkbox_remember);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        String companyName = getIntent().getStringExtra("companyName");

        boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if(saveLogin) {
            final String companyEmail = loginPreferences.getString("tenantEmail", "");
            final String tenantId = loginPreferences.getString("tenantId", "");
            final String email = loginPreferences.getString("email", "");
            final String password = loginPreferences.getString("password", "");

            inputEmail.setText(email);
            inputPassword.setText(password);
            cbRemember.setChecked(true);
        }

        final ResultWatcher<User> listener = new ResultWatcher<User>()
        {
            @Override
            public void onResult(Object source, User user) throws Exception
            {
//                dialogFragment.dismiss();
                Util.stopDialog(PROGRESS_LOGIN_COMPANY);
                Intent intent = new Intent(LoginActivity2.this, HomeActivity.class);
                if(cbRemember.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", inputEmail.getText().toString());
                    loginPrefsEditor.putString("tenantEmail", LoginService.currentTenant.getEmail());
                    loginPrefsEditor.putString("password", inputPassword.getText().toString());
                    loginPrefsEditor.commit();
                }
                else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                startActivity(intent);
            }

            @Override
            public void onError(Object source, int errCode) throws Exception
            {
//                dialogFragment.dismiss();
                Util.stopDialog(PROGRESS_LOGIN_COMPANY);
                Toast.makeText(LoginActivity2.this, R.string.fail_login, Toast.LENGTH_SHORT).show();
            }
        };

        textCompany.setText(companyName);
        btnLogin.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try {
//                            dialogFragment = new ProgressDialogFragment();
//                            dialogFragment.setCancelable(false);
//                            dialogFragment.show(getSupportFragmentManager(), "LOADING");
//                            Thread.sleep(2000);
                            if(!validateInput())
                                return ;

                            Util.showDialog(getSupportFragmentManager(), PROGRESS_LOGIN_COMPANY);
                            LoginService.login(inputEmail.getText().toString(), inputPassword.getText().toString(), listener);
                        }
                        catch (Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private Boolean validateInput() {
        if(inputEmail.getText() == null || inputEmail.getText().toString().length() <= 0) {
            Toast.makeText(this, R.string.email_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(inputPassword.getText() == null || inputPassword.getText().toString().length() <= 0) {
            Toast.makeText(this, R.string.password_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
