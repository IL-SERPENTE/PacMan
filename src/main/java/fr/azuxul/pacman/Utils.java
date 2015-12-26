package fr.azuxul.pacman;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;

/**
 * Class description
 *
 * @author Azuxul
 */
public class Utils {

    public static int getChanceForPowerup(String powerupJsonName) {

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();
        JsonObject defaultObject = new JsonObject();
        defaultObject.addProperty("swap", 5);
        defaultObject.addProperty("blindness", 9);
        defaultObject.addProperty("speed", 10);
        defaultObject.addProperty("jump-boost", 10);
        defaultObject.addProperty("double-coins", 8);
        defaultObject.addProperty("coins-magnet", 10);

        return gameProperties.getOption("powerup-chance", defaultObject).getAsJsonObject().get(powerupJsonName).getAsInt();
    }
}
