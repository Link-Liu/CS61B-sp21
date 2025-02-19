package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.Map.Vector2;

public class WorldRenderer {
    int WIDTH, HEIGHT;
    TETile[][] world;
    TERenderer ter;
    TETile floor = Tileset.FLOOR;

    WorldRenderer(int width, int height) {
        ter = new TERenderer();
        ter.initialize(width, height);
        this.WIDTH = width;
        this.HEIGHT = height;
        world = new TETile[WIDTH][HEIGHT];
        fillwithBlankTiles(world);
    }

    private void fillwithBlankTiles(TETile[][] world) {
        for (int x = 0; x < this.WIDTH; x += 1) {
            for (int y = 0; y < this.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
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
        for (int k = x; k < x + w; ++k)
        {
            for (int j = y; j < y + h; ++j)
            {
                this.world[k][j] = floor;
            }
        }
    }

    public void carve(Vector2 place) {
        world[place.x][place.y] = floor;
    }

}
