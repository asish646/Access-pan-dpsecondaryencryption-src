package com.ntrs.pan.access.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Permission {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("rights")
    private List<String> rights;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getRights() {
        return rights;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }
}
