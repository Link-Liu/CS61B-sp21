package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Menu {
    int WIGTH;
    int HEIGHT;
    Menu(int wigth, int height) {
        WIGTH = wigth;
        HEIGHT = height;
        stdInit(wigth, height);
    }

    public void startMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        String title = "LCY: THE GAME";
        StdDraw.text(WIGTH / 2, HEIGHT * 3/4, title);
        font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        String subtile1 = "New Game (N)";
        String subtile2 = "Load Game (L)";
        String subtile3 = "Ouit (Q)";
        StdDraw.text(WIGTH / 2, HEIGHT * 7/14, subtile1);
        StdDraw.text(WIGTH / 2, HEIGHT * 6/14, subtile2);
        StdDraw.text(WIGTH / 2, HEIGHT * 5/14, subtile3);

        StdDraw.show();


    }

    public static void main(String[] arg) {
        Menu m = new Menu(60,55);
        m.startMenu();
        m.gameModel();
    }

    public void gameModel() {
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'N') {
                String seed = getUserSeed();
                TETile[][] world = new Map(105,55,"seed", 140 , 75).getMap();
                gaming(world);
            }
            if (c == 'Q') {
                System.out.println("done.");
                break;
            }
        }
    }

    private String getUserSeed() {
        stdInit(Engine.WIDTH, Engine.HEIGHT);
        InputSource inputSource = new KeyboardInputSource();
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        String title = "Please enter your seed";
        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT * 3/4, title);
        StdDraw.show();
        String seed = "";
        while (inputSource.possibleNextInput()) {
            StdDraw.clear(Color.BLACK);
            StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT * 3/4, title);
            char c = inputSource.getNextKey();
            if(!Character.isDigit(c)) {
                break;
            }
            seed += c;
            StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT * 1/2, seed);
            StdDraw.show();
            StdDraw.clear(Color.BLACK);
            }
        return seed;
    }

    public void stdInit(int wigth, int height) {
        StdDraw.setCanvasSize(wigth * 16, height * 16);
        StdDraw.setXscale(0, wigth);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void gaming(TETile[][] world) {
        TERenderer ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        ter.renderFrame(world);
        new Move(world, ter);
    }
}
