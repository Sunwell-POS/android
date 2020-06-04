package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
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

public class ProductDialogFragment extends DialogFragment {

    private static final String PROGRESS_FETCH_CATEGORY = "progressFetchCategory";
    private static final String PROGRESS_ADD_PRODUCT = "progressAddCustomer";
    private static String PRODUCT = "product";
    private List<ProdCategory> categories ;
    private Map<String, String> image ;
    private LinearLayout panelCategory;
    private TextView textNoCtgr;
    private TextView textImage;
    private EditText inputName ;
    private EditText inputPrice ;
    private EditText inputBarcode ;
    private Spinner spinnerCategory ;
    private Spinner spinnerMetric ;
    private CheckBox cbUseStock ;
    private CheckBox cbUseDisc ;
    private CheckBox cbActive ;


    public static ProductDialogFragment newInstance(Product _prod) {
        Log.d(Util.APP_TAG, "newInstance created");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProductDialogFragment.PRODUCT, _prod);
        ProductDialogFragment dialog = new ProductDialogFragment();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        categories = new LinkedList<>();
        image = new HashMap<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            Log.d(Util.APP_TAG, "onCreateDialog created");
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.product_dialog);
            panelCategory = (LinearLayout) dialog.findViewById(R.id.panel_category);
            textNoCtgr = (TextView) dialog.findViewById(R.id.text_no_category);
            textImage = (TextView) dialog.findViewById(R.id.text_image);
            inputName = (EditText) dialog.findViewById(R.id.text_name);
            inputPrice = (EditText) dialog.findViewById(R.id.input_price);
            inputBarcode = (EditText) dialog.findViewById(R.id.input_barcode);
            spinnerCategory = (Spinner) dialog.findViewById(R.id.spinner_category);
            spinnerMetric = (Spinner) dialog.findViewById(R.id.spinner_metric);
            cbUseStock = (CheckBox) dialog.findViewById(R.id.cb_use_stock);
            cbUseDisc = (CheckBox) dialog.findViewById(R.id.cb_use_discount);
            cbActive = (CheckBox) dialog.findViewById(R.id.cb_active);
            Button btnChooseImage = (Button) dialog.findViewById(R.id.button_choose_image);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_category);
            Bundle arguments = getArguments();
            final Product argProd = arguments != null ? (Product) arguments.get(ProductDialogFragment.PRODUCT) : null;

            spinnerCategory.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            try {
                                if (position > 0)
                                    addNewItemcategory((ProdCategory) spinnerCategory.getSelectedItem());
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {

                        }
                    }
            );

            if (argProd != null) {
                inputName.setText(argProd.getName());
                inputPrice.setText(argProd.getPrice() + "");
                inputBarcode.setText(argProd.getBarCode());
                {
                    List<ProdCategory> categories = argProd.getCategories();

                    if (categories != null && categories.size() > 0) {
                        textNoCtgr.setVisibility(View.GONE);

                        for (ProdCategory ctgr : categories) {
                            addNewItemcategory(ctgr);
                        }
                    }
                }

                spinnerMetric.setSelection(((ArrayAdapter) spinnerMetric.getAdapter()).getPosition(argProd.getMetric()));
                cbUseStock.setChecked(argProd.getHasStock());
                cbUseDisc.setChecked(argProd.getHasDiscount());
                cbActive.setChecked(argProd.getStatus());
                btnAdd.setText(R.string.edit_product);
                dialog.setTitle(R.string.edit_product);
            }

//            dialog.setTitle(R.string.add_product);

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

            btnChooseImage.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                            ProductDialogFragment.this.dismiss();
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
                                String barCode = inputBarcode.getText().toString();
                                String metric = (String) spinnerMetric.getSelectedItem();
                                Double price = Double.valueOf(inputPrice.getText().toString());
                                Boolean status = cbActive.isChecked();
                                Boolean hasStock = cbUseStock.isChecked();
                                Boolean hasDiscount = cbUseDisc.isChecked();
                                Product prod = new Product();
                                prod.setName(name);
                                prod.setPrice(price);
                                prod.setBarCode(barCode);
                                prod.setMetric(metric);
                                prod.setStatus(status);
                                prod.setHasStock(hasStock);
                                prod.setHasDiscount(hasDiscount);
                                if (categories.size() > 0)
                                    prod.setCategories(categories);
                                if (argProd != null) {
                                    prod.setSystemId(argProd.getSystemId());
                                }

                                if (!image.isEmpty()) {
                                    Log.d(Util.APP_TAG, "NOt NULL MAP");
                                    prod.setImg(image.get("name"));
                                    prod.setImgData(image.get("data"));
                                }

                                ResultWatcher<Product> listener = new ResultWatcher<Product>()
                                {

                                    @Override
                                    public void onResult(Object source, Product result)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_PRODUCT);
                                        Intent intent = new Intent();
                                        intent.putExtra("product", result);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        ProductDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int errCode)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_PRODUCT);
                                        Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", errCode);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        ProductDialogFragment.this.dismiss();
                                    }
                                };
                                if (argProd == null)
                                    ProductService.addProduct(listener, prod);
                                else
                                    ProductService.editProduct(listener, prod);

                                Util.showDialog(getFragmentManager(), PROGRESS_ADD_PRODUCT);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            ProductService.fillCategorySpinner(spinnerCategory, getActivity(), getFragmentManager());

//            final List<ProdCategory> list = new ArrayList<>();
//            list.add(new ProdCategory());
//            if (ProductService.categories != null) {
//                for (ProdCategory ctgr : ProductService.categories) {
//                    list.add(ctgr);
//                }
//                Util.fillSpinner(spinnerCategory, list, ProdCategory.class, getActivity());
//            } else {
//                ProductService.fetchCategories(
//                        new ResultWatcher<List<ProdCategory>>()
//                        {
//                            @Override
//                            public void onResult(Object source, List<ProdCategory> result)
//                            {
//                                Util.stopDialog(PROGRESS_FETCH_CATEGORY);
//                                if (result != null) {
//                                    for (ProdCategory ctgr : result) {
//                                        list.add(ctgr);
//                                    }
//                                    Util.fillSpinner(spinnerCategory, list, ProdCategory.class, getActivity());
//                                }
//                            }
//
//                            @Override
//                            public void onError(Object source, int errCode) throws Exception {
//                                Util.stopDialog(PROGRESS_FETCH_CATEGORY);
//                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                );
//                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_CATEGORY);
//            }

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

    private void addNewItemcategory(final ProdCategory _ctgr) {

        for(ProdCategory c : categories) {
            if(_ctgr.equals(c)) {
                return;
            }
        }

        if(textNoCtgr.getVisibility() == View.VISIBLE) {
            textNoCtgr.setVisibility(View.GONE);
        }

        final LinearLayout linearLayout = new LinearLayout(getActivity());
        TextView txtCtgr = new TextView(getActivity());
        ImageButton btnDlt = new ImageButton(getActivity());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.leftMargin = Util.dpToPx(18, getActivity());

        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setLayoutParams(p);

        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.weight = 1;

        txtCtgr.setText(_ctgr.getName());
        txtCtgr.setLayoutParams(p);

        p = new LinearLayout.LayoutParams(Util.dpToPx(32, getActivity()), Util.dpToPx(32, getActivity()));
        p.leftMargin = Util.dpToPx(6, getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            btnDlt.setImageDrawable(getResources().getDrawable(R.drawable.delete, getActivity().getTheme()));
        else
            btnDlt.setImageDrawable(getResources().getDrawable(R.drawable.delete));

        btnDlt.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnDlt.setLayoutParams(p);

        btnDlt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        panelCategory.removeView(linearLayout);
                        categories.remove(_ctgr);
                    }
                }
        );

        linearLayout.addView(txtCtgr);
        linearLayout.addView(btnDlt);
        categories.add(_ctgr);
        panelCategory.addView(linearLayout);
    }

    private Boolean validateInput() {
        if(inputName.getText() == null || inputName.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.name_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(inputPrice.getText() == null || inputPrice.getText().toString().length() <= 0) {
            Toast.makeText(getActivity(), R.string.price_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(spinnerMetric.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.metric_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
