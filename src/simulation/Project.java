package simulation;

import optimization.MissingResource;
import shared.TimeSlot;

import java.util.*;
import java.util.stream.Collectors;

class Project {

    private String name;
    private Double remainingEstimatedCosts;
    private Double remainingEstimatedEarnings;
    private Integer remainingEstimatedRisk;
    private Map<String, AllocatedResource> allocatedCapabilities = new HashMap<>();
    private List<CapabilityTimeSlot> requiredResources = new ArrayList<>();

    Project(String name, double remainingEstimatedCosts, double remainingEstimatedEarnings, int remainingEstimatedRisk) {
        this.name = name;
        this.remainingEstimatedCosts = remainingEstimatedCosts;
        this.remainingEstimatedEarnings = remainingEstimatedEarnings;
        this.remainingEstimatedRisk = remainingEstimatedRisk;
    }

    private Collection<AllocatedResource> allAllocations() {
        return allocatedCapabilities.values();
    }

    private AllocatedResource allocationsOf(String resourceId) {
        return allocatedCapabilities
                .getOrDefault(resourceId, new AllocatedResource(resourceId, new ArrayList<>()));
    }

    private List<CapabilityTimeSlot> allCapabilities() {
        return allAllocations()
                .stream()
                .map(AllocatedResource::resourceNonAvailabilities)
                .flatMap(List::stream)
                .toList();
    }

    List<CapabilityTimeSlot> release(String resourceId, String name, String type, TimeSlot timeSlot) {
        AllocatedResource allocations = allocationsOf(resourceId);
        return allocations.release(name, type, timeSlot);
    }

    void allocate(String resourceId, String name, String type, TimeSlot timeSlot) {
        AllocatedResource allocatedResource = allocationsOf(resourceId);
        allocatedResource.allocate(name, type, timeSlot);
        allocatedCapabilities.put(resourceId, allocatedResource);
    }

    void requires(String name, String type, TimeSlot timeSlot) {
        requiredResources.add(new CapabilityTimeSlot(name, type, timeSlot));
    }

    List<CapabilityTimeSlot> missingResources() {
        List<CapabilityTimeSlot> missingResources = new ArrayList<>(this.requiredResources);
        for (CapabilityTimeSlot capability : allCapabilities()) {
            missingResources = coverDifferance(missingResources, capability);
        }
        return missingResources;
    }

    optimization.Project asSimulation() {
        return new optimization.Project(name, remainingEstimatedCosts, remainingEstimatedEarnings, remainingEstimatedRisk, 0,
                missingResources()
                        .stream()
                        .map(missingResource -> new MissingResource(missingResource.name(), missingResource.type(), missingResource.timeSlot()))
                        .collect(Collectors.toList()));
    }

    private List<CapabilityTimeSlot> coverDifferance(List<CapabilityTimeSlot> currentRequired, CapabilityTimeSlot capabilityTimeSlot) {
        List<CapabilityTimeSlot> requiredResources = new ArrayList<>(currentRequired);
        for (CapabilityTimeSlot requiredResource : currentRequired) {
            if (requiredResource.coveredBy(capabilityTimeSlot)) {
                requiredResources.remove(requiredResource);
            } else if (requiredResource.partiallyCoveredBy(capabilityTimeSlot)) {
                requiredResources.remove(requiredResource);
                List<CapabilityTimeSlot> remaining = requiredResource.notCoveredBy(capabilityTimeSlot);
                requiredResources.addAll(remaining);
            }
        }
        return requiredResources;
    }
}

