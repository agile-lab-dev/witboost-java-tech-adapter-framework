package com.witboost.provisioning.model.task;

public class TaskToken {

    private final String token;

    public TaskToken(String token) {
        this.token = token;
    }

    public static TaskToken empty() {
        return new TaskToken("");
    }

    @Override
    public String toString() {
        return token;
    }
}
