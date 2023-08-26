package simulation;

import shared.TimeSlot;

import java.util.*;

import static java.util.stream.Collectors.*;


record CapabilityTimeSlot(String name, String type, TimeSlot timeSlot) {

    List<CapabilityTimeSlot> diff(TimeSlot slot) {
        return
                timeSlot.diff(slot)
                        .stream()
                        .map(timeSlot -> new CapabilityTimeSlot(name, type, timeSlot))
                        .toList();
    }

    Collection<CapabilityTimeSlot> complementWith(TimeSlot forSlot) {
        return timeSlot.complementWith(forSlot)
                .stream()
                .map(timeSlot -> new CapabilityTimeSlot(name, type, timeSlot))
                .toList();
    }

    List<CapabilityTimeSlot> notCoveredBy(CapabilityTimeSlot capabilityTimeSlot) {
        return
                timeSlot().diff(capabilityTimeSlot.timeSlot())
                        .stream()
                        .map(timeSlot -> new CapabilityTimeSlot(this.name(), this.type(), timeSlot))
                        .toList();
    }

    boolean coveredBy(CapabilityTimeSlot capabilityTimeSlot) {
        return this.timeSlot().within(capabilityTimeSlot.timeSlot());
    }

    boolean partiallyCoveredBy(CapabilityTimeSlot capabilityTimeSlot) {
        return this.timeSlot().overlapsWith(capabilityTimeSlot.timeSlot());
    }



}
