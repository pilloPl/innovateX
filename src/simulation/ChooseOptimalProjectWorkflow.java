package simulation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

interface ChooseOptimalProjectWorkflow extends
        Function<List<Project>, Function<List<Resource>, Function<Function<Project, Double>, Result>>> {
}

class ChoseOptimalProjects implements Function<CalculateProfitQuery, Result> {

    @Override
    public Result apply(CalculateProfitQuery query) {
        List<Project> automaticallyIncludedProjects =
                query.projects().stream()
                .filter(project -> project.missingResource().isEmpty()).toList();

        double guaranteedProfit = automaticallyIncludedProjects.stream()
                .mapToDouble(query.profitFunction()::apply)
                .sum();

        int totalResources = query.availableResources().size();

        double[] dp = new double[totalResources + 1];
        List<Project>[] projectLists = new List[totalResources + 1];
        List<Set<Resource>> allocatedResources = new ArrayList<>(totalResources + 1);

        for (int i = 0; i <= totalResources; i++) {
            projectLists[i] = new ArrayList<>();
            allocatedResources.add(new HashSet<>());
        }

        for (Project project : query.orderedProjects()) {
            if (!project.missingResource().isEmpty()) {
                List<Resource> allocatableResources = resourcesFromRequired(project.missingResource(), query.availableResources());

                if (allocatableResources.isEmpty())
                    continue;

                double projectProfit = query.profitFunction().apply(project);
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
        }
        projectLists[totalResources].addAll(automaticallyIncludedProjects);
        return new Result(dp[totalResources] + guaranteedProfit, projectLists[totalResources]);
    }

    private boolean isResourceAllocated(List<Resource> required, Set<Resource> allocated) {
        return required.stream().anyMatch(allocated::contains);
    }

    private List<Resource> resourcesFromRequired(List<MissingResource> requiredResources, List<Resource> availableResources) {
        return requiredResources.stream()
                .flatMap(req -> availableResources.stream()
                        .filter(resource -> resource.name().equals(req.name()) && resource.resourceType().equals(req.resourceType())))
                .collect(Collectors.toList());
    }

}

record CalculateProfitQuery(List<Project> projects,
                            List<Resource> availableResources,
                            Function<Project, Double> profitFunction) {

    List<Project> orderedProjects() {
        return projects.stream().sorted(Comparator.comparing(profitFunction).reversed()).toList();
    }
}


