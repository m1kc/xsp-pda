/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xsp.pda.core;

import com.xsp.pda.main.MidletMain;
import java.io.*;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author m1kc
 */
public class DirectTransfer {

    public static void sendFile(String fileName, OutputStream os, long start, UIProxy u) {
        try {
            // File f = new File(filename);
            DataOutputStream dos = new DataOutputStream(os);
            // FileInputStream fis = new FileInputStream(f);

            MidletMain.directTransferFrame.fileName.setCaption(fileName);
            MidletMain.directTransferFrame.fileName.updateCaption();

            FileConnection fileConnection = (FileConnection) Connector.open("file://".concat(fileName));
            /*if (fileConnection.exists()) {
                fileConnection.delete();
            }
            fileConnection.create();*/

            DataInputStream fis = fileConnection.openDataInputStream();

            // Протокол: имя файла
            MidletMain.logger.outMessage("File path: " + fileName);
            MidletMain.logger.outMessage("Sending file name: " + fileConnection.getName());
            dos.writeUTF(fileConnection.getName());
            // Протокол: размер
            long size = fileConnection.fileSize();
            dos.writeLong(size);
            // Протокол: начальная точка
            dos.writeLong(start);
            // Протокол: файл
            if (start > 0) {
                fis.skip(start);
            }
            int buffLen = 0;
            byte[] buffer;
            long startTime = System.currentTimeMillis();
            int speed = 0;
            long fileLength = fileConnection.fileSize();
            for (long c = start; c < fileLength; c += buffLen) {
                MidletMain.logger.outMessage("c = " + c);
                buffer = new byte[1024 * 10];
                buffLen = fis.read(buffer);
                if (buffLen < 0) {
                    break;
                }
                dos.write(buffer, 0, buffLen);
                if (startTime != System.currentTimeMillis()) {
                    speed = (int) (((c - start) * 1000 / (System.currentTimeMillis() - startTime)) / 1024);
                }
                u.sendProgress(c, fileLength, speed);
            }
            fis.close();
            /*long sent = 0;
            long starttime = System.currentTimeMillis();
            long speed = 0;
            byte[] b = new byte[1024 * 128];
            MidletMain.logger.outMessage("Start...");
            while (fis.available() > 0) {
                MidletMain.logger.outMessage("...");
                if (fis.available() < b.length) {
                    b = new byte[fis.available()];
                }
                fis.read(b);
                dos.write(b);
                dos.flush();
                sent += b.length;
                speed = System.currentTimeMillis() - starttime; // Время в мс
                speed /= 1000; // Время в секундах
                if (speed != 0) {
                    speed = sent / speed;
                } else {
                    speed = 0; // Скорость в байт/сек
                }
                speed /= 1024; // Скорость в Кб/сек
                u.sendProgress(sent, size, (int) speed);
            }*/
            MidletMain.logger.outMessage("Done.");
            u.sendDone();
        } catch (Throwable ex) {
            MidletMain.logger.outMessage(ex.getMessage());
        }
    }

    public static void receiveFile(String filePath, InputStream is, UIProxy u) {
        try {
            DataInputStream dis = new DataInputStream(is);
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            long fileOffset = dis.readLong();

            MidletMain.directTransferFrame.fileName.setCaption(fileName);
            MidletMain.directTransferFrame.fileName.updateCaption();

            FileConnection fileConnection = (FileConnection) Connector.open("file://" + filePath.concat(fileName));
            if (fileConnection.exists()) {
                fileConnection.delete();
            }
            fileConnection.create();

            DataOutputStream dos = fileConnection.openDataOutputStream();
            int buffLen = 0;
            byte[] buffer;
            long startTime = System.currentTimeMillis();
            int speed = 0;
            for (long c = fileOffset; c < fileLength; c += buffLen) {
                // MidletMain.logger.outMessage("c = " + c);
                buffer = new byte[1024 * 10];
                buffLen = dis.read(buffer);
                if (buffLen < 0) {
                    break;
                }
                dos.write(buffer, 0, buffLen);
                if (startTime != System.currentTimeMillis()) {
                    speed = (int) (((c - fileOffset) * 1000 / (System.currentTimeMillis() - startTime)) / 1024);
                }
                u.receiveProgress(c, fileLength, speed);
            }
            MidletMain.logger.outMessage("Closed");
            dos.close();
            fileConnection.close();
        } catch (IOException ex) {
        }
        MidletMain.logger.outMessage("Done");
        u.receiveDone();
    }
}
