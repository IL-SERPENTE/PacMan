package fr.azuxul.pacman.scoreboard;

import fr.azuxul.pacman.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

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
        this.gameManager = gameManager;
        this.scoreboardManager = gameManager.getServer().getScoreboardManager();
        this.displayName = displayName;
    }

    public void sendScoreboardToPlayer(Player player) {

        Scoreboard scoreboard = scoreboardManager.getNewScoreboard(); // Get new scoreboard
        Objective objective = scoreboard.registerNewObjective("sc", "dummy"); // Register new objective
        int score = 0;

        objective.setDisplayName(displayName); // Set display name
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set display slot

        score++;
        try {
            objective.getScore(ChatColor.GRAY + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds())).setScore(score);
        } catch (Exception e) {
            objective.getScore(ChatColor.GRAY + "00:00").setScore(score);
        }

        score++;
        objective.getScore("Temps restant:").setScore(score);

        score++;
        objective.getScore("  ").setScore(score);

        player.setScoreboard(scoreboard);
    }
}
