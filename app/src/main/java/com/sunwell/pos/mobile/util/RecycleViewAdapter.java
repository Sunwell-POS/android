package com.sunwell.pos.mobile.util;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 11/13/17.
 */


public abstract class RecycleViewAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements Serializable
{

    public static final int USE_HEADER = 0;
    public static final int USE_FILTER = 1;
    public static final int PLAIN = 2;

    protected static final int HEADER = -1;
    protected static final int FILTER = -2;
    protected Integer editedPosition;
    protected int additionalItem = 0;
    protected List<T> products;

    public RecycleViewAdapter(List<T> _products, int _additionalItem) {
        this.products = new LinkedList<T>(_products);
        additionalItem = _additionalItem;
    }

    @Override
    public abstract V onCreateViewHolder(ViewGroup parent, int viewType) ;

    @Override
    public abstract void onBindViewHolder(V holder, final int position) ;

    public void setItems(List<T> _items) {
        products = new LinkedList<T>(_items);
        Log.d(Util.APP_TAG, "L S: " + _items.size());
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return products;
    }

    @Override
    public int getItemCount() {
        int add = 0;
        if(additionalItem == USE_HEADER)
            add = 1;
        else if(additionalItem == USE_FILTER)
            add = 2;
        return this.products != null ? this.products.size() + add : add;
    }

    @Override
    public int getItemViewType(int position) {
        if ((additionalItem == USE_HEADER || additionalItem == USE_FILTER) && position == 0) {
            return HEADER;
        }
        else if ((additionalItem == USE_FILTER) && position == 1) {
            return FILTER;
        }


        return super.getItemViewType(position);
    }

    public void addItem(T _prod) {
        if(this.products == null)
            this.products = new LinkedList<>();
        this.products.add(_prod);
        notifyItemAdded();
    }

    public void notifyItemAdded() {
        int add = additionalItem == PLAIN ?  -1 : additionalItem == USE_HEADER ? 0 : 1;
        notifyItemInserted(products.size() + add);
    }

    public void updateItems(List<T> _items) throws Exception {

        int add = additionalItem == PLAIN ?  0 : additionalItem == USE_HEADER ? -1 : -2;

        for(T item : _items) {
            int index = products.indexOf(item);
            if(index >= 0) {
//                Log.d(Util.APP_TAG, " Item: " + item.toString() + " index: " + index + " old: " + );
                products.set(index, item);
            }
            else
                products.add(item);
        }

        notifyDataSetChanged();
    }

    public void updateItem(T _item) throws Exception {
        if(editedPosition == null)
            throw new Exception("ERROR, EDITED POSITION IS NOT SET PROPERLY");

        int add = additionalItem == PLAIN ?  0 : additionalItem == USE_HEADER ? -1 : -2;
        products.set(editedPosition + add, _item);
        Log.d(Util.APP_TAG, "UPDATED NO: " + (editedPosition + add));
        notifyItemUpdated();
    }

    public void removeAt(int _pos) {
        int add = additionalItem == PLAIN ?  0 : additionalItem == USE_HEADER ? -1 : -2;
        Log.d(Util.APP_TAG, "POS: " + _pos + " REAL: " + (_pos + add) + " SIZE: " + products.size());
        products.remove(_pos + add);
        notifyDataSetChanged();
    }

    public void notifyItemUpdated() {
        notifyItemChanged(editedPosition);
    }
}
