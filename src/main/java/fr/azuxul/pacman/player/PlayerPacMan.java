package fr.azuxul.pacman.player;

import fr.azuxul.pacman.entity.Booster;
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
    private Booster.BoosterTypes activeBooster;

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
    public Booster.BoosterTypes getActiveBooster() {
        return activeBooster;
    }

    /**
     * Set active booster to player
     *
     * @param activeBooster new active booster
     */
    public void setActiveBooster(Booster.BoosterTypes activeBooster) {
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

    public void setInvulnerableRemainingTime(int invulnerableRemainingTime) {
        this.invulnerableRemainingTime = invulnerableRemainingTime;
    }

    /**
     * Update player stats (active effects)
     * 1 update/s
     */
    public void update() {

        Player player = getPlayerIfOnline();

        if (boosterRemainingTime >= 0 && player != null) {

            boosterRemainingTime--;
            if (activeBooster.equals(Booster.BoosterTypes.SPEED))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 39, 0));
            if (boosterRemainingTime < 0)
                activeBooster = null;
        }

        if (invulnerableRemainingTime >= 0 && player != null) {

            boolean invisibleEffect = false;

            invulnerableRemainingTime--;
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                    invisibleEffect = true;
                    break;
                }

            if (!invisibleEffect) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10, 0, true, true));
            }
        }
    }

    @Override
    public boolean equals(Object compareObject) {
        if (this == compareObject) return true;
        if (compareObject == null || getClass() != compareObject.getClass()) return false;

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
        }
        if (comparePlayerPacMan.getGameCoins() == this.getGameCoins()) {
            return 0;
        } else if (comparePlayerPacMan.getGameCoins() > this.getGameCoins()) {
            return -1;
        } else {
            return 1;
        }
    }
}
