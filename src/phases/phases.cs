using System;
using System.Collections.Generic;
using System.Linq;

public class Stage
{
    public string Name { get; }
    public List<Stage> Dependencies { get; } = new List<Stage>();

    public Stage(string name)
    {
        Name = name;
    }

    public Stage DependsOn(Stage stage)
    {
        Dependencies.Add(stage);
        return this;
    }

    public override string ToString()
    {
        return Name;
    }
}

public class Stages
{
    public List<Stage> AllStages { get; } = new List<Stage>();

    public Stages(List<Stage> stages)
    {
        AllStages = stages;
    }

    public Stages Add(Stage stage)
    {
        AllStages.Add(stage);
        return this;
    }

    public Stages WithAllDependenciesPresentIn(List<Stage> collection)
    {
        return new Stages(AllStages.Where(s => collection.Intersect(s.Dependencies).Count() == s.Dependencies.Count).ToList());
    }

    public Stages RemoveAll(List<Stage> collection)
    {
        return new Stages(AllStages.Where(s => !collection.Contains(s)).ToList());
    }

    public override string ToString()
    {
        return String.Join(",", AllStages);
    }
}

public class Phase
{
    public Stages StageList { get; }

    public Phase(Stages stages)
    {
        StageList = stages;
    }

    public override string ToString()
    {
        return $"Phase: {StageList}";
    }
}

public class Phases
{
    public List<Phase> AllPhases { get; } = new List<Phase>();

    public static Phases Empty()
    {
        return new Phases(new List<Phase>());
    }

    public Phases(List<Phase> phases)
    {
        AllPhases = phases;
    }

    public Phases Add(Phase phase)
    {
        AllPhases.Add(phase);
        return this;
    }

    public HashSet<Stage> AllStages()
    {
        return new HashSet<Stage>(AllPhases.SelectMany(p => p.StageList.AllStages));
    }
}

public class Test
{
    public static void Main(string[] args)
    {
        Stage stage1 = new Stage("Stage1");
        Stage stage2 = new Stage("Stage2").DependsOn(stage1);
        Stage stage3 = new Stage("Stage3").DependsOn(stage1);
        Stage stage4 = new Stage("Stage4").DependsOn(stage1).DependsOn(stage2);

        Stages stages = new Stages(new List<Stage> { stage1, stage2, stage3, stage4 });

        Func<Stages, Phases> calculatePhases = (s) =>
        {
            Func<Stages, Phases, Phases> intermediatePhaseCreator = null;
            intermediatePhaseCreator = (remainingStages, accumulatedPhases) =>
            {
                var alreadyProcessedStages = accumulatedPhases.AllStages().ToList();
                var stagesWithoutDependencies = remainingStages.WithAllDependenciesPresentIn(alreadyProcessedStages);

                if (!stagesWithoutDependencies.AllStages.Any())
                    return accumulatedPhases;

                var newPhases = accumulatedPhases.Add(new Phase(stagesWithoutDependencies));
                remainingStages = remainingStages.RemoveAll(stagesWithoutDependencies.AllStages);
                return intermediatePhaseCreator(remainingStages, newPhases);
            };

            return intermediatePhaseCreator(s, Phases.Empty());
        };

        Phases phases = calculatePhases(stages);
        foreach (var phase in phases.AllPhases)
            Console.WriteLine(phase);
    }
}
