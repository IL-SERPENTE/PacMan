package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Gomme;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.games.Status;
import org.apache.commons.lang.math.RandomUtils;

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
    private int remainingGlobalGommes;
    private int globalGommes;

    public GommeManager(GameManager gameManager) {

        this.gameManager = gameManager;
        this.gommeList = new ArrayList<>();
    }

    public Gomme getRandomNaturalGomme() {

        Gomme result = null;

        for (int i = 3; i <= 0 || result == null; i--) {
            Gomme gomme = gommeList.get(RandomUtils.nextInt(gommeList.size() - 1));
            if (!gomme.isDroopedByPlayer()) {
                result = gomme;
            }
        }

        return result;
    }

    public List<Gomme> getGommeList() {
        return gommeList;
    }

    /**
     * Get number of global gommes remaining
     *
     * @return remainingGlobalGommes
     */
    public int getRemainingGlobalGommes() {
        return remainingGlobalGommes;
    }

    /**
     * Set number of global gommes remaining
     *
     * @param remainingGlobalGommes global gommes remaining
     */
    public void setRemainingGlobalGommes(int remainingGlobalGommes) {

        Status status = gameManager.getStatus();

        this.remainingGlobalGommes = remainingGlobalGommes;

        // If remaining gommes is equals to 0 and is not end
        if (remainingGlobalGommes <= 0 && !status.equals(Status.FINISHED)) {

            gameManager.getServer().getOnlinePlayers().forEach(gameManager.getScoreboard()::sendScoreboardToPlayer); // Update scoreboard
            gameManager.end(EndCause.GOMMES); // End
        }
    }

    public int getGlobalGommes() {
        return globalGommes;
    }

    /**
     * Set number of global gommes
     *
     * @param globalGommes global gommes
     */
    public void setGlobalGommes(int globalGommes) {
        this.remainingGlobalGommes = globalGommes;
        this.globalGommes = globalGommes;
    }

    public void spawnGomme(World world, double x, double y, double z, boolean dopedByPlayer) {

        gommeList.add(new Gomme(world, x, y, z, dopedByPlayer));
    }

    public void spawnBigGomme(World world, double x, double y, double z, boolean dopedByPlayer, int gommeValue) {

        gommeList.add(new Gomme(world, x, y, z, dopedByPlayer, gommeValue));
    }

    public void killAllGommes() {
        gommeList.forEach(Entity::die);
    }
}
