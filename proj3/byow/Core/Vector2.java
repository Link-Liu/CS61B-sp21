package byow.Core;

class Vector2 implements Comparable<Vector2> {
    int x;
    int y;

    Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 v2) {
        return new Vector2(x + v2.x, y + v2.y);
    }

    public Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 div(int n) {
        return new Vector2(x / n, y / n);
    }

    public Vector2 mul(int n) {
        return new Vector2(x * n, y * n);
    }

    public int sqrMagnitude() {
        return x * x + y * y;
    }

    @Override
    public int compareTo(Vector2 other) {
        // 先按 x 排序，再按 y 排序
        if (this.x != other.x) {
            return Integer.compare(this.x, other.x);
        } else {
            return Integer.compare(this.y, other.y);
        }
    }
}
