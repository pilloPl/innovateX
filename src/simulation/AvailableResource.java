package simulation;

import optimization.Resource;
import shared.TimeSlot;
import simulation.ProjectSimulation.ProjectAllocationResult;

import java.util.ArrayList;
import java.util.List;

class AvailableResource {

    private String resourceId;
    private List<CapabilityTimeSlot> resourceAvailabilities;

    AvailableResource(String resourceId, List<CapabilityTimeSlot> resourceAvailabilities) {
        this.resourceId = resourceId;
        this.resourceAvailabilities = resourceAvailabilities;
    }

    AvailableResource(String resourceId, String name, String type, TimeSlot june) {
        this(resourceId, List.of(new CapabilityTimeSlot(name, type, june)));
    }

    List<Resource> toResources() {
        return resourceAvailabilities.stream().map(r ->
                        new Resource(resourceId, r.name(), r.type(), r.timeSlot()))
                .toList();
    }

    ProjectAllocationResult remove(String name, String type, TimeSlot forSlot) {
        CapabilityTimeSlot found = find(name, type, forSlot);
        if (found == null) {
            return ProjectAllocationResult.Failure;
        }
        List<CapabilityTimeSlot> newCapabilities = new ArrayList<>(resourceAvailabilities);
        newCapabilities.remove(found);
        newCapabilities.addAll(found.diff(forSlot));
        newCapabilities = CapabilityTimeSlot.createContinuousAvailabilities(newCapabilities);
        resourceAvailabilities = newCapabilities;
        return ProjectAllocationResult.Success;
    }

    private CapabilityTimeSlot find(String name, String type, TimeSlot forSlot) {
        return resourceAvailabilities.stream()
                .filter(r -> r.name().equals(name) && r.type().equals(type) && forSlot.within(r.timeSlot()))
                .findFirst()
                .orElse(null);
    }

    ProjectAllocationResult add(String name, String type, TimeSlot fromSlot) {
        List<CapabilityTimeSlot> newCapabilities = new ArrayList<>(resourceAvailabilities);
        newCapabilities.add(new CapabilityTimeSlot(name, type, fromSlot));
        newCapabilities = CapabilityTimeSlot.createContinuousAvailabilities(newCapabilities);
        resourceAvailabilities = newCapabilities;
        return ProjectAllocationResult.Success;
    }

    String resourceId() {
        return resourceId;
    }

    public void addAll(String name, String type, List<CapabilityTimeSlot> capabilities) {
        capabilities.forEach(c -> add(name, type, c.timeSlot()));
    }
}
