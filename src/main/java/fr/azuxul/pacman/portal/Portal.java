package fr.azuxul.pacman.portal;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of PacMan.
 *
 * PacMan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacMan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PacMan.  If not, see <http://www.gnu.org/licenses/>.
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
