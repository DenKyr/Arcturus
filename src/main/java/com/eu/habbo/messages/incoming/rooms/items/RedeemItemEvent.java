package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureRedeemedEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;
import com.eu.habbo.util.pathfinding.Tile;

public class RedeemItemEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null) {
            HabboItem item = room.getHabboItem(itemId);

            if (item != null) {
                boolean furnitureRedeemEventRegistered = Emulator.getPluginManager().isRegistered(FurnitureRedeemedEvent.class, true);

                if (item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_") || item.getBaseItem().getName().startsWith("DF_") || item.getBaseItem().getName().startsWith("PF_")) {
                    if (item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_")) {
                        int credits;
                        try {
                            credits = Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
                        } catch (Exception e) {
                            return;
                        }

                        if (furnitureRedeemEventRegistered) {
                            Event furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), credits, FurnitureRedeemedEvent.CREDITS);
                            Emulator.getPluginManager().fireEvent(furniRedeemEvent);

                            if (furniRedeemEvent.isCancelled()) {
                                return;
                            }
                        }

                        this.client.getHabbo().getHabboInfo().addCredits(credits);
                        this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));

                    } else if (item.getBaseItem().getName().startsWith("PF_")) {
                        int pixels;

                        try {
                            pixels = Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
                        } catch (Exception e) {
                            return;
                        }

                        if (furnitureRedeemEventRegistered) {
                            Event furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), pixels, FurnitureRedeemedEvent.PIXELS);
                            Emulator.getPluginManager().fireEvent(furniRedeemEvent);

                            if (furniRedeemEvent.isCancelled()) {
                                return;
                            }
                        }

                        this.client.getHabbo().getHabboInfo().addPixels(pixels);
                        this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
                    } else if (item.getBaseItem().getName().startsWith("PF_")) {
                        int pointsType;
                        int points;

                        try {
                            pointsType = Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
                        } catch (Exception e) {
                            return;
                        }

                        try {
                            points = Integer.valueOf(item.getBaseItem().getName().split("_")[2]);
                        } catch (Exception e) {
                            return;
                        }

                        if (furnitureRedeemEventRegistered) {
                            Event furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), points, FurnitureRedeemedEvent.DIAMONDS);
                            Emulator.getPluginManager().fireEvent(furniRedeemEvent);

                            if (furniRedeemEvent.isCancelled()) {
                                return;
                            }
                        }

                        this.client.getHabbo().getHabboInfo().addCurrencyAmount(pointsType, points);
                        this.client.sendResponse(new UserPointsComposer(this.client.getHabbo().getHabboInfo().getCurrencyAmount(pointsType), points, pointsType));
                    }

                    room.removeHabboItem(item);
                    room.sendComposer(new RemoveFloorItemComposer(item).compose());
                    Tile t = new Tile(item.getX(), item.getY(), room.getStackHeight(item.getX(), item.getY(), true));
                    room.updateTile(t);
                    room.sendComposer(new UpdateStackHeightComposer(item.getX(), item.getY(), t.Z).compose());
                    Emulator.getThreading().run(new QueryDeleteHabboItem(item));
                }
            }
        }
    }
}
