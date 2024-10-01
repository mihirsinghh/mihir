package core;
import javax.sound.sampled.*;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/*music portion assisted via GPT*/

public class Main {
    InstructionScreen instructionScreen;
    private Clip menuClip;
    private Clip gameClip;

    //menu screen dimensions
    private final int canvasWidth = 800;
    private final int canvasHeight = 600;
    private final int pauseTime = 100;

    //menu screen coordinates
    private final int menuX1 = 400;
    private final int menuY1 = 450;
    private final int menuY2 = 350;
    private final int menuY3 = 300;
    private final int menuY4 = 250;
    private final int menuY5 = 200;
    private final int menuY6 = 100;

    private final int fontSize = 24;


    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Main main = new Main();
        main.run();
    }

    private void run() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        StdDraw.enableDoubleBuffering();
        loadMenuMusic();
        loadGameMusic();
        playMenuMusic();
        displayMainMenu();
    }


    private void playMenuMusic() {
        if (menuClip != null) {
            menuClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    private void stopMenuMusic() {
        menuClip.stop();
    }


    public void playGameMusic() {
        if (gameClip != null) {
            gameClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public void stopGameMusic() {
        gameClip.stop();
    }


    private void loadGameMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File gameAudio = new File("src/music/ominous-background-music.wav");
        AudioInputStream audioInputStream = null;
        audioInputStream = AudioSystem.getAudioInputStream(gameAudio);
        gameClip = AudioSystem.getClip();
        gameClip.open(audioInputStream);
    }


    private void loadMenuMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File menuAudio = new File("src/music/right-thurr-official-instrumental.wav");
        AudioInputStream audioInputStream = null;
        audioInputStream = AudioSystem.getAudioInputStream(menuAudio);
        menuClip = AudioSystem.getClip();
        menuClip.open(audioInputStream);
    }


    private void displayMainMenu() {
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, canvasWidth);
        StdDraw.setYscale(0, canvasHeight);

        boolean running = true;

        while (running) {
            StdDraw.clear();
            drawMainMenu();
            StdDraw.show();
            StdDraw.pause(pauseTime); // Pause to prevent busy waiting

            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                switch (Character.toUpperCase(choice)) {
                    case 'N':
                        long seed = getSeed();
                        stopMenuMusic();
                        if (seed != -1) {
                            instructionScreen = new InstructionScreen(this, seed);
                            instructionScreen.showInstructions();
                        }
                        break;
                    case 'L':
                        String filePath = "src/core/save-file.txt";
                        BufferedReader reader = null;

                        try {
                            reader = new BufferedReader(new FileReader(filePath));
                            String line1 = reader.readLine();
                            String line2 = reader.readLine();
                            String line3 = reader.readLine();
                            String line4 = reader.readLine();
                            String line5 = reader.readLine();

                            stopMenuMusic();

                            assert line1 != null;
                            int avatarX = Integer.parseInt(line1);
                            assert line2 != null;
                            int avatarY = Integer.parseInt(line2);
                            assert line3 != null;
                            int enemyX = Integer.parseInt(line3);
                            assert line4 != null;
                            int enemyY = Integer.parseInt(line4);
                            assert line5 != null;
                            long sd = Long.parseLong(line5);

                            new GameGenerator(this, sd, avatarX, avatarY, enemyX, enemyY);
                            break;

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (reader != null) {
                                    reader.close();
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        break;

                    case 'Q':
                        System.exit(1);
                    default:
                        // Invalid choice
                        running = false;
                        break;
                }
            }
        }
    }

    private void drawMainMenu() {
        StdDraw.setFont(new Font("Arial", Font.BOLD, fontSize));
        StdDraw.text(menuX1, menuY1, "CS61B: The Game");
        StdDraw.text(menuX1, menuY2, "N: New Game");
        StdDraw.text(menuX1, menuY3, "L: Load Game");
        StdDraw.text(menuX1, menuY4, "Q: Quit");
    }

    private long getSeed() {
        StringBuilder seedBuilder = new StringBuilder();
        StdDraw.clear(); // Clear the screen before displaying prompt
        drawMainMenu(); // Redraw the main menu
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
                drawMainMenu();
                StdDraw.text(menuX1, menuY5, "Enter seed (end with 'S'):");
                StdDraw.text(menuX1, menuY6, seedBuilder.toString());
                StdDraw.show();
            }
        }

        return Long.parseLong(seedBuilder.toString());
    }
}

