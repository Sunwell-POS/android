package com.sunwell.pos.mobile.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class SidebarFragment_old extends Fragment {

    public static final int MENU_DASHBOARD = 0;
    public static final int MENU_POS = 1;
    public static final int MENU_SALES = 2;
    public static final int MENU_REPORTS = 3;
    public static final int MENU_ANALYTICS = 4;
    public static final int MENU_PRODUCT_LIST = 5;
    public static final int MENU_CATEGORIES = 6;
    public static final int MENU_ONHAND_STOCK = 7;
    public static final int MENU_MUTATION = 8;
    public static final int MENU_STOCK_CARD = 9;
    public static final int MENU_CUSTOMERS = 10;
    public static final int MENU_PAYMENTS = 11;
    public static final int MENU_STAFF_LIST = 12;
    public static final int MENU_STAFF_TYPE = 13;
    private boolean open = false;
    private ResultListener<String> invoiceItemListener;
    private SidebarViewListener sbl = new SidebarViewListener();
    private List<SidebarListener> sidebarListeners = new LinkedList<>();
    private LinearLayout panelPos;
    private ImageButton btnDashboard ;
    private ImageButton btnPOS; ;
    private ImageButton btnSales ;
    private ImageButton btnReports ;
    private ImageButton btnAnalytics ;
    private ImageButton btnProducts ;
    private ImageButton btnInventory ;
    private ImageButton btnCustomers ;
    private ImageButton btnPayments ;
    private ImageButton btnStaffs ;
    private ImageButton btnShowPOSSubMenu ;
    private ImageButton btnShowProdSubMenu ;
    private ImageButton btnShowInvSubMenu ;
    private ImageButton btnShowStaffSubMenu ;
    private TextView textDashboard ;
    private TextView textPOS ;
    private TextView textNewPOS ;
    private TextView textSales ;
    private TextView textReports ;
    private TextView textAnalytics ;
    private TextView textProducts ;
    private TextView textProductList ;
    private TextView textCategories ;
    private TextView textInventory ;
    private TextView textOnHand ;
    private TextView textMutation ;
    private TextView textStockCard ;
    private TextView textCustomers ;
    private TextView textPayments ;
    private TextView textStaffs ;
    private TextView textStaffList ;
    private TextView textStaffType ;
    private Map<String, View> invoiceRows = new HashMap<>();
    private List<View> listSidebar = new LinkedList<>();
    private List<View> invoiceItems = new LinkedList<>();
    private Map<View, TextView> viewMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
            View v = inflater.inflate(R.layout.sidebar, container, false);

            panelPos = (LinearLayout) v.findViewById(R.id.panel_pos);
            btnDashboard = (ImageButton) v.findViewById(R.id.button_dashboard);
            btnPOS = (ImageButton) v.findViewById(R.id.button_pos);
            btnSales = (ImageButton) v.findViewById(R.id.button_sales);
            btnReports = (ImageButton) v.findViewById(R.id.button_reports);
            btnAnalytics = (ImageButton) v.findViewById(R.id.button_analytics);
            btnProducts = (ImageButton) v.findViewById(R.id.button_products);
            btnInventory = (ImageButton) v.findViewById(R.id.button_inventory);
            btnCustomers = (ImageButton) v.findViewById(R.id.button_customers);
            btnPayments = (ImageButton) v.findViewById(R.id.button_payments);
            btnStaffs = (ImageButton) v.findViewById(R.id.button_staffs);
            btnShowPOSSubMenu = (ImageButton) v.findViewById(R.id.button_show_pos_sub_menu);
            btnShowProdSubMenu = (ImageButton) v.findViewById(R.id.button_show_products_sub_menu);
            btnShowInvSubMenu = (ImageButton) v.findViewById(R.id.button_show_inventory_sub_menu);
            btnShowStaffSubMenu = (ImageButton) v.findViewById(R.id.button_show_staffs_sub_menu);

            textDashboard = (TextView) v.findViewById(R.id.text_dashboards);
            textPOS = (TextView) v.findViewById(R.id.text_pos);
            textNewPOS = (TextView) v.findViewById(R.id.text_new_pos);
            textSales = (TextView) v.findViewById(R.id.text_sales);
            textReports = (TextView) v.findViewById(R.id.text_reports);
            textAnalytics = (TextView) v.findViewById(R.id.text_analytics);
            textProducts = (TextView) v.findViewById(R.id.text_products);
            textProductList = (TextView) v.findViewById(R.id.text_product_list);
            textCategories = (TextView) v.findViewById(R.id.text_category);
            textInventory = (TextView) v.findViewById(R.id.text_inventory);
            textOnHand = (TextView) v.findViewById(R.id.text_onhand);
            textMutation = (TextView) v.findViewById(R.id.text_mutation);
            textStockCard = (TextView) v.findViewById(R.id.text_stock_card);
            textCustomers = (TextView) v.findViewById(R.id.text_customers);
            textPayments = (TextView) v.findViewById(R.id.text_payments);
            textStaffs = (TextView) v.findViewById(R.id.text_staffs);
            textStaffList = (TextView) v.findViewById(R.id.text_staff_list);
            textStaffType = (TextView) v.findViewById(R.id.text_staff_type);

            viewMap.put(btnDashboard, textDashboard);
            viewMap.put(btnPOS, textPOS);
            viewMap.put(btnSales, textSales);
            viewMap.put(btnReports, textReports);
            viewMap.put(btnAnalytics, textAnalytics);
            viewMap.put(btnProducts, textProducts);
            viewMap.put(btnInventory, textInventory);
            viewMap.put(btnCustomers, textCustomers);
            viewMap.put(btnPayments, textPayments);
            viewMap.put(btnStaffs, textStaffs);

            listSidebar.add(textDashboard);
            listSidebar.add(textNewPOS);
            listSidebar.add(textSales);
            listSidebar.add(textReports);
            listSidebar.add(textAnalytics);
            listSidebar.add(textProductList);
            listSidebar.add(textCategories);
            listSidebar.add(textOnHand);
            listSidebar.add(textMutation);
            listSidebar.add(textStockCard);
            listSidebar.add(textCustomers);
            listSidebar.add(textPayments);
            listSidebar.add(textStaffList);
            listSidebar.add(textStaffType);
            listSidebar.add(btnDashboard);
            listSidebar.add(btnPOS);
            listSidebar.add(btnSales);
            listSidebar.add(btnProducts);
            listSidebar.add(btnInventory);
            listSidebar.add(btnReports);
            listSidebar.add(btnAnalytics);
            listSidebar.add(btnCustomers);
            listSidebar.add(btnPayments);
            listSidebar.add(btnStaffs);

            for (View view : listSidebar) {
                view.setOnClickListener(sbl);
            }

            btnShowPOSSubMenu.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (textNewPOS.getVisibility() != View.VISIBLE) {
                                    textNewPOS.setVisibility(View.VISIBLE);
                                    for (View inv : invoiceItems) {
                                        inv.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    textNewPOS.setVisibility(View.GONE);
                                    for (View inv : invoiceItems) {
                                        inv.setVisibility(View.GONE);
                                    }
                                }
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );

            btnShowProdSubMenu.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (textProductList.getVisibility() != View.VISIBLE) {
                                    textProductList.setVisibility(View.VISIBLE);
                                    textCategories.setVisibility(View.VISIBLE);
                                } else {
                                    textProductList.setVisibility(View.GONE);
                                    textCategories.setVisibility(View.GONE);
                                }
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnShowInvSubMenu.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (textOnHand.getVisibility() != View.VISIBLE) {
                                    textOnHand.setVisibility(View.VISIBLE);
                                    textMutation.setVisibility(View.VISIBLE);
                                    textStockCard.setVisibility(View.VISIBLE);
                                } else {
                                    textOnHand.setVisibility(View.GONE);
                                    textMutation.setVisibility(View.GONE);
                                    textStockCard.setVisibility(View.GONE);
                                }
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            btnShowStaffSubMenu.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try {
                                if (textStaffList.getVisibility() != View.VISIBLE) {
                                    textStaffList.setVisibility(View.VISIBLE);
                                    textStaffType.setVisibility(View.VISIBLE);
                                } else {
                                    textStaffList.setVisibility(View.GONE);
                                    textStaffType.setVisibility(View.GONE);
                                }
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            closeSidebar();

            return v;
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void addSideBarListener(SidebarListener _listener) {
        sidebarListeners.add(_listener);
    }

    public ResultListener<String> getInvoiceItemListener() {
        return invoiceItemListener;
    }

    public void setInvoiceItemListener(ResultListener<String> invoiceItemListener) {
        this.invoiceItemListener = invoiceItemListener;
    }

    public void toogleOpen() {
        if(open) {
            closeSidebar();
            open = false;
        }
        else {
            openSidebar();
            open = true;
        }
    }

    public void addInvoiceLine(final String _inv) {
        View row = getActivity().getLayoutInflater().inflate(R.layout.invoice_row, panelPos, false);
        final TextView textInvoiceRow = (TextView)row.findViewById(R.id.text_invoice_row);
        textInvoiceRow.setText(_inv);
        textInvoiceRow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            closeSidebar();
                            invoiceItemListener.onResult(SidebarFragment_old.this, _inv);
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        panelPos.addView(row, 1);
        row.setVisibility(View.GONE);
        invoiceItems.add(row);
        invoiceRows.put(_inv, row);
    }

    public void removeInvoiceLine(final String _inv) {
        panelPos.removeView(invoiceRows.get(_inv));
    }

    public void openSidebar() {
        textDashboard.setVisibility(View.VISIBLE);
        textPOS.setVisibility(View.VISIBLE);
        textSales.setVisibility(View.VISIBLE);
        textReports.setVisibility(View.VISIBLE);
        textAnalytics.setVisibility(View.VISIBLE);
        textProducts.setVisibility(View.VISIBLE);
        textInventory.setVisibility(View.VISIBLE);
        textCustomers.setVisibility(View.VISIBLE);
        textPayments.setVisibility(View.VISIBLE);
        textStaffs.setVisibility(View.VISIBLE);
        btnShowPOSSubMenu.setVisibility(View.VISIBLE);
        btnShowProdSubMenu.setVisibility(View.VISIBLE);
        btnShowInvSubMenu.setVisibility(View.VISIBLE);
        btnShowStaffSubMenu.setVisibility(View.VISIBLE);
    }

    public void closeSidebar() {
        textDashboard.setVisibility(View.GONE);
        textPOS.setVisibility(View.GONE);
        textNewPOS.setVisibility(View.GONE);
        textSales.setVisibility(View.GONE);
        textReports.setVisibility(View.GONE);
        textAnalytics.setVisibility(View.GONE);
        textProducts.setVisibility(View.GONE);
        textProductList.setVisibility(View.GONE);
        textCategories.setVisibility(View.GONE);
        textInventory.setVisibility(View.GONE);
        textOnHand.setVisibility(View.GONE);
        textMutation.setVisibility(View.GONE);
        textStockCard.setVisibility(View.GONE);
        textCustomers.setVisibility(View.GONE);
        textPayments.setVisibility(View.GONE);
        textStaffs.setVisibility(View.GONE);
        textStaffList.setVisibility(View.GONE);
        textStaffType.setVisibility(View.GONE);
        btnShowPOSSubMenu.setVisibility(View.GONE);
        btnShowProdSubMenu.setVisibility(View.GONE);
        btnShowInvSubMenu.setVisibility(View.GONE);
        btnShowStaffSubMenu.setVisibility(View.GONE);
        for(View v : invoiceItems) {
            v.setVisibility(View.GONE);
        }
    }

    private void highlightSelection(View v) {
        if(v instanceof TextView)
            ((TextView)v).setTextColor(Color.parseColor("#f6f7f7"));
        else {
            TextView textView = viewMap.get(v);
            textView.setTextColor(Color.parseColor("#f6f7f7"));
        }
        ((View)v.getParent()).setBackgroundColor(Color.parseColor("#0f7858"));
        for (View menu: listSidebar) {
            if(menu == v)
                continue;

            if(menu instanceof TextView)
                ((TextView)menu).setTextColor(Color.parseColor("#0f7858"));
            else {
                TextView textView = viewMap.get(menu);
                textView.setTextColor(Color.parseColor("#0f7858"));
            }

            ((View)menu.getParent()).setBackgroundColor(Color.parseColor("#01ffffff"));
        }
        Log.d(Util.APP_TAG, " HIGHLIGHT ");
        if(open) {
            closeSidebar();
            open = false;
            Log.d(Util.APP_TAG, " STILL OPEN ");
        }
        else {
            Log.d(Util.APP_TAG, " ID: " + v.getId());
            switch(v.getId()) {
                case R.id.button_pos:
                case R.id.button_products:
                case R.id.button_inventory:
                case R.id.button_staffs:
                    openSidebar();
                    open = true;
                    Log.d(Util.APP_TAG, "OPEN");
            }
        }
    }

    public static interface SidebarListener {
        public void onMenuClicked(int _menu) throws Exception ;
    }

    private class SidebarViewListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                int signal = -1;
                switch (v.getId()) {
                    case R.id.button_dashboard:
                    case R.id.text_dashboards:
                        signal = MENU_DASHBOARD;
                        break;
                    case R.id.button_pos:
                        signal = -1;
                        break;
                    case R.id.text_new_pos:
                        signal = MENU_POS;
                        break;
                    case R.id.button_sales:
                    case R.id.text_sales:
                        signal = MENU_SALES;
                        break;
                    case R.id.button_reports:
                    case R.id.text_reports:
                        signal = MENU_REPORTS;
                        break;
                    case R.id.button_analytics:
                    case R.id.text_analytics:
                        signal = MENU_ANALYTICS;
                        break;
                    case R.id.button_products:
                        signal = -1;
                        break;
                    case R.id.text_product_list:
                        signal = MENU_PRODUCT_LIST;
                        break;
                    case R.id.text_category:
                        signal = MENU_CATEGORIES;
                        break;
                    case R.id.button_inventory:
                        signal = -1;
                        break;
                    case R.id.text_onhand:
                        signal = MENU_ONHAND_STOCK;
                        break;
                    case R.id.text_mutation:
                        signal = MENU_MUTATION;
                        break;
                    case R.id.text_stock_card:
                        signal = MENU_STOCK_CARD;
                        break;
                    case R.id.button_customers:
                    case R.id.text_customers:
                        signal = MENU_CUSTOMERS;
                        break;
                    case R.id.button_payments:
                    case R.id.text_payments:
                        signal = MENU_PAYMENTS;
                        break;
                    case R.id.button_staffs:
                        signal = -1;
                        break;
                    case R.id.text_staff_list:
                        signal = MENU_STAFF_LIST;
                        break;
                    case R.id.text_staff_type:
                        signal = MENU_STAFF_TYPE;
                        break;
                }
                highlightSelection(v);

                for (SidebarListener listener : sidebarListeners) {
                    listener.onMenuClicked(signal);
                }
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
