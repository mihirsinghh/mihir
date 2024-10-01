package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

public class World {
    private final int worldWidth = 70;
    private final int worldHeight = 40;
    private final int X_OFFSET = 0;
    private final int Y_OFFSET = 3;
    private final double VISIBILITY_RADIUS = 5.0;

    boolean activeFog;
    TERenderer ter;

    //array representation of this World instance
    private final TETile[][] world;
    private final TETile[][] worldCopy;

    public World(TERenderer ter) {
        activeFog = true;

        //creates new, blank window with given dimensions
        this.ter = ter;
        ter.initialize(worldWidth + X_OFFSET, worldHeight + Y_OFFSET, X_OFFSET, Y_OFFSET);

        //initialize the world with empty tiles
        this.world = new TETile[worldWidth][worldHeight];
        worldCopy = new TETile[worldWidth][worldHeight];
        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                world[x][y] = Tileset.NOTHING;
                worldCopy[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    public TETile[][] getWorldCopy() {
        return worldCopy;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getXOffset() {
        return X_OFFSET;
    }

    public int getYOffset() {
        return Y_OFFSET;
    }

    public double getVisibilityRadius() {
        return VISIBILITY_RADIUS;
    }

    public void resetWorldCopy(TETile[][] arrayWorld) {
        for (int y = 0; y < worldHeight; y++) {
            for (int x = 0; x < worldWidth; x++) {
                worldCopy[x][y] = Tileset.NOTHING;
            }
        }
    }

}
