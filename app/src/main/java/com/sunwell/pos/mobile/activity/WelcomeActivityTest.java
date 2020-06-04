package com.sunwell.pos.mobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.Tenant;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

public class WelcomeActivityTest extends AppCompatActivity
{

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            Log.d(Util.APP_TAG, " ON CREATE In TEST CALLED");
            setContentView(R.layout.activity_welcome);
            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            loginPrefsEditor = loginPreferences.edit();
            Button btnLogin = (Button) findViewById(R.id.button_login);
            Button btnRegister = (Button) findViewById(R.id.button_register);
            final Intent loginIntent = new Intent(WelcomeActivityTest.this, LoginActivity.class);
            btnLogin.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                startActivity(loginIntent);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(WelcomeActivityTest.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

//            boolean saveLogin = loginPreferences.getBoolean("saveLogin", false);
//
//            if(saveLogin) {
//                final String companyEmail = loginPreferences.getString("tenantEmail", "");
//                final String tenantId = loginPreferences.getString("tenantId", "");
//                final String email = loginPreferences.getString("email", "");
//                final String password = loginPreferences.getString("password", "");
//                LoginService.companyLogin(companyEmail,
//                        new ResultWatcher<Tenant>()
//                        {
//                            @Override
//                            public void onResult(Object source, Tenant result) throws Exception
//                            {
//                                Intent intent = new Intent(WelcomeActivityTest.this, LoginActivity2.class);
//                                intent.putExtra("companyName", result.getName());
//                                startActivity(intent);
////                                LoginService.login(email, password,
////                                        new ResultWatcher<User>()
////                                        {
////                                            @Override
////                                            public void onResult(Object source, User result) throws Exception
////                                            {
////                                                super.onResult(source, result);
////                                            }
////
////                                            @Override
////                                            public void onError(Object source, int errCode) throws Exception
////                                            {
////                                                Log.d(Util.APP_TAG, "Error code: " + errCode);
////                                                Toast.makeText(WelcomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
////                                            }
////                                        }
////                                );
//                            }
//
//                            @Override
//                            public void onError(Object source, int errCode) throws Exception
//                            {
//                                Log.d(Util.APP_TAG, "Error code: " + errCode);
//                                Toast.makeText(WelcomeActivityTest.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                );
//            }

        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
            MenuItem loginItem = menu.findItem(R.id.menu_item_login);
            loginItem.setOnMenuItemClickListener(
                    new MenuItem.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            final Intent loginIntent = new Intent(WelcomeActivityTest.this, LoginActivity.class);
                            startActivity(loginIntent);
                            return true;
                        }
                    }
            );

            return true;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
