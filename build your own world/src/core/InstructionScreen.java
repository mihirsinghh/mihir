package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class InstructionScreen {
    //end screen dimensions
    private final int canvasWidth = 800;
    private final int canvasHeight = 600;
    private final int pauseTime = 100;

    //play and exit prompts
    private final int playX = 650;
    private final int playY = 80;
    private final int exitX = 650;
    private final int exitY = 50;

    //how to play:
    private final int htpX = 100;
    private final int htpY = 550;

    //instruction lines
    private final int line1X = 325;
    private final int line1Y = htpY - 50;
    private final int line2X = 153;
    private final int line2Y = line1Y - 50;
    private final int line3X = 250;
    private final int line3Y = line2Y - 50;
    private final int line4X = 363;
    private final int line4Y = line3Y - 50;
    private final int line5X = 367;
    private final int line5Y = line4Y - 50;

    //font size
    private final int instructionFontSize = 18;

    Main main;
    Long seed;

    public InstructionScreen(Main main, Long seed) {
        this.main = main;
        this.seed = seed;
    }

    public void showInstructions() {
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, canvasWidth);
        StdDraw.setYscale(0, canvasHeight);

        boolean running = true;

        while (running) {
            StdDraw.clear();
            drawInstructionScreen();
            StdDraw.show();
            StdDraw.pause(pauseTime); // Pause to prevent busy waiting

            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                if (choice == 'P' || choice == 'p') {
                    if (seed != -1) {
                        new GameGenerator(main, seed);
                    }
                    break;

                } else if (choice == 'E' || choice == 'e') {
                    System.exit(1);
                }
            }
        }
    }

    private void drawInstructionScreen() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, instructionFontSize));
        StdDraw.text(htpX, htpY, "How to play: ");
        StdDraw.text(line1X, line1Y, "Objective: find the portal (red square) before enemy catches you");
        StdDraw.text(line2X, line2Y, "Toggle fog of war using v");
        StdDraw.text(line3X, line3Y, "Portal is only visible when fog of war is enabled");
        StdDraw.text(line4X, line4Y, "Turning fog of war off can be helpful to view map and see enemy position");
        StdDraw.text(line5X, line5Y, "Teleporters (blue square) will teleport you to another teleporter on the map");
        StdDraw.text(playX, playY, "P: Play");
        StdDraw.text(exitX, exitY, "E: Exit");
    }
}
