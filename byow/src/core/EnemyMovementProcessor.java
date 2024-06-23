package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class EnemyMovementProcessor {
    Position pos;
    TETile currentTile;
    EnemyGenerator enemyGenerator;
    Avatar avatar;

    Queue<Point> fScoreQueue;
    Map<Point, Double> gScoreMap;
    Map<Point, Double> hScoreMap;
    Map<Point, Double> fScoreMap;
    Map<Point, Point> parentMap;
    Map<Point, Point> childMap;

    public EnemyMovementProcessor(Position pos, EnemyGenerator enemyGenerator, Avatar avatar) {
        this.pos = pos;
        this.enemyGenerator = enemyGenerator;
        this.avatar = avatar;
        currentTile = Tileset.ROOM; //enemy always spawns in a Room
    }

    /*
    Creates path to the avatar, then moves enemy towards avatar
     */
    public void chase() {
        determinePath();
    }

    /*
    Finds a sequence of points that will lead the enemy to the avatar in the most efficient manner possible
     */
    public void determinePath() {
        Point start = new Point(enemyGenerator.enemy.currentX, enemyGenerator.enemy.currentY);
        Point end = new Point(avatar.currentX, avatar.currentY);
        initializePath(start, end);

        //generates new path from enemy's current position
        boolean avatarNotFound = true;
        while (avatarNotFound) {
            Point processed = fScoreQueue.poll();
            assert processed != null;

            if (pos.getTile(processed.getX(), processed.getY()).equals(Tileset.WALL)) {
                continue;
            }
            /*
            when the avatar tile is the tile with the lowest f-score in the queue, the path to it will contain its
            parent, which was the tile from which the avatar's f-score was updated, and that tile's parent, etc.
             */
            if (processed.equals(end)) {
                avatarNotFound = false;
                reconstructPath(start, end);
                moveEnemy(start, end);
            /*
            update the neighbors of the point with the lowest f-score. This point is now the "parent" of
            its updated neighbors.
            */
            } else {
                evaluateNeighbors(processed);
            }
        }
    }

    /*
    Moves the enemy from the avatar's current position to the enemy's current position
     */
    public void moveEnemy(Point start, Point end) {
        Point next = childMap.get(start);
        moveToPoint(next);

        //move the enemy along the path
        while (!avatar.posChanged) {
            next = childMap.get(next);

            //enemy stops walking along calculated path if avatar changes positions, or if it reaches end of path
            if (avatar.posChanged || next == null) {
                break;
            } else {
                moveToPoint(next);
            }

            // Add a delay to simulate slower movement
            try {
                Thread.sleep(150); // Delay for x milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    Reconstructs the shortest path to the avatar from enemy's current position using parent mappings of each point
     */
    public void reconstructPath(Point start, Point end) {
        Point parent = parentMap.get(end);

        //base case - start point is the parent point of the first actual point in the path
        if (parent.equals(start)) {
            childMap.put(start, end);
            return;
        }

        childMap.put(parent, end);
        reconstructPath(start, parent);
    }

    /*
    Each time the avatar moves, the A* algorithm is re-implemented using the avatar and enemy's current positions
     */
    public void initializePath(Point start, Point end) {
        //initializes the priority queue
        fScoreQueue = new PriorityQueue<>(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return Double.compare(fScoreMap.get(o1), fScoreMap.get(o2));
            }
        });

        //initializes maps
        fScoreMap = new HashMap<>();
        gScoreMap = new HashMap<>();
        hScoreMap = new HashMap<>();
        parentMap = new HashMap<>();
        childMap = new HashMap<>();

        //all points, besides the start point, initially have infinite g and f-scores, and unique h-scores
        for (int x = 0; x <= pos.maxXIndex; x++) {
            for (int y = 0; y <= pos.maxYIndex; y++) {
                Point current = new Point(x, y);

                boolean isStart = current.equals(start);
                boolean isRoom = pos.getTile(current.getX(), current.getY()).equals(Tileset.ROOM);
                boolean isHall = pos.getTile(current.getX(), current.getY()).equals(Tileset.HALLWAY);
                boolean isAvatar = pos.getTile(current.getX(), current.getY()).equals(Tileset.AVATAR);

                if (!isStart) {
                //if ((isRoom || isHall || isAvatar) && (!isStart)) {
                    gScoreMap.put(current, Double.POSITIVE_INFINITY);
                    fScoreMap.put(current, Double.POSITIVE_INFINITY);
                    double hScore = calculateHScore(current.getX(), current.getY(), end.getX(), end.getY());
                    hScoreMap.put(current, hScore);
                }
            }
        }

        //start point has an f-score that is equivalent to its h-score, since its g-score is 0
        double startHScore = calculateHScore(start.getX(), start.getY(), end.getX(), end.getY());
        hScoreMap.put(start, startHScore);
        gScoreMap.put(start, 0.0);
        fScoreMap.put(start, startHScore);

        //adds starting point to queue
        fScoreQueue.add(start);
    }

    /*
    Evaluates the neighbors of the newly processed point
     */
    /*
    problem here is that if the path planner determines that the direct neighbors of a newly processed point are
    non-room/hall points, and there are no other direct neighbors with lower-than-current g-scores, no more points
    will get added to the f-score queue, and it will become empty before the path leads to the avatar

    potential solution - if queue empties before avatar found, change start location of path?
     */
    public void evaluateNeighbors(Point processed) {
        //evaluates north tile if it is part of a room or hallway
        if (pos.isValidCoord(processed.getX(), processed.getY() + 1)) {
            //boolean isRoom = pos.getTile(processed.getX(), processed.getY() + 1).equals(Tileset.ROOM);
            //boolean isHall = pos.getTile(processed.getX(), processed.getY() + 1).equals(Tileset.HALLWAY);
            //if (isRoom || isHall) {
                Point next = new Point(processed.getX(), processed.getY() + 1);
                evaluate(processed, next);
            //}
        }

        //evaluates south tile if it is part of a room or hallway
        if (pos.isValidCoord(processed.getX(), processed.getY() - 1)) {
            //boolean isRoom = pos.getTile(processed.getX(), processed.getY() - 1).equals(Tileset.ROOM);
            //boolean isHall = pos.getTile(processed.getX(), processed.getY() - 1).equals(Tileset.HALLWAY);
            //if (isRoom || isHall) {
                Point next = new Point(processed.getX(), processed.getY() - 1);
                evaluate(processed, next);
            //}
        }

        //evaluates east tile if it is part of a room or hallway
        if (pos.isValidCoord(processed.getX() + 1, processed.getY())) {
            //boolean isRoom = pos.getTile(processed.getX() + 1, processed.getY()).equals(Tileset.ROOM);
            //boolean isHall = pos.getTile(processed.getX() + 1, processed.getY()).equals(Tileset.HALLWAY);
            //if (isRoom || isHall) {
                Point next = new Point(processed.getX() + 1, processed.getY());
                evaluate(processed, next);
            //}
        }

        //evaluates west tile if it is part of a room or hallway
        if (pos.isValidCoord(processed.getX() - 1, processed.getY())) {
            //boolean isRoom = pos.getTile(processed.getX() - 1, processed.getY()).equals(Tileset.ROOM);
            //boolean isHall = pos.getTile(processed.getX() - 1, processed.getY()).equals(Tileset.HALLWAY);
            //if (isRoom || isHall) {
                Point next = new Point(processed.getX() - 1, processed.getY());
                evaluate(processed, next);
            //}
        }
    }

    /*
    Evaluates the neighbor point, using the newly processed point as reference, by:
        Updating the points' g and f-scores if a lower g-score is calculated
        Updating the points' shortest-path parent point if condition 1 is fulfilled
        Adding the point to the f-score queue if condition 1 is fulfilled
     */
    public void evaluate(Point processed, Point neighbor) {
        //calculates the g-score of the neighbor
        double processedGScore = gScoreMap.get(processed);
        double distToNeighbor = pos.distBetween(processed.getX(), processed.getY(), neighbor.getX(), neighbor.getY());
        double neighborGScore = processedGScore + distToNeighbor;

        /*
        compares the neighbor's tentative g-score to its current g-score.
        if smaller:
            updates g and f-scores
            adds neighbor to f-score queue
            updates parent of neighbor
         */
        if (neighborGScore < gScoreMap.get(neighbor)) {
            gScoreMap.put(neighbor, neighborGScore);
            double neighborFScore = neighborGScore + hScoreMap.get(neighbor);
            fScoreMap.put(neighbor, neighborFScore);
            parentMap.put(neighbor, processed);

            if (fScoreQueue.contains(neighbor)) {
                fScoreQueue.remove(neighbor);
                fScoreQueue.add(neighbor);
            } else {
                fScoreQueue.add(neighbor);
            }
        }
    }

    /*
    Calculates heuristic (distance between avatar's position and enemy's potential position) based on Manhattan distance
     */
    public double calculateHScore(int avatarX, int avatarY, int pointX, int pointY) {
        int xDiff = avatarX - pointX;
        int yDiff = avatarY - pointY;
        return Math.abs(xDiff) + Math.abs(yDiff);
    }

    /*
    Moves the enemy to the given point
     */
    public void moveToPoint(Point next) {
        int nextX = next.getX();
        int nextY = next.getY();
        TETile moveToTile = pos.world[nextX][nextY];

        if (currentTile == Tileset.ROOM) {
            if (moveToTile == Tileset.ROOM) {
                //enemy is now positioned on a room tile
                currentTile = Tileset.ROOM;
                //enemy leaves behind a room tile, since it moved from a room into another room
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.ROOM;
                //the enemy moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.HALLWAY) {
                //enemy is now positioned on a hallway tile
                currentTile = Tileset.HALLWAY;
                //enemy leaves behind a room tile, since it moved from a room into a hallway
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.ROOM;
                //the enemy moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.AVATAR) {
                //if enemy is about to move to avatar tile from a room tile, keep the current tile "room" since the
                //enemy will not physically move onto the avatar tile - it only lowers avatar's health if its next move
                //was to attack the avatar directly
                currentTile = Tileset.ROOM;
                //enemy leaves behind a room tile
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.ROOM;
                /*
                pos.world[nextX][nextY] = Tileset.ENEMY;
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;
                 */
                avatar.alive = false;

            } else if (moveToTile == Tileset.WALL) {
                //enemy is now positioned on a wall tile
                currentTile = Tileset.WALL;
                //enemy leaves behind a room tile, since it moved from a room into a wall
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.ROOM;
                //the enemy moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.NOTHING) {
                //enemy is now positioned on a void tile
                currentTile = Tileset.NOTHING;
                //enemy leaves behind a room tile, since it moved from a room into the void
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.ROOM;
                //the enemy moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;
            }


        } else if (currentTile == Tileset.HALLWAY) {
            if (moveToTile == Tileset.ROOM) {
                currentTile = Tileset.ROOM;
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.HALLWAY;
                pos.world[nextX][nextY] = Tileset.ENEMY;
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.HALLWAY) {
                currentTile = Tileset.HALLWAY;
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.HALLWAY;
                pos.world[nextX][nextY] = Tileset.ENEMY;
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.AVATAR) {
                //if enemy is about to move to avatar tile from a hall tile, keep the current tile "room" since the
                //enemy will not physically move onto the avatar tile - it only lowers avatar's health if its next move
                //was to attack the avatar directly
                currentTile = Tileset.HALLWAY;
                //enemy leaves behind a hall tile
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.HALLWAY;
                /*
                pos.world[nextX][nextY] = Tileset.ENEMY;
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;
                 */
                avatar.alive = false;

            } else if (moveToTile == Tileset.WALL) {
                //enemy is now positioned on a wall tile
                currentTile = Tileset.WALL;
                //enemy leaves behind a hall tile, since it moved from a hallway into a wall
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.HALLWAY;
                //the enemy moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.NOTHING) {
                //enemy is now positioned on a void tile
                currentTile = Tileset.NOTHING;
                //enemy leaves behind a hall tile, since it moved from a hallway into the void
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.HALLWAY;
                //the enemy moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;
            }


        } else if (currentTile == Tileset.WALL) {
            if (moveToTile == Tileset.WALL) {
                //enemy is now positioned on a wall tile
                currentTile = Tileset.WALL;
                //enemy leaves behind a wall tile, since it moved from a wall tile to another wall tile
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.WALL;
                //the enemy visually moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.NOTHING) {
                //enemy is now positioned on a void tile
                currentTile = Tileset.NOTHING;
                //enemy leaves behind a wall tile, since it moved from a wall tile into the void
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.WALL;
                //the enemy visually moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.ROOM) {
                //enemy is now positioned on a room tile
                currentTile = Tileset.ROOM;
                //enemy leaves behind a wall tile, since it moved from a wall tile to a room
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.WALL;
                //the enemy visually moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.HALLWAY) {
                //enemy is now positioned on a hall tile
                currentTile = Tileset.HALLWAY;
                //enemy leaves behind a wall tile, since it moved from a wall tile to a hallway
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.WALL;
                //the enemy visually moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.AVATAR) {
                //if enemy is about to move to avatar tile from a wall tile, keep the current tile "wall" since the
                //enemy will not physically move onto the avatar tile - it only lowers avatar's health if its next move
                //was to attack the avatar directly
                currentTile = Tileset.WALL;
                //enemy leaves behind a wall tile
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.WALL;
                /*
                pos.world[nextX][nextY] = Tileset.ENEMY;
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;
                 */
                avatar.alive = false;
            }
        }

        else if (currentTile == Tileset.NOTHING) {
            if (moveToTile == Tileset.NOTHING) {
                //enemy is now positioned on a void tile
                currentTile = Tileset.NOTHING;
                //enemy leaves behind a void tile, since it moved from a void tile to another void tile
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.NOTHING;
                //the enemy visually moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;

            } else if (moveToTile == Tileset.WALL) {
                //enemy is now positioned on a wall tile
                currentTile = Tileset.WALL;
                //enemy leaves behind a void tile, since it moved from the void to a wall tile
                pos.world[enemyGenerator.enemy.currentX][enemyGenerator.enemy.currentY] = Tileset.NOTHING;
                //the enemy visually moves to the given location
                pos.world[nextX][nextY] = Tileset.ENEMY;
                //enemy's current coordinates are updated
                enemyGenerator.enemy.currentX = nextX;
                enemyGenerator.enemy.currentY = nextY;
            }
        }
    }
}
