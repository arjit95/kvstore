package me.arjit.kv.store;

import me.arjit.kv.strategies.data.LRU;

public class ByteStore {
    static Cache<byte[]> instance;

    public static Cache<byte[]> getInstance() {
        if (instance == null) {
            instance = new Cache<>(new LRU<>());
        }

        return instance;
    }
}
