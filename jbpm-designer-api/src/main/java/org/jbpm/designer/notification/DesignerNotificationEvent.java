package org.jbpm.designer.notification;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.UberFireEvent;

@Portable
public class DesignerNotificationEvent extends UberFireEvent {

    private final String notification;
    private final NotificationEvent.NotificationType type;

    public DesignerNotificationEvent() {
        this("Designernotification", "message");
    }
    public DesignerNotificationEvent( final String notification, final String message ) {
        this( notification,
                NotificationEvent.NotificationType.DEFAULT );
    }

    public DesignerNotificationEvent( final String notification,
                              final NotificationEvent.NotificationType type ) {
        this.notification = notification;
        this.type = type;
    }

    public String getNotification() {
        return this.notification;
    }

    public NotificationEvent.NotificationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "DesignerNotificationEvent [notification=" + notification + ", type=" + type + "]";
    }
}
