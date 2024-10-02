package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class EndScreen {
    //end screen dimensions
    private final int canvasWidth = 800;
    private final int canvasHeight = 600;
    private final int pauseTime = 100;

    //end screen coordinates
    private final int menuX1 = 400;
    private final int menuY1 = 450;
    private final int menuY2 = 350;
    private final int menuY3 = 300;
    private final int menuY4 = 250;
    private final int menuY5 = 200;
    private final int menuY6 = 100;

    //end screen font size
    private final int fontSize = 24;

    Main main;
    Avatar avatar;

    public EndScreen(Main main, Avatar avatar) {
        this.main = main;
        this.avatar = avatar;
    }

    public void showEndScreen() {
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, canvasWidth);
        StdDraw.setYscale(0, canvasHeight);

        boolean running = true;

        while (running) {
            StdDraw.clear();
            drawEndScreen();
            StdDraw.show();
            StdDraw.pause(pauseTime); // Pause to prevent busy waiting

            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                if (choice == 'Y' || choice == 'y') {
                    long seed = newGame();
                    if (seed != -1) {
                        new GameGenerator(main, seed);
                    }
                    break;

                } else if (choice == 'N' || choice == 'n') {
                    System.exit(1);
                }
            }
        }
    }

    private void drawEndScreen() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, fontSize));
        if (avatar.foundPortal) {
            StdDraw.text(menuX1, menuY1, "You win!");
        } else {
            StdDraw.text(menuX1, menuY1, "You lose");
        }
        StdDraw.text(menuX1, menuY2, "Y: Play Again");
        StdDraw.text(menuX1, menuY3, "N: Exit");
    }

    private long newGame() {
        StringBuilder seedBuilder = new StringBuilder();
        StdDraw.clear(); // Clear the screen before displaying prompt
        drawEndScreen();
        StdDraw.text(menuX1, menuY5, "Enter seed (end with 'S'):");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (Character.isDigit(input)) {
                    seedBuilder.append(input);
                } else if (Character.toUpperCase(input) == 'S') {
                    break;
                }

                StdDraw.clear();
                drawEndScreen();
                StdDraw.text(menuX1, menuY5, "Enter seed (end with 'S'):");
                StdDraw.text(menuX1, menuY6, seedBuilder.toString());
                StdDraw.show();
            }
        }
        return Long.parseLong(seedBuilder.toString());
    }
}
