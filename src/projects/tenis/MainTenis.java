package projects.tenis;

import java.util.*;

 class TennisCamp {

    static final int MAX_GROUP_SIZE = 4;

    static class Player {
        String name;
        int priority;
        String skillLevel;
        Set<Player> mustPlayWith = new HashSet<>();
        Set<Player> cannotPlayWith = new HashSet<>();
        String preferredTime = null; // "morning" or "evening" or null

        Player(String name, int priority, String skillLevel) {
            this.name = name;
            this.priority = priority;
            this.skillLevel = skillLevel;
        }

        @Override
        public String toString() {
            return "Player{" +
                    "name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return priority == player.priority && Objects.equals(name, player.name) && Objects.equals(skillLevel, player.skillLevel) && Objects.equals(mustPlayWith, player.mustPlayWith) && Objects.equals(cannotPlayWith, player.cannotPlayWith) && Objects.equals(preferredTime, player.preferredTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, priority, skillLevel, mustPlayWith, cannotPlayWith, preferredTime);
        }
    }

    static class Group {
        String time;
        String skillLevel;
        List<Player> players = new ArrayList<>();

        Group(String time, String skillLevel) {
            this.time = time;
            this.skillLevel = skillLevel;
        }

        boolean canAddPlayer(Player p) {
            if (players.size() < MAX_GROUP_SIZE && p.skillLevel.equals(skillLevel)) {
                for (Player player : players) {
                    if (p.cannotPlayWith.contains(player.name)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        void addPlayer(Player p) {
            players.add(p);
        }
    }

    static class Coach {
        String name;
        Group morningGroup;
        Group eveningGroup;

        Coach(String name, String skillLevel) {
            this.name = name;
            morningGroup = new Group("morning", skillLevel);
            eveningGroup = new Group("evening", skillLevel);
        }
    }

    public static boolean tryAssignPlayer(Player p, List<Coach> coaches) {
        for (int i = 1; i <= 6; i++) {
            for (Coach c : coaches) {
                switch (i) {
                    case 1:
                        if (p.preferredTime != null && p.preferredTime.equals("morning") && c.morningGroup.canAddPlayer(p) && p.mustPlayWith.containsAll(c.morningGroup.players)) {
                            c.morningGroup.addPlayer(p);
                            return true;
                        }
                        if (p.preferredTime != null && p.preferredTime.equals("evening") && c.eveningGroup.canAddPlayer(p) && p.mustPlayWith.containsAll(c.eveningGroup.players)) {
                            c.eveningGroup.addPlayer(p);
                            return true;
                        }
                        break;
                    case 2:
                        if (p.preferredTime != null && p.preferredTime.equals("morning") && c.morningGroup.canAddPlayer(p)) {
                            c.morningGroup.addPlayer(p);
                            return true;
                        }
                        if (p.preferredTime != null && p.preferredTime.equals("evening") && c.eveningGroup.canAddPlayer(p)) {
                            c.eveningGroup.addPlayer(p);
                            return true;
                        }
                        break;
                    case 3:
                        if (p.preferredTime != null && p.preferredTime.equals("morning") && c.morningGroup.canAddPlayer(p) && p.mustPlayWith.containsAll(c.morningGroup.players)) {
                            c.morningGroup.addPlayer(p);
                            return true;
                        }
                        if (p.preferredTime != null && p.preferredTime.equals("evening") && c.eveningGroup.canAddPlayer(p) && p.mustPlayWith.containsAll(c.eveningGroup.players)) {
                            c.eveningGroup.addPlayer(p);
                            return true;
                        }
                        break;
                    case 4:
                        if (c.morningGroup.canAddPlayer(p)) {
                            c.morningGroup.addPlayer(p);
                            return true;
                        }
                        if (c.eveningGroup.canAddPlayer(p)) {
                            c.eveningGroup.addPlayer(p);
                            return true;
                        }
                        break;
                    case 5:
                        if (p.skillLevel.equals(c.morningGroup.skillLevel) && c.morningGroup.players.size() < MAX_GROUP_SIZE) {
                            c.morningGroup.addPlayer(p);
                            return true;
                        }
                        if (p.skillLevel.equals(c.eveningGroup.skillLevel) && c.eveningGroup.players.size() < MAX_GROUP_SIZE) {
                            c.eveningGroup.addPlayer(p);
                            return true;
                        }
                        break;
                    case 6:
                        if (c.morningGroup.players.size() < MAX_GROUP_SIZE) {
                            c.morningGroup.addPlayer(p);
                            return true;
                        }
                        if (c.eveningGroup.players.size() < MAX_GROUP_SIZE) {
                            c.eveningGroup.addPlayer(p);
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<Coach> coaches = new ArrayList<>();
            coaches.add(new Coach("daniel", "beginner"));
            coaches.add(new Coach("braś", "intermediate"));
            coaches.add(new Coach("braś2", "kids"));
            coaches.add(new Coach("braś3", "advanced"));


        List<Player> players = new ArrayList<>();

        Player karolinaMajewska = new Player("Karolina Majewska", 1, "advanced");
        karolinaMajewska.preferredTime = "morning";
        players.add(karolinaMajewska);

        Player zbyszekMajewski = new Player("Zbyszek Majewski", 2, "advanced");
        zbyszekMajewski.preferredTime = "morning";
        players.add(zbyszekMajewski);

        Player kubaMajewski = new Player("Kuba Majewski", 3, "kids");
        kubaMajewski.preferredTime = "morning";
        players.add(kubaMajewski);

        Player stasMajewski = new Player("Staś Majewski", 4, "kids");
        stasMajewski.preferredTime = "morning";
        players.add(stasMajewski);

        Player malgorzataPietrzyk = new Player("Małgorzata pietrzyk", 5, "beginner");
        malgorzataPietrzyk.preferredTime = "morning";
        players.add(malgorzataPietrzyk);

        Player michalPietrzyk = new Player("Michał Pietrzyk", 6, "beginner");
        michalPietrzyk.preferredTime = "morning";
        players.add(michalPietrzyk);

        Player igaPietrzyk = new Player("Iga Pietrzyk - 10 lat", 7, "kids");
        igaPietrzyk.preferredTime = "morning";
        players.add(igaPietrzyk);

        Player nikodemPietrzyk = new Player("Nikodem Pietrzyk - 7 lat", 8, "kids");
        nikodemPietrzyk.preferredTime = "morning";
        players.add(nikodemPietrzyk);

        Player adamWozniak = new Player("Adam Woźniak", 9, "intermediate");
        adamWozniak.preferredTime = "morning";
        players.add(adamWozniak);

        Player andzelikaWozniak = new Player("Andżelika Woźniak", 10, "intermediate");
        andzelikaWozniak.preferredTime = "morning";
        players.add(andzelikaWozniak);

        Player michalinaBras = new Player("Michalina Braś", 11, "advanced");
        michalinaBras.preferredTime = "evening";
        players.add(michalinaBras);

        Player annaKoziol = new Player("Anna Kozioł", 12, "intermediate");
        annaKoziol.preferredTime = "evening";
        players.add(annaKoziol);

        Player jakubSlupski = new Player("Jakub Słupski", 13, "intermediate");
        jakubSlupski.preferredTime = "morning";
        players.add(jakubSlupski);

        Player anetaSokolowska = new Player("Aneta Sokołowska", 14, "intermediate");
        anetaSokolowska.preferredTime = "morning";
        players.add(anetaSokolowska);

        Player mariuszWysocki = new Player("Mariusz Wysocki", 15, "beginner");
        mariuszWysocki.preferredTime = "morning";
        players.add(mariuszWysocki);

        Player igaSzarmach = new Player("Iga Szarmach", 16, "advanced");
        igaSzarmach.preferredTime = "morning";
        players.add(igaSzarmach);

        Player maciejKuczynski = new Player("Maciej Kuczyński", 17, "intermediate");
        maciejKuczynski.preferredTime = "morning";
        players.add(maciejKuczynski);

        Player magdaLutynska = new Player("Magda Lutyńska", 18, "advanced");
        players.add(magdaLutynska);

        Player justynaPowierza = new Player("Justyna Powierża", 19, "beginner");
        players.add(justynaPowierza);

        Player michalIwanowski = new Player("Michał Iwanowski", 20, "beginner");
        players.add(michalIwanowski);

        Player kasiaIwanowska = new Player("Kasia Iwanowska", 21, "beginner");
        players.add(kasiaIwanowska);

        Player dominikaPoniatowska = new Player("Dominika Poniatowska", 22, "beginner");
        players.add(dominikaPoniatowska);

        Player dawidPoniatowski = new Player("Dawid Poniatowski", 23, "beginner");
        players.add(dawidPoniatowski);

        Player robertBogucki = new Player("Robert Bogucki", 24, "advanced");
        players.add(robertBogucki);

        Player michalZiolkowski = new Player("Michał Ziółkowski", 26, "beginner");
        players.add(michalZiolkowski);

        Player romanJawczak = new Player("Roman Jawczak", 27, "intermediate");
        players.add(romanJawczak);

        Player krzysztofHarazinski = new Player("Krzysztof Haraziński", 0, "intermediate");
        players.add(krzysztofHarazinski);

        Player tomaszSzostek = new Player("Tomasz Szostek", 29, "beginner");
        players.add(tomaszSzostek);

        Player lukaszFranczak = new Player("Łukasz Frańczak", 30, "advanced");
        players.add(lukaszFranczak);

        Player aleksandraGroszek = new Player("Aleksandra Groszek", 31, "advanced");
        players.add(aleksandraGroszek);

        // Gracze, którzy muszą grać razem
        michalIwanowski.mustPlayWith.add(kasiaIwanowska);
        kasiaIwanowska.mustPlayWith.add(dominikaPoniatowska);
        igaSzarmach.mustPlayWith.add(michalinaBras);
        karolinaMajewska.mustPlayWith.add(michalinaBras);
        nikodemPietrzyk.mustPlayWith.add(kubaMajewski);
        igaPietrzyk.mustPlayWith.add(stasMajewski);
        igaPietrzyk.mustPlayWith.add(nikodemPietrzyk);
        aleksandraGroszek.mustPlayWith.add(michalinaBras);

// Gracze, którzy nie mogą grać razem
        robertBogucki.cannotPlayWith.add(krzysztofHarazinski);
        anetaSokolowska.cannotPlayWith.add(mariuszWysocki);
        igaSzarmach.cannotPlayWith.add(maciejKuczynski);
        krzysztofHarazinski.cannotPlayWith.add(anetaSokolowska);
        // Sort players by priority
        players.sort((a, b) -> Integer.compare(b.priority, a.priority));
        //Collections.reverse(players);


        for (Player p : players) {
            if (!tryAssignPlayer(p, coaches)) {
                System.out.println("Couldn't assign player " + p.name);
            }
        }

        // Print the assignment
        for (Coach c : coaches) {
            System.out.println("Coach " + c.name + " " + c.morningGroup.skillLevel + " morning group: " + c.morningGroup.players);
            System.out.println("");
            System.out.println("Coach " + c.name + " " + c.eveningGroup.skillLevel + " evening group: " + c.eveningGroup.players);
            System.out.println("");
        }
    }
}
