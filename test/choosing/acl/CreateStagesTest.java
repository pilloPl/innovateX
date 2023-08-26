package choosing.acl;

import org.junit.jupiter.api.Test;
import phases.Stage;
import phases.Stages;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateStagesTest {

    @Test
    void testStageDependencies() {
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

        //when
        Stages stages = new CreateStages().apply(chosenStages);

        System.out.println(stages);
        for (Stage stage : stages.all()) {
            System.out.println(stage.name() + " " + stage.dependencies().all());
        }

        //then
        assertTrue(getStage(stages, "Stage1").dependencies().stages().contains(getStage(stages, "Stage2")));
        assertEquals(1, getStage(stages, "Stage1").dependencies().all().size());

        assertEquals(0, getStage(stages, "Stage2").dependencies().all().size());

        assertEquals(0, getStage(stages, "Stage3").dependencies().all().size());

        assertEquals(1, getStage(stages, "Stage4").dependencies().all().size());
        assertTrue(getStage(stages, "Stage4").dependencies().stages().contains(getStage(stages, "Stage3")));
    }

    Stage getStage(Stages stages, String name) {
        return stages.all()
                .stream()
                .filter(stage -> stage.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}