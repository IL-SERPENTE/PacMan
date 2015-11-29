package fr.azuxul.pacman;

import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.scoreboard.ScoreboardPacMan;
import fr.azuxul.pacman.timer.TimerPacMan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
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

    public GameManager(Logger logger, Plugin plugin) {

        this.server = Bukkit.getServer();
        this.logger = logger;
        this.plugin = plugin;
        this.scoreboard = new ScoreboardPacMan(ChatColor.YELLOW + "Pac-Man", this);
        this.timer = new TimerPacMan(this);
        this.playerPacManList = new ArrayList<>();
    }

    public void updatePlayerNb() {

        int players = server.getOnlinePlayers().size();

        if(players >= 6) {
            minPlayer = true;
            if(players >= 10)
                maxPlayer = true;
        }
    }

    public List<PlayerPacMan> getPlayerPacManList() {
        return playerPacManList;
    }

    public TimerPacMan getTimer() {
        return timer;
    }

    public ScoreboardPacMan getScoreboard() {
        return scoreboard;
    }

    public Server getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isEnd() {
        return end;
    }

    public boolean isMaxPlayer() {
        return maxPlayer;
    }

    public boolean isMinPlayer() {
        return minPlayer;
    }

    public void start() {

    }

    public void end() {

    }
}
