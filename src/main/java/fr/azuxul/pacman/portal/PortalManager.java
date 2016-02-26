package fr.azuxul.pacman.portal;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.azuxul.pacman.GameManager;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portal manager
 *
 * @author Azuxul
 * @version 1.0
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

        JsonArray portalsArray = new JsonArray();

        JsonObject portal0 = new JsonObject();
        JsonObject portal1 = new JsonObject();
        JsonArray portalLinked0 = new JsonArray();
        JsonArray portalLinked1 = new JsonArray();

        portalLinked1.add(new JsonPrimitive("portal0"));
        portalLinked0.add(new JsonPrimitive("portal1"));

        portal0.addProperty("name", "portal0");
        portal0.addProperty(locationProperty, "world, 0, 71, 24, 180, 0");
        portal0.add(linkedPortalsProperty, portalLinked0);

        portal0.addProperty("name", "portal1");
        portal0.addProperty(locationProperty, "world, 0, 71, -24, 0, 0");
        portal0.add(linkedPortalsProperty, portalLinked1);

        portalsArray.add(portal0);
        portalsArray.add(portal1);

        JsonArray jsonPortals = gameProperties.getOption("portals", portalsArray).getAsJsonArray();

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

        Portal result = null;

        for (Portal portal : portals)
            if (portal.getLocation().distance(location) <= 5) {
                result = portal;
                break;
            }


        return result;
    }

    public List<Portal> getPortals() {
        return portals;
    }
}
