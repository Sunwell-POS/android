package com.sunwell.pos.mobile.service;

import android.util.Log;

import com.sunwell.pos.mobile.model.PaymentMethod;
import com.sunwell.pos.mobile.model.PaymentMethodObj;
import com.sunwell.pos.mobile.model.SalesInvoice;
import com.sunwell.pos.mobile.model.SalesInvoiceLine;
import com.sunwell.pos.mobile.model.SalesPayment;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 11/2/17.
 */

public class InvoiceService {

    public static List<SalesInvoice> salesInvoices;
    public static List<SalesInvoice> unpaidSalesInvoices;
    public static List<SalesInvoice> paidSalesInvoices;
    public static List<SalesInvoice> addedSalesInvoices = new LinkedList<>();
    public static List<SalesInvoice> addedUnpaidSalesInvoices = new LinkedList<>();
    public static List<SalesInvoice> addedPaidSalesInvoices = new LinkedList<>();
    public static List<SalesPayment> salesPayments;
    public static List<SalesPayment> addedSalesPayments = new LinkedList<>();
    public static List<PaymentMethodObj> paymentMethods;
    public static List<PaymentMethodObj> addedPaymentMethods = new LinkedList<>();
    public static List<PaymentMethod> originPaymentMethods;

    static {
        LoginService.addLogoutListener(
                new ResultWatcher<User>()
                {
                    @Override
                    public void onResult(Object source, User result) throws Exception
                    {
                        clear();
                    }
                }
        );
    }

    public static void clear() {
        salesInvoices = null;
        unpaidSalesInvoices = null;
        paidSalesInvoices = null;
        addedSalesInvoices = new LinkedList<>();;
        addedUnpaidSalesInvoices = new LinkedList<>();
        addedPaidSalesInvoices = new LinkedList<>();
        salesPayments = null;
        addedSalesPayments = new LinkedList<>();
        paymentMethods = null;
        addedPaymentMethods = new LinkedList<>();
        originPaymentMethods = null;
    }


    public static void fetchInvoices(final ResultWatcher<List<SalesInvoice>> _listener) throws Exception {
        fetchInvoices(_listener, null);
    }

    public static void fetchInvoices(final ResultWatcher<List<SalesInvoice>> _listener, Map<String, String> _parameter) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);

        if(_parameter != null)
            for(String key : _parameter.keySet()) {
                json.put(key, _parameter.get(key));
            }

        GeneralTask<List<SalesInvoice>> gt = new GeneralTask<>(_listener, Util.SALES_INVOICE_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<SalesInvoice>>() {

                    @Override
                    public List<SalesInvoice> parse(JSONObject response) throws Exception {
                        salesInvoices = null ;
                        if (!response.has("listSalesInvoice"))
                            return null;

                        JSONArray jArrInvoices = response.getJSONArray("listSalesInvoice");
                        if (jArrInvoices == null || jArrInvoices.length() <= 0)
                            return null;

                        SalesInvoice[] arrInvoices = Util.parseJSONData(jArrInvoices.toString(), SalesInvoice[].class);
                        if (arrInvoices != null && arrInvoices.length > 0) {
                            salesInvoices = new LinkedList<>(Arrays.asList(arrInvoices));
                            if(unpaidSalesInvoices != null) {
                                for(SalesInvoice si : salesInvoices) {
                                    if(si.getPaid())
                                        continue;
                                    int index = unpaidSalesInvoices.indexOf(si);
                                    if(index > -1)
                                        unpaidSalesInvoices.set(index, si);
                                    else
                                        unpaidSalesInvoices.add(si);
                                }
                            }

                            if(paidSalesInvoices != null) {
                                for(SalesInvoice si : salesInvoices) {
                                    if(!si.getPaid())
                                        continue;
                                    int index = paidSalesInvoices.indexOf(si);
                                    if(index > -1)
                                        paidSalesInvoices.set(index, si);
                                    else
                                        paidSalesInvoices.add(si);
                                }
                            }
                        }
                        Log.d(Util.APP_TAG, "SI SIZE: " + salesInvoices.size());
                        return salesInvoices;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchUnpaidInvoices(final ResultWatcher<List<SalesInvoice>> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("voided", "false");
        json.put("paid", "false");
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<SalesInvoice>> gt = new GeneralTask<>(_listener, Util.SALES_INVOICE_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<SalesInvoice>>() {

                    @Override
                    public List<SalesInvoice> parse(JSONObject response) throws Exception {
                        unpaidSalesInvoices = null ;
                        if (!response.has("listSalesInvoice"))
                            return null;

                        JSONArray jArrInvoices = response.getJSONArray("listSalesInvoice");
                        if (jArrInvoices == null || jArrInvoices.length() <= 0)
                            return null;

                        SalesInvoice[] arrInvoices = Util.parseJSONData(jArrInvoices.toString(), SalesInvoice[].class);
                        if (arrInvoices != null && arrInvoices.length > 0) {
                            unpaidSalesInvoices = new LinkedList<>(Arrays.asList(arrInvoices));
                            if(salesInvoices != null) {
                                for(SalesInvoice si : unpaidSalesInvoices) {
                                    int index = salesInvoices.indexOf(si);
                                    if(index > -1)
                                        salesInvoices.set(index, si);
                                    else
                                        salesInvoices.add(si);
                                }
                            }
                        }
                        Log.d(Util.APP_TAG, "SI SIZE: " + unpaidSalesInvoices.size());
                        return unpaidSalesInvoices;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchPaidInvoices(final ResultWatcher<List<SalesInvoice>> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        json.put("paid", "true");
        GeneralTask<List<SalesInvoice>> gt = new GeneralTask<>(_listener, Util.SALES_INVOICE_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<SalesInvoice>>() {

                    @Override
                    public List<SalesInvoice> parse(JSONObject response) throws Exception {
                        paidSalesInvoices = null ;
                        if (!response.has("listSalesInvoice"))
                            return null;

                        JSONArray jArrInvoices = response.getJSONArray("listSalesInvoice");
                        if (jArrInvoices == null || jArrInvoices.length() <= 0)
                            return null;

                        SalesInvoice[] arrInvoices = Util.parseJSONData(jArrInvoices.toString(), SalesInvoice[].class);
                        if (arrInvoices != null && arrInvoices.length > 0) {
                            paidSalesInvoices = new LinkedList<>(Arrays.asList(arrInvoices));
                            if(salesInvoices != null) {
                                for(SalesInvoice si : paidSalesInvoices) {
                                    int index = salesInvoices.indexOf(si);
                                    if(index > -1)
                                        salesInvoices.set(index, si);
                                    else
                                        salesInvoices.add(si);
                                }
                            }
                        }
                        Log.d(Util.APP_TAG, "SI SIZE: " + paidSalesInvoices.size());
                        return paidSalesInvoices;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchSalesPayments(final ResultWatcher<List<SalesPayment>> _listener) throws Exception {
        fetchSalesPayments(_listener, null);
    }

    public static void fetchSalesPayments(final ResultWatcher<List<SalesPayment>> _listener, Map<String, String> _parameter) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);

        if(_parameter != null)
            for(String key : _parameter.keySet()) {
                json.put(key, _parameter.get(key));
            }

        GeneralTask<List<SalesPayment>> gt = new GeneralTask<>(_listener, Util.SALES_PAYMENT_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<SalesPayment>>() {

                    @Override
                    public List<SalesPayment> parse(JSONObject response) throws Exception {
                        salesPayments = null ;
                        if (!response.has("listSalesPayment"))
                            return null;

                        JSONArray jArrPayments = response.getJSONArray("listSalesPayment");
                        if (jArrPayments == null || jArrPayments.length() <= 0)
                            return null;

                        SalesPayment[] arrPayments = Util.parseJSONData(jArrPayments.toString(), SalesPayment[].class);
                        if (arrPayments != null && arrPayments.length > 0) {
                            salesPayments = new LinkedList<>(Arrays.asList(arrPayments));

                        }
                        Log.d(Util.APP_TAG, "SP SIZE: " + salesPayments.size());
                        return salesPayments;
                    }
                }
        );
        gt.get(json);
    }


    public static void fetchPaymentMethods(final ResultWatcher<List<PaymentMethodObj>> _listener) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<PaymentMethodObj>> gt = new GeneralTask<>(_listener, Util.PAYMENT_METHOD_OBJ_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<PaymentMethodObj>>() {

                    @Override
                    public List<PaymentMethodObj> parse(JSONObject response) throws Exception {
                        paymentMethods = null ;
                        if (!response.has("listPaymentMethod"))
                            return null;

                        JSONArray jMethods = response.getJSONArray("listPaymentMethod");
                        if (jMethods == null || jMethods.length() <= 0)
                            return null;

                        PaymentMethodObj[] arrMethods = Util.parseJSONData(jMethods.toString(), PaymentMethodObj[].class);
                        if (arrMethods != null && arrMethods.length > 0) {
                            paymentMethods = new LinkedList<>(Arrays.asList(arrMethods));
                        }
                        Log.d(Util.APP_TAG, " SIZE: " + paymentMethods.size());
                        return paymentMethods;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchOriginPaymentMethods(final ResultWatcher<List<PaymentMethod>> _listener) throws Exception {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<PaymentMethod>> gt = new GeneralTask<>(_listener, Util.PAYMENT_METHOD_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<PaymentMethod>>() {

                    @Override
                    public List<PaymentMethod> parse(JSONObject response) throws Exception {
                        originPaymentMethods = null ;
                        if (!response.has("listPaymentMethod"))
                            return null;

                        JSONArray jMethods = response.getJSONArray("listPaymentMethod");
                        if (jMethods == null || jMethods.length() <= 0)
                            return null;

                        PaymentMethod[] arrMethods = Util.parseJSONData(jMethods.toString(), PaymentMethod[].class);
                        if (arrMethods != null && arrMethods.length > 0) {
                            originPaymentMethods = new LinkedList<>(Arrays.asList(arrMethods));
                        }
                        Log.d(Util.APP_TAG, " SIZE: " + paymentMethods.size());
                        return originPaymentMethods;
                    }
                }
        );
        gt.get(json);
    }

    public static void addSalesInvoice(final ResultListener<SalesInvoice> _listener, SalesInvoice _inv) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_inv, SalesInvoice.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<SalesInvoice> listener = new ResultWatcher<SalesInvoice>() {
            @Override
            public void onResult(Object source, SalesInvoice result) throws Exception {

                if(salesInvoices != null)
                    salesInvoices.add(result);
                else
                    addedSalesInvoices.add(result);

                if(unpaidSalesInvoices != null)
                    unpaidSalesInvoices.add(result);

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SALES_INVOICE_URL, SalesInvoice.class).post(jsonObj);
    }

    public static void splitBill(final ResultListener<SalesInvoice> _listener, final SalesInvoice _inv) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_inv, SalesInvoice.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<SalesInvoice> listener = new ResultWatcher<SalesInvoice>() {
            @Override
            public void onResult(Object source, SalesInvoice result) throws Exception {

                List<SalesInvoice> listSI = salesInvoices != null ? salesInvoices : unpaidSalesInvoices != null ? unpaidSalesInvoices : addedPaidSalesInvoices;
                List<SalesInvoiceLine> deletedLines = new LinkedList<>();
                for(SalesInvoice si : listSI) {
                    if(si.getSystemId().equals(_inv.getSystemId())) {
                        for(SalesInvoiceLine sil : si.getSalesInvoiceLines()) {
                            for(SalesInvoiceLine newLine : _inv.getSalesInvoiceLines()) {
                                if(sil.getSystemId().equals(newLine.getSystemId())) {
                                    if(sil.getQty() > newLine.getQty())
                                        sil.setQty(sil.getQty() - newLine.getQty());
                                    else
                                        deletedLines.add(sil);
                                    Log.d(Util.APP_TAG, "SIL: " + sil.getSystemId() + " QTY: " + sil.getQty());
                                    break;
                                }
                            }
                        }

                        for(SalesInvoiceLine sil : deletedLines) {
                            si.getSalesInvoiceLines().remove(sil);
                        }

                        break;
                    }
                }

                if(salesInvoices != null) {
                    salesInvoices.add(result);
                }
                else
                    addedSalesInvoices.add(result);

                if(unpaidSalesInvoices != null)
                    unpaidSalesInvoices.add(result);

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SPLIT_BILL_URL, SalesInvoice.class).post(jsonObj);
    }

    public static void addSalesPayment(final ResultListener<SalesPayment> _listener, SalesPayment _sp) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_sp, SalesPayment.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<SalesPayment> listener = new ResultWatcher<SalesPayment>() {
            @Override
            public void onResult(Object source, SalesPayment result) throws Exception {
                if (salesPayments == null)
                    addedSalesPayments.add(result);
                else
                    salesPayments.add(result);

                List<SalesInvoice> siList = null;
                SalesInvoice paidSI = null;
                if (unpaidSalesInvoices != null)
                    siList = unpaidSalesInvoices;
                else if (salesInvoices != null)
                    siList = salesInvoices;
                else if (addedSalesInvoices != null)
                    siList = addedSalesInvoices;
                else if (addedUnpaidSalesInvoices.size() > 0) {
                    siList = addedUnpaidSalesInvoices;
                    throw new Exception("NO INVOICE DATA FOUND");
                }

                paidSI = siList.get(siList.indexOf(result.getParent()));

                if(paidSI != null)
                    paidSI.setPaid(true);
                else
                    throw new Exception("CAN'T FIND INVOICE DATA");

                if (unpaidSalesInvoices != null)
                    unpaidSalesInvoices.remove(paidSI);
                else if(addedUnpaidSalesInvoices != null)
                    addedUnpaidSalesInvoices.remove(paidSI);

                if (paidSalesInvoices != null)
                    paidSalesInvoices.add(paidSI);
                else if(salesInvoices == null && addedSalesInvoices == null){
                    addedPaidSalesInvoices.add(paidSI);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                Log.d(Util.APP_TAG, "On ERROR ERRCODE: " + _errCode);
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SALES_PAYMENT_URL, SalesPayment.class).post(jsonObj);
    }

    public static void addPaymentMethod(final ResultListener<PaymentMethodObj> _listener, PaymentMethodObj _method) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_method, PaymentMethodObj.class);
        Log.d(Util.APP_TAG, "JSON PMO: " + jsonObj.toString());
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<PaymentMethodObj> listener = new ResultWatcher<PaymentMethodObj>() {
            @Override
            public void onResult(Object source, PaymentMethodObj result) throws Exception {

                if(paymentMethods != null)
                    paymentMethods.add(result);
                else
                    addedPaymentMethods.add(result);

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.PAYMENT_METHOD_OBJ_URL, PaymentMethodObj.class).post(jsonObj);
    }

    public static void addSalesInvoiceItem(final ResultListener<SalesInvoiceLine> _listener, SalesInvoiceLine _line, final String _idInv) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_line, SalesInvoiceLine.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        jsonObj.put("salesInvoiceId", _idInv);
        ResultWatcher<SalesInvoiceLine> listener = new ResultWatcher<SalesInvoiceLine>(){
            @Override
            public void onResult(Object source, SalesInvoiceLine result) throws Exception {
                    List<SalesInvoice> invoices = null;
                    if (unpaidSalesInvoices != null)
                        invoices = unpaidSalesInvoices;
                    else if (salesInvoices != null)
                        invoices = salesInvoices;
                    else if (addedSalesInvoices != null)
                        invoices = addedSalesInvoices;
                    else
                        throw new Exception("NO INVOICES DATA FOUND");

                    SalesInvoice salesInvoice = invoices.get(invoices.indexOf(new SalesInvoice(_idInv)));
                    List<SalesInvoiceLine> salesInvoiceLines = salesInvoice.getSalesInvoiceLines();
                    if (salesInvoiceLines != null) {
                        salesInvoiceLines.add(result);
                    }
                    else {
                        salesInvoiceLines = new LinkedList<>();
                        salesInvoiceLines.add(result);
                        salesInvoice.setSalesInvoiceLines(salesInvoiceLines);
                    }

                    if (_listener != null)
                        _listener.onResult(null, result);

            }

            @Override
            public void onError(Object source, int _errCode) throws Exception{
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SALES_INVOICE_LINE_URL, SalesInvoiceLine.class).post(jsonObj);
    }

    public static void editSalesInvoice(final ResultListener<SalesInvoice> _listener, final SalesInvoice _inv) throws Exception {
        JSONObject jsonObject = Util.toJSONObject(_inv, SalesInvoice.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        ResultWatcher<SalesInvoice> listener = new ResultWatcher<SalesInvoice>() {
            @Override
            public void onResult(Object source, SalesInvoice result) throws Exception {
                List<SalesInvoice> invoices = null;
                if(salesInvoices != null) {
                    invoices = salesInvoices;
                }
                else if(addedSalesInvoices != null) {
                    invoices = addedSalesInvoices;
                }

                if (invoices != null) {
                    for (int i = 0 ; i < invoices.size() ; i++) {
                        SalesInvoice s = invoices.get(i);
                        if (s.getSystemId().equals(_inv.getSystemId())) {
                            invoices.set(i, result);
                            break;
                        }
                    }
                }

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SALES_INVOICE_URL, SalesInvoice.class).put(jsonObject);
    }

    public static void editSalesInvoiceItem(final ResultListener<SalesInvoiceLine> _listener, SalesInvoiceLine _line, final String _idInv) throws Exception {
        JSONObject jsonObj = Util.toJSONObject(_line, SalesInvoiceLine.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<SalesInvoiceLine> listener = new ResultWatcher<SalesInvoiceLine>() {
            @Override
            public void onResult(Object source, SalesInvoiceLine result) throws Exception {
                    List<SalesInvoice> invoices = null;
                    if (unpaidSalesInvoices != null)
                        invoices = unpaidSalesInvoices;
                    else if(salesInvoices != null)
                        invoices = salesInvoices;
                    else if (addedSalesInvoices != null)
                        invoices = addedSalesInvoices;
                    else
                        throw new Exception("NO INVOICES DATA FOUND");

                    SalesInvoice salesInvoice = invoices.get(invoices.indexOf(new SalesInvoice(_idInv)));
                    List<SalesInvoiceLine> salesInvoiceLines = salesInvoice.getSalesInvoiceLines();
                    if (salesInvoiceLines != null) {
                        for(int i = 0 ; i < salesInvoiceLines.size() ; i++) {
                            if(salesInvoiceLines.get(i).equals(result)) {
                                salesInvoiceLines.set(i, result);
                                break;
                            }
                        }
                    }
                    else
                        throw new Exception("CAN'T FIND INVOICE DATA");

                    if (_listener != null)
                        _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SALES_INVOICE_URL, SalesInvoiceLine.class).post(jsonObj);
    }

    public static void editPaymentMethod(final ResultListener<PaymentMethodObj> _listener, final PaymentMethodObj _method) throws Exception {
        JSONObject jsonObject = Util.toJSONObject(_method, PaymentMethodObj.class);
        Log.d(Util.APP_TAG, "JSON PMO: " + jsonObject.toString());
        jsonObject.put("sessionString", LoginService.sessionString);
        ResultWatcher<PaymentMethodObj> listener = new ResultWatcher<PaymentMethodObj>() {
            @Override
            public void onResult(Object source, PaymentMethodObj result) throws Exception {
                List<PaymentMethodObj> methods = null;
                if(paymentMethods != null) {
                    methods = paymentMethods;
                }
                else if(addedSalesInvoices != null) {
                    methods = addedPaymentMethods;
                }

                if (methods != null) {
                    for (int i = 0 ; i < methods.size() ; i++) {
                        PaymentMethodObj s = methods.get(i);
                        if (s.getSystemId().equals(_method.getSystemId())) {
                            methods.set(i, result);
                            break;
                        }
                    }
                }

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.PAYMENT_METHOD_OBJ_URL, PaymentMethodObj.class).put(jsonObject);
    }

    public static void deleteSalesInvoice(final ResultWatcher<Boolean> _listener, final String _id) throws Exception {
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>() {
            @Override
            public void onResult(Object source, Boolean result)  throws Exception {
                List<SalesInvoice> invoices = null;
                if(salesInvoices != null) {
                    invoices = salesInvoices;
                }
                else if(addedSalesInvoices != null) {
                    invoices = addedSalesInvoices;
                }

                if(invoices != null) {
                    SalesInvoice s = null;
                    for (int i = 0 ; i < invoices.size() ; i++) {
                        if (invoices.get(i).getSystemId().equals(_id)) {
                            s = invoices.get(i);
                            break;
                        }
                    }
                    invoices.remove(s);
                }

                if(unpaidSalesInvoices != null) {
                    SalesInvoice s = null;
                    for (int i = 0 ; i < unpaidSalesInvoices.size() ; i++) {
                        if (unpaidSalesInvoices.get(i).getSystemId().equals(_id)) {
                            s = unpaidSalesInvoices.get(i);
                            break;
                        }
                    }
                    unpaidSalesInvoices.remove(s);
                }

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.SALES_INVOICE_URL, null).delete(param);
    }

    public static void deletePaymentMethod(final ResultWatcher<Boolean> _listener, final String _id) throws Exception {
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>() {
            @Override
            public void onResult(Object source, Boolean result) throws Exception {
                List<PaymentMethodObj> methods = null;
                if(paymentMethods != null) {
                    methods = paymentMethods;
                }
                else if(addedPaymentMethods != null) {
                    methods = addedPaymentMethods;
                }

                if(methods != null) {
                    PaymentMethodObj method = null;
                    for (int i = 0 ; i < methods.size() ; i++) {
                        if (methods.get(i).getSystemId().equals(_id)) {
                            method = methods.get(i);
                            break;
                        }
                    }
                    methods.remove(method);
                }

                if(_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception {
                if(_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.PAYMENT_METHOD_OBJ_URL, null).delete(param);
    }

    public static List<SalesInvoice> getSalesInvoices() {
        if(salesInvoices != null)
            return salesInvoices;
        else if(addedSalesInvoices != null)
            return addedSalesInvoices;
        else
            return null;
    }

    public static List<SalesInvoice> getUnpaidSalesInvoices() {
        if(unpaidSalesInvoices != null)
            return unpaidSalesInvoices;
        else {
            List<SalesInvoice> retval = new LinkedList<>();
            List<SalesInvoice> listSI = null;
            if (salesInvoices != null) {
                listSI = salesInvoices;
            }
            else if (addedSalesInvoices != null) {
                listSI = addedSalesInvoices;
            }
            else if(addedUnpaidSalesInvoices.size() > 0) {
                listSI = addedUnpaidSalesInvoices;
            }

            if(listSI != null) {
                for (SalesInvoice si : listSI) {
                    if (!si.getPaid())
                        retval.add(si);
//
//                    if (retval.size() > 0)
//                        return retval;
                }
            }

//            if(salesInvoices == null && addedSalesInvoices == null && addedUnpaidSalesInvoices != null) {
//                for(SalesInvoice si : addedUnpaidSalesInvoices) {
//                    if(!si.getPaid()) {
//                        retval.add(si);
//                    }
//                }
//            }

            if(retval.size() > 0)
                return retval;


            return  null;
        }
    }

    public static SalesInvoice getInvoiceByNo(String _inv) {
        if(salesInvoices != null) {
            for (SalesInvoice _si : salesInvoices) {
                if (_si.getNoInvoice().equals(_inv))
                    return _si;
            }

            return null;
        }
        else {
            if (unpaidSalesInvoices != null) {
                for (SalesInvoice _si : unpaidSalesInvoices) {
                    if (_si.getNoInvoice().equals(_inv))
                        return _si;
                }
            }

            if (paidSalesInvoices != null) {
                for (SalesInvoice _si : paidSalesInvoices) {
                    if (_si.getNoInvoice().equals(_inv))
                        return _si;
                }
            }

            return null;
        }
    }

//    public static void fillPaymentMethodObjSpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception
//    {
//        final List<PaymentMethodObj> list = new ArrayList<>();
//        list.add(new ProdCategory());
//        if (ProductService.categories != null) {
//            for (ProdCategory ctgr : ProductService.categories) {
//                list.add(ctgr);
//            }
//            Util.fillSpinner(_spinner, list, ProdCategory.class, _ctx);
//        } else {
//            Util.showDialog(_fm, PROGRESS_FETCH_CATEGORY);
//            ProductService.fetchCategories(
//                    new ResultWatcher<List<ProdCategory>>()
//                    {
//                        @Override
//                        public void onResult(Object source, List<ProdCategory> result)
//                        {
//                            Util.stopDialog(PROGRESS_FETCH_CATEGORY);
//                            if (result != null) {
//                                for (ProdCategory ctgr : result) {
//                                    list.add(ctgr);
//                                }
//                                Util.fillSpinner(_spinner, list, ProdCategory.class, _ctx);
//                            }
//                        }
//
//                        @Override
//                        public void onError(Object source, int errCode) throws Exception
//                        {
//                            Util.stopDialog(PROGRESS_FETCH_CATEGORY);
//                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//            );
//        }
//    }
}
