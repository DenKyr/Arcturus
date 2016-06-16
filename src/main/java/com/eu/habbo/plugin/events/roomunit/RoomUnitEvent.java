package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.Event;

public abstract class RoomUnitEvent extends Event {

    /**
     * The RoomUnit this event applies to.
     */
    public final RoomUnit roomUnit;

    /**
     * @param roomUnit The RoomUnit this event applies to.
     */
    public RoomUnitEvent(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
    }
}
