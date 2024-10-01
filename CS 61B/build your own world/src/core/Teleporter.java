package core;

import tileengine.Tileset;

public class Teleporter {
    Position pos;
    int x;
    int y;

    public Teleporter(Position pos) {
        this.pos = pos;
        chooseLocation();
    }

    public void create() {
        pos.world[x][y] = Tileset.TELEPORTER;
    }

    public void chooseLocation() {
        boolean invalidLocation = true;

        while (invalidLocation) {
            x = pos.randomX(0, pos.maxXIndex);
            y = pos.randomY(0, pos.maxYIndex);

            if (!pos.isValidCoord(x, y)) {
                continue;
            }

            if (pos.world[x][y] == Tileset.ROOM && nextToWall(x, y)) {
                invalidLocation = false;
            }
        }
    }

    public boolean nextToWall(int x, int y) {
        //check top-side
        if (pos.isValidCoord(x, y + 1) && pos.world[x][y + 1].equals(Tileset.WALL)) {
            return true;
        }
        //check bottom-side
        if (pos.isValidCoord(x, y - 1) && pos.world[x][y - 1].equals(Tileset.WALL)) {
            return true;
        }
        //check right-side
        if (pos.isValidCoord(x + 1, y) && pos.world[x + 1][y].equals(Tileset.WALL)) {
            return true;
        }
        //check left-side
        if (pos.isValidCoord(x - 1, y) && pos.world[x - 1][y].equals(Tileset.WALL)) {
            return true;
        }

        return false;
    }
}
