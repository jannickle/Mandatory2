package com.mandatory2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mandatory2.adapter.MyAdapter;
import com.mandatory2.model.Snapinfo;
import com.mandatory2.repo.Repos;

import java.util.ArrayList;
import java.util.List;

public class SnapsActivity extends AppCompatActivity implements Updatable {
    List<Snapinfo> items = new ArrayList<>();
    ListView listView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        setupListView();
        Repos.r().setup(this, items);
    }

    private void setupListView() {
        listView = findViewById(R.id.listView1);
        myAdapter = new MyAdapter(items, this);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            System.out.println("click on row: " + position);
            Intent intent = new Intent(SnapsActivity.this, SnapDetailsActivity.class);
            intent.putExtra("snapid", items.get(position).getId());
            intent.putExtra("url", items.get(position).getImageURL());

            startActivity(intent);
        });
    }

    @Override
    public void update(Object o) {
        myAdapter.notifyDataSetChanged();
    }
}
