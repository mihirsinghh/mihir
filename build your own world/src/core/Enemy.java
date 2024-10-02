package core;


public class Enemy {
    int spawnX;
    int spawnY;
    int currentX;
    int currentY;

    boolean foundAvatar;

    Position pos;
    World realWorld;

    public Enemy(Position pos) {
        this.pos = pos;
        this.realWorld = pos.realWorld;
        foundAvatar = false;
    }

    public int pickSpawnX(){
        spawnX = pos.randomX(0,pos.maxXIndex);
        return spawnX;
    }

    public int pickSpawnY(){
        spawnY = pos.randomY(0,pos.maxYIndex);
        return spawnY;
    }
}
