package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModToolBan implements Runnable {

    private final int userId;
    private final int staffId;
    public int expireDate;
    public String reason;

    private final boolean needsInsert;

    public ModToolBan(ResultSet set) throws SQLException {
        this.userId = set.getInt("user_id");
        this.staffId = set.getInt("user_staff_id");
        this.expireDate = set.getInt("ban_expire");
        this.reason = set.getString("ban_reason");
        this.needsInsert = false;
    }

    public ModToolBan(int userId, int staffId, int expireDate, String reason) {
        this.userId = userId;
        this.staffId = staffId;
        this.expireDate = expireDate;
        this.reason = reason;
        this.needsInsert = true;
    }

    @Override
    public void run() {
        if (needsInsert) {
            try {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO bans (user_id, user_staff_id, ban_expire, ban_reason) VALUES (?, ?, ?, ?)");
                statement.setInt(1, this.userId);
                statement.setInt(2, this.staffId);
                statement.setInt(3, this.expireDate);
                statement.setString(4, this.reason);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
