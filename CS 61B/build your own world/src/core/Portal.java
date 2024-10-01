package core;

import java.util.Random;

public class Portal {
    int x;
    int y;
    Random random;
    Position pos;
    Avatar avatar;

    public Portal(Position pos, Avatar avatar) {
        this.pos = pos;
        this.avatar = avatar;
    }

    public int chooseX() {
        x = this.pos.randomX(0, pos.maxXIndex);
        return x;
    }

    public int chooseY() {
        y = this.pos.randomY(0, pos.maxYIndex);
        return y;
    }

    /*
    Returns the distance between Portal and the Avatar
     */
    public double distToAvatar() {
        return pos.distBetween(x, y, this.avatar.spawnX, this.avatar.spawnY);
    }

}

