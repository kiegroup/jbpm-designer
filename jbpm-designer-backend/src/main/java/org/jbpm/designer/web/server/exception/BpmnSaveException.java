package org.jbpm.designer.web.server.exception;

/**
 * @author a.petrov
 */
public class BpmnSaveException extends BpmnDesignerException {

  public BpmnSaveException() {
  }

  public BpmnSaveException(String message) {
    super(message);
  }

  public BpmnSaveException(String message, Throwable cause) {
    super(message, cause);
  }

  public BpmnSaveException(Throwable cause) {
    super(cause);
  }

  public BpmnSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
