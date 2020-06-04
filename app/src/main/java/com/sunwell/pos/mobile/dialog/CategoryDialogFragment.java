package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by sunwell on 10/19/17.
 */

public class CategoryDialogFragment extends DialogFragment
{
    private static final String PROGRESS_ADD_CATEGORY = "progressAddCategory";
    private static String CATEGORY = "category";
    private String color = "#0f7858";
    private EditText inputName;
    private CheckBox cbDef ;
    private LinearLayout panelColor;

    public static CategoryDialogFragment newInstance(ProdCategory _ctgr)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(CategoryDialogFragment.CATEGORY, _ctgr);
        CategoryDialogFragment dialog = new CategoryDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            color = (String) savedInstanceState.getString("color");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.category_dialog);
            inputName = (EditText) dialog.findViewById(R.id.text_name);
            cbDef = (CheckBox) dialog.findViewById(R.id.cb_default);
            panelColor = (LinearLayout) dialog.findViewById(R.id.panel_color);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_category);

            Bundle arguments = getArguments();
            final ProdCategory argCtgr = arguments != null ? (ProdCategory) arguments.get(CategoryDialogFragment.CATEGORY) : null;

//            void validateInput() {
//
//            }

            if (argCtgr != null) {
                inputName.setText(argCtgr.getName());
                cbDef.setChecked(argCtgr.isDefault1());
                if(argCtgr.getBgColor() != null)
                    panelColor.setBackgroundColor(Color.parseColor(argCtgr.getBgColor()));

                btnAdd.setText(R.string.edit_category);
                dialog.setTitle(R.string.edit_category);
            }

//            dialog.setTitle(R.string.add_category);

            inputName.addTextChangedListener(
                    new TextWatcher()
                    {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after)
                        {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count)
                        {

                        }

                        @Override
                        public void afterTextChanged(Editable s)
                        {
                            if (inputName.getText().length() > 0)
                                btnAdd.setEnabled(true);
                            else
                                btnAdd.setEnabled(false);
                        }
                    }
            );

            panelColor.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            colorpicker();
                        }
                    }
            );

            btnClose.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            CategoryDialogFragment.this.dismiss();
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

                                String name = inputName.getText().toString();
                                boolean dflt = cbDef.isChecked();
                                Log.d(Util.APP_TAG, "IS CHECKED: " + dflt);
                                ProdCategory ctgr = new ProdCategory();
                                ctgr.setName(name);
                                ctgr.setBgColor(color);
                                ctgr.setDefault1(dflt);
                                if (argCtgr != null)
                                    ctgr.setSystemId(argCtgr.getSystemId());

                                ResultWatcher<ProdCategory> categoryListener = new ResultWatcher<ProdCategory>()
                                {
                                    @Override
                                    public void onResult(Object source, ProdCategory category)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_CATEGORY );
                                        Intent intent = new Intent();
                                        intent.putExtra("category", category);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        CategoryDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int _errCode)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_CATEGORY );
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", _errCode);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        CategoryDialogFragment.this.dismiss();
                                    }
                                };

                                if (argCtgr == null)
                                    ProductService.addCategory(categoryListener, ctgr);
                                else
                                    ProductService.editCategory(categoryListener, ctgr);

                                Util.showDialog(getFragmentManager(), PROGRESS_ADD_CATEGORY );
                            }
                            catch (Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            return dialog;
        }
        catch (Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("color", color);
    }

    private Boolean validateInput() {
        if(inputName.getText() == null || inputName.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.name_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void colorpicker() {
        //     initialColor is the initially-selected color to be shown in the rectangle on the left of the arrow.
        //     for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware of the initial 0xff which is the alpha.

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), 0xff0000ff,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {

                    // Executes, when user click Cancel button
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog){
                    }

                    // Executes, when user click OK button
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int _color) {
                        panelColor.setBackgroundColor(_color);
                        color = "#" + Integer.toHexString(_color);
                        Log.d(Util.APP_TAG, "COLOR STRING: " + color);
//                        Toast.makeText(getActivity().getBaseContext(), "Selected Color : " + color, Toast.LENGTH_LONG).show();
                    }
                });
        dialog.show();
    }
}
