package projects;

import java.util.List;

record Project(String name, double estimatedBudget, double estimatedEarnings, int risk, double penalty,
                      List<RequiredResource> requiredResources) {

}
