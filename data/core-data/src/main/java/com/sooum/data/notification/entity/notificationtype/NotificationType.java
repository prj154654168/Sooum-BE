package com.sooum.data.notification.entity.notificationtype;

public enum NotificationType {
    FEED_LIKE, COMMENT_LIKE, COMMENT_WRITE, BLOCKED, DELETED;

    public boolean isNotSystem() {
        return !(this.equals(BLOCKED) || this.equals(DELETED));
    }

    public boolean isNotGeneral() {
        return !(this.equals(FEED_LIKE) || this.equals(COMMENT_LIKE) || this.equals(COMMENT_WRITE));
    }
}
