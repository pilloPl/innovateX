package project.acl;


import simulation.MissingResource;
import simulation.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

class Project {

    private String name;
    private Double remainingEstimatedCosts;
    private Double remainingEstimatedEarnings;
    private Integer remainingEstimatedRisk;
    private List<AllocatedResource> allocatedResources = new ArrayList<>();
    private List<RequiredResource> requiredResources = new ArrayList<>();

    Project(String name, double remainingEstimatedCosts, double remainingEstimatedEarnings, int remainingEstimatedRisk) {
        this.name = name;
        this.remainingEstimatedCosts = remainingEstimatedCosts;
        this.remainingEstimatedEarnings = remainingEstimatedEarnings;
        this.remainingEstimatedRisk = remainingEstimatedRisk;
    }

    List<RequiredResource> missingResources() {
        return requiredResources.stream()
                .filter(requiredResource -> allocatedResources.stream().noneMatch(
                        allocatedResource -> allocatedResource.name().equals(requiredResource.name()) &&
                                allocatedResource.type().equals(requiredResource.type())
                ))
                .collect(Collectors.toList());
    }

    AvailableResource removeResource(AllocatedResource resource) {
        allocatedResources.remove(resource);
        return new AvailableResource(resource.id(), resource.name(), resource.type());
    }

    AllocatedResource allocate(AvailableResource resource) {
        AllocatedResource allocatedResource = new AllocatedResource(resource.id(), resource.name(), resource.type());
        allocatedResources.add(allocatedResource);
        return allocatedResource;
    }

    void requires(RequiredResource resource) {
        requiredResources.add(resource);
    }

    simulation.Project asSimulation() {
        return new simulation.Project(name, remainingEstimatedCosts, remainingEstimatedEarnings, remainingEstimatedRisk, 0,
                missingResources().stream().map(missingResource -> new MissingResource(missingResource.name(), missingResource.type().name())).collect(Collectors.toList()));
    }

    boolean contains(AllocatedResource allocatedResource) {
        return allocatedResources.contains(allocatedResource);
    }
}

enum ResourceType {
    SKILL, PERMISSION, DEVICE
}

record RequiredResource(String name, ResourceType type) {

}

record AllocatedResource(String id, String name, ResourceType type) {

}

record AvailableResource(String id, String name, ResourceType type) {


}

class Test {

    public static void main(String[] args) {
        // Tworzenie zasobów
        AvailableResource anna = new AvailableResource("Ania", "Java Developer", ResourceType.SKILL);
        AvailableResource marek = new AvailableResource("Marek", "Web Designer", ResourceType.SKILL);
        AvailableResource tool1 = new AvailableResource("XPS", "Laptop", ResourceType.DEVICE);

        // Creating projects
        Project project1 = new Project("Website Creation", 1000, 3000, 30);
        Project project2 = new Project("Database Setup", 1500, 4000, 500);
        project1.requires(new RequiredResource("Java Developer", ResourceType.SKILL));
        project2.requires(new RequiredResource("Java Developer", ResourceType.SKILL));


        ProjectManipulation projectManipulation = new ProjectManipulation(
                Stream.of(project1, project2).collect(toCollection(ArrayList::new)),
                Stream.of(anna, marek, tool1).collect(toCollection(ArrayList::new)));

        AllocatedResource allocatedAnna = projectManipulation.allocate(anna, project1);

        Result calculate = projectManipulation.calculate();
        System.out.println("Max Profit: " + calculate.profit());

        Result result2 = projectManipulation.profitAfterReleasingResource(allocatedAnna, project1);
        System.out.println("Max Profit: " + result2.profit());
        projectManipulation.allocate(anna, project1);

        Double difference = projectManipulation.differenceAfterReleasingResource(allocatedAnna, project1);
        System.out.println("diff: " +  difference);
    }


}