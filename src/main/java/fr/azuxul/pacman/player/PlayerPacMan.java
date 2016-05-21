package fr.azuxul.pacman.player;

import fr.azuxul.pacman.PacMan;
import fr.azuxul.pacman.powerup.PowerupEffectType;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.scoreboards.ObjectiveSign;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

/**
 * PlayerPacMan
 *
 * @author Azuxul
 * @version 1.0
 */
public class PlayerPacMan extends GamePlayer implements Comparable<PlayerPacMan> {

    private int gomme;
    private int totalGomme;
    private int boosterRemainingTime;
    private int invulnerableRemainingTime;
    private int portalTicks;
    private int kills;
    private PowerupEffectType activeBooster;
    private ObjectiveSign objectiveSign;

    public PlayerPacMan(Player player) {

        super(player);
        boosterRemainingTime = -1;
        invulnerableRemainingTime = -1;
        gomme = 0;
        totalGomme = 0;
        kills = 0;
        objectiveSign = null;
    }

    public void addTotalGomme(int gommeAdd) {
        totalGomme += gommeAdd;
    }

    public int getTotalGomme() {
        return totalGomme;
    }

    public ObjectiveSign getObjectiveSign() {
        return objectiveSign;
    }

    public void setObjectiveSign(ObjectiveSign objectiveSign) {
        this.objectiveSign = objectiveSign;
    }

    /**
     * Get kills number
     *
     * @return kills
     */
    public int getKills() {
        return kills;
    }

    /**
     * Set kills number
     *
     * @param kills number
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Get gomme number of playerPacMan
     *
     * @return gomme
     */
    public int getGomme() {
        return gomme;
    }

    /**
     * Set gomme number of playerPacMan
     *
     * @param gomme value of gameCoins number
     */
    public void setGomme(int gomme) {
        this.gomme = gomme;
    }

    /**
     * Get active booster of player
     *
     * @return activeBooster
     */
    public PowerupEffectType getActiveBooster() {
        return activeBooster;
    }

    /**
     * Set active booster to player
     *
     * @param activeBooster PowerupEffectType
     */
    public void setActiveBooster(PowerupEffectType activeBooster) {
        this.activeBooster = activeBooster;
    }

    /**
     * Get remaining time of active booster
     * If not active booster return -1
     *
     * @return boosterRemainingTime
     */
    public int getBoosterRemainingTime() {
        return boosterRemainingTime;
    }

    /**
     * Set remaining time of active booster
     *
     * @param boosterRemainingTime new boosterRemainingTime
     */
    public void setBoosterRemainingTime(int boosterRemainingTime) {
        this.boosterRemainingTime = boosterRemainingTime;
    }

    /**
     * Set remaining time of invulnerability to damages
     */
    public void setInvulnerableTime(int time) {
        this.invulnerableRemainingTime = time;
    }

    /**
     * Get remaining time of invulnerability to damages
     * If is not invulnerable return -1
     *
     * @return invulnerableRemainingTime
     */
    public int getInvulnerableRemainingTime() {
        return invulnerableRemainingTime;
    }

    /**
     * Get remaining before player can't use portals
     *
     * @return portalTick
     */
    public int getPortalTicks() {
        return portalTicks;
    }

    /**
     * Set portal ticks
     *
     * @param portalTicks new portalTicks
     */
    public void setPortalTicks(int portalTicks) {
        this.portalTicks = portalTicks;
    }

    /**
     * Update player stats (active effects)
     * 1 update/s
     */
    public void update() {

        Player player = getPlayerIfOnline();

        if (boosterRemainingTime >= 0 && activeBooster != null) {

            boosterRemainingTime--;
            player.setLevel(boosterRemainingTime);
            if (activeBooster.equals(PowerupEffectType.SPEED))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1), true);
            else if (activeBooster.equals(PowerupEffectType.JUMP_BOOST))
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, 2), true);
            if (boosterRemainingTime < 0)
                activeBooster = null;
        }

        if (invulnerableRemainingTime >= 0 && player != null) {
            invulnerableRemainingTime--;
            getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 0, true, true), true);
        }

        if (portalTicks > 0)
            portalTicks--;
    }

    public void damage() {
        for (int i = RandomUtils.nextInt(3); i >= 1 && gomme > 0; i--) {

            gomme--; // Decrement coins of player

            Location location = getPlayerIfOnline().getLocation();

            // Spawn coin
            PacMan.getGameManager().getGommeManager().spawnGomme(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY() + 1.1, location.getZ(), true);

            setGomme(gomme); // Set player coins
        }
    }

    @Override
    public boolean equals(Object compareObject) {
        if (this == compareObject)
            return true;

        if (compareObject == null || getClass() != compareObject.getClass())
            return false;

        PlayerPacMan that = (PlayerPacMan) compareObject;

        return getUUID() == that.getUUID();
    }

    @Override
    public int hashCode() {
        return getUUID() != null ? getUUID().hashCode() : 0;
    }

    @Override
    public int compareTo(@Nullable PlayerPacMan comparePlayerPacMan) {

        if (comparePlayerPacMan == null) {
            throw new NullPointerException("The compared object can not be null");
        } else if (comparePlayerPacMan.getGomme() == this.getGomme()) {
            return 0;
        } else if (comparePlayerPacMan.getGomme() > this.getGomme()) {
            return -1;
        } else {
            return 1;
        }
    }
}
