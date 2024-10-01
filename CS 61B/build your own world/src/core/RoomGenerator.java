package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.*;
import java.util.Random;

public class RoomGenerator {
    TETile[][] world;
    World realWorld;
    Position pos;
    Random random;
    List<Room> existingRooms;
    PointToRoomMap pointToRoomMap; //maps each Point to a Room ID

    int maxNumRooms;
    int numRooms;
    private final int min = 8;
    private final int max = 13;

    public RoomGenerator(PointToRoomMap pointToRoomMap, Position pos) {
        this.world = pos.world;
        this.realWorld = pos.realWorld;
        this.random = pos.random;
        this.pointToRoomMap = pointToRoomMap;
        this.pos = pos;

        existingRooms = new ArrayList<>();

        numRooms = 0;
        maxNumRooms = RandomUtils.uniform(random, min, max);
    }

    /*
    Determines if the given coordinate position is a valid location for a new Room to be built
    Room is built from its top-left corner
     */
    public boolean isValidBuild(int x, int y, int width, int height) {

        if (pos.isValidCoord(x, y)) {
            int endPointX = x + width;
            int endPointY = y - height;

            //check room build
            for (int yPos = y; yPos > endPointY; yPos--) {
                for (int xPos = x; xPos < endPointX; xPos++) {
                    if (!pos.isValidCoord(xPos, yPos) || pos.tileExists(xPos, yPos)) {
                        return false;
                    }
                }
            }

            //check north wall space
            if (pos.isValidCoord(x, y + 1)) {
                for (int xPos = x; xPos < endPointX; xPos++) {
                    if (!pos.isValidCoord(xPos, y + 1) || pos.tileExists(xPos, y + 1)) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            //check south wall space
            if (pos.isValidCoord(x, endPointY)) {
                for (int xPos = x; xPos < endPointX; xPos++) {
                    if (!pos.isValidCoord(xPos, endPointY) || pos.tileExists(xPos, endPointY)) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            //check east wall space
            if (pos.isValidCoord(endPointX + 1, y)) {
                for (int yPos = y; yPos > endPointY; yPos--) {
                    if (!pos.isValidCoord(x + 1, yPos) || pos.tileExists(endPointX + 1, yPos)) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            //check west wall space
            if (pos.isValidCoord(x - 1, y)) {
                for (int yPos = y; yPos > endPointY; yPos--) {
                    if (!pos.isValidCoord(x - 1, yPos) || pos.tileExists(x - 1, yPos)) {
                        return false;
                    }
                }
            } else {
                return false;
            }

        } else {
            return false;
        }
        return true;
    }

    /*
    Creates a new Room in the world
     */
    public void createRoom() {
        boolean invalidRoom = true;
        int attempts = 0;

        while (invalidRoom) {
            Room newRoom = new Room(world, realWorld, this.random);
            int cornerX = newRoom.pickCornerX();
            int cornerY = newRoom.pickCornerY();
            int roomWidth = newRoom.pickWidth();
            int roomHeight = newRoom.pickHeight();

            if (isValidBuild(cornerX, cornerY, roomWidth, roomHeight)) {
                invalidRoom = false;
                int endPointX = cornerX + roomWidth;
                int endPointY = cornerY - roomHeight;
                newRoom.id = numRooms; //each Room id is its build order value

                //constructs the Room and maps each point within the Room to the Room's id
                for (int yPos = cornerY; yPos > endPointY; yPos--) {
                    for (int xPos = cornerX; xPos < endPointX; xPos++) {
                        this.world[xPos][yPos] = Tileset.ROOM;
                        pointToRoomMap.addTile(xPos, yPos, newRoom.id);
                    }
                }
                //build surrounding walls
                buildWalls(newRoom);

                numRooms += 1;
                existingRooms.add(newRoom);

            } else {
                attempts += 1;
                if (attempts >= pos.realWorld.getWorldWidth() * pos.realWorld.getWorldHeight()) {
                    if (maxNumRooms >= min) {
                        maxNumRooms -= 1;
                    }
                    break;
                }
            }
        }
    }


    /*
    Encloses the given Room with walls
     */
    public void buildWalls(Room newRoom) {
        int cornerX = newRoom.cornerX;
        int cornerY = newRoom.cornerY;
        int roomWidth = newRoom.roomWidth;
        int roomHeight = newRoom.roomHeight;

        int endPointX = cornerX + roomWidth;
        int endPointY = cornerY - roomHeight;

        //build top-edge wall
        for (int xPos = cornerX; xPos < endPointX; xPos++) {
            this.world[xPos][cornerY + 1] = Tileset.WALL;
        }


        //build right-edge wall
        for (int yPos = cornerY; yPos > endPointY; yPos--) {
            this.world[endPointX][yPos] = Tileset.WALL;
        }


        //build left-edge wall
        for (int yPos = cornerY; yPos > endPointY; yPos--) {
            this.world[cornerX - 1][yPos] = Tileset.WALL;
        }


        //build bottom-edge wall
        for (int xPos = cornerX; xPos < endPointX; xPos++) {
            this.world[xPos][endPointY] = Tileset.WALL;
        }
    }

    /*
    Fills the world with Rooms
     */
    public void drawRooms() {
        while (numRooms < maxNumRooms) {
            createRoom();
        }
    }
}

