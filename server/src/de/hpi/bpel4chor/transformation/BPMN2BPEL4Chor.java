package de.hpi.bpel4chor.transformation;


public interface BPMN2BPEL4Chor extends java.rmi.Remote {
    public java.lang.String[] transform(java.lang.String diagramStr, boolean validate) throws java.rmi.RemoteException;
}
