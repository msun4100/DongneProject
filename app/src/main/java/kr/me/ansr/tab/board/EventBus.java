package kr.me.ansr.tab.board;

import com.squareup.otto.Bus;

/**
 * Created by KMS on 2016-07-29.
 */

public final class EventBus {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private EventBus() {
        // No instances.
    }
}