package com.portfolicko.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Enayet Hussain on 25/12/2017.
 */
public class DatabaseThread extends Thread {
    private String query;

    public DatabaseThread(String query) {
        this.query = query;
    }
    public void run() {
        // Load data
        Properties p = new Properties();
        try {
            FileInputStream fin = new FileInputStream("database.prop");
            p.load(fin);
            fin.close();
            PropertyData propertyData = new PropertyData(p);
            final String url = "jdbc:mysql://" +
                    propertyData.getIp() + ":" +
                    propertyData.getPort() + "/" +
                    propertyData.getDatabase() + "?autoReconnect=true&useSSL=false";
            final String username = propertyData.getUsername();
            final String password = propertyData.getPassword();
            new Database(url, username, password, query);
        } catch (PropertyFileErrorException | FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PropertyData {
    private final Properties prop;
    private String ip;
    private String port;
    private String database;
    private String username;
    private String password;

    public PropertyData(Properties prop) throws PropertyFileErrorException {
        this.prop = prop;
        if (nn("ip") && nn("port") && nn("database") && nn("username") && nn("password")) {
            this.ip = prop.getProperty("ip");
            this.port = prop.getProperty("port");
            this.database = prop.getProperty("database");
            this.username = prop.getProperty("username");
            this.password = prop.getProperty("password");
        } else throw new PropertyFileErrorException();
    }

    private boolean nn(String p) {
        return this.prop.get(p) != null;
    }

    public String getIp() { return this.ip; }

    public String getPort() { return this.port; }

    public String getDatabase() { return this.database; }

    public String getUsername() { return this.username; }

    public String getPassword() { return this.password; }

    public String toString() { return String.format(
            "Username: %s\nPassword: %s\nIP: %s\nPort: %s\nDatabase: %s",
            this.username,
            this.password,
            this.ip,
            this.port,
            this.database
    ); }
}

class PropertyFileErrorException extends Exception {
    public PropertyFileErrorException() {}
}