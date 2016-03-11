package fr.azuxul.pacman;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IGameProperties;

/**
 * Utils
 *
 * @author Azuxul
 * @author Rigner
 * @version 1.0
 */
public class Utils {

    private Utils() {
    }

    public static int getChanceForPowerup(String powerupJsonName) {

        IGameProperties gameProperties = SamaGamesAPI.get().getGameManager().getGameProperties();

        return gameProperties.getConfigs().get("powerup-chance").getAsJsonObject().get(powerupJsonName).getAsInt();
    }
}
