package simulation;

import java.util.List;
import java.util.function.Function;

public class Test {

    public Result calculate(List<Project> projectsToOptimize, List<Resource> resourcesWeHave) {

        ChooseOptimalProjectWorkflow workflow = projects -> resources -> profitFunction -> new ChoseOptimalProjects()
                .apply(new CalculateProfitQuery(projects, resources, profitFunction));

        return workflow
                .apply(projectsToOptimize)
                .apply(resourcesWeHave)
                .apply(chooseProfitFunction());
    }

    private Function<Project, Double> chooseProfitFunction() {
        return project -> project.estimatedEarnings() - project.estimatedBudget();
    }
}



