package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class EnemyGenerator {
    TETile[][] world;
    World realWorld;
    Avatar avatar;
    Position pos;
    Enemy enemy;
    Random random;
    PointToRoomMap pointToRoomMap;

    //int spawnX;
    //int spawnY;

    public EnemyGenerator(Position pos, Avatar avatar, PointToRoomMap pointToRoomMap) {
        this.world = pos.world;
        this.realWorld = pos.realWorld;
        this.random = pos.random;
        this.pos = pos;
        this.avatar = avatar;
        this.pointToRoomMap = pointToRoomMap;
    }

    /*
    Chooses a valid spawn-point for the enemy
    */
    public void chooseSpawn(){
        boolean invalid = true;

        while (invalid) {
            enemy = new Enemy(pos);
            enemy.spawnX = enemy.pickSpawnX();
            enemy.spawnY = enemy.pickSpawnY();

            if (world[enemy.spawnX][enemy.spawnY] == Tileset.ROOM) {
                int enemySpawnPointRoomID = pointToRoomMap.getID(enemy.spawnX, enemy.spawnY);
                int avatarSpawnPointRoomID = pointToRoomMap.getID(avatar.spawnX, avatar.spawnY);

                if (enemySpawnPointRoomID != avatarSpawnPointRoomID) {
                    invalid = false;
                }
            }
        }
    }

    /*
    Spawns the enemy
    */
    public void spawn() {
        chooseSpawn();
        world[enemy.spawnX][enemy.spawnY] = Tileset.ENEMY;
        enemy.currentX = enemy.spawnX;
        enemy.currentY = enemy.spawnY;
    }

    /*
    Loads the enemy into its saved position
     */
    public void load(int x, int y) {
        enemy = new Enemy(pos);
        enemy.spawnX = x;
        enemy.spawnY = y;
        world[x][y] = Tileset.ENEMY;
        enemy.currentX = x;
        enemy.currentY = y;
    }
}
