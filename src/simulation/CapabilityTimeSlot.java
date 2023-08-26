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

    static List<CapabilityTimeSlot> createContinuousAvailabilities(List<CapabilityTimeSlot> resourceAvailabilities) {
        return mergeContinuousResourceAvailabilities(resourceAvailabilities);
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

    private static List<CapabilityTimeSlot> mergeContinuousResourceAvailabilities(List<CapabilityTimeSlot> resourceAvailabilities) {
        return resourcesGroupedByNameAndType(resourceAvailabilities)
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    List<TimeSlot> timeSlots = entry
                            .getValue()
                            .stream()
                            .map(CapabilityTimeSlot::timeSlot)
                            .toList();
                    List<TimeSlot> mergedTimeSlots = TimeSlot.mergeContinuousTimeSlots(timeSlots);
                    return mergedTimeSlots.stream().map(ts -> new CapabilityTimeSlot(entry.getKey().get(0), entry.getKey().get(1), ts));
                })
                .collect(toList());
    }

    private static LinkedHashMap<List<String>, List<CapabilityTimeSlot>> resourcesGroupedByNameAndType(List<CapabilityTimeSlot> resources) {
        return resources.stream()
                .collect(groupingBy(
                        resource -> Arrays.asList(resource.name(), resource.type()),
                        LinkedHashMap::new, toList()
                ));
    }



}
