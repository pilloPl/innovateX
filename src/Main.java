import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.*;

class Main {

    public static void main(String[] args) {
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage2").dependsOn(stage1);
        Stage stage3 = new Stage("Stage3").dependsOn(stage1);
        Stage stage4 = new Stage("Stage4");

        Stages stages = new Stages(Arrays.asList(stage1, stage2, stage3, stage4));
        CreatePhasesFunction function = new CreatePhasesFunction(new IntermediatePhaseCreator());
        Phases phases = function.apply(stages);

        phases.all().forEach(System.out::println);
    }
}

record Stage(String name, Stages dependencies) {
    Stage(String name) {
        this(name, new Stages(new ArrayList<>()));
    }

    Stage dependsOn(Stage stage) {
        return new Stage(this.name, this.dependencies.add(stage));
    }

    @Override
    public String toString() {
        return name;
    }
}

record Phase(Stages stages) {
    @Override
    public String toString() {
        return "Phase: " + stages;
    }
}

record Stages(List<Stage> stages) {

    List<Stage> all() {
        return Collections.unmodifiableList(stages);
    }

    Stages add(Stage stage) {
        List<Stage> newStages =
                concat(stages.stream(), of(stage)).collect(toList());
        return new Stages(newStages);
    }

    @Override
    public String toString() {
        return "Stages{" +
                "stages=" + stages +
                '}';
    }

    boolean isEmpty() {
        return all().isEmpty();
    }

    Stages withAllDependenciesPresentIn(Collection<Stage> stages) {
        return new Stages(this.all().stream()
                .filter(s -> stages.containsAll(s.dependencies().all()))
                .collect(toList()));
    }

    Stages removeAll(Collection<Stage> stages) {
        return new Stages(this.all().stream()
                .filter(s -> !stages.contains(s))
                .collect(toList()));
    }
}

record Phases(List<Phase> phases) {

    static Phases empty() {
        return new Phases(new ArrayList<>());
    }

    List<Phase> all() {
        return Collections.unmodifiableList(phases);
    }

    Phases add(Phase phase) {
        List<Phase> newPhases = concat(
                phases.stream(), of(phase)).collect(toList());
        return new Phases(newPhases);
    }

    Set<Stage> allStages() {
        return this.all().stream()
                .flatMap(phase -> phase.stages().all().stream())
                .collect(Collectors.toSet());
    }
}


class CreatePhasesFunction implements Function<Stages, Phases> {
    private final BiFunction<Stages, Phases, Phases> createPhasesRecursively;

    public CreatePhasesFunction(BiFunction<Stages, Phases, Phases> createPhasesRecursively) {
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

        Stages stagesWithoutDependencies = remainingStages.withAllDependenciesPresentIn(alreadyProcessedStages);

        if (stagesWithoutDependencies.isEmpty()) {
            return accumulatedPhases;
        }

        Phases newPhases = accumulatedPhases.add(new Phase(stagesWithoutDependencies));
        remainingStages = remainingStages.removeAll(stagesWithoutDependencies.all());

        return this.apply(remainingStages, newPhases);
    }
}