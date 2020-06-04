package com.sunwell.pos.mobile.service;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.model.Customer;
import com.sunwell.pos.mobile.model.Tenant;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.model.UserGroup;
import com.sunwell.pos.mobile.util.ResultListener;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/17/17.
 */

public class LoginService
{
    private static String PROGRESS_FETCH_CUSTOMER = "progressFetchCustomer";
    private static final String PROGRESS_FETCH_USER_GROUP = "progressFetchUserGroup";
    private static List<ResultListener<User>> listLogoutListener = new LinkedList<>();
    public static String sessionString;
    public static User currentUser;
    public static Tenant currentTenant;
    public static List<UserGroup> userGroups;
    public static List<User> users;
    public static List<Customer> customers;

    private LoginService()
    {
        Log.d(Util.APP_TAG, "NOTHING");
    }

    public static void addLogoutListener(ResultListener<User> _listener) {
        listLogoutListener.add(_listener);
    }

    public static void removeLogoutListener(ResultListener<User> _listener) {
        listLogoutListener.remove(_listener);
    }

    public static void companyLogin(String _email, final ResultWatcher<Tenant> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("email", _email);
        ResultWatcher<Tenant> listener = new ResultWatcher<Tenant>()
        {

            @Override
            public void onResult(Object source, Tenant result) throws Exception
            {
                currentTenant = result;
                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, errCode);
            }
        };
        new GeneralTask<Tenant>(listener, Util.COMPANY_URL, Tenant.class).get(json);
    }

    public static void login(String _email, String _password, final ResultWatcher<User> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        JSONObject tenant = new JSONObject();
        tenant.put("systemId", currentTenant.getSystemId());
        json.put("email", _email);
        json.put("password", _password);
        json.put("tenant", tenant);

        ResultWatcher<User> listener = new ResultWatcher<User>()
        {
            @Override
            public void onResult(Object source, User result) throws Exception
            {
                currentUser = result;
                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int errCode) throws Exception
            {
                _listener.onError(null, errCode);
            }

            @Override
            public void onData(Object source, Map<String, Object> data) throws Exception
            {
                sessionString = (String) data.get("sessionString");
            }
        };

        GeneralTask<User> gt = new GeneralTask<>(listener, Util.LOGIN_URL, User.class);
        gt.setRequestedAttributes("sessionString");
        gt.post(json);
    }

    public static void logout(final ResultWatcher<User> _listener) throws Exception {
        if(sessionString == null)
            return;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("sessionString", sessionString);
        ResultWatcher<User> listener = new ResultWatcher<User>()
        {
            @Override
            public void onResult(Object source, User result) throws Exception
            {
                for(ResultListener<User> listener : listLogoutListener) {
                    listener.onResult(null, result);
                }
                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.LOGOUT_URL, User.class).post(jsonObj);
    }

    public static void addUser(final ResultListener<User> _listener, User _user) throws Exception
    {
        JSONObject jsonObj = Util.toJSONObject(_user, User.class);
        Log.d(Util.APP_TAG, "JSON: " + jsonObj.toString());
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<User> listener = new ResultWatcher<User>()
        {
            @Override
            public void onResult(Object source, User result) throws Exception
            {
                if (users != null) {
                    Log.d(Util.APP_TAG, "Adding now...");
                    users.add(result);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.USER_URL, User.class).post(jsonObj);
    }

    public static void editUser(final ResultListener<User> _listener, final User _user) throws Exception
    {
        JSONObject jsonObject = Util.toJSONObject(_user, User.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        ResultWatcher<User> listener = new ResultWatcher<User>()
        {
            @Override
            public void onResult(Object source, User result) throws Exception
            {
                if (users != null) {
                    for (int i = 0; i < users.size(); i++) {
                        User user = users.get(i);
                        if (user.getSystemId().equals(_user.getSystemId())) {
                            users.set(i, user);
                            break;
                        }
                    }
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.USER_URL, User.class).put(jsonObject);
    }

    public static void deleteUser(final ResultListener<Boolean> _listener, final String _id) throws Exception
    {
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>()
        {
            @Override
            public void onResult(Object source, Boolean result) throws Exception
            {
                if (users != null) {
                    User user = null;
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getSystemId().equals(_id)) {
                            user = users.get(i);
                            break;
                        }
                    }
                    users.remove(user);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.USER_URL, null).delete(param);
    }

    public static void fetchUsers(final ResultWatcher<List<User>> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<User>> gt = new GeneralTask<>(_listener, Util.USER_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<User>>()
                {

                    @Override
                    public List<User> parse(JSONObject response) throws Exception
                    {
                        users = null;
                        if (!response.has("listUser"))
                            return null;

                        JSONArray jArrCategory = response.getJSONArray("listUser");
                        if (jArrCategory == null || jArrCategory.length() <= 0)
                            return null;

                        User[] arrUsers = Util.parseJSONData(jArrCategory.toString(), User[].class);
                        if (arrUsers != null && arrUsers.length > 0) {
                            users = new LinkedList<>(Arrays.asList(arrUsers));
                        }
                        Log.d(Util.APP_TAG, "UG SIZE: " + users.size());
                        return users;
                    }
                }
        );
        gt.get(json);
    }

    public static void addUserGroup(final ResultListener<UserGroup> _listener, UserGroup _userGroup) throws Exception
    {
        JSONObject jsonObj = Util.toJSONObject(_userGroup, UserGroup.class);
        Log.d(Util.APP_TAG, "JSON: " + jsonObj.toString());
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<UserGroup> listener = new ResultWatcher<UserGroup>()
        {
            @Override
            public void onResult(Object source, UserGroup result) throws Exception
            {
                if (userGroups != null) {
                    Log.d(Util.APP_TAG, "Adding now...");
                    userGroups.add(result);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.USER_GROUP_URL, UserGroup.class).post(jsonObj);
    }

    public static void editUserGroup(final ResultListener<UserGroup> _listener, final UserGroup _userGroup) throws Exception
    {
        JSONObject jsonObject = Util.toJSONObject(_userGroup, UserGroup.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        ResultWatcher<UserGroup> listener = new ResultWatcher<UserGroup>()
        {
            @Override
            public void onResult(Object source, UserGroup result) throws Exception
            {
                if (userGroups != null) {
                    for (int i = 0; i < userGroups.size(); i++) {
                        UserGroup ug = userGroups.get(i);
                        if (ug.getSystemId().equals(_userGroup.getSystemId())) {
                            userGroups.set(i, ug);
                            break;
                        }
                    }
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.USER_GROUP_URL, UserGroup.class).put(jsonObject);
    }

    public static void deleteUserGroup(final ResultListener<Boolean> _listener, final String _id) throws Exception
    {
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>()
        {
            @Override
            public void onResult(Object source, Boolean result) throws Exception
            {
                if (userGroups != null) {
                    UserGroup ug = null;
                    for (int i = 0; i < userGroups.size(); i++) {
                        if (userGroups.get(i).getSystemId().equals(_id)) {
                            ug = userGroups.get(i);
                            break;
                        }
                    }
                    userGroups.remove(ug);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.USER_GROUP_URL, null).delete(param);
    }

    public static void fetchUserGroups(final ResultWatcher<List<UserGroup>> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<UserGroup>> gt = new GeneralTask<>(_listener, Util.USER_GROUP_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<UserGroup>>()
                {

                    @Override
                    public List<UserGroup> parse(JSONObject response) throws Exception
                    {
                        userGroups = null;
                        if (!response.has("listUserGroup"))
                            return null;

                        JSONArray jArrCategory = response.getJSONArray("listUserGroup");
                        if (jArrCategory == null || jArrCategory.length() <= 0)
                            return null;

                        UserGroup[] arrGroups = Util.parseJSONData(jArrCategory.toString(), UserGroup[].class);
                        if (arrGroups != null && arrGroups.length > 0) {
                            userGroups = new LinkedList<>(Arrays.asList(arrGroups));
                        }
                        Log.d(Util.APP_TAG, "UG SIZE: " + userGroups.size());
                        return userGroups;
                    }
                }
        );
        gt.get(json);
    }

    public static void fetchCustomers(final ResultWatcher<List<Customer>> _listener) throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("sessionString", LoginService.sessionString);
        GeneralTask<List<Customer>> gt = new GeneralTask<>(_listener, Util.CUSTOMER_URL, null);
        gt.setParser(
                new GeneralTask.ResponseParser<List<Customer>>()
                {

                    @Override
                    public List<Customer> parse(JSONObject response) throws Exception
                    {
                        customers = null;
                        if (!response.has("listCustomer"))
                            return null;

                        JSONArray jArrCategory = response.getJSONArray("listCustomer");
                        if (jArrCategory == null || jArrCategory.length() <= 0)
                            return null;

                        Customer[] arrCustomers = Util.parseJSONData(jArrCategory.toString(), Customer[].class);
                        if (arrCustomers != null && arrCustomers.length > 0) {
                            customers = new LinkedList<>(Arrays.asList(arrCustomers));
                        }
                        Log.d(Util.APP_TAG, "C SIZE: " + customers.size());
                        return customers;
                    }
                }
        );
        gt.get(json);
    }

    public static void addCustomer(final ResultListener<Customer> _listener, Customer _cust) throws Exception
    {
        JSONObject jsonObj = Util.toJSONObject(_cust, Customer.class);
        jsonObj.put("sessionString", LoginService.sessionString);
        ResultWatcher<Customer> listener = new ResultWatcher<Customer>()
        {
            @Override
            public void onResult(Object source, Customer result) throws Exception
            {
                if (customers != null) {
                    Log.d(Util.APP_TAG, "Adding now...");
                    customers.add(result);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(source, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.CUSTOMER_URL, Customer.class).post(jsonObj);
    }

    public static void editCustomer(final ResultListener<Customer> _listener, final Customer _cust) throws Exception
    {
        JSONObject jsonObject = Util.toJSONObject(_cust, Customer.class);
        jsonObject.put("sessionString", LoginService.sessionString);
        ResultWatcher<Customer> listener = new ResultWatcher<Customer>()
        {
            @Override
            public void onResult(Object source, Customer result) throws Exception
            {
                if (customers != null) {
                    for (int i = 0; i < customers.size(); i++) {
                        Customer c = customers.get(i);
                        if (c.getSystemId().equals(_cust.getSystemId())) {
                            customers.set(i, result);
                            break;
                        }
                    }
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.CUSTOMER_URL, Customer.class).put(jsonObject);
    }

    public static void deleteCustomer(final ResultListener<Boolean> _listener, final String _id) throws Exception
    {
        JSONObject param = new JSONObject();
        param.put("sessionString", LoginService.sessionString);
        param.put("systemId", _id);
        ResultWatcher<Boolean> listener = new ResultWatcher<Boolean>()
        {
            @Override
            public void onResult(Object source, Boolean result) throws Exception
            {
                if (customers != null) {
                    Customer c = null;
                    for (int i = 0; i < customers.size(); i++) {
                        if (customers.get(i).getSystemId().equals(_id)) {
                            c = customers.get(i);
                            break;
                        }
                    }
                    customers.remove(c);
                }

                if (_listener != null)
                    _listener.onResult(null, result);
            }

            @Override
            public void onError(Object source, int _errCode) throws Exception
            {
                if (_listener != null)
                    _listener.onError(null, _errCode);
            }
        };
        new GeneralTask<>(listener, Util.CUSTOMER_URL, null).delete(param);
    }

    public static void fillCustomerSpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception {
        final List<Customer> list = new ArrayList<>();
        list.add(new Customer());
        if (customers != null) {
            for (Customer cust : customers) {
                list.add(cust);
            }
            Util.fillSpinner(_spinner, list, Customer.class, _ctx);
        } else {
            Util.showDialog(_fm, PROGRESS_FETCH_CUSTOMER);
            fetchCustomers(
                    new ResultWatcher<List<Customer>>()
                    {
                        @Override
                        public void onResult(Object source, List<Customer> result)
                        {
                            Util.stopDialog(PROGRESS_FETCH_CUSTOMER);
                            if (result != null) {
                                for (Customer cust : result) {
                                    list.add(cust);
                                }
                                Util.fillSpinner(_spinner, list, Customer.class, _ctx);
                            }
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception {
                            Util.stopDialog(PROGRESS_FETCH_CUSTOMER);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    public static void fillUserGroupSpinner(final Spinner _spinner, final Context _ctx, final FragmentManager _fm) throws Exception {
        final List<UserGroup> list = new ArrayList<>();
        list.add(new UserGroup());
        if (userGroups != null) {
            for (UserGroup ug : userGroups) {
                list.add(ug);
            }
            Util.fillSpinner(_spinner, list, UserGroup.class, _ctx);
        } else {
            Util.showDialog(_fm, PROGRESS_FETCH_USER_GROUP);
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
                                Util.fillSpinner(_spinner, list, UserGroup.class, _ctx);
                            }
                        }

                        @Override
                        public void onError(Object source, int errCode) throws Exception {
                            Util.stopDialog(PROGRESS_FETCH_USER_GROUP);
                            Toast.makeText(_ctx, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }


//    ------------------------------------------------ old code ----------------------------------------------------


//    @Deprecated
//    public interface LoginResultListener extends ResultWatcher<User> {
//    }
//
//    @Deprecated
//    public interface CompanyLoginResultListener extends ResultWatcher<Tenant> {
//    }
//
//    @Deprecated
//    private static class CompanyLoginTask extends AsyncTask<String, Void, Tenant>
//    {
//        private CompanyLoginResultListener listener;
//        int errCode = -1;
//
//        public CompanyLoginTask(CompanyLoginResultListener _listener) {
//            listener = _listener;
//        }
//
//        @Override
//        protected Tenant doInBackground(String... params) {
//            try {
//                String email = params[0];
//                Map<String, String> p = new HashMap<>();
//                p.put("email", email);
//                String responseString = Util.getRequest(Util.COMPANY_URL, p);
//                JSONObject responseJSon = new JSONObject(responseString);
//                if(responseJSon.has("errorCode")) {
//                    errCode = responseJSon.getInt("errorCode");
//                    return null;
//                }
//                Log.d(Util.APP_TAG, "Response: " + responseJSon.toString(4));
//                currentTenant = Util.parseJSONData(responseString, Tenant.class);
//                return currentTenant;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Tenant tenant) {
//            if(errCode != -1)
//                listener.onError(errCode);
//            else
//                listener.onResult(tenant);
//        }
//    }
//
//    @Deprecated
//    private static class LoginTask extends AsyncTask<String, Void, User>
//    {
//        LoginResultListener listener ;
//        int errCode = -1;
//
//        public LoginTask(LoginResultListener _listener) {
//            listener = _listener;
//        }
//
//        @Override
//        protected User doInBackground(String... params) {
//            try {
//                String email = params[0];
//                String password = params[1];
//                JSONObject json = new JSONObject();
//                json.put("email", email);
//                json.put("password", password);
//                String responseString = Util.postRequest(Util.LOGIN_URL, json);
//                JSONObject responseJSon = new JSONObject(responseString);
//                if(responseJSon.has("errorCode")) {
//                    errCode = responseJSon.getInt("errorCode");
//                    return null;
//                }
//                Log.d(Util.APP_TAG, "Response: " + responseJSon.toString(4));
//                json = new JSONObject(responseString);
//                sessionString = json.getString("sessionString");
//                currentUser = Util.parseJSONData(responseString, User.class);
//                return currentUser;
//            } catch (Exception e) {
//                Log.d(Util.APP_TAG, "Exception: " + e.getMessage());
//                e.printStackTrace();
//                return null;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(User _user) {
//            if(errCode != -1)
//                listener.onError(errCode);
//            else
//                listener.onResult(_user);
//        }
//    }
}
