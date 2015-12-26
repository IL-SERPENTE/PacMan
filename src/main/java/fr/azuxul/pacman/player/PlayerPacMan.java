package fr.azuxul.pacman.player;

import fr.azuxul.pacman.powerup.PowerupEffectType;
import net.samagames.api.games.GamePlayer;
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

    private int gameCoins, boosterRemainingTime, invulnerableRemainingTime;
    private PowerupEffectType activeBooster;

    public PlayerPacMan(Player player) {

        super(player);
        boosterRemainingTime = -1;
        invulnerableRemainingTime = -1;
        gameCoins = 0;
    }

    /**
     * Get gameCoins number of playerPacMan
     *
     * @return gameCoins
     */
    public int getGameCoins() {
        return gameCoins;
    }

    /**
     * Set gameCoins number of playerPacMan
     *
     * @param gameCoins value of gameCoins number
     */
    public void setGameCoins(int gameCoins) {
        this.gameCoins = gameCoins;
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
            getPlayerIfOnline().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, true, true), true);
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
        } else if (comparePlayerPacMan.getGameCoins() == this.getGameCoins()) {
            return 0;
        } else if (comparePlayerPacMan.getGameCoins() > this.getGameCoins()) {
            return -1;
        } else {
            return 1;
        }
    }
}
