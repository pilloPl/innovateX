package simulation;

import java.util.List;

public record Project(String name, double estimatedBudget, double estimatedEarnings, int risk, double penalty,
                      List<MissingResource> missingResource) {

}


