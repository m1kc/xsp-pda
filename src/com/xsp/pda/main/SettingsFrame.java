package com.xsp.pda.main;

import com.tomclaw.datagear.GroupNotFoundException;
import com.tomclaw.datagear.IncorrectValueException;
import com.tomclaw.tcuilite.Check;
import com.tomclaw.tcuilite.Field;
import com.tomclaw.tcuilite.GObject;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Label;
import com.tomclaw.tcuilite.List;
import com.tomclaw.tcuilite.ListItem;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Tab;
import com.tomclaw.tcuilite.TabEvent;
import com.tomclaw.tcuilite.TabItem;
import com.tomclaw.tcuilite.Window;

/**
 *
 * @author solkin
 */
public final class SettingsFrame extends Window {

    private Tab settingsTab;
    private GObject[] settingsPane;
    private PopupItem rightMenu;
    private PopupItem saveSettings;
    /** Address **/
    private List addrList;
    /** File transfer **/
    private Check autoAcceptFiles;
    public Field acceptFilesFolder;

    public SettingsFrame() {
        super(MidletMain.screen);
        /** Header **/
        header = new Header("Settings");
        /** Soft **/
        soft = new Soft(MidletMain.screen);
        soft.leftSoft = new PopupItem("Back") {

            public void actionPerformed() {
                MidletMain.screen.setActiveWindow(s_prevWindow);
            }
        };
        rightMenu = new PopupItem("Menu");
        rightMenu.addSubItem(new PopupItem("Append address") {

            public void actionPerformed() {
                AddressFormFrame addressFormFrame = new AddressFormFrame(0x01);
                addressFormFrame.s_prevWindow = SettingsFrame.this;
                MidletMain.screen.setActiveWindow(addressFormFrame);
            }
        });
        rightMenu.addSubItem(new PopupItem("Remove address") {

            public void actionPerformed() {
                addrList.items.removeElementAt(addrList.selectedIndex);
                addrList.selectedIndex = addrList.items.size()-1;
                MidletMain.screen.repaint();
            }
        });
        saveSettings = (new PopupItem("Save") {

            public void actionPerformed() {
                saveSettings();
                MidletMain.screen.setActiveWindow(s_prevWindow);
            }
        });
        rightMenu.addSubItem(saveSettings);

        soft.rightSoft = rightMenu;
        /** Objects **/
        settingsTab = new Tab();
        settingsTab.tabEvent = new TabEvent() {

            public void stateChanged(int i, int i1, int i2) {
                settingsTab.setGObject(settingsPane[i1]);
                switch (i1) {
                    case 0x00: {
                        soft.rightSoft = rightMenu;
                        break;
                    }
                    case 0x01: {
                        soft.rightSoft = saveSettings;
                    }
                }
            }
        };
        updateTabs();
        settingsTab.setGObject(settingsPane[0]);
        /** Settings GObject **/
        setGObject(settingsTab);
    }

    public void updateTabs() {
        settingsPane = new GObject[2];
        /** Addresses **/
        settingsTab.addTabItem(new TabItem("Address", 0, -1));
        addrList = new List();
        settingsPane[0] = addrList;
        settingsPane[0].setTouchOrientation(MidletMain.screen.isPointerEvents);
        for (int c = 0; c < MidletMain.addrList.length; c++) {
            ((List) settingsPane[0]).addItem(new ListItem(MidletMain.addrList[c][0] + ":" + MidletMain.addrList[c][1]));
        }
        /** Filetransfer */
        settingsTab.addTabItem(new TabItem("File transfer", 0, -1));
        settingsPane[1] = new Pane(null, false);
        settingsPane[1].setTouchOrientation(MidletMain.screen.isPointerEvents);
        settingsPane[1].setTouchOrientation(MidletMain.screen.isPointerEvents);
        Label label6 = new Label("DirectTransfer protocol settings. Enter here a folder to store incoming files, e.g. /e:/ . Also, you can receive files automatically. ");
        label6.isTitle = true;
        ((Pane) settingsPane[1]).addItem(label6);
        autoAcceptFiles = new Check("Auto accept files", MidletMain.getBoolean(MidletMain.settings, "Filetransfer", "autoReceive"));
        autoAcceptFiles.setFocusable(true);
        autoAcceptFiles.setFocused(true);
        ((Pane) settingsPane[1]).addItem(autoAcceptFiles);
        ((Pane) settingsPane[1]).addItem(new Label("Folder to store incoming files:"));
        acceptFilesFolder = new Field(MidletMain.getString(MidletMain.settings, "Filetransfer", "folder"));
        acceptFilesFolder.setFocusable(true);
        ((Pane) settingsPane[1]).addItem(acceptFilesFolder);
    }

    public void appendAddress(String host, String port) {
        addrList.addItem(new ListItem(host.concat(":").concat(port)));
        prepareGraphics();
    }

    public void saveSettings() {
        try {
            /** Network **/
            MidletMain.settings.addGroup("Network");
            String tempString;
            for (int c = 0; c < addrList.items.size(); c++) {
                tempString = ((ListItem) addrList.items.elementAt(c)).title;
                MidletMain.settings.addItem("Network", tempString.substring(0, tempString.indexOf(":")), tempString.substring(tempString.indexOf(":") + 1));
            }
            /** Filetransfer **/
            MidletMain.settings.addGroup("Filetransfer");
            MidletMain.settings.addItem("Filetransfer", "autoReceive", autoAcceptFiles.state ? "true" : "false");
            MidletMain.settings.addItem("Filetransfer", "folder", acceptFilesFolder.getText());

            MidletMain.saveRmsData();
            MidletMain.updateSettings();
        } catch (GroupNotFoundException ex) {
            ex.printStackTrace();
        } catch (IncorrectValueException ex) {
            ex.printStackTrace();
        }
    }
}
