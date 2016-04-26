package com.itime.team.itime.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itime.team.itime.listener.RepeatSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leveyleonhardt on 4/25/16.
 */
public class NewEventRepeatDialogFragment extends DialogFragment {
    final private String[] repeatArray = {"One-time event", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
    private RepeatSelectionListener listener;
    public static final String SELECTED = "selected";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Repeats");
        dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setSingleChoiceItems(repeatArray, bundle.getInt(SELECTED), new DialogInterface.OnClickListener() {
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
