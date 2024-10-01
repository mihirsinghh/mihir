package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class PortalGenerator {
    TETile[][] world;
    World realWorld;
    Portal portal;
    Avatar avatar;
    Position pos;
    Random random;
    PointToRoomMap pointToRoomMap;

    int portalX;
    int portalY;

    public PortalGenerator(Avatar avatar, Position pos, PointToRoomMap pointToRoomMap) {
        this.pos = pos;
        this.world = pos.world;
        this.random = pos.random;
        this.avatar = avatar;
        this.pointToRoomMap = pointToRoomMap;
    }

    /*
    Creates a portal in the world
     */
    public void createPortal() {
        chooseCoords();
        world[portalX][portalY] = Tileset.PORTAL;
    }

    /*
    Chooses a valid set of coordinates for the Portal
     */
    public void chooseCoords() {
        boolean invalid = true;

        while (invalid) {
            portal = new Portal(pos, this.avatar);
            portalX = portal.chooseX();
            portalY = portal.chooseY();

            if (this.world[portalX][portalY] == Tileset.ROOM) {
                int thisLocationsRoomID = pointToRoomMap.getID(portalX, portalY);
                int avatarSpawnRoomID = pointToRoomMap.getID(avatar.spawnX, avatar.spawnY);

                if (thisLocationsRoomID != avatarSpawnRoomID) {
                    invalid = false;
                }
            }

        }
    }

}

