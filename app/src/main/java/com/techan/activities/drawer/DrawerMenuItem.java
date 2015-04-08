package com.techan.activities.drawer;

import android.view.View;
import android.widget.TextView;

import com.techan.R;

public class DrawerMenuItem implements IDrawerMenuItem {
    private final String text;

    public DrawerMenuItem(String text) {
        this.text = text;
    }

    @Override
    public int getItemTypeId() {
        return IDrawerMenuItem.MENU_ITEM_TYPE_ID;
    }

    @Override
    public int getLayoutId() {
        return R.layout.drawer_menu_item_text;
    }

    @Override
    public void setText(View view) {
        if(text != null) {
            TextView textView = (TextView) view.findViewById(R.id.menuItemText);
            textView.setText(text);
        }
    }

    @Override
    public String getText() {
        return text;
    }
}
