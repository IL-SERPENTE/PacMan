package fr.azuxul.pacman.portal;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Portal class
 *
 * @author Azuxul
 * @version 1.0
 */
public class Portal {

    private String name;
    private Location location;
    private List<Portal> linkedPortals;

    public Portal(String name, Location location) {
        this.name = name;
        this.location = location;
        this.linkedPortals = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<Portal> getLinkedPortals() {
        return linkedPortals;
    }
}
