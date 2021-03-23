package me.arjit.kv.utils;

import lombok.extern.slf4j.Slf4j;
import me.arjit.kv.config.environment.Constants;
import me.arjit.kv.models.Context;
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

    public void setName(String name) {
        log.debug("Setting node name to {}", name);
        this.name = name;
    }

    public boolean isLeader() {
        if (this.name == null || this.members.size() == 0) {
            return false;
        }

        return this.name.equals(this.members.first().getName());
    }

    public static ClusterInfo getInstance() {
        if (instance == null) {
            instance = new ClusterInfo();
        }

        return instance;
    }

    public String getAddress() {
        Context context = Context.getContext();
        return Constants.ZOOKEEPER_LEADER  + "/" + context.env.getValue(Constants.APPLICATION_NAME);
    }
}
