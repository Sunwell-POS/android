package com.sunwell.pos.mobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.InvoiceDialogFragment;
import com.sunwell.pos.mobile.fragment.CategoryFragment;
import com.sunwell.pos.mobile.fragment.CustomerFragment;
import com.sunwell.pos.mobile.fragment.DashboardFragment;
import com.sunwell.pos.mobile.fragment.InvoiceFragment;
import com.sunwell.pos.mobile.fragment.OnHandStockFragment;
import com.sunwell.pos.mobile.fragment.PaymentMethodFragment;
import com.sunwell.pos.mobile.fragment.ProductFragment;
import com.sunwell.pos.mobile.fragment.SalesFragment;
import com.sunwell.pos.mobile.fragment.SidebarFragment;
import com.sunwell.pos.mobile.fragment.StockCardFragment;
import com.sunwell.pos.mobile.fragment.StockMutationFragment;
import com.sunwell.pos.mobile.fragment.UserFragment;
import com.sunwell.pos.mobile.fragment.UserGroupFragment;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.service.InvoiceService;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements SidebarFragment.SidebarListener
{
    private static final String PROGRESS_FETCH_INVOICE = "progressFetchInvoice";
    private static final String PROGRESS_LOGOUT = "progressLogout";
    private int menuFlag = -1;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Fragment currentFragment ;
    private SidebarFragment sidebarFragment;
    private InvoiceDialogListener invDialogListener = new InvoiceDialogListener() ;
    private InvoiceItemListener invItemListener = new InvoiceItemListener();
    private PaymentListener paymentListener = new PaymentListener();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);

            if(savedInstanceState != null) {
                menuFlag = savedInstanceState.getInt("menuFlag");
                Log.d(Util.APP_TAG, "NOt NULL, menu flag is: " + menuFlag);
            }
            setup();
            Log.d(Util.APP_TAG, "OC CALLED");
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart()
    {
        try {
            super.onStart();
            Log.d(Util.APP_TAG, "OS CALLED");

            // dipanggil di sini karena ada operasi ui anak activity ini yang harus dipanggil setelah onCreate
            setupFragment();
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
            MenuItem userMenuItem = menu.findItem(R.id.menu_item_login);
            MenuItem registerMenuItem = menu.findItem(R.id.menu_item_register);
            MenuItem logoutMenuItem = menu.findItem(R.id.menu_item_logout);
            userMenuItem.setTitle(LoginService.currentUser.getName());
//            registerMenuItem.setVisible(false);

            logoutMenuItem.setOnMenuItemClickListener(
                    new MenuItem.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            try {
//                                Log.d(Util.APP_TAG, "NAVIGATING");
//                                Intent intent = new Intent(HomeActivity.this, WelcomeActivityTest.class);
//                                startActivity(intent);
                                LoginService.logout(
                                        new ResultWatcher<User>()
                                        {
                                            @Override
                                            public void onResult(Object source, User result) throws Exception
                                            {
                                                Util.stopDialog(PROGRESS_LOGOUT);
                                                loginPrefsEditor.clear();
                                                loginPrefsEditor.commit();
                                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onError(Object source, int errCode) throws Exception
                                            {
                                                Util.stopDialog(PROGRESS_LOGOUT);
                                                Log.d(Util.APP_TAG, "Error code: " + errCode);
                                                Toast.makeText(HomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                                Util.showDialog(getSupportFragmentManager(), PROGRESS_LOGOUT);

                                return true;
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(HomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                return true;
                            }
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("menuFlag", menuFlag);
    }

    @Override
    public void onMenuClicked(int _menu)
    {
        try {
            FragmentManager fm = getSupportFragmentManager();
            DialogFragment dialog = null;
            currentFragment = null;
            Log.d(Util.APP_TAG, "Clicked, signal: " + _menu);

            switch (_menu) {
                case SidebarFragment.MENU_DASHBOARD:
                    currentFragment = new DashboardFragment();
                    ((DashboardFragment)currentFragment).setInvoiceItemListener(invItemListener);
                    break;
                case SidebarFragment.MENU_CATEGORIES:
                    currentFragment = new CategoryFragment();
                    break;
                case SidebarFragment.MENU_PRODUCT_LIST:
                    currentFragment = new ProductFragment();
                    break;
                case SidebarFragment.MENU_STAFF_LIST:
                    currentFragment = new UserFragment();
                    break;
                case SidebarFragment.MENU_STAFF_TYPE:
                    currentFragment = new UserGroupFragment();
                    break;
                case SidebarFragment.MENU_CUSTOMERS:
                    currentFragment = new CustomerFragment();
                    break;
                case SidebarFragment.MENU_SALES:
                    currentFragment = new SalesFragment();
                    break;
                case SidebarFragment.MENU_PAYMENTS:
                    currentFragment = new PaymentMethodFragment();
                    break;
                case SidebarFragment.MENU_ONHAND_STOCK:
                    currentFragment = new OnHandStockFragment();
                    break;
                case SidebarFragment.MENU_STOCK_CARD:
                    currentFragment = new StockCardFragment();
                    break;
                case SidebarFragment.MENU_MUTATION:
                    currentFragment = new StockMutationFragment();
                    break;
                case SidebarFragment.MENU_POS:
                    InvoiceDialogFragment d = new InvoiceDialogFragment();
                    d.setDialogListener(invDialogListener);
                    dialog = d;
                    break;
            }

            menuFlag = _menu;

//            currentFragment = null;

            if (currentFragment != null) {
                fm.beginTransaction()
                        .replace(R.id.layout_content, currentFragment)
                        .commit();

                if(InvoiceService.unpaidSalesInvoices != null && currentFragment instanceof DashboardFragment) {
                    boolean b = fm.executePendingTransactions();
                    Log.d(Util.APP_TAG, " B: " + b);
                    Log.d(Util.APP_TAG, " CALLED EXC PEND");
                    ((DashboardFragment) currentFragment).setSalesInvoices(InvoiceService.unpaidSalesInvoices);
                    Log.d(Util.APP_TAG, " SET SI");
                }
            }
            else if (dialog != null) {
                dialog.show(getSupportFragmentManager(), "Invoice");
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setup() throws Exception {
        Log.d(Util.APP_TAG, "SETUP CALLED");
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
//        invDialogListener = new InvoiceDialogListener();
//        invItemListener = new InvoiceItemListener();
//        paymentListener = new PaymentListener();
        setContentView(R.layout.activity_home);
        setupToolbar();
    }



    private void setupFragment() throws Exception
    {
        Log.d(Util.APP_TAG, "SETUP CALLED");
        FragmentManager fm = getSupportFragmentManager();
//        ViewGroup root = (ViewGroup) findViewById(R.id.root);
//        Log.d(Util.APP_TAG, "DP: " + Util.pxToDp(root.getWidth(), this));
//        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        View sidebar = findViewById(R.id.layout_sidebar);

        sidebarFragment = (SidebarFragment) fm.findFragmentById(R.id.layout_sidebar);
        currentFragment = fm.findFragmentById(R.id.layout_content);
        if (sidebarFragment == null) {
            sidebarFragment = new SidebarFragment();
            fm.beginTransaction()
                    .add(R.id.layout_sidebar, sidebarFragment)
                    .commit();
        }
        else
            Log.d(Util.APP_TAG, "SB IS NOT NULL");

        sidebarFragment.addSideBarListener(this);
        sidebarFragment.setInvoiceItemListener(invItemListener);

        if(currentFragment == null)
            onMenuClicked(SidebarFragment.MENU_DASHBOARD);
        else {
            if(currentFragment instanceof DashboardFragment) {
                onMenuClicked(SidebarFragment.MENU_DASHBOARD);
            }
            else if (currentFragment instanceof InvoiceFragment) {
                ((InvoiceFragment) currentFragment).setDialogListener(paymentListener);
            }
        }



        if (InvoiceService.unpaidSalesInvoices == null) {
            Log.d(Util.APP_TAG, "FETCHING INVOICES");
            InvoiceService.fetchUnpaidInvoices(
                    new ResultWatcher<List<SalesInvoice>>()
                    {
                        @Override
                        public void onResult(Object source, List<SalesInvoice> result)
                        {
                            Util.stopDialog(PROGRESS_FETCH_INVOICE);
                            if (result != null) {
                                for (SalesInvoice si : result) {
                                    sidebarFragment.addInvoiceLine(si.getNoInvoice());
                                }
                                if(currentFragment instanceof DashboardFragment)
                                    ((DashboardFragment) currentFragment).setSalesInvoices(result);
                            }
                        }

                        @Override
                        public void onError(Object source, int errCode)
                        {
                            Util.stopDialog(PROGRESS_FETCH_INVOICE);
                            Toast.makeText(HomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            Util.showDialog(getSupportFragmentManager(), PROGRESS_FETCH_INVOICE);
        }
        else {
            for (SalesInvoice si : InvoiceService.unpaidSalesInvoices) {
                sidebarFragment.addInvoiceLine(si.getNoInvoice());
            }

            if(currentFragment instanceof DashboardFragment)
                ((DashboardFragment) currentFragment).setSalesInvoices(InvoiceService.unpaidSalesInvoices);
        }


        sidebar.bringToFront();
    }

    private void setupToolbar()
    {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        ImageButton btnExpand = (ImageButton) mToolbar.findViewById(R.id.button_expand);
        TextView textCompany = (TextView) mToolbar.findViewById(R.id.text_company_name);

        btnExpand.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try {
                            Log.d(Util.APP_TAG, "BUTN EXPAND CALLED");
                            sidebarFragment.toogleDisplay();
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        textCompany.setText(LoginService.currentTenant.getName());
    }

    private class InvoiceDialogListener extends ResultWatcher<SalesInvoice>
    {

        @Override
        public void onResult(Object source, SalesInvoice result) throws Exception
        {
            Toast.makeText(HomeActivity.this, R.string.success_add_invoice, Toast.LENGTH_SHORT).show();
            sidebarFragment.addInvoiceLine(result.getNoInvoice());
            InvoiceFragment invoiceFragment = InvoiceFragment.newInstance(result);
            invoiceFragment.setDialogListener(paymentListener);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_content, invoiceFragment)
                    .commit();

        }

        @Override
        public void onError(Object source, int errCode) throws Exception
        {
            Toast.makeText(HomeActivity.this, R.string.fail_add_invoice, Toast.LENGTH_SHORT).show();
        }
    }

    private class InvoiceItemListener extends ResultWatcher<String>
    {
        @Override
        public void onResult(Object source, String result) throws Exception
        {
            if (InvoiceService.getSalesInvoices() != null) {
                for (SalesInvoice si : InvoiceService.getUnpaidSalesInvoices()) {
                    if (si.getNoInvoice().equals(result)) {

                        InvoiceFragment invoiceFragment = InvoiceFragment.newInstance(si);
                        invoiceFragment.setDialogListener(paymentListener);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.layout_content, invoiceFragment)
                                .commit();
                    }
                }
            }
        }

        @Override
        public void onError(Object source, int errCode) throws Exception
        {
            Toast.makeText(HomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class PaymentListener extends ResultWatcher<SalesPayment>
    {
        @Override
        public void onResult(Object source, SalesPayment sp) throws Exception
        {
            sidebarFragment.removeInvoiceLine(sp.getParent().getNoInvoice());
            SalesFragment salesFrament = new SalesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_content, salesFrament)
                    .commit();
        }

        @Override
        public void onError(Object source, int errCode)
        {
            Toast.makeText(HomeActivity.this, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }
}
