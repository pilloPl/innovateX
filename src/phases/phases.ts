type StageDependencies = Stage[];

class Stage {
    constructor(public name: string, public dependencies: StageDependencies = []) {}

    dependsOn(stage: Stage): Stage {
        this.dependencies.push(stage);
        return this;
    }

    toString(): string {
        return this.name;
    }
}

class Stages {
    constructor(public stages: Stage[] = []) {}

    add(stage: Stage): Stages {
        this.stages.push(stage);
        return this;
    }

    withAllDependenciesPresentIn(collection: Stage[]): Stages {
        return new Stages(this.stages.filter(s => s.dependencies.every(dep => collection.includes(dep))));
    }

    removeAll(collection: Stage[]): Stages {
        return new Stages(this.stages.filter(s => !collection.includes(s)));
    }

    toString(): string {
        return this.stages.toString();
    }
}

class Phase {
    constructor(public stages: Stages) {}

    toString(): string {
        return `Phase: ${this.stages}`;
    }
}

class Phases {
    constructor(public phases: Phase[] = []) {}

    static empty(): Phases {
        return new Phases();
    }

    add(phase: Phase): Phases {
        this.phases.push(phase);
        return this;
    }

    allStages(): Set<Stage> {
        return new Set(this.phases.map(p => p.stages.stages).reduce((acc, val) => acc.concat(val), []));
    }
}

type CalculatePhasesFunc = (stages: Stages) => Phases;

const calculatePhases: CalculatePhasesFunc = (stages) => {
    const intermediatePhaseCreator = (remainingStages: Stages, accumulatedPhases: Phases): Phases => {
        const alreadyProcessedStages: Stage[] = [...accumulatedPhases.allStages()];
        const stagesWithoutDependencies = remainingStages.withAllDependenciesPresentIn(alreadyProcessedStages);

        if (stagesWithoutDependencies.stages.length === 0) {
            return accumulatedPhases;
        }

        const newPhases = accumulatedPhases.add(new Phase(stagesWithoutDependencies));
        remainingStages = remainingStages.removeAll(stagesWithoutDependencies.stages);
        return intermediatePhaseCreator(remainingStages, newPhases);
    };

    return intermediatePhaseCreator(stages, Phases.empty());
};

// Test
const stage1 = new Stage("Stage1");
const stage2 = new Stage("Stage2").dependsOn(stage1);
const stage3 = new Stage("Stage3").dependsOn(stage1);
const stage4 = new Stage("Stage4").dependsOn(stage1).dependsOn(stage2);

const stages = new Stages([stage1, stage2, stage3, stage4]);
const phases = calculatePhases(stages);
phases.phases.forEach(phase => console.log(phase.toString()));
