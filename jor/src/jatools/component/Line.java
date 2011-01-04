/*
 *
 *   ���ݽܴ����   All Copyrights Reserved.
 */
package jatools.component;

import jatools.accessor.PropertyDescriptor;

import java.awt.BasicStroke;
import java.awt.Graphics2D;


/**
 * �Զ��������
 *
 */
public class Line extends Component {
    /**
     * DOCUMENT ME!
     */
    private static Object[] stockLinePatterns = new Object[6];

    static {
        float j = 1.1f;

        for (int i = 1; i < stockLinePatterns.length; i++, j += 1.0f) {
            float[] dash = {
                    j
                };
            stockLinePatterns[i] = dash;
        }
    }

    private int linePattern;
    private float lineSize = 1.0f;

   
    /**
    * ����һ���߶���
    */
    public Line() {
    }

    /**
    * ����һ���߶���
    *
    * @param x ������������е�x����
    * @param y ������������е�y����
    * @param width ��������
    * @param height ������߶�
    */
    public Line(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * ��������
     *
     * @param linePattern ����ֵ
     */
    public void setLinePattern(int linePattern) {
        this.linePattern = linePattern;
    }

    /**
     * ��ȡ����
     *
     * @return ����ֵ
     */
    public int getLinePattern() {
        return linePattern;
    }

    /**
     * �����߿�
     *
     * @param lineSize �߿�ֵ
     */
    public void setLineSize(float lineSize) {
        this.lineSize = lineSize;
    }

    /**
     * ��ȡ�߿�
     *
     * @return �߿�ֵ
     */
    public float getLineSize() {
        return lineSize;
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    public void setStroke(Graphics2D g) {
        float lineSize1 = getLineSize();

        float[] dash = (float[]) stockLinePatterns[getLinePattern()];

        BasicStroke stroke;

        if (dash == null) {
            stroke = new BasicStroke(lineSize1);
        } else {
            stroke = new BasicStroke(lineSize1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, dash, 0.0f);
        }

        g.setStroke(stroke);
    }

    /**
    * �ߵ����Լ�
    *
    * @return ������
    */
    public PropertyDescriptor[] getRegistrableProperties() {
        return new PropertyDescriptor[] {
            ComponentConstants._NAME,
            ComponentConstants._FORE_COLOR,
            ComponentConstants._LINE_PATTERN,
            ComponentConstants._LINE_SIZE,
            ComponentConstants._X,
            ComponentConstants._Y,
            ComponentConstants._WIDTH,
            ComponentConstants._HEIGHT,
            ComponentConstants._CELL,
			ComponentConstants._INIT_PRINT,
            ComponentConstants._AFTERPRINT,
            ComponentConstants._BEFOREPRINT2
        };
    }

	public static Object[] getStockLinePatterns() {
		return stockLinePatterns;
	}
}
