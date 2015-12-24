package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.NumberPicker;

import com.itime.team.itime.activities.R;


/**
 * Created by mac on 15/12/18.
 */
public class MeetingDurationDialogFragment extends DialogFragment implements NumberPicker.OnValueChangeListener,AbsListView.OnScrollListener,NumberPicker.Formatter {
    private View durationDialog;
    private NumberPicker hourPicker;
    private NumberPicker minPicker;
    private Button save;
    private Button cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        durationDialog = inflater.inflate(R.layout.fragment_meeting_duration_dialog,null);
        hourPicker = (NumberPicker) durationDialog.findViewById(R.id.meeting_hourpicker);
        minPicker = (NumberPicker) durationDialog.findViewById(R.id.meeting_minuteicker);
        save = (Button) durationDialog.findViewById(R.id.meeting_duration_dialog_save);
        cancel = (Button) durationDialog.findViewById(R.id.meeting_duration_dialog_cancel);
        init();
        return durationDialog;
    }

    private void init() {
        hourPicker.setFormatter(this);
        hourPicker.setOnValueChangedListener(this);
        hourPicker.setMaxValue(24);
        hourPicker.setMinValue(0);
        hourPicker.setValue(9);

        minPicker.setFormatter(this);
        minPicker.setOnValueChangedListener(this);
        minPicker.setMaxValue(59);
        minPicker.setMinValue(0);
        minPicker.setValue(49);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Duration",hourPicker.getValue() + ":" + minPicker.getValue());
                MeetingDurationDialogFragment.this.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingDurationDialogFragment.this.dismiss();
            }
        });
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//        Toast.makeText(
//                getActivity(),
//                "原来的值 " + oldVal + "--新值: "
//                        + newVal, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
