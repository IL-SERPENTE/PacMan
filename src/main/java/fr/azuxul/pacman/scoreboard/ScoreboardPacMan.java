package fr.azuxul.pacman.scoreboard;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
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

    public void sendScoreboardToPlayer(Player player) {

        if(!gameManager.isStart())
            return;

        Scoreboard scoreboard = scoreboardManager.getNewScoreboard(); // Get new scoreboard
        Objective objective = scoreboard.registerNewObjective("sc", "dummy"); // Register new objective
        int score = 0;

        List<PlayerPacMan> playerPacManList = gameManager.getPlayerPacManList();
        PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId()); // Get playerPacMan

        objective.setDisplayName(displayName); // Set display name
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set display slot

        // Display remaining time
        score++;
        try {
            objective.getScore(ChatColor.GRAY + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds())).setScore(score);
        } catch (IllegalFormatException e) {
            e.printStackTrace();
        }

        score++;
        objective.getScore("Temps restant:").setScore(score);

        score++;
        objective.getScore(" ").setScore(score);

        // Display remaining global coins number
        score++;
        objective.getScore("Coins restants: " + ChatColor.GOLD + gameManager.getGlobalCoins()).setScore(score);

        // Display coins number
        score++;
        objective.getScore("Coins: " + ChatColor.GOLD + playerPacMan.getCoins()).setScore(score);

        score++;
        objective.getScore("  ").setScore(score);

        // Display classement
        Collections.sort(playerPacManList);
        int size = playerPacManList.size() - 1;
        for (int i = 0; i <= (size >= 5 ? 4 : size); i++) {

            PlayerPacMan playerPacManDisplay = playerPacManList.get(i); // Get playerPacMan

            try {
                score++;
                objective.getScore(ChatColor.GRAY + playerPacManDisplay.getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + playerPacManDisplay.getCoins()).setScore(score);
            } catch (NullPointerException e) {
                throw new NullPointerException("PlayerPacMan values can not be null");
            }
        }

        score++;
        objective.getScore("Classement: ").setScore(score);

        player.setScoreboard(scoreboard);
    }
}
