package phases;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public record Stages(List<Stage> stages) {

    public List<Stage> all() {
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

    public boolean isEmpty() {
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
