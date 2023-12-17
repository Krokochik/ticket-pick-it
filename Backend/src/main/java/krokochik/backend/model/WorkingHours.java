package krokochik.backend.model;

import lombok.val;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WorkingHours {
    public record Period(Time begin, Time end) {
        public long length(TimeUnit units) {
            return units.convert(end.getTime() - begin.getTime(),
                    TimeUnit.MILLISECONDS);
        }
    }

    private final Map<DayOfWeek, List<Period>> schedule;

    public Map<DayOfWeek, List<Period>> get() {
        return schedule;
    }

    public long sum(DayOfWeek day) {
        if (schedule.get(day) == null) return 0;
        val sum = new AtomicLong();
        schedule.get(day).forEach(p -> {
            sum.set(sum.get() + p.end().getHours() - p.begin().getHours());
        });
        return sum.get();
    }

    public WorkingHours() {
        schedule = new HashMap<>();
    }

    public WorkingHours(Time begin, Time end) {
        schedule = new HashMap<>();
        for (int i = 1; i < 6; i++) {
            System.out.println(DayOfWeek.of(i));
            schedule.put(DayOfWeek.of(i), Collections.singletonList(
                    new Period(begin, end)));
        }
    }

    public void addPeriod(DayOfWeek d, Period p) {
        schedule.computeIfAbsent(d, k -> new ArrayList<>());
        schedule.get(d).add(p);
    }

    public void removePeriod(DayOfWeek d, Period p) {
        if (schedule.get(d) == null) return;
        schedule.get(d).remove(p);
    }
}
