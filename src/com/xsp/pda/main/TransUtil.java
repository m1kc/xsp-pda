package com.xsp.pda.main;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2010
 * http://www.tomclaw.com/
 * @author Игорь
 */
public class TransUtil {
    public TransUtil() {
    }

    //Allocation Util
    public final static int BYTE_ARRAY_SIZE_MAX = 10240 - 1;

    public static byte[] createByteArray(int size) {
        return new byte[size];
    }

    static public byte[] explodeToBytes(String text, char serparator, int radix) {
        String[] strings = explode(text, serparator);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        for (int i = 0; i < strings.length; i++) {
            String item = strings[i];
            if (item.charAt(0) == '*') {
                for (int j = 1; j < item.length(); j++) {
                    bytes.write((byte) item.charAt(j));
                }
            } else {
                bytes.write(Integer.parseInt(item, radix));
            }
        }
        return bytes.toByteArray();
    }

    static public String[] explode(String text, char serparator) {
        Vector tmp = new Vector();
        StringBuffer strBuf = new StringBuffer();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char chr = text.charAt(i);
            if (chr == serparator) {
                tmp.addElement(strBuf.toString());
                strBuf.delete(0, strBuf.length());
            } else {
                strBuf.append(chr);
            }
        }
        tmp.addElement(strBuf.toString());
        String[] result = new String[tmp.size()];
        tmp.copyInto(result);
        return result;
    }
}
