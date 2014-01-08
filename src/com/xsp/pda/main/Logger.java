package com.xsp.pda.main;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.file.FileConnection;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2010
 * http://www.tomclaw.com/
 * @author Игорь
 */
public class Logger {

    public boolean isOutToSock = false;
    public boolean isOutToFile = false;
    public boolean isOutToCons = true;
    public String filePath = "/root1/";
    private OutputStream fileOutputStream = null;
    private OutputStream sockOutputStream = null;
    private OutputStream consOutputStream = null;

    public Logger(boolean isOutToCons, boolean isOutToSock, String host, int port, boolean isOutToFile, String filePath) {
        this.isOutToCons = isOutToCons;
        this.isOutToFile = isOutToFile;
        this.isOutToSock = isOutToSock;
        this.filePath = filePath;
        if (isOutToFile) {
            try {
                openFileConnection();
            } catch (IOException ex) {
            }
        }
        if (isOutToCons) {
            try {
                openConsConnection();
            } catch (IOException ex) {
            }
        }
        if (isOutToSock) {
            try {
                openSockConnection(host, port);
            } catch (IOException ex) {
            }
        }
    }

    public void outMessage(String logMessage) {
        outMessage(logMessage, false);
    }

    public void outMessage(String logMessage, boolean isError) {
        logMessage = "[" + Runtime.getRuntime().freeMemory() / 1024 + " KiB / "
                + Runtime.getRuntime().totalMemory() / 1024 + " KiB] " + (isError ? "[ERR] " : "") + logMessage;
        if (isOutToFile) {
            write(fileOutputStream, (isError ? "[ERR] " : "").concat(logMessage));
        }
        if (isOutToSock) {
            write(sockOutputStream, (isError ? "[ERR] " : "").concat(logMessage));
        }
        if (isOutToCons) {
            write(consOutputStream, (isError ? "[ERR] " : "").concat(logMessage));
        }

    }

    private void openFileConnection() throws IOException {
        String fileName = "file://" + filePath + "mnd_" + (new Date()).getTime() + ".log";
        FileConnection fileConnection = (FileConnection) Connector.open(fileName, 3);
        if (!((FileConnection) (fileConnection)).exists()) {
            (fileConnection).create();
        }
        fileOutputStream = fileConnection.openOutputStream();
    }

    private void openConsConnection() throws IOException {
        consOutputStream = System.out;
    }

    private void openSockConnection(String host, int port) throws IOException {
        SocketConnection socket = (SocketConnection) Connector.open("socket://" + host + ":" + port,
                Connector.READ_WRITE);
        sockOutputStream = socket.openOutputStream();
    }

    private void write(OutputStream outputStream, String logMessage) {
        if (outputStream != null && logMessage != null) {
            try {
                outputStream.write((logMessage + "\n").getBytes());
                outputStream.flush();
            } catch (IOException ex) {
            }
        }
    }
}
