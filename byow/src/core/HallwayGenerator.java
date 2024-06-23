package core;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.List;
import java.util.Random;

public class HallwayGenerator {
    TETile[][] world;
    RoomGenerator roomGen;
    WeightedQuickUnionUF connectedRooms;
    PointToRoomMap pointToRoomMap;
    List<Room> allRooms;
    Position pos;
    Random random;

    public HallwayGenerator(RoomGenerator roomGen) {
        this.world = roomGen.world;
        this.roomGen = roomGen;

        connectedRooms = new WeightedQuickUnionUF(roomGen.numRooms);
        allRooms = roomGen.existingRooms;

        this.pointToRoomMap = roomGen.pointToRoomMap;
        this.random = roomGen.random;
        this.pos = roomGen.pos;
    }

    /*
    Connects the Rooms in the world
     */
    public void connectRooms() {
        for (Room eachRoom : allRooms) {
            connect(eachRoom);
        }
    }

    /*
    Attempts to connect this Room to any other Rooms in the world
     */
    public void connect(Room room) {
        buildFromPoint(room);
    }

    /*
    Attempts to find a point in the Room from where a hallway in either direction can be built and builds it if found
    If none found, no hallway is constructed from this Room
     */
    public void buildFromPoint(Room room) {
        boolean invalidPoint = true;
        int attempts = 0;

        while (invalidPoint) {
            int x = pos.randomX(room.cornerX, room.cornerX + room.roomWidth - 1);
            int y = pos.randomY(room.cornerY, room.cornerY - room.roomHeight + 1);

            if (validNorthHallway(x, y, room.id)) {
                buildNorthHallway(x, y, room.id);
                invalidPoint = false;

            } else if (validSouthHallway(x, y, room.id)) {
                buildSouthHallway(x, y, room.id);
                invalidPoint = false;

            } else if (validEastHallway(x, y, room.id)) {
                buildEastHallway(x, y, room.id);
                invalidPoint = false;

            } else if (validWestHallway(x, y, room.id)) {
                buildWestHallway(x, y, room.id);
                invalidPoint = false;
            }
            attempts += 1;
            if (attempts >= room.roomWidth * room.roomHeight) {
                break;
            }
        }
    }


    /*
    Draws hallway in the north direction
     */
    public void buildNorthHallway(int x, int y, int id) {
        for (int yPos = y; yPos <= pos.maxYIndex; yPos++) {

            TETile currentTile = pos.getTile(x, yPos);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, yPos);
                if (id != newRoomID) {
                    connectedRooms.union(id, newRoomID);
                    break;
                }

            } else {
                world[x][yPos] = Tileset.HALLWAY;
                if (!pos.tileExists(x + 1, yPos)) {
                    world[x + 1][yPos] = Tileset.WALL;
                }
                if (!pos.tileExists(x - 1, yPos)) {
                    world[x - 1][yPos] = Tileset.WALL;
                }
            }
        }
        buildNorthBranches(x, y, id);
    }

    /*
    Builds branching hallways from a hallway being built towards the north. These hallways will go east and west
     */
    public void buildNorthBranches(int x, int y, int id) {
        boolean noRoomFound = true;

        while (noRoomFound && y <= pos.maxYIndex) {
            if (pos.getTile(x, y).equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, y);
                if (id != newRoomID) {
                    noRoomFound = false;
                }

            } else if (pos.getTile(x, y).equals(Tileset.HALLWAY)) {
                if (validEastHallway(x, y, id)) {
                    buildEastHallway(x, y, id);
                }
                if (validWestHallway(x, y, id)) {
                    buildWestHallway(x, y, id);
                }
            }
            y += 1;
        }
    }

    /*
    Validates path if a path generated upward from (x, y) would encounter a Room that is not connected to the
    original Room
    If a Room is encountered, but this Room is already connected to the original, then the path is invalidated
     */
    public boolean validNorthHallway(int x, int y, int id) {
        for (int yPos = y; yPos <= pos.maxYIndex; yPos++) {
            TETile currentTile = pos.getTile(x, yPos);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, yPos);
                if (!connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return true;
                }
                if (connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return false;
                }
            }
        }
        return false;
    }






    /*
    Draws hallways in the south direction if hallways adjoin rooms
     */
    public void buildSouthHallway(int x, int y, int id) {

        for (int yPos = y; yPos >= 0; yPos--) {
            TETile currentTile = pos.getTile(x, yPos);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, yPos);
                if (id != newRoomID) {
                    connectedRooms.union(id, newRoomID);
                    break;
                }

            } else {
                world[x][yPos] = Tileset.HALLWAY;
                if (!pos.tileExists(x + 1, yPos)) {
                    world[x + 1][yPos] = Tileset.WALL;
                }
                if (!pos.tileExists(x - 1, yPos)) {
                    world[x - 1][yPos] = Tileset.WALL;
                }
            }
        }
        buildSouthBranches(x, y, id);
    }

    public void buildSouthBranches(int x, int y, int id) {
        boolean noRoomFound = true;
        while (noRoomFound && y >= 0) {

            if (pos.getTile(x, y).equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, y);
                if (id != newRoomID) {
                    noRoomFound = false;
                }

            } else if (pos.getTile(x, y).equals(Tileset.HALLWAY)) {
                if (validEastHallway(x, y, id)) {
                    buildEastHallway(x, y, id);
                }
                if (validWestHallway(x, y, id)) {
                    buildWestHallway(x, y, id);
                }
            }

            y -= 1;
        }
    }

    /*
    Validates path if a path generated south from (x, y) would encounter a Room that is not connected to the
    original Room
    If a Room is encountered, but this Room is already connected to the original, then the path is invalidated
     */
    public boolean validSouthHallway(int x, int y, int id) {
        for (int yPos = y; yPos >= 0; yPos--) {
            TETile currentTile = pos.getTile(x, yPos);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, yPos);
                if (!connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return true;
                }
                if (connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return false;
                }
            }
        }
        return false;
    }






    /*
    Draws hallways in the east direction if hallways adjoin rooms
     */
    public void buildEastHallway(int x, int y, int id) {

        for (int xPos = x; xPos <= pos.maxXIndex; xPos++) {
            TETile currentTile = pos.getTile(xPos, y);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(xPos, y);
                if (id != newRoomID) {
                    connectedRooms.union(id, newRoomID);
                    break;
                }

            } else {
                world[xPos][y] = Tileset.HALLWAY;
                if (!pos.tileExists(xPos, y + 1)) {
                    world[xPos][y + 1] = Tileset.WALL;
                }
                if (!pos.tileExists(xPos, y - 1)) {
                    world[xPos][y - 1] = Tileset.WALL;
                }
            }
        }
        buildEastBranches(x, y, id);
    }

    public void buildEastBranches(int x, int y, int id) {
        boolean noRoomFound = true;
        while (noRoomFound && x <= pos.maxXIndex) {

            if (pos.getTile(x, y).equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, y);
                if (id != newRoomID) {
                    noRoomFound = false;
                }

            } else if (pos.getTile(x, y).equals(Tileset.HALLWAY)) {
                if (validNorthHallway(x, y, id)) {
                    buildNorthHallway(x, y, id);
                }
                if (validSouthHallway(x, y, id)) {
                    buildSouthHallway(x, y, id);
                }
            }

            x += 1;
        }
    }

    /*
    Validates path if a path generated east from (x, y) would encounter a Room that is not connected to the
    original Room
    If a Room is encountered, but this Room is already connected to the original, then the path is invalidated
     */
    public boolean validEastHallway(int x, int y, int id) {
        for (int xPos = x; xPos <= pos.maxXIndex; xPos++) {
            TETile currentTile = pos.getTile(xPos, y);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(xPos, y);
                if (!connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return true;
                }
                if (connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return false;
                }
            }
        }
        return false;
    }






    /*
    Draws hallways in the west direction if hallways adjoin rooms
    */
    public void buildWestHallway(int x, int y, int id) {

        for (int xPos = x; xPos >= 0; xPos--) {
            TETile currentTile = pos.getTile(xPos, y);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(xPos, y);
                if (id != newRoomID) {
                    connectedRooms.union(id, newRoomID);
                    break;
                }

            } else {
                world[xPos][y] = Tileset.HALLWAY;
                if (!pos.tileExists(xPos, y + 1)) {
                    world[xPos][y + 1] = Tileset.WALL;
                }
                if (!pos.tileExists(xPos, y - 1)) {
                    world[xPos][y - 1] = Tileset.WALL;
                }
            }
        }
        buildWestBranches(x, y, id);
    }

    public void buildWestBranches(int x, int y, int id) {
        boolean noRoomFound = true;

        while (noRoomFound && x >= 0) {
            if (pos.getTile(x, y).equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(x, y);
                if (id != newRoomID) {
                    noRoomFound = false;
                }

            } else if (pos.getTile(x, y).equals(Tileset.HALLWAY)) {
                if (validNorthHallway(x, y, id)) {
                    buildNorthHallway(x, y, id);
                }
                if (validSouthHallway(x, y, id)) {
                    buildSouthHallway(x, y, id);
                }
            }

            x -= 1;
        }
    }

    /*
    Validates path if a path generated west from (x, y) would encounter a Room that is not connected to the
    original Room
    If a Room is encountered, but this Room is already connected to the original, then the path is invalidated
     */
    public boolean validWestHallway(int x, int y, int id) {
        for (int xPos = x; xPos >= 0; xPos--) {
            TETile currentTile = pos.getTile(xPos, y);

            if (currentTile.equals(Tileset.ROOM)) {
                int newRoomID = pointToRoomMap.getID(xPos, y);
                if (!connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return true;
                }
                if (connectedRooms.connected(id, newRoomID) && id != newRoomID) {
                    return false;
                }
            }
        }
        return false;
    }

}
