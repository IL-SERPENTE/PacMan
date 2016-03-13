package fr.azuxul.pacman;

import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.portal.PortalManager;
import fr.azuxul.pacman.scoreboard.ScoreboardPacMan;
import fr.azuxul.pacman.timer.TimerPacMan;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Game;
import net.samagames.api.games.Status;
import net.samagames.api.games.themachine.messages.ITemplateManager;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.RulesBook;
import net.samagames.tools.powerups.PowerupManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GameManager for PacMan plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class GameManager extends Game<PlayerPacMan> {

    private final Server server;
    private final Plugin plugin;
    private final TimerPacMan timer;
    private final ScoreboardPacMan scoreboard;
    private final PowerupManager powerupManager;
    private final GommeManager gommeManager;
    private final PortalManager portalManager;
    private final Location spawn;
    private final Location mapCenter;
    private final List<Location> spawns;
    private final ItemStack rulesBook;

    /**
     * Class constructor
     *
     * @param plugin PacMan plugin
     */
    public GameManager(JavaPlugin plugin) {

        super("pacman", "PacMan", "", PlayerPacMan.class);

        this.server = plugin.getServer();
        this.plugin = plugin;
        this.scoreboard = new ScoreboardPacMan(ChatColor.YELLOW + "PacMan", this);
        this.timer = new TimerPacMan(this);
        this.powerupManager = new PowerupManager(plugin);
        this.gommeManager = new GommeManager(this);
        this.portalManager = new PortalManager(this);
        this.spawn = LocationUtils.str2loc(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("waiting-lobby").getAsString());
        this.mapCenter = LocationUtils.str2loc(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("map-center").getAsString());
        this.spawns = new ArrayList<>();

        SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("spawn-points").getAsJsonArray().forEach(location -> spawns.add(LocationUtils.str2loc(location.getAsString())));
        Collections.shuffle(spawns);

        this.rulesBook = new RulesBook("§6§lLivre de règles").addOwner("Azuxul")
                .addPage("§lBut du jeu ?§0",
                        " Vous devez\n récupérer le plus\n" +
                                " de gommes\n possible avant la\n" +
                                " fin de la\n partie. (plus de\n" +
                                " pièces ou\n temps écoulé)")
                .addPage("§lPvp ?§0",
                        " Vous pouvez taper\n" +
                                " vos adversaires\n" +
                                " pour leur faire\n perdre des gommes !\n" +
                                " Quand un joueur\n meurt, il lâche\n" +
                                " 20% de ses gommes.")
                .addPage("&lPowerups &0",
                        " Durant la partie,\n certains powerups\n" +
                                " peuvent apparaître !\n Prenez les" +
                                " tous et\n découvrez les !").toItemStack();
    }

    /**
     * Get list of winners
     *
     * @param playerPacManList players
     * @return winners list
     */
    private static List<PlayerPacMan> getWinners(List<PlayerPacMan> playerPacManList) {

        List<PlayerPacMan> winners = new ArrayList<>();

        // Sort playerPacManList
        Collections.sort(playerPacManList);

        int playerPacManListSize = playerPacManList.size();

        // Get winners
        for (int i = 1; i <= 3; i++) {
            if (playerPacManListSize <= 0 || winners.size() >= playerPacManListSize || playerPacManListSize - i >= playerPacManListSize || playerPacManListSize - i < 0)
                break;

            PlayerPacMan playerPacMan = playerPacManList.get(playerPacManListSize - i);

            if (playerPacMan.getPlayerIfOnline() != null)
                winners.add(playerPacMan);

        }

        Collections.sort(winners);

        return winners;
    }

    @Override
    public void handleLogout(Player player) {
        super.handleLogout(player);

        if (getConnectedPlayers() <= 1 && getStatus().equals(Status.IN_GAME))
            end(EndCause.PLAYER_LOGOUT);
    }

    @Override
    public void handleLogin(Player player) {

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().addItem(getRulesBook());

        player.teleport(getSpawn());

        super.handleLogin(player);
    }

    public ItemStack getRulesBook() {
        return rulesBook;
    }

    /**
     * Get portal manager
     *
     * @return portalManager
     */
    public PortalManager getPortalManager() {
        return portalManager;
    }

    /**
     * Get spawn platform location
     *
     * @return spawn location
     */
    public Location getSpawn() {
        return spawn;
    }

    /**
     * Get map center location
     *
     * @return map center location
     */
    public Location getMapCenter() {
        return mapCenter;
    }

    /**
     * Get list of playerPacMan
     *
     * @return playerListPacMan
     */
    public List<PlayerPacMan> getPlayerPacManList() {
        return new ArrayList<>(getInGamePlayers().values());
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
     * Get plugin
     *
     * @return plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get powerup manager
     *
     * @return powerupManager
     */
    public PowerupManager getPowerupManager() {
        return powerupManager;
    }

    public GommeManager getGommeManager() {
        return gommeManager;
    }

    /**
     * Start the game
     * Init players
     */
    @Override
    public void startGame() {

        super.startGame();

        Location playerSpawn = mapCenter;
        playerSpawn.add(0, 5, 0);
        ItemStack woodenSword = new ItemStack(Material.WOOD_SWORD);
        ItemMeta swordMeta = woodenSword.getItemMeta();

        swordMeta.spigot().setUnbreakable(true);
        woodenSword.setItemMeta(swordMeta);

        int spawnIndex = 0;

        for (PlayerPacMan playerPacMan : getPlayerPacManList()) {

            Player player = playerPacMan.getPlayerIfOnline();

            player.setGameMode(GameMode.ADVENTURE); // Set player game mode
            player.setExp(0.0f); // Clear XP
            player.getInventory().clear(); // Clear inventory
            player.getInventory().addItem(woodenSword); // Give wooden sword
            player.teleport(spawns.get(spawnIndex));
            player.getInventory().setHeldItemSlot(0);
            player.setBedSpawnLocation(playerSpawn, true);
            spawnIndex++;

            playerPacMan.setInvulnerableTime(5);

            if (spawnIndex >= spawns.size()) {
                spawnIndex = 0;
            }
        }

        playerSpawn.getWorld().setSpawnLocation(playerSpawn.getBlockX(), playerSpawn.getBlockY(), playerSpawn.getBlockZ());

        powerupManager.start();
    }

    public boolean isTestServer() {

        return SamaGamesAPI.get().getServerName().startsWith("TestServer_");
    }

    /**
     * Set end of game
     * Reward players
     * Display winners
     */
    public void end(EndCause cause) {

        displayEndMessage(cause);

        ITemplateManager templateManager = getCoherenceMachine().getTemplateManager();
        List<PlayerPacMan> playerPacManList = getPlayerPacManList();
        List<PlayerPacMan> winners = getWinners(playerPacManList);

        timer.setToZero(); // Set timer to zero
        powerupManager.stop();

        // Add Gommes to players
        for (PlayerPacMan playerPacMan : playerPacManList) {

            int percentOfGommes = 0;

            if (playerPacMan.getGomme() > 0 && gommeManager.getGlobalGommes() > 0) {
                percentOfGommes = playerPacMan.getGomme() * 100 / gommeManager.getGlobalGommes(); // Calculate percent of player Gommes
                int coins = percentOfGommes / 2; // Calculate coins for player

                playerPacMan.addCoins(coins, percentOfGommes + "% des gommes récupérée");
            }

            if (percentOfGommes >= 35) {
                playerPacMan.addStars(1, "Plus de 35% des gommes récupérée");
            }
        }

        // Display winners
        if (!winners.isEmpty()) { // If winners is not empty

            int winnerSize = winners.size();
            PlayerPacMan winner;

            if (winnerSize < 3) { // If winners size < 3

                winner = winners.get(winnerSize - 1); // Get winner

                templateManager.getPlayerWinTemplate().execute(winner.getPlayerIfOnline(), winner.getGomme()); // Display player win template
            } else {

                // Get winners
                winner = winners.get(2);
                PlayerPacMan second = winners.get(1);
                PlayerPacMan third = winners.get(0);

                String message = "Terminé dans le classement";

                second.addCoins(15, message);
                second.addStars(1, message);
                third.addCoins(15, message);
                third.addStars(1, message);

                templateManager.getPlayerLeaderboardWinTemplate().execute(winner.getPlayerIfOnline(), second.getPlayerIfOnline(), third.getPlayerIfOnline(), winner.getGomme(), second.getGomme(), third.getGomme()); // Display players leadboard template
            }

            winner.addCoins(30, "Partie gagnée");
            winner.addStars(2, "Partie gagnée");
        }


        this.handleGameEnd();
    }

    private void displayEndMessage(EndCause cause) {

        if (cause.equals(EndCause.TIMER))
            getServer().broadcastMessage(getCoherenceMachine().getGameTag() + ChatColor.GOLD + " Le timer est arrivé à zéro, la partie est terminée!");
        else if (cause.equals(EndCause.GOMMES))
            getServer().broadcastMessage(getCoherenceMachine().getGameTag() + ChatColor.GOLD + " Toutes les gommes ont été récupérées, la partie est terminée!");
        else
            getServer().broadcastMessage(getCoherenceMachine().getGameTag() + ChatColor.GOLD + " Il n'y a plus de joueurs, la partie est terminée!");
    }
}
