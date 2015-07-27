package org.jbpm.designer.web.server.exception;

/**
 * @author a.petrov
 */
public class BpmnDesignerException extends Exception {

  public BpmnDesignerException() {
  }

  public BpmnDesignerException(String message) {
    super(message);
  }

  public BpmnDesignerException(String message, Throwable cause) {
    super(message, cause);
  }

  public BpmnDesignerException(Throwable cause) {
    super(cause);
  }

  public BpmnDesignerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
