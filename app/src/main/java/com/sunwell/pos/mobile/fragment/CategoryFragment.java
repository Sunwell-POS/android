package com.sunwell.pos.mobile.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunwell.pos_mobile.R;
import com.sunwell.pos.mobile.dialog.CategoryDialogFragment;
import com.sunwell.pos.mobile.model.ProdCategory;
import com.sunwell.pos.mobile.service.ProductService;
import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.ResultWatcher;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 10/18/17.
 */

public class CategoryFragment extends Fragment {

    private static final String PROGRESS_FETCH_CATEGORY = "progressFetchCustomer";
    private static final String PROGRESS_DELETE_CATEGORY = "progressDeleteCategory";
    private RecyclerView listCategory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            super.onCreateView(inflater, container, savedInstanceState);
            View v = inflater.inflate(R.layout.layout_category, container, false);
            Button btnAddCtgr = (Button) v.findViewById(R.id.button_add_category);
            ProdCategoryAdapter listAdapter;

            btnAddCtgr.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                FragmentManager fragmentManager = getFragmentManager();
                                CategoryDialogFragment dialog = new CategoryDialogFragment();
                                dialog.setTargetFragment(CategoryFragment.this, Util.REQUEST_CODE_ADD);
                                dialog.show(fragmentManager, "Category");
                            }
                            catch(Exception e) {
                                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
            listCategory = (RecyclerView) v.findViewById(R.id.list_category);
            listCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
            ResultWatcher<List<ProdCategory>> listener = new ResultWatcher<List<ProdCategory>>() {
                @Override
                public void onResult(Object source, List<ProdCategory> _categories) throws Exception{
                    Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                    listCategory.setAdapter(new ProdCategoryAdapter(_categories));
                }

                @Override
                public void onError(Object source, int errCode) throws Exception {
                    Util.stopDialog(PROGRESS_FETCH_CATEGORY);
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }
            };
            ProductService.fetchCategories(listener);
            Util.showDialog(getFragmentManager(), PROGRESS_FETCH_CATEGORY);
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
                    ProdCategory ctgr = (ProdCategory)data.getSerializableExtra("category");
                    ((ProdCategoryAdapter) listCategory.getAdapter()).addItem(ctgr);
                    Toast.makeText(getActivity(), R.string.success_add_category, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_add_category, Toast.LENGTH_SHORT).show();
                }
            } else if (_requestCode == Util.REQUEST_CODE_EDIT) {
                if (_resultCode == Util.RESULT_CODE_SUCCESS) {
                    ProdCategory category = (ProdCategory)data.getSerializableExtra("category");
                    ((ProdCategoryAdapter) listCategory.getAdapter()).updateItem(category);
                    Toast.makeText(getActivity(), R.string.success_edit_category, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.fail_edit_category, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e) {
            Log.d(Util.APP_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
        }
    }

    private class ProdCategoryHolder extends RecyclerView.ViewHolder {

        public TextView textNo;
        public TextView textName;
        public ImageButton btnDefault;
        public ImageButton btnColor;
        public ImageButton btnEdit;
        public ImageButton btnDelete;
        public ImageButton btnSearch;

        public EditText inputName;
        public Spinner spinnerDefault;

        public ProdCategoryHolder(View itemView) {
            super(itemView);
            textNo = (TextView) itemView.findViewById(R.id.text_no);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            btnDefault = (ImageButton) itemView.findViewById(R.id.button_default);
            btnColor = (ImageButton) itemView.findViewById(R.id.button_color);
            btnEdit = (ImageButton) itemView.findViewById(R.id.button_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

            inputName = (EditText) itemView.findViewById(R.id.input_name);
            spinnerDefault = (Spinner) itemView.findViewById(R.id.spinner_default);
            btnSearch = (ImageButton) itemView.findViewById(R.id.button_search);
        }
    }

    private class ProdCategoryAdapter extends RecycleViewAdapter<ProdCategory, ProdCategoryHolder>
    {
        private List<ProdCategory> fullCategories;

        public ProdCategoryAdapter(List<ProdCategory> _categories) {
            super(new LinkedList<>(_categories), RecycleViewAdapter.USE_FILTER);
            fullCategories = _categories;
        }

        @Override
        public ProdCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view;
                if (viewType == HEADER)
                    view = layoutInflater.inflate(R.layout.category_header, parent, false);
                else if (viewType == FILTER)
                    view = layoutInflater.inflate(R.layout.category_filter, parent, false);
                else
                    view = layoutInflater.inflate(R.layout.category_row, parent, false);
                return new ProdCategoryHolder(view);
            }
            catch(Exception e) {
                Log.d(Util.APP_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(final ProdCategoryHolder holder, final int position) {
            try {
                if (position == 0 || products == null)
                    return;

                if(position > 1) {
                    final ProdCategory ctgr = this.products.get(position - 2);
                    holder.textNo.setText("" + (position - 1));
                    holder.textName.setText(ctgr.getName());
                    Drawable drawable;
                    if (ctgr.isDefault1()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            drawable = getResources().getDrawable(R.drawable.right, getActivity().getTheme());
                        else
                            drawable = getResources().getDrawable(R.drawable.right);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            drawable = getResources().getDrawable(R.drawable.wrong, getActivity().getTheme());
                        else
                            drawable = getResources().getDrawable(R.drawable.wrong);
                    }

                    holder.btnDefault.setImageDrawable(drawable);
                    if(ctgr.getBgColor() != null)
                        holder.btnColor.setBackgroundColor(Color.parseColor(ctgr.getBgColor()));
                    else
                        holder.btnColor.setBackgroundColor(Color.parseColor("#dbdedd"));

                    holder.btnEdit.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    try {
                                        CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(ctgr);
                                        dialog.setTargetFragment(CategoryFragment.this, Util.REQUEST_CODE_EDIT);
                                        editedPosition = position;
                                        Log.d(Util.APP_TAG, " POSITION: " + editedPosition + " CTGR: " + ctgr.hashCode());
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
                                        String systemId = ctgr.getSystemId();
                                        ProductService.deleteCategory(
                                                new ResultWatcher<Boolean>()
                                                {
                                                    @Override
                                                    public void onResult(Object source, Boolean result)
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_CATEGORY);
                                                        removeAt(position);
                                                        Toast.makeText(getActivity(), R.string.success_delete_category, Toast.LENGTH_SHORT).show();
//                                                        listCategory.getAdapter().notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onError(Object source, int errCode)
                                                    {
                                                        Util.stopDialog(PROGRESS_DELETE_CATEGORY);
                                                        Toast.makeText(getActivity(), R.string.fail_delete_category, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                , systemId);
                                        Util.showDialog(getFragmentManager(), PROGRESS_DELETE_CATEGORY);
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
                                    String filterName = holder.inputName.getText().toString();
                                    Integer posDefault = holder.spinnerDefault.getSelectedItemPosition();
                                    List<ProdCategory> filteredcategories = new LinkedList<>();
                                    if(fullCategories != null && fullCategories.size() > 0) {
                                        for(ProdCategory ctgr : fullCategories) {
                                            Log.d(Util.APP_TAG, "NAME: " + filterName + " C NAME: " + ctgr.getName());
                                            if (filterName != null && filterName.length() > 0) {
                                                if(!ctgr.getName().toLowerCase().contains(filterName.toLowerCase()))
                                                    continue;
                                            }

                                            if(posDefault > 0) {
                                                boolean def = posDefault == 1 ? true : false;
                                                if(ctgr.isDefault1() != null) {
                                                    if (def) {
                                                        if (!ctgr.isDefault1())
                                                            continue;
                                                    }
                                                    else {
                                                        if(ctgr.isDefault1())
                                                            continue;
                                                    }
                                                }
                                                else {
                                                    if(def)
                                                        continue;
                                                }
                                            }

                                            filteredcategories.add(ctgr);
                                        }

                                        setItems(filteredcategories);
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
