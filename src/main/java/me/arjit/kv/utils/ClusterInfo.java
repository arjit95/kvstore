package me.arjit.kv.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
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

    public void removeMember(String member) {
        this.getMembers().remove(member);
    }

    public void setMembers(List<String> members) {
        Collections.sort(members);
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        log.debug("Setting node name to {}", name);
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
