package com.xsp.pda.main;

import com.tomclaw.tcuilite.ChatItem;
import com.tomclaw.tcuilite.Dialog;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.KeyEvent;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Screen;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;
import com.xsp.pda.core.Connection;
import com.xsp.pda.core.DirectTransfer;
import com.xsp.pda.core.Sender;
import com.xsp.pda.core.UIProxy;
import com.xsp.pda.core.XSPConstants;
import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author solkin
 */
public class MainFrame extends Window implements XSPConstants, UIProxy {

    /** Objects **/
    public Pane chatPane;
    /** Chat **/
    public TextBox textBox;
    public int maxSize = 2048;
    /** Other **/
    public long pingTime;

    public MainFrame() {
        super(MidletMain.screen);
        /** Header **/
        header = new Header("XSP PDA");
        /** Soft **/
        soft = new Soft(MidletMain.screen);

        final PopupItem connectPopupItem = new PopupItem("Connection");
        final PopupItem disconnectPopupItem = new PopupItem("Disconnect") {

            public void actionPerformed() {
                try {
                    MidletMain.session.closeStreams();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        final PopupItem clientPopupItem = new PopupItem("Connect as client");
        final PopupItem serverPopupItem = new PopupItem("Create server") {

            public void actionPerformed() {
                new Thread() {

                    public void run() {
                        MainFrame.this.showDialog(new Dialog("Detecting IP...",
                                "Detecting your connection IP address",
                                MidletMain.screen.getWidth(),
                                MidletMain.screen.getHeight()));
                        MidletMain.screen.repaint();
                        try {
                            MidletMain.myIp = Connection.requestMyIp();
                            MainFrame.this.showDialog(new Dialog("Waiting for client...",
                                    "Your IP: " + MidletMain.myIp,
                                    MidletMain.screen.getWidth(),
                                    MidletMain.screen.getHeight()));
                            MidletMain.screen.repaint();
                            MidletMain.session = Connection.connectAsServer(MidletMain.port, MainFrame.this);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        MainFrame.this.closeDialog();
                        MidletMain.screen.repaint();
                    }
                }.start();
            }
        };

        soft.leftSoft = new PopupItem("Menu") {

            public void actionPerformed() {
                if (!clientPopupItem.isEmpty()) {
                    clientPopupItem.subPopup.items.removeAllElements();
                }
                clientPopupItem.addSubItem(new PopupItem("Enter address...") {

                    public void actionPerformed() {
                        AddressFormFrame addressFormFrame = new AddressFormFrame(0x00);
                        addressFormFrame.s_prevWindow = MainFrame.this;
                        MidletMain.screen.setActiveWindow(addressFormFrame);
                    }
                });
                for (int c = 0; c < MidletMain.addrList.length; c++) {
                    final String t_host = MidletMain.addrList[c][0];
                    final String t_port = MidletMain.addrList[c][1];
                    clientPopupItem.addSubItem(new PopupItem(t_host.concat(":").concat(t_port)) {

                        public void actionPerformed() {
                            MidletMain.host = t_host;
                            MidletMain.port = Integer.parseInt(t_port);
                            MidletMain.logger.outMessage(MidletMain.host.concat(":").concat(String.valueOf(MidletMain.port)));
                            MidletMain.session = Connection.connectAsClient(MidletMain.host, MidletMain.port, MainFrame.this);
                        }
                    });
                }
                clientPopupItem.subPopup.prepareBackground();
                if (!connectPopupItem.isEmpty()) {
                    connectPopupItem.subPopup.items.removeAllElements();
                }
                if (MidletMain.session == null || !MidletMain.session.isAlive) {
                    connectPopupItem.addSubItem(serverPopupItem);
                    connectPopupItem.addSubItem(clientPopupItem);
                } else {
                    connectPopupItem.addSubItem(disconnectPopupItem);
                }
                connectPopupItem.subPopup.selectedIndex = 0;
            }
        };
        soft.leftSoft.addSubItem(connectPopupItem);
        soft.leftSoft.addSubItem(new PopupItem("Ping remote") {

            public void actionPerformed() {
                if (MidletMain.session != null && MidletMain.session.isAlive()) {
                    showDialog(new Dialog("Ping...", "Ping in progress, wait a while.", MidletMain.screen.getWidth(), MidletMain.screen.getHeight()));
                    MidletMain.screen.repaint(Screen.REPAINT_STATE_PLAIN);
                    pingRemote();
                }
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("Direct transfer") {

            public void actionPerformed() {
                MidletMain.screen.setActiveWindow(MidletMain.directTransferFrame);
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("Send file") {

            public void actionPerformed() {
                if (MidletMain.session != null && MidletMain.session.isAlive()) {
                    FileBrowserFrame fileBrowserFrame = new FileBrowserFrame(0x00);
                    fileBrowserFrame.s_prevWindow = MainFrame.this;
                    MidletMain.screen.setActiveWindow(fileBrowserFrame);
                }
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("Request caps") {

            public void actionPerformed() {
                if (MidletMain.session != null && MidletMain.session.isAlive()) {
                    showDialog(new Dialog("Capscheck...", "Caps checking in progress.", MidletMain.screen.getWidth(), MidletMain.screen.getHeight()));
                    MidletMain.screen.repaint(Screen.REPAINT_STATE_PLAIN);
                    Sender.sendPack(MidletMain.session.os, CAPSCHECK, ASK, MainFrame.this);
                }
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("Settings") {

            public void actionPerformed() {
                MidletMain.screen.setActiveWindow(s_nextWindow);
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("About") {

            public void actionPerformed() {
                showDialog(new Dialog("About",
                        "XSP PDA - XSP protocol client for J2ME devices. TomClaw Software & m1kc project. \nhttp://sourceforge.net/projects/m1kc-xsp/\nVersion " + MidletMain.midletMain.getAppProperty("MIDlet-Version") + "\nBuild " + MidletMain.midletMain.getAppProperty("Build"), MidletMain.screen.getWidth(), MidletMain.screen.getHeight()));
                prepareGraphics();
                MidletMain.screen.repaint();
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("Minimize") {

            public void actionPerformed() {
                Display.getDisplay(MidletMain.midletMain).setCurrent(null);
            }
        });
        soft.leftSoft.addSubItem(new PopupItem("Exit") {

            public void actionPerformed() {
                MidletMain.midletMain.notifyDestroyed();
            }
        });
        soft.rightSoft = new PopupItem("Chat");
        soft.rightSoft.addSubItem(new PopupItem("Write") {

            public void actionPerformed() {
                MainFrame.this.getKeyEvent("EVENT_START_DIALOG").actionPerformed();
            }
        });
        soft.rightSoft.addSubItem(new PopupItem("Clear chat") {

            public void actionPerformed() {
                chatPane.items.removeAllElements();
            }
        });
        /** Object **/
        chatPane = new Pane(this, true);
        /** GObject **/
        setGObject(chatPane);

        /** Events **/
        addKeyEvent(new KeyEvent(Screen.FIRE, "EVENT_START_DIALOG", false) {

            public void actionPerformed() {
                Display.getDisplay(MidletMain.midletMain).setCurrent(textBox);
            }
        });
        textBox = new TextBox("", "", maxSize, TextField.ANY);
        textBox.addCommand(new Command("Send", Command.OK, 4));
        textBox.addCommand(new Command("Clear", Command.CANCEL, 1));
        textBox.addCommand(new Command("Back", Command.BACK, 3));
        textBox.setCommandListener(new CommandListener() {

            public void commandAction(Command c, Displayable d) {
                switch (c.getCommandType()) {
                    case Command.OK: {
                        if (MidletMain.session.isAlive()) {
                            /** Account is online **/
                            Sender.sendPack(MidletMain.session.os, XSPConstants.MESSAGE, XSPConstants.UNKNOWN, textBox.getString(), new byte[]{}, MainFrame.this);
                            MainFrame.this.addChatItem(textBox.getString(), false);
                            textBox.setString("");
                        }

                        // MidletMain.screen.setFullScreenMode(true);
                        MainFrame.this.prepareGraphics();
                        Display.getDisplay(MidletMain.midletMain).setCurrent(MidletMain.screen);
                        MidletMain.screen.setFullScreenMode(true);
                        MidletMain.screen.repaint(Screen.REPAINT_STATE_PLAIN);
                        break;
                    }
                    case Command.CANCEL: {
                        textBox.setString("");
                        break;
                    }
                    case Command.BACK: {
                        MidletMain.screen.setFullScreenMode(true);
                        MainFrame.this.prepareGraphics();
                        Display.getDisplay(MidletMain.midletMain).setCurrent(MidletMain.screen);
                        MidletMain.screen.setFullScreenMode(true);
                        MidletMain.screen.repaint(Screen.REPAINT_STATE_PLAIN);
                        break;
                    }
                }
            }
        });
        capKeyEvent = new KeyEvent(0, "", false) {

            public void actionPerformed() {
                if (Screen.getExtGameAct(keyCode) == Screen.KEY_CODE_LEFT_MENU
                        || Screen.getExtGameAct(keyCode) == Screen.KEY_CODE_RIGHT_MENU) {
                    if (dialog != null) {
                        MainFrame.this.closeDialog();
                        prepareGraphics();
                        MidletMain.screen.repaint();
                    }
                }
            }
        };
    }

    public void pingRemote() {
        MidletMain.logger.outMessage("Trying to ping...");
        pingTime = System.currentTimeMillis();
        Sender.sendPack(MidletMain.session.os, PING, CALL, this);
    }

    public void errorWhileSending(Throwable ex) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void packReceived(int type, int subtype, String[] utf, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void packSent(int type, int subtype, String[] utf, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void errorUnknownType(int type, int subtype) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void handleService(int subtype, String[] body, byte[] bytes) {
        for (int i = 0; i < body.length; i++) {
            MidletMain.logger.outMessage("Сервисное сообщение: " + body[i]);
        }
    }

    public void handlePing(int subtype, String[] s, byte[] bytes) {
        switch (subtype) {
            case CALL:
                Sender.sendPack(MidletMain.session.os, PING, ANSWER, this);
                MidletMain.logger.outMessage("Командир, нас пингуют!");
                break;
            case ANSWER:
                long t = System.currentTimeMillis() - pingTime;
                MidletMain.logger.outMessage("Ping OK, time: " + t + " ms.");
                showDialog(new Dialog("Ping OK", "Time: " + t + " ms.", MidletMain.screen.getWidth(), MidletMain.screen.getHeight()));
                prepareGraphics();
                MidletMain.screen.repaint();
                break;
            default:
                MidletMain.logger.outMessage("PING: What the...?");
                break;
        }
    }

    public void handleCapsCheck(int subtype, String[] body, byte[] bytes) {
        switch (subtype) {
            case ASK:
                /*boolean flag = false;
                for (int i = 0; i < CAPS.length; i++) {
                if (CAPS[i].toUpperCase().hashCode() == body[0].toUpperCase().hashCode()) {
                flag = true;
                }
                }
                if (flag) {
                Sender.sendPack(MidletMain.session.os, CAPSCHECK, SUPPORTED, body[0], null, this);
                } else {
                Sender.sendPack(MidletMain.session.os, CAPSCHECK, NOT_SUPPORTED, body[0], null, this);
                }*/
                Sender.sendPack(MidletMain.session.os, CAPSCHECK, TELL, CAPS, null, this);
                break;
            case TELL:
                String string = "Device type: " + body[0]+"\n";
                string+="Client: " + body[1]+"\n";
                string+="Implementation: " + body[2]+"\n";
                string+="Author: " + body[3]+"\n";
                string+="Supported caps: \n";
                for (int c = 4; c < body.length; c++) {
                    string+=body[c]+"\n";
                }
                MidletMain.logger.outMessage(string);
                showDialog(new Dialog("Capscheck OK", string, MidletMain.screen.getWidth(), MidletMain.screen.getHeight()));
                prepareGraphics();
                MidletMain.screen.repaint();

                break;
            default:
                MidletMain.logger.outMessage("CAPSCHECK: What the...?");
                break;
        }
    }

    public void handleMessage(int subtype, String[] s, byte[] bytes) {
        addChatItem(s[0], true);
    }

    public void handleTerminal(int subtype, String[] body, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void handleFile(int subtype, String[] body, byte[] bytes) {
        switch (subtype) {
            case REQUEST:
                //if (jCheckBox1.isSelected()) {
                Sender.sendPack(MidletMain.session.os, FILE, AGREE, body, null, this);
                receiveFile();
                //} else {
                //    Sender.sendPack(os, FILE, DISAGREE, body, null, this);
                //}
                break;
            case AGREE:
                // sendFile();
                new Thread() {

                    public void run() {
                        if (MidletMain.fileLocation != null) {
                            MidletMain.logger.outMessage(MidletMain.fileLocation);
                            DirectTransfer.sendFile(MidletMain.fileLocation, MidletMain.session.fos, 0, MidletMain.mainFrame);
                        }
                    }
                }.start();
                break;
            case DISAGREE:
                MidletMain.logger.outMessage("Передача отменена принимающим.");
                break;
            default:
                MidletMain.logger.outMessage("FILE: What the...?");
                break;
        }
    }

    public void handleMicrophone(int subtype, String[] body, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void handleDialog(int subtype, String[] body, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void handleMouse(int subtype, String[] body, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void handleScreen(int subtype, String[] body, byte[] bytes) {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    public void sendProgress(long sent, long size, int speed) {
        MidletMain.directTransferFrame.transSize.setCaption(String.valueOf(sent));
        MidletMain.directTransferFrame.transSize.updateCaption();
        MidletMain.directTransferFrame.fileSize.setCaption(String.valueOf(size));
        MidletMain.directTransferFrame.fileSize.updateCaption();
        MidletMain.directTransferFrame.transSpeed.setCaption(String.valueOf(speed));
        MidletMain.directTransferFrame.transSpeed.updateCaption();
        MidletMain.directTransferFrame.fileProgress.setValue((int) (100 * sent / size));
        if (MidletMain.screen.activeWindow.equals(MidletMain.directTransferFrame)) {
            MidletMain.directTransferFrame.prepareGraphics();
            MidletMain.screen.repaint();
        }
    }

    public void sendDone() {
        MidletMain.directTransferFrame.fileProgress.setValue(100);
        MidletMain.directTransferFrame.fileProgress.caption = "Done.";
        if (MidletMain.screen.activeWindow.equals(MidletMain.directTransferFrame)) {
            MidletMain.directTransferFrame.prepareGraphics();
            MidletMain.screen.repaint();
        }
    }

    public void receiveProgress(long got, long size, int speed) {
        sendProgress(got, size, speed);
    }

    public void receiveDone() {
        MidletMain.logger.outMessage("Not supported yet.");
    }

    private void addChatItem(String string, boolean isIncoming) {
        string = string.concat(" ");
        ChatItem chatItem = new com.tomclaw.tcuilite.ChatItem(chatPane, string);
        chatItem.dlvStatus = isIncoming ? ChatItem.DLV_STATUS_INCOMING : ChatItem.DLV_STATUS_NOT_SENT;
        chatItem.cookie = new byte[]{0, 0, 0, 0, 0, 0};
        chatItem.itemType = 0;
        chatItem.buddyNick = isIncoming ? MidletMain.host : MidletMain.myIp;
        chatItem.buddyId = isIncoming ? MidletMain.host : MidletMain.myIp;
        // To history must be saved whole date
        chatItem.itemDateTime = TimeUtil.getDateString(true);

        chatPane.addItem(chatItem);
        MainFrame.this.prepareGraphics();
        MidletMain.screen.repaint();
    }

    public void receiveFile() {
        new Thread() {

            public void run() {
                DirectTransfer.receiveFile(MidletMain.inFolder, MidletMain.session.fis, MainFrame.this);
            }
        }.start();
    }
}
