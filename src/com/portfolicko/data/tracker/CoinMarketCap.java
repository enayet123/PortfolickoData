package com.portfolicko.data.tracker;

import com.google.gson.*;
import com.portfolicko.data.utilities.CoinTracker;
import com.portfolicko.data.DatabaseThread;
import com.portfolicko.data.utilities.Previous;
import com.portfolicko.data.utilities.TextMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Enayet Hussain on 25/12/2017.
 */
public class CoinMarketCap extends Thread {
    private String allcoins = "https://api.coinmarketcap.com/v1/ticker/?convert=GBP&limit=10000";
    private String phonenumbers = "http://www.portfolicko.com/api/private/coin-release-list";
    private CoinTracker ct;
    private Previous previous;

    public CoinMarketCap (CoinTracker ct, Previous previous) { this.ct = ct; this.previous = previous; }

    public void run() {
        String cryptoSql;
        String priceSql;
        Date date = new Date();
        try {
            // Connect to site and fetch data
            URL url = new URL(this.allcoins);
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.connect();

            // Parse PropertyData
            JsonParser jp = new JsonParser();
            JsonElement je = null;
            try {
                je = jp.parse(new InputStreamReader((InputStream) req.getContent()));
                JsonArray ja = je.getAsJsonArray();

                // Run inserts
                // If there is a new coin to add
                if ((cryptoSql = this.insertCryptoQuery(ja)) != null)
                    new DatabaseThread(cryptoSql).start();
                // If the new insert prices are not identical to the previous
                if (!Objects.equals(priceSql = this.insertPriceQuery(ja), previous.toString())) {
                    System.out.printf("%1$s %2$td %2$tM %2$tY at %2$tH:%2$tm:%2$ts\n", "Updating prices on ", date);
                    new DatabaseThread(priceSql).start();
                    previous.set(priceSql);
                }
            } catch (JsonSyntaxException jse) {
                jse.printStackTrace();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String insertCryptoQuery(JsonArray ja) {
        // Beginning of SQL statement
        String sql = "INSERT IGNORE INTO cryptocurrency_type(name,symbol) VALUES ";

        // New coin counter
        int count = 0;

        // Get each required piece of data
        for (int x=0;x<ja.size();x++) {

            // Get each Json Obj
            JsonObject jo = ja.get(x).getAsJsonObject();

            // Check if coin was previously added
            if (!ct.exists(jo.get("name").toString(), jo.get("symbol").toString())) {
                // Add coin if not exist
                ct.add(jo.get("name").toString(), jo.get("symbol").toString());

                // Create SQL statement
                sql += "(" + jo.get("name") + "," + jo.get("symbol") + "),";

                // Increment coin counter
                count++;
            }
        }

        if (count > 0 && count < 10) {
            System.out.println(
                    TextMessage.sendSms(
                            this.getPhoneNumbers(),
                            "New Coin(s):" + reformatCoins(sql.substring(59, sql.length() - 1))
                    )
            );
        }

        return (count > 0) ? (sql.substring(0, sql.length()-1) + ";") : null;
    }

    private String insertPriceQuery(JsonArray ja) {
        // Beginning of SQL statement
        String sql = "INSERT INTO cryptocurrency_price(cryptocurrency_name, cryptocurrency_symbol, price_usd, source_id) VALUES ";

        // Get each required piece of data
        for (int x=0;x<ja.size();x++) {
            JsonObject jo = ja.get(x).getAsJsonObject();
            if (jo.get("price_usd") == null || Objects.equals(jo.get("price_usd").toString(), "null")) continue;
            sql += "(" + jo.get("name") + "," + jo.get("symbol") + "," + jo.get("price_usd") + ", 1),";
        }
        return sql.substring(0, sql.length()-1) + ";";
    }

    private String getPhoneNumbers() {
        try {
            URL url = new URL(phonenumbers);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String reformatCoins(String newcoins) {
        String[] list = newcoins.replaceAll("[^a-zA-Z0-9\"]+. ", "").split("\"");
        ArrayList<String> forbidden = new ArrayList<>(Arrays.asList(" ", ",", "(", ")", "", "),("));
        ArrayList array = new ArrayList<String>();
        for (String listItem: list)
            if (!forbidden.contains(listItem))
                array.add(listItem);
        newcoins = "\n";
        for (int x=0;x<(array.size()/2);x++)
            newcoins += String.format("%s (%s) \n", array.get(x*2), array.get((x*2)+1));
        return newcoins;
    }
}
