package optimization;

import java.util.List;

public record Project(String name, double estimatedCost, double estimatedEarnings, int risk, double penalty,
                      List<MissingResource> missingResource) {

    double estimatedProfit() {
        return estimatedEarnings - estimatedCost;
    }
}
