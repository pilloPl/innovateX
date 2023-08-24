package phases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
