package me.arjit.kv.utils;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.models.Server;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
public class ClusterInfo {
    private static ClusterInfo instance;
    private final SortedSet<Server> members;
    private String name;

    private ClusterInfo() {
        members = new TreeSet<>(Comparator.comparing(Server::getName));
    }

    public Server[] getMembers() {
        Server[] memberList = new Server[members.size()];
        int i = 0;

        for (Server member : members) {
            memberList[i++] = member;
        }

        return memberList;
    }

    public void addMember(Server member) {
        this.members.add(member);
    }

    public void removeMember(Server member) {
        this.members.remove(member);
    }

    public String getName() {
        return name;
    }

    public String getPartitionName(String name) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i))) {
                break;
            }

            builder.append(name.charAt(i));
        }

        return builder.toString();
    }

    public String getPartitionName() {
        return getPartitionName(name);
    }

    public void setName(String name) {
        log.debug("Setting node name to {}", name);
        this.name = name;
    }

    public Server getLeader() {
        if (this.members.size() == 0) {
            return null;
        }

        return this.members.first();
    }

    public boolean isLeader() {
        if (this.name == null || this.members.size() == 0) {
            return false;
        }

        return this.name.equals(this.getLeader().getName());
    }

    public static ClusterInfo getInstance() {
        if (instance == null) {
            instance = new ClusterInfo();
        }

        return instance;
    }
}
