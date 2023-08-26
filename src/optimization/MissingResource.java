package optimization;

import shared.TimeSlot;

public record MissingResource(String name, String resourceType, TimeSlot timeSlot) {

    boolean canBeAllocatedBy(Resource resource) {
        return resource.name().equals(name) &&
                resource.resourceType().equals(resourceType) &&
                timeSlot.within(resource.timeSlot());
    }
}
