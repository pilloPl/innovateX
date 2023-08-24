package simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class ChoseOptimalProjects implements Function<CalculateProfitQuery, Result> {

    @Override
    public Result apply(CalculateProfitQuery query) {

        int totalResources = query.resources().size();
        double[] dp = new double[totalResources + 1];
        List<Project>[] projectLists = new List[totalResources + 1];

        for (int i = 0; i <= totalResources; i++) {
            projectLists[i] = new ArrayList<>();
        }

        for (Project project : query.projects()) {
            int requiredResources = countAllocatableResources(project.requiredResources(), query.resources());
            double projectProfit = query.profitFunction().apply(project);

            if (requiredResources == project.requiredResources().size()) {
                for (int j = totalResources; j >= requiredResources; j--) {
                    if (dp[j] < projectProfit + dp[j - requiredResources]) {
                        dp[j] = projectProfit + dp[j - requiredResources];
                        projectLists[j] = new ArrayList<>(projectLists[j - requiredResources]);
                        projectLists[j].add(project);
                    }
                }
            }
        }

        return new Result(dp[totalResources], projectLists[totalResources]);
    }

    private int countAllocatableResources(List<RequiredResource> requiredResources, List<Resource> availableResources) {
        List<Resource> allocatableResources = availableResources.stream()
                .filter(resource -> requiredResources.contains(new RequiredResource(resource.name(), resource.type())))
                .toList();

        return allocatableResources.size();
    }

}

record CalculateProfitQuery(List<Project> projects,
                            List<Resource> resources,
                            Function<Project, Double> profitFunction) {};
