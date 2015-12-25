package fr.azuxul.pacman.scoreboard;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import net.samagames.api.games.Status;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Scoreboard for PacMan plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class ScoreboardPacMan {

    private final GameManager gameManager;
    private final ScoreboardManager scoreboardManager;
    private final String displayName;

    private final String TEAM_NAME = "global";

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

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective;

        if (scoreboard == null || scoreboard.getTeam(TEAM_NAME) == null) {

            scoreboard = scoreboardManager.getNewScoreboard(); // Get new scoreboard

            generateGlobalTeam(scoreboard);

        } else {

            Set<String> entries = scoreboard.getTeam(TEAM_NAME).getEntries();
            boolean regenerate = false;

            for (Player p : gameManager.getServer().getOnlinePlayers())
                if (!entries.contains(p.getName())) {
                    regenerate = true;
                    break;
                }

            if (regenerate)
                generateGlobalTeam(scoreboard);
        }

        String OBJECTIVE_NAME = "pacManObjective";
        objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective != null)
            objective.unregister();

        objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy"); // Register new objective
        int score = 0;

        List<PlayerPacMan> playerPacManList = gameManager.getPlayerPacManList();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId()); // Get playerPacMan
        PowerupEffectType activeBooster = playerPacMan.getActiveBooster();

        objective.setDisplayName(displayName); // Set display name
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Set display slot

        // Display remaining time
        score++;
        try {
            objective.getScore(ChatColor.GRAY + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds())).setScore(score);
        } catch (Exception e) {
            gameManager.getLogger().info(String.valueOf(e));
        }

        score++;
        objective.getScore("Temps restant:").setScore(score);

        score++;
        objective.getScore(" ").setScore(score);

        score++;
        objective.getScore("Booster actif: " + ChatColor.GREEN + (activeBooster == null ? "Aucun (0)" : activeBooster.getName() + " (" + playerPacMan.getBoosterRemainingTime() + ")")).setScore(score);

        score++;
        objective.getScore("   ").setScore(score);

        // Display remaining global coins number
        score++;
        int remainingCoins = gameManager.getCoinManager().getRemainingGlobalCoins();
        objective.getScore("Coins restant: " + ChatColor.GOLD + (remainingCoins < 0 ? 0 : remainingCoins)).setScore(score);

        // Display coins number
        score++;
        objective.getScore("Coins: " + ChatColor.GOLD + playerPacMan.getGameCoins()).setScore(score);

        score++;
        objective.getScore("  ").setScore(score);

        // Display classement
        Collections.sort(playerPacManList);
        int size = playerPacManList.size() - 1;
        int maxI = size >= 5 ? 4 : size;

        for (int i = 0; i <= maxI; i++) {

            PlayerPacMan playerPacManDisplay = playerPacManList.get(i); // Get playerPacMan

            try {
                score++;
                objective.getScore(ChatColor.GRAY + playerPacManDisplay.getOfflinePlayer().getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + playerPacManDisplay.getGameCoins()).setScore(score);
            } catch (NullPointerException e) {
                gameManager.getLogger().warning(String.valueOf(e));
            }
        }

        score++;
        objective.getScore("Classement: ").setScore(score);

        player.setScoreboard(scoreboard); // Send scoreboard to the player
    }

    private void generateGlobalTeam(Scoreboard scoreboard) {

        Team team = scoreboard.getTeam(TEAM_NAME);

        if (team != null)
            team.unregister();

        team = scoreboard.registerNewTeam(TEAM_NAME);

        team.setCanSeeFriendlyInvisibles(true);
        team.setNameTagVisibility(NameTagVisibility.NEVER);

        for (Player player : gameManager.getServer().getOnlinePlayers())
            team.addEntry(player.getName());

    }
}
