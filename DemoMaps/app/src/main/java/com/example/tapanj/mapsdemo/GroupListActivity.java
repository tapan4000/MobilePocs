package com.example.tapanj.mapsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager groupsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeDisplayContent();
    }

    private void initializeDisplayContent() {
        RecyclerView groupList = (RecyclerView) findViewById(R.id.list_groups);
        List<Group> groups = DataManager.getInstance().getGroups();
        ArrayAdapter<Group> adapterGroups = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groups);

        groupList.setHasFixedSize(true);

        if(groupsLayoutManager == null){
            groupsLayoutManager = new LinearLayoutManager(this);
        }

        groupList.setLayoutManager(groupsLayoutManager);

        // Specify an adapter
        GenericRecyclerViewAdapter groupListRecyclerViewAdapter = new GenericRecyclerViewAdapter(groups.toArray(), new GenericRecyclerViewAdapter.OnRecyclerItemClickListener<Group>() {
            @Override
            public void onItemClick(Group itemData) {
                Intent groupClickIntent = new Intent(GroupListActivity.this, GroupActivity.class);
                startActivity(groupClickIntent);
            }
        });
        groupList.setAdapter(groupListRecyclerViewAdapter);
    }
}
