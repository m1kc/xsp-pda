package com.xsp.pda.core;

/**
 *
 * @author m1kc
 */
public interface XSPConstants {

    // Типы пакетов
    final static int SERVICE = 0;
    final static int PING = 1;
    final static int CAPSCHECK = 2;
    final static int MESSAGE = 3;
    final static int TERMINAL = 4;
    final static int FILE = 5;
    final static int MICROPHONE = 6;
    final static int DIALOG = 7;
    // OLD
    final static int MOUSE = 1513;
    final static int SCREEN = 1514;
    // Подтипы
    // Generic
    final static int UNKNOWN = 0;
    // PING
    final static int CALL = 0;
    final static int ANSWER = 1;
    // CAPSCHECK
    final static int ASK = 0;
    final static int TELL = 1;
    // TERMINAL
    final static int FULL = 0;
    final static int ADD = 1;
    final static int DEL = 2;
    // FILE, MICROPHONE, DIALOG
    final static int REQUEST = 0;
    final static int AGREE = 1;
    final static int DISAGREE = 2;
    // MICROPHONE, DIALOG
    final static int STOP = 3;
    // Возможности клиента
    final static String[] CAPS = {"PDA", "XSP 1.0", "J2ME",
        "m1kc project & TomClaw Software", "PING", "MESSAGE", "CAPSCHECK", "FILE"};
}
