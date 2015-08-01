package com.techan.activities.drawer;

import android.view.View;
import android.widget.TextView;

import com.techan.R;
import com.techan.custom.Util;
import com.techan.memrepo.GoldRepo;

public class DrawerMenuGoldItem implements IDrawerMenuItem {
    public DrawerMenuGoldItem() {}

    @Override
    public int getItemTypeId() {
        return IDrawerMenuItem.MENU_GOLD_ITEM_TYPE_ID;
    }

    @Override
    public int getLayoutId() {
        return R.layout.drawer_menu_gold_item_text;
    }

    @Override
    public void setText(View view) {
        Double price = GoldRepo.get().getLatestPrice();

        TextView textView = (TextView) view.findViewById(R.id.menuGoldItemPriceText);
        if(price == null) {
            textView.setText("-");
        } else {
            textView.setText(Double.toString(Util.roundTwoDecimals(price)));
        }
    }

    @Override
    public String getText() {
        return null;
    }

}
