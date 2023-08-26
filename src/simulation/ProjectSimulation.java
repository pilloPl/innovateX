package simulation;

import optimization.OptimizationFacade;
import optimization.Resource;
import optimization.Result;
import shared.TimeSlot;

import java.util.*;


class ProjectSimulation {

    enum ProjectAllocationResult {
        Success, Failure
    }

    private List<Project> projects;
    private Map<String, AvailableResource> availableResources;

    ProjectSimulation(List<Project> projects, Map<String, AvailableResource> availableResources) {
        this.projects = projects;
        this.availableResources = availableResources;
    }

    ProjectAllocationResult allocate(String id, Project project, String name, String type, TimeSlot forSlot) {
        if (projects.contains(project)) {
            availabilitiesOf(id).remove(name, type, forSlot);
            project.allocate(id, name, type, forSlot);
            return ProjectAllocationResult.Success;
        }
        return ProjectAllocationResult.Failure;
    }

    private AvailableResource availabilitiesOf(String id) {
        return availableResources
                .getOrDefault(id, new AvailableResource(id, new ArrayList<>()));
    }

    void remove(String resourceId, String name, String type, Project project, TimeSlot fromSlot) {
        if (projects.contains(project)) {
            List<CapabilityTimeSlot> released = project.release(resourceId, name, type, fromSlot);
            availabilitiesOf(resourceId).addAll(name, type, released);
        }
    }

    optimization.Result calculate() {
        //C-S
        return new OptimizationFacade()
                .calculate(simulated(projects),
                        availableResources
                                .values()
                                .stream()
                                .map(AvailableResource::toResources)
                                .flatMap(List::stream)
                                .toList());
    }

    private List<optimization.Project> simulated(List<Project> projects) {
        return projects
                .stream()
                .map(Project::asSimulation)
                .toList();
    }

    Double differenceAfterReleasingResource(String resourceId, Project project, String name, String type, TimeSlot... timeSlots) {
        Result result1 = this.calculate();
        Arrays.asList(timeSlots).forEach(slot -> remove(resourceId, name, type, project, slot));
        Result result2 = this.calculate();
        return result2.profit() - result1.profit();
    }
}


class Test {

    public static void main(String[] args) {
        TimeSlot june = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 6);
        TimeSlot oneDayInJune = TimeSlot.createDailyTimeSlotAtUTC(2023, 6, 2);
        TimeSlot anotherDayInJune = TimeSlot.createDailyTimeSlotAtUTC(2023, 6, 5);
        TimeSlot oneDayInOctober = TimeSlot.createDailyTimeSlotAtUTC(2023, 10, 5);
        // Tworzenie zasobów
        AvailableResource anna =
                new AvailableResource("Ania", "Java Developer", "SKILL", june);
        AvailableResource annaOct =
                new AvailableResource("Ania", "Java Developer", "SKILL", oneDayInOctober);
        AvailableResource marek =
                new AvailableResource("Marek", "Web Designer", "SKILL", june);
        AvailableResource tool1 =
                new AvailableResource("XPS123", "Laptop", "DEVICE", june);

        // Creating projects
        Project project1 = new Project("Website Creation", 1000, 3000, 30);
        Project project2 = new Project("Database Setup", 1500, 4000, 500);
        Project project3 = new Project("Shit Setup", 1500, 1800, 500);

        project1.requires("Java Developer", "SKILL", june);
        project2.requires("Java Developer", "SKILL", oneDayInJune);
        project2.requires("Java Developer", "SKILL", anotherDayInJune);
        project1.requires("Java Developer", "SKILL", oneDayInOctober);
        project3.requires("Java Developer", "SKILL", oneDayInOctober);

        Map<String, AvailableResource> availableResources = new HashMap<>();
        availableResources.put(anna.resourceId(), anna);
        availableResources.put(anna.resourceId(), annaOct);
        availableResources.put(marek.resourceId(), marek);
        availableResources.put(tool1.resourceId(), tool1);

        List<Project> projects = Arrays.asList(project1, project2, project3);

        ProjectSimulation projectManipulation = new ProjectSimulation(
                projects,
                availableResources);

        projectManipulation.allocate("Ania", project1, "Java Developer", "SKILL", june);

        Result calculate = projectManipulation.calculate();
        System.out.println("Max Profit: " + calculate.profit());

        Double difference = projectManipulation.differenceAfterReleasingResource("Ania", project1, "Java Developer", "SKILL", oneDayInJune, anotherDayInJune, oneDayInOctober);
        System.out.println("diff: " + difference);
    }


}

