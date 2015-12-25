package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Coin;
import fr.azuxul.pacman.event.PlayerEvent;
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

        SamaGamesAPI samaGamesAPI = SamaGamesAPI.get();

        synchronized (this) {
            gameManager = new GameManager(this); // Register GameManager
        }

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

        org.bukkit.World world = getServer().getWorlds().get(0);

        world.setSpawnLocation(0, 73, 0); // Set spawn location
        world.setDifficulty(Difficulty.NORMAL); // Set difficulty
        world.setGameRuleValue("doMobSpawning", "false"); // Set doMobSpawning game rule
        world.setGameRuleValue("keepInventory", "true"); // Set keepInventory game rule
        world.setStorm(false); // Clear storm
        world.setThundering(false); // Clear weather
        world.setThunderDuration(0); // Clear weather
        world.setWeatherDuration(0); // Clear weather

        powerupInitialisation();
        mapInitialisation();
    }

    @Override
    public void onDisable() {
        gameManager.getCoinManager().killAllCoin();
    }

    /**
     * Initialize powerup manager
     * Registry powerups
     * Set spawn frequency
     */
    private void powerupInitialisation() {

        PowerupManager powerupManager = gameManager.getPowerupManager();

        // Register powerups
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.SPEED, 10));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.DOUBLE_COINS, 8));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.COINS_MAGNET, 10));
        powerupManager.registerPowerup(new PowerupSwap());

        powerupManager.setInverseFrequency(230); // Set spawn frequency
    }

    /**
     * Initialise the map :
     * Replace gold block with coins
     */
    private void mapInitialisation() {

        org.bukkit.World world = getServer().getWorlds().get(0);
        World worldNMS = ((CraftWorld) world).getHandle();
        PowerupManager powerupManager = gameManager.getPowerupManager();
        CoinManager coinManager = gameManager.getCoinManager();

        // Replace gold block with coins
        int globalCoins = 0;
        for (int x = -100; x <= 100; x++)
            for (int z = -100; z <= 100; z++)
                for (int y = 1; y <= 200; y++) {
                    Block block = world.getBlockAt(x, y, z); // Get block

                    if (block.getType().equals(Material.GOLD_BLOCK)) { // If is gold block
                        block.setType(Material.AIR); // Set air

                        // Spawn normal coin
                        coinManager.spawnCoin(worldNMS, x + 0.5, y - 0.3, z + 0.5, false);
                        globalCoins++;

                    } else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
                        block.setType(Material.AIR); // Set air

                        powerupManager.registerLocation(new Location(world, x + 0.5, y, z + 0.5)); // Register booster location
                    }
                }

        gameManager.getCoinManager().setGlobalCoins(globalCoins); // Set global coins
    }

    /**
     * Register entity
     *
     * @param name  entity name
     * @param id    entity id
     * @param clazz entity class
     */
    private void registerEntity(String name, int id, Class clazz) {

        try {
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class); // Get method for register new entity
            method.setAccessible(true); // Set accessible
            method.invoke(null, clazz, name, id); // Invoke

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            getLogger().warning(String.valueOf(e));
        }
    }
}
