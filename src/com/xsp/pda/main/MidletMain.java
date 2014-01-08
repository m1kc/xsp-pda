package com.xsp.pda.main;

import com.tomclaw.datagear.DataGear;
import com.tomclaw.datagear.GroupNotFoundException;
import com.tomclaw.datagear.IncorrectValueException;
import com.tomclaw.datagear.RecordFile;
import com.tomclaw.images.Splitter;
import com.tomclaw.tcuilite.Screen;
import com.tomclaw.tcuilite.Settings;
import com.tomclaw.tcuilite.smiles.Smiles;
import com.xsp.pda.core.Connection;
import com.xsp.pda.core.Session;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.midlet.*;

/**
 * @author solkin
 */
public class MidletMain extends MIDlet {

    /** Main **/
    static MidletMain midletMain;
    static Screen screen;
    public static Logger logger;
    static Session session;
    /** Frames **/
    static MainFrame mainFrame;
    static SettingsFrame settingsFrame;
    public static DirectTransferFrame directTransferFrame;
    public static String fileLocation = null;
    /** Settings **/
    static String settingsResFile = "xsp_settings.ini";
    static String host = "m1kc.dyndns.org";
    static int port = 3214;
    static String myIp = "localhost";
    static int timeOffset = 0;
    static int dls = 0;
    static int gmtOffset = 0;
    /** DataGear instance **/
    static DataGear settings;
    static String[][] addrList = new String[0][2];
    static String inFolder = "";
    static boolean autoReceive = false;

    public void startApp() {
        /** Fixing midlet instance **/
        midletMain = this;
        /** Screen instance **/
        Settings.MENU_DRAW_ALPHABACK = false;
        Settings.MENU_DRAW_DIRECTSHADOWS = false;
        Settings.DIALOG_DRAW_ALPHABACK = false;
        Settings.DIALOG_DRAW_SHADOWS=false;
        screen = new Screen(this);
        /** Displaying scren **/
        screen.show();
        /** Temporary logger **/
        logger = new Logger(true, false, "127.0.0.1", 2000, false, "/root1/");
        /** Loading frames **/
        Smiles.readSmileData();
        Splitter.splitImage("/res/img_files.png");
        settings = new DataGear();
        loadRmsData();
        updateSettings();
        mainFrame = new MainFrame();
        settingsFrame = new SettingsFrame();
        directTransferFrame = new DirectTransferFrame();
        mainFrame.s_nextWindow = settingsFrame;
        mainFrame.s_prevWindow = directTransferFrame;
        settingsFrame.s_prevWindow = mainFrame;
        directTransferFrame.s_nextWindow = mainFrame;
        /** Settings active frame **/
        screen.setActiveWindow(mainFrame);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    /** Settings **/
    public static void loadRmsData() {
        try {
            if (RecordFile.getRecordsCount(settingsResFile) > 0) {
                RecordFile.readFile(settingsResFile, settings);
                // settings.exportToIni(System.out);
                MidletMain.logger.outMessage("Settings read");
                return;
            }
        } catch (Throwable ex) {
            logger.outMessage("Rms data unexist: " + ex.getMessage() + ". Loading Res");
        }
        loadResData();
        saveRmsData();
    }

    public static void saveRmsData() {
        saveRmsData(settingsResFile, MidletMain.settings);
    }

    public static void saveRmsData(String fileName, DataGear dataGear) {
        logger.outMessage("saveRmsData( " + fileName + " )");
        try {
            RecordFile.removeFile(fileName);
        } catch (Throwable ex) {
            logger.outMessage("RMS IOException: \"" + ex.getMessage() + "\" on write. File: [" + fileName + "]");
        }
        try {
            logger.outMessage("RMS index: " + RecordFile.saveFile(fileName, dataGear, false));
        } catch (IOException ex) {
            logger.outMessage("RMS IOException: \"" + ex.getMessage() + "\" on write. File: [" + fileName + "]");
        }
        System.gc();
    }

    public static void loadResData() {
        try {
            settings.importFromIni(new DataInputStream(Class.forName("com.xsp.pda.main.MidletMain").getResourceAsStream("/res/".concat(settingsResFile))));
        } catch (ClassNotFoundException ex) {
        } catch (IOException ex) {
        } catch (IncorrectValueException ex) {
        } catch (GroupNotFoundException ex) {
        }
    }

    public static void updateSettings() {
        try {
            Hashtable network = settings.getGroup("Network");
            Enumeration address = network.keys();
            addrList = new String[network.size()][2];
            for (int c = 0; c < network.size(); c++) {
                String s_Key = (String)address.nextElement();
                String s_Port = (String)network.get(s_Key);
                addrList[c][0] = s_Key;
                addrList[c][1] = s_Port;
            }
        } catch (IncorrectValueException ex) {
        } catch (GroupNotFoundException ex) {
        }
        inFolder = getString(settings, "Filetransfer", "folder");
        autoReceive = getBoolean(settings, "Filetransfer", "autoReceive");
    }

    public static boolean getBoolean(DataGear dataGear, String groupName, String itemName) {
        try {
            return dataGear.getValue(groupName, itemName).equals("true") ? true : false;
        } catch (Throwable ex) {
            return false;
        }
    }

    public static String getString(DataGear dataGear, String groupName, String itemName) {
        try {
            return dataGear.getValue(groupName, itemName);
        } catch (Throwable ex) {
            return "";
        }
    }

    public static int getInteger(DataGear dataGear, String groupName, String itemName) {
        try {
            return Integer.parseInt(dataGear.getValue(groupName, itemName));
        } catch (Throwable ex) {
            return 0;
        }
    }

    public static long getLong(DataGear dataGear, String groupName, String itemName) {
        try {
            return Long.parseLong(dataGear.getValue(groupName, itemName));
        } catch (Throwable ex) {
            return 0;
        }
    }
}
