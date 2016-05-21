package com.itime.team.itime.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.itime.team.itime.R;
import com.itime.team.itime.listener.RepeatSelectionListener;

/**
 * Created by leveyleonhardt on 4/28/16.
 */
public class NewEventAlertDialogFragment extends DialogFragment {
    static public String[] alertArray;
    private RepeatSelectionListener listener;
    public static final String SELECTED = "selected";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        alertArray = getActivity().getResources().getStringArray(R.array.entry_default_alert_time);
        Bundle bundle = getArguments();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Alert Time");
        dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setSingleChoiceItems(alertArray, bundle.getInt(SELECTED), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.selectItem(which);
                }
                dialog.dismiss();
            }
        });
        return dialog.create();
    }


    public void setListener(RepeatSelectionListener listener) {
        this.listener = listener;
    }
}
