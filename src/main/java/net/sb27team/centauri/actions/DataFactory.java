package net.sb27team.centauri.actions;

import java.util.HashMap;

public class DataFactory {

    private HashMap<String, Object> data = new HashMap<>();

    public <T> T getData(DataKey<T> key) {
        return (T) data.get(key.name);
    }

    public <T> void putData(DataKey<T> key, T obj) {
        data.putIfAbsent(key.name, obj);
    }

}
