package choosing.acl;

import simulation.Resource;
import simulation.TimeSlot;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

record Employee(String name, List<String> skills,
                       List<String> permissions,
                       List<TimeSlot> availabilities) {

    List<Resource> toResources() {
        Stream<Resource> skillResources = availabilities.stream()
                .flatMap(timeSlot ->
                        skills.stream()
                                .map(skill -> new Resource(name, skill, "SKILL", timeSlot))
                );

        Stream<Resource> permissionResources = availabilities.stream()
                .flatMap(timeSlot ->
                        permissions.stream()
                                .flatMap(permission ->
                                        IntStream.range(0, 5)
                                                .mapToObj(i -> new Resource(name, permission, "PERMISSION", timeSlot))
                                ));

        return Stream.concat(skillResources, permissionResources).collect(toList());
    }
}
