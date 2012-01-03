package bsh;

import java.io.StringReader;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class Test {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        try {
           

            try {
                Interpreter it = new Interpreter();
                it.DEBUG = true;
                it.eval("$(指标.运费计数,需求日期.1994.11.1)");
                
                
                it = new Interpreter();
                it.DEBUG = true;
                it.eval("new jatools.data.date.DateGroup(1,2);");
                it = new Interpreter();
                it.DEBUG = true;
                it.eval("new jatools.data.date.DateGroup(1,2);");
            } catch (EvalError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //    Parser p = new Parser(new StringReader("{afdsafdsa{ffa}dsfasdfsadff}"));

            //	p.replace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // \\数据源\学生[0]\@年龄+100
        // $r\@NAME{@this=="Cuba"}.count();
        //        try {
        //           // it.set("$r", new SimpleDataset());
        //        //    it.eval("${//path/aa/xx[last()]}");
        //        //    it.eval("aa{=1991}");
        //   //         System.out.println(it.eval(
        //     //               "$r.addcol(rank_desc(POPULATION) as RANK).order(RANK desc)"));
        //        } catch (EvalError e) {
        //            e.printStackTrace();
        //        }
    }
}
