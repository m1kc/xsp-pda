/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xsp.pda.main;

import com.tomclaw.tcuilite.Check;
import com.tomclaw.tcuilite.Field;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Label;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;
import com.xsp.pda.core.Connection;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author solkin
 */
public class AddressFormFrame extends Window {

    private Pane pane;
    private Field hostField;
    private Field portField;
    private Check saveCheck;

    public AddressFormFrame(final int invocationType) {
        super(MidletMain.screen);
        /** Header **/
        header = new Header("Address form");
        /** Soft **/
        soft = new Soft(MidletMain.screen);
        soft.leftSoft = new PopupItem("Back") {

            public void actionPerformed() {
                MidletMain.screen.setActiveWindow(s_prevWindow);
            }
        };
        soft.rightSoft = new PopupItem("") {

            public void actionPerformed() {
                switch (invocationType) {
                    case 0x00: {
                        MidletMain.host = hostField.getText();
                        MidletMain.port = Integer.parseInt(portField.getText());
                        MidletMain.logger.outMessage(MidletMain.host.concat(":").concat(String.valueOf(MidletMain.port)));
                        MidletMain.session = Connection.connectAsClient(MidletMain.host, MidletMain.port, MidletMain.mainFrame);
                        if (!saveCheck.state) {
                            break;
                        }
                    }
                    case 0x01: {
                        MidletMain.settingsFrame.appendAddress(hostField.getText(), portField.getText());
                        if (saveCheck.state) {
                            MidletMain.settingsFrame.saveSettings();
                        }
                        break;
                    }
                }
                MidletMain.screen.setActiveWindow(s_prevWindow);
            }
        };
        switch (invocationType) {
            case 0x00: {
                soft.rightSoft.title = "Start";
                break;
            }
            case 0x01: {
                soft.rightSoft.title = "Append";
                break;
            }
        }

        /** Objects **/
        pane = new Pane(null, false);
        pane.addItem(new Label("Host:"));
        hostField = new Field("");
        hostField.constraints = TextField.HYPERLINK;
        hostField.setFocusable(true);
        hostField.setFocused(true);
        pane.addItem(hostField);
        pane.addItem(new Label("Port:"));
        portField = new Field("3214");
        portField.constraints = TextField.NUMERIC;
        portField.setFocusable(true);
        pane.addItem(portField);
        saveCheck = new Check("Save address to list", false);
        if (invocationType == 0x00) {
            saveCheck.setFocusable(true);
            pane.addItem(saveCheck);
        }
        /** Setting GObject **/
        setGObject(pane);
    }
}
