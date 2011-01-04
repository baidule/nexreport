package jatools.dom.field;

import bsh.BSHArguments;
import bsh.CallStack;
import bsh.EvalError;
import bsh.Filter;
import bsh.Interpreter;
import bsh.Node;
import bsh.Primitive;

import jatools.component.table.PowerTable;

import jatools.dataset.CrossIndexView;
import jatools.dataset.Dataset;
import jatools.dataset.FilteredCrossIndexField;
import jatools.dataset.IndexView;
import jatools.dataset.Key;

import jatools.dom.DatasetNode;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class DatasetField extends AbstractValuesField implements Filter {
    private int col;
    DatasetNode node;

    /**
     * Creates a new NodeField object.
     *
     * @param col
     *            DOCUMENT ME!
     * @param nodestack
     *            DOCUMENT ME!
     */
    public DatasetField(int col, DatasetNode node) {
        this.col = col;
        this.node = node;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumn() {
        return col;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] values() {
        Object vals = node.valuesAt(col);

        return (Object[]) ((vals == null) ? EMPTY_ARRAY : vals);
    }

    /**
     * DOCUMENT ME!
     *
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     * @param parameterNode DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object filter(CallStack callstack, Interpreter interpreter, Node parameterNode)
        throws EvalError {
        Object view = node.getIndexView();
        int col = getColumn();

        if (view instanceof IndexView) {
            if (parameterNode instanceof BSHArguments) {
                Object[] keys = ((BSHArguments) parameterNode).getArguments(callstack, interpreter);

                for (int i = 0; i < keys.length; i++) {
                    keys[i] = Primitive.unwrap(keys[i]);
                }

                Key key = null;

                if ((keys.length == 1) && (keys[0] == Dataset.STAR)) {
                    key = Dataset.STAR;
                } else {
                    key = new Key(keys);
                }

                Object value2 = new FilteredIndexField((IndexView) view, key, col);
                interpreter.getRoot().setValue2(value2);

                return value2;
            }
        } else if (view instanceof CrossIndexView) {
            Key key = null;
            Key key2 = node.getRoot().getScript().getKeyStack(1).getKey();

            if (parameterNode instanceof BSHArguments) {
                Object[] keys = ((BSHArguments) parameterNode).getArguments(callstack, interpreter);

                for (int i = 0; i < keys.length; i++) {
                    keys[i] = Primitive.unwrap(keys[i]);
                }

                if ((keys.length == 1) && (keys[0] == PowerTable.CURRENT_ROW_KEY)) {
                    key = node.getRoot().getScript().getKeyStack(0).getKey();
                } else {
                    key = new Key(keys);
                }
            } else if (parameterNode == null) {
                key = Dataset.STAR;
            }

            if (key != null) {
                return new FilteredCrossIndexField(((CrossIndexView) view).locate(key), col,
                    node.getRoot().getScript().getKeyStack(1));
            } else {
                return null;
            }
        }

        return null;
    }
}
