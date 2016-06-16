package com.eu.habbo.habbohotel.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MarketPlaceOffer implements Runnable {

    private int offerId;
    private final Item baseItem;
    private final int itemId;
    private int price;
    private int type;
    private int limitedStack;
    private int limitedNumber;
    private int timestamp = Emulator.getIntUnixTimestamp();
    private MarketPlaceState state = MarketPlaceState.OPEN;
    private boolean needsUpdate = false;

    public int avarage;
    public int count;

    public MarketPlaceOffer(ResultSet set, boolean privateOffer) throws SQLException {
        this.offerId = set.getInt("id");
        this.price = set.getInt("price");
        this.timestamp = set.getInt("timestamp");
        this.baseItem = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("base_item_id"));
        this.state = MarketPlaceState.getType(set.getInt("state"));
        this.itemId = set.getInt("item_id");
        String typel = set.getString("type");

        if (typel.equalsIgnoreCase("s")) {
            this.type = 1;
        }
        if (typel.equalsIgnoreCase("i")) {
            this.type = 2;
        }
        if (!set.getString("ltd_data").split(":")[1].equals("0")) {
            this.type = 3;
            this.limitedStack = Integer.valueOf(set.getString("ltd_data").split(":")[0]);
            this.limitedNumber = Integer.valueOf(set.getString("ltd_data").split(":")[1]);
        }

        if (!privateOffer) {
            this.avarage = set.getInt("avg");
            this.count = set.getInt("number");
            this.price = set.getInt("minPrice");
        }
    }

    public MarketPlaceOffer(HabboItem item, int price, Habbo habbo) throws SQLException {
        this.price = price;
        this.baseItem = item.getBaseItem();
        this.itemId = item.getId();
        if (item.getBaseItem().getType().equalsIgnoreCase("s")) {
            this.type = 1;
        }
        if (item.getBaseItem().getType().equalsIgnoreCase("i")) {
            this.type = 2;
        }
        if (item.getLimitedSells() > 0) {
            this.limitedNumber = item.getLimitedSells();
            this.limitedStack = item.getLimitedStack();
        }

        PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO marketplace_items (item_id, user_id, price, timestamp, state) VALUES (?, ?, ?, ?, ?)");
        statement.setInt(1, item.getId());
        statement.setInt(2, habbo.getHabboInfo().getId());
        statement.setInt(3, this.price);
        statement.setInt(4, this.timestamp);
        statement.setString(5, this.state.getState() + "");
        statement.execute();

        ResultSet id = statement.getGeneratedKeys();
        while (id.next()) {
            this.offerId = id.getInt(1);
        }
        id.close();
        statement.close();
        statement.getConnection().close();

    }

    public int getOfferId() {
        return this.offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getItemId() {
        return this.baseItem.getSpriteId();
    }

    public int getPrice() {
        return this.price;
    }

    public MarketPlaceState getState() {
        return this.state;
    }

    public void setState(MarketPlaceState state) {
        this.state = state;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public int getType() {
        return this.type;
    }

    public int getLimitedStack() {
        return this.limitedStack;
    }

    public int getLimitedNumber() {
        return this.limitedNumber;
    }

    public int getSoldItemId() {
        return this.itemId;
    }

    public void needsUpdate(boolean value) {
        this.needsUpdate = value;
    }

    @Override
    public void run() {
        if (this.needsUpdate) {
            this.needsUpdate = false;
            try {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE marketplace_items SET state = ? WHERE id = ?");
                statement.setInt(1, this.state.getState());
                statement.setInt(2, this.offerId);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public static void insert(MarketPlaceOffer offer, Habbo habbo) {
        try {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO marketplace_items VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, offer.getItemId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, offer.getPrice());
            statement.setInt(4, offer.getTimestamp());
            statement.setString(5, offer.getState().getState() + "");
            statement.execute();

            ResultSet id = statement.getGeneratedKeys();
            while (id.next()) {
                offer.setOfferId(id.getInt(1));
            }
            id.close();
            statement.close();
            statement.getConnection().close();

        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
