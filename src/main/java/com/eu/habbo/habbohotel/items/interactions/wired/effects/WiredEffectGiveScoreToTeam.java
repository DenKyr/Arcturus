package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.TIntIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveScoreToTeam extends InteractionWiredEffect {

    public static final WiredEffectType type = WiredEffectType.GIVE_SCORE_TEAM;

    private int points;
    private int count;
    private GameTeamColors teamColor = GameTeamColors.RED;

    private final TIntIntHashMap startTimes = new TIntIntHashMap();

    public WiredEffectGiveScoreToTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public WiredEffectGiveScoreToTeam(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null) {
            Class<? extends Game> game = habbo.getHabboInfo().getCurrentGame();

            if (game != null) {
                Game g = room.getGame(game);

                if (g != null) {
                    int c = this.startTimes.get(g.getStartTime());

                    if (c < this.count) {
                        GameTeam team = g.getTeam(this.teamColor);

                        if (team != null) {
                            team.addTeamScore(this.points);

                            this.startTimes.put(g.getStartTime(), c++);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String getWiredData() {
        return this.points + ";" + this.count + ";" + this.teamColor.type;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] data = set.getString("wired_data").split(":");

        if (data.length == 3) {
            this.points = Integer.valueOf(data[0]);
            this.count = Integer.valueOf(data[0]);
            this.teamColor = GameTeamColors.values()[Integer.valueOf(data[0])];
        }
    }

    @Override
    public void onPickUp() {
        this.startTimes.clear();
        this.points = 0;
        this.count = 0;
        this.teamColor = GameTeamColors.RED;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message) {
        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(3);
        message.appendInt32(this.points);
        message.appendInt32(this.count);
        message.appendInt32(this.teamColor.type + 1);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet) {
        packet.readInt();

        this.points = packet.readInt();
        this.count = packet.readInt();
        this.teamColor = GameTeamColors.values()[packet.readInt() - 1];

        return true;
    }
}
