package me.arjit.kv.store;

import me.arjit.kv.strategies.LRU;

public class ByteStore {
    static Cache<byte[]> instance;

    public static Cache<byte[]> getInstance() {
        if (instance == null) {
            instance = new Cache<byte[]>(new LRU<>());
        }

        return instance;
    }
}
