package fr.azuxul.pacman;

import fr.azuxul.pacman.event.PlayerEvent;
import fr.azuxul.pacman.player.PlayerPacMan;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class of PacMan plugin for SpigotMC 1.8.8-R0.1-SNAPSHOT
 *
 * @author Azuxul
 * @version 1.0
 */
public class PacMan extends JavaPlugin {

    private static GameManager gameManager;

    public static GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public void onEnable() {

        gameManager = new GameManager(getLogger(), this); // Register GameManager

        gameManager.getServer().getPluginManager().registerEvents(new PlayerEvent(), this); // Register events

        gameManager.getServer().getScheduler().scheduleSyncRepeatingTask(this, gameManager.getTimer(), 0l, 20l);

        // Add players in playerPacManList
        getServer().getOnlinePlayers().forEach(player -> gameManager.getPlayerPacManList().add(new PlayerPacMan(player.getUniqueId(), player.getDisplayName())));

        // TODO: Remove (test)
        Utils.spawnCoin(new Location(gameManager.getServer().getWorlds().get(0), 5, 190, 2));
    }
}
