package render;

import model.Scene;
import model.Solid;
import model.Vertex;
import rasterize.LineRasterizer;
import rasterize.Raster;
import transforms.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Renderer {

    //Pipeline Implementation
    private Raster raster;
    private LineRasterizer lineRasterizer;
    private Mat4 model = new Mat4RotXYZ(Math.PI / 2, Math.PI / 3, Math.PI / 4);
    private Mat4 view = new Mat4ViewRH(
            new Vec3D(1, 1, 1),
            new Vec3D(-1, -1, -1),
            new Vec3D(0, 1, 1));
    private Mat4 projection = new Mat4PerspRH(Math.PI / 2, 1, 0.1, 10);
    private Color color = Color.YELLOW;

    public Mat4 getView() {
        return view;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public Renderer(Raster raster, LineRasterizer lineRasterizer) {
        this.raster = raster;
        this.lineRasterizer = lineRasterizer;
    }

    public void render(Scene scene) {
        for (Solid solid : scene.getSolids()) {
            render(solid);
        }
    }

    public void render(Solid solid) {
        Mat4 mat = model.mul(view).mul(projection);
        color = solid.getColor();

        List<Vertex> tempVertices = new ArrayList<>();
        for (Vertex vx : solid.getVertices()) {
            tempVertices.add(vx.transform(mat));
        }

        for (int i = 0; i < solid.getIndicies().size() - 1; i += 2) {
            int indexA = solid.getIndicies().get(i);
            int indexB = solid.getIndicies().get(i + 1);

            Vertex a = tempVertices.get(indexA),
                    b = tempVertices.get(indexB);
            renderEdge(a, b);

        }
    }

    private void renderEdge(Vertex a, Vertex b) {

        if(!a.dehomog().isPresent() || !b.dehomog().isPresent()) return;
        if(a.isInView() && b.isInView()){
            final Vec3D vecA = a.dehomog().get();
            final Vec3D vecB = b.dehomog().get();

            int x1 = (int) ((vecA.getX() + 1) * (raster.getWidth() - 1) / 2);
            int x2 = (int) ((vecB.getX() + 1) * (raster.getWidth() - 1) / 2);
            int y1 = (int) ((1 - vecA.getY()) * (raster.getHeight() - 1) / 2);
            int y2 = (int) ((1 - vecB.getY()) * (raster.getHeight() - 1) / 2);

            lineRasterizer.rasterize(x1, y1, x2, y2, color);
        }
    }
}