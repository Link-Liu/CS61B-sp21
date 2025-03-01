package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 105;
    public static final int HEIGHT = 55;
    public static final int MENU_WIDTH = 60;
    public static final int ROOMWANTED = 140;
    public static final int HOWWIND = 75;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Menu m = new Menu(MENU_WIDTH, HEIGHT);
        m.startMenu();
        m.gameModel();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TETile[][] finalWorldFrame;
        if (input.startsWith("n")) {
            finalWorldFrame = handalN(input);
            Save s = new Save();
            s.clean();
            if (input.substring(input.length() - 2).equals(":q")) {
                s.write(input.substring(0, input.length() - 2));
            }
        } else {
            finalWorldFrame = handalL(input);
        }
        return finalWorldFrame;
    }

    public void renderWorldFrame(TETile[][] world) {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }

    public int getSeed(String input) {
        int i = 1; // pass "s"
        while (i < input.length()) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                i++;
                continue;
            }
            break;
        }
        return i;
    }

    public TETile[][] handalN(String input) {
        int indexToSplit = getSeed(input);
        String seed = input.substring(1, indexToSplit);
        String key = input.substring(indexToSplit);
        TETile[][] world = new Map(WIDTH, HEIGHT, seed, ROOMWANTED, HOWWIND).getMap();
        Move move = new Move(world, key);
        return move.getWorld();
    }

    public TETile[][] handalL(String input) {
        Save s = new Save();
        String resetSeed = s.read();
        TETile[][] world = handalN(resetSeed.substring(0, resetSeed.length()));
        Move move = new Move(world, input);
        if (input.length() >= 2) {
            if (input.substring(input.length() - 2).equals(":q")) {
                s.write(resetSeed.substring(1, resetSeed.length() - 2));
            }
        }
        return move.getWorld();
    }
}
