import model.Line;
import model.Polygon;
import rasterize.DashedLineRasterizer;
import rasterize.FilledLineRasterizer;
import rasterize.PolygonRasterizer;
import rasterize.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main {

    private JPanel jPanel;
    private RasterBufferedImage rasterBufferedImage;
    private FilledLineRasterizer filledLineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private model.Polygon polygon = new model.Polygon();
    private ArrayList<Line> lines = new ArrayList<>();
    private int x1,x2,y1,y2;
    private int lx1,lx2,ly1,ly2;
    private boolean i = true;
    private boolean j = true;
    private boolean action = true;
    private model.Point lastPoint;
    private model.Point firstPoint;
    private DashedLineRasterizer dashedLineRasterizer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Main(800, 600).start()
        );
    }

    public void present(Graphics graphics){
        rasterBufferedImage.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        rasterBufferedImage.getGraphics().drawString("Use mouse and try to resize.", 5, 15);
        jPanel.repaint();
    }

    public void clear(int color) {
        rasterBufferedImage.setClearColor(color);
        rasterBufferedImage.clear();
    }

    public Main(int width, int height) {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());
        jFrame.setTitle(this.getClass().getName());
        jFrame.setResizable(true);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        rasterBufferedImage = new RasterBufferedImage(width, height);
        filledLineRasterizer = new FilledLineRasterizer(rasterBufferedImage);
        dashedLineRasterizer = new DashedLineRasterizer(rasterBufferedImage);
        polygonRasterizer = new PolygonRasterizer(dashedLineRasterizer);

        jPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                present(graphics);
            }
        };

        jPanel.setPreferredSize(new Dimension(width, height));

        jFrame.add(jPanel, BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setVisible(true);
        jPanel.requestFocus();
        jPanel.requestFocusInWindow();


        jPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    action = true;
                    if (i) {
                        x1 = e.getX();
                        y1 = e.getY();
                        model.Point point = new model.Point(x1, y1);
                        polygon.addPoint(point);
                        i = false;
                    } else {
                        x2 = e.getX();
                        y2 = e.getY();
                        model.Point point = new model.Point(x2, y2);
                        polygon.addPoint(point);
                        lastPoint = point;
                        filledLineRasterizer.rasterize(x1, y1, x2, y2, Color.GREEN);
                        x1 = x2;
                        y1 = y2;
                    }
                    jPanel.repaint();
                }

                if (e.getButton() == MouseEvent.BUTTON3) {
                    action = false;
                    if (j) {
                        lx1 = e.getX();
                        ly1 = e.getY();
                        j=false;
                    } else {
                        lx2 = e.getX();
                        ly2 = e.getY();
                        Line line = new Line(lx1, ly1, lx2, ly2, Color.CYAN.getRGB());
                        lines.add(line);
                        filledLineRasterizer.rasterize(lx1, ly1, lx2, ly2, Color.CYAN);
                        j=true;
                    }
                    jPanel.repaint();
                }
            }
        });

        //Clear window on press key "C"
        jPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_C){
                    polygon = new Polygon();
                    lines = new ArrayList<>();
                    firstPoint = null;
                    lastPoint = null;
                    i = true;
                    j = true;
                    clear(0xaaaaaa);
                    jPanel.repaint();
                }
            }
        });

        //Mouse move listener repainting all the components
        jPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                clear(0xaaaaaa);
                //Redraw single lines
                filledLineRasterizer.rasterize(lines);

                if(polygon.getPolygonPointList().size() > 1){
                    //Redraw polygon on move
                    polygonRasterizer.rasterize(polygon);
                    // Rasterizing moving lines for polygon
                    if(action){
                        filledLineRasterizer.rasterize(lastPoint.x, lastPoint.y, e.getX(), e.getY(), Color.YELLOW);
                        filledLineRasterizer.rasterize(polygon.getPolygonPointList().get(0).x,
                                polygon.getPolygonPointList().get(0).y, e.getX(), e.getY(), Color.YELLOW);
                    }
                }
                jPanel.repaint();
            }
        });

        //Resizable window
        jPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (jPanel.getWidth() < 1 || jPanel.getHeight() < 1)
                    return;
                if (jPanel.getWidth() <= rasterBufferedImage.getWidth()
                        && jPanel.getHeight() <= rasterBufferedImage.getHeight()) //no resize if new one is smaller
                    return;
                RasterBufferedImage newRaster = new RasterBufferedImage(jPanel.getWidth(), jPanel.getHeight());

                newRaster.draw(rasterBufferedImage);
                rasterBufferedImage = newRaster;
                filledLineRasterizer = new FilledLineRasterizer(rasterBufferedImage);
                dashedLineRasterizer = new DashedLineRasterizer(rasterBufferedImage);
                polygonRasterizer = new PolygonRasterizer(dashedLineRasterizer);
            }
        });
    }
}