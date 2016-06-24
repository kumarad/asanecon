package com.techan.activities.drawer;

import android.view.View;

public interface IDrawerMenuItem {
    Integer MENU_ITEM_TYPE_ID = 0;
    Integer MENU_SUB_ITEM_TYPE_ID = 1;
    Integer MENU_ADD_ITEM_TYPE_ID = 2;
    Integer MENU_GOLD_ITEM_TYPE_ID = 3;
    Integer MENU_HEADER_ITEM_TYPE_ID = 4;

    Integer DRAWER_MENU_ITEM_TYPE_COUNT = 5;

    int getItemTypeId();
    int getLayoutId();
    void setText(View view);
    String getText();
}
