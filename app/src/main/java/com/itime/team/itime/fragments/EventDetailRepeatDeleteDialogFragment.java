package com.itime.team.itime.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by leveyleonhardt on 5/1/16.
 */
public class EventDetailRepeatDeleteDialogFragment extends DialogFragment {
    private String title = "Delete Event";
    private String thisEvent = "Delete for this event only";
    private String futureEvent = "Delete for the future events";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.create();
        dialog.setTitle(title);
        dialog.setMessage("What do you want");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, thisEvent, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ((EventDetailFragment) getTargetFragment()).deleteSingleEventInRepeat();
                } catch (Exception e) {
                    ((EventDetailEditFragment) getTargetFragment()).deleteSingleEventInRepeat();

                }

            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, futureEvent, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ((EventDetailFragment) getTargetFragment()).deleteFutureEvents();
                } catch (Exception e) {
                    ((EventDetailEditFragment) getTargetFragment()).deleteFutureEvents();
                }
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        return dialog;
    }

    public void setDialogTitle(String title) {
        this.title = title;
    }

    public void setThisEvent(String thisEvent) {
        this.thisEvent = thisEvent;
    }

    public void setFutureEvent(String futureEvent) {
        this.futureEvent = futureEvent;
    }
}
