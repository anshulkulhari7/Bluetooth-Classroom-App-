package com.example.multiplebt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Util
{


    public static byte[] concat(byte[] first, byte[] second){
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
