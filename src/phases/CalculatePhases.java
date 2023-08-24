package phases;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;



public class CalculatePhases implements Function<Stages, Phases> {
    private final BiFunction<Stages, Phases, Phases> createPhasesRecursively;

    public CalculatePhases() {
        this.createPhasesRecursively = new IntermediatePhaseCreator();
    }

    @Override
    public Phases apply(Stages stages) {
        return createPhasesRecursively.apply(stages, Phases.empty());
    }
}

class IntermediatePhaseCreator implements BiFunction<Stages, Phases, Phases> {

    @Override
    public Phases apply(Stages remainingStages, Phases accumulatedPhases) {
        Set<Stage> alreadyProcessedStages = accumulatedPhases.allStages();
        Stages stagesWithoutDependencies =
                remainingStages
                        .withAllDependenciesPresentIn(alreadyProcessedStages);

        if (stagesWithoutDependencies.isEmpty()) {
            return accumulatedPhases;
        }

        Phases newPhases = accumulatedPhases.add(new Phase(stagesWithoutDependencies));
        remainingStages = remainingStages.removeAll(stagesWithoutDependencies.all());
        return this.apply(remainingStages, newPhases);
    }

}


class Test {

    public static void main(String[] args) {
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage2").dependsOn(stage1);
        Stage stage3 = new Stage("Stage3").dependsOn(stage1);
        Stage stage4 = new Stage("Stage4").dependsOn(stage1).dependsOn(stage2);

        Stages stages = new Stages(Arrays.asList(stage1, stage2, stage3, stage4));
        CalculatePhases function = new CalculatePhases();
        Phases phases = function.apply(stages);
        phases.all().forEach(System.out::println);
    }

}
