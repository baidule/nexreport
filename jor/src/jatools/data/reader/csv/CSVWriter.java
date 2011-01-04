package jatools.data.reader.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
  */
public class CSVWriter {
    //            "����",
    //            "��������",  // ����˾�ּ�(0),�ش���(1),��Ƽ�(2),����Ա������(3)
    //            "��ѵ����",  // ������ѵ(0),��ְ��ѵ(1),����������ѵ(2)
    //            "��ѵ����",  // 1-100
    //            "��ѵ����",  // ��������(0),ר��ҵ��(1),����֪ʶ(2),ѧ��ѧλ����(3)
    //            "��ѵ����"   // ��У(0),����ѧԺ(1),������ѵ����(2)
    static String cls = "string,int,int,int,int,int\n";
    static String names1 = "����,��������,��ѵ����,��ѵ����,��ѵ����,��ѵ����\n";

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        try {
            FileReader filereader = new FileReader(new File("d:/names.txt"));
            BufferedReader reader = new BufferedReader(filereader);
            reader.readLine();

            String line = null;
            Map names = new HashMap();

            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 1) {
                    names.put(line.trim(), line.trim());
                }
            }

            reader.close();

            FileWriter writer = new FileWriter(new File("d:/train.txt"));
            writer.write(cls);
            writer.write(names1);
            Math.random() ;
            

            Iterator it = names.keySet().iterator();

            while (it.hasNext()) {
                Object val = it.next();
                writer.write(val+","+random(4)+","+random(3)+","+random(100)+","+random(4)+","+random(3)+"\n");
                System.out.println(val);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static int random(int max)
    {
    	return (int) ((Math.random() * 100) % max) ;
    }
}
