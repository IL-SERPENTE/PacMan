package fr.azuxul.pacman.scoreboard;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.api.games.Status;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * Scoreboard for PacMan plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class ScoreboardPacMan {

    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private String displayName;

    public ScoreboardPacMan(String displayName, GameManager gameManager) {
        this.gameManager = gameManager; // Set gameManager
        this.scoreboardManager = gameManager.getServer().getScoreboardManager(); // Set scoreboard manager
        this.displayName = displayName; // Set display name
    }

    /**
     * Send scoreboard to param player
     *
     * @param player Receive scoreboard
     * @param status Game status
     */
    public void sendScoreboardToPlayer(Player player, Status status) {

        if (!status.equals(Status.IN_GAME)) // If game is not started
            return;

        Scoreboard scoreboard = scoreboardManager.getNewScoreboard(); // Get new scoreboard
        Objective objective = scoreboard.registerNewObjective("sc", "dummy"); // Register new objective
        int score = 0;

        List<PlayerPacMan> playerPacManList = gameManager.getPlayerPacManList();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId()); // Get playerPacMan

        objective.setDisplayName(displayName); // Set display name
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set display slot

        // Display remaining time
        score++;
        try {
            objective.getScore(ChatColor.GRAY + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds())).setScore(score);
        } catch (IllegalFormatException e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                gameManager.getLogger().throwing(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), e.getCause());
            }
        }

        score++;
        objective.getScore("Temps restant:").setScore(score);

        score++;
        objective.getScore(" ").setScore(score);

        // Display remaining global coins number
        score++;
        int remainingCoins = gameManager.getRemainingGlobalCoins();
        objective.getScore("Coins restants: " + ChatColor.GOLD + (remainingCoins < 0 ? 0 : remainingCoins)).setScore(score);

        // Display coins number
        score++;
        objective.getScore("Coins: " + ChatColor.GOLD + playerPacMan.getGameCoins()).setScore(score);

        score++;
        objective.getScore("  ").setScore(score);

        // Display classement
        Collections.sort(playerPacManList);
        int size = playerPacManList.size() - 1;
        int maxI = (size >= 5 ? 4 : size);

        for (int i = 0; i <= maxI; i++) {

            PlayerPacMan playerPacManDisplay = playerPacManList.get(i); // Get playerPacMan

            try {
                score++;
                objective.getScore(ChatColor.GRAY + playerPacManDisplay.getOfflinePlayer().getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + playerPacManDisplay.getGameCoins()).setScore(score);
            } catch (NullPointerException e) {
                throw new NullPointerException("PlayerPacMan values can not be null");
            }
        }

        score++;
        objective.getScore("Classement: ").setScore(score);

        player.setScoreboard(scoreboard); // Send scoreboard to the player
    }
}
