/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xsp.pda.main;

import com.tomclaw.tcuilite.Dialog;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.List;
import com.tomclaw.tcuilite.ListItem;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Screen;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;
import com.tomclaw.tcuilite.localization.Localization;
import com.xsp.pda.core.DirectTransfer;
import com.xsp.pda.core.Sender;
import com.xsp.pda.core.XSPConstants;
import com.xsp.pda.main.MidletMain;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author solkin
 */
public class FileBrowserFrame extends Window {

    /** Local variables **/
    public List filesList = null;
    FileConnection fileConnection;
    String systemPath = "";
    public final int manType;

    public FileBrowserFrame(final int manType) {
        super(MidletMain.screen);
        this.manType = manType;

        header = new Header("Browser frame");

        soft = new Soft(MidletMain.screen);

        PopupItem openItem = new PopupItem("Open") {

            public void actionPerformed() {
                /** Action thread was been **/
                String __selectedString = null;
                __selectedString = ((ListItem) filesList.items.elementAt(filesList.selectedIndex)).title;
                if (__selectedString.hashCode() != "...".hashCode()) {
                    boolean isFolder = __selectedString.endsWith("/");

                    if (!isFolder) {
                        if (manType == 0x00) {
                            String filePath = ("/".concat(systemPath).concat(__selectedString));
                            FileConnection fileConnection = null;
                            try {
                                fileConnection = (FileConnection) Connector.open("file://".concat(filePath), Connector.READ);
                                if (!fileConnection.exists()) {
                                    return;
                                }

                                /*parent.fileLocation = "/".concat(systemPath);
                                parent.fileName = __selectedString;
                                parent.fileSize = fileConnection.fileSize();
                                parent.filePath.setLabel(filePath);
                                parent.fileBytesSize.setLabel(String.valueOf(parent.fileSize) + " bytes");
                                 */
                                final String fileName = __selectedString;
                                final String fileLocation = "/".concat(systemPath);
                                long fileSize = fileConnection.fileSize();

                                if (fileName != null) {

                                    /**
                                     * fileName, fileLocation, fileSize
                                     */
                                    MidletMain.fileLocation = fileLocation.concat(fileName);
                                    Sender.sendPack(MidletMain.session.os, XSPConstants.FILE, XSPConstants.REQUEST, fileName, null, MidletMain.mainFrame);
                                }
                                MidletMain.screen.setActiveWindow(FileBrowserFrame.this.s_prevWindow);
                                return;
                            } catch (IOException ex) {
                                // ex.printStackTrace();
                                MidletMain.logger.outMessage("Local file error: " + ex.getMessage(), true);
                            }
                        }
                    } else {
                        systemPath += __selectedString;
                        readLevel(systemPath);
                    }
                } else {
                    getLowerLevel();
                    readLevel(systemPath);
                }
                /** Repainting **/
                MidletMain.screen.repaint(Screen.REPAINT_STATE_PLAIN);
            }
        };
        soft.leftSoft = openItem;
        PopupItem menuItem = new PopupItem("Menu");
        soft.rightSoft = menuItem;

        PopupItem backItem = new PopupItem("Cancel") {

            public void actionPerformed() {
                if (FileBrowserFrame.this.s_prevWindow != null) {
                    MidletMain.screen.setActiveWindow(FileBrowserFrame.this.s_prevWindow);
                } /*else if (settingsParent != null) {
                MidletMain.setCurrentWindow(settingsParent);
                }*/
            }
        };
        soft.rightSoft.addSubItem(backItem);
        if (manType == 0x00) {
            PopupItem infoPopupItem = new PopupItem("Info") {

                public void actionPerformed() {
                    String __selectedString = null;
                    __selectedString = ((ListItem) filesList.items.elementAt(filesList.selectedIndex)).title;
                    if (__selectedString.hashCode() != "...".hashCode()) {
                        boolean isFolder = __selectedString.endsWith("/");

                        if (!isFolder) {
                            String filePath = ("/".concat(systemPath).concat(__selectedString));
                            FileConnection fileConnection = null;
                            try {
                                fileConnection = (FileConnection) Connector.open("file://".concat(filePath), Connector.READ);
                                if (!fileConnection.exists()) {
                                    return;
                                }

                                /*parent.fileLocation = "/".concat(systemPath);
                                parent.fileName = __selectedString;
                                parent.fileSize = fileConnection.fileSize();
                                parent.filePath.setLabel(filePath);
                                parent.fileBytesSize.setLabel(String.valueOf(parent.fileSize) + " bytes");
                                 */
                                final String fileInfoString = ("File name").concat(":").concat(__selectedString)
                                        .concat("\n ").concat("File size").concat(":").concat(fileConnection.fileSize() + " KiB")
                                        .concat("\n ").concat("Modification").concat(":").concat(
                                        TimeUtil.getDateString(false, fileConnection.lastModified()))
                                        .concat("\n ").concat("Folder").concat(":").concat(filePath).concat("\n");
                                new Thread() {

                                    public void run() {
                                        FileBrowserFrame.this.showDialog(new Dialog(
                                                "Info",
                                                fileInfoString,
                                                MidletMain.screen.getWidth(),
                                                MidletMain.screen.getHeight()));
                                        MidletMain.screen.repaint();
                                        try {
                                            Thread.currentThread().sleep(5000);
                                        } catch (InterruptedException ex) {
                                        }
                                        FileBrowserFrame.this.closeDialog();
                                        MidletMain.screen.repaint();
                                    }
                                }.start();
                                // MidletMain.setCurrentWindow(FileBrowserFrame.this.s_prevWindow);
                            } catch (IOException ex) {
                                // ex.printStackTrace();
                                MidletMain.logger.outMessage("Local file error: " + ex.getMessage(), true);
                            }

                        }
                    }
                }
            };
            soft.rightSoft.addSubItem(infoPopupItem);
        }
        if (manType == 0x01) {
            PopupItem selectItem = new PopupItem(Localization.getMessage("SELECT_ITEM")) {

                public void actionPerformed() {
                    String __selectedString = ((ListItem) filesList.items.elementAt(filesList.selectedIndex)).title;
                    if (__selectedString.hashCode() != "...".hashCode()) {
                        boolean isFolder = __selectedString.endsWith("/");

                        if (!isFolder) {
                        } else {
                            if (manType == 0x01) {
                                MidletMain.settingsFrame.acceptFilesFolder.setText("/".concat(systemPath).concat(__selectedString));
                                MidletMain.settingsFrame.acceptFilesFolder.updateCaption();
                                MidletMain.settingsFrame.prepareGraphics();
                                MidletMain.screen.setActiveWindow(MidletMain.settingsFrame);
                                return;
                            }
                        }
                    } else {
                        if (manType == 0x01) {
                            MidletMain.settingsFrame.acceptFilesFolder.setText("/".concat(systemPath));
                            MidletMain.settingsFrame.acceptFilesFolder.updateCaption();
                            MidletMain.settingsFrame.prepareGraphics();
                            MidletMain.screen.setActiveWindow(MidletMain.settingsFrame);
                            return;
                        }
                    }
                }
            };
            soft.rightSoft.addSubItem(selectItem);
        }

        filesList = new List();
        setGObject(filesList);

        readLevel(systemPath);
    }

    /**
     * FS working methods
     */
    public void getLowerLevel() {
        String lastPath = new String();
        String newPath = "";
        for (int c = 0; c < systemPath.length(); c++) {
            if (systemPath.charAt(c) == '/') {
                lastPath = newPath;
                char pathGet[] = new char[c];
                systemPath.getChars(0, c, pathGet, 0);
                newPath = String.valueOf(pathGet);
            }
        }
        systemPath = lastPath + "/";

    }

    public final boolean readLevel(String levelPath) {
        if (levelPath.length() > 1) {
            return readFiles(levelPath);
        } else {
            readRoots();
            return true;
        }
    }

    public boolean readFiles(String filePath) {
        try {
            fileConnection = (FileConnection) Connector.open("file:///" + filePath, 1);
            MidletMain.logger.outMessage("Establising: " + "file:///" + filePath + ". Is direcory: " + fileConnection.isDirectory());
            if (fileConnection.isDirectory()) {
                outEnumeration(fileConnection.list());
                return true;
            } else {
                return false;
                //Файл
                //fileReader filereader = new fileReader(fileConnection);
                //Thread thread = new Thread(filereader);
                //thread.start();
                //pumptask.textField2.setString(fileConnection.getPath());
            }
        } catch (IOException ex) {
            //cList.addItem(new MenuListItem(ex.getMessage() + ": " + systemPath, UIIconType.FILES, UIIconType.FILES_FILE, true));
            ex.printStackTrace();
            return false;
        }
    }

    public void readRoots() {
        outEnumeration(javax.microedition.io.file.FileSystemRegistry.listRoots());
        systemPath = "";
    }

    public void outEnumeration(Enumeration enumeration) {
        try {
            Vector files = new Vector();
            filesList.items.removeAllElements();

            ListItem upItem = new ListItem("...");
            upItem.imageFileHash = IconsType.HASH_FILES;
            upItem.imageIndex = IconsType.FILES_UP;
            filesList.addItem(upItem);

            byte lType = -1;
            int image = IconsType.FILES_FOLDER;
            while (enumeration.hasMoreElements()) {

                String nextelement = (String) enumeration.nextElement();

                boolean isDirectory;
                if (nextelement.charAt(nextelement.length() - 1) == '/') {
                    isDirectory = true;
                    image = IconsType.FILES_FOLDER;
                } else {
                    isDirectory = false;
                    image = IconsType.FILES_FILE;
                }

                if (isDirectory && systemPath.length() > 1) {
                    //if (lType != 0) {
                    image = IconsType.FILES_FOLDER;
                    lType = 0;
                    //}
                } else if (!isDirectory) {
                    image = IconsType.FILES_FILE;
                } else {
                    //if (lType != 2) {
                    image = IconsType.FILES_DISK;
                    lType = 2;
                    //}
                }
                ListItem menuItem =
                        new ListItem(nextelement);
                menuItem.imageFileHash = IconsType.HASH_FILES;
                menuItem.imageIndex = image;
                if (image != IconsType.FILES_FILE) {
                    filesList.addItem(menuItem);
                } else {
                    files.addElement(menuItem);
                }

            }
            switch (manType) {
                case 0x00: {
                    break;
                }
                case 0x01: {
                    files = new Vector();
                    break;
                }
                case 0x02: {
                    files = new Vector();
                    break;
                }
                default: {
                    break;
                }
            }
            for (int c = 0; c < files.size(); c++) {
                filesList.addItem((ListItem) files.elementAt(c));
            }
            filesList.selectedIndex = 0;

        } catch (java.lang.IndexOutOfBoundsException ex1) {
        }
        try {
            if (filesList.items.size() >= 0) {
                filesList.selectedIndex = 0;
            }
        } catch (Throwable ex1) {
        }
    }
}
