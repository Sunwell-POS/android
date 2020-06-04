package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ChoosePaymentMethodDialogFragment;
import com.sunwell.pos.mobile.dialog.InvoiceLineDialogFragment;
import com.sunwell.pos.mobile.dialog.PaymentDialogFragment;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class InvoiceFragment_old extends Fragment {

    private static int idLayoutProductRow;
    private static int idLayoutProductHeader;
    private SalesInvoice argSI;
    private PaymentMethodObj paymentMethod;
    private ImageDownloader<ProductHolder> downloader;
    private List<SalesInvoiceLine> siLines = new LinkedList<>();
    private ResultListener<SalesPayment> dialogListener;
    private LayoutInflater layoutInflater ;
    private LinearLayout panelItem;
    private TextView textTotal ;
    private TextView textDisc ;
    private TextView textService ;
    private TextView textTax ;
    private TextView textSubTotal ;
    private TextView textPaymentMethod ;
//    private RecyclerView listProduct;

    private static String SALES_INVOICE = "sales_invoice";

    public static InvoiceFragment_old newInstance(SalesInvoice _si) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(InvoiceFragment_old.SALES_INVOICE, _si);
        InvoiceFragment_old fragment = new InvoiceFragment_old();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (ProductService.categories == null) {
                ProductService.fetchCategories(null);
            }
            Bundle arguments = getArguments();
            argSI = arguments != null ? (SalesInvoice) arguments.get(InvoiceFragment_old.SALES_INVOICE) : null ;

            Handler responseHandler = new Handler();
            Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
            int imagePxSize = 0;
            if(Util.isSmallScreen(getActivity()))
                imagePxSize = Util.dpToPx(R.dimen.small_image, getActivity());
            else
                imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
            downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
            downloader.setThumbnailDownloadListener(
                    new ImageDownloader.DownloadListener<ProductHolder>() {

                        @Override
                        public void onImageDownloaded(ProductHolder _holder, Bitmap _bitmap) throws Exception {
                            Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
                            _holder.bindDrawable(drawable);
                        }

                        @Override
                        public void onImageDownloaded(ProductHolder _holder, Drawable _drawable) throws Exception {
                            _holder.bindDrawable(_drawable);
                        }
                    }
            );
            downloader.start();
            downloader.getLooper();
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.error_product_list, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Log.d(Util.APP_TAG, "OCV CALLED");
            View v = _inflater.inflate(R.layout.invoice_old, container, false);
            layoutInflater = _inflater;
            TextView textInv = (TextView) v.findViewById(R.id.text_no_invoice);
            Button btnPaymentMethod = (Button) v.findViewById(R.id.button_payment_method);
            Button btnMakePayment = (Button) v.findViewById(R.id.button_make_payment);
            textSubTotal = (TextView) v.findViewById(R.id.text_subtotal_amount);
            textDisc = (TextView) v.findViewById(R.id.text_discount_amount);
            textService = (TextView) v.findViewById(R.id.text_service_amount);
            textTax = (TextView) v.findViewById(R.id.text_tax_amount);
            textTotal = (TextView) v.findViewById(R.id.text_total_amount);
            textPaymentMethod = (TextView) v.findViewById(R.id.text_payment_method);
            panelItem = (LinearLayout)v.findViewById(R.id.panel_item);

            textInv.setText(argSI.getNoInvoice());
            if(argSI.getSalesInvoiceLines() != null && argSI.getSalesInvoiceLines().size() > 0) {
                for(SalesInvoiceLine sil : argSI.getSalesInvoiceLines()) {
                    addInvoiceLineRow(sil);
                }
                calculateTotal();
            }

//            listProduct = (RecyclerView) v.findViewById(R.id.list_product);
//            listProduct.setLayoutManager(new GridLayoutManager(getActivity(), 4));

            btnPaymentMethod.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                ChoosePaymentMethodDialogFragment dialog = new ChoosePaymentMethodDialogFragment();
                                dialog.setTargetFragment(InvoiceFragment_old.this, Util.REQUEST_CODE_PICK);
                                dialog.show(getFragmentManager(), "payment_method");
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnMakePayment.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                PaymentDialogFragment dialog = PaymentDialogFragment.newInstance(argSI, paymentMethod);
                                dialog.setTargetFragment(InvoiceFragment_old.this, Util.REQUEST_CODE_CREATE);
                                dialog.show(getFragmentManager(), "payment");
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            if(InvoiceService.paymentMethods == null) {
                InvoiceService.fetchPaymentMethods(
                        new ResultWatcher<List<PaymentMethodObj>>()
                        {
                            @Override
                            public void onResult(Object source, List<PaymentMethodObj> result) throws Exception
                            {
                                if (result != null && result.size() > 0) {
                                    paymentMethod = result.get(0);
                                    textPaymentMethod.setText(paymentMethod.getName());
                                }
                            }

                            @Override
                            public void onError(Object source, int errCode) throws Exception
                            {
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
            else {
                paymentMethod = InvoiceService.paymentMethods.get(0);
                textPaymentMethod.setText(paymentMethod.getName());
            }

            if(ProductService.products == null) {

                ResultWatcher<List<Product>> listener = new ResultWatcher<List<Product>>() {
                    @Override
                    public void onResult(Object source, List<Product> _products) throws Exception {
//                        listProduct.setAdapter(new StockCardAdapter(_products));
//                        RecycleViewAdapter<Product, ? extends RecyclerView.ViewHolder> adapter = buildAdapter(_products);

                            Log.d(Util.APP_TAG, "FRAGMENT: " + getFragmentManager().findFragmentById(R.id.layout_pager));
//                        if(getFragmentManager().findFragmentById(R.id.layout_pager) == null) {
//                            Log.d(Util.APP_TAG, "NULL AND PRODS IS NULL");
                            ProductAdapter adapter = new ProductAdapter(_products);
                            PagerFragment fragment = PagerFragment.newInstance(adapter, 16);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.layout_pager, fragment)
                                    .commit();
//                        }
//                        else
//                            Log.d(Util.APP_TAG, "NOT NULL AND PRODS IS NULL");
                    }

                    @Override
                    public void onError(Object source, int errCode) throws Exception {
                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                };

                ProductService.fetchProducts(listener);
            }
            else {
//                if(getFragmentManager().findFragmentById(R.id.layout_pager) == null) {
//                    Log.d(Util.APP_TAG, "NULL In ELSE");
                    Log.d(Util.APP_TAG, "FRAGMENT: " + getFragmentManager().findFragmentById(R.id.layout_pager));
                    ProductAdapter adapter = new ProductAdapter(ProductService.products);
                    PagerFragment fragment = PagerFragment.newInstance(adapter, 16);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.layout_pager, fragment)
                            .commit();
//                }
//                else
//                    Log.d(Util.APP_TAG, "NOT NULL IN ELSE");
            }
            return v;
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
            if (_requestCode == Util.REQUEST_CODE_ADD) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    SalesInvoiceLine sil = (SalesInvoiceLine) data.getSerializableExtra("salesInvoiceLine");
                    addInvoiceLine(sil);
                    calculateTotal();
                    Toast.makeText(getActivity(), R.string.success_add_invoice_line, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
                    Toast.makeText(getActivity(), R.string.fail_add_invoice_line, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_PICK) {
                paymentMethod = (PaymentMethodObj) data.getSerializableExtra("paymentMethod");
                textPaymentMethod.setText(paymentMethod.getName());
            } else if (_requestCode == Util.REQUEST_CODE_CREATE) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    SalesPayment salesPayment = (SalesPayment) data.getSerializableExtra("salesPayment");
                    if (dialogListener != null)
                        dialogListener.onResult(InvoiceFragment_old.this, salesPayment);
                    else
                        Toast.makeText(getActivity(), R.string.success_create_sales_payment, Toast.LENGTH_SHORT).show();
                } else {
                    int errCode = data.getIntExtra("errorCode", 999);
                    Log.d(Util.APP_TAG, "ERROR CODE: " + errCode);
                    Toast.makeText(getActivity(), R.string.fail_create_sales_payment, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void setDialogListener(ResultListener<SalesPayment> _listener) {
        dialogListener = _listener;
    }

    private void addInvoiceLine(SalesInvoiceLine _sil)
    {
//        if(argSI.getSalesInvoiceLines() == null)
//            argSI.setSalesInvoiceLines(new LinkedList<SalesInvoiceLine>());
//        argSI.getSalesInvoiceLines().add(_sil);
        addInvoiceLineRow(_sil);
    }

    private void addInvoiceLineRow(SalesInvoiceLine _sil) {
        View invoiceLine = layoutInflater.inflate(R.layout.invoice_line, panelItem, false);
        TextView textName = (TextView)invoiceLine.findViewById(R.id.text_name);
        TextView textQty = (TextView)invoiceLine.findViewById(R.id.text_qty);
        TextView textDisc = (TextView)invoiceLine.findViewById(R.id.text_disc);
        TextView textTotal = (TextView)invoiceLine.findViewById(R.id.text_total);
        textName.setText(_sil.getProduct().getName());
        textQty.setText(String.valueOf(_sil.getQty()));
        textDisc.setText(String.valueOf(_sil.getDiscValue()));
        textTotal.setText(String.valueOf(_sil.getSubTotal()));
        panelItem.addView(invoiceLine);
    }

    private void calculateTotal() {
        double subTotal = 0;
        double total = 0;
        double disc = 0;
        double tax = 0;
        double svc = 0;

        if(argSI.getSalesInvoiceLines() != null && argSI.getSalesInvoiceLines().size() > 0) {
            for (SalesInvoiceLine sil : argSI.getSalesInvoiceLines()) {
                subTotal += sil.getSubTotal();
                disc += sil.getRealDiscValue();
                Log.d(Util.APP_TAG, "ST: " + subTotal + "D: " + disc);
            }
        }
//        subTotal =
        total = subTotal - disc;
        svc = 0.05 * total;
        total = total + svc ;
        tax = 0.10 * total;
        total = Math.round((total + svc + tax) * 100.0) / 100;
        subTotal = Math.round(subTotal * 100.0) / 100;
        disc = Math.round(disc * 100.0) / 100;
        svc = Math.round(svc * 100) / 100;
        tax = Math.round( tax * 100) / 100;

        textTotal.setText(String.valueOf(total));
        textDisc.setText(String.valueOf(disc));
        textSubTotal.setText(String.valueOf(subTotal));
        textService.setText(String.valueOf(svc));
        textTax.setText(String.valueOf(tax));
    }



    private class ProductHolder extends RecyclerView.ViewHolder {

        public TextView textName;
        public ImageView imageProduct;


        public ProductHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            imageProduct = (ImageView) itemView.findViewById(R.id.image_product);
        }

        public void bindDrawable(Drawable drawable) {
            imageProduct.setImageDrawable(drawable);
        }

    }

    private class ProductAdapter extends RecycleViewAdapter<Product, ProductHolder>
    {
        public ProductAdapter(List<Product> _products) {
            super(new LinkedList<>(_products), RecycleViewAdapter.PLAIN);
            this.products = _products;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.invoice_item, parent, false);
                return new ProductHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, final int position) {
            try {
                final Product prod = this.products.get(position);
                holder.textName.setText(prod.getName());

                holder.imageProduct.setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                InvoiceLineDialogFragment dialog = InvoiceLineDialogFragment.newInstance(argSI, prod);
                                dialog.setTargetFragment(InvoiceFragment_old.this, Util.REQUEST_CODE_ADD);
                                dialog.show(getFragmentManager(), "salesinvoiceline");
                            }
                        }
                );

                if (prod.getImg() == null)
                    downloader.queueImage(holder, null);
                else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("sessionString", LoginService.sessionString);
                    params.put("image", prod.getImg());
                    downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
                    Log.d(Util.APP_TAG, "Prod: " + prod.getName() + " pos: " + position + " url: " + Util.getImageURL(Util.PRODUCT_URL, params));
                }
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private RecycleViewAdapter<Product, StockMutationHolder> buildAdapter(List<Product> _products)
//    {
//        return new RecycleViewAdapter<Product, StockMutationHolder>(_products)
//        {
//
//            @Override
//            public StockMutationHolder onCreateViewHolder(ViewGroup parent, int viewType)
//            {
//                try {
//                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//                    View view = layoutInflater.inflate(R.layout.invoice_item, parent, false);
//                    return new StockMutationHolder(view);
//                }
//                catch (Exception e) {
//                    Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                    return null;
//                }
//            }
//
//            @Override
//            public void onBindViewHolder(StockMutationHolder holder, final int position)
//            {
//                try {
//                    final Product prod = this.products.get(position);
//                    holder.textNumber.setText(prod.getName());
//
//                    holder.imageProduct.setOnClickListener(
//                            new View.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(View v)
//                                {
//                                    InvoiceLineDialogFragment dialog = InvoiceLineDialogFragment.newInstance(argSI, prod);
//                                    dialog.setTargetFragment(InvoiceFragment_old.this, Util.REQUEST_CODE_ADD);
//                                    dialog.show(getFragmentManager(), "salesinvoiceline");
//                                }
//                            }
//                    );
//
//                    if (prod.getImg() == null)
//                        downloader.queueImage(holder, null);
//                    else {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("sessionString", LoginService.sessionString);
//                        params.put("image", prod.getImg());
//                        downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
//                        Log.d(Util.APP_TAG, "Prod: " + prod.getName() + " pos: " + position + " url: " + Util.getImageURL(Util.PRODUCT_URL, params));
//                    }
//                }
//                catch (Exception e) {
//                    Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

//    private class StockCardAdapter extends RecyclerView.Adapter<StockMutationHolder> {
//        private List<Product> products;
//        private static final int HEADER = -1;
//        private int editedPosition;
//
//        public StockCardAdapter(List<Product> _products) {
//            this.products = _products;
//        }
//
//        public void addItem(Product _prod) {
//            if(this.products == null)
//                this.products = new LinkedList<>();
//
//            this.products.add(_prod);
//            Log.d(Util.APP_TAG, "ADD : " + _prod.getName());
//            notifyItemInserted(this.products.size());
//        }
//
//        public void notifyItemAdded() {
//            notifyItemInserted(products.size());
//        }
//        public void notifyItemUpdated() {
//            listProduct.getAdapter().notifyItemChanged(editedPosition);
//        }
//
//        @Override
//        public StockMutationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            try {
//                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//
//                View view = layoutInflater.inflate(R.layout.invoice_item, parent, false);
//                return new StockMutationHolder(view);
//            }
//            catch(Exception e) {
//                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                e.printStackTrace();
//                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                return null;
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(StockMutationHolder holder, final int position) {
//            try {
//                final Product prod = this.products.get(position);
//                holder.textNumber.setText(prod.getName());
//
//                holder.imageProduct.setOnClickListener(
//                        new View.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(View v)
//                            {
//                                InvoiceLineDialogFragment dialog = InvoiceLineDialogFragment.newInstance(argSI, prod);
//                                dialog.setTargetFragment(InvoiceFragment_old.this, Util.REQUEST_CODE_ADD);
//                                dialog.show(getFragmentManager(), "salesinvoiceline");
//                            }
//                        }
//                );
//
//                if (prod.getImg() == null)
//                    downloader.queueImage(holder, null);
//                else {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("sessionString", LoginService.sessionString);
//                    params.put("image", prod.getImg());
//                    downloader.queueImage(holder, Util.getImageURL(Util.PRODUCT_URL, params));
//                    Log.d(Util.APP_TAG, "Prod: " + prod.getName() + " pos: " + position + " url: " + Util.getImageURL(Util.PRODUCT_URL, params));
//                }
//            }
//            catch(Exception e) {
//                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
//                e.printStackTrace();
//                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//
//            return this.products != null ? this.products.size() : 0;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (position == 0) {
//                return HEADER;
//            }
//
//            return super.getItemViewType(position);
//        }
//    }

//    public abstract class abs<T> {
//
//    }
//
//    public class ext extends abs<Product> {
//
//    }
//
//    private abs<Product> var = new ext();

}
