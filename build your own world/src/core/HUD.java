package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.util.Random;


public class HUD {
    TETile[][] world;
    World realWorld;
    Avatar avatar;
    Random random;
    Position pos;

    private final int xPlacement1 = 32;
    private final int yPlacement1 = 18;
    private final int xPlacement2 = 5;
    private final int yPlacement2 = 18;
    private final int xPlacement3 = 28;
    private final int yPlacement3 = 18;


    public HUD(Avatar avatar, Position pos) {
        this.pos = pos;
        this.world = pos.world;
        this.realWorld = pos.realWorld;
        this.random = pos.random;
        this.avatar = avatar;
    }

    public void trackMouse() {
        drawHUD();
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        /*
        where the world appears to be on screen is a consequence of where rendering begins
        thus, if yOffset is 3, world[0][0] = (0, 3) on-screen
         */
        int worldX = mouseX - realWorld.getXOffset();
        int worldY = mouseY - realWorld.getYOffset();

        if (pos.isValidCoord(worldX, worldY)) {
            if (realWorld.activeFog) {
                TETile tile = pos.getTile(avatar.currentX, avatar.currentY); //if fog of war enabled, only show avatar
                displayTile(tile);
            } else {
                TETile tile = pos.getTile(worldX, worldY); //else, show location in world where cursor is pointing
                displayTile(tile);
            }
        }
    }

    public void drawHUD() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text((double) realWorld.getWorldWidth() / 2 - xPlacement1, (double) (realWorld.getWorldHeight()) / 2 - yPlacement1,
                "location: ");
        StdDraw.text((double) realWorld.getWorldWidth() - xPlacement2, (double) realWorld.getWorldHeight() / 2 - yPlacement2,
                "v: toggle fog of war");

    }

    public void displayTile(TETile tile) {
        StdDraw.setPenColor(StdDraw.WHITE);
        String tileDesc = tile.description();
        StdDraw.text((double) (realWorld.getWorldWidth()) / 2 - xPlacement3, (double) (realWorld.getWorldHeight()) / 2 - yPlacement3,
                tileDesc);
        StdDraw.show();
    }

}
