package simulation;

import org.junit.jupiter.api.Test;
import shared.TimeSlot;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContinuousResourceAvailabilitiesTest {

    @Test
    public void testMergeContinuousTimeSlots() {
        //given
        Instant day1Start = Instant.parse("2023-01-01T00:00:00Z");
        Instant day1End = Instant.parse("2023-01-02T00:00:00Z");
        Instant day2Start = day1End;
        Instant day2End = Instant.parse("2023-01-03T00:00:00Z");
        Instant day3Start = day2End;
        Instant day3End = Instant.parse("2023-01-04T00:00:00Z");
        Instant day8start = Instant.parse("2023-01-10T00:00:00Z");

        Instant day8end = Instant.parse("2023-01-11T00:00:00Z");

        List<CapabilityTimeSlot> resources = Arrays.asList(
                new CapabilityTimeSlot("WEB", "SKILL", new TimeSlot(day1Start, day1End)),
                new CapabilityTimeSlot("WEB", "SKILL", new TimeSlot(day2Start, day2End)),
                new CapabilityTimeSlot("WEB", "SKILL", new TimeSlot(day2Start, day3End)),
                new CapabilityTimeSlot("WEB", "SKILL", new TimeSlot(day8start, day8end)),
                new CapabilityTimeSlot("JAVA", "SKILL",new TimeSlot(day2Start, day2End)),
                new CapabilityTimeSlot("WEB", "SKILL", new TimeSlot(day3Start, day3End))
        );
        List<CapabilityTimeSlot> resources2 = Arrays.asList(
                new CapabilityTimeSlot("WEB", "SKILL", new TimeSlot(day3Start, day3End))
        );

        //when
        List<CapabilityTimeSlot> result1 = CapabilityTimeSlot.createContinuousAvailabilities(resources);
        List<CapabilityTimeSlot> result2 = CapabilityTimeSlot.createContinuousAvailabilities(resources2);

        //then
        assertEquals(3, result1.size());
        assertEquals(day1Start, result1.get(0).timeSlot().from());
        assertEquals(day3End, result1.get(0).timeSlot().to());
        assertEquals(day8start, result1.get(1).timeSlot().from());
        assertEquals(day8end, result1.get(1).timeSlot().to());
        assertEquals(day2Start, result1.get(2).timeSlot().from());
        assertEquals(day2End, result1.get(2).timeSlot().to());

        assertEquals(1, result2.size());
        assertEquals(day3Start, result2.get(0).timeSlot().from());
        assertEquals(day3End, result2.get(0).timeSlot().to());
    }

}