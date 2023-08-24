package phases;

import java.util.*;

class Test {

    public static void main(String[] args) {
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage2").dependsOn(stage1);
        Stage stage3 = new Stage("Stage3").dependsOn(stage1);
        Stage stage4 = new Stage("Stage4").dependsOn(stage1).dependsOn(stage2);

        Stages stages = new Stages(Arrays.asList(stage1, stage2, stage3, stage4));
        CalculatePhases function = new CalculatePhases(new IntermediatePhaseCreator());
        Phases phases = function.apply(stages);
        phases.all().forEach(System.out::println);
    }
}


