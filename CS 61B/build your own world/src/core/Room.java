package core;

import tileengine.TETile;
import utils.RandomUtils;

import java.util.Random;


public class Room {
    Position pos;
    TETile[][] world;
    World realWorld;
    Random random;

    //size requirements
    int maxWidth;
    int maxHeight;
    int minWidth;
    int minHeight;

    //room dimensions
    int roomHeight;
    int roomWidth;
    int cornerX; //x-coordinate of top-left corner
    int cornerY; //y-coordinate of top-left corner
    int id;
    String type;


    public Room(TETile[][] world, World realWorld, Random random) {
        this.world = world;
        this.realWorld = realWorld;

        pos = new Position(world, realWorld, random);

        this.random = random;

        maxWidth = realWorld.getWorldWidth() / 5;
        maxHeight = realWorld.getWorldHeight() / 4;
        minWidth = 8;
        minHeight = 8;

        type = "room";
    }

    /*
    returns valid top-left corner x-coordinate
    */
    public int pickCornerX() {
        cornerX = pos.randomX(0, pos.maxXIndex - minWidth);
        return cornerX;
    }

    /*
    returns valid top-left corner y-coordinate
    */
    public int pickCornerY() {
        cornerY = pos.randomY(minHeight, pos.maxYIndex);
        return cornerY;
    }


    /*
    returns a valid width value for a Room given its corner-x value
     */
    public int pickWidth() {
        boolean invalidWidth = true;

        while (invalidWidth) {
            roomWidth = RandomUtils.uniform(random, minWidth, maxWidth + 1);
            if (cornerX + roomWidth <= pos.maxXIndex) {
                invalidWidth = false;
            }
        }
        return roomWidth;
    }


    /*
    returns a height value for a Room
     */
    public int pickHeight() {
        boolean invalidHeight = true;

        while (invalidHeight) {
            roomHeight = RandomUtils.uniform(random, minHeight, maxHeight + 1);
            if (cornerY - roomHeight >= 0) {
                invalidHeight = false;
            }
        }
        return roomHeight;
    }
}

