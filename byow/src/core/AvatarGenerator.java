package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class AvatarGenerator {
    TETile[][] world;
    World realWorld;
    Position pos;
    Avatar avatar;
    Random random;

    //int spawnX;
    //int spawnY;

    public AvatarGenerator(Position pos) {
        this.world = pos.world;
        this.realWorld = pos.realWorld;
        this.random = pos.random;
        this.pos = pos;
    }

    /*
    Chooses a valid spawn point for the avatar
     */
    public void chooseSpawn() {
        boolean invalid = true;

        while (invalid) {
            avatar = new Avatar(pos);
            avatar.spawnX = avatar.pickSpawnX();
            avatar.spawnY = avatar.pickSpawnY();

            if (this.world[avatar.spawnX][avatar.spawnY] == Tileset.ROOM) {
                invalid = false;
            }
        }
    }

    /*
    Spawns the avatar
     */
    public void spawn() {
        chooseSpawn();
        world[avatar.spawnX][avatar.spawnY] = Tileset.AVATAR;
        avatar.currentX = avatar.spawnX;
        avatar.currentY = avatar.spawnY;
    }

    /*
    Loads the avatar into its saved position
     */
    public void load(int x, int y) {
        avatar = new Avatar(pos);
        avatar.spawnX = x;
        avatar.spawnY = y;
        world[x][y] = Tileset.AVATAR;
        avatar.currentX = x;
        avatar.currentY = y;
    }
}
