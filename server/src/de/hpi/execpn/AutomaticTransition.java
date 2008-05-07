package de.hpi.execpn;

import de.hpi.petrinet.TauTransition;

public interface AutomaticTransition extends TauTransition {

String getAction();
String getXsltURL();
boolean isManuallyTriggered();
String getLabel();


void setAction(String action);
void setXsltURL(String url);
void setManuallyTriggered(boolean triggerManually);
void setLabel(String label);
}
