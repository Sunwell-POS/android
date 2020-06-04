package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.ProductDialogFragment;
import com.sunwell.pos.mobile.dialog.UserDialogFragment;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.Product;
import com.sunwell.pos.mobile.model.User;
import com.sunwell.pos.mobile.model.UserGroup;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.ImageDownloader;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunwell on 10/18/17.
 */

public class UserFragment extends Fragment {

    private static final String PROGRESS_FETCH_USER = "progressFetchUser";
    private static final String PROGRESS_DELETE_USER = "progressDeleteUser";
//    private static int idLayoutProductRow;
//    private static int idLayoutProductHeader;

    private ImageDownloader<UserHolder> downloader;
    private RecyclerView listUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
//            Util.isSmallScreen(getActivity());
            Handler responseHandler = new Handler();
            Drawable defDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
            int imagePxSize = Util.dpToPx(R.dimen.big_image, getActivity());
            downloader = new ImageDownloader<>(responseHandler, defDrawable, imagePxSize, imagePxSize);
            downloader.setThumbnailDownloadListener(
                    new ImageDownloader.DownloadListener<UserHolder>() {

                        @Override
                        public void onImageDownloaded(UserHolder _holder, Bitmap _bitmap) throws Exception {
                            Drawable drawable = new BitmapDrawable(getResources(), _bitmap);
                            _holder.bindDrawable(drawable);
                        }

                        @Override
                        public void onImageDownloaded(UserHolder _tenantHolder, Drawable _drawable) throws Exception {
                            _tenantHolder.bindDrawable(_drawable);
                        }
                    }
            );
            downloader.start();
            downloader.getLooper();
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
            View v = inflater.inflate(R.layout.standard_list, container, false);
            Button btnAddProduct = (Button) v.findViewById(R.id.button_add);
            UserAdapter listAdapter;
//            int width = Util.pxToDp(container.getWidth(), getContext());
//            Log.d(Util.APP_TAG, "W: " + width);
//            if (width < 900) {
//                idLayoutProductHeader = R.layout.product_header_short;
//                idLayoutProductRow = R.layout.product_row_short;
//            } else {
//                idLayoutProductHeader = R.layout.product_header;
//                idLayoutProductRow = R.layout.product_row;
//            }

            btnAddProduct.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FragmentManager fragmentManager = getFragmentManager();
                            UserDialogFragment dialog = new UserDialogFragment();
                            dialog.setTargetFragment(UserFragment.this, Util.REQUEST_CODE_ADD);
                            dialog.show(fragmentManager, "Product");
                        }
                        catch(Exception e) {
                            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            );
            listUser = (RecyclerView) v.findViewById(R.id.list_objects);
            listUser.setLayoutManager(new LinearLayoutManager(getActivity()));
            ResultWatcher<List<User>> listener = new ResultWatcher<List<User>>() {
                @Override
                public void onResult(Object source, List<User> _users) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_USER);
                    Log.d(Util.APP_TAG, " LIST: " + _users);
                    listUser.setAdapter(new UserAdapter(_users));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_USER);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };

            LoginService.fetchUsers(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_USER);
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
                    User user = (User)data.getSerializableExtra("user");
                    ((UserAdapter) listUser.getAdapter()).addItem(user);
                    Toast.makeText(getActivity(), R.string.success_add_product, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
                    Toast.makeText(getActivity(), R.string.fail_add_product, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_EDIT) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    User user = (User)data.getSerializableExtra("user");
                    ((UserAdapter) listUser.getAdapter()).updateItem(user);
                    Toast.makeText(getActivity(), R.string.success_edit_product, Toast.LENGTH_SHORT).show();
//                    ((StockCardAdapter) listUser.getAdapter()).notifyItemUpdated();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_edit_product, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class UserHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textName;
        public TextView textEmail;
        public TextView textUserGroup;
        public ImageView imageUser;
        public ImageView imageStatus;
        public ImageButton btnEdit;
        public ImageButton btnDelete;
        public ImageButton btnSearch;

        public EditText inputName;
        public EditText inputEmail;
        public Spinner spinnerUserGroup;


        public UserHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textEmail = (TextView) itemView.findViewById(R.id.text_email);
            textUserGroup = (TextView) itemView.findViewById(R.id.text_user_group);
            imageUser = (ImageView) itemView.findViewById(R.id.image_user);
            imageStatus = (ImageView) itemView.findViewById(R.id.image_status);

            inputName = (EditText) itemView.findViewById(R.id.input_name);
            inputEmail = (EditText) itemView.findViewById(R.id.input_email);
            spinnerUserGroup = (Spinner) itemView.findViewById(R.id.spinner_user_group);

            btnEdit = (ImageButton) itemView.findViewById(R.id.button_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.button_delete);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }

        public void bindDrawable(Drawable drawable) {
            imageUser.setImageDrawable(drawable);
        }
    }

    private class UserAdapter extends RecycleViewAdapter<User, UserHolder>
    {
        private List<User> fullUsers;

        public UserAdapter(List<User> _users) {
            super(new LinkedList<>(_users), RecycleViewAdapter.USE_FILTER);
            fullUsers = _users;
        }

//        public void addItem(Product _prod) {
//            if(this.products == null)
//                this.products = new LinkedList<>();
//
//            this.products.add(_prod);
//            Log.d(Util.APP_TAG, "ADD : " + _prod.getName());
//            notifyItemInserted(this.products.size());
//        }


        @Override
        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                View view;
                if (viewType == HEADER) {
                    view = layoutInflater.inflate(R.layout.user_header, parent, false);
                }
                else if(viewType == FILTER) {
                    view = layoutInflater.inflate(R.layout.user_filter, parent, false);
                }
                else {
                    view = layoutInflater.inflate(R.layout.user_row, parent, false);
                }
                return new UserHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final UserHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {

                    final User user = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textName.setText(user.getName());
                    holder.textEmail.setText(user.getEmail());
                    holder.textUserGroup.setText(user.getUserGroup().getName() + "");
//                    if (user.getCategories() != null && user.getCategories().size() > 0) {
//                        Log.d(Util.APP_TAG, " NAME: " + user.getName() + " SIZE: " + user.getCategories().size());
//                        holder.textCategory.setText(user.getCategories().get(0).getName());
//                    }
                    Drawable drawable;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        if(user.get() != null && user.getBarCode().length() > 0)
//                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
//                        else
//                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());
//
//                        holder.imageBarcode.setImageDrawable(drawable);
//
//                        if(user.getHasStock())
//                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
//                        else
//                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());
                        drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
                        holder.imageStatus.setImageDrawable(drawable);
                    }
                    else {
//                        if(user.getBarCode() != null && user.getBarCode().length() > 0)
//                            drawable = getResources().getDrawable(R.drawable.right);
//                        else
//                            drawable = getResources().getDrawable(R.drawable.right);
//
//                        holder.imageBarcode.setImageDrawable(drawable);
//
//                        if(user.getHasStock())
//                            drawable = getResources().getDrawable(R.drawable.right);
//                        else
//                            drawable = getResources().getDrawable(R.drawable.right);

                        drawable = getResources().getDrawable(R.drawable.right);
                        holder.imageStatus.setImageDrawable(drawable);
                    }

                    holder.btnEdit.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        UserDialogFragment dialog = UserDialogFragment.newInstance(user);
                                        dialog.setTargetFragment(UserFragment.this, Util.REQUEST_CODE_EDIT);
                                        editedPosition = position;
                                        dialog.show(getFragmentManager(), "Product");
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
//
                    holder.btnDelete.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        String systemId = user.getSystemId();
                                        LoginService.deleteUser(
                                                new ResultWatcher<Boolean>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, Boolean result) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_USER);
                                                        ((UserAdapter) listUser.getAdapter()).removeAt(position);
                                                        Toast.makeText(getActivity(), R.string.success_delete_user, Toast.LENGTH_SHORT).show();
//                                                        listUser.getAdapter().notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode) throws Exception
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_USER);
                                                        Toast.makeText(getActivity(), R.string.fail_delete_user, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , systemId
                                        );
                                        Util.showDialog(getFragmentManager(), PROGRESS_DELETE_USER);
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );

                    if (user.getImg() == null)
                        downloader.queueImage(holder, null);
                    else {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sessionString", LoginService.sessionString);
                        params.put("image", user.getImg());
                        downloader.queueImage(holder, Util.getImageURL(Util.USER_URL, params));
                        Log.d(Util.APP_TAG, "Prod: " + user.getName() + " pos: " + position + " url: " + Util.getImageURL(Util.USER_URL, params));
                    }
                }
                else if(position == 1) {
                    LoginService.fillUserGroupSpinner(holder.spinnerUserGroup, getActivity(), getFragmentManager());
                    holder.btnSearch.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    String text = holder.inputName.getText().toString();
                                    String email = holder.inputEmail.getText().toString();
                                    UserGroup ug = null;
                                    if(holder.spinnerUserGroup.getSelectedItemPosition() > 0)
                                        ug = (UserGroup )holder.spinnerUserGroup.getSelectedItem();
                                    List<User> filteredUsers = new LinkedList<>();
                                    if(fullUsers != null) {
                                        for (User u : fullUsers) {
                                            if(text != null && text.length() > 0)
                                                if(!u.getName().toLowerCase().contains(text.toLowerCase()))
                                                    continue;

                                            if(email != null && email.length() > 0)
                                                if(!u.getEmail().toLowerCase().contains(email.toLowerCase()))
                                                    continue;

                                            if(ug != null )
                                                if(!u.getUserGroup().equals(ug))
                                                    continue;

                                            filteredUsers.add(u);
                                        }
                                        setItems(filteredUsers);
                                    }
                                }
                            }
                    );
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
