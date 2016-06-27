package com.example.christina.carna_ui.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.christina.carna_ui.R;
import com.example.christina.carna_ui.database.AngelMemoDataSource;
import com.example.christina.carna_ui.database.AngelMemoUser;
import com.example.christina.carna_ui.enumclass.IntentValueType;

import java.util.List;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class UserSelectionActivity extends AppCompatActivity {

    Button addUserButton;
        private ArrayAdapter<String> listAdapter;
        AngelMemoDataSource source = null;
        NotificationManager mNotificationManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_select_user);
        source= new AngelMemoDataSource(this);
        source.open();


        updateUserListAdapter();
        ListView lv = (ListView) findViewById(R.id.userListView);
        lv.setAdapter(listAdapter);


        addUserButton = (Button)findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(UserSelectionActivity.this);
                View promptView = layoutInflater.inflate(R.layout.prompt_add_user, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserSelectionActivity.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addUser(editText.getText().toString());
                                updateUserListAdapter();
                                ListView lv = (ListView) findViewById(R.id.userListView);
                                lv.setAdapter(listAdapter);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                String username = listAdapter.getItem(position);
                AngelMemoUser user = source.getUserByName(username);
                Intent intent = new Intent(parent.getContext(), ScanActivity.class);
                intent.putExtra(IntentValueType.USER.toString(), user);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View item, int position, long id) {
                LayoutInflater layoutInflater = LayoutInflater.from(UserSelectionActivity.this);
                View promptView = layoutInflater.inflate(R.layout.prompt_delete_user, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserSelectionActivity.this);
                alertDialogBuilder.setView(promptView);

                final int itemPos = position;

                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String username = listAdapter.getItem(itemPos);
                                source.deleteUserByName(username);
                                updateUserListAdapter();
                                ListView lv = (ListView) findViewById(R.id.userListView);
                                lv.setAdapter(listAdapter);
                            }
                        })
                        .setNegativeButton("Abbrechen",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

                return true;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        //unscheduleUpdaters();
        //mBleDevice.disconnect();
    }


    private void addUser(String username){
        AngelMemoDataSource source = new AngelMemoDataSource(this);
        source.open();
        source.addUser(username);
    }

    private void updateUserListAdapter(){
        List<String> users = source.getAllUsernames();
        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, users);
    }
}
