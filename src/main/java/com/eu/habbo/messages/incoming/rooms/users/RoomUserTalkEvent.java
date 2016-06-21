package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RoomUserTalkEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return;
        }

        if (!this.client.getHabbo().getRoomUnit().canTalk()) {
            return;
        }

        this.client.getHabbo().getHabboInfo().getCurrentRoom().talk(this.client.getHabbo(), new RoomChatMessage(this), RoomChatType.TALK);

    }
}
