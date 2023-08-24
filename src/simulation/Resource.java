package simulation;

record Resource(String id, String name, ResourceType type) {}

enum ResourceType {
    SKILL, PERMISSION, DEVICE
}

record RequiredResource(String name, ResourceType type) {}
