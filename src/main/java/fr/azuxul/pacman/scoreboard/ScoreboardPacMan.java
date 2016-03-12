package fr.azuxul.pacman.scoreboard;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import net.samagames.api.games.Status;
import net.samagames.tools.chat.ActionBarAPI;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

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

    private static final String TEAM_NAME = "global";
    private final GameManager gameManager;
    private final ScoreboardManager scoreboardManager;
    private final String displayName;

    public ScoreboardPacMan(String displayName, GameManager gameManager) {
        this.gameManager = gameManager; // Set gameManager
        this.scoreboardManager = gameManager.getServer().getScoreboardManager(); // Set scoreboard manager
        this.displayName = displayName; // Set display name
    }

    private ObjectiveSign generateObjectiveSign() {

        ObjectiveSign objectiveSign = new ObjectiveSign("BombermanSc", displayName);

        objectiveSign.setLine(0, ChatColor.GRAY + "00:00");
        objectiveSign.setLine(1, "Temps restant :");
        objectiveSign.setLine(2, " ");
        objectiveSign.setLine(3, "Booster actif : " + ChatColor.GREEN + "Aucun (0)");
        objectiveSign.setLine(4, "   ");
        objectiveSign.setLine(5, "Gommes restantes : -1");
        objectiveSign.setLine(6, "Gommes : -1");
        objectiveSign.setLine(7, "  ");
        objectiveSign.setLine(8, "Classement : ");
        objectiveSign.setLine(9, "    ");

        return objectiveSign;
    }

    /**
     * Send scoreboard to param player
     *
     * @param player Receive scoreboard
     */
    public void sendScoreboardToPlayer(Player player) {

        if (!gameManager.getStatus().equals(Status.IN_GAME)) // If game is not started
            return;

        List<PlayerPacMan> playerPacManList = gameManager.getPlayerPacManList();
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard == null || scoreboard.getTeam(TEAM_NAME) == null) {

            scoreboard = scoreboardManager.getNewScoreboard(); // Get new scoreboard

            generateGlobalTeam(scoreboard);

        } else {

            Set<String> entries = scoreboard.getTeam(TEAM_NAME).getEntries();
            boolean regenerate = false;

            for (PlayerPacMan p : playerPacManList)
                if (!entries.contains(p.getPlayerIfOnline().getName())) {
                    regenerate = true;
                    break;
                }

            if (regenerate)
                generateGlobalTeam(scoreboard);
        }

        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId()); // Get playerPacMan
        ObjectiveSign objectiveSign = playerPacMan.getObjectiveSign();

        if (objectiveSign == null) {

            objectiveSign = generateObjectiveSign();
            playerPacMan.setObjectiveSign(objectiveSign);
            objectiveSign.addReceiver(player);
        }

        PowerupEffectType activeBooster = playerPacMan.getActiveBooster();

        // Display remaining time
        try {
            objectiveSign.setLine(0, ChatColor.GRAY + String.format("%02d:%02d", gameManager.getTimer().getMinutes(), gameManager.getTimer().getSeconds()));
        } catch (Exception e) {
            gameManager.getServer().getLogger().info(String.valueOf(e));
        }

        objectiveSign.setLine(3, "Booster actif : " + ChatColor.GREEN + (activeBooster == null ? "Aucun (0)" : activeBooster.getName() + " (" + playerPacMan.getBoosterRemainingTime() + ")"));

        // Display remaining global gommes number
        int remainingGommes = gameManager.getGommeManager().getRemainingGlobalGommes();
        objectiveSign.setLine(5, "Gommes restantes : " + ChatColor.GOLD + (remainingGommes < 0 ? 0 : remainingGommes));

        // Display gommes number
        objectiveSign.setLine(6, "Gommes : " + ChatColor.GOLD + playerPacMan.getGomme());

        // Display classement
        Collections.sort(playerPacManList);
        int size = playerPacManList.size() - 1;
        int maxI = size - 3 < 0 ? 0 : size - 3;
        int line = 7;

        for (int i = maxI; i < size + 1; i++) {

            PlayerPacMan playerPacManDisplay = playerPacManList.get(i); // Get playerPacMan

            try {
                line++;
                objectiveSign.setLine(line, ChatColor.GRAY + playerPacManDisplay.getOfflinePlayer().getName() + ChatColor.GRAY + " : " + ChatColor.GREEN + playerPacManDisplay.getGomme());
            } catch (NullPointerException e) {
                gameManager.getServer().getLogger().warning(String.valueOf(e));
            }
        }

        line++;
        objectiveSign.setLine(line, "Classement :");

        if (!player.getGameMode().equals(GameMode.SPECTATOR) && activeBooster != null)
            ActionBarAPI.sendMessage(player, ChatColor.GREEN + "Booster : " + ChatColor.GOLD + activeBooster.getName());

        player.setScoreboard(scoreboard); // Send scoreboard to the player
        objectiveSign.updateLines(false);
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
