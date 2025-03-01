package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Move {
    TETile[][] world;
    Vector2 userPos;
    Vector2 LEFT = new Vector2(-1,0);
    Vector2 RIGHT = new Vector2(1,0);
    Vector2 UP = new Vector2(0, 1);
    Vector2 DOWN = new Vector2(0, -1);
    Engine engine;
    TERenderer ter;
    String action;

    public Move(TETile[][] world,TERenderer ter) {
        this.world = world;
        this.ter = ter;
        action = "";
        engine = new Engine();
        findUser();
        startMove();
    }

    public Move(TETile[][] world, String key) {
        this.world = world;
        findUser();
        startMove(key.toUpperCase());
    }

    private void startMove() {
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            char key = inputSource.getNextKey();
            if (key == ':') {
                if (inputSource.possibleNextInput()) {
                    char key2 = inputSource.getNextKey();
                    if (key2 == 'Q') {
                        action += ":q";
                        break;
                    }
                }
            }
            charMoveHelper(key);
            ter.renderFrame(world);
        }
    }

    private TETile[][] startMove(String input) {
        for (char c : input.toCharArray()) {
            if (c != ':') {
                charMoveHelper(c);
            }
        }
        return getWorld();
    }

    private void charMoveHelper(char key) {
        if (key == 'W') {
            moveHelper(UP);
            action += key;
        } else if (key == 'D') {
            moveHelper(RIGHT);
            action += key;
        } else if (key == 'A') {
            moveHelper(LEFT);
            action += key;
        } else if (key == 'S') {
            moveHelper(DOWN);
            action += key;
        }
    }

    public void moveHelper(Vector2 direction) {
        if (!isWall(direction.add(userPos))) {
            Vector2 dist = direction.add(userPos);
            world[userPos.x][userPos.y] = Tileset.MYFLOOR;
            world[dist.x][dist.y] = Tileset.USER;
            userPos = dist;
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

    public TETile[][] getWorld() {
        return world;
    }

    public String getAction() {
        return action;
    }


}
