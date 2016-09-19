package com.arktech.waqasansari.mindpalace;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class ActivityHome extends AppCompatActivity {
    List<ClassMemoryDetail> memoryList;
    DatabaseHandler dbHandler;
    ListView lstDetails;

    private TextView view;
    boolean isDisplayed = false;

    public TextView getView() {
        return view;
    }

    public void setView() {
        isDisplayed = true;
        view = new TextView(getApplicationContext());
        view.setText("No entries\nTap to add the first one");
        view.setTextSize(17.0f);
        view.setTextColor(Color.BLACK);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setPadding(40, 40, 40, 40);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityHome.this, ActivityAddMemory.class));
            }
        });
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHandler = new DatabaseHandler(this);

        setView();


        memoryList = dbHandler.getAllClassDetailMemory();

        FloatingActionButton btnAddMemory = (FloatingActionButton) findViewById(R.id.btnAddMemory);
        if (btnAddMemory != null) {
            btnAddMemory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivityHome.this, ActivityAddMemory.class));
                }
            });
        }

        lstDetails = (ListView) findViewById(R.id.lstDetail);
        if (lstDetails != null) {
            lstDetails.setFastScrollEnabled(true);
        }

        if(memoryList.size() < 1)
            lstDetails.addHeaderView(getView());

        CustomAdapter customAdapter = new CustomAdapter(ActivityHome.this, memoryList);
        lstDetails.setAdapter(customAdapter);


        lstDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ActivityMemoryDetail.class);
                ClassMemoryDetail temp = (ClassMemoryDetail) lstDetails.getItemAtPosition(position);
                intent.putExtra("memoryObject", temp);
                startActivity(intent);
            }
        });

        lstDetails.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ClassMemoryDetail temp = (ClassMemoryDetail) lstDetails.getItemAtPosition(position);
                final CharSequence[] items = { "Edit", "Delete", "Send via SMS", "Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityHome.this);
                builder.setTitle("Select option");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Edit")) {
                            Intent editIntent = new Intent(ActivityHome.this, ActivityAddMemory.class);
                            editIntent.putExtra("memoryObject", temp);
                            startActivity(editIntent);
                        } else if (items[item].equals("Delete")) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityHome.this);
                            alertDialog.setTitle("Delete memory")
                                    .setMessage("Do you want to delete this memory?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dbHandler.deleteMemory(temp);
                                            memoryList = dbHandler.getAllClassDetailMemory();
                                            CustomAdapter adapter = new CustomAdapter(ActivityHome.this, memoryList);
                                            lstDetails.setAdapter(adapter);
                                            if(dbHandler.getMemoryCount() == 0){
                                                lstDetails.addHeaderView(getView());
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();

                        } else if (items[item].equals("Send via SMS")) {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setData(Uri.parse("sms:"));
                            String smsText = "Type: " + temp.getType() + "\n" +
                                    "Title: " + temp.getTitle() + "\n" +
                                    "Description: " + temp.getDescription() + "\n" +
                                    "Tags: " + temp.getTags() + "\n" +
                                    "Date: " + Utility.getFormattedDate(temp.getDated());
                            smsIntent.putExtra("sms_body", smsText);
                            startActivity(smsIntent);
                        } else if (items[item].equals("Cancel"))
                            dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

    }


    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            memoryList = dbHandler.searchEverywhere(newText);

            CustomAdapter customAdapter = new CustomAdapter(ActivityHome.this, memoryList);
            lstDetails.setAdapter(customAdapter);
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setListView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(listener);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                memoryList = dbHandler.getAllClassDetailMemory();

                CustomAdapter customAdapter = new CustomAdapter(ActivityHome.this, memoryList);
                lstDetails.setAdapter(customAdapter);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setListView(){
        memoryList = dbHandler.getAllClassDetailMemory();

        if(memoryList.size() > 0)
            if(isDisplayed)
                lstDetails.removeHeaderView(getView());

        CustomAdapter adapter = new CustomAdapter(ActivityHome.this, memoryList);
        lstDetails.setAdapter(adapter);
    }
}
