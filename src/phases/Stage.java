package phases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

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
