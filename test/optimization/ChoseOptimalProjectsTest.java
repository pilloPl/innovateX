package optimization;

import org.junit.jupiter.api.Test;
import shared.TimeSlot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChoseOptimalProjectsTest {

    final TimeSlot JUNE = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 6);
    final TimeSlot NOVEMBER = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 11);

    @Test
    void nothingIsDoneWhenNoResources() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 200, 1, 0, List.of(new MissingResource("COMMON SENSE", "Skill", JUNE))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("THINKING", "Skill", JUNE))));
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
    void ifEnoughResourcesAllProjectsAreChosen() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 1900, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))));
        Resource r1 = new Resource("anna", "WEB DEVELOPMENT", "Skill", NOVEMBER);
        Resource r2 = new Resource("anna2", "WEB DEVELOPMENT", "Skill", NOVEMBER);
        List<Resource> resources = List.of(r1, r2);

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(1900, result.profit(), 0.0d);
        assertEquals(2, result.projects().size());
    }

    @Test
    void moreProfitableProjectIsChosen() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 19000, 10, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))));
        Resource r1 = new Resource("anna", "WEB DEVELOPMENT", "Skill", NOVEMBER);
        List<Resource> resources = List.of(r1);

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(18900, result.profit(), 0.0d);
        assertEquals(1, result.projects().size());

        //when
        Result result2 = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources, Project::getRisk, Comparator.comparing(Project::getRisk)));

        //then
        assertEquals(1, result2.profit(), 0.0d);
        assertEquals(1, result2.projects().size());
    }

    @Test
    void nothingIsChosenBecauseTimeSlotNotMatched() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 1900, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))));
        List<Resource> resources = List.of(new Resource("anna", "WEB DEVELOPMENT", "Skill", JUNE));

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(0, result.profit(), 0.0d);
        assertEquals(0, result.projects().size());
    }
}