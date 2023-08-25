package simulation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChoseOptimalProjectsTest {

    @Test
    void nothingIsDoneWhenNoResources() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 200, 1, 0, List.of(new MissingResource("COMMON SENSE", "Skill"))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("THINKING", "Skill"))));
        List<Resource> resources = new ArrayList<>();

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(0, result.profit(), 0.0d);
        assertEquals(0, result.projects().size());

    }

    @Test
    void everythingIsChosenWhenNoMissingResources() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 1900, 1, 0,
                        List.of()),
                new Project("Project2", 100, 300, 1, 0,
                        List.of())
        );
        List<Resource> resources = new ArrayList<>();

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(2000, result.profit(), 0.0d);
        assertEquals(2, result.projects().size());

    }

    @Test
    void motProfitableProjectIsChosen() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 1900, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill"))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill"))));
        List<Resource> resources = List.of(new Resource("anna", "WEB DEVELOPMENT", "Skill"));

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(1800, result.profit(), 0.0d);
        assertEquals(1, result.projects().size());
        assertEquals(result.projects().get(0).name(), "Project1");
    }
}