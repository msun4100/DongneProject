package kr.me.ansr.image.model;

/**
 * Created by KMS on 2016-12-02.
 */
public class PinchMetrics {
    public int width;
    public int height;
    public PinchMetrics(){}
    public PinchMetrics(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "PinchMetrics{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
