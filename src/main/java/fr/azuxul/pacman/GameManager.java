package fr.azuxul.pacman;

import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.scoreboard.ScoreboardPacMan;
import fr.azuxul.pacman.timer.TimerPacMan;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.themachine.messages.ITemplateManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * GameManager for PacMan plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class GameManager extends Game<PlayerPacMan> {

    private Server server;
    private Logger logger;
    private Plugin plugin;
    private TimerPacMan timer;
    private ScoreboardPacMan scoreboard;
    private SamaGamesAPI samaGamesAPI;
    private List<PlayerPacMan> playerPacManList;
    private int remainingGlobalCoins, globalCoins;

    /**
     * Class constructor
     *
     * @param logger          plugin logger
     * @param server          server
     * @param gameCodeName    unique id for game
     * @param gameName        game name
     * @param gameDescription game description
     * @param gamePlayerClass game players class
     */
    public GameManager(Logger logger, Plugin plugin, Server server, String gameCodeName, String gameName, String gameDescription, Class<PlayerPacMan> gamePlayerClass) {

        super(gameCodeName, gameName, gameDescription, gamePlayerClass);

        this.server = server;
        this.logger = logger;
        this.plugin = plugin;
        this.scoreboard = new ScoreboardPacMan(ChatColor.YELLOW + "Pac-Man", this);
        this.samaGamesAPI = SamaGamesAPI.get();
        this.timer = new TimerPacMan(this, samaGamesAPI);
        this.playerPacManList = new ArrayList<>();
    }

    /**
     * Get list of playerPacMan
     *
     * @return playerListPacMan
     */
    public List<PlayerPacMan> getPlayerPacManList() {
        return playerPacManList;
    }

    /**
     * Get game timer
     *
     * @return timer
     */
    public TimerPacMan getTimer() {
        return timer;
    }

    /**
     * Get game scoreboard
     *
     * @return scoreboard
     */
    public ScoreboardPacMan getScoreboard() {
        return scoreboard;
    }

    /**
     * Get server
     *
     * @return server
     */
    public Server getServer() {
        return server;
    }

    /**
     * Get logger of plugin
     *
     * @return logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Get plugin
     *
     * @return plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get number of global coins remaining
     *
     * @return remainingGlobalCoins
     */
    public int getRemainingGlobalCoins() {
        return remainingGlobalCoins;
    }

    /**
     * Set number of global coins remaining
     *
     * @param remainingGlobalCoins global coins remaining
     */
    public void setRemainingGlobalCoins(int remainingGlobalCoins) {
        this.remainingGlobalCoins = remainingGlobalCoins;
    }

    /**
     * Set number of global coins
     *
     * @param globalCoins global coins
     */
    public void setGlobalCoins(int globalCoins) {
        this.setRemainingGlobalCoins(globalCoins);
        this.globalCoins = globalCoins;
    }

    /**
     * Start the game
     */
    @SuppressWarnings("deprecation")
    @Override
    public void startGame() {

        super.startGame();

        Location spawn = new Location(getServer().getWorlds().get(0), 0, 78, 0);

        for (Player player : server.getOnlinePlayers()) {
            player.teleport(spawn); // Teleport player to spawn
            player.setGameMode(GameMode.ADVENTURE); // Set player gamemode
        }
    }

    /**
     * Set end of game
     */
    public void end() {

        ITemplateManager templateManager = samaGamesAPI.getGameManager().getCoherenceMachine().getTemplateManager();
        List<PlayerPacMan> winners = new ArrayList<>();

        timer.setToZero(); // Set timer to zero

        // Sort playerPacManList
        Collections.sort(playerPacManList);

        int playerPacManListSize = playerPacManList.size();

        // Get winners
        for (int i = 1; i <= 3; i++) {
            if (winners.size() >= playerPacManListSize)
                break;

            winners.add(playerPacManList.get(playerPacManListSize - i));
        }

        // Add coins to players
        for (PlayerPacMan playerPacMan : playerPacManList) {

            int percentOfCoins = 0;

            try {
                percentOfCoins = Math.round(playerPacMan.getCoins() * 100 / globalCoins); // Calculate percent of player coins
                int coins = percentOfCoins / 5; // Calculate coins for player

                playerPacMan.addCoins(coins, percentOfCoins + "% des coins récupérer");
            } catch (ArithmeticException ignored) {
            }

            if (winners.contains(playerPacMan)) {
                if (winners.indexOf(playerPacMan) == 0) {
                    playerPacMan.addCoins(30, "Partie gagnée");
                    playerPacMan.addStars(2, "Partie gagnée");
                } else {
                    playerPacMan.addCoins(15, "Términer dans le classement");
                    playerPacMan.addStars(1, "Términer dans le classement");
                }
            }

            if (percentOfCoins >= 35) {
                playerPacMan.addStars(1, "Plus de 35% de coins récupérer");
            }
        }

        // Display winners
        if (!winners.isEmpty()) { // If winners is not empty
            if (winners.size() < 3) { // If winners size < 3

                PlayerPacMan winner = winners.get(0); // Get winner

                templateManager.getPlayerWinTemplate().execute(winner.getPlayerIfOnline(), winner.getCoins()); // Display player win template
            } else {

                PlayerPacMan winner = winners.get(0), second = winners.get(1), third = winners.get(2); // Get winners

                templateManager.getPlayerLeaderboardWinTemplate().execute(winner.getPlayerIfOnline(), second.getPlayerIfOnline(), third.getPlayerIfOnline(), winner.getCoins(), second.getCoins(), third.getCoins()); // Display players leadboard template
            }
        }

        this.handleGameEnd();
    }
}
