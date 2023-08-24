package simulation;

public record MissingResource(String name, String resourceType) {

    boolean canBeAllocatedBy(Resource resource) {
        return this.resourceType.equals(resource.resourceType());
    }
}
