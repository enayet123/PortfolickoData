package com.portfolicko.data.tracker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.portfolicko.data.DatabaseThread;
import com.portfolicko.data.utilities.Previous;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

/**
 * Created by enayethussain on 26/12/2017.
 */
public class Fixer extends Thread {
    private String url = "https://api.fixer.io/latest?base=USD&symbols=AUD,BRL,CAD,CHF,CNY,CZK,DKK,EUR,GBP,HKD,HUF,IDR,ILS,INR,JPY,KRW,MXN,MYR,NOK,NZD,PHP,PLN,RUB,SEK,SGD,THB,TRY,TWD,ZAR";
    private Previous previous;

    public Fixer(Previous previous) { this.previous = previous; }

    public void run() {
        String sql;
        try {
            // Connect to site and fetch data
            URL url = new URL(this.url);
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.connect();

            // Parse Data
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(new InputStreamReader((InputStream) req.getContent()));
            JsonObject jo = je.getAsJsonObject();
            JsonObject rates = jo.get("rates").getAsJsonObject();

            // Create Insert SQL query
            if (!Objects.equals(sql = this.createInsertQuery(rates), previous.toString())) {
                new DatabaseThread(sql).start();
                previous.set(sql);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String createInsertQuery(JsonObject rates) {
        // Iterate through all currencies and create SQL statement
        String sql = "INSERT INTO fiat_exchangerate(fiat_id, rate) VALUES ";
        Set<String> fiat = rates.keySet();
        for (String currency: fiat)
            sql += "('" + currency + "', " + rates.get(currency) + "),";
        return sql.substring(0, sql.length()-1) + ";";
    }
}
