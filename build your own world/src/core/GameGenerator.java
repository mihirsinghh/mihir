package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.Tileset;

import java.awt.*;
import java.util.Random;
import java.util.Set;

public class GameGenerator {
    World world;
    RoomGenerator roomGen;
    HallwayGenerator hallwayGen;
    AvatarGenerator avatarGen;
    EnemyGenerator enemyGen;
    PortalGenerator portalGen;
    TeleporterGenerator teleporterGenerator;
    MovementProcessor mover;
    EnemyMovementProcessor enemyMover;
    PointToRoomMap pointToRoomMap;
    TeleporterToPointMap teleporterToPointMap;
    Position pos;
    HUD hud;
    Random random;
    TERenderer ter;
    Main main;

    public GameGenerator(Main main, long seed) {
        //creates a Random object initialized with a given seed
        random = new Random(seed);

        //main instance allows for access to Main methods (playing in game music, etc.)
        this.main = main;

        //creates a new, empty world
        ter = new TERenderer();
        world = new World(ter);

        //initializes world tracker and map of tiles to corresponding rooms
        pos = new Position(world.getWorld(), world, random);
        this.pointToRoomMap = new PointToRoomMap();

        //generates the contents of the world (rooms, hallways, etc.)
        roomGen = new RoomGenerator(pointToRoomMap, pos);
        roomGen.drawRooms();
        hallwayGen = new HallwayGenerator(roomGen);
        hallwayGen.connectRooms();

        //creates and spawns a new avatar in a random room
        avatarGen = new AvatarGenerator(pos);
        avatarGen.spawn();
        enemyGen = new EnemyGenerator(pos, avatarGen.avatar, pointToRoomMap);
        enemyGen.spawn();

        //creates a new Portal in a random room that is sufficiently far from the avatar's spawn point
        portalGen = new PortalGenerator(avatarGen.avatar, pos, pointToRoomMap);
        portalGen.createPortal();

        //creates Teleporters in the world
        teleporterToPointMap = new TeleporterToPointMap();
        teleporterGenerator = new TeleporterGenerator(pos, teleporterToPointMap, roomGen);
        teleporterGenerator.placeTeleporters();

        //renders changes to world
        ter.renderFrame(world.getWorld());

        //creates a new movement processor/handler for the avatar
        mover = new MovementProcessor(avatarGen.avatar, enemyGen, seed, pos);
        enemyMover = new EnemyMovementProcessor(pos, enemyGen, avatarGen.avatar);

        hud = new HUD(avatarGen.avatar, pos);


        playGame();
    }

    public GameGenerator(Main main, long seed, int avatarX, int avatarY, int enemyX, int enemyY) {
        //creates a Random object initialized with a given seed
        random = new Random(seed);

        //main instance allows for access to Main methods (playing in game music, etc.)
        this.main = main;

        //creates a new, empty world
        ter = new TERenderer();
        world = new World(ter);

        //initializes world tracker and map of tiles to corresponding rooms
        pos = new Position(world.getWorld(), world, random);
        this.pointToRoomMap = new PointToRoomMap();

        //generates the contents of the world (rooms, hallways, etc.)
        roomGen = new RoomGenerator(pointToRoomMap, pos);
        roomGen.drawRooms();
        hallwayGen = new HallwayGenerator(roomGen);
        hallwayGen.connectRooms();

        //creates and spawns a new avatar in a random room
        avatarGen = new AvatarGenerator(pos);
        avatarGen.load(avatarX, avatarY);
        enemyGen = new EnemyGenerator(pos, avatarGen.avatar, pointToRoomMap);
        enemyGen.load(enemyX, enemyY);

        //creates a new Portal in a random room that is sufficiently far from the avatar's spawn point
        portalGen = new PortalGenerator(avatarGen.avatar, pos, pointToRoomMap);
        portalGen.createPortal();

        //creates Teleporters in the world
        teleporterToPointMap = new TeleporterToPointMap();
        teleporterGenerator = new TeleporterGenerator(pos, teleporterToPointMap, roomGen);
        teleporterGenerator.placeTeleporters();

        //renders changes to world
        ter.renderFrame(world.getWorld());

        //creates a new movement processor/handler for the avatar
        mover = new MovementProcessor(avatarGen.avatar, enemyGen, seed, pos);
        enemyMover = new EnemyMovementProcessor(pos, enemyGen, avatarGen.avatar);

        hud = new HUD(avatarGen.avatar, pos);


        playGame();
    }

    public void playGame() {
        // Run the enemy movement in a separate thread
        new Thread(() -> {
            while (!avatarGen.avatar.foundPortal && avatarGen.avatar.alive) {
                enemyMover.chase();
                try {
                    Thread.sleep(150); // Delay between each chase execution
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (!avatarGen.avatar.foundPortal && avatarGen.avatar.alive) {
            main.playGameMusic();
            mover.moveAvatar();
            hud.trackMouse();

            StdDraw.clear(Color.BLACK);

            if (!world.activeFog) {
                ter.drawTiles(world.getWorld());
                hideWorldComponents();
            } else {
                activateFog();
                showWorldComponents();
            }
        }

        main.stopGameMusic();
        EndScreen endScreen = new EndScreen(main, avatarGen.avatar);
        endScreen.showEndScreen();
    }

    /*
    Activates the fog of war by creating a copy of the world, but only copying the Tiles that are within the
    vicinity of the avatar - everything else in this world copy is a Nothing Tile

    If a Tile is in the vicinity of the avatar, its coordinate will be marked as "true" in the boolean world

    The copies are recreated each time the method is called to simulate fog-of-war following the avatar
     */
    public void activateFog() {
        boolean[][] visibility = new boolean[world.getWorldWidth()][world.getWorldHeight()];

        Set<Point> withinRadius = pos.withinRadius(avatarGen.avatar.currentX, avatarGen.avatar.currentY);
        for (Point eachPoint : withinRadius) {
            int x = eachPoint.getX();
            int y = eachPoint.getY();
            visibility[x][y] = true;
        }

        for (int y = 0; y <= pos.maxYIndex; y++) {
            for (int x = 0; x <= pos.maxXIndex; x++) {
                if (visibility[x][y]) {
                    world.getWorldCopy()[x][y] = world.getWorld()[x][y];
                }
            }
        }
        ter.drawTiles(world.getWorldCopy());
        world.resetWorldCopy(world.getWorldCopy());
    }

    public void hideWorldComponents() {
        hidePortal();
        //hideTeleporters();
    }

    public void showWorldComponents() {
        showPortal();
        showTeleporters();
    }

    /*
    Hides the portal when fog of war is disabled
     */
    public void hidePortal() {
        int portalX = portalGen.portalX;
        int portalY = portalGen.portalY;
        world.getWorld()[portalX][portalY] = Tileset.ROOM;
    }

    /*
    Makes portal appear in the world by modifying the world array so that the copy can access the portal tile when
    fog of war is enabled
     */
    public void showPortal() {
        int portalX = portalGen.portalX;
        int portalY = portalGen.portalY;
        world.getWorld()[portalX][portalY] = Tileset.PORTAL;
    }

    /*
    Hides all teleporters when fog of war is disabled
     */
    public void hideTeleporters() {
        for (Teleporter eachTeleporter : teleporterToPointMap.allTeleporters()) {
            world.getWorld()[eachTeleporter.x][eachTeleporter.y] = Tileset.ROOM;
        }
    }

    /*
    Makes teleporters appear in the world by modifying the world array so that the copy can access the TP tile when
    fog of war is enabled
     */
    public void showTeleporters() {
        for (Teleporter eachTeleporter : teleporterToPointMap.allTeleporters()) {
            world.getWorld()[eachTeleporter.x][eachTeleporter.y] = Tileset.TELEPORTER;
        }
    }
}
