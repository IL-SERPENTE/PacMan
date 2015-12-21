package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Coin;
import fr.azuxul.pacman.event.PlayerEvent;
import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.powerup.BasicPowerup;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import fr.azuxul.pacman.powerup.PowerupSwap;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.powerups.PowerupManager;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

        final String gameCodeName = "pacman";
        final String gameName = "PacMan";
        final String gameDescription = "";

        SamaGamesAPI samaGamesAPI = SamaGamesAPI.get();

        gameManager = new GameManager(getLogger(), this, getServer(), gameCodeName, gameName, gameDescription, PlayerPacMan.class); // Register GameManager

        samaGamesAPI.getGameManager().registerGame(gameManager); // Register game on SamaGameAPI
        samaGamesAPI.getGameManager().getGameProperties(); // Get properties

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerEvent(), this);

        // Register timer
        getServer().getScheduler().scheduleSyncRepeatingTask(this, gameManager.getTimer(), 0L, 20L);

        // Register entity
        registerEntity("Coin", 69, Coin.class);

        // Kick players
        getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(""));

        getServer().getWorlds().get(0).setSpawnLocation(0, 73, 0); // Set spawn location
        getServer().getWorlds().get(0).setDifficulty(Difficulty.NORMAL); // Set difficulty
        getServer().getWorlds().get(0).setGameRuleValue("doMobSpawning", "false"); // Set doMobSpawning game rule
        getServer().getWorlds().get(0).setGameRuleValue("keepInventory", "true"); // Set keepInventory game rule
        getServer().getWorlds().get(0).setStorm(false); // Clear storm
        getServer().getWorlds().get(0).setThundering(false); // Clear weather
        getServer().getWorlds().get(0).setThunderDuration(0); // Clear weather
        getServer().getWorlds().get(0).setWeatherDuration(0); // Clear weather

        powerupInitialisation();
        mapInitialisation();
    }

    private void powerupInitialisation() {

        PowerupManager powerupManager = gameManager.getPowerupManager();

        // Register powerups
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.SPEED, 10));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.DOUBLE_COINS, 8));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.COINS_MAGNET, 10));
        powerupManager.registerPowerup(new PowerupSwap());

        powerupManager.setInverseFrequency(230);
    }

    /**
     * Initialise the map :
     * Replace gold block with coins
     */
    private void mapInitialisation() {

        org.bukkit.World world = getServer().getWorlds().get(0);
        World worldNMS = ((CraftWorld) world).getHandle();
        PowerupManager powerupManager = gameManager.getPowerupManager();

        // Replace gold block with coins
        int globalCoins = 0;
        for (int x = -100; x <= 100; x++) {
            for (int z = -100; z <= 100; z++) {
                Block block = world.getBlockAt(x, 71, z); // Get block

                if (block.getType().equals(Material.GOLD_BLOCK)) { // If is gold block
                    block.setType(Material.AIR); // Set air

                    // Spawn normal coin
                    new Coin(worldNMS, x + 0.5, 70.7, z + 0.5, false);
                    globalCoins++;

                } else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
                    block.setType(Material.AIR); // Set air

                    powerupManager.registerLocation(new Location(world, x + 0.5, 71, z + 0.5)); // Register booster location
                }
            }
        }
        gameManager.setGlobalCoins(globalCoins); // Set global coins
    }

    /**
     * Register entity
     *
     * @param name  entity name
     * @param id    entity id
     * @param clazz entity class
     */
    @SuppressWarnings("unchecked")
    private void registerEntity(String name, int id, Class clazz) {

        try {
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class); // Get method
            method.setAccessible(true); // Set accessible
            method.invoke(null, clazz, name, id); // Invoke

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            getLogger().warning(String.valueOf(e));
        }
    }
}
