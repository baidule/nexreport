package jatools.component.table;



import jatools.accessor.ProtectPublic;
import jatools.component.Label;
import jatools.component.Text;
import jatools.dataset.Dataset;
import jatools.designer.JatoolsException;


/**
 *
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class DynamicColumnTableBuilder implements ProtectPublic {
    Table table;
    Dataset dataset;
    String fields;
    String titles;
    String classes;

    /**
         * @param table
         * @param dataset
         */
    public DynamicColumnTableBuilder(Table table, Dataset dataset) {
        this.table = table;
        this.dataset = dataset;
    }

    /**
    * DOCUMENT ME!
    *
    * @param table ������,����Ϊ����,һ��,�ҵ�һ����Ԫ��Ϊ��ǩ����,�ڶ��е�Ԫ��Ϊ�ı�����
    * @param dataset ���ݼ�����
    * @param fields ѡ�����ݼ�����Щ�ֶ�����ʾ
    * @param titles ��ͷ��ʾ����
    * @param classes ������ʽ��
    * @throws JatoolsException
    */
    public void build() throws JatoolsException {
        if (table == null) {
            throw new JatoolsException("���ܴ�����̬�б��,Դ���Ϊ��!");
        } else if (table.getRowCount() >2 ||      (table.getColumnCount() != 1)) {
            throw new JatoolsException("���ܴ�����̬�б��,Դ������Ϊ����һ��!");
        } /*else if (!(table.getComponent(0, 0) instanceof Label) ||
            !(table.getComponent(1, 0) instanceof Text)) {
        throw new JatoolsException("���ܴ�����̬�б��,[0,0]��Ԫ��Ϊ��ǩ����,��[1,0]��Ԫ��Ϊ�ı�!");
        }*/
        if (dataset == null) {
            throw new JatoolsException("���ܴ�����̬�б��,���ݼ�Ϊ��!");
        }

        String[] fieldsArray = null;

        if (fields != null) {
            fieldsArray = fields.split(";");
        } else {
            // ���ûָ����ʾ��,��ʾ����dataset�е���
            fieldsArray = new String[dataset.getColumnCount()];

            for (int i = 0; i < fieldsArray.length; i++) {
                fieldsArray[i] = dataset.getColumnName(i);
            }
        }

        String[] titlesArray = null;

        if (titles != null) {
            titlesArray = titles.split(";");

            if (titlesArray.length != fieldsArray.length) {
                throw new JatoolsException("���ܴ�����̬�б��,ָ�����������������һ��.");
            }
        } else {
            // ���ûָ��̧ͷ,��������������
            titlesArray = fieldsArray;
        }

        String[] classesArray = null;

        if (classes != null) {
            classesArray = classes.split(";");

            if (classesArray.length != fieldsArray.length) {
                throw new JatoolsException("���ܴ�����̬�б��,ָ������ʽ���������ʾ������һ��.");
            }
        }

        // ��ʼ����
        // ����ȷ������
        for (int i = 0; i < (fieldsArray.length - 1); i++) {
            table.insertColumnAfter(0, table.getColumnWidth(0));
        }

        if (table.getRowCount() == 2) {
            Label firstLabel = (Label) table.getComponent(0, 0);
            Text firstText = (Text) table.getComponent(1, 0);
            RowPanel rowPanel = (RowPanel) firstText.getParent();

            try {
                for (int i = 0; i < titlesArray.length; i++) {
                    Label label = null;
                    Text text = null;

                    if (i == 0) {
                        label = firstLabel;
                        text = firstText;
                    } else {
                        label = (Label) firstLabel.clone();
                        table.add(label, 0, i);

                        text = (Text) firstText.clone();
                        rowPanel.add(text, 1, i);
                    }

                    label.setText(titlesArray[i]);
                    text.setVariable("=$." + fieldsArray[i]);

//                    if (classesArray != null) {
//                        text.setStyleClass(classesArray[i]);
//                    }
                }
            } catch (CloneNotSupportedException e) {
                throw new JatoolsException(e);
            }
        } else {
            Text firstText = (Text) table.getComponent(0, 0);
            RowPanel rowPanel = (RowPanel) firstText.getParent();

            try {
                for (int i = 0; i < titlesArray.length; i++) {
                    Text text = null;

                    if (i == 0) {
                        text = firstText;
                    } else {
                        text = (Text) firstText.clone();
                        rowPanel.add(text, 0, i);
                    }

                    text.setVariable("=$." + fieldsArray[i]);

//                    if (classesArray != null) {
//                        text.setStyleClass(classesArray[i]);
//                    }
                }
            } catch (CloneNotSupportedException e) {
                throw new JatoolsException(e);
            }
        }

        table.validate();
    }

    /**
     * DOCUMENT ME!
     *
     * @param fields DOCUMENT ME!
     */
    public void setFields(String fields) {
        this.fields = fields;
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * DOCUMENT ME!
     *
     * @param titles DOCUMENT ME!
     */
    public void setTitles(String titles) {
        this.titles = titles;
    }

    /**
     * DOCUMENT ME!
     *
     * @param classes DOCUMENT ME!
     */
    public void setClasses(String classes) {
        this.classes = classes;
    }
}
