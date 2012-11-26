package org.jbpm.designer.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Date;
import java.util.Random;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jbpm.designer.test.web.AbstractGuvnorIntegrationTest;
import org.jbpm.designer.test.web.util.GuvnorInterfaceUtil;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
@Ignore
public class ScratchTest extends AbstractGuvnorIntegrationTest { 

    @BeforeClass
    public static void beforeArquillian() { 
        assignOpenPortToArquillianServer();
    }
    
    private static void assignOpenPortToArquillianServer() { 
        Random random = new Random();
        // http://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers#Dynamic.2C_private_or_ephemeral_ports 
        // Starts at  49152, max port is 65535
//        int START = 49152;
//        int MAX = 65535-START;
        int START = 0;
        int MAX = 1024-START;
        
        int openPort = -1;
        while( openPort < 0 ) { 
            int portToTry = random.nextInt(MAX) + START;
            if( available(portToTry) ) { 
                System.out.println( "port " + portToTry + " is open!" );
                openPort = portToTry;
            }
        }
        
        InputStream is = ScratchTest.class.getResourceAsStream("/arquillian.xml");
    }
    
    public static boolean available(int port) {
        System.out.println( "Trying port "+ port + "..." );
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            // do nothing
        } finally {
            if (ds != null) { ds.close(); }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    // should not be thrown 
                }
            }
        }

        return false;
    }
    
    @Test
    public void setupGuvnor() throws Exception {
        GuvnorInterfaceUtil guvnor = GuvnorInterfaceUtil.instance(guvnorUrl);
        
        // Insert packages
        final String now = sdf.format(new Date());
        packageNameList[0] = ("one" + now).intern();
        packageNameList[1] = ("two" + now).intern();
        packageNameList[2] = ("thr" + now).intern();

        for (String pkg : packageNameList) {
            guvnor.createPackageViaAtom(pkg);
        }
    }

}