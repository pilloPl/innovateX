package optimization;

import java.util.List;

public class OptimizationFacade {

    public Result calculate(List<Project> projects, List<Resource> availableResources) {
        return new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, availableResources));
    }
}
