package availability;

import shared.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AvailabilityFacade {

    List<TimeSlot> availabilitySlotsFor(String resourceId) {
        //czy jesli jestem wolny caly tydzien to dostane 7 slotow dniowych czy 1 tygodniowy?
        return List.of();
    }

    List<Allocation> resourceAllocations(String resource, TimeSlot timeSlot) {
        return List.of();
    }

    List<Allocation> allTakenBy(TakenBy takenBy) {
        return List.of();
    }

}

record TakenBy(String name) {

}