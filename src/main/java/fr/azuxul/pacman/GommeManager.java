package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Gomme;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.games.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Gomme manager
 *
 * @author Azuxul
 * @version 1.0
 */
public class GommeManager {

    private final GameManager gameManager;
    private final List<Gomme> gommeList;
    private int remainingGlobalCoins;
    private int globalCoins;

    public GommeManager(GameManager gameManager) {

        this.gameManager = gameManager;
        this.gommeList = new ArrayList<>();
    }

    /**
     * Get number of global coins remaining
     *
     * @return remainingGlobalCoins
     */
    public int getRemainingGlobalCoins() {
        return remainingGlobalCoins;
    }

    /**
     * Set number of global coins remaining
     *
     * @param remainingGlobalCoins global coins remaining
     */
    public void setRemainingGlobalCoins(int remainingGlobalCoins) {

        Status status = gameManager.getStatus();

        this.remainingGlobalCoins = remainingGlobalCoins;

        // If remaining coins is equals to 0 and is not end
        if (remainingGlobalCoins <= 0 && !status.equals(Status.FINISHED)) {

            gameManager.getServer().getOnlinePlayers().forEach(gameManager.getScoreboard()::sendScoreboardToPlayer); // Update scoreboard
            gameManager.end(); // End
        }
    }

    public int getGlobalCoins() {
        return globalCoins;
    }

    /**
     * Set number of global coins
     *
     * @param globalCoins global coins
     */
    public void setGlobalCoins(int globalCoins) {
        this.remainingGlobalCoins = globalCoins;
        this.globalCoins = globalCoins;
    }

    public void spawnCoin(World world, double x, double y, double z, boolean dopedByPlayer) {

        gommeList.add(new Gomme(world, x, y, z, dopedByPlayer));
    }

    public void spawnBigCoin(World world, double x, double y, double z, boolean dopedByPlayer, int coinValue) {

        gommeList.add(new Gomme(world, x, y, z, dopedByPlayer, coinValue));
    }

    public void killAllCoin() {
        gommeList.forEach(Entity::die);
    }
}
