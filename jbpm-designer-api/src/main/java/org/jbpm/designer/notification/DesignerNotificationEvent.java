/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.notification;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.workbench.events.NotificationEvent;

@Portable
public class DesignerNotificationEvent {

    private final String notification;
    private final NotificationEvent.NotificationType type;
    private final String userId;

    public DesignerNotificationEvent() {
        this("Designernotification",
             "message",
             "userid");
    }

    public DesignerNotificationEvent(final String notification,
                                     final String message,
                                     final String userId) {
        this(notification,
             NotificationEvent.NotificationType.DEFAULT,
             userId);
    }

    public DesignerNotificationEvent(final String notification,
                                     final NotificationEvent.NotificationType type,
                                     final String userId) {
        this.notification = notification;
        this.type = type;
        this.userId = userId;
    }

    public String getNotification() {
        return this.notification;
    }

    public NotificationEvent.NotificationType getType() {
        return type;
    }

    public String getUserId() {
        return this.userId;
    }

    @Override
    public String toString() {
        return "DesignerNotificationEvent [notification=" + notification + ", type=" + type + ", userId=" + userId + "]";
    }
}
