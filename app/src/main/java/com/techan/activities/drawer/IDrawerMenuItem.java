package com.techan.activities.drawer;

import android.view.View;

public interface IDrawerMenuItem {
    public static final Integer MENU_ITEM_TYPE_ID = 0;
    public static final Integer MENU_SUB_ITEM_TYPE_ID = 1;
    public static final Integer MENU_ADD_ITEM_TYPE_ID = 2;

    public static final Integer DRAWER_MENU_ITEM_TYPE_COUNT = 3;

    int getItemTypeId();
    int getLayoutId();
    void setText(View view);
    String getText();
}
