/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xsp.pda.main;

import com.tomclaw.tcuilite.Gauge;
import com.tomclaw.tcuilite.Header;
import com.tomclaw.tcuilite.Label;
import com.tomclaw.tcuilite.Pane;
import com.tomclaw.tcuilite.PopupItem;
import com.tomclaw.tcuilite.Soft;
import com.tomclaw.tcuilite.Window;

/**
 *
 * @author solkin
 */
public class DirectTransferFrame extends Window {

    public Label fileName;
    public Label transSpeed;
    public Label fileSize;
    public Label transSize;
    public Label timeElapsed;
    public Gauge fileProgress;

    public DirectTransferFrame() {
        super(MidletMain.screen);
        /** Header **/
        header = new Header("Direct transfer");
        /** Soft **/
        soft = new Soft(MidletMain.screen);
        soft.leftSoft = new PopupItem("Back") {
            public void actionPerformed() {
                MidletMain.screen.setActiveWindow(s_nextWindow);
            }
        };
        Pane pane = new Pane(null, false);
        fileName = new Label("n/a");
        transSpeed = new Label("0");
        fileSize = new Label("0");
        transSize = new Label("0");
        timeElapsed = new Label("n/a");
        addLabel(pane, "File name:");
        pane.addItem(fileName);
        addLabel(pane, "Speed:");
        pane.addItem(transSpeed);
        addLabel(pane, "File size:");
        pane.addItem(fileSize);
        addLabel(pane, "Transferred size:");
        pane.addItem(transSize);
        addLabel(pane, "Time elapsed:");
        pane.addItem(timeElapsed);
        fileProgress = new Gauge("Progress");
        pane.addItem(fileProgress);
        setGObject(pane);
    }

    public void addLabel(Pane pane, String title) {
        Label label1 = new Label(title);
        label1.isTitle = true;
        pane.addItem(label1);
    }
}
