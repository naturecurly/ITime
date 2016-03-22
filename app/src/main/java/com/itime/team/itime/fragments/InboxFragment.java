/*
 * Copyright (C) 2016 Yorkfine Chan <yorkfinechan@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.R;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class InboxFragment extends Fragment {

    private static final String LOG_TAG = InboxFragment.class.getSimpleName();

    private static final int UNREAD = 0;
    private static final int ALL = 1;

    /* Status: UNREAD or ALL */
    private int mStatus = UNREAD;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_inbox, null);
        setHasOptionsMenu(true);
        //setTitle();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.inbox_action) {
            mStatus = (mStatus+1)%2;
            setTitle();
            setItemTitle(item);
            // TODO: 22/03/16 Reload the list view

        }
        return super.onOptionsItemSelected(item);
    }

    private void setItemTitle(MenuItem item) {
        switch (mStatus) {
            case UNREAD:
                item.setTitle("All");
                break;

            case ALL:
                item.setTitle("UnRead");
                break;
        }
    }

    public void setTitle() {
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        switch (mStatus) {
            case UNREAD:
                title.setText("UnRead");
                break;

            case ALL:
                title.setText("All");
                break;
        }

    }
}
