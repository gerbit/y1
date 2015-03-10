package me.gerbit.twitter.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");

    /** Writes data {@param s} to a connection */
    public static int write(HttpsURLConnection connection, String s) {
        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            wr.write(s);
            wr.flush();
            wr.close();
            return connection.getResponseCode();
        } catch (IOException e) {
            return -1; // TODO rework method returns
        }
    }

    /**
     *  Reads a response for a given connection
     */
    public static String read(HttpsURLConnection connection) {
        String ret = null;
        try {
            StringBuilder str = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
