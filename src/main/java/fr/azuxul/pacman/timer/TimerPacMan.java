package fr.azuxul.pacman.timer;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.entity.Booster;
import net.samagames.api.games.Status;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

/**
 * Timer for PacMan
 *
 * @author Azuxul
 * @version 1.0
 */
public class TimerPacMan implements Runnable {

    private short seconds, minutes;
    private final GameManager gameManager;
    private final Server server;

    /**
     * Class constructor
     *
     * @param gameManager game manager
     */
    public TimerPacMan(GameManager gameManager) {
        this.gameManager = gameManager;
        this.server = gameManager.getServer();
        this.minutes = 20;
    }

    /**
     * When is run, timer decrement second if is started
     * else if minimum player number are reached, start
     * timer before game start
     */
    @Override
    public void run() {

        Status gameStatus = gameManager.getStatus();

        if (gameStatus.equals(Status.IN_GAME)) {

            // GAME TIMER

            seconds--;
            if (seconds <= 0) {
                minutes--;
                seconds = 60;
                if (minutes <= 0)
                    gameManager.end();
            }

            // If random and boosterLocation contain value with false
            if (RandomUtils.nextInt(100) <= 15 && gameManager.getBoosterLocations().values().contains(false)) {

                // Get all entry with value is false
                gameManager.getBoosterLocations().entrySet().stream().filter(mapEntity -> mapEntity.getValue().equals(false)).forEach(mapEntity -> {

                    Location location = mapEntity.getKey(); // Get location

                    // Get random type
                    Booster.BoosterTypes type = Booster.BoosterTypes.values()[RandomUtils.nextInt(Booster.BoosterTypes.values().length)];

                    // Spawn new booster
                    new Booster(((CraftWorld) gameManager.getServer().getWorlds().get(0)).getHandle(), location.getX(), location.getY(), location.getZ(), type);
                    gameManager.getBoosterLocations().put(location, true); // Set booster spawned to true
                });
            }
        }

        // Update scoreboard to all player and update player
        server.getOnlinePlayers().forEach(player -> {
            gameManager.getScoreboard().sendScoreboardToPlayer(player, gameStatus);
            gameManager.getPlayer(player.getUniqueId()).update();
        });
    }

    /**
     * Set timer to 0 minutes and 0 seconds
     */
    public void setToZero() {

        minutes = 0;
        seconds = 0;
    }

    /**
     * Get seconds remaining before end
     *
     * @return seconds
     */
    public short getSeconds() {
        return seconds;
    }

    /**
     * Get minutes remaining before end
     *
     * @return minutes
     */
    public short getMinutes() {
        return minutes;
    }
}
