package fr.azuxul.pacman.entity;

import fr.azuxul.pacman.GameManager;
import fr.azuxul.pacman.PacMan;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Coin entity
 *
 * @author Azuxul
 * @version 1.0
 */
public class Coin extends EntityArmorStand {

    boolean droopedByPlayer;

    /**
     * Class constructor and
     * spawn coin
     *
     * @param world       world of entity
     * @param coinDrooped if true, coins was remove after 30s,
     *                    the coin not subtract of global coin number
     *                    when is catch and spawn effect is special
     */
    public Coin(World world, double x, double y, double z, boolean coinDrooped) {
        super(world);

        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        Vector velocity = null;
        droopedByPlayer = coinDrooped;

        c(nbtTagCompound); // Init nbtTagCompound

        nbtTagCompound.setBoolean("Small", true); // Set Small
        nbtTagCompound.setBoolean("NoGravity", !coinDrooped); // Set NoGravity
        nbtTagCompound.setBoolean("Invulnerable", true); // Set Invulnerable
        nbtTagCompound.setBoolean("Invisible", true); // Set Invulnerable
        nbtTagCompound.setInt("DisabledSlots", 31); // Disable slots

        f(nbtTagCompound); // Set nbtTagCompound

        setEquipment(4, CraftItemStack.asNMSCopy(new ItemStack(Material.GOLD_BLOCK))); // Set helmet

        if (coinDrooped) { // If is drooped by player

            GameManager gameManager = PacMan.getGameManager();

            // Run task after 600 ticks (30s)
            gameManager.getServer().getScheduler().runTaskLater(gameManager.getPlugin(), () -> {

                this.getBukkitEntity().remove(); // Kill coin
            }, 600L);

            // Get randomly velocity
            velocity = new Vector(1 + RandomUtils.nextInt(4), 3 + RandomUtils.nextInt(4), 1 + RandomUtils.nextInt(3)).multiply(RandomUtils.nextBoolean() ? 0.1 : -0.1);
        }

        spawn(world, x, y, z, velocity); // Spawn
    }

    /**
     * Spawn this coin
     *
     * @param world    world to spawn
     * @param x        x location
     * @param y        y location
     * @param z        z location
     * @param velocity velocity
     */
    private void spawn(World world, double x, double y, double z, Vector velocity) {

        this.setPosition(x, y, z); // Set location
        world.addEntity(this); // Spawn

        if (velocity != null)
            this.getBukkitEntity().setVelocity(velocity); // Set velocity
    }

    /**
     * Get if coin was drooped by player
     *
     * @return droopedByPlayer
     */
    public boolean isDroopedByPlayer() {

        return droopedByPlayer;
    }
}
