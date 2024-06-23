package core;

import tileengine.TETile;
import utils.RandomUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Position {
    TETile[][] world;
    World realWorld;
    Random random;

    //max possible x and y coordinate values
    int maxXIndex;
    int maxYIndex;


    public Position(TETile[][] world, World realWorld, Random random) {
        maxXIndex = realWorld.getWorldWidth() - 1;
        maxYIndex = realWorld.getWorldHeight() - 1;
        this.world = world;
        this.realWorld = realWorld;
        this.random = random;
    }


    public boolean isValidCoord(int x, int y) {
        return x >= 0 && x <= maxXIndex && y >= 0 && y <= maxYIndex;
    }


    /*
    Returns true if a non-NOTHING Tile exists at this position
     */
    public boolean tileExists(int x, int y) {
        if (isValidCoord(x, y)) {
            return !getTile(x, y).description().equals("nothing");

        } else {
            throw new IllegalArgumentException("isValidCoord() failed from tileExists()");
        }
    }


    /*
    Returns the tile at the given coordinate position. world[0][0] represents the bottom-left tile in the world
     */
    public TETile getTile(int x, int y) {
        if (isValidCoord(x, y)) {
            return world[x][y];
        }
        throw new IllegalArgumentException("isValidCoord() failed from getTile()");
    }

    /*
    Returns the distance between two sets of coordinates
     */
    public double distBetween(int x1, int y1, int x2, int y2) {
        int yDist = y2 - y1;
        int xDist = x2 - x1;

        double yDistSquared = Math.pow(yDist, 2);
        double xDistSquared = Math.pow(xDist, 2);

        return Math.sqrt(yDistSquared + xDistSquared);
    }


    /*
    Returns a set of all Points that are within the world's visibility radius from the current position
     */
    public Set<Point> withinRadius(int avatarX, int avatarY) {
        Set<Point> withinRadius = new HashSet<>();
        double radius = realWorld.getVisibilityRadius();

        for (int y = 0; y <= maxYIndex; y++) {
            for (int x = 0; x <= maxXIndex; x++) {
                if (distBetween(avatarX, avatarY, x, y) <= radius) {
                    withinRadius.add(new Point(x, y));
                }
            }
        }
        return withinRadius;
    }

    /*
    Returns a random value along x-axis in range [bound1, bound2]
     */
    public int randomX(int index1, int index2) {
        if (index1 < 0 || index2 < 0 || index1 > this.maxXIndex || index2 > this.maxXIndex) {
            throw new IllegalArgumentException("at least one of these bounds is not within the width of the world");
        }

        /*
        the uniform method returns a random value in range [bound1, bound2), so the order of arguments to the method
        matters, as the larger value must be the second argument
         */
        if (index1 == index2) {
            return index2;
        } else if (index1 > index2) {
            return RandomUtils.uniform(this.random, index2, index1 + 1);
        } else {
            return RandomUtils.uniform(this.random, index1, index2 + 1);
        }
    }


    /*
    Returns a random value along y-axis within the given bounds in range [bound1, bound2]
     */
    public int randomY(int index1, int index2) {
        if (index1 < 0 || index2 < 0 || index1 > this.maxYIndex || index2 > this.maxYIndex) {
            throw new IllegalArgumentException("at least one of these bounds is not within the height of the world");
        }

        /*
        the uniform method returns a random value in range [bound1, bound2), so the order of arguments to the method
        matters, as the larger value must be the second argument
         */
        if (index1 == index2) {
            return index1;
        } else if (index1 > index2) {
            return RandomUtils.uniform(this.random, index2, index1 + 1);
        } else {
            return RandomUtils.uniform(this.random, index1, index2 + 1);
        }
    }

}
