package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.CrackableReward;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.CrackableExplode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCrackable extends HabboItem {

    public InteractionCrackable(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionCrackable(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.getExtradata().length() == 0) {
            this.setExtradata("0");
        }

        serverMessage.appendInt32(7 + (this.isLimited() ? 256 : 0));

        serverMessage.appendString(Emulator.getGameEnvironment().getItemManager().calculateCrackState(Integer.valueOf(this.getExtradata()), Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId())) + "");
        serverMessage.appendInt32(Integer.valueOf(this.getExtradata()));
        serverMessage.appendInt32(Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId()));

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);

        if (client == null) {
            return;
        }

        if (this.getExtradata().length() == 0) {
            this.setExtradata("0");
        }

        this.setExtradata(Integer.valueOf(this.getExtradata()) + 1 + "");
        this.needsUpdate(true);
        room.updateItem(this);

        CrackableReward rewardData = Emulator.getGameEnvironment().getItemManager().getCrackableData(this.getBaseItem().getId());

        if (rewardData != null && !rewardData.achievementTick.isEmpty()) {
            AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get(rewardData.achievementTick));
        }
        if (Integer.valueOf(this.getExtradata()) >= Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId())) {
            Emulator.getThreading().run(new CrackableExplode(room, this), 1500);

            if (rewardData != null && !rewardData.achievementCracked.isEmpty()) {
                AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get(rewardData.achievementCracked));
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {

    }

    @Override
    public void onWalkOn(RoomUnit client, Room room, Object[] objects) throws Exception {

    }

    @Override
    public void onWalkOff(RoomUnit client, Room room, Object[] objects) throws Exception {

    }
}
