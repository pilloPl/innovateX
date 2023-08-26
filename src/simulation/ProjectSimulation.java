package simulation;

import optimization.OptimizationFacade;
import optimization.Resource;
import optimization.Result;
import shared.TimeSlot;

import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


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
            List<CapabilityTimeSlot> allocated = availabilitiesOf(id).remove(name, type, forSlot);
            project.allocateAll(id, name, type, allocated);
            return ProjectAllocationResult.Success;
        }
        return ProjectAllocationResult.Failure;
    }

    private AvailableResource availabilitiesOf(String id) {
        return availableResources
                .getOrDefault(id, new AvailableResource(id, new ArrayList<>()));
    }

    ProjectAllocationResult remove(String resourceId, String name, String type, Project project, TimeSlot fromSlot) {
        if (projects.contains(project)) {
            List<CapabilityTimeSlot> released = project.release(resourceId, name, type, fromSlot);
            availabilitiesOf(resourceId).addAll(name, type, released);
            return ProjectAllocationResult.Success;
        }
        return ProjectAllocationResult.Failure;
    }

    optimization.Result calculate() {
        //C-S
        return new OptimizationFacade()
                .calculate(simulated(projects),
                        availableResources
                                .values()
                                .stream()
                                .map(resource -> toOptimizationResources(resource.resourceId(), resource.resourceAvailabilities()))
                                .flatMap(List::stream)
                                .toList());
    }

    List<Resource> toOptimizationResources(String resourceId, List<CapabilityTimeSlot> resourceAvailabilities) {
        return ProjectSimulation.createContinuousAvailabilities(resourceAvailabilities).stream().map(r ->
                        new Resource(resourceId, r.name(), r.type(), r.timeSlot()))
                .toList();
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

    //ACL
    static List<CapabilityTimeSlot> createContinuousAvailabilities(List<CapabilityTimeSlot> resourceAvailabilities) {
        return mergeContinuousResourceAvailabilities(resourceAvailabilities);
    }

    private static List<CapabilityTimeSlot> mergeContinuousResourceAvailabilities(List<CapabilityTimeSlot> resourceAvailabilities) {
        return resourcesGroupedByNameAndType(resourceAvailabilities)
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    List<TimeSlot> timeSlots = entry
                            .getValue()
                            .stream()
                            .map(CapabilityTimeSlot::timeSlot)
                            .toList();
                    List<TimeSlot> mergedTimeSlots = TimeSlot.mergeContinuousTimeSlots(timeSlots);
                    return mergedTimeSlots.stream().map(ts -> new CapabilityTimeSlot(entry.getKey().get(0), entry.getKey().get(1), ts));
                })
                .collect(toList());
    }

    private static LinkedHashMap<List<String>, List<CapabilityTimeSlot>> resourcesGroupedByNameAndType(List<CapabilityTimeSlot> resources) {
        return resources.stream()
                .collect(groupingBy(
                        resource -> Arrays.asList(resource.name(), resource.type()),
                        LinkedHashMap::new, toList()
                ));
    }
}


class Test {

    public static void main(String[] args) {

        TimeSlot june = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 6);
        TimeSlot oneDayInJune = TimeSlot.createDailyTimeSlotAtUTC(2023, 6, 2);
        TimeSlot anotherDayInJune = new TimeSlot(oneDayInJune.to(), oneDayInJune.to().plus(1, ChronoUnit.DAYS));
        TimeSlot twoDays = oneDayInJune.addOneDay();
        TimeSlot oneDayInOctober = TimeSlot.createDailyTimeSlotAtUTC(2023, 10, 5);

        // Tworzenie zasobów
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
        project2.requires("Java Developer", "SKILL", twoDays);
        project3.requires("Java Developer", "SKILL", oneDayInOctober);

        List<CapabilityTimeSlot> capabilityTimeSlots = new ArrayList<>();
        CapabilityTimeSlot one = new CapabilityTimeSlot("Java Developer", "SKILL", oneDayInJune);
        CapabilityTimeSlot one2 = new CapabilityTimeSlot("Java Developer", "SKILL", anotherDayInJune);
        capabilityTimeSlots.add(one);
        capabilityTimeSlots.add(one2);

        List<CapabilityTimeSlot> continuousAvailabilities = List.of(one, one2);
        System.out.println(continuousAvailabilities);
        AvailableResource anna  = new AvailableResource("Anna", capabilityTimeSlots);

        Map<String, AvailableResource> availableResources = new HashMap<>();

        availableResources.put(anna.resourceId(), anna);
        availableResources.put(marek.resourceId(), marek);
        availableResources.put(tool1.resourceId(), tool1);

        List<Project> projects = Arrays.asList(project1, project2, project3);

        ProjectSimulation projectManipulation = new ProjectSimulation(
                projects,
                availableResources);

        projectManipulation.allocate("Ania", project1, "Java Developer", "SKILL", oneDayInJune);

        Result calculate = projectManipulation.calculate();
        System.out.println("Max Profit: " + calculate.profit());

        Double difference = projectManipulation.differenceAfterReleasingResource("Ania", project1, "Java Developer", "SKILL", oneDayInJune);
        System.out.println("diff: " + difference);
    }


}

