package fr.azuxul.pacman.event;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.ChatColor;
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

        gameManager.updatePlayerNb();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        GameManager gameManager = PacMan.getGameManager();

        if (!gameManager.isStart())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        GameManager gameManager = PacMan.getGameManager();

        player.getNearbyEntities(0, 0, 0).stream().filter(entity -> ((CraftEntity) entity).getHandle() instanceof EntityArmorStand && ((CraftEntity) entity).getHandle().getCustomName().equals(ChatColor.GOLD + "Coin")).forEach(entity -> { // Get entities Coin in radius of 0

            entity.remove(); // Kill

            PlayerPacMan playerPacMan = PlayerPacMan.getPlayerPacManInList(gameManager.getPlayerPacManList(), player.getUniqueId());
            playerPacMan.setCoins(playerPacMan.getCoins() + 1); // Add coin to player

            System.out.println(playerPacMan.getCoins());
        });
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

}
