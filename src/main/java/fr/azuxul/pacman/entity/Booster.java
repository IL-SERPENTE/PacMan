package fr.azuxul.pacman.entity;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

/**
 * Class description
 *
 * @author Azuxul
 */
public class Booster extends EntityItem {

    private EntityArmorStand armorStand;
    private BoosterTypes type;
    private GameManager gameManager;

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

        y += 1.5d;
        this.type = type;
        ItemStack itemStack = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(type.getMaterial()));
        armorStand = new EntityArmorStand(world, x, y, z);
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        gameManager = PacMan.getGameManager();

        initArmorStand(armorStand);

        setItemStack(itemStack);

        b(nbtTagCompound); // Init nbtTagCompound

        nbtTagCompound.setBoolean("Invulnerable", true); // Set Invulnerable
        nbtTagCompound.setInt("Age", -32768); // Set Age
        nbtTagCompound.setInt("PickupDelay", 32767); // Set CustomName

        a(nbtTagCompound); // Set nbtTagCompound

        spawn(world, x, y, z); // Spawn
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

        this.die();
    }

    /**
     * Kill booster and set not
     * spawned in boosterLocation map
     * of gameManager
     */
    @Override
    public void die() {

        super.die();
        armorStand.die();

        Location location = getBukkitEntity().getLocation();
        location.setY(71);

        gameManager.getBoosterLocations().put(location, false);
    }

    /**
     * Enum of available type for Booster entity
     *
     * @author Azuxul
     * @version 1.0
     * @see fr.azuxul.pacman.entity.Booster
     */
    public enum BoosterTypes {

        SPEED("Speed", Material.FEATHER),
        DOUBLE_COINS("Double coins", Material.GOLD_BLOCK),
        COINS_MAGNET("Coin magnet", Material.ENDER_PEARL);

        private String name;
        private Material material;

        BoosterTypes(String name, Material material) {

            this.name = name;
            this.material = material;
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
    }
}
