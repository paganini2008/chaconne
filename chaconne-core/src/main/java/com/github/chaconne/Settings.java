package com.github.chaconne;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 
 * @Description: Settings
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public final class Settings {

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC");

    public static LocalDateTime NOW() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

}
