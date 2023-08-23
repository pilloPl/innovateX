package projects;

import java.util.*;
import java.util.stream.Collectors;

import java.time.Instant;


import java.time.Instant;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ProjectOptimizer optimizer = new ProjectOptimizer();

        // Tworzenie zasobów
        Resource skill1 = new Resource("Java Developer", ResourceType.SKILL, Instant.parse("2023-08-18T10:00:00Z"), Instant.parse("2023-08-18T11:00:00Z"));
        Resource skill2 = new Resource("Web Designer", ResourceType.SKILL, Instant.parse("2023-08-18T10:00:00Z"), Instant.parse("2023-08-18T11:00:00Z"));
        Resource skill3 = new Resource("Database Admin", ResourceType.SKILL, Instant.parse("2023-08-18T10:00:00Z"), Instant.parse("2023-08-18T11:00:00Z"));
        Resource tool1 = new Resource("Laptop", ResourceType.DEVICE, Instant.parse("2023-08-18T10:00:00Z"), Instant.parse("2023-08-18T11:00:00Z"));

        // Tworzenie projektów
        Project project1 = new Project("Website Creation", 1000, 3000,
                Arrays.asList(
                        new Phase("Design and Development", Instant.parse("2023-08-18T10:15:00Z"), Instant.parse("2023-08-18T10:35:00Z"),
                                Arrays.asList(
                                        new RequiredResource("Java Developer", ResourceType.SKILL, Instant.parse("2023-08-18T10:15:00Z"), Instant.parse("2023-08-18T10:35:00Z")),
                                        new RequiredResource("Web Designer", ResourceType.SKILL, Instant.parse("2023-08-18T10:15:00Z"), Instant.parse("2023-08-18T10:35:00Z"))
                                )
                        )
                )
        );

        Project project2 = new Project("Database Setup", 1500, 4000,
                Arrays.asList(
                        new Phase("DB Initialization", Instant.parse("2023-08-18T10:40:00Z"), Instant.parse("2023-08-18T11:10:00Z"),
                                Arrays.asList(
                                        new RequiredResource("Database Admin", ResourceType.SKILL, Instant.parse("2023-08-18T10:40:00Z"), Instant.parse("2023-08-18T11:10:00Z"))
                                )
                        )
                )
        );

        // Test
        List<Project> projects = Arrays.asList(project1, project2);
        List<Resource> resources = Arrays.asList(skill1, skill2 , tool1);
        Pair<Double, List<Project>> result = optimizer.maxProfit(projects, resources);

        System.out.println("Max Profit: " + result.getFirst());
        System.out.println("Selected Projects: " + result.getSecond());
    }
}


enum ResourceType {
    SKILL, PERMISSION, DEVICE
}

record Resource(String name, ResourceType type, Instant from, Instant to) {
    boolean isAvailable(Instant requestedFrom, Instant requestedTo) {
        return !to.isBefore(requestedFrom) && !from.isAfter(requestedTo);
    }
}

record RequiredResource(String name, ResourceType type, Instant from, Instant to) {}

record Phase(String phaseName, Instant from, Instant to, List<RequiredResource> requiredResources) {}

record Project(String name, double estimatedBudget, double estimatedEarnings, List<Phase> phases) {
    double estimatedProfit() {
        return estimatedEarnings - estimatedBudget;
    }

    public Set<RequiredResource> uniqueRequiredResources() {
        return phases.stream()
                .flatMap(phase -> phase.requiredResources().stream())
                .collect(Collectors.toSet());
    }
}

class ProjectOptimizer {

    public Pair<Double, List<Project>> maxProfit(List<Project> projects, List<Resource> resources) {
        int totalResources = resources.size();
        double[] dp = new double[totalResources + 1];
        List<Project>[] projectLists = new List<>[totalResources + 1];

        for (int i = 0; i <= totalResources; i++) {
            projectLists[i] = new ArrayList<>();
        }

        for (Project project : projects) {
            Set<RequiredResource> uniqueRequiredResources = project.uniqueRequiredResources();

            int requiredResources = countAllocatableResources(uniqueRequiredResources, resources);

            if (requiredResources >= uniqueRequiredResources.size()) {
                for (int j = totalResources; j >= requiredResources; j--) {
                    if (dp[j] < project.estimatedProfit() + dp[j - requiredResources]) {
                        dp[j] = project.estimatedProfit() + dp[j - requiredResources];
                        projectLists[j] = new ArrayList<>(projectLists[j - requiredResources]);
                        projectLists[j].add(project);
                    }
                }
            }
        }

        return new Pair<>(dp[totalResources], projectLists[totalResources]);
    }

    private int countAllocatableResources(Set<RequiredResource> uniqueRequiredResources, List<Resource> availableResources) {
        List<Resource> allocatableResources = availableResources.stream()
                .filter(resource -> canAllocateResource(resource, uniqueRequiredResources))
                .collect(Collectors.toList());

        return allocatableResources.size();
    }

    private boolean canAllocateResource(Resource resource, Set<RequiredResource> uniqueRequiredResources) {
        for (RequiredResource required : uniqueRequiredResources) {
            if (resource.name().equals(required.name()) &&
                    resource.type().equals(required.type()) &&
                    resource.isAvailable(required.from(), required.to())) {
                return true;
            }
        }
        return false;
    }
}

class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
