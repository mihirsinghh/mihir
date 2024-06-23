package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;


public class AGWorld {
    long seed;
    int width;
    int height;

    //array representation of this World instance
    private final TETile[][] world;
    private final int WIDTH = 70;
    private final int HEIGHT = 40;
    TERenderer ter;

    public AGWorld(Random random) {
        //initialize world parameters
        this.width = WIDTH;
        this.height = HEIGHT;

        //initialize a new tile-rendering engine with the given dimensions. This will create a new window (empty world)
        ter = new TERenderer();
        ter.initialize(width, height);

        //initialize the world with empty tiles
        this.world = new TETile[width][height];
        for (int y = height - 1; y > -1; y--) {
            for (int x = 0; x < width; x++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /*
    Ensures that changes to the world appear on-screen
     */
    public void renderWorld(TETile[][] arrayWorld) {
        ter.renderFrame(arrayWorld);
    }

    //returns the World object in 2D-array form
    public TETile[][] getWorld() {
        return world;
    }

}

