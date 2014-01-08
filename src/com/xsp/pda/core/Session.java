package com.xsp.pda.core;

import java.io.*;
import javax.microedition.io.StreamConnection;

public class Session extends Thread implements XSPConstants {

    private StreamConnection socket, fileSocket, voiceSocket, screenSocket;
    public DataInputStream is, fis, vis, sis;
    public DataOutputStream os, fos, vos, sos;
    public UIProxy uiProxy;
    public boolean isAlive = false;

    public Session(StreamConnection socket, StreamConnection fileSocket,
            StreamConnection voiceSocket, StreamConnection screenSocket,
            UIProxy uiProxy) {
        this.socket = socket;
        this.fileSocket = fileSocket;
        this.voiceSocket = voiceSocket;
        this.screenSocket = screenSocket;
        this.uiProxy = uiProxy;

        // this.setName("XSP Session");

        try {
            is = socket.openDataInputStream();
            os = socket.openDataOutputStream();
            fis = fileSocket.openDataInputStream();
            fos = fileSocket.openDataOutputStream();
            vis = voiceSocket.openDataInputStream();
            vos = voiceSocket.openDataOutputStream();
            sis = screenSocket.openDataInputStream();
            sos = screenSocket.openDataOutputStream();
        } catch (IOException ex) {
            // Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        isAlive = true;
        while (isAlive) {
            try {
                if (is.available() > 0) {

                    // Next generation:
                    // [int:тип][int:подтип][int:кол-во UTF][UTF]...[UTF][int:кол-во байт][byte[]:байты]

                    // Прочитать тип пакета
                    int type = is.readInt();
                    // Прочитать подтип
                    int subtype = is.readInt();
                    // Читаем UTF
                    int utfl = is.readInt();
                    String[] utf = null;
                    if (utfl > 0) {
                        utf = new String[utfl];
                    }
                    if (utf != null) {
                        for (int i = 0; i < utf.length; i++) {
                            utf[i] = is.readUTF();
                        }
                    }
                    // Байты
                    int bytel = is.readInt();
                    byte[] bytes = null;
                    if (bytel > 0) {
                        bytes = new byte[bytel];
                    }
                    if (bytes != null) {
                        int received = 0;
                        while (received < bytel) {
                            received += is.read(bytes, received, bytel - received);
                        }
                    }
                    if (uiProxy != null) {
                        uiProxy.packReceived(type, subtype, utf, bytes);
                        callHandler(type, subtype, utf, bytes);
                    }
                }
            } catch (Throwable ex) {
                if (ex instanceof IOException) {
                    try {
                        closeStreams();
                    } catch (IOException ex1) {
                    }
                    break;
                }
                // Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                // Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void callHandler(int type, int subtype, String[] body, byte[] bytes) {
        switch (type) {
            case SERVICE:
                uiProxy.handleService(subtype, body, bytes);
                break;

            case PING:
                uiProxy.handlePing(subtype, body, bytes);
                break;
            case CAPSCHECK:
                uiProxy.handleCapsCheck(subtype, body, bytes);
                break;
            case MESSAGE:
                uiProxy.handleMessage(subtype, body, bytes);
                break;
            case TERMINAL:
                uiProxy.handleTerminal(subtype, body, bytes);
                break;
            case FILE:
                uiProxy.handleFile(subtype, body, bytes);
                break;
            case MICROPHONE:
                uiProxy.handleMicrophone(subtype, body, bytes);
                break;
            case DIALOG:
                uiProxy.handleDialog(subtype, body, bytes);
                break;
            case MOUSE:
                uiProxy.handleMouse(subtype, body, bytes);
                break;
            case SCREEN:
                uiProxy.handleScreen(subtype, body, bytes);
                break;

            default:
                uiProxy.errorUnknownType(type, subtype);
                break;
        }
    }

    public void closeStreams() throws IOException {
        isAlive = false;
        try {
            join();
        } catch (InterruptedException ex) {
        }
        is.close();
        os.close();
        fis.close();
        fos.close();
        vis.close();
        vos.close();
        sis.close();
        sos.close();
        socket.close();
        fileSocket.close();
        voiceSocket.close();
        screenSocket.close();
    }
}
