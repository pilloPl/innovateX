
type Resource = {
    id: string;
    name: string;
    resourceType: string;
};

class MissingResource {
    constructor(public name: string, public resourceType: string) {}

    canBeAllocatedBy(resource: Resource): boolean {
        return this.resourceType === resource.resourceType;
    }
}

class Project {
    constructor(
        public name: string,
        public estimatedCost: number,
        public estimatedEarnings: number,
        public risk: number,
        public penalty: number,
        public missingResource: MissingResource[]
    ) {}

    estimatedProfit(): number {
        return this.estimatedEarnings - this.estimatedCost;
    }
}

type CalculateProfitQuery = {
    projects: Project[];
    availableResources: Resource[];

    orderedProjects: () => Project[];
};

type Result = {
    profit: number;
    projects: Project[];

    toString: () => string;
};

class ChoseOptimalProjects {
    apply(query: CalculateProfitQuery): Result {
        const totalResources = query.availableResources.length;
        let dp: number[] = new Array(totalResources + 1).fill(0);
        let projectLists: Project[][] = new Array(totalResources + 1).fill([]);
        let allocatedResources: Set<Resource>[] = new Array(totalResources + 1).fill(new Set());

        for (let project of query.orderedProjects()) {
            let allocatableResources = this.resourcesFromRequired(project.missingResource, query.availableResources);
            if (allocatableResources.length === 0) continue;
            let projectProfit = project.estimatedProfit();
            let allocatableResourcesCount = allocatableResources.length;

            for (let j = totalResources; j >= allocatableResourcesCount; j--) {
                if (!this.isResourceAllocated(allocatableResources, allocatedResources[j - allocatableResourcesCount])) {
                    if (dp[j] < projectProfit + dp[j - allocatableResourcesCount]) {
                        dp[j] = projectProfit + dp[j - allocatableResourcesCount];
                        projectLists[j] = [...projectLists[j - allocatableResourcesCount], project];
                        allocatedResources[j] = new Set([...allocatedResources[j], ...allocatableResources]);
                    }
                }
            }
        }
        return {
            profit: dp[totalResources],
            projects: projectLists[totalResources],
            toString: () => `Result{profit=${dp[totalResources]}, projects=${projectLists[totalResources]}}`
        };
    }

    private isResourceAllocated(required: Resource[], allocated: Set<Resource>): boolean {
        return required.some(res => allocated.has(res));
    }

    private resourcesFromRequired(requiredResources: MissingResource[], availableResources: Resource[]): Resource[] {
        return requiredResources.flatMap(req => availableResources.filter(resource => resource.name === req.name && resource.resourceType === req.resourceType));
    }
}
function test() {
    // Creating resources
    const skill1: Resource = { id: "Ania", name: "Java Developer", resourceType: "SKILL" };
    const skill2: Resource = { id: "Marek", name: "Web Designer", resourceType: "SKILL" };
    const tool1: Resource = { id: "XPS", name: "Laptop", resourceType: "DEVICE" };

    // Creating projects
    const project1 = new Project(
        "Website Creation",
        1000,
        3000,
        30,
        500,
        [new MissingResource("Web Designer", "SKILL")]
    );

    const project2 = new Project(
        "Database Setup",
        1500,
        4000,
        50,
        800,
        [new MissingResource("Web Designer", "SKILL")]
    );

    const projectsToOptimize: Project[] = [project1, project2];
    const resourcesWeHave: Resource[] = [skill1, skill2, tool1];
    const query: CalculateProfitQuery = { projects: projectsToOptimize, availableResources: resourcesWeHave, orderedProjects: function () { return this.projects.sort((a, b) => b.estimatedProfit() - a.estimatedProfit()); } };
    const chooser = new ChoseOptimalProjects();
    const result = chooser.apply(query);

    console.log("Max Profit:", result.profit);
    console.log("Selected Projects:", result.projects);
}

test();
