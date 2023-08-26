package simulation;

import optimization.Result;
import org.junit.jupiter.api.Test;
import shared.TimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.*;

class ProjectSimulationTest {

    @Test
    void test() {
//        TimeSlot june = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 6);
//        TimeSlot october = TimeSlot.createMonthlyTimeSlotAtUTC(2023, 10);
//
//        // Tworzenie employee
//        Employee anna =
//                new Employee("Ania", List.of("Java Developer"), List.of(""), List.of(june));
//        Employee marek =
//                new Employee("Marek", List.of("Web Designer"), List.of(""), List.of(june));
//
//
//        // Creating projects
//        Project project1 = new Project("Website Creation", 1000, 3000, 30);
//        Project project2 = new Project("Database Setup", 1500, 4000, 500);
//        project1.requires(new RequiredResource("Java Developer", "SKILL", june));
//        project2.requires(new RequiredResource("Java Developer", "SKILL", june));
//
//        ProjectSimulation projectManipulation = new ProjectSimulation(
//                of(project1, project2).collect(toCollection(ArrayList::new)),
//                concat(anna.availableResources().stream(), marek.availableResources().stream()).collect(toCollection(ArrayList::new)));
//
//
//        AllocatedResource allocatedAnna = projectManipulation.allocate(anna.skill("Java Developer", june), project1);
//
//        Double difference = projectManipulation.differenceAfterReleasingResource(allocatedAnna, project1);
//
//        assertEquals(500.0d, difference);
    }

}