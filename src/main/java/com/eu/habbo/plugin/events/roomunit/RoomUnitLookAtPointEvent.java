package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.util.pathfinding.Tile;

public class RoomUnitLookAtPointEvent extends RoomUnitEvent {

    /**
     * The tile to look at.
     */
    public final Tile location;

    /**
     * @param roomUnit The RoomUnit this event applies to.
     * @param location The tile to look at.
     */
    public RoomUnitLookAtPointEvent(RoomUnit roomUnit, Tile location) {
        super(roomUnit);

        this.location = location;
    }
}
