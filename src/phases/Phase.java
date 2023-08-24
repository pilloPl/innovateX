package phases;

public record Phase(Stages stages) {

    @Override
    public String toString() {
        return "phases.Phase: " + stages;
    }
}
