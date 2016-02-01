package com.itime.team.itime.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.widget.ListView;

/**
 * Created by mac on 16/2/1.
 */
public class SearchFriendActivity extends Activity {
    private SearchView mSearch;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend);
        init();
    }

    private void init(){
        mSearch = (SearchView) findViewById(R.id.meeting_search_search);
        mListView = (ListView) findViewById(R.id.meeting_search_friendbyid);
    }


}
