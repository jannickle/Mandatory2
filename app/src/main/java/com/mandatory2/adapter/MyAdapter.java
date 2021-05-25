package com.mandatory2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mandatory2.R;
import com.mandatory2.model.Snapinfo;
import com.mandatory2.model.Title;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<Snapinfo> items;
    private LayoutInflater layoutInflater;

    public MyAdapter(List<Snapinfo> items, Context context) {
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            view = layoutInflater.inflate(R.layout.snapimage, null);
        }
        TextView textView = view.findViewById(R.id.textView1);
        ImageView imageView = view.findViewById(R.id.image_view);
        if(textView != null) {
            textView.setText(items.get(i).getImageName()); // later I will connect to the items list
        }
        return textView;
    }
}
