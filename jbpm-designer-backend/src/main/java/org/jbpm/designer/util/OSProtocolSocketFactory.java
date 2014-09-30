package org.jbpm.designer.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OSProtocolSocketFactory implements ProtocolSocketFactory {

    private static Log log = LogFactory.getLog(OSProtocolSocketFactory.class);

    public OSProtocolSocketFactory() {
    }

    @Override
    public Socket createSocket(String host,
                               int port,
                               InetAddress localAddress,
                               int localPort) throws IOException, UnknownHostException {
        if (log.isDebugEnabled()) {
            log.debug("createSocket called. host = " + host + ", port = " + port
                    + ", ignoring localAddress = " + ((localAddress != null) ? localAddress.toString() : "null")
                    + ", ignoring localPort = " + localPort);
        }

        Socket socket = null;
        try {
            socket = new Socket(host, port);
            log.debug("Socket created");
        }
        catch (IOException e) {
            log.error("Error creating socket: " + e.getMessage());
            throw e;
        }
        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress,
                               int localPort, HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {

        log.debug("createSocket called with HttpConnectionParams -- ignoring the timeout value and proceeding");

        return this.createSocket(host, port, localAddress, localPort);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException,
            UnknownHostException,IOException {

        log.debug("createSocket called with just host and port. proceeding..");

        return this.createSocket(host, port, null, 0);
    }

}