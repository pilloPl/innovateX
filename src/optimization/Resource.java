package optimization;

import shared.TimeSlot;

import java.util.UUID;

public record Resource(UUID uuid, String id, String name, String resourceType, TimeSlot timeSlot) {
    public Resource(String id, String name, String resourceType, TimeSlot timeSlot) {
        this(UUID.randomUUID(), id, name, resourceType, timeSlot);
    }
}
