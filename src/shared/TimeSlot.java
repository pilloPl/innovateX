package shared;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public record TimeSlot(Instant from, Instant to) {

    static TimeSlot empty() {
        return new TimeSlot(Instant.EPOCH, Instant.EPOCH);
    }

    public static TimeSlot createMonthlyTimeSlotAtUTC(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        Instant from = startOfMonth.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant to = endOfMonth.atTime(23, 59, 59).atZone(ZoneId.of("UTC")).toInstant();
        return new TimeSlot(from, to);
    }

    public static TimeSlot createDailyTimeSlotAtUTC(int year, int month, int day) {
        LocalDate thisDay = LocalDate.of(year, month, day);
        Instant from = thisDay.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant to = thisDay.atTime(23, 59, 59).atZone(ZoneId.of("UTC")).toInstant();
        return new TimeSlot(from, to);
    }

    public TimeSlot add(TimeSlot other) {
        if (overlapsWith(other) || isContiguousWith(other)) {
            return new TimeSlot(
                    this.from.isBefore(other.from) ? this.from : other.from,
                    this.to.isAfter(other.to) ? this.to : other.to
            );
        }
        throw new IllegalArgumentException("TimeSlots do not overlap nor are contiguous");
    }

    public TimeSlot addOneDay() {
        Instant nextDay = this.to.plus(1, java.time.temporal.ChronoUnit.DAYS);
        return new TimeSlot(this.from, nextDay);
    }

    public List<TimeSlot> diff(TimeSlot other) {
        List<TimeSlot> result = new ArrayList<>();
        if (!other.overlapsWith(this)) {
            return result;
        }
        if (this.equals(other)) {
            return result;
        }
        if (this.from.isBefore(other.from)) {
            result.add(new TimeSlot(this.from, other.from));
        }
        if (other.from.isBefore(from)) {
            result.add(new TimeSlot(other.from, this.from));
        }
        if (this.to.isAfter(other.to)) {
            result.add(new TimeSlot(other.to, this.to));
        }
        if (other.to.isAfter(this.to)) {
            result.add(new TimeSlot(this.to, other.to));
        }
        return result;
    }

    public boolean overlapsWith(TimeSlot other) {
        return !this.from().isAfter(other.to()) && !this.to().isBefore(other.from());
    }

    private boolean isContiguousWith(TimeSlot other) {
        return this.to.equals(other.from) || this.from.equals(other.to);
    }

    public static List<TimeSlot> mergeContinuousTimeSlots(List<TimeSlot> timeSlots) {
        if (timeSlots.isEmpty()) {
            return timeSlots;
        }
        List<TimeSlot> sortedTimeSlots = timeSlots
                .stream()
                .sorted(comparing(TimeSlot::from)).toList();
        List<TimeSlot> mergedTimeSlots = new ArrayList<>();
        TimeSlot current = sortedTimeSlots.get(0);
        for (int i = 1; i < sortedTimeSlots.size(); i++) {
            TimeSlot next = sortedTimeSlots.get(i);
            if (current.to().equals(next.from()) || current.to().isAfter(next.from())) {
                current = new TimeSlot(current.from(), next.to());
            } else {
                mergedTimeSlots.add(current);
                current = next;
            }
        }
        mergedTimeSlots.add(current);
        return mergedTimeSlots;
    }

    public boolean within(TimeSlot other) {
        return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
    }

    public TimeSlot commonPartWith(TimeSlot other) {
        if (!this.overlapsWith(other)) {
            return TimeSlot.empty();
        }
        Instant commonStart = this.from.isAfter(other.from) ? this.from : other.from;
        Instant commonEnd = this.to.isBefore(other.to) ? this.to : other.to;

        return new TimeSlot(commonStart, commonEnd);
    }


    public List<TimeSlot> complementWith(TimeSlot other) {
        TimeSlot commonPart = this.commonPartWith(other);
        if (commonPart.isEmpty()) {
            return List.of(this);
        }
        return this.diff(commonPart);
    }

    boolean isEmpty() {
        return this.equals(TimeSlot.empty());
    }


}
