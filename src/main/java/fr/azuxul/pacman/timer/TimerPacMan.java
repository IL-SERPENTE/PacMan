package fr.azuxul.pacman.timer;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Server;

/**
 * Timer for PacMan
 *
 * @author Azuxul
 * @version 1.0
 */
public class TimerPacMan implements Runnable {

    private short seconds, minutes, secondsBStart;
    private GameManager gameManager;
    private Server server;

    public TimerPacMan(GameManager gameManager) {
        this.gameManager = gameManager;
        this.server = gameManager.getServer();
        this.minutes = 20;
        this.secondsBStart = -2;
    }

    @Override
    public void run() {

        if (!gameManager.isStart()) {

            // TIMER BEFORE START

            if (secondsBStart < -1) {

                // If min players
                if (gameManager.isMinPlayer()) {
                    secondsBStart = 5; // Set timer to 45 // TODO: Set to 45
                    Utils.sendHotbarMessage(server.getOnlinePlayers(), ChatColor.YELLOW + "La partie va bientot commencer !"); // Send hotbar message
                } else
                    Utils.sendHotbarMessage(server.getOnlinePlayers(), ChatColor.GREEN + "En attente de joueurs"); // Send hotbar message

            } else if (secondsBStart == 0) { // If timer is 0

                gameManager.start(); // Start
                secondsBStart = -1;

            } else if (!gameManager.isMinPlayer()) { // If is not min players

                secondsBStart = -2; // Reset timer
                server.broadcastMessage(ChatColor.RED + "Démarrage annulé ! En attente de joueurs"); // Send message
            } else if (secondsBStart > 0) {

                if (gameManager.isMaxPlayer() && secondsBStart > 30) // If max player
                    secondsBStart = 30; // Set timer to 30

                if (secondsBStart == 30 || secondsBStart == 15 || secondsBStart == 10 || (secondsBStart <= 5 && secondsBStart > 0))
                    Utils.sendHotbarMessage(server.getOnlinePlayers(), ChatColor.YELLOW + "La partie commence dans " + ChatColor.DARK_GREEN + secondsBStart + "s");


                secondsBStart--; // Decrement timer
            }


        } else if (!gameManager.isEnd()) {

            // GAME TIMER

            seconds--;
            if (seconds <= 0) {
                minutes--;
                seconds = 59;
                if (minutes <= 0)
                    gameManager.end();
            }
        }

        server.getOnlinePlayers().forEach(player -> gameManager.getScoreboard().sendScoreboardToPlayer(player));
    }

    public short getSeconds() {
        return seconds;
    }

    public short getMinutes() {
        return minutes;
    }
}
