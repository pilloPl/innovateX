package phases;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

class CalculatePhases implements Function<Stages, Phases> {
    private final BiFunction<Stages, Phases, Phases> createPhasesRecursively;

    public CalculatePhases(BiFunction<Stages, Phases, Phases> createPhasesRecursively) {
        this.createPhasesRecursively = createPhasesRecursively;
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
