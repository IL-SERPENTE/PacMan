package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Gomme;
import fr.azuxul.pacman.event.PlayerEvent;
import fr.azuxul.pacman.powerup.BasicPowerup;
import fr.azuxul.pacman.powerup.PowerupBlindness;
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

    /**
     * Initialize powerup manager
     * Registry powerups
     * Set spawn frequency
     */
    private static void powerupInitialisation() {

        PowerupManager powerupManager = gameManager.getPowerupManager();
        int spawnFrequency = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("powerup-frequency").getAsInt();

        // Register powerups
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.SPEED, "speed"));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.JUMP_BOOST, "jump-boost"));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.DOUBLE_GOMMES, "double-coins"));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.GOMME_MAGNET, "coins-magnet"));
        powerupManager.registerPowerup(new PowerupSwap());
        powerupManager.registerPowerup(new PowerupBlindness());

        powerupManager.setInverseFrequency(spawnFrequency); // Set spawn frequency
    }

    /**
     * Initialise the map :
     * Replace gold block with coins
     */
    private static void mapInitialisation() {

        PowerupManager powerupManager = gameManager.getPowerupManager();
        GommeManager gommeManager = gameManager.getGommeManager();
        Location baseLocation = gameManager.getMapCenter();
        if (baseLocation == null)
            return;
        org.bukkit.World world = baseLocation.getWorld();
        World worldNMS = ((CraftWorld) world).getHandle();

        // Replace gold block with coins
        int globalCoins = 0;
        int xMin = baseLocation.getBlockX() - 100;
        int xMax = baseLocation.getBlockX() + 100;
        int yMin = baseLocation.getBlockY() - 20;
        int yMax = baseLocation.getBlockY() + 70;
        int zMin = baseLocation.getBlockZ() - 100;
        int zMax = baseLocation.getBlockZ() + 100;

        if (yMin <= 0)
            yMin = 1;
        if (yMax > 255)
            yMax = 255;

        for (int x = xMin; x <= xMax; x++)
            for (int z = zMin; z <= zMax; z++)
                for (int y = yMin; y <= yMax; y++) {
                    Block block = world.getBlockAt(x, y, z); // Get block

                    if (block.getType().equals(Material.GOLD_BLOCK)) { // If is gold block
                        block.setType(Material.AIR); // Set air

                        // Spawn normal coin
                        gommeManager.spawnCoin(worldNMS, x + 0.5, y - 0.3, z + 0.5, false);
                        globalCoins++;

                    } else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
                        block.setType(Material.AIR); // Set air

                        powerupManager.registerLocation(new Location(world, x + 0.5, y, z + 0.5)); // Register booster location
                    }
                }

        gameManager.getGommeManager().setGlobalCoins(globalCoins); // Set global coins
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
        getServer().getPluginManager().registerEvents(new PlayerEvent(gameManager), this);

        // Register timer
        getServer().getScheduler().scheduleSyncRepeatingTask(this, gameManager.getTimer(), 0L, 20L);

        // Register entity
        registerEntity("Gomme", 69, Gomme.class);

        // Kick players
        getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(""));

        Location spawn = gameManager.getSpawn();
        org.bukkit.World world = spawn.getWorld();

        world.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY() + 3, spawn.getBlockZ()); // Set spawn location
        world.setDifficulty(Difficulty.EASY); // Set difficulty
        world.setGameRuleValue("doMobSpawning", "false"); // Set doMobSpawning game rule
        world.setGameRuleValue("keepInventory", "true"); // Set keepInventory game rule
        world.setGameRuleValue("reducedDebugInfo", "true"); // Reduce debug info (Mask location)
        world.setStorm(false); // Clear storm
        world.setThundering(false); // Clear weather
        world.setThunderDuration(0); // Clear weather
        world.setWeatherDuration(0); // Clear weather

        powerupInitialisation();
        mapInitialisation();
    }

    @Override
    public void onDisable() {
        gameManager.getGommeManager().killAllCoin();
    }

    /**
     * Register entity
     *
     * @param name  entity name
     * @param id    entity id
     * @param clazz entity class
     */
    private void registerEntity(String name, int id, Class clazz) {

        // Exception when plugin are reloaded

        try {
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class); // Get method for register new entity
            method.setAccessible(true); // Set accessible
            method.invoke(null, clazz, name, id); // Invoke

        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            getLogger().warning(String.valueOf(e));
        }
    }
}
