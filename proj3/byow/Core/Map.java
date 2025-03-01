package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.*;

public class Map {
    int width;
    int height;
    private final Random RANDOM;
    ArrayList<Vector2> POSSIBLE_DIR; //上下左右的方向向量
    private int currentRegionIndex;
    private static final int BLOCK = -1; // TEliset.Nothing
    private final int[][] _regions; //记录地砖属性
    private int roomRegionCount = 0; // 记录房间区域的数量
    private static final int MAXTOZERO = 100; // 0-100用于生成概率
    private static final int HOWSIMPLE = 80; // 控制迷宫死路，越小越复杂
    private WorldRenderer worldRenderer;

    /*Map类的构造函数*/
    Map(int width, int height, String seed, int roomWanted, int howWind) { //width is x, hight is y
        POSSIBLE_DIR = new ArrayList<>();
        POSSIBLE_DIR.add(new Vector2(1, 0));
        POSSIBLE_DIR.add(new Vector2(0, 1));
        POSSIBLE_DIR.add(new Vector2(0, -1));
        POSSIBLE_DIR.add(new Vector2(-1, 0)); //初始化
        this.width = width;
        this.height = height;
        this._regions = new int[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                _regions[i][j] = BLOCK;
            }
        }
        this.RANDOM = new Random(seed.hashCode());
        worldRenderer = new WorldRenderer(width, height);
        addRoom(roomWanted, 2, worldRenderer);
        fillWithMaze(worldRenderer, howWind);
        connectRegions(worldRenderer);
        removeDeadEnds(worldRenderer);
        putWall(worldRenderer);
        addUser(worldRenderer);
    }

    /*生成[min,Max)的随机数*/
    private int myRandom(int min, int max) { //不包含max 包含min
        return RANDOM.nextInt(max - min) + min;
    }

    /*为map加上房间*/
    private void addRoom(int numTry, int roomExtraSize, WorldRenderer wr) {
        ArrayList<Room> rooms = new ArrayList<>(); // 添加room类
        for (int i = 0; i < numTry; i++) {
            int size = myRandom(1, 3 + roomExtraSize) * 2 + 1; // 正方形
            int rectangularity = myRandom(0, 1 + size / 2) * 2;  // 变成长方形
            int w = size, h = size; //width and height for room
            if (0 == myRandom(0, MAXTOZERO) % 2) { // 随机长高或变胖
                w += rectangularity;
            } else {
                h += rectangularity;
            }
            // x,y 代表房间左下角的坐标
            int x = myRandom(0, (width - w) / 2) * 2 + 1;
            int y = myRandom(0, (height - h) / 2) * 2 + 1;
            boolean overlaps = false;  // a flag when true the room will not be added
            Room room = new Room(x, y, w, h);
            for (Room other : rooms) {
                if (room.isOverlap(other)) {
                    overlaps = true;
                    break;
                }
            }
            if (overlaps) {
                continue;  //生成下一个房间
            }
            rooms.add(room);
            for (int x1 = room.x; x1 < room.x + room.w; x1++) {
                for (int y1 = room.y; y1 < room.y + room.h; y1++) {
                    _regions[x1][y1] = currentRegionIndex;
                }
            }
            ++currentRegionIndex;
            wr.roomRender(room);
        }
        this.roomRegionCount = rooms.size();
    }

    /* 使用洪水填充生成散点，为之后连接成迷宫准备 */
    public void fillWithMaze(WorldRenderer wr, int howWind) {
        for (int x = 1; x < width; x += 2) {
            for (int y = 1; y < height; y += 2) {
                Vector2 pos = new Vector2(x, y);
                if (isNothing(pos, wr)) {
                    growMaze(pos, wr, howWind);
                }
            }
        }
    }

    /*判断地板属性为NOTHING*/
    private boolean isNothing(Vector2 pos, WorldRenderer wr) {
        return wr.world[pos.x][pos.y] == Tileset.NOTHING || wr.world[pos.x][pos.y] == Tileset.MYWALL;
    }
    /*开始填充附近的点*/
    private void growMaze(Vector2 start, WorldRenderer wr, int windingPercent) {
        ArrayList<Vector2> cells = new ArrayList<>();
        Vector2 lastDir = new Vector2(0, 0);
        int region = currentRegionIndex; // 使用当前索引
        currentRegionIndex++; // 然后递增
        _regions[start.x][start.y] = region;
        wr.carve(start);

        cells.add(start);
        while (!cells.isEmpty()) {
            Vector2 cell = cells.get(cells.size() - 1);
            //  检查可生长的方向
            ArrayList<Vector2> unmadeCells = new ArrayList<>();
            for (Vector2 dir : POSSIBLE_DIR) {
                if (canCarve(cell, dir, wr)) {
                    unmadeCells.add(dir);
                }
            }

            if (!unmadeCells.isEmpty()) {
                Vector2 dir;
                if ((unmadeCells.contains(lastDir)) && (myRandom(0, MAXTOZERO) > windingPercent)) {
                    dir = lastDir;
                } else {
                    //  换个方向生长
                    dir = unmadeCells.get(myRandom(0, unmadeCells.size()));
                }
                _regions[cell.add(dir).x][cell.add(dir).y] = currentRegionIndex; // 更新_regions数组
                _regions[cell.add(dir.mul(2)).x][cell.add(dir.mul(2)).y] = currentRegionIndex; // 更新_regions数组
                //  记录迷宫
                wr.carve(cell.add(dir));
                wr.carve(cell.add(dir.mul(2)));
                // 下一个要生长点
                cells.add(cell.add(dir.mul(2)));
                lastDir = dir;
            } else {
                //  没有任何一个可生长方向，此路径结束
                cells.remove(cells.size() - 1);
                lastDir = new Vector2(0, 0);
            }
        }

    }
    /*检查两个点间是否可以加点*/
    private boolean canCarve(Vector2 cell, Vector2 dir, WorldRenderer wr) {
        // 假设dir是一个二维向量，表示方向和步长，例如(2, 0)代表向右移动两格
        Vector2 middleCell = cell.add(dir.div(2)); // 中间位置
        Vector2 targetCell = cell.add(dir);     // 目标位置

        // 边界检查，确保middleCell和targetCell在地图范围内
        if (isOutOfBounds(middleCell) || isOutOfBounds(targetCell)) {
            return false;
        }
        // 确保middleCell和targetCell都是墙（未被雕刻）
        if (!isNothing(middleCell, wr) || !isNothing(targetCell, wr)) {
            return false;
        }
        // 如果以上条件都满足，则可以在该方向上雕刻
        return true;
    }

    /*检查是否超出地图*/
    private boolean isOutOfBounds(Vector2 pos) {
        // 判断pos是否超出了地图边界
        return pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height;
    }

    /*生成图*/
    private void connectRegions(WorldRenderer wr) {
        // 找出所有可连接两个(或以上）空间的墙块
        TreeMap<Vector2, ArrayList<Integer>> connectorRegions = new TreeMap<>();
        for (int i = 1; i < width - 1; ++i) {
            for (int j = 1; j < height - 1; ++j) {
                Vector2 pos = new Vector2(i, j);
                if (!isNothing(pos, wr)) {
                    continue; // 跳过不是墙的地方
                }

                ArrayList<Integer> regions = new ArrayList<>();
                for (Vector2 dir : POSSIBLE_DIR) {
                    int region = _regions[i + dir.x][j + dir.y];
                    if (region != BLOCK) {
                        regions.add(region);
                    }
                }

                if (regions.size() >= 2) {
                    connectorRegions.put(pos, new ArrayList<>(regions)); // 存储副本
                }
            }
        }

        // 将连接器按键排序以确保顺序一致
        ArrayList<Vector2> connectors = new ArrayList<>(connectorRegions.keySet());
        ArrayList<Integer> merged = new ArrayList<>();
        ArrayList<Integer> openRegions = new ArrayList<>();
        for (int i = 0; i <= currentRegionIndex; ++i) {
            merged.add(i);
            openRegions.add(i);
        }

        while (openRegions.size() > 1 && !connectors.isEmpty()) {
            int connectorIndex = myRandom(0, connectors.size());
            Vector2 connector = connectors.get(connectorIndex);
            wr.carve(connector);
            _regions[connector.x][connector.y] = merged.get(openRegions.get(0)); // 更新区域索引

            ArrayList<Integer> sources = new ArrayList<>(connectorRegions.get(connector)); // 使用副本
            for (int i = 0; i < sources.size(); ++i) {
                sources.set(i, merged.get(sources.get(i)));
            }
            int dest = sources.get(0);
            sources.remove(0);

            // 合并所有源区域到目标区域
            for (int i = 0; i < merged.size(); ++i) {
                if (sources.contains(merged.get(i))) {
                    merged.set(i, dest);
                }
            }

            // 移除已合并的区域
            openRegions.removeAll(sources);
            // 更新连接器列表
            connectors.removeIf(pos -> needRemove(merged, connectorRegions, connector, pos));
        }
    }

    private static boolean needRemove(ArrayList<Integer> merged,
                                      TreeMap<Vector2, ArrayList<Integer>> connectorRegions,
                                      Vector2 connector,
                                      Vector2 pos) {
        // 移除相邻的连接器避免过于密集
        if (connector.sub(pos).sqrMagnitude() < 2) {
            return true;
        }

        ArrayList<Integer> regions = connectorRegions.get(pos);
        if (regions == null) {
            return true;
        }

        HashSet<Integer> mergedRegions = new HashSet<>();
        for (int region : regions) {
            mergedRegions.add(merged.get(region));
        }
        return mergedRegions.size() <= 1;
    }
    /*移除死路*/
    private void removeDeadEnds(WorldRenderer wr) {
        boolean done = false;

        while (!done) {
            done = true;

            for (int i = 1; i < width - 1; ++i) {
                for (int j = 1; j < height - 1; ++j) {
                    Vector2 pos = new Vector2(i, j);
                    if (isNothing(pos, wr)) {
                        continue;
                    }
                    // 新增：跳过属于房间区域的块
                    if (_regions[i][j] < roomRegionCount) {
                        continue;
                    }
                    int exists = 0;
                    for (Vector2 dir : POSSIBLE_DIR) {
                        Vector2 neighbor = new Vector2(i + dir.x, j + dir.y);
                        if (!isNothing(neighbor, wr)) {
                            exists++;
                        }
                    }
                    if (exists == 1) {
                        // HOWSIMPLE的概率删除该死路，保留部分复杂性
                        if (myRandom(0, MAXTOZERO) > HOWSIMPLE) {
                            done = false;
                            _regions[i][j] = BLOCK;
                            wr.world[i][j] = Tileset.NOTHING;
                        }
                    }
                }
            }
        }
    }
    /*生成墙*/
    private void putWall(WorldRenderer wr) {
        ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
        neighbors.addAll(POSSIBLE_DIR);
        neighbors.add(new Vector2(1, 1));
        neighbors.add(new Vector2(1, -1));
        neighbors.add(new Vector2(-1, -1));
        neighbors.add(new Vector2(-1, 0));

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Vector2 pos = new Vector2(i, j);
                if (!isNothing(pos, wr)) {
                    continue;
                }
                int exists = 0;
                for (Vector2 dir : neighbors) {
                    int neighborX = i + dir.x;
                    int neighborY = j + dir.y;
                    // 边界检查
                    if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                        Vector2 neighbor = new Vector2(neighborX, neighborY);
                        if (!isNothing(neighbor, wr)) {
                            exists++;
                        }
                    }
                }
                if (exists != 0) {
                    wr.putWall(pos);
                }
            }
        }
    }

    public void addUser(WorldRenderer wr) {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (wr.world[i][j] == Tileset.MYFLOOR) {
                    wr.putUser(new Vector2(i, j));
                    return;
                }
            }
        }
    }


    public TETile[][] getMap() {
        return this.worldRenderer.world;
    }

    public void showMap() {
        worldRenderer.startWorldRender();
        worldRenderer.showWorld();
    }
}
