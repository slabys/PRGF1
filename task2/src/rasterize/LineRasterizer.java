package rasterize;

import model.Line;
import model.Point;

import java.awt.*;

public abstract class LineRasterizer {
    Raster raster;
    Color color;

    public LineRasterizer(Raster raster){
        this.raster = raster;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void rasterize(Line line) {
        setColor(line.getColor());
        drawLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public void rasterize(Point p1, Point p2) {
        setColor(Color.PINK);
        drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    protected void drawLine(int x1, int y1, int x2, int y2) {
    }


    public Color getColor() {
        return color;
    }

}
