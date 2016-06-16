package com.eu.habbo.habbohotel.navigation;

public enum SearchMode {

    LIST(0),
    THUMBNAILS(1),
    FORCED_THUNBNAILS(2);

    public final int type;

    SearchMode(int type) {
        this.type = type;
    }
}
