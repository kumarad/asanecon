package com.techan.activities.drawer;

import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.techan.R;
import com.techan.custom.Util;
import com.techan.database.CursorUtil;
import com.techan.database.StocksTable;

public class DrawerSubMenuItem implements IDrawerMenuItem {
    private final String text;

    public DrawerSubMenuItem(String text) {
        this.text = text;
    }

    @Override
    public int getItemTypeId() {
        return IDrawerMenuItem.MENU_SUB_ITEM_TYPE_ID;
    }

    @Override
    public int getLayoutId() {
        return R.layout.drawer_menu_sub_item_text;
    }

    @Override
    public void setText(View view) {
        if(text != null) {
            String[] projection = {StocksTable.COLUMN_PRICE, StocksTable.COLUMN_CHANGE};
            Cursor cursor = CursorUtil.getCursor(view.getContext(), text, projection);
            cursor.moveToFirst();
            double overallChange = 0;
            while(!cursor.isAfterLast()) {
                double changePercent = cursor.getDouble(1) * 100/cursor.getDouble(0);
                overallChange += changePercent;
                cursor.moveToNext();
            }

            TextView textView = (TextView) view.findViewById(R.id.menuSubItemText);
            textView.setText(text);

            TextView changeView = (TextView) view.findViewById(R.id.menuSubItemSubText);
            changeView.setText(Double.toString(Util.roundTwoDecimals(overallChange)) + "%");

            if(overallChange > 0) {
                changeView.setTextColor(changeView.getResources().getColor(R.color.asaneconGreen));
                changeView.setVisibility(View.VISIBLE);
            } else if(overallChange < 0) {
                changeView.setTextColor(changeView.getResources().getColor(R.color.asaneconRed));
                changeView.setVisibility(View.VISIBLE);
            } else {
                changeView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public String getText() {
        return text;
    }
}
