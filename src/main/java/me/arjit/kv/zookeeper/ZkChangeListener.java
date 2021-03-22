package me.arjit.kv.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;

public interface ZkChangeListener {
    void add(ChildData data);
    void remove(ChildData data);
    void modify(ChildData old, ChildData data);
}
