package com.itime.team.itime.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.itime.team.itime.bean.Events;
import com.itime.team.itime.listener.RepeatSelectionListener;
import com.itime.team.itime.model.ParcelableCalendarType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leveyleonhardt on 4/29/16.
 */
public class NewEventCalendarTypeDialogFragment extends DialogFragment {
    private List<String> calendarTypes = new ArrayList<>();
    public static final String SELECTED = "selected";
    private RepeatSelectionListener listener;
    private String[] calendarArray;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Calendar");
        dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        for (ParcelableCalendarType calendarType : Events.calendarTypeList) {
            calendarTypes.add(calendarType.calendarName + "-" + calendarType.calendarOwnerName);
        }
        calendarArray = calendarTypes.toArray(new String[calendarTypes.size()]);
        Log.d("types", calendarArray.length + "");

        dialog.setSingleChoiceItems(calendarTypes.toArray(new String[calendarTypes.size()]), bundle.getInt(SELECTED), new DialogInterface.OnClickListener() {
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
