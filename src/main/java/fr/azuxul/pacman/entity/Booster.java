package fr.azuxul.pacman.entity;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.player.PlayerPacMan;
import net.minecraft.server.v1_8_R3.*;
import net.samagames.api.games.Status;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Booster entity
 *
 * @author Azuxul
 * @version 1.0
 */
public class Booster extends EntityItem {

    private final EntityArmorStand armorStand;
    private final BoosterTypes type;
    private final GameManager gameManager;

    /**
     * Constructor of Booster
     *
     * @param world world of spawn
     * @param x x location
     * @param y y location
     * @param z z location
     * @param type type of booster
     *             @see fr.azuxul.pacman.entity.Booster.BoosterTypes
     */
    public Booster(World world, double x, double y, double z, BoosterTypes type) {

        super(world);

        double spawnY = y + 1.5d;
        this.type = type;
        ItemStack itemStack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(type.getMaterial()));
        armorStand = new EntityArmorStand(world, x, spawnY, z);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        gameManager = PacMan.getGameManager();

        initArmorStand(armorStand);

        setItemStack(itemStack);

        b(nbtTagCompound); // Init nbtTagCompound

        nbtTagCompound.setBoolean("Invulnerable", true); // Set Invulnerable
        nbtTagCompound.setInt("Age", -32768); // Set Age
        nbtTagCompound.setInt("PickupDelay", 32767); // Set CustomName

        a(nbtTagCompound); // Set nbtTagCompound

        spawn(world, x, spawnY, z); // Spawn
    }

    /**
     * Spawn this booster and armorStand
     * Set booster vehicle with armorStand
     *
     * @param world world to spawn
     * @param x     x location
     * @param y     y location
     * @param z     z location
     */
    private void spawn(World world, double x, double y, double z) {

        this.setPosition(x, y, z); // Set location
        world.addEntity(this); // Spawn

        armorStand.getBukkitEntity().setPassenger(this.getBukkitEntity());
        world.addEntity(armorStand);
    }

    /**
     * Initialize armor stand
     * this armor stand is used for displaying
     * the name of this booster and hold booster
     *
     * @param armorStand used armorStand
     */
    private void initArmorStand(EntityArmorStand armorStand) {

        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        armorStand.c(nbtTagCompound); // Init nbtTagCompound

        nbtTagCompound.setBoolean("NoGravity", true); // Set NoGravity
        nbtTagCompound.setBoolean("Marker", true); // Set Marker to true
        nbtTagCompound.setBoolean("Invulnerable", true); // Set Invulnerable
        nbtTagCompound.setBoolean("Invisible", true); // Set Invisible
        nbtTagCompound.setInt("DisabledSlots", 31); // Set DisabledSlots
        nbtTagCompound.setBoolean("CustomNameVisible", true); // Set CustomNameVisible
        nbtTagCompound.setString("CustomName", type.getName()); // Set CustomName
        nbtTagCompound.setBoolean("Small", true); // Set Small

        armorStand.f(nbtTagCompound); // Set nbtTagCompound
    }

    /**
     * Detect collides with entity human
     *
     * @param entityHuman collided human
     */
    @Override
    public void d(EntityHuman entityHuman) {

        Player player = (Player) entityHuman.getBukkitEntity();
        Status status = gameManager.getStatus();
        List<PlayerPacMan> playerPacManList = gameManager.getPlayerPacManList();

        if (status.equals(Status.IN_GAME) && !player.getGameMode().equals(GameMode.SPECTATOR) && this.isAlive()) {

            double distanceAtCoin = this.getBukkitEntity().getLocation().distance(player.getLocation()); // Calculate distance
            PlayerPacMan playerPacMan = gameManager.getPlayer(player.getUniqueId());

            if (distanceAtCoin <= 2 && playerPacMan.getActiveBooster() == null) {
                die();

                if (type.equals(BoosterTypes.PLAYER_SWAP)) {

                    List<PlayerPacMan> swappablePlayers = playerPacManList.stream().filter(playerFilter -> !playerFilter.equals(playerPacMan) && playerFilter.getPlayerIfOnline() != null).collect(Collectors.toList());

                    if (swappablePlayers.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "Il n'y pas de joueur avec qui swap !");
                    } else {

                        PlayerPacMan playerSwapPacMan = swappablePlayers.get(RandomUtils.nextInt(swappablePlayers.size())); // Get random player

                        Player playerSwap = playerSwapPacMan.getPlayerIfOnline();

                        Location locationOfPlayerSwap = playerSwap.getLocation();
                        Location locationOfPlayer = player.getLocation();

                        player.teleport(locationOfPlayerSwap);
                        playerSwap.teleport(locationOfPlayer);

                        playerSwap.sendMessage(ChatColor.GREEN + "Vous avez été swap avec un autre joueur");
                        player.sendMessage(ChatColor.GREEN + "Vous avez été swap avec un autre joueur");
                    }

                } else {
                    playerPacMan.setActiveBooster(type);
                    playerPacMan.setBoosterRemainingTime(type.getBoosterTime());
                }
            }
        }
    }

    /**
     * Kill booster and set not
     * spawned in boosterLocation map
     * of gameManager
     */
    @Override
    public void die() {

        super.die(); // Kill booster
        armorStand.die(); // Kill armor stand

        Location location = getBukkitEntity().getLocation();
        location.setY(71);

        gameManager.getBoosterLocations().put(location, false); // Put booster spawned false in booster location map
    }

    /**
     * Enum of available type for Booster entity
     *
     * @author Azuxul
     * @version 1.0
     * @see fr.azuxul.pacman.entity.Booster
     */
    public enum BoosterTypes {

        SPEED("Speed", Material.FEATHER, 20),
        PLAYER_SWAP("Swap", Material.ENDER_PEARL, 0),
        DOUBLE_COINS("Double coins", Material.GOLD_BLOCK, 15),
        COINS_MAGNET("Coin magnet", Material.EYE_OF_ENDER, 15);

        private final String name;
        private final Material material;
        private final int boosterTime;

        BoosterTypes(String name, Material material, int boosterTime) {

            this.name = name;
            this.material = material;
            this.boosterTime = boosterTime;
        }

        /**
         * Get name of booster type
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * Get item material of booster type
         *
         * @return item material
         */
        public Material getMaterial() {
            return material;
        }

        /**
         * Get booster time duration
         *
         * @return boosterTime
         */
        public int getBoosterTime() {
            return boosterTime;
        }
    }
}
