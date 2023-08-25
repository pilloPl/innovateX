package simulation;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChoseOptimalProjectsForEmployee {

    final TimeSlot JUNE = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 6);
    final TimeSlot NOVEMBER = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 11);

    @Test
    void samePersonCanBeIn5ProjectsGivenACertainSkill() {
        //given
        List<Project> projects = List.of(
                new Project("Project1", 100, 300, 1, 0, List.of(new MissingResource("Admin", "PERMISSION", JUNE))),
                new Project("Project2", 100, 400, 1, 0, List.of(new MissingResource("Admin", "PERMISSION", JUNE))),
                new Project("Project3", 100, 500, 1, 0, List.of(new MissingResource("Admin", "PERMISSION", JUNE))),
                new Project("Project4", 100, 600, 1, 0, List.of(new MissingResource("Admin", "PERMISSION", JUNE))),
                new Project("Project5", 100, 700, 1, 0, List.of(new MissingResource("Admin", "PERMISSION", JUNE))),
                new Project("Project6", 100, 800, 1, 0, List.of(new MissingResource("Admin", "PERMISSION", JUNE))));

        Set<ResourceCapability> capabilities = new HashSet<>();
        capabilities.add(new ResourceCapability("Admin", "PERMISSION", 0));

        //when
        Resource resource = new Resource("anna", capabilities, JUNE);
        Result result = new ChoseOptimalProjects().apply(new CalculateProfitQuery(projects, List.of(resource)));

        //then
        assertEquals(2200, result.profit(), 0.0d);
        assertEquals(4, result.projects().size());

    }

}