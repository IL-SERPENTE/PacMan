package fr.azuxul.pacman.event;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

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

        gameManager.updatePlayerNb(); // Update player status

        if (PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId()) == null) // If player is not in player pacman list
            gameManager.getPlayerPacManList().add(new PlayerPacMan(player.getUniqueId(), player.getDisplayName())); // Add this player in list
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        // TODO: Disallow if is full or started
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        GameManager gameManager = PacMan.getGameManager();

        if (!gameManager.isStart()) // If is not started
            event.setCancelled(true); // Cancel damages
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        GameManager gameManager = PacMan.getGameManager();

        if(gameManager.isStart() && !player.getGameMode().equals(GameMode.SPECTATOR)) {

            // Detect collides with coins
            player.getNearbyEntities(0, 0, 0).stream().filter(entity -> ((CraftEntity) entity).getHandle() instanceof EntityArmorStand && ((CraftEntity) entity).getHandle().getCustomName().equals(ChatColor.GOLD + "Coin")).forEach(entity -> { // Get entities Coin in radius of 0

                entity.remove(); // Kill

                PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId());
                playerPacMan.setCoins(playerPacMan.getCoins() + 1); // Add coin to player

                // Send scoreboard to player
                gameManager.getScoreboard().sendScoreboardToPlayer(player);

                // Set global coins
                int globalCoins = gameManager.getGlobalCoins() - 1;
                gameManager.setGlobalCoins(globalCoins);

                // If remaning coins is equals to 0 and is not end
                if(globalCoins <= 0 && !gameManager.isEnd())
                    gameManager.end(); // End

            });
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true); // Cancel player interaction
    }

}
