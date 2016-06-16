package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BadgesComponent {

    private final THashSet<HabboBadge> badges = new THashSet<HabboBadge>();

    public BadgesComponent(Habbo habbo) {
        this.badges.addAll(loadBadges(habbo));
    }

    private static THashSet<HabboBadge> loadBadges(Habbo habbo) {
        THashSet<HabboBadge> badgesList = new THashSet<HabboBadge>();
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users_badges WHERE user_id = ?");
            statement.setInt(1, habbo.getHabboInfo().getId());

            ResultSet set = statement.executeQuery();

            while (set.next()) {
                badgesList.add(new HabboBadge(set, habbo));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        return badgesList;
    }

    public static void resetSlots(Habbo habbo) {
        for (HabboBadge badge : habbo.getHabboInventory().getBadgesComponent().getBadges()) {
            if (badge.getSlot() == 0) {
                continue;
            }

            badge.setSlot(0);
            badge.needsUpdate(true);
            Emulator.getThreading().run(badge);
        }
    }

    public THashSet<HabboBadge> getWearingBadges() {
        synchronized (this.badges) {
            THashSet<HabboBadge> badgesList = new THashSet<HabboBadge>();
            for (HabboBadge badge : this.badges) {
                if (badge.getSlot() == 0) {
                    continue;
                }

                badgesList.add(badge);
            }
            return badgesList;
        }
    }

    public THashSet<HabboBadge> getBadges() {
        return this.badges;
    }

    public static THashSet<HabboBadge> getBadgesOfflineHabbo(int userId) {
        THashSet<HabboBadge> badgesList = new THashSet<HabboBadge>();
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users_badges WHERE slot_id > 0 AND user_id = ?");
            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                badgesList.add(new HabboBadge(set, null));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
        return badgesList;
    }

    public boolean hasBadge(String badge) {
        return this.getBadge(badge) != null;
    }

    public HabboBadge getBadge(String badgeCode) {
        synchronized (this.badges) {
            for (HabboBadge badge : this.badges) {
                if (badge.getCode().equals(badgeCode)) {
                    return badge;
                }
            }
            return null;
        }
    }

    public void addBadge(HabboBadge badge) {
        synchronized (this.badges) {
            this.badges.add(badge);
        }
    }

    public void removeBadge(String badge) {
        synchronized (this.badges) {
            for (HabboBadge b : this.badges) {
                if (b.getCode().equalsIgnoreCase(badge)) {
                    this.badges.remove(b);
                }
            }
        }
    }

    public void removeBadge(HabboBadge badge) {
        synchronized (this.badges) {
            this.badges.remove(badge);
        }
    }

    public void dispose() {
        synchronized (this.badges) {
            this.badges.clear();
        }
    }

    public static HabboBadge createBadge(String code, Habbo habbo) {
        HabboBadge badge = new HabboBadge(0, code, 0, habbo);
        badge.run();

        return badge;
    }
}
