package byow.Core;

public class Room {
    int x;
    int y;
    int w;
    int h;
    // 构造函数 其中x,y是房间的左下角坐标 w,h分别是宽高
    Room(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean IsOverlap(Room r2) {
            // 计算每个房间的边界
            int r1Right = this.x + this.w;
            int r1Top = this.y + this.h;
            int r2Right = r2.x + r2.w;
            int r2Top = r2.y + r2.h;

            // 判断是否不重叠 只要满足一者  不重叠就是真
            boolean noOverlap = this.x >= r2Right ||   // r1在r2右侧
                    r1Right <= r2.x ||   // r1在r2左侧
                    this.y >= r2Top ||  // r1在r2上方
                    r1Top <= r2.y;    // r1在r2下方

            // 如果不满足不重叠条件，则两房间重叠
            return !noOverlap;
    }

}
