package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Love {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    private static void fillWithBlankTiles(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void love(TETile[][] world, int size, Position center, TETile tile) {
        boolean[][] visited = new boolean[WIDTH][HEIGHT];

        for (double t = 0.0; t <= 360.0; t += 0.5) {
            double radians = Math.toRadians(t);
            int x = (int) Math.round(size * 16 * Math.pow(Math.sin(radians), 3));
            int y = (int) Math.round(size * (
                    13 * Math.cos(radians)
                            - 5 * Math.cos(2 * radians)
                            - 2 * Math.cos(3 * radians)
                            - Math.cos(4 * radians)
            ));

            int px = center.x + x;
            int py = center.y + y; // 修改Y轴方向

            if (px >= 0 && px < WIDTH && py >= 0 && py < HEIGHT) {
                drawLine(world, center.x, center.y, px, py, tile, visited);
            }
        }
    }

    private static void drawLine(TETile[][] world, int x1, int y1, int x2, int y2, TETile tile, boolean[][] visited) {
        int dx = Math.abs(x2 - x1);
        int dy = -Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;

        while (true) {
            if (!visited[x1][y1]) {
                world[x1][y1] = tile;
                visited[x1][y1] = true;
            }
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x1 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private static class Position {
        int x, y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithBlankTiles(world);

        Position center = new Position(30, 30); // 中心点
        love(world, 1, center, Tileset.FLOWER);  // 调整size为1

        ter.renderFrame(world);
    }
}



