package simulation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
    void mostProfitableProjectIsChosen() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 1900, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))),
                new Project("Project2", 100, 200, 1, 0, List.of(new MissingResource("WEB DEVELOPMENT", "Skill", NOVEMBER))));
        List<Resource> resources = List.of(new Resource("anna", "WEB DEVELOPMENT", "Skill", NOVEMBER));

        //when
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, resources));

        //then
        assertEquals(1800, result.profit(), 0.0d);
        assertEquals(1, result.projects().size());
        assertEquals(result.projects().get(0).name(), "Project1");
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