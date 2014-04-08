package org.jbpm.designer.client.notification;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.designer.notification.DesignerNotificationEvent;
import org.uberfire.client.workbench.widgets.notifications.NotificationPopupView;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import org.uberfire.client.workbench.widgets.animations.LinearFadeOutAnimation;

@ApplicationScoped
public class DesignerNotificationPopupsManager {

    @Inject
    private SyncBeanManager iocManager;

    //When true we are in the process of removing a notification message
    private boolean removing = false;

    private final int SPACING = 48;

    private List<NotificationPopupView> activeNotifications = new ArrayList<NotificationPopupView>();
    private List<NotificationPopupView> deactiveNotifications = new ArrayList<NotificationPopupView>();


    /**
     * Display a Notification message
     * @param event
     */
    public void addNotification( @Observes final DesignerNotificationEvent event ) {

        //Create a Notification pop-up. Because it is instantiated with CDI we need to manually destroy it when finished
        final NotificationPopupView view = iocManager.lookupBean( NotificationPopupView.class ).getInstance();
        activeNotifications.add( view );
        view.setPopupPosition( getMargin(),
                activeNotifications.size() * SPACING );
        view.setNotification( event.getNotification() );
        view.setType( event.getType() );
        view.setNotificationWidth( getWidth() + "px" );
        view.show( new Command() {

            @Override
            public void execute() {
                //The notification has been shown and can now be removed
                deactiveNotifications.add( view );
                remove();
            }

        } );
    }

    //80% of screen width
    private int getWidth() {
        return (int) ( Window.getClientWidth() * 0.8 );
    }

    //10% of screen width
    private int getMargin() {
        return (int) ( ( Window.getClientWidth() - getWidth() ) / 2 );
    }

    //Remove a notification message. Recursive until all pending removals have been completed.
    private void remove() {
        if ( removing ) {
            return;
        }
        if ( deactiveNotifications.size() == 0 ) {
            return;
        }
        removing = true;
        final NotificationPopupView view = deactiveNotifications.get( 0 );
        final LinearFadeOutAnimation fadeOutAnimation = new LinearFadeOutAnimation( view ) {

            @Override
            public void onUpdate( double progress ) {
                super.onUpdate( progress );
                for ( int i = 0; i < activeNotifications.size(); i++ ) {
                    NotificationPopupView v = activeNotifications.get( i );
                    final int left = v.getPopupLeft();
                    final int top = (int) ( ( ( i + 1 ) * SPACING ) - ( progress * SPACING ) );
                    v.setPopupPosition( left,
                            top );
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                view.hide();
                deactiveNotifications.remove( view );
                activeNotifications.remove( view );
                iocManager.destroyBean( view );
                removing = false;
                remove();
            }

        };
        fadeOutAnimation.run( 500 );
    }

}
