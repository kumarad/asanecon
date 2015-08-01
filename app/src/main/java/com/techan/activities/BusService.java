package com.techan.activities;

import com.squareup.otto.Bus;

public class BusService {
    private static Bus bus = null;

    public static Bus getInstance() {
        if(bus == null) {
            bus = new Bus();
        }

        return bus;
    }
}
