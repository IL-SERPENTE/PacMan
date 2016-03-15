package fr.azuxul.pacman;

import com.google.gson.JsonObject;
import fr.azuxul.pacman.entity.Gomme;
import fr.azuxul.pacman.event.PlayerEvent;
import fr.azuxul.pacman.powerup.BasicPowerup;
import fr.azuxul.pacman.powerup.PowerupBlindness;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import fr.azuxul.pacman.powerup.PowerupSwap;
import net.minecraft.server.v1_8_R3.Entity;
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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Main class of PacMan plugin for SpigotMC 1.8.8-R0.1-SNAPSHOT
 *
 * @author Azuxul
 * @version 1.0
 */
public class PacMan extends JavaPlugin {

    private static GameManager gameManager;
    private Material gommeMaterial;
    private Material powerupMaterial;
    private String[] initRadius;

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
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.DOUBLE_GOMMES, "double-gommes"));
        powerupManager.registerPowerup(new BasicPowerup(PowerupEffectType.GOMME_MAGNET, "gommes-magnet"));
        powerupManager.registerPowerup(new PowerupSwap());
        powerupManager.registerPowerup(new PowerupBlindness());

        powerupManager.setInverseFrequency(spawnFrequency); // Set spawn frequency
    }

    private void mapPreInitialisation() {

        Logger logger = gameManager.getServer().getLogger();

        JsonObject json = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("map-init").getAsJsonObject();

        initRadius = json.get("radius").getAsString().split(", ");

        if (initRadius.length > 3) {
            logger.warning("Map initialisation warning, radius is not valid !");
            return;
        }

        try {
            gommeMaterial = Material.getMaterial(json.get("gomme-block").getAsString().toUpperCase());
            powerupMaterial = Material.getMaterial(json.get("powerup-block").getAsString().toUpperCase());
        } catch (Exception e) {
            logger.warning("Map initialisation warning, blocks is not valid ! " + e);
            return;
        }

        mapInitialisation();
    }

    /**
     * Initialise the map :
     * Replace gold block with gommes
     */
    private void mapInitialisation() {

        PowerupManager powerupManager = gameManager.getPowerupManager();
        GommeManager gommeManager = gameManager.getGommeManager();
        Location baseLocation = gameManager.getMapCenter();
        if (baseLocation == null)
            return;
        org.bukkit.World world = baseLocation.getWorld();
        World worldNMS = ((CraftWorld) world).getHandle();

        // Replace gold block with gommes
        int globalGommes = 0;

        int xRadius = Integer.parseInt(initRadius[0]);
        int yRadius = Integer.parseInt(initRadius[1]);
        int zRadius = Integer.parseInt(initRadius[2]);

        int xMin = baseLocation.getBlockX() - xRadius;
        int xMax = baseLocation.getBlockX() + xRadius;
        int yMin = baseLocation.getBlockY() - yRadius;
        int yMax = baseLocation.getBlockY() + yRadius;
        int zMin = baseLocation.getBlockZ() - zRadius;
        int zMax = baseLocation.getBlockZ() + zRadius;

        if (yMin <= 0)
            yMin = 1;
        if (yMax > 255)
            yMax = 255;

        for (int x = xMin; x <= xMax; x++)
            for (int z = zMin; z <= zMax; z++)
                for (int y = yMin; y <= yMax; y++) {
                    Block block = world.getBlockAt(x, y, z); // Get block

                    if (block.getType().equals(gommeMaterial)) { // If is gold block
                        block.setType(Material.AIR); // Set air

                        // Spawn normal gomme
                        gommeManager.spawnGomme(worldNMS, x + 0.5, y - 0.3, z + 0.5, false);
                        globalGommes++;

                    } else if (block.getType().equals(powerupMaterial)) {
                        block.setType(Material.AIR); // Set air

                        powerupManager.registerLocation(new Location(world, x + 0.5, y, z + 0.5)); // Register booster location
                    }
                    }

        gameManager.getGommeManager().setGlobalGommes(globalGommes); // Set global gommes
        Collections.shuffle(gameManager.getGommeManager().getGommeList());
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
        try {
            registerEntityInEntityEnum(Gomme.class, "Gomme", 69);
        } catch (Exception e) {
            getLogger().warning("Error to register entity Gomme " + e);
        }

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
        mapPreInitialisation();
    }

    @Override
    public void onDisable() {
        gameManager.getGommeManager().killAllGommes();
    }

    private void registerEntityInEntityEnum(Class paramClass, String paramString, int paramInt) throws Exception {
        ((Map<String, Class<? extends Entity>>) this.getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
        ((Map<Class<? extends Entity>, String>) this.getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
        ((Map<Integer, Class<? extends Entity>>) this.getPrivateStatic(EntityTypes.class, "e")).put(paramInt, paramClass);
        ((Map<Class<? extends Entity>, Integer>) this.getPrivateStatic(EntityTypes.class, "f")).put(paramClass, paramInt);
        ((Map<String, Integer>) this.getPrivateStatic(EntityTypes.class, "g")).put(paramString, paramInt);
    }

    private Object getPrivateStatic(Class clazz, String f) throws Exception {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);

        return field.get(null);
    }
}
