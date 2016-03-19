package com.itime.team.itime.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.Contact;
import com.itime.team.itime.fragments.PhoneContactAddWayDialogFragment;
import com.itime.team.itime.views.adapters.PhotoContactsAdapter;

import java.util.ArrayList;

/**
 * Created by mac on 16/2/6.
 */
public class PhoneContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ArrayList<Contact> mContact;
    private ListView mListView;
    private Boolean[] mIsChecked;
    private PhotoContactsAdapter mListViewAdapter;
    private ProgressDialog dialog;

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact);
        mListView = (ListView) findViewById(R.id.phone_contact_listview);
        init();
        new LoadingThread(this).start();
    }

    private void init(){
        mContact = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.phone_contact_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.phone_contact_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mListView.setOnItemClickListener(this);
        dialog = ProgressDialog.show(this, null, "Loading", true, false);
    }

    public void setContacts() {
        ContentResolver resolver = this.getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                Contact contact = new Contact();
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                contact.setName(contactName);
                ArrayList<String> phoneNum = new ArrayList<>();
                phoneNum.add(phoneNumber);
                contact.setPhotoNumber(phoneNum);
                mContact.add(contact);
            }
            phoneCursor.close();
        }
        initCheckBox();
    }

    private void setListView(){
        mListViewAdapter = new PhotoContactsAdapter(this,mContact, mIsChecked);
        mListView.setAdapter(mListViewAdapter);
    }

    private void initCheckBox(){
        mIsChecked = new Boolean[mContact.size()];
        for(int i = 0; i < mIsChecked.length; i ++){
            mIsChecked[i] = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.phone_contact_seach, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.phone_search_add) {
            PhoneContactAddWayDialogFragment dialogFragment = new PhoneContactAddWayDialogFragment(mIsChecked,mContact);
            dialogFragment.show(getSupportFragmentManager(),"");
            return true;
        } else if (id == R.id.phone_search_all){
            selectAll();
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectAll(){
        mListViewAdapter.invert();
        mListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.phone_contact_check);
        checkBox.toggle();
    }


    private class LoadingThread extends Thread {

        private PhoneContactActivity activity;

        public LoadingThread(PhoneContactActivity act) {
            activity = act;
        }
        public void run() {
            setContacts();
            activity.mHandler.sendEmptyMessage(0);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            setListView();
            if (dialog.isShowing())
                dialog.dismiss();
        }
    };


}
