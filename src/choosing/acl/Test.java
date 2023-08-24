package choosing.acl;

import phases.CalculatePhases;
import phases.Phases;
import phases.Stage;
import phases.Stages;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

record ChosenStage(String name, LocalDate startDate, LocalDate finishAt, Set<String> resources) {
}

class Test {

    public static void main(String[] args) {
        // Sample data
        ChosenStage chosenStage1 = new ChosenStage("Stage1",
                LocalDate.of(2023, 1, 7),
                LocalDate.of(2023, 1, 10),
                Set.of("resource1"));
        ChosenStage chosenStage2 = new ChosenStage("Stage2",
                LocalDate.of(2023, 1, 5),
                LocalDate.of(2023, 1, 15),
                Set.of("resource2", "resource1"));
        ChosenStage chosenStage3 = new ChosenStage("Stage3",
                LocalDate.of(2023, 1, 5),
                LocalDate.of(2023, 1, 12),
                Set.of("resource4"));
        ChosenStage chosenStage4 = new ChosenStage("Stage4",
                LocalDate.of(2023, 1, 8),
                LocalDate.of(2023, 1, 20),
                Set.of("resource4", "resource6"));
        List<ChosenStage> chosenStages = Arrays.asList(chosenStage1, chosenStage2, chosenStage3, chosenStage4);


        Phases phases = new CreateStages().andThen(new CalculatePhases()).apply(chosenStages);

        phases.all().forEach(System.out::println);

    }


}

class CreateStages implements Function<List<ChosenStage>, Stages> {

    @Override
    public Stages apply(List<ChosenStage> chosenStages) {
        Map<String, Stage> stageMap = new HashMap<>();

        // Creating new stages without dependencies
        for (ChosenStage cs : chosenStages) {
            stageMap.put(cs.name(), new Stage(cs.name()));
        }

        // Setting dependencies based on shared resources
        for (int i = 0; i < chosenStages.size(); i++) {
            ChosenStage cs = chosenStages.get(i);
            for (int j = i + 1; j < chosenStages.size(); j++) {
                ChosenStage other = chosenStages.get(j);
                if (!cs.name().equals(other.name()) && !Collections.disjoint(cs.resources(), other.resources())) {
                    if (cs.startDate().isBefore(other.startDate())) {
                        Stage stage = stageMap.get(other.name()).dependsOn(stageMap.get(cs.name()));
                        stageMap.put(other.name(), stage);
                    } else if (!other.startDate().isAfter(cs.startDate())) {
                        Stage stage = stageMap.get(cs.name()).dependsOn(stageMap.get(other.name()));
                        stageMap.put(cs.name(), stage);
                    }
                }
            }
        }

        return new Stages(new ArrayList<>(stageMap.values()));
    }
}
