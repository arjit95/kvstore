package me.arjit.kv.models;

public class CacheEntry<T> {
    private String key;
    private T value;

    public CacheEntry(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
