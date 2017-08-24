package fr.azuxul.pacman.portal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.azuxul.pacman.GameManager;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class PortalManager {

    GameManager gameManager;
    List<Portal> portals;

    public PortalManager(GameManager gameManager) {

        this.gameManager = gameManager;
        this.portals = new ArrayList<>();

        initFormJSON();
    }

    /**
     * Init portalManager with game.json file
     * Init portals
     */
    private void initFormJSON() {

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();

        final String locationProperty = "location";
        final String linkedPortalsProperty = "linkedPortals";

        JsonArray jsonPortals = gameProperties.getConfigs().get("portals").getAsJsonArray();

        Map<Portal, List<String>> portalMap = new HashMap<>();

        for (JsonElement element : jsonPortals) {

            JsonObject jsonPortal = element.getAsJsonObject();

            String name = jsonPortal.get("name").getAsString();
            Location portalLocation = LocationUtils.str2loc(jsonPortal.get(locationProperty).getAsString());
            List<String> stringLinkedPortals = new ArrayList<>();

            JsonArray jsonLinkedPortals = jsonPortal.get(linkedPortalsProperty).getAsJsonArray();

            for (JsonElement jsonLinkedPortal : jsonLinkedPortals)
                stringLinkedPortals.add(jsonLinkedPortal.getAsString());

            portalMap.put(new Portal(name, portalLocation), stringLinkedPortals);
        }

        convertStringPortalListAndAddToPortals(portalMap);

    }

    /**
     * Add in key portal, portals in list value
     *
     * @param portalMap map with portal for key and list of portal name to add of portal
     */
    private void convertStringPortalListAndAddToPortals(Map<Portal, List<String>> portalMap) {

        List<Portal> portalsList = new ArrayList<>(portalMap.keySet());

        for (Map.Entry<Portal, List<String>> entry : portalMap.entrySet()) {

            Portal portal = entry.getKey();

            for (String name : entry.getValue()) {

                portalsList.stream().filter(p -> p.getName().equals(name)).forEach(portal.getLinkedPortals()::add);
            }

            portals.add(portal);
        }

    }

    public Portal getPortalAtLocation(Location location) {

        for (Portal portal : portals)
            if (portal.getLocation().distance(location) <= 5)
                return portal;

        return null;
    }

    public List<Portal> getPortals() {
        return portals;
    }
}
