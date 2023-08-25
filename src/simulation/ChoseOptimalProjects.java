package simulation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


record Project(String name, double estimatedCost, double estimatedEarnings, int risk, double penalty,
               List<MissingResource> missingResource) {

    double estimatedProfit() {
        return estimatedEarnings - estimatedCost;
    }
}

record TimeSlot(Instant from, Instant to) {

    static TimeSlot createMonthlyTimeSlotAtUTC(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        Instant from = startOfMonth.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant to = endOfMonth.atTime(23, 59, 59).atZone(ZoneId.of("UTC")).toInstant();
        return new TimeSlot(from, to);
    }

    boolean within(TimeSlot other) {
        return !this.from.isBefore(other.from) && !this.to.isAfter(other.to);
    }
}

record Resource(String id, String name, String resourceType, TimeSlot timeSlot) {
}

record MissingResource(String name, String resourceType, TimeSlot timeSlot) {

    boolean canBeAllocatedBy(Resource resource) {
        return  resource.name().equals(name) &&
                resource.resourceType().equals(resourceType) &&
                timeSlot.within(resource.timeSlot());
    }
}

class ChoseOptimalProjects implements Function<CalculateProfitQuery, Result> {

    @Override
    public Result apply(CalculateProfitQuery query) {
        int totalResources = query.availableResources().size();

        double[] dp = new double[totalResources + 1];
        List<Project>[] projectLists = new List[totalResources + 1];
        List<Set<Resource>> allocatedResources = new ArrayList<>(totalResources + 1);

        List<Project> automaticallyIncludedProjects =
                query.projects().stream()
                        .filter(project -> project.missingResource().isEmpty()).toList();

        double guaranteedProfit = automaticallyIncludedProjects.stream()
                .mapToDouble(Project::estimatedProfit)
                .sum();


        for (int i = 0; i <= totalResources; i++) {
            projectLists[i] = new ArrayList<>();
            allocatedResources.add(new HashSet<>());
        }

        for (Project project : query.orderedProjects()) {
            List<Resource> allocatableResources = resourcesFromRequired(project.missingResource(), query.availableResources());
            if (allocatableResources.isEmpty())
                continue;

            double projectProfit = project.estimatedProfit();
            int allocatableResourcesCount = allocatableResources.size();

            for (int j = totalResources; j >= allocatableResourcesCount; j--) {
                // Check if availableResources are already allocated
                if (!isResourceAllocated(allocatableResources, allocatedResources.get(j - allocatableResourcesCount))) {
                    if (dp[j] < projectProfit + dp[j - allocatableResourcesCount]) {
                        dp[j] = projectProfit + dp[j - allocatableResourcesCount];

                        projectLists[j] = new ArrayList<>(projectLists[j - allocatableResourcesCount]);
                        projectLists[j].add(project);

                        allocatedResources.get(j).addAll(allocatableResources);
                    }
                }
            }

        }
        projectLists[totalResources].addAll(automaticallyIncludedProjects);
        return new Result(dp[totalResources] + guaranteedProfit, projectLists[totalResources]);
    }

    private boolean isResourceAllocated(List<Resource> required, Set<Resource> allocated) {
        return required.stream().anyMatch(allocated::contains);
    }

    private List<Resource> resourcesFromRequired(List<MissingResource> requiredResources, List<Resource> availableResources) {
        return requiredResources.stream()
                .flatMap(requiredResource -> availableResources.stream()
                        .filter(requiredResource::canBeAllocatedBy))
                .collect(Collectors.toList());
    }

}

record CalculateProfitQuery(List<Project> projects,
                            List<Resource> availableResources) {

    List<Project> orderedProjects() {
        return projects.stream().sorted(Comparator.comparing(Project::estimatedProfit).reversed()).toList();
    }
}

record Result(Double profit, List<Project> projects) {
    @Override
    public String toString() {
        return "Result{" +
                "profit=" + profit +
                ", projects=" + projects +
                '}';
    }
}

class Test {

    public static void main(String[] args) {
        TimeSlot june = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 6);
        TimeSlot october = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 10);

        Resource skill1 = new Resource("Ania", "Java Developer", "SKILL", june);
        Resource skill2 = new Resource("Marek", "Web Designer", "SKILL", october);
        Resource tool1 = new Resource("XPS", "Laptop", "DEVICE", october);

        // Creating projects
        Project project1 = new Project("Website Creation", 1000, 3000, 30, 500,
                Arrays.asList(
                        new MissingResource("Web Designer", "SKILL", october)
                )
        );

        Project project2 = new Project("Database Setup", 1500, 4000, 50, 800,
                Arrays.asList(
                        new MissingResource("Web Designer", "SKILL", october)
                )
        );


        List<Project> projectsToOptimize = Arrays.asList(project1, project2);
        List<Resource> resourcesWeHave = Arrays.asList(skill1, skill2, tool1);
        CalculateProfitQuery query = new CalculateProfitQuery(projectsToOptimize, resourcesWeHave);
        Result result = new ChoseOptimalProjects().apply(query);

        System.out.println("Max Profit: " + result.profit());
        System.out.println("Selected Projects: " + result.projects());
    }


}


