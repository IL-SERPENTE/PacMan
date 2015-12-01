package fr.azuxul.pacman;

import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.scoreboard.ScoreboardPacMan;
import fr.azuxul.pacman.timer.TimerPacMan;
import org.bukkit.*;
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
public class GameManager {

    private Server server;
    private Logger logger;
    private Plugin plugin;
    private TimerPacMan timer;
    private ScoreboardPacMan scoreboard;
    private List<PlayerPacMan> playerPacManList;
    private boolean start, end, maxPlayer, minPlayer;
    private int globalCoins;

    /**
     * Class constructor
     *
     * @param logger plugin logger
     * @param plugin plugin
     * @param server server
     */
    public GameManager(Logger logger, Plugin plugin, Server server) {

        this.server = server;
        this.logger = logger;
        this.plugin = plugin;
        this.scoreboard = new ScoreboardPacMan(ChatColor.YELLOW + "Pac-Man", this);
        this.timer = new TimerPacMan(this);
        this.playerPacManList = new ArrayList<>();
    }

    /**
     * Update player status values
     *
     * @param playerDisconnect if update when player disconnect
     */
    public void updatePlayerNb(boolean playerDisconnect) {

        // Get player size and subtract one if playerDisconnect
        int playerSize = server.getOnlinePlayers().size() - (playerDisconnect ? 1 : 0);

        // TODO: Set to 6
        if (playerSize >= 1) { // If player nb is >= 6
            minPlayer = true; // Stet min player to true
            maxPlayer = playerSize >= 10; // Set max player to player nb >= 10
        } else
            minPlayer = false; // Else set to false
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
     * Get is game started
     *
     * @return started
     */
    public boolean isStart() {
        return start;
    }

    /**
     * Get is game end
     *
     * @return end
     */
    public boolean isEnd() {
        return end;
    }

    /**
     * Get if maximum number of player for
     * start game is reached
     *
     * @return maxPlayer
     */
    public boolean isMaxPlayer() {
        return maxPlayer;
    }

    /**
     * Get if minimum number of player for
     * start game is reached
     *
     * @return minPlayer
     */
    public boolean isMinPlayer() {
        return minPlayer;
    }

    /**
     * Get number of global coins remaining
     *
     * @return globalCoins
     */
    public int getGlobalCoins() {
        return globalCoins;
    }

    /**
     * Set number of global coins remaining
     *
     * @param globalCoins global coins remaining
     */
    public void setGlobalCoins(int globalCoins) {
        this.globalCoins = globalCoins;
    }

    /**
     * Start the game
     */
    @SuppressWarnings("deprecation")
    public void start() {

        Location spawn = new Location(getServer().getWorlds().get(0), 0, 78, 0);

        for (Player player : server.getOnlinePlayers()) {
            player.teleport(spawn); // Teleport player to spawn
            player.setGameMode(GameMode.ADVENTURE); // Set player gamemode
        }

        // Timer before start

        for (Player player : server.getOnlinePlayers()) {
            player.sendTitle(ChatColor.GREEN + "3", ""); // Send title to player
            player.playNote(player.getLocation(), Instrument.PIANO, new Note(0, Note.Tone.E, true)); // Play note to player
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            for (Player player : server.getOnlinePlayers()) {
                player.sendTitle(ChatColor.YELLOW + "2", ""); // Send title to player
                player.playNote(player.getLocation(), Instrument.PIANO, new Note(0, Note.Tone.E, true)); // Play note to player
            }
        }, 20L);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            for (Player player : server.getOnlinePlayers()) {
                player.sendTitle(ChatColor.RED + "1", ""); // Send title to player
                player.playNote(player.getLocation(), Instrument.PIANO, new Note(0, Note.Tone.E, true)); // Play note to player
            }
        }, 40L);

        // Start
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            start = true; // Set start

            server.getOnlinePlayers().forEach(player -> player.playNote(player.getLocation(), Instrument.PIANO, new Note(18))); // Play note to player

        }, 60L);
    }

    /**
     * Set end of game
     */
    public void end() {

        timer.setToZero(); // Set timer to zero

        end = true; // Set end

        // Sort playerPacManList
        Collections.sort(playerPacManList);

        // Display whiners
        int size = playerPacManList.size();
        server.broadcastMessage(ChatColor.GOLD + "------------------------------");
        if (size >= 1) {
            server.broadcastMessage(ChatColor.GREEN + "      Premier: " + playerPacManList.get(size - 1).getName() + ChatColor.GRAY + "(" + playerPacManList.get(size - 1).getCoins() + ")");
            if (size >= 2) {
                server.broadcastMessage(ChatColor.GREEN + "      Dexiéme: " + playerPacManList.get(size - 2).getName() + ChatColor.GRAY + "(" + playerPacManList.get(size - 2).getCoins() + ")");
                if (size >= 3)
                    server.broadcastMessage(ChatColor.GREEN + "      Troisiéme: " + playerPacManList.get(size - 3).getName() + ChatColor.GRAY + "(" + playerPacManList.get(size - 3).getCoins() + ")");
            }
        }
        server.broadcastMessage(ChatColor.GOLD + "------------------------------");

        // Kick players and shutdown the server after 15.30s
        server.getScheduler().runTaskTimer(plugin, () -> {

            // TODO: Remove com
            //server.getOnlinePlayers().forEach(player -> player.kickPlayer(""));
            //server.shutdown();

        }, 310L, 0L);
    }
}
