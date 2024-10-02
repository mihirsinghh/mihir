package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MovementProcessor {
    TETile[][] world;
    TETile currentTile;
    TETile moveToTile;

    World realWorld;
    Avatar avatar;
    EnemyGenerator enemyGen;
    Position pos;
    Random random;
    Long seed;

    private final Queue<Character> keyPressHistory = new LinkedList<>();
    private final String targetSequence = ":Q";

    public MovementProcessor(Avatar avatar, EnemyGenerator enemyGen, Long seed, Position pos) {
        this.pos = pos;
        this.world = pos.world;
        this.realWorld = pos.realWorld;
        this.random = pos.random;
        this.avatar = avatar;
        this.enemyGen = enemyGen;
        currentTile = Tileset.ROOM; //the type of Tile upon which the avatar is first located
        this.seed = seed;
    }

    public void moveAvatar() {
        avatar.posChanged = false;

        if (StdDraw.hasNextKeyTyped()) {
            char nextKey = StdDraw.nextKeyTyped();
            updateKeyPressHistory(nextKey);

            if (nextKey == 'w' || nextKey == 'W') {
                if (validateUp(avatar.currentX, avatar.currentY)) {
                    moveUp(avatar.currentX, avatar.currentY);
                }

            } else if (nextKey == 's' || nextKey == 'S') {
                if (validateDown(avatar.currentX, avatar.currentY)) {
                    moveDown(avatar.currentX, avatar.currentY);
                }

            } else if (nextKey == 'd' || nextKey == 'D') {
                if (validateRight(avatar.currentX, avatar.currentY)) {
                    moveRight(avatar.currentX, avatar.currentY);
                }

            } else if (nextKey == 'a' || nextKey == 'A') {
                if (validateLeft(avatar.currentX, avatar.currentY)) {
                    moveLeft(avatar.currentX, avatar.currentY);
                }

            } else if (nextKey == 'v' || nextKey == 'V') {
                updateVisibility(realWorld);

            } else if (checkSequence()) { //if the characters typed = exit sequence, save and end game
                try {
                    FileWriter fileWriter = new FileWriter("src/core/save-file.txt");

                    fileWriter.write(Integer.toString(avatar.getCurrentX()));
                    fileWriter.write(System.lineSeparator());
                    fileWriter.write(Integer.toString(avatar.getCurrentY()));
                    fileWriter.write(System.lineSeparator());
                    fileWriter.write(Integer.toString(enemyGen.enemy.currentX));
                    fileWriter.write(System.lineSeparator());
                    fileWriter.write(Integer.toString(enemyGen.enemy.currentY));
                    fileWriter.write(System.lineSeparator());
                    fileWriter.write(Long.toString(seed));

                    fileWriter.close();

                    System.out.println("Game has been saved to the file successfully.");
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to the file: " + e.getMessage());
                    e.printStackTrace();
                }

                System.exit(0);
            }
        }
    }

    /*
    Updates the user's key-press history by adding the key to the Queue. When Queue reaches sufficient size, restarts
    Written by ChatGPT
     */
    private void updateKeyPressHistory(char key) {
        // Append the latest key to the queue
        keyPressHistory.add(key);

        // Ensure that the history never exceeds the length of the target sequence
        while (keyPressHistory.size() > targetSequence.length()) {
            keyPressHistory.poll();
        }
    }

    /*
    Checks whether any sequence of characters in the Queue matches the exit String
    Written by ChatGPT
     */
    private boolean checkSequence() {
        StringBuilder sb = new StringBuilder();
        for (char key : keyPressHistory) {
            sb.append(key);
        }
        return sb.toString().equals(targetSequence);
    }

    /*
    Returns a boolean value indicating whether an upward movement of the avatar from its given location is valid or not
     */
    public boolean validateUp(int currentX, int currentY) {
        if (pos.isValidCoord(currentX, currentY + 1)) {
            TETile toTile = pos.getTile(currentX, currentY + 1); //tile to which the avatar would move
            return toTile == Tileset.ROOM || toTile == Tileset.HALLWAY || toTile == Tileset.PORTAL || toTile == Tileset.TELEPORTER;
        }
        return false;
    }

    /*
    Processes an upward movement of the avatar
     */
    public void moveUp(int currentX, int currentY) {
        moveToTile = world[currentX][currentY + 1];

        if (currentTile == Tileset.ROOM) {
            /*
            if the avatar is moving into a hallway from a Room, it leaves behind a Room tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a Room, it leaves behind a Room tile and is now positioned on
            another Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a Room, it leaves behind a Room tile and is now positioned
            on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY += 1;
                avatar.posChanged = true;
                avatar.foundPortal = true;

            /*
            if the avatar moves to a teleporter from a room, it leaves behind a room tile and is now positioned on a
            teleporter tile
             */
            } else if (moveToTile == Tileset.TELEPORTER) {
                currentTile = Tileset.TELEPORTER;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY += 1; //avatar's y-coord is now on avatar tile (originally teleporter tile)
                boolean teleporterFound = false;
                while (!teleporterFound) {
                    int x = pos.randomX(0, pos.maxXIndex);
                    int y = pos.randomY(0, pos.maxYIndex);
                    if (world[x][y] == Tileset.TELEPORTER) {
                        world[x][y] = Tileset.AVATAR;
                        world[avatar.currentX][avatar.currentY] = Tileset.TELEPORTER; //avatar's prev pos was av. tile
                        avatar.currentX = x; //update av. x-coord
                        avatar.currentY = y; //update av. y-coord
                        avatar.posChanged = true;
                        teleporterFound = true;
                    }
                }
            }


        } else if (currentTile == Tileset.HALLWAY) {
            /*
            if the avatar is moving into a hallway from a hallway, it leaves behind a hallway tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentY += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a hallway, it leaves behind a hallway tile and is now positioned
            on a Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentY += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a hallway, it leaves behind a hallway tile and is now
            positioned on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentY += 1;
                avatar.posChanged = true;
                avatar.foundPortal = true;
            }


        } else if (currentTile == Tileset.TELEPORTER) {
            /*
            if the avatar is moving into a room from a teleporter, it leaves behind a teleporter tile and is now
            positioned on a room tile
             */
            if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX][currentY + 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.TELEPORTER;
                avatar.currentY += 1;
                avatar.posChanged = true;
            }
        }
    }




    /*
    Returns a boolean value indicating whether a downward movement of the avatar from its given location is valid or not
     */
    public boolean validateDown(int currentX, int currentY) {
        if (pos.isValidCoord(currentX, currentY - 1)) {
            TETile toTile = pos.getTile(currentX, currentY - 1); //tile to which the avatar would move
            return toTile == Tileset.ROOM || toTile == Tileset.HALLWAY || toTile == Tileset.PORTAL || toTile == Tileset.TELEPORTER;
        }
        return false;
    }

    /*
    Processes a downward movement of the avatar
    */
    public void moveDown(int currentX, int currentY) {
        moveToTile = world[currentX][currentY - 1];

        if (currentTile == Tileset.ROOM) {
            /*
            if the avatar is moving into a hallway from a Room, it leaves behind a Room tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a Room, it leaves behind a Room tile and is now positioned on
            another Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a Room, it leaves behind a Room tile and is now positioned
            on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.foundPortal = true;
                avatar.currentY -= 1;
                avatar.posChanged = true;


            } else if (moveToTile == Tileset.TELEPORTER) {
                currentTile = Tileset.TELEPORTER;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentY -= 1; //avatar's y-coord is now on avatar tile (originally teleporter tile)
                boolean teleporterFound = false;
                while (!teleporterFound) {
                    int x = pos.randomX(0, pos.maxXIndex);
                    int y = pos.randomY(0, pos.maxYIndex);
                    if (world[x][y] == Tileset.TELEPORTER) {
                        world[x][y] = Tileset.AVATAR;
                        world[avatar.currentX][avatar.currentY] = Tileset.TELEPORTER; //avatar's prev pos was av. tile
                        avatar.currentX = x; //update av. x-coord
                        avatar.currentY = y; //update av. y-coord
                        avatar.posChanged = true;
                        teleporterFound = true;
                    }
                }
            }


        } else if (currentTile == Tileset.HALLWAY) {
            /*
            if the avatar is moving into a hallway from a hallway, it leaves behind a hallway tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentY -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a hallway, it leaves behind a hallway tile and is now positioned
            on a Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentY -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a hallway, it leaves behind a hallway tile and is now
            positioned on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentY -= 1;
                avatar.posChanged = true;
                avatar.foundPortal = true;
            }


        } else if (currentTile == Tileset.TELEPORTER) {
            /*
            if the avatar is moving into a room from a teleporter, it leaves behind a teleporter tile and is now
            positioned on a room tile
             */
            if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX][currentY - 1] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.TELEPORTER;
                avatar.currentY -= 1;
                avatar.posChanged = true;
            }
        }
    }



    /*
    Returns a boolean value indicating whether a right movement of the avatar from its given location is valid or not
     */
    public boolean validateRight(int currentX, int currentY) {
        if (pos.isValidCoord(currentX + 1, currentY)) {
            TETile toTile = pos.getTile(currentX + 1, currentY); //tile to which the avatar would move
            return toTile == Tileset.ROOM || toTile == Tileset.HALLWAY || toTile == Tileset.PORTAL || toTile == Tileset.TELEPORTER;
        }
        return false;
    }

    /*
    Processes a right movement of the avatar
    */
    public void moveRight(int currentX, int currentY) {
        moveToTile = world[currentX + 1][currentY];

        if (currentTile == Tileset.ROOM) {
            /*
            if the avatar is moving into a hallway from a Room, it leaves behind a Room tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a Room, it leaves behind a Room tile and is now positioned on
            another Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a Room, it leaves behind a Room tile and is now positioned
            on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX += 1;
                avatar.posChanged = true;
                avatar.foundPortal = true;

            } else if (moveToTile == Tileset.TELEPORTER) {
                currentTile = Tileset.TELEPORTER;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX += 1; //avatar's x-coord is now on avatar tile (originally teleporter tile)
                boolean teleporterFound = false;
                while (!teleporterFound) {
                    int x = pos.randomX(0, pos.maxXIndex);
                    int y = pos.randomY(0, pos.maxYIndex);
                    if (world[x][y] == Tileset.TELEPORTER) {
                        world[x][y] = Tileset.AVATAR;
                        world[avatar.currentX][avatar.currentY] = Tileset.TELEPORTER; //avatar's prev pos was av. tile
                        avatar.currentX = x; //update av. x-coord
                        avatar.currentY = y; //update av. y-coord
                        avatar.posChanged = true;
                        teleporterFound = true;
                    }
                }
            }


        } else if (currentTile == Tileset.HALLWAY) {
            /*
            if the avatar is moving into a hallway from a hallway, it leaves behind a hallway tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentX += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a hallway, it leaves behind a hallway tile and is now positioned
            on a Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentX += 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a hallway, it leaves behind a hallway tile and is now
            positioned on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.foundPortal = true;
                avatar.currentX += 1;
                avatar.posChanged = true;
            }


        } else if (currentTile == Tileset.TELEPORTER) {
            /*
            if the avatar is moving into a room from a teleporter, it leaves behind a teleporter tile and is now
            positioned on a room tile
             */
            if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX + 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.TELEPORTER;
                avatar.currentX += 1;
                avatar.posChanged = true;
            }
        }
    }



    /*
Returns a boolean value indicating whether a left movement of the avatar from its given location is valid or not
 */
    public boolean validateLeft(int currentX, int currentY) {
        if (pos.isValidCoord(currentX - 1, currentY)) {
            TETile toTile = pos.getTile(currentX - 1, currentY); //tile to which the avatar would move
            return toTile == Tileset.ROOM || toTile == Tileset.HALLWAY || toTile == Tileset.PORTAL || toTile == Tileset.TELEPORTER;
        }
        return false;
    }

    /*
    Processes a left movement of the avatar
    */
    public void moveLeft(int currentX, int currentY) {
        moveToTile = world[currentX - 1][currentY];

        if (currentTile == Tileset.ROOM) {
            /*
            if the avatar is moving into a hallway from a Room, it leaves behind a Room tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a Room, it leaves behind a Room tile and is now positioned on
            another Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a Room, it leaves behind a Room tile and is now positioned
            on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX -= 1;
                avatar.posChanged = true;
                avatar.foundPortal = true;


            } else if (moveToTile == Tileset.TELEPORTER) {
                currentTile = Tileset.TELEPORTER;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.ROOM;
                avatar.currentX -= 1; //avatar's x-coord is now on avatar tile (originally teleporter tile)
                boolean teleporterFound = false;
                while (!teleporterFound) {
                    int x = pos.randomX(0, pos.maxXIndex);
                    int y = pos.randomY(0, pos.maxYIndex);
                    if (world[x][y] == Tileset.TELEPORTER) {
                        world[x][y] = Tileset.AVATAR;
                        world[avatar.currentX][avatar.currentY] = Tileset.TELEPORTER;
                        avatar.currentX = x; //update av. x-coord
                        avatar.currentY = y; //update av. y-coord
                        avatar.posChanged = true;
                        teleporterFound = true;
                    }
                }
            }


        } else if (currentTile == Tileset.HALLWAY) {
            /*
            if the avatar is moving into a hallway from a hallway, it leaves behind a hallway tile and is now positioned
            on a hallway tile
             */
            if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentX -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into a Room from a hallway, it leaves behind a hallway tile and is now positioned
            on a Room tile
             */
            } else if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.currentX -= 1;
                avatar.posChanged = true;

            /*
            if the avatar is moving into the Portal from a hallway, it leaves behind a hallway tile and is now
            positioned on the Portal tile
             */
            } else if (moveToTile == Tileset.PORTAL) {
                currentTile = Tileset.PORTAL;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.HALLWAY;
                avatar.foundPortal = true;
                avatar.currentX -= 1;
                avatar.posChanged = true;
            }


        } else if (currentTile == Tileset.TELEPORTER) {
            /*
            if the avatar is moving into a room from a teleporter, it leaves behind a teleporter tile and is now
            positioned on a room tile
             */
            if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                world[currentX - 1][currentY] = Tileset.AVATAR;
                world[currentX][currentY] = Tileset.TELEPORTER;
                avatar.currentX -= 1;
                avatar.posChanged = true;
            }
        }
    }

    /*
    Switches the current state of visibility
     */
    public void updateVisibility(World actualWorld) {
        if (actualWorld.activeFog) {
            actualWorld.activeFog = false;
        } else {
            actualWorld.activeFog = true;
        }
    }
}
