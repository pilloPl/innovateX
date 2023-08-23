package projects;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


import java.util.List;

record CalculateProfitQuery(List<Project> projects, List<Resource> resources, Function<Project, Double> profitFunction) {};

interface CalculatingRiskWorkflow extends Function<Supplier<Resource>, Function<CalculateProfitQuery, Result>> {};


class Main {

    public static final Function<List<Project>, Function<List<Resource>, Function<Function<Project, Double>, Result>>> WORKFLOW = projects -> resources -> profitFunction -> new ProjectOptimizer()
            .apply(new CalculateProfitQuery(projects, resources, profitFunction));

    public static void main(String[] args) {

        // Tworzenie zasobów
        Resource skill1 = new Resource("Ania", "Java Developer", ResourceType.SKILL);
        Resource skill2 = new Resource("Marek", "Web Designer", ResourceType.SKILL);
        Resource skill3 = new Resource("Staszek", "Database Admin", ResourceType.SKILL);
        Resource tool1 = new Resource("XPS", "Laptop", ResourceType.DEVICE);

        // Creating projects
        Project project1 = new Project("Website Creation", 1000, 3000, 30, 500,
                Arrays.asList(
                        new RequiredResource("Java Developer", ResourceType.SKILL),
                        new RequiredResource("Web Designer", ResourceType.SKILL)
                )
        );

        Project project2 = new Project("Database Setup", 1500, 4000, 50, 800,
                Arrays.asList(
                        new RequiredResource("Database Admin", ResourceType.SKILL)
                )
        );
        List<Project> projectsToOptimize = Arrays.asList(project1, project2);
        List<Resource> resourcesWeHave = Arrays.asList(skill1, skill2, tool1);

        Result result = WORKFLOW
                .apply(projectsToOptimize)
                .apply(resourcesWeHave)
                .apply(chooseProfitFunction());

        System.out.println("Max Profit: " + result.profit());
        System.out.println("Selected Projects: " + result.projects());
    }

    private static Function<Project, Double> chooseProfitFunction() {
        return project -> project.estimatedEarnings() - project.estimatedBudget();
    }
}

enum ResourceType {
    SKILL, PERMISSION, DEVICE
}

record Resource(String id, String name, ResourceType type) {}

record RequiredResource(String name, ResourceType type) {}

record Project(String name, double estimatedBudget, double estimatedEarnings, int risk, double penalty, List<RequiredResource> requiredResources) {

}


class ProjectOptimizer implements Function<CalculateProfitQuery, Result> {

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
                .collect(Collectors.toList());

        return allocatableResources.size();
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
