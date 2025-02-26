package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            int index = rand.nextInt(CHARACTERS.length);
            char ch = CHARACTERS[index];
            result += ch;
        }
        return result;
    }

    public void drawFrame(String s, int model) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        if (gameOver) {
            String gameInfo = "Game Over! You made it to round:" + round;
            StdDraw.text(width/2, height/2, gameInfo);
        } else {
            font = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(font);
            String roundInfo = "Round: " + this.round;
            double margin = 1; // 设置一个适当的边距值
            StdDraw.textLeft(margin, height - margin, roundInfo);
            String worldInfo = ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)];
            StdDraw.textRight(width - margin, height - margin, worldInfo);
            if (model == 0) {
                StdDraw.text(width / 2, height - margin, "Watch!");
            } else {
                StdDraw.text(width / 2, height - margin, "Type!");
            }
            StdDraw.line(0, height - 2 * margin, width, height - 2 * margin);
            font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.text((double) width / 2, (double) height / 2, s);
        }
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (char c : letters.toCharArray()) {
            StdDraw.clear(Color.BLACK);
            drawFrame(Character.toString(c), 0);
            StdDraw.pause(1000);
            StdDraw.show();
            drawFrame("", 0);
            StdDraw.pause(500);
            StdDraw.show();
        }

    }

    public String solicitNCharsInput(int n) {
        String input = "";
        for (int i = 0; i < n; i++) {
            while (!StdDraw.hasNextKeyTyped()) {
                // 等待用户输入
            }
            char ch = StdDraw.nextKeyTyped();
            input += ch;
            drawFrame(input, 1); // 更新显示的内容为当前收集到的所有字符
            StdDraw.show(); // 刷新画面以显示最新的输入
        }
        return input;
    }

    public void startGame() {
        while (!gameOver) {
            String result = generateRandomString(round);
            flashSequence(result);
            String input = solicitNCharsInput(round);
            if (input.equals(result)) {
                round++;
            } else {
                gameOver = true;
                drawFrame(input, 3);
                StdDraw.show();
            }
        }
    }

}
