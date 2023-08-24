package simulation;

import java.util.*;


import java.util.List;


class Main {

    public static void main(String[] args) {
        Test facade = new Test();

        // Tworzenie zasobów
        Resource skill1 = new Resource("Ania", "Java Developer", "SKILL");
        //Resource skill2 = new Resource("Marek", "Web Designer", "SKILL");
        Resource tool1 = new Resource("XPS", "Laptop", "DEVICE");

        // Creating projects
        Project project1 = new Project("Website Creation", 1000, 3000, 30, 500,
                Arrays.asList(
                )
        );

        Project project2 = new Project("Database Setup", 1500, 4000, 50, 800,
                Arrays.asList(
                        new MissingResource("Web Designer", "SKILL")
                )
        );


        List<Project> projectsToOptimize = Arrays.asList(project1, project2);
        List<Resource> resourcesWeHave = Arrays.asList(skill1, tool1);


        Result result = facade.calculate(projectsToOptimize, resourcesWeHave);

        System.out.println("Max Profit: " + result.profit());
        System.out.println("Selected Projects: " + result.projects());

        //sytuacja druga
        // Tworzenie zasobów
        // Creating projects

        Resource skill2 = new Resource("Marek", "Web Designer", "SKILL");

        Project project1B = new Project("Website Creation", 1000, 3000, 30, 500,
                Arrays.asList(
                        new MissingResource("Web Designer", "SKILL")
                )
        );

        Project project2B = new Project("Database Setup", 1500, 4000, 50, 800,
                Arrays.asList(
                        new MissingResource("Web Designer", "SKILL")
                )
        );


        List<Project> projectsToOptimizeB = Arrays.asList(project1B, project2B);
        List<Resource> resourcesWeHaveB = Arrays.asList(skill1, skill2, tool1);


        Result resultB = facade.calculate(projectsToOptimizeB, resourcesWeHaveB);

        System.out.println("Max Profit: " + resultB.profit());
        System.out.println("Selected Projects: " + resultB.projects());

        System.out.println("Diff: " + (result.profit() - resultB.profit()));
    }


}


