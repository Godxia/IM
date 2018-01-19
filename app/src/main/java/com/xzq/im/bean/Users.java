package com.xzq.im.bean;

import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.util.List;

/**
 * Created by lenovo on 2018/1/15.
 */

public class Users {
    private String groupName;
    private List<UsersDetails> details;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UsersDetails> getDetails() {
        return details;
    }

    public void setDetails(List<UsersDetails> details) {
        this.details = details;
    }
    public static class UsersDetails {
        private String userIp;
        private String pickName;
        private RosterPacket.ItemType type;
      //  private RosterPacket.ItemStatus status;

        public String getUserIp() {
            return userIp;
        }

        public void setUserIp(String userIp) {
            this.userIp = userIp;
        }

        public String getPickName() {
            return pickName;
        }

        public void setPickName(String pickName) {
            this.pickName = pickName;
        }

        public RosterPacket.ItemType getType() {
            return type;
        }

        public void setType(RosterPacket.ItemType type) {
            this.type = type;
        }
//
//        public RosterPacket.ItemStatus getStatus() {
//            return status;
//        }
//
//        public void setStatus(RosterPacket.ItemStatus status) {
//            this.status = status;
//        }
    }
}
