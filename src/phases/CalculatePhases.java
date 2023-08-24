package phases;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;


record Phase(Stages stages) {
    @Override
    public String toString() {
        return "phases.Phase: " + stages;
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


record Stage(String name, LocalDate start, LocalDate finish, Stages dependencies, Set<String> resources) {
    Stage(String name, LocalDate start, LocalDate finish, Set<String> resources) {
        this(name, start, finish, new Stages(new ArrayList<>()), resources);
    }

    Stage dependsOn(Stage stage) {
        if (this.overlaps(stage)) {
            throw new IllegalArgumentException("Cannot depend on a stage that finishes after or on the current stage's start.");
        }
        return new Stage(this.name, this.start, this.finish, this.dependencies.add(stage), this.resources);
    }

    private boolean overlaps(Stage stage) {
        return !stage.finish.isBefore(this.start);
    }

    private boolean canDependOn(Stage stage) {
        return this.start.isAfter(stage.finish);
    }

    @Override
    public String toString() {
        return name + " (" + start + " to " + finish + ")";
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
        return "phases.Stages{" +
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

        Stages stagesWithoutDependencies = remainingStages
                .withAllDependenciesPresentIn(alreadyProcessedStages);

        // Start with an empty set of resources for the new phase.
        Set<String> resourcesForThisPhase = new HashSet<>();
        List<Stage> stagesForThisPhase = new ArrayList<>();

        for (Stage stage : stagesWithoutDependencies.all()) {
            Set<String> stageResources = stage.resources();
            // Check if the current stage resources overlap with any resources in this phase.
            if (Collections.disjoint(resourcesForThisPhase, stageResources)) {
                resourcesForThisPhase.addAll(stageResources);
                stagesForThisPhase.add(stage);
            }
        }

        if (stagesForThisPhase.isEmpty()) {
            return accumulatedPhases;
        }

        Phases newPhases = accumulatedPhases.add(new Phase(new Stages(stagesForThisPhase)));
        remainingStages = remainingStages.removeAll(stagesForThisPhase);
        return this.apply(remainingStages, newPhases);
    }
}


class Test {

    public static void main(String[] args) {
        LocalDate date1Start = LocalDate.of(2023, 8, 24);
        LocalDate date1Finish = LocalDate.of(2023, 8, 25);

        LocalDate date2Start = LocalDate.of(2023, 8, 26);
        LocalDate date2Finish = LocalDate.of(2023, 8, 27);

        LocalDate date3Start = LocalDate.of(2023, 8, 28);
        LocalDate date3Finish = LocalDate.of(2023, 8, 29);

        LocalDate date4Start = LocalDate.of(2023, 8, 30);
        LocalDate date4Finish = LocalDate.of(2023, 8, 31);

        Stage stage1 = new Stage("Stage1", date1Start, date1Finish, Set.of("R1"));
        Stage stage2 = new Stage("Stage2", date2Start, date2Finish, Set.of("R1")); // Same resource as stage1, should be in different phase
        Stage stage3 = new Stage("Stage3", date3Start, date3Finish, Set.of("R2")).dependsOn(stage1);
        Stage stage4 = new Stage("Stage4", date4Start, date4Finish, Set.of("R3")).dependsOn(stage1).dependsOn(stage2);

        Stages stages = new Stages(Arrays.asList(stage1, stage2, stage3, stage4));
        CalculatePhases function = new CalculatePhases(new IntermediatePhaseCreator());
        Phases phases = function.apply(stages);
        phases.all().forEach(System.out::println);
    }
}