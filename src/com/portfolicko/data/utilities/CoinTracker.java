package com.portfolicko.data.utilities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by enayethussain on 26/12/2017.
 */
public class CoinTracker {
    HashMap<String, Symbol> map = new HashMap<>();

    // Check if coin exists
    public boolean exists(String name, String symbol) {
        if (map.get(name) != null )
            return map.get(name).exists(symbol);
        return false;
    }

    // Add new found coin
    public void add(String name, String symbol) {
        Symbol s = map.get(name);
        if (s != null)
            s.add(symbol);
        else
            map.put(name, new Symbol(symbol));
    }
}

class Symbol {
    ArrayList<String> list = new ArrayList<>();
    public Symbol(String symbol) { this.add(symbol); }
    public boolean exists(String symbol) { return (list.contains(symbol)); }
    public void add(String symbol) { list.add(symbol); }
}