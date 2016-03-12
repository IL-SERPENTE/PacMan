package fr.azuxul.pacman.event;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.player.PlayerPacMan;
import fr.azuxul.pacman.portal.Portal;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.games.Status;
import net.samagames.tools.ParticleEffect;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
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

    private GameManager gameManager;

    public PlayerEvent(GameManager gameManager) {

        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Block block = event.getFrom().getBlock();
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

        if (gameManager.getStatus().equals(Status.IN_GAME) && block.getType().equals(Material.PORTAL)) {

            Portal portal = gameManager.getPortalManager().getPortalAtLocation(event.getFrom());

            if (portal != null)
                portal.teleportPlayer(player);
        } else if (event.getTo().getY() <= 0) {

            player.teleport(gameManager.getSpawn());
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        Status status = gameManager.getStatus();

        if (!status.equals(Status.IN_GAME) || event.getEntity() instanceof ArmorStand || event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) // If is not started
            event.setCancelled(true); // Cancel damages
        else if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());
            int gommes = playerPacMan.getGomme(); // Get player gommes

            if (playerPacMan.getInvulnerableRemainingTime() >= 0) {
                event.setCancelled(true);
            } else if (gommes > 0) {

                playerPacMan.damage();
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {

            Player damager = (Player) event.getDamager();
            PlayerPacMan damgerPacMan = gameManager.getPlayer(damager.getUniqueId());

            if (damgerPacMan.getInvulnerableRemainingTime() >= 0) {
                damager.sendMessage(gameManager.getCoherenceMachine().getGameTag() + ChatColor.GRAY + " Vous n'êtes plus invulnérable aux dégats.");
                damgerPacMan.setInvulnerableTime(-1);
            }
        }
    }

    @EventHandler
    public void onPlayerKillByPlayer(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();

        if (gameManager.getStatus().equals(Status.IN_GAME)) {

            Player player = event.getEntity();
            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

            if (killer != null) {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20, 19, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));

                PlayerPacMan killerPacMan = gameManager.getPlayer(killer.getUniqueId());
                killerPacMan.setKills(killerPacMan.getKills() + 1);
            }

            int gommes = 0;
            int playerGommes = playerPacMan.getGomme();

            if (playerGommes > 4)
                gommes = (int) Math.round(playerGommes * 0.2); // Calculate percent of player gommes

            if (playerGommes - gommes >= 0)
                playerPacMan.setGomme(playerGommes - gommes);

            World world = ((CraftWorld) player.getWorld()).getHandle();
            Location location = player.getLocation();
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            if (gommes > 0)
                gameManager.getGommeManager().spawnBigGomme(world, x, y, z, true, gommes);

            ParticleEffect.FIREWORKS_SPARK.display(2.0f, 2.0f, 2.0f, 0.0f, 50, location, 50.0f);

            Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.STAR).trail(true).flicker(true).withColor(Color.ORANGE, Color.RED).withFade(Color.BLUE, Color.GREEN).build();

            fireworkMeta.addEffect(effect);
            fireworkMeta.setPower(2);

            firework.setFireworkMeta(fireworkMeta);

            event.setDeathMessage("");

            player.spigot().respawn();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

        if (gameManager.getStatus().equals(Status.IN_GAME)) {
            playerPacMan.setInvulnerableTime(5);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));

            event.setRespawnLocation(gameManager.getGommeManager().getGommeList().get(0).getLocation().add(0, 0.3, 0));
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

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true); // Cancel player drop item
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

}
