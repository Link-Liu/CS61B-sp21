package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.Tileset;

import java.sql.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

public class Map {
    int width;
    int height;
    private final Random RANDOM;
    ArrayList<Vector2> POSSIBLE_DIR;
    int currentRegionIndex;

    /*Map类的构造函数*/
    Map(int width, int height, String seed) { //width is x, hight is y
        POSSIBLE_DIR = new ArrayList<Vector2>();
        POSSIBLE_DIR.add(new Vector2(1,0));
        POSSIBLE_DIR.add(new Vector2(0,1));
        POSSIBLE_DIR.add(new Vector2(0,-1));
        POSSIBLE_DIR.add(new Vector2(-1,0));
        this.width = width;
        this.height = height;
        this.RANDOM = new Random(seed.hashCode());
        WorldRenderer wr = new WorldRenderer(width, height);
        addRoom(85, 5 , wr);
        fillWithMaze(wr);
        wr.showWorld();
    }

    private int myRandom(int min, int max) { //不包含max 包含min
        return this.RANDOM.nextInt(max - min) + min;
    }

    private void addRoom(int numTry, int roomExtraSize, WorldRenderer wr) {
        ArrayList<Room> rooms = new ArrayList<>(); // 添加room类
        for (int i = 0; i < numTry; i++) {
            int size = myRandom(1, 3 + roomExtraSize) * 2 + 1;
            int rectangularity = myRandom(0, 1 + size / 2) * 2;
            int w = size, h = size; //width and height for room
            if (0 == myRandom(0,100) % 2) {
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
                if (room.IsOverlap(other)) {
                    overlaps = true;
                    break;
                }
            }
            if (overlaps) {
                continue;  //生成下一个房间
            }
            rooms.add(room);
            ++currentRegionIndex;
            wr.roomRender(room);
        }
    }

    public void fillWithMaze(WorldRenderer wr) {
        for (int x = 1; x < width; x += 2)
        {
            for (int y = 1; y < height; y += 2)
            {
                Vector2 pos = new Vector2(x, y);
                if (IsNothing(pos, wr))
                {
                    GrowMaze(pos, wr, 75);
                }
            }
        }
    }

    private boolean IsNothing(Vector2 pos, WorldRenderer wr) {
        return wr.world[pos.x][pos.y] == Tileset.NOTHING;
    }

    private void GrowMaze(Vector2 start, WorldRenderer wr, int  windingPercent) {
        ArrayList<Vector2> cells = new ArrayList<Vector2>();
        Vector2 lastDir = new Vector2(0, 0) ;
        ++currentRegionIndex;
        wr.carve(start);

        cells.add(start);
        while(!cells.isEmpty())
        {
            Vector2 cell = cells.get(cells.size() - 1);
            //  检查可生长的方向
            ArrayList<Vector2> unmadeCells = new ArrayList<Vector2>();
            for (Vector2 dir : POSSIBLE_DIR) {
                if (CanCarve(cell, dir, wr))
                {
                    unmadeCells.add(dir);
                }
            }

            if(!unmadeCells.isEmpty())
            {
                Vector2 dir;
                if((unmadeCells.contains(lastDir)) && (myRandom(0, 100) > windingPercent))
                {
                    dir = lastDir;
                }
                else
                {
                    //  换个方向生长
                    dir = unmadeCells.get(myRandom(0, unmadeCells.size()));
                }

                //  TODO: 记录迷宫
                wr.carve(cell.add(dir));
                wr.carve(cell.add(dir.mul(2)));

                cells.add(cell.add(dir.mul(2)));
                lastDir = dir;
            }
            else
            {
                //  没有任何一个可生长方向，此路径结束
                cells.remove(cells.size() - 1);
                lastDir = new Vector2(0, 0);
            }
        }

    }

    private boolean CanCarve(Vector2 cell, Vector2 dir, WorldRenderer wr)
    {
        // 假设dir是一个二维向量，表示方向和步长，例如(2, 0)代表向右移动两格
        Vector2 middleCell = cell.add(dir.div(2)) ; // 中间位置
        Vector2 targetCell = cell.add(dir);     // 目标位置

        // 边界检查，确保middleCell和targetCell在地图范围内
        if (IsOutOfBounds(middleCell) || IsOutOfBounds(targetCell))
        {
            return false;
        }
        // 确保middleCell和targetCell都是墙（未被雕刻）
        if (!IsNothing(middleCell,wr) || ! IsNothing(targetCell, wr))
        {
            return false;
        }
        // 如果以上条件都满足，则可以在该方向上雕刻
        return true;
    }

    // 示例辅助函数
    private boolean IsOutOfBounds(Vector2 pos)
    {
        // 判断pos是否超出了地图边界
        return pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height;
    }

    int ConnectRegions(WorldRenderer wr)
    {
        //  找出所有可连接两个(或以上）空间的墙块（其实最多只可能是两个对吧）
        HashMap<Vector2, ArrayList<Integer>> connectorRegions = new HashMap<Vector2, ArrayList<Integer>>();
        for (int i = 1; i < width - 1; ++i)
        {
            for (int j = 1; j < height - 1; ++j)
            {
                if (!IsNothing(new Vector2(i, j), wr))
                {
                    continue;
                }
                ArrayList<Integer> regions = new ArrayList<Integer>();
                for(Vector2 dir : POSSIBLE_DIR) {
                    int region = _regions[i + dir.x, j + dir.y];
                    if ((BLOCK != region))
                    {
                        regions.add(region);
                    }
                }

                if (regions.size() >= 2)
                {
                    connectorRegions.put(new Vector2(i, j), regions);
                }
            }
        }

        ArrayList<Vector2> connectors = new ArrayList<>();
        connectors.addAll(connectorRegions.keySet());
        //observeValue = connectors.Count;

        ArrayList<Integer> merged = new ArrayList<Integer>();
        ArrayList<Integer> openRegions = new ArrayList<Integer>();
        for (int i = 0; i <= currentRegionIndex; ++i)
        {
            merged.add(i);
            openRegions.add(i);
        }

        while (openRegions.size() > 1)
        {
            Vector2 connector = connectors.get(myRandom(0, connectors.size()));
            wr.carve(connector);

            ArrayList<Integer> sources = connectorRegions.get(connector);
            for (int i = 0; i < sources.size(); ++i)
            {
                sources.set(i, merged.get(sources.get(i)));
            }
            int dest = sources.get(0);
            sources.remove(0);

            for (int i = 0; i <= currentRegionIndex; ++i)
            {
                if (sources.contains(merged.get(i))) {
                    merged.set(i, dest);
                }
            }

            for (int s : sources) {
                openRegions.removeIf(value -> (value == s));
            }
            connectors.removeIf(pos -> NeedRemove(merged, connectorRegions, connector, pos));
        }
    }

    private static boolean NeedRemove(ArrayList<Integer> merged, HashMap<Vector2, ArrayList<Integer>> connectorRegions, Vector2 connector, Vector2 pos) {
        {
            if ((connector.sub(pos)).SqrMagnitude() < 1.5f) {
                return true;
            }
            ArrayList<Integer> tempList = connectorRegions.get(pos);
            for (int i = 0; i < tempList.size(); ++i)
            {
                tempList.set(i, merged.get(tempList.get(i)));
            }
            HashSet<Integer> set = new HashSet<Integer>(tempList);
            if (set.size() > 1)
            {
                return false;
            }
            return true;
        }
    }




    public class Vector2 {
        int x;
        int y;

        public Vector2(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector2 add(Vector2 v2) {
            return new Vector2(x + v2.x, y + v2.y);
        }

        public Vector2 sub(Vector2 v) {
            return new Vector2(x - v.x, y - v.y);
        }

        private Vector2 div(int n) {
            return new Vector2(x / n, y / n);
        }

        private Vector2 mul(int n) {
            return new Vector2(x * n, y * n);
        }

        private int SqrMagnitude() {
            return x * x + y * y;
        }
    }




}
