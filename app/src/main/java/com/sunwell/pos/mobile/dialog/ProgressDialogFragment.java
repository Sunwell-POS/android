package com.sunwell.pos.mobile.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by sunwell on 11/22/17.
 */

public class ProgressDialogFragment extends AppCompatDialogFragment
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog _dialog = new ProgressDialog(getActivity());
        this.setStyle(STYLE_NO_TITLE, getTheme()); // You can use styles or inflate a view
        _dialog.setMessage("Loading.."); // set your messages if not inflated from XML
//        _dialog.setCancelable(false);
        return _dialog;
    }

}
