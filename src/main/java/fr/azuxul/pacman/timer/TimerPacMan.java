package fr.azuxul.pacman.timer;

import fr.azuxul.pacman.EndCause;
import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
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

    private final GameManager gameManager;
    private final Server server;
    private short seconds;
    private short minutes;

    /**
     * Class constructor
     *
     * @param gameManager game manager
     */
    public TimerPacMan(GameManager gameManager) {
        this.gameManager = gameManager;
        this.server = gameManager.getServer();
        this.minutes = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("timer").getAsShort();
    }

    /**
     * When is run, timer decrement second if is started
     */
    @Override
    public void run() {

        Status gameStatus = gameManager.getStatus();

        if (gameStatus.equals(Status.IN_GAME)) {

            // GAME TIMER

            seconds--;
            if (seconds <= -1) {
                minutes--;
                seconds = 59;
                if (minutes <= -1) {
                    setToZero();
                    gameManager.end(EndCause.TIMER);
                }
            }

            // Update scoreboard to all player and update player
            gameManager.getInGamePlayers().entrySet().forEach(entry -> {
                gameManager.getScoreboard().sendScoreboardToPlayer(entry.getValue().getPlayerIfOnline());

                PlayerPacMan playerPacMan = entry.getValue();

                if (playerPacMan != null)
                    playerPacMan.update();
            });
        }
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
