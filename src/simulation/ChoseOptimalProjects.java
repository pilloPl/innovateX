package simulation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class ChoseOptimalProjects implements Function<CalculateProfitQuery, Result> {

    @Override
    public Result apply(CalculateProfitQuery query) {
        int totalResources = query.resources().size();

        double[] dp = new double[totalResources + 1];
        List<Project>[] projectLists = new List[totalResources + 1];
        List<Set<Resource>> allocatedResources = new ArrayList<>(totalResources + 1);

        for (int i = 0; i <= totalResources; i++) {
            projectLists[i] = new ArrayList<>();
            allocatedResources.add(new HashSet<>());
        }

        for (Project project : query.orderedProjects()) {
            List<Resource> requiredResourcesList = resourcesFromRequired(project.requiredResources(), query.resources());

            if (requiredResourcesList.isEmpty()) continue;

            double projectProfit = query.profitFunction().apply(project);
            System.out.println("profit " + projectProfit + " of " + project.name());
            int requiredResourcesCount = requiredResourcesList.size();

            for (int j = totalResources; j >= requiredResourcesCount; j--) {
                // Check if resources are already allocated
                if (!isResourceAllocated(requiredResourcesList, allocatedResources.get(j - requiredResourcesCount))) {
                    if (dp[j] < projectProfit + dp[j - requiredResourcesCount]) {
                        dp[j] = projectProfit + dp[j - requiredResourcesCount];

                        projectLists[j] = new ArrayList<>(projectLists[j - requiredResourcesCount]);
                        projectLists[j].add(project);

                        allocatedResources.get(j).addAll(requiredResourcesList);
                    }
                }
            }
        }

        return new Result(dp[totalResources], projectLists[totalResources]);
    }

    private boolean isResourceAllocated(List<Resource> required, Set<Resource> allocated) {
        for (Resource r : required) {
            if (allocated.contains(r)) return true;
        }
        return false;
    }

    private List<Resource> resourcesFromRequired(List<RequiredResource> requiredResources, List<Resource> availableResources) {
        return requiredResources.stream()
                .flatMap(req -> availableResources.stream()
                        .filter(resource -> resource.name().equals(req.name()) && resource.type().equals(req.type())))
                .collect(Collectors.toList());
    }

}

record CalculateProfitQuery(List<Project> projects,
                            List<Resource> resources,
                            Function<Project, Double> profitFunction) {

    List<Project> orderedProjects() {
        return projects.stream().sorted(Comparator.comparing(profitFunction).reversed()).toList();
    }
};
