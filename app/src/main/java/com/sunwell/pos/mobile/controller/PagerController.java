package com.sunwell.pos.mobile.controller;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.sunwell.pos.mobile.util.RecycleViewAdapter;
import com.sunwell.pos.mobile.util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunwell on 1/19/18.
 */

public class PagerController<T>
{
    public static final String ADAPTER = "adapter";
    public static final String OBJ_PER_PAGE = "objPerPage";
    protected int numberOfPages = 0;
    protected int objectPerPages = 1;
    protected int currentPage = -1;
    //    private NamedObjectTranslator<T> translator;
    protected List<T> fullListObjects;
    protected List<T> listObjects;
    protected RecycleViewAdapter<T, ? extends RecyclerView.ViewHolder> adapter;
    protected Context ctx ;
    protected RecyclerView listViewObjects;
    protected Button btn1;
    protected Button btn2;
    protected Button btn3;
    protected Button btn4;
    protected Button btn5;
    protected ImageButton btnNext;
    protected ImageButton btnBack;
    protected List<Button> listButton = new LinkedList<>();

    public PagerController(RecycleViewAdapter<T, ? extends RecyclerView.ViewHolder> _adapter, int _objPerPage) {
        adapter = _adapter;
        fullListObjects = adapter.getItems();
        listObjects = adapter.getItems();
        objectPerPages = _objPerPage;
        recalculateNumberOfPages();
        Log.d(Util.APP_TAG, "Num of pages: " + numberOfPages);
    }

    protected void moveToPage(int _page) {
//        if(currentPage == _page)
//            return;

        int pageLeft = numberOfPages - _page;
        int location = numberOfPages < 5 ? numberOfPages - pageLeft : 5 - pageLeft;
        if(location < 1)
            location = 1;

        location = location - 1;
        Button currentButton = listButton.get(location);
        currentButton.setText(String.valueOf(_page));
        Button btnDef = new Button(ctx);
        for(int l = location - 1, p = _page - 1 ; l >= 0 ; l--, p--) {
            Button button = listButton.get(l);
            button.setText(String.valueOf(p));
            button.setTextColor(btnDef.getTextColors());
            button.setBackgroundResource(android.R.drawable.btn_default);
        }
        for(int l = location + 1, p = _page + 1 ; l < 5 && p <= numberOfPages ; l++, p++) {
            Button button = listButton.get(l);
            button.setText(String.valueOf(p));
            button.setTextColor(btnDef.getTextColors());
            button.setBackgroundResource(android.R.drawable.btn_default);
        }

        currentButton.setTextColor(Color.parseColor("#f6f7f7"));
        currentButton.setBackgroundColor(Color.parseColor("#0f7858"));

        int toIndex = (_page * objectPerPages) >= listObjects.size()  ? listObjects.size() : _page * objectPerPages;
        List<T> items = listObjects.subList((_page - 1) * objectPerPages, toIndex);
//        LinkedList<T> items = new LinkedList<T>(listObjects.subList((_page - 1) * objectPerPages, toIndex));
        Log.d(Util.APP_TAG, "CALL SET ITEMS");
        adapter.setItems(items);  // mesti dikasih linked list agar bisa serializable
        currentPage = _page;
//        adapter.notifyDataSetChanged();
    }

    protected void recalculateNumberOfPages() {
        numberOfPages = 0;
        if (listObjects != null && listObjects.size() > 0) {
            numberOfPages = (listObjects.size() / objectPerPages);
            if ((listObjects.size() % objectPerPages) > 0)
                numberOfPages += 1;
        }
    }

    protected class ButtonListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            Button b = (Button)v;
            int page = Integer.parseInt((String) b.getText());
            moveToPage(page);
        }
    }
}
