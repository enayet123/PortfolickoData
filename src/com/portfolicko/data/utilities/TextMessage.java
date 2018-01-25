package com.portfolicko.data.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by enayethussain on 27/12/2017.
 */
public class TextMessage {
    public static String sendSms(String numb, String msg) {
        try {
            // Construct data
            String user = "username=" + "enayet32808@gmail.com";
            String hash = "&hash=" + "db2facf992aad59ba1aaf3c680b83bf9399752302f77083fc69982847fbce9f5";
            String message = "&message=" + msg;
            String sender = "&sender=" + "Portfolicko";
            String numbers = "&numbers=" + numb;
            String test = "&test=" + "0";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("http://api.txtlocal.com/send/?").openConnection();
            String data = user + hash + numbers + message + sender + test;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }
}
