package shared;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {


    @Test
    void testOverlapsWith_True_Scenarios() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-05T00:00:00Z"), Instant.parse("2022-01-15T00:00:00Z"));

        assertTrue(slot1.overlapsWith(slot2));
        assertTrue(slot1.overlapsWith(slot1));

        TimeSlot slot3 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        assertTrue(slot1.overlapsWith(slot3));

        TimeSlot slot4 = new TimeSlot(Instant.parse("2022-01-05T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));

        assertTrue(slot1.overlapsWith(slot4));

        TimeSlot slot5 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));

        assertTrue(slot1.overlapsWith(slot5));
    }

    @Test
    void testOverlapsWith_False_Scenarios() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-10T01:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        assertFalse(slot1.overlapsWith(slot2));

        TimeSlot slot3 = new TimeSlot(Instant.parse("2022-01-11T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        assertFalse(slot1.overlapsWith(slot3));
    }

    @Test
    public void testDiff_NoOverlap() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-15T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        assertTrue(slot1.diff(slot2).isEmpty());
    }

    @Test
    public void testDiff_SameTimeSlot() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));

        assertTrue(slot1.diff(slot1).isEmpty());
    }

    @Test
    public void testDiff_OverlapAtStart() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-15T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        List<TimeSlot> difference = slot1.diff(slot2);
        assertEquals(2, difference.size());
        assertEquals(Instant.parse("2022-01-01T00:00:00Z"), difference.get(0).from());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), difference.get(0).to());
        assertEquals(Instant.parse("2022-01-15T00:00:00Z"), difference.get(1).from());
        assertEquals(Instant.parse("2022-01-20T00:00:00Z"), difference.get(1).to());
    }

    @Test
    public void testDiff_OverlapAtEnd() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-05T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));

        List<TimeSlot> difference = slot1.diff(slot2);
        assertEquals(2, difference.size());
        assertEquals(Instant.parse("2022-01-01T00:00:00Z"), difference.get(0).from());
        assertEquals(Instant.parse("2022-01-05T00:00:00Z"), difference.get(0).to());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), difference.get(1).from());
        assertEquals(Instant.parse("2022-01-20T00:00:00Z"), difference.get(1).to());
    }

    @Test
    public void testDiff_OverlapInTheMiddle() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-15T00:00:00Z"));

        List<TimeSlot> difference = slot1.diff(slot2);
        assertEquals(2, difference.size());
        assertEquals(Instant.parse("2022-01-01T00:00:00Z"), difference.get(0).from());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), difference.get(0).to());
        assertEquals(Instant.parse("2022-01-15T00:00:00Z"), difference.get(1).from());
        assertEquals(Instant.parse("2022-01-20T00:00:00Z"), difference.get(1).to());
    }

    @Test
    public void testCommonPartWith_NoOverlap() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-15T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        assertTrue(slot1.commonPartWith(slot2).isEmpty());
    }

    @Test
    public void testCommonPartWith_OverlapAtStart() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-15T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        TimeSlot common = slot1.commonPartWith(slot2);
        assertFalse(common.isEmpty());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), common.from());
        assertEquals(Instant.parse("2022-01-15T00:00:00Z"), common.to());
    }

    @Test
    public void testCommonPartWith_FullOverlap() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        TimeSlot common = slot1.commonPartWith(slot2);
        assertFalse(common.isEmpty());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), common.from());
        assertEquals(Instant.parse("2022-01-20T00:00:00Z"), common.to());
    }

    @Test
    public void testComplementWith_NoOverlap() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-10T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-15T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        List<TimeSlot> complement = slot1.complementWith(slot2);
        assertEquals(1, complement.size());
        assertEquals(slot1, complement.get(0));
    }

    @Test
    public void testComplementWith_OverlapAtStart() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-01T00:00:00Z"), Instant.parse("2022-01-15T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));

        List<TimeSlot> complement = slot1.complementWith(slot2);
        assertEquals(1, complement.size());
        assertEquals(Instant.parse("2022-01-01T00:00:00Z"), complement.get(0).from());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), complement.get(0).to());
    }

    @Test
    public void testComplementWith_OverlapAtEnd() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-25T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-20T00:00:00Z"), Instant.parse("2022-01-30T00:00:00Z"));

        List<TimeSlot> complement = slot1.complementWith(slot2);
        assertEquals(1, complement.size());
        assertEquals(Instant.parse("2022-01-10T00:00:00Z"), complement.get(0).from());
        assertEquals(Instant.parse("2022-01-20T00:00:00Z"), complement.get(0).to());
    }

    @Test
    public void testComplementWith_FullOverlap() {
        TimeSlot slot1 = new TimeSlot(Instant.parse("2022-01-10T00:00:00Z"), Instant.parse("2022-01-20T00:00:00Z"));
        TimeSlot slot2 = new TimeSlot(Instant.parse("2022-01-05T00:00:00Z"), Instant.parse("2022-01-25T00:00:00Z"));

        List<TimeSlot> complement = slot1.complementWith(slot2);
        assertTrue(complement.isEmpty());
    }
}

