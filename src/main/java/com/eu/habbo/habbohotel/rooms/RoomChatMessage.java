package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class RoomChatMessage implements Runnable, ISerialize {

    private String message;
    private RoomChatMessageBubbles bubbleId;
    private final Habbo habbo;
    private Habbo targetHabbo;
    private final RoomUnit roomUnit;
    private byte emotion;

    public RoomChatMessage(MessageHandler message) {
        if (message.packet.getMessageId() == Incoming.RoomUserWhisperEvent) {
            String data = message.packet.readString();
            this.targetHabbo = message.client.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(data.split(" ")[0]);
            this.message = data.substring(data.split(" ")[0].length() + 1, data.length());
        } else {
            this.message = message.packet.readString();
        }
        try {
            this.bubbleId = RoomChatMessageBubbles.getBubble(message.packet.readInt());
        } catch (Exception e) {
            this.bubbleId = RoomChatMessageBubbles.NORMAL;
        }

        this.habbo = message.client.getHabbo();
        this.roomUnit = habbo.getRoomUnit();

        this.checkEmotion();

        if (Emulator.getConfig().getBoolean("save.room.chats", false)) {
            Emulator.getThreading().run(this);
        }

        this.filter();
    }

    public RoomChatMessage(RoomChatMessage chatMessage) {
        this.message = chatMessage.getMessage();
        this.habbo = chatMessage.getHabbo();
        this.targetHabbo = chatMessage.getTargetHabbo();
        this.bubbleId = chatMessage.getBubble();
        this.roomUnit = chatMessage.roomUnit;
        this.emotion = (byte) chatMessage.getEmotion();
    }

    public RoomChatMessage(String message, RoomUnit roomUnit, RoomChatMessageBubbles bubbleId) {
        this.message = message;
        this.habbo = null;
        this.bubbleId = bubbleId;
        this.roomUnit = roomUnit;
    }

    public RoomChatMessage(String message, Habbo habbo, RoomChatMessageBubbles bubbleId) {
        this.message = message;
        this.habbo = habbo;
        this.bubbleId = bubbleId;
        this.checkEmotion();
        this.roomUnit = habbo.getRoomUnit();
        this.message = this.message.replace("\r", "").replace("\n", "");

        if (this.getHabbo().getHabboStats().chatColor != RoomChatMessageBubbles.NORMAL) {
            this.bubbleId = this.getHabbo().getHabboStats().chatColor;
        }
    }

    public RoomChatMessage(String message, Habbo habbo, Habbo targetHabbo, RoomChatMessageBubbles bubbleId) {
        this.message = message;
        this.habbo = habbo;
        this.targetHabbo = targetHabbo;
        this.bubbleId = bubbleId;
        this.checkEmotion();
        this.roomUnit = this.habbo.getRoomUnit();
        this.message = this.message.replace("\r", "").replace("\n", "");

        if (this.getHabbo().getHabboStats().chatColor != RoomChatMessageBubbles.NORMAL) {
            this.bubbleId = this.getHabbo().getHabboStats().chatColor;
        }
    }

    private void checkEmotion() {
        if (this.message.contains(":)") || this.message.contains(":-)") || this.message.contains(":]")) {
            this.emotion = 1;
        } else if (this.message.contains(":@") || this.message.contains(">:(")) {
            this.emotion = 2;
        } else if (this.message.contains(":o") || this.message.contains(":O") || this.message.contains(":0") || this.message.contains("O.o") || this.message.contains("o.O") || this.message.contains("O.O")) {
            this.emotion = 3;
        } else if (this.message.contains(":(") || this.message.contains(":-(") || this.message.contains(":[")) {
            this.emotion = 4;
        }
    }

    @Override
    public synchronized void run() {
        if (habbo == null) {
            return;
        }

        if (Emulator.getConfig().getBoolean("save.room.chats", false)) {
            if (this.message.length() > 255) {
                try {
                    this.message = this.message.substring(0, 254);
                } catch (Exception e) {
                    Emulator.getLogging().logErrorLine(e);
                }
            }

            try {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO chatlogs_room (user_from_id, user_to_id, message, timestamp, room_id) VALUES (?, ?, ?, ?, ?)");
                statement.setInt(1, this.habbo.getHabboInfo().getId());
                if (this.targetHabbo != null) {
                    statement.setInt(2, this.targetHabbo.getHabboInfo().getId());
                } else {
                    statement.setInt(2, 0);
                }
                statement.setString(3, this.message);
                statement.setInt(4, Emulator.getIntUnixTimestamp());
                if (habbo.getHabboInfo().getCurrentRoom() != null) {
                    statement.setInt(5, habbo.getHabboInfo().getCurrentRoom().getId());
                } else {
                    statement.setInt(6, 0);
                }
                statement.execute();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RoomChatMessageBubbles getBubble() {
        return this.bubbleId;
    }

    public Habbo getHabbo() {
        return this.habbo;
    }

    public Habbo getTargetHabbo() {
        return this.targetHabbo;
    }

    public int getEmotion() {
        return this.emotion;
    }

    @Override
    public void serialize(ServerMessage message) {
        if (this.habbo != null) {
            if (!this.habbo.hasPermission("acc_anychatcolor")) {
                for (String s : Emulator.getConfig().getValue("commands.cmd_chatcolor.banned_numbers").split(";")) {
                    if (Integer.valueOf(s) == this.bubbleId.getType()) {
                        this.bubbleId = RoomChatMessageBubbles.NORMAL;
                        break;
                    }
                }
            }
        }

        try {
            message.appendInt32(this.roomUnit.getId());
            message.appendString(this.getMessage());
            message.appendInt32(this.getEmotion());
            message.appendInt32(this.getBubble().getType());
            message.appendInt32(0);
            message.appendInt32(this.getMessage().length());
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public void filter() {
        if (Emulator.getConfig().getBoolean("hotel.wordfilter.enabled") && Emulator.getConfig().getBoolean("hotel.wordfilter.rooms")) {
            if (!habbo.hasPermission("acc_chat_no_filter")) {
                if (!Emulator.getGameEnvironment().getWordFilter().autoReportCheck(this)) {
                    if (!Emulator.getGameEnvironment().getWordFilter().hideMessageCheck(this.message)) {
                        this.message = Emulator.getGameEnvironment().getWordFilter().filter(this.message, this.habbo);
                        return;
                    }
                }

                this.message = "";
            }
        }
    }
}
