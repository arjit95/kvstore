package me.arjit.kv.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClusterInfo {
    private static ClusterInfo instance;
    private List<String> members;
    private String name;

    private ClusterInfo() {
        members = new ArrayList<>();
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        Collections.sort(members);
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLeader() {
        if (this.name == null) {
            return false;
        }

        return this.name.equals(this.getMembers().get(0));
    }

    public static ClusterInfo getInstance() {
        if (instance == null) {
            instance = new ClusterInfo();
        }

        return instance;
    }
}
