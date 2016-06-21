package com.eu.habbo.habbohotel.wired;

public class WiredGiveRewardItem {

    public final int id;
    public final boolean badge;
    public final String data;
    public final int probability;

    public WiredGiveRewardItem(int id, boolean badge, String data, int probability) {
        this.id = id;
        this.badge = badge;
        this.data = data;
        this.probability = probability;
    }

    public WiredGiveRewardItem(String dataString) {
        String[] datal = dataString.split(",");

        this.id = Integer.valueOf(datal[0]);
        this.badge = datal[1].equalsIgnoreCase("0");
        this.data = datal[2];
        this.probability = Integer.valueOf(datal[3]);
    }

    @Override
    public String toString() {
        return this.id + "," + (this.badge ? 0 : 1) + "," + this.data + "," + probability;
    }

    public String wiredString() {
        return (this.badge ? 0 : 1) + "," + this.data + "," + probability;
    }
}
