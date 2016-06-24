package com.techan.activities.drawer;

import android.view.View;

import com.techan.R;

public class DrawerHeaderItem implements IDrawerMenuItem {

    public DrawerHeaderItem() {}

    @Override
    public int getItemTypeId() {
        return IDrawerMenuItem.MENU_HEADER_ITEM_TYPE_ID;
    }

    @Override
    public int getLayoutId() {
        return R.layout.drawer_menu_header_item;
    }

    @Override
    public void setText(View view) {}

    @Override
    public String getText() { return ""; }
}
