package fr.azuxul.pacman.timer;

import fr.azuxul.pacman.GameManager;
import net.samagames.api.SamaGamesAPI;
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
    private GameManager gameManager;
    private Server server;
    private SamaGamesAPI samaGamesAPI;

    /**
     * Class constructor
     *
     * @param gameManager game manager
     */
    public TimerPacMan(GameManager gameManager, SamaGamesAPI samaGamesAPI) {
        this.gameManager = gameManager;
        this.server = gameManager.getServer();
        this.samaGamesAPI = samaGamesAPI;
        this.minutes = 20;
    }

    /**
     * When is run, timer decrement second if is started
     * else if minimum player number are reached, start
     * timer before game start
     */
    @Override
    public void run() {

        Status gameStatus = samaGamesAPI.getGameManager().getGameStatus();

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

        Status status = samaGamesAPI.getGameManager().getGameStatus();

        server.getOnlinePlayers().forEach(player -> gameManager.getScoreboard().sendScoreboardToPlayer(player, status));
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
