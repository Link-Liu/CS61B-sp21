package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.Vector2;
import byow.TileEngine.Tileset;

public class Move {
    TETile [][] world;
    Vector2 userPos;
    Vector2 LEFT = new Vector2(-1,0);
    Vector2 RIGHT = new Vector2(1,0);
    Vector2 UP = new Vector2(0, 1);
    Vector2 DOWN = new Vector2(0, -1);
    Engine engine;
    TERenderer ter;

    public Move(TETile [][] world,TERenderer ter) {
        this.world = world;
        this.ter = ter;
        engine = new Engine();
        findUser();
        startMove();
    }

    private void startMove() {
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            System.out.println("hi");
            char key = inputSource.getNextKey();
            if (key == 'W') {
                moveHelper(UP);
            } else if (key == 'D') {
                moveHelper(RIGHT);
            } else if (key == 'A') {
                moveHelper(LEFT);
            } else if (key == 'S') {
                moveHelper(DOWN);
            }
        }
    }

    public TETile[][] moveHelper(Vector2 direction) {
        if (isWall(direction.add(userPos))) {
            return world;
        } else {
            Vector2 dist = direction.add(userPos);
            world[userPos.x][userPos.y] = Tileset.MYFLOOR;
            world[dist.x][dist.y] = Tileset.USER;
            userPos = dist;
            ter.renderFrame(world);
            return world;
        }
    }

    public void findUser() {
        for (int i = 0; i < Engine.WIDTH; ++i) {
            for (int j = 0; j < Engine.HEIGHT; ++j) {
                if (world[i][j] == Tileset.USER) {
                    userPos = new Vector2(i, j);
                    return;
                }
            }
        }
    }

    private boolean isWall(Vector2 pos) {
        return world[pos.x][pos.y] == Tileset.MYWALL;
    }


}
