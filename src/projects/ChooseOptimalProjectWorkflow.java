package projects;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


import java.util.List;


interface ChooseOptimalProjectWorkflow extends
        Function<Supplier<Resource>, Function<CalculateProfitQuery, Result>> {};


class Test {

    static final Function<List<Project>, Function<List<Resource>, Function<Function<Project, Double>, Result>>> WORKFLOW = projects -> resources -> profitFunction -> new ChoseOptimalProjects()
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


record Result(Double profit, List<Project> projects) {


    @Override
    public String toString() {
        return "Result{" +
                "profit=" + profit +
                ", projects=" + projects +
                '}';
    }
}
