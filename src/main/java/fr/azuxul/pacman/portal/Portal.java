package fr.azuxul.pacman.portal;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    public void teleportPlayer(Player player) {

        GameManager gameManager = PacMan.getGameManager();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (playerPacMan != null && playerPacMan.getPortalTicks() < 1) {

            int index = linkedPortals.size() > 1 ? 100 / linkedPortals.size() / (RandomUtils.nextInt(100) + 1) - 1 : 0;

            playerPacMan.setPortalTicks(3);
            player.teleport(linkedPortals.get(index).getLocation().clone().add(0, 0.4, 0));
        }
    }
}
