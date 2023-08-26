package simulation;

import shared.TimeSlot;
import simulation.ProjectSimulation.ProjectAllocationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static simulation.ProjectSimulation.ProjectAllocationResult.Success;

class AllocatedResource {

    private String resourceId;
    private List<CapabilityTimeSlot> resourceNonAvailabilities;

    AllocatedResource(String resourceId, List<CapabilityTimeSlot> resourceNonAvailabilities) {
        this.resourceId = resourceId;
        this.resourceNonAvailabilities = resourceNonAvailabilities;
    }

    List<CapabilityTimeSlot> release(String name, String type, TimeSlot forSlot) {
        CapabilityTimeSlot found = find(name, type, forSlot);
        if (found == null) {
            return List.of();
        }
        List<CapabilityTimeSlot> newCapabilities = new ArrayList<>(resourceNonAvailabilities);
        newCapabilities.remove(found);
        newCapabilities.addAll(found.complementWith(forSlot));
        newCapabilities = CapabilityTimeSlot.createContinuousAvailabilities(newCapabilities);
        resourceNonAvailabilities = newCapabilities;
        return List.of(new CapabilityTimeSlot(name, type, forSlot));
    }

    ProjectAllocationResult allocate(String name, String type, TimeSlot timeSlot) {
        CapabilityTimeSlot slot = new CapabilityTimeSlot(name, type, timeSlot);
        List<CapabilityTimeSlot> newCapabilities = new ArrayList<>(resourceNonAvailabilities);
        newCapabilities.add(slot);
        newCapabilities = CapabilityTimeSlot.createContinuousAvailabilities(newCapabilities);
        resourceNonAvailabilities = newCapabilities;
        return Success;
    }

    private CapabilityTimeSlot find(String name, String type, TimeSlot forSlot) {
        return resourceNonAvailabilities.stream()
                .filter(r -> r.name().equals(name) && r.type().equals(type) && forSlot.within(r.timeSlot()))
                .findFirst()
                .orElse(null);

    }

    List<CapabilityTimeSlot> resourceNonAvailabilities() {
        return Collections.unmodifiableList(resourceNonAvailabilities);
    }
}
