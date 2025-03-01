package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Menu {
    private static final int FACTOR = 16;
    int WIGTH;
    int HEIGHT;
    public static final int FONTSIZE50 = 50;
    public static final int FONTSIZE30 = 30;
    private static final int MENUWIGHT = 60;
    private static final int MENUHEIGHT = 55;
    private static final int DIVIDER = 14;
    Menu(int wigth, int height) {
        WIGTH = wigth;
        HEIGHT = height;
        stdInit(wigth, height);
    }

    public void startMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, FONTSIZE50);
        StdDraw.setFont(font);
        String title = "LCY: THE GAME";
        StdDraw.text(WIGTH / 2, HEIGHT * 3 / 4, title);
        font = new Font("Monaco", Font.BOLD, FONTSIZE30);
        StdDraw.setFont(font);
        String subtile1 = "New Game (N)";
        String subtile2 = "Load Game (L)";
        String subtile3 = "Ouit (Q)";
        StdDraw.text(WIGTH / 2, HEIGHT * 7 / DIVIDER, subtile1);
        StdDraw.text(WIGTH / 2, HEIGHT * 6 / DIVIDER, subtile2);
        StdDraw.text(WIGTH / 2, HEIGHT * 5 / DIVIDER, subtile3);

        StdDraw.show();


    }

    public static void main(String[] arg) {
        Menu m = new Menu(MENUWIGHT, MENUHEIGHT);
        m.startMenu();
        m.gameModel();
    }

    public void gameModel() {
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'N') {
                Save s = new Save();
                s.clean();
                String seed = getUserSeed();
                s.write("n" + seed);
                Map m = new Map(Engine.WIDTH, Engine.HEIGHT, seed, Engine.ROOMWANTED, Engine.HOWWIND);
                TETile[][] world = m.getMap();
                String action = gaming(world);
                dicidedSave(action);
                break;
            }
            if (c == 'L') {
                Engine e = new Engine();
                TETile[][] world = e.interactWithInputString("l");
                String action = gaming(world);
                dicidedSave(action);
                break;
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
        Font font = new Font("Monaco", Font.BOLD, FONTSIZE50);
        StdDraw.setFont(font);
        String title = "Please enter your seed";
        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT * 3 / 4, title);
        StdDraw.show();
        String seed = "";
        while (inputSource.possibleNextInput()) {
            StdDraw.clear(Color.BLACK);
            StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT * 3 / 4, title);
            char c = inputSource.getNextKey();
            if (!Character.isDigit(c)) {
                break;
            }
            seed += c;
            StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT * 1 / 2, seed);
            StdDraw.show();
            StdDraw.clear(Color.BLACK);
        }
        return seed;
    }

    public void stdInit(int wigth, int height) {
        StdDraw.setCanvasSize(wigth * FACTOR, height * FACTOR);
        StdDraw.setXscale(0, wigth);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public String gaming(TETile[][] world) {
        TERenderer ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        ter.renderFrame(world);
        Move m = new Move(world, ter);
        return m.getAction();
    }

    public boolean dicidedSave(String input) {
        Save s = new Save();
        if (input.substring(input.length() - 2).equals(":q")) {
            s.write(input.substring(0, input.length() - 2));
            return true;
        }
        return false;
    }

}
