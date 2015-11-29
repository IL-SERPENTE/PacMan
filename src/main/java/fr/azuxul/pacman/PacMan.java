package fr.azuxul.pacman;

import fr.azuxul.pacman.event.PlayerEvent;
import fr.azuxul.pacman.player.PlayerPacMan;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

        gameManager = new GameManager(getLogger(), this, getServer()); // Register GameManager

        gameManager.updatePlayerNb(); // Update player nb

        // Register events
        gameManager.getServer().getPluginManager().registerEvents(new PlayerEvent(), this);

        gameManager.getServer().getScheduler().scheduleSyncRepeatingTask(this, gameManager.getTimer(), 0l, 20l);

        // Add players in playerPacManList
        getServer().getOnlinePlayers().forEach(player -> gameManager.getPlayerPacManList().add(new PlayerPacMan(player.getUniqueId(), player.getDisplayName())));

        // TODO: Remove (test)
        getServer().getWorlds().get(0).getBlockAt(0, 71, 0).setType(Material.GOLD_BLOCK);
        getServer().getWorlds().get(0).getBlockAt(0, 71, 1).setType(Material.GOLD_BLOCK);
        getServer().getWorlds().get(0).getBlockAt(0, 71, 2).setType(Material.GOLD_BLOCK);
        getServer().getWorlds().get(0).getBlockAt(1, 71, 1).setType(Material.GOLD_BLOCK);
        getServer().getWorlds().get(0).getBlockAt(0, 71, 1).setType(Material.GOLD_BLOCK);
        getServer().getWorlds().get(0).getBlockAt(1, 71, 2).setType(Material.GOLD_BLOCK);
        getServer().getWorlds().get(0).getBlockAt(0, 71, 2).setType(Material.GOLD_BLOCK);

        // Replace gold block with coins
        int globalCoins = 0;
        for (int x = -100; x <= 100; x++) {
            for (int z = -100; z <= 100; z++) {
                Block block = getServer().getWorlds().get(0).getBlockAt(x, 71, z); // Get block

                if (block.getType().equals(Material.GOLD_BLOCK)) { // If is gold block
                    block.setType(Material.AIR); // Set air
                    Utils.spawnCoin(block.getLocation()); // Spawn coin
                    globalCoins++;
                }
            }
        }
        gameManager.setGlobalCoins(globalCoins); // Set global coins

    }
}
