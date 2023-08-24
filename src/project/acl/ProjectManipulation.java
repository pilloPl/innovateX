package project.acl;

import simulation.Result;
import simulation.Test;

import java.util.List;

class ProjectManipulation {

    private List<Project> projects;
    private List<AvailableResource> availableResources;

    ProjectManipulation(List<Project> projects, List<AvailableResource> availableResources) {
        this.projects = projects;
        this.availableResources = availableResources;
    }

    AllocatedResource allocate(AvailableResource availableResource, Project project) {
        if (projects.contains(project)) {
            AllocatedResource allocated = project.allocate(availableResource);
            availableResources.remove(availableResource);
            return allocated;
        }
        return null;
    }

    AvailableResource remove(AllocatedResource allocatedResource, Project project) {
        if (projects.contains(project)) {
            AvailableResource availableResource = project.removeResource(allocatedResource);
            availableResources.add(availableResource);
            return availableResource;
        }
        return null;
    }

    simulation.Result calculate() {
        return new Test().calculate(simulated(projects), availableResources.stream().map(ar -> new simulation.Resource(ar.id(), ar.name(), ar.type().name())).toList());
    }

    private List<simulation.Project> simulated(List<Project> projects) {
        return projects.stream().map(Project::asSimulation).toList();
    }

    Result profitAfterReleasingResource(AllocatedResource allocatedResource, Project project) {
        remove(allocatedResource, project);
        return calculate();
    }

    Double differenceAfterReleasingResource(AllocatedResource allocated, Project project) {
        Result result1 = this.calculate();

        Result result2 = this.profitAfterReleasingResource(allocated, project);

        return result2.profit() - result1.profit();
    }
}
