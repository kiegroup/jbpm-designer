if (!ORYX.Plugins)
    ORYX.Plugins = {};

if (!ORYX.Config)
    ORYX.Config = {};

ORYX.Plugins.NotificationsPlugin = Clazz.extend({
    construct: function(facade){
        this.facade = facade;
        if(!ORYX.NOTIFICATIONSINIT) {
            this.facade.registerOnEvent(ORYX.CONFIG.EVENT_NOTIFICATION_SHOW, this.showNotification.bind(this));
            ORYX.NOTIFICATIONSINIT = true;
        }
    },
    showNotification: function(options) {
        notifications.options = {
            positionClass: options.position || 'notification-top-right',
            onclick: options.onclick || null
        };
        var notification = notifications[options.ntype](options.msg, options.title);
    }
});