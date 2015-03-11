package me.gerbit.twitter.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");

    /** Writes data {@param s} to a connection */
    public static boolean write(OutputStream outputStream, String s) {
        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(outputStream));
            wr.write(s);
            wr.flush();
            wr.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *  Reads a response for a given connection
     */
    public static String read(InputStream inputStream) {
        String ret = null;
        try {
            StringBuilder str = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = br.readLine()) != null) {
                str.append(line + LINE_SEPARATOR);
            }
            ret = str.toString();
        } catch (IOException e) {
            ret = "";
        }
        return ret;
    }
}
