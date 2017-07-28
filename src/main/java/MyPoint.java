
public class MyPoint {
    double x;
    double y;
    static MyPoint R;
    MyPoint() {}
    MyPoint(double inX, double inY) {
        this.x = inX;
        this.y = inY;

    }
    // если вернул больше нуля, то С слева от АВ, если вернул меньше нуля, то С лежит справа от АВ
    public static double rotate(MyPoint B, MyPoint C){
        return ((B.x-R.x)*(C.y-B.y)-(B.y-R.y)*(C.x-B.x));

    }
}