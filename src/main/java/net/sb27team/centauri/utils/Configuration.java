package net.sb27team.centauri.utils;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/*
 * Created by Cubxity on 18/05/2018
 */
public class Configuration {
    private JsonObject json;
    private File config = new File(System.getProperty("user.home"), "centauri.json");
    private JsonParser parser = new JsonParser();
    private Gson gson = new Gson();

    public Configuration() {
        if (config.exists())
            load();
        else json = new JsonObject();
    }

    private void load() {
        try {
            json = parser.parse(Files.toString(config, Charset.defaultCharset())).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Files.write(json.toString(), config, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String key, T def) {
        if (json.has(key))
            return (T) gson.fromJson(json.get(key), def.getClass());
        json.add(key, gson.toJsonTree(def));
        return def;
    }

    public void set(String key, Object val) {
        json.add(key, gson.toJsonTree(val));
    }
}
