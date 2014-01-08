package com.xsp.pda.core;


/**
 *
 * @author m1kc
 */
public interface UIProxy
{
    /**
     * Errors
     * @param ex
     */
    public void errorWhileSending(Throwable ex);

    /**
     * Packet parsing
     * @param type
     * @param subtype
     * @param utf
     * @param bytes
     */
    public void packReceived(int type, int subtype, String[] utf, byte[] bytes);
    public void packSent(int type, int subtype, String[] utf, byte[] bytes);

    /**
     * Error packet parsing
     * @param type
     * @param subtype
     */
    public void errorUnknownType(int type, int subtype);

    /**
     * Packet parsing
     */
    public void handleService(int subtype, String[] body, byte[] bytes);
    public void handlePing(int subtype, String[] s, byte[] bytes);
    public void handleCapsCheck(int subtype, String[] body, byte[] bytes);
    public void handleMessage(int subtype, String[] s, byte[] bytes);
    public void handleTerminal(int subtype, String[] body, byte[] bytes);
    public void handleFile(int subtype, String[] body, byte[] bytes);
    public void handleMicrophone(int subtype, String[] body, byte[] bytes);
    public void handleDialog(int subtype, String[] body, byte[] bytes);
    public void handleMouse(int subtype, String[] body, byte[] bytes);
    public void handleScreen(int subtype, String[] body, byte[] bytes);

    /**
     * DirectTransfer
     */ 
    public void sendProgress(long sent, long size, int speed);
    public void sendDone();
    public void receiveProgress(long got, long size, int speed);
    public void receiveDone();
}
