package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.model.UserGroup;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/19/17.
 */

public class UserDialogFragment extends DialogFragment {

    private static final String PROGRESS_FETCH_USER_GROUP = "progressFetchUserGroup";
    private static final String PROGRESS_ADD_USER = "progressAddUser";
    private static String USER = "user";
    private Map<String, String> image ;
    private TextView textImage;
    private EditText inputName ;
    private EditText inputEmail ;;
    private Spinner spinnerUserGroup ;


    public static UserDialogFragment newInstance(User _user) {
        Log.d(Util.APP_TAG, "newInstance created");
        Bundle bundle = new Bundle();
        bundle.putSerializable(UserDialogFragment.USER, _user);
        UserDialogFragment dialog = new UserDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        image = new HashMap<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            Log.d(Util.APP_TAG, "onCreateDialog created");
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.user_dialog);
            TextView textTitle = (TextView) dialog.findViewById(R.id.text_title);
            inputName = (EditText) dialog.findViewById(R.id.input_name);
            inputEmail = (EditText) dialog.findViewById(R.id.input_email);
            spinnerUserGroup = (Spinner) dialog.findViewById(R.id.spinner_user_group);
            Button btnChooseImage = (Button) dialog.findViewById(R.id.button_choose_image);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_user);
            Bundle arguments = getArguments();
            final User argUser = arguments != null ? (User) arguments.get(UserDialogFragment.USER) : null;

            btnAdd.setText(R.string.add_user);

            if (argUser != null) {
                inputName.setText(argUser.getName());
                inputEmail.setText(argUser.getEmail());

                btnAdd.setText(R.string.edit_user);
                textTitle.setText(R.string.edit_user);
                dialog.setTitle(R.string.edit_user);
            }

            btnChooseImage.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, Util.REQUEST_CODE_PICK);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnClose.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            UserDialogFragment.this.dismiss();
                        }
                    }
            );

            btnAdd.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if(!validateInput())
                                    return;

                                Log.d(Util.APP_TAG, "ADD CALLED");
                                String name = inputName.getText().toString();
                                String email = inputEmail.getText().toString();
                                UserGroup ug = (UserGroup) spinnerUserGroup.getSelectedItem();
                                User user = new User();
                                user.setName(name);
                                user.setEmail(email);
                                user.setUserGroup(ug);

                                if (argUser != null) {
                                    user.setSystemId(argUser.getSystemId());
                                }

                                if (!image.isEmpty()) {
                                    Log.d(Util.APP_TAG, "NOt NULL MAP");
                                    user.setImg(image.get("name"));
                                    user.setImgData(image.get("data"));
                                }

                                ResultWatcher<User> listener = new ResultWatcher<User>()
                                {

                                    @Override
                                    public void onResult(Object source, User result)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_USER);
                                        Intent intent = new Intent();
                                        intent.putExtra("user", result);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        UserDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int errCode)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_USER);
                                        Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", errCode);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        UserDialogFragment.this.dismiss();
                                    }
                                };
                                if (argUser == null)
                                    LoginService.addUser(listener, user);
                                else
                                    LoginService.editUser(listener, user);

                                Util.showDialog(getFragmentManager(), PROGRESS_ADD_USER);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            final List<UserGroup> list = new ArrayList<>();
            list.add(new UserGroup());
            if (LoginService.userGroups != null) {
                for (UserGroup ug : LoginService.userGroups) {
                    list.add(ug);
                }
                Util.fillSpinner(spinnerUserGroup, list, UserGroup.class, getActivity());
                if(argUser != null)
                    spinnerUserGroup.setSelection(((ArrayAdapter)spinnerUserGroup.getAdapter()).getPosition(argUser.getUserGroup()));
            } else {
                LoginService.fetchUserGroups(
                        new ResultWatcher<List<UserGroup>>()
                        {
                            @Override
                            public void onResult(Object source, List<UserGroup> result)
                            {
                                Util.stopDialog(PROGRESS_FETCH_USER_GROUP);
                                if (result != null) {
                                    for (UserGroup ug : result) {
                                        list.add(ug);
                                    }
                                    Util.fillSpinner(spinnerUserGroup, list, UserGroup.class, getActivity());
                                    if(argUser != null)
                                        spinnerUserGroup.setSelection(((ArrayAdapter)spinnerUserGroup.getAdapter()).getPosition(argUser.getUserGroup()));
                                }
                            }

                            @Override
                            public void onError(Object source, int errCode) throws Exception {
                                Util.stopDialog(PROGRESS_FETCH_USER_GROUP);
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_USER_GROUP);
            }

            return dialog;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onActivityResult(int _requestCode, int _resultCode, Intent data) {
        try {
            super.onActivityResult(_requestCode, _resultCode, data);
            if (_requestCode == Util.REQUEST_CODE_PICK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    Log.d(Util.APP_TAG, "PATH: " + selectedImage.getPath() + " string: " + selectedImage.toString());
                    String imageData = Util.encodeToBase64String(getActivity().getContentResolver().openInputStream(selectedImage));
                    String fileName = null;
                    String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                    Cursor metaCursor = getActivity().getContentResolver().query(selectedImage, projection, null, null, null);
                    if (metaCursor != null) {
                        try {
                            if (metaCursor.moveToFirst()) {
                                fileName = metaCursor.getString(0);
                            }
                        }
                        finally {
                            metaCursor.close();
                        }
                    }
                    Log.d(Util.APP_TAG, "SIZE B64: " + imageData.length() + " NAME: " + fileName);
                    textImage.setText(fileName);
                    image.put("name", fileName);
                    image.put("data", imageData);
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validateInput() {
        if(inputName.getText() == null || inputName.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.name_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(inputEmail.getText() == null || inputEmail.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.email_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(spinnerUserGroup.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.type_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
