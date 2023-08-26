package availability;

import shared.TimeSlot;

public record Allocation(TakenBy takenBy, TimeSlot timeSlot) {

}
