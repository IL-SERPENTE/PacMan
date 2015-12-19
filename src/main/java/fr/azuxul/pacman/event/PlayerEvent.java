package fr.azuxul.pacman.event;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.entity.Booster;
import fr.azuxul.pacman.entity.Coin;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.games.Status;
import net.samagames.tools.ParticleEffect;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        GameManager gameManager = PacMan.getGameManager();
        Player player = event.getPlayer();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (playerPacMan.getActiveBooster() != null && playerPacMan.getActiveBooster().equals(Booster.BoosterTypes.SPEED)) {

            // Get random color
            ParticleEffect.ParticleColor color = new ParticleEffect.OrdinaryColor(RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255));

            Location location = player.getLocation();
            location.setPitch(0);
            location.add(0, 0.7, 0);

            // Display particle
            for (double i = 0; i <= 8; i++) {
                location.add(0, 0.1, 0);
                ParticleEffect.REDSTONE.display(color, location, 45D);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        GameManager gameManager = PacMan.getGameManager();
        Status status = gameManager.getStatus();

        if (!status.equals(Status.IN_GAME) || event.getEntity() instanceof ArmorStand || event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) // If is not started
            event.setCancelled(true); // Cancel damages
        else if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

            if (playerPacMan.getInvulnerableRemainingTime() >= 0) {
                event.setCancelled(true);
            } else {

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
    }

    @EventHandler
    public void onPlayerKillByPlayer(EntityDeathEvent event) {

        Player killer = event.getEntity().getKiller();
        GameManager gameManager = PacMan.getGameManager();

        if (gameManager.getStatus().equals(Status.IN_GAME) && event.getEntity() instanceof Player) {

            Player entity = (Player) event.getEntity();
            PlayerPacMan playerPacMan = gameManager.getPlayer(entity.getUniqueId());

            if (killer != null)
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));

            int coins = 0;
            int playerCoins = playerPacMan.getGameCoins();

            if (playerPacMan.getGameCoins() > 0)
                coins = (int) Math.round(playerCoins * 0.2); // Calculate percent of player coins

            playerPacMan.setGameCoins(playerCoins - coins);
            World world = ((CraftWorld) entity.getWorld()).getHandle();
            Location location = entity.getLocation();
            double x = location.getX(), y = location.getY(), z = location.getZ();

            for (int i = 0; i <= coins; i++)
                new Coin(world, x, y, z, true);

            //ParticleEffect.BlockData blockData = new ParticleEffect.BlockData(Material.GOLD_BLOCK, (byte) 0);

            //ParticleEffect.BLOCK_DUST.display(blockData, 5, 5, 5, 500, 500, location, 50);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        GameManager gameManager = PacMan.getGameManager();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (gameManager.getStatus().equals(Status.IN_GAME)) {
            playerPacMan.setInvulnerableRespawn();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        event.setCancelled(true); // Cancel player interaction
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true); // Cancel food level change
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {

        if (event.toWeatherState()) // If is sunny
            event.setCancelled(true); // Cancel weather change
    }

}
