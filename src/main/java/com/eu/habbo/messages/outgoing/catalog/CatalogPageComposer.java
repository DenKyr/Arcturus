package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.catalog.CatalogPageType;
import com.eu.habbo.habbohotel.catalog.layouts.RecentPurchasesLayout;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CatalogPageComposer extends MessageComposer {

    private final CatalogPage page;
    private final Habbo habbo;

    public CatalogPageComposer(CatalogPage page, Habbo habbo) {
        this.page = page;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.CatalogPageComposer);
        this.response.appendInt32(this.page.getId());
        this.response.appendString(CatalogPageType.NORMAL.name().toUpperCase());
        this.page.serialize(this.response);

        if (page instanceof RecentPurchasesLayout) {
            this.response.appendInt32(this.habbo.getHabboStats().getRecentPurchases().size());

            for (Map.Entry<Integer, CatalogItem> item : this.habbo.getHabboStats().getRecentPurchases().entrySet()) {
                item.getValue().serialize(this.response);
            }
        } else {
            this.response.appendInt32(this.page.getCatalogItems().size());
            List<CatalogItem> items = new ArrayList<CatalogItem>();
            items.addAll(this.page.getCatalogItems().valueCollection());
            Collections.sort(items);
            for (CatalogItem item : items) {
                item.serialize(this.response);
            }
        }
        this.response.appendInt32(0);
        this.response.appendBoolean(false); //acceptSeasonCurrencyAsCredits
        return this.response;
    }
}
