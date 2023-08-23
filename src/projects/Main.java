package projects;

import java.util.*;
import java.util.stream.Collectors;

import java.time.Instant;


import java.time.Instant;
import java.util.List;

class Main {

    public static void main(String[] args) {
        ProjectOptimizer optimizer = new ProjectOptimizer();

        // Tworzenie zasobów
        Resource skill1 = new Resource("Ania", "Java Developer", ResourceType.SKILL);
        Resource skill2 = new Resource("Marek", "Web Designer", ResourceType.SKILL);
        Resource skill3 = new Resource("Staszek", "Database Admin", ResourceType.SKILL);
        Resource tool1 = new Resource("XPS", "Laptop", ResourceType.DEVICE);

        // Tworzenie projektów
        Project project1 = new Project("Website Creation", 1000, 3000,
                Arrays.asList(
                        new RequiredResource("Java Developer", ResourceType.SKILL),
                        new RequiredResource("Web Designer", ResourceType.SKILL)
                )
        );

        Project project2 = new Project("Database Setup", 1500, 4000,
                Arrays.asList(
                        new RequiredResource("Database Admin", ResourceType.SKILL)
                )
        );

        // Test
        List<Project> projects = Arrays.asList(project1, project2);
        List<Resource> resources = Arrays.asList(skill1, skill2 , tool1, skill3);
        Pair<Double, List<Project>> result = optimizer.maxProfit(projects, resources);

        System.out.println("Max Profit: " + result.getFirst());
        System.out.println("Selected Projects: " + result.getSecond());
    }
}

enum ResourceType {
    SKILL, PERMISSION, DEVICE
}

record Resource(String id, String name, ResourceType type) {}

record RequiredResource(String name, ResourceType type) {}

record Project(String name, double estimatedBudget, double estimatedEarnings, List<RequiredResource> requiredResources) {
    double estimatedProfit() {
        return estimatedEarnings - estimatedBudget;
    }
}

class ProjectOptimizer {

    Pair<Double, List<Project>> maxProfit(List<Project> projects, List<Resource> resources) {
        int totalResources = resources.size();
        double[] dp = new double[totalResources + 1];
        List<Project>[] projectLists = new List[totalResources + 1];

        for (int i = 0; i <= totalResources; i++) {
            projectLists[i] = new ArrayList<>();
        }

        for (Project project : projects) {
            int requiredResources = countAllocatableResources(project.requiredResources(), resources);

            if (requiredResources == project.requiredResources().size()) {
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

    private int countAllocatableResources(List<RequiredResource> requiredResources, List<Resource> availableResources) {
        List<Resource> allocatableResources = availableResources.stream()
                .filter(resource -> requiredResources.contains(new RequiredResource(resource.name(), resource.type())))
                .collect(Collectors.toList());

        return allocatableResources.size();
    }
}

class Pair<F, S> {
    private F first;
    private S second;

    Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    F getFirst() {
        return first;
    }

    S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}