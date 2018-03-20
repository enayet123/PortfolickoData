package com.portfolicko.data;

import com.portfolicko.data.tracker.CoinMarketCap;
import com.portfolicko.data.tracker.Fixer;
import com.portfolicko.data.tracker.LastCheck;
import com.portfolicko.data.utilities.CoinTracker;
import com.portfolicko.data.utilities.Previous;

/**
 * Created by Enayet Hussain on 24/12/2017.
 */
public class Condenser {
    public static void main(String[] args) {
        System.out.println("Started...");
        CoinTracker ct = new CoinTracker();
        Previous cmcPrevious = new Previous("");
        Previous fPrevious = new Previous("");
        LastCheck fixerLastCheck = new LastCheck();
        try {
            while (true) {
                if (fixerLastCheck.timeToCheck(60))
                    new Fixer(fPrevious).start();
                new CoinMarketCap(ct, cmcPrevious).start();
                Thread.sleep(4 * 60 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}