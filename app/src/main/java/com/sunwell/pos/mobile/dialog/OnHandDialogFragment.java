package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.IncomingGood;
import com.sunwell.pos.mobile.model.OnHandStock;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.Warehouse;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.service.InventoryService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/19/17.
 */

public class OnHandDialogFragment extends DialogFragment {

//    private static final String PROGRESS_FETCH_WAREHOUSE = "progressFetchWarehouse";
    private static final String PROGRESS_FETCH_PRODUCT = "progressFetchProduct";
    private static final String PROGRESS_ADD_INCOMING_GOOD = "progressAddOnHandStock";
    private Date incomingDate;
//    private Product product;
    private List<Product> products;
//    private List<Date> incomingDates;
    private EditText inputMemo;
    private EditText inputDate ;
//    private EditText inputQty ;
//    private EditText inputPrice;
//    private AutoCompleteTextView inputProduct ;
    private Spinner spinnerWarehouse ;
    private LinearLayout panelItem;


//    public static OnHandDialogFragment newInstance(User _user) {
//        Log.d(Util.APP_TAG, "newInstance created");
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(OnHandDialogFragment.USER, _user);
//        OnHandDialogFragment dialog = new OnHandDialogFragment();
//        dialog.setArguments(bundle);
//        return dialog;
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            products = (LinkedList<Product>) savedInstanceState.getSerializable("products");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.onhand_dialog);
            TextView textAddProduct = (TextView) dialog.findViewById(R.id.text_add_product);
            final AutoCompleteTextView inputProduct = (AutoCompleteTextView) dialog.findViewById(R.id.input_product);
//            inputQty = (EditText) dialog.findViewById(R.id.input_qty);
//            inputPrice = (EditText) dialog.findViewById(R.id.input_price);
            inputMemo = (EditText) dialog.findViewById(R.id.input_memo);
            inputDate = (EditText) dialog.findViewById(R.id.input_date);
            spinnerWarehouse = (Spinner) dialog.findViewById(R.id.spinner_warehouse);
            panelItem = (LinearLayout) dialog.findViewById(R.id.panel_item);
            ImageButton btnDate = (ImageButton) dialog.findViewById(R.id.button_date);
            Button btnClose = (Button) dialog.findViewById(R.id.button_close);
            final Button btnAdd = (Button) dialog.findViewById(R.id.button_add_user);

            textAddProduct.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                addNewItemRow();
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnDate.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                DatePickerDialogFragment dlg = new DatePickerDialogFragment();
                                dlg.setTargetFragment(OnHandDialogFragment.this, Util.REQUEST_CODE_PICK);
                                dlg.show(getFragmentManager(), "pickDate");
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
                            OnHandDialogFragment.this.dismiss();
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

                                List<IncomingGood> listIncomingGood = new LinkedList<>();

                                for(int i = 1; i < panelItem.getChildCount(); i++) {

                                    LinearLayout itemRow = (LinearLayout) panelItem.getChildAt(i);

                                    AutoCompleteTextView inputProduct = (AutoCompleteTextView) itemRow.findViewById(R.id.input_product);
                                    EditText inputQty = (EditText) itemRow.findViewById(R.id.input_qty);
                                    EditText inputPrice = (EditText) itemRow.findViewById(R.id.input_price);
//                                    Spinner spinnerWarehouse = (Spinner) itemRow.findViewById(R.id.spinner_warehouse);

                                    String name = inputProduct.getText().toString();
                                    String memo = inputMemo.getText() != null ? inputMemo.getText().toString() : null;
                                    Double qty = Double.parseDouble(inputQty.getText().toString());
                                    Double price = Double.parseDouble(inputPrice.getText().toString());
                                    Product prod = ProductService.findProductByName(name);
                                    Warehouse wrhs = (Warehouse) spinnerWarehouse.getSelectedItem();

                                    IncomingGood ic = new IncomingGood();
                                    ic.setProduct(prod);
                                    ic.setWarehouse(wrhs);
                                    ic.setQty(qty);
                                    ic.setUnitPrice(price);
                                    ic.setMemo(memo);
                                    ic.setIncomingDate(incomingDate);
                                    listIncomingGood.add(ic);
                                }

//                                Map<String, String> params = new HashMap<String, String>();
//                                params.put("productId", )

                                ResultWatcher<List<IncomingGood>> listener = new ResultWatcher<List<IncomingGood>>()
                                {

                                    @Override
                                    public void onResult(Object source, List<IncomingGood> result)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_INCOMING_GOOD);
                                        Intent intent = new Intent();
                                        intent.putExtra("listIncomingGood", new LinkedList<>(result));
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_SUCCESS, intent);
                                        OnHandDialogFragment.this.dismiss();
                                    }

                                    @Override
                                    public void onError(Object source, int errCode)
                                    {
                                        Util.stopDialog(PROGRESS_ADD_INCOMING_GOOD);
                                        Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                                        Intent intent = new Intent();
                                        intent.putExtra("errorCode", errCode);
                                        if (getTargetFragment() != null)
                                            getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
                                        OnHandDialogFragment.this.dismiss();
                                    }
                                };

//                                InventoryService.fetchOnHandStocks(
//                                        new ResultWatcher<List<OnHandStock>>()
//                                        {
//                                            @Override
//                                            public void onResult(Object source, List<OnHandStock> result) throws Exception
//                                            {
//                                                super.onResult(source, result);
//                                            }
//
//                                            @Override
//                                            public void onError(Object source, int errCode)
//                                            {
//                                                Util.stopDialog(PROGRESS_ADD_INCOMING_GOOD);
//                                                Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
//                                                Intent intent = new Intent();
//                                                intent.putExtra("errorCode", errCode);
//                                                if (getTargetFragment() != null)
//                                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Util.RESULT_CODE_FAILED, intent);
//                                                OnHandDialogFragment.this.dismiss();
//                                            }
//                                        },
//                                );
                                InventoryService.addIncomingGoods(listener, listIncomingGood);

                                Util.showDialog(getFragmentManager(), PROGRESS_ADD_INCOMING_GOOD);
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            final List<Warehouse> list = new ArrayList<>();
            InventoryService.fillWarehouseSpinner(spinnerWarehouse, getActivity(), getFragmentManager());
            ProductService.fillProductAutoComplete(inputProduct, getActivity(), getFragmentManager());

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
                    incomingDate = (Date)data.getSerializableExtra(DatePickerDialogFragment.DATE);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    inputDate.setText(sdf.format(incomingDate));
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(products != null && products.size() > 0)
            outState.putSerializable("products", (LinkedList<Product>) products);
    }

    private Boolean validateInput() {

        int count = panelItem.getChildCount() ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        products = new LinkedList<>();

        if (spinnerWarehouse.getSelectedItemPosition() <= 0) {
            Toast.makeText(getActivity(), R.string.warehouse_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (incomingDate == null) {
            Toast.makeText(getActivity(), R.string.date_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        for(int i = 1 ; i < count ; i++) {

            LinearLayout itemRow = (LinearLayout) panelItem.getChildAt(i);

            AutoCompleteTextView inputProduct = (AutoCompleteTextView) itemRow.findViewById(R.id.input_product);
            EditText inputQty = (EditText) itemRow.findViewById(R.id.input_qty);
            EditText inputPrice = (EditText) itemRow.findViewById(R.id.input_price);

            if (inputProduct.getText() == null || inputProduct.getText().toString().length() <= 0) {
                Toast.makeText(getActivity(), R.string.name_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            Product product = ProductService.findProductByName(inputProduct.getText().toString());

            if (product == null) {
                Toast.makeText(getActivity(), R.string.cannot_find_the_specified_product, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (inputQty.getText() == null || inputQty.getText().toString().length() <= 0) {
                Toast.makeText(getActivity(), R.string.qty_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (inputPrice.getText() == null || inputPrice.getText().toString().length() <= 0) {
                Toast.makeText(getActivity(), R.string.price_field_cannot_be_empty, Toast.LENGTH_SHORT).show();
                return false;
            }

            products.add(product);
        }

        return true;
    }

    private void addNewItemRow() throws Exception {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final LinearLayout row = (LinearLayout)inflater.inflate(R.layout.onhand_item, panelItem, false);
        ImageButton btnDelete = (ImageButton)row.findViewById(R.id.button_delete_item);
        AutoCompleteTextView inputProduct = (AutoCompleteTextView) row.findViewById(R.id.input_product);
        btnDelete.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        panelItem.removeView(row);
                    }
                }
        );
        panelItem.addView(row);
        ProductService.fillProductAutoComplete(inputProduct, getActivity(), getFragmentManager());
    }

    private void fillAutoComplete(final AutoCompleteTextView _input) throws Exception {
        if(ProductService.products != null) {
            ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, ProductService.products);
            _input.setAdapter(adapter);
        }
        else {
            ProductService.fetchProducts(
                    new ResultWatcher<List<Product>>()
                    {
                        @Override
                        public void onResult(Object source, List<Product> result) throws Exception
                        {
                            Util.stopDialog(PROGRESS_FETCH_PRODUCT);

                            if(result == null || result.size() <= 0)
                                return;

                            ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(getActivity(),
                                    android.R.layout.simple_dropdown_item_1line, result);
                            _input.setAdapter(adapter);
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception
                        {
                            Util.stopDialog(PROGRESS_FETCH_PRODUCT);
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_PRODUCT);
        }
    };

    private void removeItemRow(View _v) {
        panelItem.removeView(_v);
    }
}
