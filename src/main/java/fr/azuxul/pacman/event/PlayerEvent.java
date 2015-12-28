package fr.azuxul.pacman.event;

import fr.azuxul.pacman.CoinManager;
import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.Utils;
import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.games.Status;
import net.samagames.tools.ParticleEffect;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.meta.FireworkMeta;
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
        player.getInventory().clear();
        player.getInventory().addItem(Utils.getRulesBook());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        GameManager gameManager = PacMan.getGameManager();
        Player player = event.getPlayer();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (playerPacMan.getActiveBooster() != null && playerPacMan.getActiveBooster().equals(PowerupEffectType.SPEED)) {

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
            CoinManager coinManager = gameManager.getCoinManager();
            int coins = playerPacMan.getGameCoins(); // Get player coins

            if (playerPacMan.getInvulnerableRemainingTime() >= 0) {
                event.setCancelled(true);
            } else if (coins > 0) {

                for (int i = RandomUtils.nextInt(3); i >= 1; i--) {

                    coins--; // Decrement coins of player

                    Location location = player.getLocation();

                    // Spawn coin
                    coinManager.spawnCoin(((CraftWorld) player.getWorld()).getHandle(), location.getX(), location.getY() + 1.1, location.getZ(), true);

                    playerPacMan.setGameCoins(coins); // Set player coins
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKillByPlayer(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();
        GameManager gameManager = PacMan.getGameManager();

        if (gameManager.getStatus().equals(Status.IN_GAME)) {

            Player player = event.getEntity();
            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());
            CoinManager coinManager = gameManager.getCoinManager();

            if (killer != null)
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));

            int coins = 0;
            int playerCoins = playerPacMan.getGameCoins();

            if (playerPacMan.getGameCoins() > 0)
                coins = (int) Math.round(playerCoins * 0.2); // Calculate percent of player coins

            playerPacMan.setGameCoins(playerCoins - coins);
            World world = ((CraftWorld) player.getWorld()).getHandle();
            Location location = player.getLocation();
            double x = location.getX(), y = location.getY(), z = location.getZ();

            if (coins > 0)
                coinManager.spawnBigCoin(world, x, y, z, true, coins);

            ParticleEffect.FIREWORKS_SPARK.display(2.0f, 2.0f, 2.0f, 0.0f, 50, location, 50.0f);

            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.STAR).trail(true).flicker(true).withColor(Color.ORANGE, Color.RED).withFade(Color.BLUE, Color.GREEN).build();

            fireworkMeta.addEffect(effect);
            fireworkMeta.setPower(2);

            firework.setFireworkMeta(fireworkMeta);

            event.setDeathMessage("");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        GameManager gameManager = PacMan.getGameManager();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (gameManager.getStatus().equals(Status.IN_GAME)) {
            playerPacMan.setInvulnerableTime(5);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
            player.teleport(gameManager.getMapCenter().add(0, 5, 0));
        }
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getItem() == null || (!event.getItem().getType().equals(Material.WRITTEN_BOOK) && !(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))))
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
