package simulation;

import optimization.Resource;
import shared.TimeSlot;
import simulation.ProjectSimulation.ProjectAllocationResult;

import java.util.ArrayList;
import java.util.Collections;
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

    List<CapabilityTimeSlot> remove(String name, String type, TimeSlot forSlot) {
        CapabilityTimeSlot found = find(name, type, forSlot);
        if (found == null) {
            return List.of();
        }
        resourceAvailabilities.remove(found);
        resourceAvailabilities.addAll(found.diff(forSlot));
        return List.of(new CapabilityTimeSlot(name, type, forSlot));
    }

    private CapabilityTimeSlot find(String name, String type, TimeSlot forSlot) {
        return resourceAvailabilities.stream()
                .filter(r -> r.name().equals(name) && r.type().equals(type) && forSlot.within(r.timeSlot()))
                .findFirst()
                .orElse(null);
    }

    ProjectAllocationResult add(String name, String type, TimeSlot fromSlot) {
        resourceAvailabilities.add(new CapabilityTimeSlot(name, type, fromSlot));
        return ProjectAllocationResult.Success;
    }

    String resourceId() {
        return resourceId;
    }

    void addAll(String name, String type, List<CapabilityTimeSlot> capabilities) {
        capabilities.forEach(c -> add(name, type, c.timeSlot()));
    }

    List<CapabilityTimeSlot> resourceAvailabilities() {
        return Collections.unmodifiableList(resourceAvailabilities);
    }
}
