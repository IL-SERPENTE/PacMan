package fr.azuxul.pacman.timer;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.api.games.Status;
import org.bukkit.Server;

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
        this.minutes = 5;
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
        }

        // Update scoreboard to all player and update player
        server.getOnlinePlayers().forEach(player -> {
            gameManager.getScoreboard().sendScoreboardToPlayer(player, gameStatus);

            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

            if (playerPacMan != null)
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
