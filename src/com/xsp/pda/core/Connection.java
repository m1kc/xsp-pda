package com.xsp.pda.core;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

public class Connection {

    public static Session connectAsServer(int port, UIProxy uiProxy) {
        StreamConnection mainSocket = null;
        StreamConnection fileSocket = null;
        StreamConnection voiceSocket = null;
        StreamConnection screenSocket = null;
        try {
            ServerSocketConnection server = (ServerSocketConnection) javax.microedition.io.Connector.open("socket://:" + port);
            mainSocket = server.acceptAndOpen();
            fileSocket = server.acceptAndOpen();
            voiceSocket = server.acceptAndOpen();
            screenSocket = server.acceptAndOpen();
        } catch (IOException ex) {
            // Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Session session = new Session(mainSocket, fileSocket, voiceSocket, screenSocket, uiProxy);
        session.start();
        return session;
    }

    public static Session connectAsClient(String host, int port, UIProxy uiProxy) {
        StreamConnection mainSocket = null;
        StreamConnection fileSocket = null;
        StreamConnection voiceSocket = null;
        StreamConnection screenSocket = null;
        try {
            mainSocket = (SocketConnection) javax.microedition.io.Connector.open("socket://" + host + ":" + port);
            fileSocket = (SocketConnection) javax.microedition.io.Connector.open("socket://" + host + ":" + port);
            voiceSocket = (SocketConnection) javax.microedition.io.Connector.open("socket://" + host + ":" + port);
            screenSocket = (SocketConnection) javax.microedition.io.Connector.open("socket://" + host + ":" + port);
        } catch (IOException ex) {
            // Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Session session = new Session(mainSocket, fileSocket, voiceSocket, screenSocket, uiProxy);
        session.start();
        return session;
    }

    public static String requestMyIp() throws IOException {
        String myHost = new String();
        HttpConnection httpConnection = (HttpConnection) Connector.open("http://www.tomclaw.com/services/simple/getip.php");
        InputStream is = httpConnection.openInputStream();
        int read;
        byte[] buffer = new byte[128];
        while((read = is.read(buffer)) != -1) {
            myHost+=new String(buffer,0,read);
        }
        return myHost;
    }
}
