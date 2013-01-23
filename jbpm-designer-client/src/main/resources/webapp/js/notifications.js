;(function(window, $) {
    window.notifications = (function() {
        var 
            defaults = {
                tapToDismiss: true,
                notificationClass: 'notification',
                containerId: 'notification-container',
                debug: false,
                fadeIn: 300,
                fadeOut: 1000,
                extendedTimeOut: 1000,
                iconClasses: {
                    error: 'notification-error',
                    info: 'notification-info',
                    success: 'notification-success',
                    warning: 'notification-warning'
                },
                iconClass: 'notification-info',
                positionClass: 'notification-top-right',
                timeOut: 5000, // Set timeOut to 0 to make it sticky
                titleClass: 'notification-title',
                messageClass: 'notification-message'
            },


            error = function(message, title) {
                return notify({
                    iconClass: getOptions().iconClasses.error,
                    message: message,
                    title: title
                })
            },

            getContainer = function(options) {
                var $container = $('#' + options.containerId)

                if ($container.length)
                    return $container

                $container = $('<div/>')
                    .attr('id', options.containerId)
                    .addClass(options.positionClass)

                $container.appendTo($('body'))

                return $container
            },

            getOptions = function() {
                return $.extend({}, defaults, notifications.options)
            },

            info = function(message, title) {
                return notify({
                    iconClass: getOptions().iconClasses.info,
                    message: message,
                    title: title
                })
            },

            notify = function(map) {
                var 
                    options = getOptions(),
                    iconClass = map.iconClass || options.iconClass,
                    intervalId = null,
                    $container = getContainer(options),
                    $notificationElement = $('<div/>'),
                    $titleElement = $('<div/>'),
                    $messageElement = $('<div/>'),
                    response = { options: options, map: map }

                if (map.iconClass) {
                    $notificationElement.addClass(options.notificationClass).addClass(iconClass)
                }

                if (map.title) {
                    $titleElement.append(map.title).addClass(options.titleClass)
                    $notificationElement.append($titleElement)
                }

                if (map.message) {
                    $messageElement.append(map.message).addClass(options.messageClass)
                    $notificationElement.append($messageElement)
                }

                var fadeAway = function() {
                    if ($(':focus', $notificationElement).length > 0)
                		return
                	
                    var fade = function() {
                        return $notificationElement.fadeOut(options.fadeOut)
                    }

                    $.when(fade()).done(function() {
                        if ($notificationElement.is(':visible')) {
                            return
                        }
                        $notificationElement.remove()
                        if ($container.children().length === 0)
                            $container.remove()
                    })
                }

                var delayedFadeAway = function() {
                    if (options.timeOut > 0 || options.extendedTimeOut > 0) {
                        intervalId = setTimeout(fadeAway, options.extendedTimeOut)
                    }
                }

                var stickAround = function() {
                    clearTimeout(intervalId)
                    $notificationElement.stop(true, true)
                        .fadeIn(options.fadeIn)
                }

                $notificationElement.hide()
                $container.prepend($notificationElement)
                $notificationElement.fadeIn(options.fadeIn)

                if (options.timeOut > 0) {
                    intervalId = setTimeout(fadeAway, options.timeOut)
                }

                $notificationElement.hover(stickAround, delayedFadeAway)

                if (options.tapToDismiss) {
                    $notificationElement.click(fadeAway)
                }

                if (options.debug) {
                    console.log(response)
                }
                return $notificationElement
            },

            success = function(message, title) {
                return notify({
                    iconClass: getOptions().iconClasses.success,
                    message: message,
                    title: title
                })
            },

            warning = function(message, title) {
                return notify({
                    iconClass: getOptions().iconClasses.warning,
                    message: message,
                    title: title
                })
            }

        return {
            error: error,
            info: info,
            options: {},
            success: success,
            warning: warning
        }
    })()
} (window, jQuery));