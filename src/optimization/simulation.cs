using System;
using System.Collections.Generic;
using System.Linq;

namespace Simulation
{
    public record Resource(string Id, string Name, string ResourceType);

    public record MissingResource(string Name, string ResourceType)
    {
        public bool CanBeAllocatedBy(Resource resource)
        {
            return this.ResourceType == resource.ResourceType;
        }
    }

    public record Project(string Name, double EstimatedCost, double EstimatedEarnings, int Risk, double Penalty, List<MissingResource> MissingResources)
    {
        public double EstimatedProfit()
        {
            return EstimatedEarnings - EstimatedCost;
        }

		public override string ToString()
        {
            return $"{Name}";
        }
    }

    public class ChoseOptimalProjects
    {
        public Result Apply(CalculateProfitQuery query)
        {
            int totalResources = query.AvailableResources.Count;
            double[] dp = new double[totalResources + 1];
            List<Project>[] projectLists = new List<Project>[totalResources + 1];
            List<HashSet<Resource>> allocatedResources = new List<HashSet<Resource>>(totalResources + 1);

            for (int i = 0; i <= totalResources; i++)
            {
                projectLists[i] = new List<Project>();
                allocatedResources.Add(new HashSet<Resource>());
            }

            foreach (Project project in query.OrderedProjects())
            {
                List<Resource> allocatableResources = ResourcesFromRequired(project.MissingResources, query.AvailableResources);

                if (!allocatableResources.Any())
                    continue;

                double projectProfit = project.EstimatedProfit();
                int allocatableResourcesCount = allocatableResources.Count;

                for (int j = totalResources; j >= allocatableResourcesCount; j--)
                {
                    if (!IsResourceAllocated(allocatableResources, allocatedResources[j - allocatableResourcesCount]))
                    {
                        if (dp[j] < projectProfit + dp[j - allocatableResourcesCount])
                        {
                            dp[j] = projectProfit + dp[j - allocatableResourcesCount];

                            projectLists[j] = new List<Project>(projectLists[j - allocatableResourcesCount]);
                            projectLists[j].Add(project);

                            allocatedResources[j].UnionWith(allocatableResources);
                        }
                    }
                }
            }
            return new Result(dp[totalResources], projectLists[totalResources]);
        }

        private bool IsResourceAllocated(List<Resource> required, HashSet<Resource> allocated)
        {
            return required.Any(allocated.Contains);
        }

        private List<Resource> ResourcesFromRequired(List<MissingResource> requiredResources, List<Resource> availableResources)
        {
            return requiredResources.SelectMany(req => availableResources.Where(resource => resource.Name == req.Name && resource.ResourceType == req.ResourceType)).ToList();
        }
    }

    public record CalculateProfitQuery(List<Project> Projects, List<Resource> AvailableResources)
    {
        public List<Project> OrderedProjects()
        {
            return Projects.OrderByDescending(p => p.EstimatedProfit()).ToList();
        }
    }

    public record Result(double Profit, List<Project> Projects)
    {
        public override string ToString()
        {
            return $"Result {{ profit = {Profit}, projects = {Projects} }}";
        }
    }

    public class Test
    {
        public static void Main()
        {
            Resource skill1 = new Resource("Ania", "Java Developer", "SKILL");
            Resource skill2 = new Resource("Marek", "Web Designer", "SKILL");
            Resource tool1 = new Resource("XPS", "Laptop", "DEVICE");

            Project project1 = new Project("Website Creation", 1000, 3000, 30, 500, new List<MissingResource> { new MissingResource("Web Designer", "SKILL") });
            Project project2 = new Project("Database Setup", 1500, 4000, 50, 800, new List<MissingResource> { new MissingResource("Web Designer", "SKILL") });

            List<Project> projectsToOptimize = new List<Project> { project1, project2 };
            List<Resource> resourcesWeHave = new List<Resource> { skill1, skill2, tool1 };
            CalculateProfitQuery query = new CalculateProfitQuery(projectsToOptimize, resourcesWeHave);
            Result result = new ChoseOptimalProjects().Apply(query);

            Console.WriteLine("Max Profit: " + result.Profit);
			Console.WriteLine("Selected Projects:");
            result.Projects.ForEach(project => Console.WriteLine(project.ToString()));
        }
    }
}
