package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class WorldRenderer {
    int WIDTH, HEIGHT;
    TETile[][] world;
    TERenderer ter;
    TETile floor = Tileset.MYFLOOR;
    TETile wall = Tileset.MYWALL;
    TETile user = Tileset.USER;
    Vector2 userDir;

    WorldRenderer(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        world = new TETile[WIDTH][HEIGHT];
        fillwithBlankTiles(world);
    }

    private void fillwithBlankTiles(TETile[][] worldToFill) {
        for (int x = 0; x < this.WIDTH; x += 1) {
            for (int y = 0; y < this.HEIGHT; y += 1) {
                worldToFill[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void showWorld() {
        ter.renderFrame(this.world);
    }

    public void roomRender(Room room) {
        int x = room.x;
        int y = room.y;
        int w = room.w;
        int h = room.h;
        for (int k = x; k < x + w; ++k) {
            for (int j = y; j < y + h; ++j) {
                this.world[k][j] = floor;
            }
        }
    }

    public void carve(Vector2 place) {
        world[place.x][place.y] = floor;
    }

    public void putWall(Vector2 place) {
        world[place.x][place.y] = wall;
    }

    public void startWorldRender() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
    }

    public void putUser(Vector2 place) {
        world[place.x][place.y] = user;
    }

}
