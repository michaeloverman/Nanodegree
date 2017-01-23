package com.app.movie.cinephilia.DataBus;

import com.squareup.otto.Bus;

/**
 * Created by GAURAV on 23-01-2016.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}