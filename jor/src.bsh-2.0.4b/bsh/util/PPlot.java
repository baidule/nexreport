package bsh.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
 
public class PPlot
{
    PPlotPanel plotPanel;
 
    public PPlot()
    {
        plotPanel = new PPlotPanel();
        PointMover mover = new PointMover(plotPanel);
        plotPanel.addMouseListener(mover);
        plotPanel.addMouseMotionListener(mover);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(getSpinnerPanel(), "North");
        f.getContentPane().add(plotPanel);
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
    }
 
    private JPanel getSpinnerPanel()
    {
        final SpinnerNumberModel model = new SpinnerNumberModel(0.5, 0, 1.0, 0.01);
        JSpinner spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();
        Dimension d = editor.getPreferredSize();
        d.width = 30;
        editor.setPreferredSize(d);
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        spinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                double value = model.getNumber().doubleValue();
                plotPanel.setT1(value);
            }
        });
        JPanel panel = new JPanel();
        panel.add(new JLabel("t value for point 1"));
        panel.add(spinner);
        return panel;
    }
 
    public static void main(String[] args)
    {
        new PPlot();
    }
}
 
class PPlotPanel extends JPanel
{
    Point2D.Double a0, a1, a2;       // constants for parametric equation
    Point2D p0, p1, p2;              // start, mid and end points on path
    double t1;                       // t value for p1 along path
    GeneralPath path;
    final int PAD = 20;
 
    public PPlotPanel()
    {
        t1 = 0.5;
    }
 
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        if(path == null)
            initVariables(w,h);
        // axes - ordinate
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h - PAD));
        // abcissa
        g2.draw(new Line2D.Double(PAD, h - PAD, w - PAD, h - PAD));
 
        g2.draw(path);
        g2.setPaint(Color.red);
        g2.fill(new Ellipse2D.Double(p0.getX() - 2, p0.getY() - 2, 4, 4));
        g2.fill(new Ellipse2D.Double(p1.getX() - 2, p1.getY() - 2, 4, 4));
        g2.fill(new Ellipse2D.Double(p2.getX() - 2, p2.getY() - 2, 4, 4));
    }
 
    private void initVariables(int w, int h)
    {
        a0 = new Point2D.Double();
        a1 = new Point2D.Double();
        a2 = new Point2D.Double();
        p0 = new Point2D.Double(PAD, h - PAD);
        p1 = new Point2D.Double(w/3, h/3);
        p2 = new Point2D.Double(w - PAD, h/8);
        path = new GeneralPath();
        calculatePath();
    }
 
    private void calculateConstants()
    {
        double divisor = t1*(t1 - 1.0);
        a0.x = p0.getX();
        a2.x = ( (p1.getX() - p0.getX()) - t1*(p2.getX() - p0.getX()) ) / divisor;
        a1.x = p2.getX() - p0.getX() - a2.getX();
        a0.y = p0.getY();
        a2.y = ( (p1.getY() - p0.getY()) - t1*(p2.getY() - p0.getY()) ) / divisor;
        a1.y = p2.getY() - p0.getY() - a2.getY();
    }
 
    /**
     * Parametric equation of the form
     *    P = a2*t^2 + a1*t + a0
     */
    private Point2D getParametricValue(double t)
    {
        Point2D.Double p = new Point2D.Double();
        p.x = a2.getX()*t*t + a1.getX()*t + a0.getX();
        p.y = a2.getY()*t*t + a1.getY()*t + a0.getY();
        return p;
    }
 
    private void calculatePath()
    {
        calculateConstants();
        path.reset();
        path.moveTo((float)p0.getX(), (float)p0.getY());
        int w = getWidth();
        double t;
        Point2D p;
        for(int j = 1; j <= w; j++)
        {
            t = j/(double)w;
            p = getParametricValue(t);
            path.lineTo((float)p.getX(), (float)p.getY());
        }
    }
 
    public void setT1(double t)
    {
        Point2D p = getParametricValue(t);
        p1.setLocation(p.getX(), p.getY());
        t1 = t;
        repaint();
    }
 
    public void movePoint(Point2D p, double x, double y)
    {
        p.setLocation(x, y);
        calculatePath();
        repaint();
    }
 
    public Point2D[] getPoints()
    {
        return new Point2D[] { p0, p1, p2 };
    }
}
 
/**
 * To select and drag the three Points on curve.
 */
class PointMover extends MouseInputAdapter
{
    PPlotPanel plotPanel;
    Point2D selectedPoint;
    Point2D.Double offset;
    boolean dragging;
    final int RADIUS = 4;
 
    public PointMover(PPlotPanel pp)
    {
        plotPanel = pp;
        offset = new Point2D.Double();
        dragging = false;
    }
 
    public void mousePressed(MouseEvent e)
    {
        Point p = e.getPoint();
        Point2D[] points = plotPanel.getPoints();
        for(int j = 0; j < points.length; j++)
            if(points[j].distance(p) < RADIUS)
            {
                selectedPoint = points[j];
                offset.x = p.x - points[j].getX();
                offset.y = p.y - points[j].getY();
                dragging = true;
                break;
            }
    }
 
    public void mouseReleased(MouseEvent e)
    {
        dragging = false;
    }
 
    public void mouseDragged(MouseEvent e)
    {
        if(dragging)
        {
            double x = e.getX() - offset.x;
            double y = e.getY() - offset.y;
            plotPanel.movePoint(selectedPoint, x, y);
        }
    }
}
