package demo;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Demo {


    public void test1() {

    }

    public static class Student {
        String name;

        public Student(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calBegin = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calBegin.setTime(sf.parse("2020-02-22"));
        calEnd.setTime(sf.parse("2022-01-22"));
        int difYear = calEnd.get(Calendar.YEAR) - calBegin.get(Calendar.YEAR);
        int difMonth = calEnd.get(Calendar.MONTH) - calBegin.get(Calendar.MONTH);

        //return dif>0? dif+"年" ;
        System.out.println(difYear);
        System.out.println(difMonth);
    }


    {
        int ret[] = getDateLength("20050531 ", "20070101 ");
        System.out.println(ret[0] + ": " + ret[1] + ": " + ret[2]);
    }

    static int[] getDateLength(String fromDate, String toDate) {
        Calendar c1 = getCal(fromDate);
        Calendar c2 = getCal(toDate);
        int[] p1 = {c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH)};
        int[] p2 = {c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH)};
        return new int[]{p2[0] - p1[0], p2[0] * 12 + p2[1] - p1[0] * 12 - p1[1], (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / (24 * 3600 * 1000))};
    }

    static Calendar getCal(String date) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6, 8)));
        return cal;
    }

}
