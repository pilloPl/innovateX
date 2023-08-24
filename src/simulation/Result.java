package simulation;

import java.util.List;

public record Result(Double profit, List<Project> projects) {
    @Override
    public String toString() {
        return "Result{" +
                "profit=" + profit +
                ", projects=" + projects +
                '}';
    }
}
