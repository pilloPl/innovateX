package simulation;

import optimization.Resource;
import shared.TimeSlot;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public record Employee(String name, List<String> skills,
                       List<String> permissions,
                       List<TimeSlot> availabilities) {

    public List<CapabilityTimeSlot> availableResources() {
        Stream<CapabilityTimeSlot> skillResources = availabilities.stream()
                .flatMap(timeSlot ->
                        skills.stream()
                                .map(skill -> new CapabilityTimeSlot(skill, "SKILL", timeSlot))
                );

        Stream<CapabilityTimeSlot> permissionResources = availabilities.stream()
                .flatMap(timeSlot ->
                        permissions.stream()
                                .flatMap(permission ->
                                        IntStream.range(0, 5)
                                                .mapToObj(i -> new CapabilityTimeSlot(permission, "PERMISSION", timeSlot))
                                ));

        return Stream.concat(skillResources, permissionResources).collect(toList());
    }

    public List<Resource> toResources() {
        return availableResources()
                .stream()
                .map(ct -> new Resource(name, ct.name(), ct.type(), ct.timeSlot())).toList();
    }

    public CapabilityTimeSlot skill(String name, TimeSlot requiredSlot) {
        if (skills.contains(name)) {
            Optional<TimeSlot> slot = availabilities.stream()
                    .filter(requiredSlot::within)
                    .findFirst();
            if (slot.isPresent()) {
                return new CapabilityTimeSlot(name, "SKILL", requiredSlot);
            }
        }
        return null;
    }
}
