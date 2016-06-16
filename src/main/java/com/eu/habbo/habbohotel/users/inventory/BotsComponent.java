package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class BotsComponent {

    private final THashMap<Integer, Bot> userBots = new THashMap<Integer, Bot>();

    public BotsComponent(Habbo habbo) {
        this.loadBots(habbo);
    }

    private void loadBots(Habbo habbo) {
        synchronized (this.userBots) {
            try {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON users.id = bots.user_id WHERE user_id = ? AND room_id = 0 ORDER BY id ASC");
                statement.setInt(1, habbo.getHabboInfo().getId());
                ResultSet set = statement.executeQuery();
                while (set.next()) {
                    Bot bot = Emulator.getGameEnvironment().getBotManager().loadBot(set);
                    if (bot != null) {
                        //bot = new Bot(set);
                        bot.setOwnerName(habbo.getHabboInfo().getUsername());
                        this.userBots.put(set.getInt("id"), bot);
                    }
                }
                set.close();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public Bot getBot(int botId) {
        return this.userBots.get(botId);
    }

    public void addBot(Bot bot) {
        synchronized (this.userBots) {
            this.userBots.put(bot.getId(), bot);
        }
    }

    public void removeBot(Bot bot) {
        synchronized (this.userBots) {
            this.userBots.remove(bot.getId());
        }
    }

    public THashMap<Integer, Bot> getUserBots() {
        return this.userBots;
    }

    public void dispose() {
        synchronized (this.userBots) {
            for (Map.Entry<Integer, Bot> map : this.userBots.entrySet()) {
                if (map.getValue().needsUpdate()) {
                    Emulator.getThreading().run(map.getValue());
                }
            }
            this.userBots.clear();
        }
    }
}
