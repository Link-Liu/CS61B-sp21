package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    private static void fillwithBlankTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void draw_line(TETile[][] world, int length, Position p, TETile tileset) {
        for (int i = 0; i < length; i += 1) {
            world[p.x + i][p.y] = tileset;
        }
    }
    private static void draw_hex(TETile[][] world, int size, Position p, TETile tileset) {
        draw_hex_helper(world, size, p, tileset, size, size);
    }

    private static void draw_hex_helper(TETile[][] world, int size, Position p, TETile tileset, int space, int down) {
        Position start = p.move(space, 0);
        draw_line(world, size, start, tileset);
        if (down != 1) {
            Position nextP = p.move(0, 1);
            draw_hex_helper(world, size + 2, nextP, tileset, space - 1, down - 1);
        }
        Position end = p.move(space, down * 2 - 1);
        draw_line(world, size, end, tileset);
    }

    private static void fillWithHexTiles(TETile[][] world, int size, Position p, TETile tileset) {
        fill_helper(world, size, p, tileset, size);
    }

    private static void fill_helper(TETile[][] world, int size, Position p, TETile tileset, int times) {
        for (int i = 0 ; i < times ; i += 1) {
            draw_hex(world, size, p.move(0, size * 2 * i), tileset);
        }
    }

    private static class Position {
        int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private Position move(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }

    }


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillwithBlankTiles(world);
        Position p = new Position(1, 32);
        fillWithHexTiles(world, 3, p, Tileset.FLOWER);
        ter.renderFrame(world);
    }

}


