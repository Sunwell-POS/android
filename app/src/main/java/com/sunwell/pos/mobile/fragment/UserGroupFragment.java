package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.CategoryDialogFragment;
import com.sunwell.pos.mobile.dialog.UserGroupDialogFragment;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.model.UserGroup;
import com.sunwell.pos.mobile.service.LoginService;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/18/17.
 */

public class UserGroupFragment extends Fragment {

    private static final String PROGRESS_FETCH_USER_GROUP = "progressFetchUserGroup";
    private static final String PROGRESS_DELETE_USER_GROUP = "progressDeleteUserGroup";

    RecyclerView listObjects;
//    private List<UserGroup> userGroups;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            try {
                View v = inflater.inflate(R.layout.standard_list, container, false);
                Button btnAdd = (Button) v.findViewById(R.id.button_add);
                UserGroupAdapter listAdapter;
                btnAdd.setText(R.string.add_user_group);
                btnAdd.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FragmentManager fragmentManager = getFragmentManager();
                                UserGroupDialogFragment dialog = new UserGroupDialogFragment();
                                dialog.setTargetFragment(UserGroupFragment.this, Util.REQUEST_CODE_ADD);
                                dialog.show(fragmentManager, "userGroup");
                            }
                        }
                );
                listObjects = (RecyclerView) v.findViewById(R.id.list_objects);
                listObjects.setLayoutManager(new LinearLayoutManager(getActivity()));
                ResultWatcher<List<UserGroup>> listener = new ResultWatcher<List<UserGroup>>() {
                    @Override
                    public void onResult(Object source, List<UserGroup> _groups) {
                        Util.stopDialog(PROGRESS_FETCH_USER_GROUP);
                        listObjects.setAdapter(new UserGroupAdapter(_groups));
                    }

                    @Override
                    public void onError(Object source, int errCode) {
                        Util.stopDialog(PROGRESS_FETCH_USER_GROUP);
                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                };

                LoginService.fetchUserGroups(listener);
                Util.showDialog(getFragmentManager(), PROGRESS_FETCH_USER_GROUP);
                return v;
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
    }

    @Override
    public void onActivityResult(int _requestCode, int _resultCode, Intent data) {
        try {
            if (_requestCode == Util.REQUEST_CODE_ADD) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    UserGroup ug = (UserGroup) data.getSerializableExtra("userGroup");
                    ((UserGroupAdapter) listObjects.getAdapter()).addItem(ug);
                    Toast.makeText(getActivity(), R.string.success_add_user_group, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Util.APP_TAG, " ERR CODE: " + data.getIntExtra("errorCode", -3));
                    Toast.makeText(getActivity(), R.string.fail_add_user_group, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_EDIT) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    UserGroup ug = (UserGroup) data.getSerializableExtra("userGroup");
                    ((UserGroupAdapter) listObjects.getAdapter()).updateItem(ug);
                    Toast.makeText(getActivity(), R.string.success_edit_user_group, Toast.LENGTH_SHORT).show();
//                ((UserGroupAdapter) listObjects.getAdapter()).notifyItemUpdated();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_edit_user_group, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class UserGroupHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textType;
        public EditText inputType;
        public ImageButton btnEdit;
        public ImageButton btnDelete;
        public ImageButton btnSearch;

        public UserGroupHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textType = (TextView) itemView.findViewById(R.id.text_type);
            inputType = (EditText) itemView.findViewById(R.id.input_type);
            btnEdit = (ImageButton) itemView.findViewById(R.id.button_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.button_delete);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }
    }

    private class UserGroupAdapter extends RecycleViewAdapter<UserGroup, UserGroupHolder>
    {
        private List<UserGroup> fullGroups;

        public UserGroupAdapter(List<UserGroup> _groups) {
            super(new LinkedList<>(_groups), RecycleViewAdapter.USE_FILTER);
            fullGroups = _groups;
        }

        @Override
        public UserGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view;
                if (viewType == HEADER)
                    view = layoutInflater.inflate(R.layout.user_group_header, parent, false);
                else if (viewType == FILTER)
                    view = layoutInflater.inflate(R.layout.user_group_filter, parent, false);
                else
                    view = layoutInflater.inflate(R.layout.user_group_row, parent, false);
                return new UserGroupHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final UserGroupHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {
                    final UserGroup ug = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textType.setText(ug.getName());
//                    Drawable drawable;
//                    if (ctgr.isDefault1()) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
//                        else
//                            drawable = getResources().getDrawable(R.drawable.right);
//                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());
//                        else
//                            drawable = getResources().getDrawable(R.drawable.wrong);
//                    }
//
//                    holder.btnDefault.setImageDrawable(drawable);

                    holder.btnEdit.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        UserGroupDialogFragment dialog = UserGroupDialogFragment.newInstance(ug);
                                        dialog.setTargetFragment(UserGroupFragment.this, Util.REQUEST_CODE_EDIT);
                                        editedPosition = position;
                                        Log.d(Util.APP_TAG, " POSITION: " + editedPosition + " CTGR: " + ug.hashCode());
                                        dialog.show(getFragmentManager(), "Category");
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );

                    holder.btnDelete.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        String systemId = ug.getSystemId();
                                        LoginService.deleteUserGroup(
                                                new ResultWatcher<Boolean>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, Boolean result)
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_USER_GROUP);
                                                        removeAt(position);
                                                        Toast.makeText(getActivity(), R.string.success_delete_user_group, Toast.LENGTH_SHORT).show();
//                                                        listCategory.getAdapter().notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode)
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_USER_GROUP);
                                                        Toast.makeText(getActivity(), R.string.fail_delete_user_group, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , systemId);
                                        Util.showDialog(getFragmentManager(), PROGRESS_DELETE_USER_GROUP);
                                    }
                                    catch (Exception e) {
                                        Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
                }
                else {
                    holder.btnSearch.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
//                                    filter(holder);
                                    String filterName = holder.inputType.getText().toString();
                                    List<UserGroup> filteredGroups = new LinkedList<>();
                                    if(fullGroups != null && fullGroups.size() > 0) {
                                        for(UserGroup ug : fullGroups) {
                                            Log.d(Util.APP_TAG, "NAME: " + filterName + " C NAME: " + ug.getName());
                                            if (filterName != null && filterName.length() > 0) {
                                                if(!ug.getName().toLowerCase().contains(filterName.toLowerCase()))
                                                    continue;
                                            }

                                            filteredGroups.add(ug);
                                        }

                                        setItems(filteredGroups);
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

//    private class UserGroupAdapter extends RecyclerView.Adapter<UserGroupHolder> {
//        private List<UserGroup> userGroups;
//        private static final int HEADER = -1;
//        private int editedPosition;
//
//        public UserGroupAdapter(List<UserGroup> _groups) {
//            this.userGroups = _groups;
//        }
//
//        public void addItem(UserGroup _group) {
//            if(this.userGroups == null)
//                this.userGroups = new LinkedList<>();
//
//            this.userGroups.add(_group);
//            Log.d(Util.APP_TAG, "ADD : " + _group.getName());
//            notifyItemInserted(this.userGroups.size());
//        }
//
//        public void notifyItemAdded() {
//            notifyItemInserted(userGroups.size());
//        }
//        public void notifyItemUpdated() {
//            listObjects.getAdapter().notifyItemChanged(editedPosition);
//        }
//
//        @Override
//        public UserGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//            View view ;
//            if(viewType == HEADER)
//                view = layoutInflater.inflate(R.layout.user_group_header, parent, false);
//            else
//                view = layoutInflater.inflate(R.layout.user_group_row, parent, false);
//            return new UserGroupHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(UserGroupHolder holder, final int position) {
//            if(position == 0 || userGroups == null)
//                return ;
//
//
//            final UserGroup ug = this.userGroups.get(position - 1);
//            holder.textNo.setText("" + position);
//            holder.textType.setText(ug.getName());
//            Drawable drawable;
////            if(ctgr.isDefault1()) {
////                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
////                    drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
////                else
////                    drawable = getResources().getDrawable(R.drawable.right);
////            }
////            else {
////                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
////                    drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());
////                else
////                    drawable = getResources().getDrawable(R.drawable.wrong);
////            }
//
////            holder.btnDefault.setImageDrawable(drawable);
//
////            holder.btnEdit.setOnClickListener(
////                new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(ctgr);
////                        dialog.setTargetFragment(UserGroupFragment.this, Util.REQUEST_CODE_EDIT);
////                        editedPosition = position;
////                        dialog.show(getFragmentManager(), "Category");
////                    }
////                }
////            );
////
////            holder.btnDelete.setOnClickListener(
////                    new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            try {
////                                String systemId = userGroups.get(position - 1).getSystemId();
////                                ProductService.deleteCategory(
////                                        new ResultWatcher<Boolean>() {
////                                            @Override
////                                            public void onResult(Boolean result) {
////                                                Toast.makeText(getActivity(), R.string.success_delete_category, Toast.LENGTH_SHORT).show();
////                                                listObjects.getAdapter().notifyDataSetChanged();
////                                            }
////
////                                            @Override
////                                            public void onError(int errCode) {
////                                                Toast.makeText(getActivity(), R.string.fail_delete_category, Toast.LENGTH_SHORT).show();
////                                            }
////                                        }
////                                        , systemId);
////                            }
////                            catch(Exception e) {
////                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
////                                e.printStackTrace();
////                            }
////                        }
////                    }
////            );
//        }
//
//        @Override
//        public int getItemCount() {
//
//            return this.userGroups != null ? this.userGroups.size() + 1 : 1;
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

}
