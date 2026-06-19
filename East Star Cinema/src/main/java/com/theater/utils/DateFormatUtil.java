package com.theater.utils;

import java.time.format.DateTimeFormatter;

public class DateFormatUtil {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
}
