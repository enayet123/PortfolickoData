package com.portfolicko.data.tracker;

import java.time.LocalDateTime;

/**
 * Created by enayethussain on 26/12/2017.
 */
public class LastCheck {
    // Set largely expired time
    LocalDateTime lastCheck = LocalDateTime.now().minusYears(1);

    public boolean timeToCheck(int minutesToPass) {
        if (lastCheck.plusMinutes(minutesToPass).compareTo(LocalDateTime.now()) < 1)
            return check();
        return false;
    }

    public boolean check() {
        lastCheck = LocalDateTime.now();
        return true;
    }
}
