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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        player.setGameMode(GameMode.ADVENTURE);

        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (playerPacMan == null) { // If player is not in player pacman list

            playerPacMan = new PlayerPacMan(player);
            gameManager.getPlayerPacManList().add(playerPacMan); // Add this player in list
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        GameManager gameManager = PacMan.getGameManager();
        Status status = SamaGamesAPI.get().getGameManager().getGameStatus();

        if (!status.equals(Status.IN_GAME) || event.getEntity() instanceof ArmorStand || event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) // If is not started
            event.setCancelled(true); // Cancel damages
        else if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

            int coins = playerPacMan.getGameCoins(); // Get player coins

            for (int i = RandomUtils.nextInt(3); i >= 1; i--)
                if (coins > 0) { // If player coins number > 0
                    coins--; // Decrement coins of player

                    Location location = player.getLocation();

                    // Spawn coin
                    new Coin(((CraftWorld) player.getWorld()).getHandle(), location.getX(), location.getY() + 1.1, location.getZ(), true);

                    playerPacMan.setGameCoins(coins); // Set player coins
                }
        }
    }

    @EventHandler
    public void onPlayerKillByPlayer(EntityDeathEvent event) {

        Player killer = event.getEntity().getKiller();

        if (event.getEntity() instanceof Player && killer != null)
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true); // Cancel player interaction
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true); // Cancel food level change
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {

        if(!event.getWorld().isThundering()) // If is sunny
            event.setCancelled(true); // Cancel weather change
    }

}
