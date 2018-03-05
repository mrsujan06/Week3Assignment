package com.test.week3assignment.model;

/**
 * Created by Sujan on 05/03/2018.
 */

public class PushRequestBody {

    private final String to;
    private final Notification notification;

    public PushRequestBody(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }
}
