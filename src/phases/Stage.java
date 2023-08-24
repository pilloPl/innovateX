package phases;

import java.util.ArrayList;

public record Stage(String name, Stages dependencies) {
    public Stage(String name) {
        this(name, new Stages(new ArrayList<>()));
    }

    public Stage dependsOn(Stage stage) {
        return new Stage(this.name, this.dependencies.add(stage));
    }

    @Override
    public String toString() {
        return name;
    }
}
