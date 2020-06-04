package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.AccessRight;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.UserGroup;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/19/17.
 */

public class UserGroupDialogFragment extends DialogFragment {

    private static String USER_GROUP = "user_group";
//    TextView textNoCtgr;
//    TextView textImage;
//    Map<String, String> image = new HashMap<>();
//    private List<UserGroup> userGroups = new LinkedList<>();
    private EditText inputName;
//    private LinearLayout panelCategory;

    public static UserGroupDialogFragment newInstance(UserGroup _ug) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(UserGroupDialogFragment.USER_GROUP, _ug);
        UserGroupDialogFragment dialog = new UserGroupDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.user_group_dialog);
            inputName = (EditText) dialog.findViewById(R.id.input_name);
            final Spinner spinnerPOS = (Spinner) dialog.findViewById(R.id.spinner_pos);
            final Spinner spinnerReport = (Spinner) dialog.findViewById(R.id.spinner_report);
            final Spinner spinnerProduct = (Spinner) dialog.findViewById(R.id.spinner_product);
            final Spinner spinnerStaff = (Spinner) dialog.findViewById(R.id.spinner_staff);
            final Spinner spinnerCustomer = (Spinner) dialog.findViewById(R.id.spinner_customer);
            final Spinner spinnerPayment = (Spinner) dialog.findViewById(R.id.spinner_payment);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_user_group);

            Bundle arguments = getArguments();
            final UserGroup argUG = arguments != null ? (UserGroup) arguments.get(UserGroupDialogFragment.USER_GROUP) : null;

            dialog.setTitle(R.string.add_product);
            if (argUG != null) {
                inputName.setText(argUG.getName());

                if (argUG.getAccessRights() != null && argUG.getAccessRights().size() > 0) {
                    List<AccessRight> accessRights = argUG.getAccessRights();
                    List<Integer> acIds = new LinkedList<>();
                    for (AccessRight ac : accessRights) {
                        acIds.add(ac.getSystemId());
                    }

                    Log.d(Util.APP_TAG, " SIZE: " + acIds.size() + " INDEX: " + acIds.indexOf(AccessRight.TASK_ADD_PRODUCT));

                    boolean isCreateAllowed = acIds.contains(AccessRight.TASK_ADD_PRODUCT) ;
                    boolean isReadAllowed = acIds.contains(AccessRight.TASK_VIEW_PRODUCT);

                    if (isCreateAllowed && isReadAllowed)
                        spinnerProduct.setSelection(3);
                    else if (isCreateAllowed)
                        spinnerCustomer.setSelection(2);
                    else if (isReadAllowed)
                        spinnerCustomer.setSelection(1);
                    else
                        spinnerCustomer.setSelection(0);

                    isCreateAllowed = acIds.contains(AccessRight.TASK_ADD_STAFF);
                    isReadAllowed = acIds.contains(AccessRight.TASK_VIEW_STAFF);

                    if (isCreateAllowed && isReadAllowed)
                        spinnerStaff.setSelection(3);
                    else if (isCreateAllowed)
                        spinnerStaff.setSelection(2);
                    else if (isReadAllowed)
                        spinnerStaff.setSelection(1);
                    else
                        spinnerStaff.setSelection(0);

                    isCreateAllowed = acIds.contains(AccessRight.TASK_ADD_CUSTOMER);
                    isReadAllowed = acIds.contains(AccessRight.TASK_VIEW_CUSTOMER);

                    if (isCreateAllowed && isReadAllowed)
                        spinnerCustomer.setSelection(3);
                    else if (isCreateAllowed)
                        spinnerCustomer.setSelection(2);
                    else if (isReadAllowed)
                        spinnerCustomer.setSelection(1);
                    else
                        spinnerCustomer.setSelection(0);

                    isCreateAllowed = acIds.contains(AccessRight.TASK_ADD_PAYMENT);
                    isReadAllowed = acIds.contains(AccessRight.TASK_VIEW_PAYMENT);

                    if (isCreateAllowed && isReadAllowed)
                        spinnerPayment.setSelection(3);
                    else if (isCreateAllowed)
                        spinnerPayment.setSelection(2);
                    else if (isReadAllowed)
                        spinnerPayment.setSelection(1);
                    else
                        spinnerPayment.setSelection(0);

                    isReadAllowed = acIds.contains(AccessRight.TASK_VIEW_REPORT);

                    if (isReadAllowed)
                        spinnerReport.setSelection(1);

                    isCreateAllowed = acIds.contains(AccessRight.TASK_INPUT_INVOICE);

                    if (isCreateAllowed)
                        spinnerPOS.setSelection(2);

                }

                btnAdd.setText(R.string.edit_user_group);
                dialog.setTitle(R.string.edit_user_group);
            }


            btnClose.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            UserGroupDialogFragment.this.dismiss();
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
                                UserGroup ug = new UserGroup();
                                List<AccessRight> accessRights = new LinkedList<>();

                                ug.setName(name);

                                if (spinnerProduct.getSelectedItemPosition() > 0) {
                                    AccessRight acrProduct = new AccessRight();
                                    if (spinnerProduct.getSelectedItemPosition() == 1) {
                                        acrProduct.setSystemId(AccessRight.TASK_VIEW_PRODUCT);
                                    } else if (spinnerProduct.getSelectedItemPosition() == 2) {
                                        acrProduct.setSystemId(AccessRight.TASK_ADD_PRODUCT);
                                    } else if (spinnerProduct.getSelectedItemPosition() == 3) {
                                        AccessRight acrProduct2 = new AccessRight();
                                        acrProduct.setSystemId(AccessRight.TASK_VIEW_PRODUCT);
                                        acrProduct2.setSystemId(AccessRight.TASK_ADD_PRODUCT);
                                        accessRights.add(acrProduct2);
                                    }
                                    accessRights.add(acrProduct);
                                }

                                if (spinnerStaff.getSelectedItemPosition() > 0) {
                                    AccessRight acrStaff = new AccessRight();
                                    if (spinnerStaff.getSelectedItemPosition() == 1) {
                                        acrStaff.setSystemId(AccessRight.TASK_VIEW_STAFF);
                                    } else if (spinnerStaff.getSelectedItemPosition() == 2) {
                                        acrStaff.setSystemId(AccessRight.TASK_ADD_STAFF);
                                    } else if (spinnerStaff.getSelectedItemPosition() == 3) {
                                        AccessRight acrStaff2 = new AccessRight();
                                        acrStaff.setSystemId(AccessRight.TASK_VIEW_STAFF);
                                        acrStaff2.setSystemId(AccessRight.TASK_ADD_STAFF);
                                        accessRights.add(acrStaff2);
                                    }
                                    accessRights.add(acrStaff);
                                }

                                if (spinnerCustomer.getSelectedItemPosition() > 0) {
                                    AccessRight acrCustomer = new AccessRight();
                                    if (spinnerCustomer.getSelectedItemPosition() == 1) {
                                        acrCustomer.setSystemId(AccessRight.TASK_VIEW_CUSTOMER);
                                    } else if (spinnerCustomer.getSelectedItemPosition() == 2) {
                                        acrCustomer.setSystemId(AccessRight.TASK_ADD_CUSTOMER);
                                    } else if (spinnerCustomer.getSelectedItemPosition() == 3) {
                                        AccessRight acrCustomer2 = new AccessRight();
                                        acrCustomer.setSystemId(AccessRight.TASK_VIEW_CUSTOMER);
                                        acrCustomer2.setSystemId(AccessRight.TASK_ADD_CUSTOMER);
                                        accessRights.add(acrCustomer2);
                                    }
                                    accessRights.add(acrCustomer);
                                }

                                if (spinnerPayment.getSelectedItemPosition() > 0) {
                                    AccessRight acrPayment = new AccessRight();
                                    if (spinnerPayment.getSelectedItemPosition() == 1) {
                                        acrPayment.setSystemId(AccessRight.TASK_VIEW_PAYMENT);
                                    } else if (spinnerPayment.getSelectedItemPosition() == 2) {
                                        acrPayment.setSystemId(AccessRight.TASK_ADD_PAYMENT);
                                    } else if (spinnerPayment.getSelectedItemPosition() == 3) {
                                        AccessRight acrPayment2 = new AccessRight();
                                        acrPayment.setSystemId(AccessRight.TASK_VIEW_PAYMENT);
                                        acrPayment2.setSystemId(AccessRight.TASK_ADD_PAYMENT);
                                        accessRights.add(acrPayment2);
                                    }
                                    accessRights.add(acrPayment);
                                }

                                if (spinnerPOS.getSelectedItemPosition() > 0) {
                                    AccessRight acrPos = new AccessRight();
                                    if (spinnerPOS.getSelectedItemPosition() == 2 || spinnerPayment.getSelectedItemPosition() == 3) {
                                        acrPos.setSystemId(AccessRight.TASK_INPUT_INVOICE);
                                        accessRights.add(acrPos);
                                    }
                                }

                                if (spinnerReport.getSelectedItemPosition() > 0) {
                                    AccessRight acrReport = new AccessRight();
                                    if (spinnerReport.getSelectedItemPosition() == 1 || spinnerPayment.getSelectedItemPosition() == 3) {
                                        acrReport.setSystemId(AccessRight.TASK_VIEW_REPORT);
                                        accessRights.add(acrReport);
                                    }
                                }

                                ug.setName(name);
                                if (accessRights.size() > 0)
                                    ug.setAccessRights(accessRights);

                                ResultWatcher<UserGroup> listener = new ResultWatcher<UserGroup>()
                                {

                                    @Override
                                    public void onResult(Object source, UserGroup result) throws Exception
                                    {
                                        Intent intent = new Intent();
                                        intent.putExtra("userGroup", result);
                                        getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        UserGroupDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int errCode)
                                    {
                                        Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", errCode);
                                        getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        UserGroupDialogFragment.this.dismiss();
                                    }
                                };
                                if (argUG == null)
                                    LoginService.addUserGroup(listener, ug);
                                else {
                                    ug.setSystemId(argUG.getSystemId());
                                    LoginService.editUserGroup(listener, ug);
                                }
                            }
                            catch (Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
            );

            return dialog;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

//    private void addNewItemcategory(final ProdCategory _ctgr) {
//
//        for(ProdCategory c : categories) {
//            if(_ctgr.equals(c)) {
//                return;
//            }
//        }
//
//        if(textNoCtgr.getVisibility() == View.VISIBLE) {
//            textNoCtgr.setVisibility(View.GONE);
//        }
//
//        final LinearLayout linearLayout = new LinearLayout(getActivity());
//        TextView txtCtgr = new TextView(getActivity());
//        ImageButton btnDlt = new ImageButton(getActivity());
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        p.leftMargin = Util.dpToPx(18, getActivity());
//
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        linearLayout.setGravity(Gravity.CENTER);
//        linearLayout.setLayoutParams(p);
//
//        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        p.weight = 1;
//
//        txtCtgr.setText(_ctgr.getName());
//        txtCtgr.setLayoutParams(p);
//
//        p = new LinearLayout.LayoutParams(Util.dpToPx(32, getActivity()), Util.dpToPx(32, getActivity()));
//        p.leftMargin = Util.dpToPx(6, getActivity());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            btnDlt.setImageDrawable(getResources().getDrawable(R.drawable.delete, getActivity().getTheme()));
//        else
//            btnDlt.setImageDrawable(getResources().getDrawable(R.drawable.delete));
//
//        btnDlt.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        btnDlt.setLayoutParams(p);
//
//        btnDlt.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        panelCategory.removeView(linearLayout);
//                        categories.remove(_ctgr);
//                    }
//                }
//        );
//
//        linearLayout.addView(txtCtgr);
//        linearLayout.addView(btnDlt);
//        categories.add(_ctgr);
//        panelCategory.addView(linearLayout);
//    }

//    @Override
//    public void onActivityResult(int _requestCode, int _resultCode, Intent data) {
//        super.onActivityResult(_requestCode, _resultCode, data);
//        Log.d(Util.APP_TAG, "C: " + _requestCode);
//        if(_requestCode == Util.REQUEST_CODE_PICK_IMAGE) {
//            try {
//                if (data != null) {
//                    Uri selectedImage = data.getData();
//                    Log.d(Util.APP_TAG, "PATH: " + selectedImage.getPath() + " string: " + selectedImage.toString() );
//                    String imageData = Util.encodeToBase64String(getActivity().getContentResolver().openInputStream(selectedImage));
////                    imageData = imageData.replace("\\", "");
//                    String fileName = null;
//                    String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
//                    Cursor metaCursor = getActivity().getContentResolver().query(selectedImage, projection, null, null, null);
//                    if (metaCursor != null) {
//                        try {
//                            if (metaCursor.moveToFirst()) {
//                                fileName = metaCursor.getString(0);
//                            }
//                        } finally {
//                            metaCursor.close();
//                        }
//                    }
//                    Log.d(Util.APP_TAG, "SIZE B64: " + imageData.length() + " NAME: " + fileName);
//                    textImage.setText(fileName);
//                    image.put("name", fileName);
//                    image.put("data", imageData);
//                }
//            }
//            catch(Exception _e) {
//                _e.printStackTrace();
//            }
//        }
//    }

    private Boolean validateInput() {
        if(inputName.getText() == null || inputName.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.name_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
