package com.itime.team.itime.activities;

import android.app.ProgressDialog;
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
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if(cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while(cursor.moveToNext()) {
            Contact contact = new Contact();
            ArrayList<String> photoNumers = new ArrayList<>();

            /*
             * Set Name
             */
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            contact.setName(name);

            /*
             * Search phone information of contacts
             */
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if(phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while(phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                photoNumers.add(phoneNumber);
            }
            contact.setPhotoNumber(photoNumers);

            /*
             * set E-mail inforamtion
             */
            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId,
                    null, null);
            int emailIndex = 0;
            if(emails.getCount() > 0) {
                emailIndex = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            }
            while(emails.moveToNext()) {
                String email = emails.getString(emailIndex);
                contact.seteMail(email);
            }
            mContact.add(contact);
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
