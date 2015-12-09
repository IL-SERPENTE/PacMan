package fr.azuxul.pacman.event;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.entity.Coin;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player events of PacMan plugin
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        GameManager gameManager = PacMan.getGameManager();
        Player player = event.getPlayer();

        PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId());

        if (playerPacMan == null) { // If player is not in player pacman list

            playerPacMan = new PlayerPacMan(player);
            gameManager.getPlayerPacManList().add(playerPacMan); // Add this player in list
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(PacMan.getGameManager().getPlayerPacManList(), event.getPlayer().getUniqueId());

        if (playerPacMan != null)
            playerPacMan.handleLogout();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        GameManager gameManager = PacMan.getGameManager();
        Status status = SamaGamesAPI.get().getGameManager().getGameStatus();

        if (!status.equals(Status.IN_GAME) || event.getEntity() instanceof ArmorStand) // If is not started
            event.setCancelled(true); // Cancel damages
        else if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId());

            int coins = playerPacMan.getCoins(); // Get player coins

            for (int i = RandomUtils.nextInt(3); i >= 1; i--)
                if (coins > 0) { // If player coins number > 0
                    coins--; // Decrement coins of player

                    Location location = player.getLocation();

                    // Spawn coin
                    new Coin(((CraftWorld) player.getWorld()).getHandle(), location.getX(), location.getY() + 1.1, location.getZ(), true);

                    playerPacMan.setCoins(coins); // Set player coins
                }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        GameManager gameManager = PacMan.getGameManager();
        Status status = SamaGamesAPI.get().getGameManager().getGameStatus();

        if (status.equals(Status.IN_GAME) && !player.getGameMode().equals(GameMode.SPECTATOR)) {

            // Detect collides with coins
            player.getNearbyEntities(0, 0, 0).stream().filter(entity -> ((CraftEntity) entity).getHandle() instanceof Coin && !entity.isDead()).forEach(entity -> { // Get entities Coin in radius of 0

                Coin coin = ((Coin) ((CraftEntity) entity).getHandle());

                entity.remove(); // Kill

                PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId());
                playerPacMan.setCoins(playerPacMan.getCoins() + 1); // Add coin to player

                // Send scoreboard to player
                gameManager.getScoreboard().sendScoreboardToPlayer(player, status);

                if (!coin.isDroopedByPlayer()) { // If coin was not drooped by player

                    // Set global coins
                    int globalCoins = gameManager.getRemainingGlobalCoins() - 1;
                    gameManager.setRemainingGlobalCoins(globalCoins);

                    // If remaining coins is equals to 0 and is not end
                    if (globalCoins <= 0 && !status.equals(Status.FINISHED))
                        gameManager.end(); // End
                }
            });
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true); // Cancel player interaction
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true); // Cancel food level change
    }

}
