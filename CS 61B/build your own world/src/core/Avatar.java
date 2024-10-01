package core;

import java.util.Random;

public class Avatar {
    boolean foundPortal;
    boolean alive;
    boolean posChanged;
    int spawnX;
    int spawnY;
    int currentX;
    int currentY;
    Random random;
    Position pos;
    World realWorld;

    public Avatar(Position pos) {
        foundPortal = false;
        alive = true;
        this.pos = pos;
        this.realWorld = pos.realWorld;
    }

    public int pickSpawnX() {
        spawnX = pos.randomX(0, pos.maxXIndex);
        return spawnX;
    }

    public int pickSpawnY() {
        spawnY = pos.randomY(0, pos.maxYIndex);
        return spawnY;
    }
    // Method to get the current x-coordinate of the avatar
    public int getCurrentX() {
        return currentX;
    }

    // Method to get the current y-coordinate of the avatar
    public int getCurrentY() {
        return currentY;
    }

}
