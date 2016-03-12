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
    private final List<Gomme> playerGommeList;
    private int remainingGlobalGommes;
    private int globalGommes;

    public GommeManager(GameManager gameManager) {

        this.gameManager = gameManager;
        this.gommeList = new ArrayList<>();
        this.playerGommeList = new ArrayList<>();
    }

    public Gomme getRandomNaturalGomme() {

        if (gommeList.isEmpty())
            return null;
        else if (gommeList.size() <= 1) {
            return gommeList.get(0);
        } else
            return gommeList.get(RandomUtils.nextInt(gommeList.size() - 1));
    }

    public void removeGomme(Gomme gomme) {

        if (gomme.isDroopedByPlayer())
            playerGommeList.remove(gomme);
        else
            gommeList.remove(gomme);
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

        Gomme gomme = new Gomme(world, x, y, z, dopedByPlayer);

        if (!dopedByPlayer)
            gommeList.add(gomme);
        else
            playerGommeList.add(gomme);
    }

    public void spawnBigGomme(World world, double x, double y, double z, boolean dopedByPlayer, int gommeValue) {

        playerGommeList.add(new Gomme(world, x, y, z, dopedByPlayer, gommeValue));
    }

    public void killAllGommes() {
        gommeList.forEach(Entity::die);
        playerGommeList.forEach(Entity::die);
    }
}
