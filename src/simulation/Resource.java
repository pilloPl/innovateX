package simulation;


import java.util.*;

public class Resource {

    private UUID uuid;
    private String id;
    private Set<ResourceCapability> resourceCapabilities;
    private TimeSlot timeSlot;

    Resource(UUID uuid, String id, Set<ResourceCapability> resourceCapabilities, TimeSlot timeSlot) {
        this.uuid = uuid;
        this.id = id;
        this.resourceCapabilities = resourceCapabilities;
        this.timeSlot = timeSlot;
    }

    public Resource(String id, String name, String resourceType, TimeSlot timeSlot) {
        this(UUID.randomUUID(), id, new HashSet<>(Set.of(new ResourceCapability(name, resourceType, 0))), timeSlot);
    }

    public Resource(String name, Set<ResourceCapability> capabilities, TimeSlot timeSlot) {
        this(UUID.randomUUID(), name, capabilities, timeSlot);
    }

    ResourceCapability use(String name, String resourceType) {
        ResourceCapability capability = findCapability(name, resourceType);
        if (capability != null) {
            resourceCapabilities.remove(capability);
            ResourceCapability rc = capability.use();
            resourceCapabilities.add(rc);
        }
        return capability;
    }

    private ResourceCapability findCapability(String name, String resourceType) {
        return resourceCapabilities.stream()
                .filter(resourceCapability -> resourceCapability.name().equals(name) &&
                        resourceCapability.resourceType().equals(resourceType))
                .findFirst()
                .orElse(null);
    }


    public boolean canBeUsedFor(String name, String resourceType, TimeSlot timeSlot) {
        if (!this.timeSlot.within(timeSlot)) {
            return false;
        }

        boolean takenEntirely = resourceCapabilities.stream()
                .anyMatch(rc -> rc.usedTimes() > 0 && !rc.resourceType().equals("PERMISSION"));

        if (takenEntirely) {
            return false;
        }

        return resourceCapabilities
                .stream()
                .anyMatch(
                        resourceCapability -> resourceCapability.canBeUsed() &&
                                resourceCapability.name().equals(name) &&
                                resourceCapability.resourceType().equals(resourceType)
                );
    }

    List<ResourceCapability> capabilities() {
        return new ArrayList<>(resourceCapabilities);
    }

    public int capabilitiesSize() {
        return resourceCapabilities
                .stream()
                .mapToInt(ResourceCapability::size)
                .sum();
    }
}

record ResourceCapability(String name, String resourceType, int usedTimes) {

    boolean canBeUsed() {
        if (resourceType.equals("PERMISSION")) {
            return usedTimes < 4;
        }
        return usedTimes == 0;
    }

    ResourceCapability use() {
        return new ResourceCapability(name, resourceType, usedTimes + 1);
    }

    public int size() {
        if (resourceType.equals("PERMISSION")) {
            return 4;
        }
        return 1;
    }
}