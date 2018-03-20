package com.portfolicko.data.utilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Enayet Hussain on 27/12/2017.
 */
public class TextMessage {
    public static void main(String[] args) {
        Properties p = new Properties();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream("textapi.prop");
            p.load(fin);
            fin.close();
            PropertyData propertyData = new PropertyData(p);
            System.out.println(propertyData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (PropertyFileErrorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String sendSms(String numb, String msg) {
        try {
            // Load data
            Properties p = new Properties();
            FileInputStream fin = new FileInputStream("textapi.prop");
            p.load(fin);
            fin.close();
            PropertyData propertyData = new PropertyData(p);
            // Construct data
            String user = "username=" + propertyData.getUsername();
            String hash = "&hash=" + propertyData.getHash();
            String message = "&message=" + msg;
            String sender = "&sender=" + propertyData.getSender();
            String numbers = "&numbers=" + numb;
            String test = "&test=" + propertyData.getTest();

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
            while ((line = rd.readLine()) != null)
                stringBuffer.append(line);
            rd.close();
            System.out.format("-- Sending Text --\nRecipient(s): %s\nMessage:\n%s\n", numb, msg);
            return stringBuffer.toString();
        } catch (PropertyFileErrorException | FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
        return null;
    }
}

class PropertyData {
    private final Properties prop;
    private String username;
    private String hash;
    private String sender;
    private String test;

    public PropertyData(Properties prop) throws PropertyFileErrorException {
        this.prop = prop;
        if (nn("username") && nn("hash") && nn("sender") && nn("test")) {
            this.username = prop.getProperty("username");
            this.hash = prop.getProperty("hash");
            this.sender = prop.getProperty("sender");
            this.test = prop.getProperty("test");
        } else {
            throw new PropertyFileErrorException();
        }
    }

    private boolean nn(String p) {
        return this.prop.get(p) != null;
    }

    public String getUsername() { return this.username; }

    public String getHash() { return this.hash; }

    public String getSender() { return this.sender; }

    public String getTest() { return this.test; }

    public String toString() { return String.format(
            "Username: %s\nHash: %s\nSender: %s\nTest: %s",
            this.username,
            this.hash,
            this.sender,
            ((Objects.equals(this.test, "1")) ? "true" : "false")
    ); }

}

class PropertyFileErrorException extends Exception {
    public PropertyFileErrorException() {}
}