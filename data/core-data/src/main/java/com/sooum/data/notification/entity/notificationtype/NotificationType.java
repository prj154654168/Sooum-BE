package com.sooum.data.notification.entity.notificationtype;

public enum NotificationType {
    FEED_LIKE, COMMENT_LIKE, COMMENT_WRITE, BLOCKED, DELETED, TRANSFER_SUCCESS;

    public boolean isNotSystem() {
        return !(this.equals(BLOCKED) || this.equals(DELETED) || this.equals(TRANSFER_SUCCESS));
    }

    public boolean isNotGeneral() {
        return !(this.equals(FEED_LIKE) || this.equals(COMMENT_LIKE) || this.equals(COMMENT_WRITE));
    }
}
