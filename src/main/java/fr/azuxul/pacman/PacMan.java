package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Booster;
import fr.azuxul.pacman.entity.Coin;
import fr.azuxul.pacman.event.PlayerEvent;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.SamaGamesAPI;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;

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

        final String gameCodeName = "pacman-test"; // TODO: Change gameCodeName
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
        registerEntity("Coin", 54, Coin.class);
        registerEntity("Booster", 55, Booster.class);

        // Kick players
        getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(""));

        getServer().getWorlds().get(0).setSpawnLocation(0, 73, 0); // Set spawn location
        getServer().getWorlds().get(0).setDifficulty(Difficulty.NORMAL); // Set difficulty
        getServer().getWorlds().get(0).setGameRuleValue("doMobSpawning", "false"); // Set doMobSpawning game rule

        mapInitialisation();
    }

    /**
     * Initialise the map :
     * Replace gold block with coins
     */
    private void mapInitialisation() {

        org.bukkit.World world = getServer().getWorlds().get(0);
        World NMSWorld = ((CraftWorld) world).getHandle();

        // Replace gold block with coins
        int globalCoins = 0;
        for (int x = -100; x <= 100; x++) {
            for (int z = -100; z <= 100; z++) {
                Block block = world.getBlockAt(x, 71, z); // Get block

                if (block.getType().equals(Material.GOLD_BLOCK)) { // If is gold block
                    block.setType(Material.AIR); // Set air

                    // Spawn normal coin
                    new Coin(NMSWorld, x + 0.5, 70.7, z + 0.5, false);
                    globalCoins++;

                } else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
                    block.setType(Material.AIR); // Set air

                    // Get random type
                    Booster.BoosterTypes type = Booster.BoosterTypes.values()[RandomUtils.nextInt(Booster.BoosterTypes.values().length)];

                    new Booster(NMSWorld, x + 0.5, 70.7, z + 0.5, type); // Spawn booster
                    gameManager.getBoosterLocations().put(new Location(world, x, 71, z), true); // Add booster in list
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

        // put entity details in maps of EntityTypes class
        ((Map) getPrivateFieldOfEntityTypes("c", null)).put(name, clazz);
        ((Map) getPrivateFieldOfEntityTypes("d", null)).put(clazz, name);
        ((Map) getPrivateFieldOfEntityTypes("e", null)).put(id, clazz);
        ((Map) getPrivateFieldOfEntityTypes("f", null)).put(clazz, id);
        ((Map) getPrivateFieldOfEntityTypes("g", null)).put(name, id);
    }

    /**
     * Get returned object of private field
     *
     * @param fieldName fieldName
     * @param object    parameter of field
     * @return returned object by field
     */
    private Object getPrivateFieldOfEntityTypes(String fieldName, Object object) {

        Field field;
        Object returnObject = null;

        try {

            field = net.minecraft.server.v1_8_R3.EntityTypes.class.getDeclaredField(fieldName);

            field.setAccessible(true);
            returnObject = field.get(object);

        } catch (NoSuchFieldException | IllegalAccessException e) {

            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                gameManager.getLogger().throwing(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), e.getCause());
            }
        }

        return returnObject;
    }
}
