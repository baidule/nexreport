/*
 * Created on 2003-11-11
 *
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package jatools.component;

import jatools.accessor.PropertyDescriptor;

import org.krysalis.barcode4j.BarcodeClassResolver;
import org.krysalis.barcode4j.DefaultBarcodeClassResolver;


/**
 * @author java
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 *
 *
 * �����뱨��Ԫ��
 *
 */
public class BarCode extends Image implements Var{
    private static BarcodeClassResolver resolver;
    private String codeType = "codabar";
    private String variable;

    /**
     * ����һ�����������
     *
     * @param x ���������Ͻǵĺ�����
     * @param y ���Ͻǵ�������
     * @param width ��������
     * @param height ������߶�
     */
    public BarCode(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setHorizontalAlignment(1);
        this.setVerticalAlignment(1);
    }

    /**
     * ����һ�����������.
     */
    public BarCode() {
        this.setHorizontalAlignment(1);
        this.setVerticalAlignment(1);
    }

    /**
     * @return Returns the codeType.
     */
    public String getCodeType() {
        return codeType;
    }

    /**
     * @param codeType The codeType to set.
     */
    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    /*
     * ����������Լ�
     *
     * @see com.jatools.core.accessor.ZPropertyAccessor#getRegistrableProperties()
     */

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PropertyDescriptor[] getRegistrableProperties() {
        return new PropertyDescriptor[] {
            ComponentConstants._NAME,
            ComponentConstants._VARIABLE,
            ComponentConstants._BACK_COLOR,
            ComponentConstants._FORE_COLOR,
            ComponentConstants._BORDER,
            ComponentConstants._CODE_TYPE,
            ComponentConstants._REQUIRED_HTML_IMAGE_FORMAT2,
            ComponentConstants._HYPERLINK,
            ComponentConstants._TOOLTIP_TEXT,
            ComponentConstants._X,
            ComponentConstants._Y,
            ComponentConstants._WIDTH,
            ComponentConstants._HEIGHT,
            ComponentConstants._PRINT_STYLE,
            ComponentConstants._CELL,
            ComponentConstants._INIT_PRINT, // 
            ComponentConstants._AFTERPRINT,
            ComponentConstants._BEFOREPRINT2
        };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ImageStyle getImageStyle() {
        return new ImageStyle(null, 1, 1, false, this.getExportImageFormat());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static BarcodeClassResolver getBarcodeClassResolver() {
        if (resolver == null) {
            resolver = new DefaultBarcodeClassResolver();
        }

        return resolver;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVariable() {
        return variable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param variable DOCUMENT ME!
     */
    public void setVariable(String variable) {
        this.variable = variable;
    }
}
