package com.techan.activities.drawer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class DrawerMenuListAdapter extends BaseAdapter {

    private final Context context;
    private final List<IDrawerMenuItem> items;

    public DrawerMenuListAdapter(Context context, List<IDrawerMenuItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return IDrawerMenuItem.DRAWER_MENU_ITEM_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemTypeId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IDrawerMenuItem item = items.get(position);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(item.getLayoutId(), null);
        }

        item.setText(convertView);

        return convertView;
    }
}
