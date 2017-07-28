

import com.clearspring.analytics.util.Pair;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        long start = System.currentTimeMillis();
        Pair<ArrayList<ArrayList<String>>,ArrayList<String>> cellList_cnt = IO.readCellList();
        ArrayList<BS> tmp_BS = IO.reader();
        Calculate.findArrayNeedAndCalcCenter(tmp_BS,cellList_cnt);
        long finish = System.currentTimeMillis();
        long timeConsumedMillis = (finish - start)/1000;
        System.out.println("Время работы программы = "+timeConsumedMillis+" cекунд");
       // IO.check_result();
    }
}
