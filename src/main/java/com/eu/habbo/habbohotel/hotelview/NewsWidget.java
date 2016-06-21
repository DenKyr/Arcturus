package com.eu.habbo.habbohotel.hotelview;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsWidget {

    /**
     * News ID
     */
    private final int id;

    /**
     * Title
     */
    private final String title;

    /**
     * Message
     */
    private final String message;

    /**
     * Text on the button
     */
    private final String buttonMessage;

    /**
     * Type
     */
    private final int type;

    /**
     * Link
     */
    private final String link;

    /**
     * Image
     */
    private final String image;

    public NewsWidget(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.title = set.getString("title");
        this.message = set.getString("text");
        this.buttonMessage = set.getString("button_text");
        this.type = set.getString("button_type").equals("client") ? 1 : 0;
        this.link = set.getString("button_link");
        this.image = set.getString("image");
    }

    /**
     * News ID
     *
     * @return ID of news
     */
    public int getId() {
        return this.id;
    }

    /**
     * Title
     *
     * @return Title of news
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Message
     *
     * @return Message of news
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Text on the button
     *
     * @return Button text of news
     */
    public String getButtonMessage() {
        return this.buttonMessage;
    }

    /**
     * Type
     *
     * @return Type of news
     */
    public int getType() {
        return this.type;
    }

    /**
     * Link
     *
     * @return Link of news
     */
    public String getLink() {
        return this.link;
    }

    /**
     * Image
     *
     * @return Image of news
     */
    public String getImage() {
        return this.image;
    }
}
