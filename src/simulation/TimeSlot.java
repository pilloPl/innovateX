package simulation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public record TimeSlot(Instant from, Instant to) {

    static TimeSlot createMonthlyTimeSlotAtUTC(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        Instant from = startOfMonth.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant to = endOfMonth.atTime(23, 59, 59).atZone(ZoneId.of("UTC")).toInstant();
        return new TimeSlot(from, to);
    }

    boolean within(TimeSlot other) {
        return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
    }
}
