package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {

    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3, false);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4, false);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5, false);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6, false);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7, false);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8, false);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9, false);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10, false);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11, false);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12, false);

    //the ones I'm using
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", 0, false);
    public static final TETile ENEMY = new TETile('>', Color.white, Color.black, "enemy", 17, false);

    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", 1, false);
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black, "floor", 2, false);
    public static final TETile ROOM = new TETile('·', new Color(128, 192, 128), Color.black, "room", 14, false);
    public static final TETile HALLWAY = new TETile('·', new Color(128, 192, 128), Color.black, "hallway", 15, false);

    public static final TETile PORTAL = new TETile('▢', Color.red, Color.black,
            "portal", 16, false);
    public static final TETile TELEPORTER = new TETile('▢', Color.blue, Color.black,
            "teleporter", 18, false);

}


