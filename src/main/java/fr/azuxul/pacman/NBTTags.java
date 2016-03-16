package fr.azuxul.pacman;

/**
 * Enum of NBT tags
 *
 * @author Azuxul
 * @version 1.0
 */
public enum NBTTags {

    NO_GRAVITY("NoGravity"),
    MARKER("Marker"),
    INVULNERABLE("Invulnerable"),
    INVISIBLE("Invisible"),
    DISABLED_SLOTS("DisabledSlots"),
    CUSTOM_NAME_VISIBLE("CustomNameVisible"),
    CUSTOM_NAME("CustomName"),
    SMALL("Small"),
    AGE("Age"),
    PICKUP_DELAY("PickupDelay");

    private final String name;

    NBTTags(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
