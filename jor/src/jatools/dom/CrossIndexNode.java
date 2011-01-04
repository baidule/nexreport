package jatools.dom;

import bsh.CallStack;
import bsh.Interpreter;
import bsh.Primitive;

import jatools.dataset.CrossIndexField;
import jatools.dataset.CrossIndexView;
import jatools.dataset.Dataset;
import jatools.dataset.RowSet;

import jatools.engine.script.KeyStack;

import org.w3c.dom.Document;

import java.util.HashMap;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.6 $
  */
public class CrossIndexNode extends DatasetBasedNode {
    private String tag;
    private CrossIndexView indexView;
    private HashMap<String, Object> fieldsCache;

    /**
     * Creates a new CrossIndexNode object.
     *
     * @param parent DOCUMENT ME!
     * @param tag DOCUMENT ME!
     * @param indexView DOCUMENT ME!
     */
    public CrossIndexNode(DatasetBasedNode parent, String tag, CrossIndexView indexView) {
        _parent = parent;
        this.tag = tag;
        this.indexView = indexView;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dataset getDataset() {
        return this.getDatasetRoot().getDataset();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getChildElementsLocalName() {
        return null;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getOwnerDocument() {
        return this._parent.getOwnerDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNamespaceURI() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrefix() {
        return this._parent.getPrefix();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLocalName() {
        return tag;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public RowSet getRowSet() {
        return this.getDataset().getRowSet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param prop DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getProperty(String prop, CallStack callstack, Interpreter interpreter) {
        if (this.fieldsCache == null) {
            this.fieldsCache = new HashMap<String, Object>();
        }

        Object result = this.fieldsCache.get(prop);

        if (result == null) {
            int col = getDataset().getColumnIndex(prop);

            if (col > -1) {
                KeyStack key = this.getRoot().getScript().getKeyStack(0);
                KeyStack key2 = this.getRoot().getScript().getKeyStack(1);

                result = new CrossIndexField(this.indexView, col, key, key2);
            } else {
                result = Primitive.VOID;
            }

            this.fieldsCache.put(prop, result);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public CrossGroupList getGroups() {
        return new CrossGroupList(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public CrossIndexView getIndexView() {
        return indexView;
    }
}
