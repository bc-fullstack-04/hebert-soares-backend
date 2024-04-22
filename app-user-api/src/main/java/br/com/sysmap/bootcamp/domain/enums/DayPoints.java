package br.com.sysmap.bootcamp.domain.enums;

import java.time.DayOfWeek;

public enum DayPoints {
    SUNDAY(25),
    MONDAY(7),
    TUESDAY(6),
    WEDNESDAY(2),
    THURSDAY(10),
    FRIDAY(15),
    SATURDAY(20);

    private final int points;

    DayPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public static int getPointsByDayOfWeek(DayOfWeek day) {
        for (DayPoints dp : DayPoints.values()) {
            if (dp.name().equals(day.name())) {
                return dp.getPoints();
            }
        }
        return 0;
    }
}
