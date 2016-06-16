package com.eu.habbo.core;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public final class TextsManager {

    /**
     * All emulator texts are stored in this object.
     */
    private final Properties texts;

    public TextsManager() {
        long millis = System.currentTimeMillis();

        this.texts = new Properties();

        try {

            this.reload();

            Emulator.getLogging().logStart("Texts Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine("[CRITICAL] FAILED TO LOAD TEXTS MANAGER!");
        }
    }

    /**
     * Reloads all texts from the database.
     *
     * @throws Exception
     */
    public void reload() throws Exception {
        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM emulator_texts");

        try {
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                if (this.texts.containsKey(set.getString("key"))) {
                    this.texts.setProperty(set.getString("key"), set.getString("value"));
                } else {
                    this.texts.put(set.getString("key"), set.getString("value"));
                }
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Gets the string value for a specific key.
     *
     * @param key The key to find the value for.
     * @return The string value for the key. Returns an empty string if not
     * found.
     */
    public String getValue(String key) {
        return getValue(key, "");
    }

    /**
     * Gets the string value for a specific key.
     *
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not
     * found.
     * @return The string value for the key. Returns defaultValue when not
     * found.
     */
    public String getValue(String key, String defaultValue) {
        if (!this.texts.containsKey(key)) {
            Emulator.getLogging().logErrorLine("[TEXTS] Text key not found: " + key);
        }
        return this.texts.getProperty(key, defaultValue);
    }

    /**
     * Gets the boolean value for a specific key.
     *
     * @param key The key to find the value for.
     * @return The boolean value for the key. Returns false if not found.
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Gets the boolean value for a specific key.
     *
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not
     * found.
     * @return The boolean value for the key. Returns defaultValue when not
     * found.
     */
    public boolean getBoolean(String key, Boolean defaultValue) {
        try {
            return (getValue(key, "0").equals("1")) || (getValue(key, "false").equals("true"));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
        return defaultValue;
    }

    /**
     * Gets the int value for a specific key.
     *
     * @param key The key to find the value for.
     * @return The int value for the key. Returns 0 if not found.
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * Gets the int value for a specific key.
     *
     * @param key The key to find the value for.
     * @param defaultValue The value that will be returned when the key is not
     * found.
     * @return The int value for the key. Returns defaultValue when not found.
     */
    public int getInt(String key, Integer defaultValue) {
        try {
            return Integer.parseInt(getValue(key, defaultValue.toString()));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
        return defaultValue;
    }
}
