package phases;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculatePhasesTest {

    static final CalculatePhases CALCULATE_PHASES = new CalculatePhases();

    @Test
    void testPhasesCalculation() {
        //given
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage2").dependsOn(stage1);
        Stage stage3 = new Stage("Stage3").dependsOn(stage1);
        Stage stage4 = new Stage("Stage4").dependsOn(stage1).dependsOn(stage2);
        Stages stages = new Stages(Arrays.asList(stage1, stage2, stage3, stage4));

        //when
        Phases phases = CALCULATE_PHASES.apply(stages);

        //then
        assertEquals(3, phases.all().size(), "There should be 3 phases in total");
        assertEquals(1, phases.all().get(0).stages().all().size(), "First phase should have one stage");
        assertTrue(phases.all().get(0).stages().all().contains(stage1), "First phase should contain Stage1");

        // Phase 2 should contain Stage2 and Stage3 as they both depend on Stage1
        assertEquals(2, phases.all().get(1).stages().all().size(), "Second phase should have two stages");
        assertTrue(phases.all().get(1).stages().all().contains(stage2), "Second phase should contain Stage2");
        assertTrue(phases.all().get(1).stages().all().contains(stage3), "Second phase should contain Stage3");

        // Phase 3 should contain Stage4 as it depends on Stage1 and Stage2
        assertEquals(1, phases.all().get(2).stages().all().size(), "Third phase should have one stage");
        assertTrue(phases.all().get(2).stages().all().contains(stage4), "Third phase should contain Stage4");

    }

    @Test
    void testStagesWithoutDependencies() {
        //given
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage1");
        Stages stages = new Stages(List.of(stage1, stage2));

        //when
        Phases phases = CALCULATE_PHASES.apply(stages);

        //then
        assertEquals(1, phases.all().size(), "There should be 1 stage without dependencies");
    }

    @Test
    void testCyclicDependency() {
        //given
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage2").dependsOn(stage1);
        stage1 = stage1.dependsOn(stage2); // making it cyclic

        Stages stages = new Stages(Arrays.asList(stage1, stage2));

        //when
        Phases phases = CALCULATE_PHASES.apply(stages);

        //then
        // As the stages are cyclically dependent, no phases should be created
        assertTrue(phases.all().isEmpty(), "There should be no solvable phases due to cyclic dependencies");
    }

}