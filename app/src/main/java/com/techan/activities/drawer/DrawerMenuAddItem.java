package com.techan.activities.drawer;

import android.view.View;
import android.widget.TextView;

import com.techan.R;

public class DrawerMenuAddItem implements IDrawerMenuItem {
    private final String text;

    public DrawerMenuAddItem(String text) {
        this.text = text;
    }

    @Override
    public int getItemTypeId() {
        return IDrawerMenuItem.MENU_ADD_ITEM_TYPE_ID;
    }

    @Override
    public int getLayoutId() {
        return R.layout.drawer_menu_add_item_text;
    }

    @Override
    public void setText(View view) {
        if(text != null) {
            TextView textView = (TextView) view.findViewById(R.id.menuAddItemText);
            textView.setText(text);
        }
    }

    @Override
    public String getText() {
        return text;
    }
}
