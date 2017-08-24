package fr.azuxul.pacman;

import fr.azuxul.pacman.entity.Gomme;
import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.World;
import net.samagames.api.games.Status;

import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of PacMan.
 *
 * PacMan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacMan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PacMan.  If not, see <http://www.gnu.org/licenses/>.
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

    public List<Gomme> getGommeList() {
        return gommeList;
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
