package com.cczj.urlservice.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class LocalDateUtils {

    //周一
    public static LocalDate getWeekMonday(LocalDate date) {
        LocalDate lastWeekMonday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        // 检查是否跨月
        if (lastWeekMonday.getMonth() != date.getMonth()) {
            // 上一周的周一是上个月的最后一天
            lastWeekMonday = lastWeekMonday.with(TemporalAdjusters.lastDayOfMonth());
        }
        return lastWeekMonday;
    }

    public static LocalDate getWeekSunday(LocalDate date) {
        LocalDate lastWeekMonday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        LocalDate lastWeekSunday = lastWeekMonday.plusWeeks(1).minusDays(1);
        if (lastWeekSunday.getMonth() != date.getMonth()) {
            // 上一周的周日是这个月的第一天
            lastWeekSunday = lastWeekSunday.with(TemporalAdjusters.firstDayOfMonth());
        }
        return lastWeekSunday;
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00")));
    }
}
