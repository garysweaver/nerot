package nerot;

import java.util.Date;

public class SampleObjectToWrap {

    public String someFantasticMethod() {
        throw new RuntimeException("called someFantasticMethod() by accident");
    }

    public String someFantasticMethod(String s, Date date, boolean b, char c, String d, Character ch) {
        throw new RuntimeException("called someFantasticMethod( String s, Date date, boolean b, char c, String d, Character ch) by accident");
    }

    public String someFantasticMethod(String s, Date date, boolean b, char c) {
        throw new RuntimeException("called someFantasticMethod( String s, Date date, boolean b, char c) by accident");
    }

    public String someFantasticMethod(String s, Date date, boolean b, char c, double d, Character ch) {
        return "" + s + "," + date + "," + b + "," + c + "," + d + "," + ch;
    }
}