

import com.clearspring.analytics.util.Pair;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        long start = System.currentTimeMillis();
        List<String>  cellList = IO.readCsvList(Settings.pathToSellList);
        ArrayList<ArrayList<String>>  bsList = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < Settings.inputs.length; i++) {
            bsList.add((ArrayList<String>) IO.readCsvList( Settings.path + Settings.inputs[i]));
        }

        Pair<ArrayList<MyPoint>,ArrayList<String>> centerArrayAndCnt =
                Calculate.findArrayNeedAndCalcCenter(bsList,cellList);
        IO.OutPut(centerArrayAndCnt.left, centerArrayAndCnt.right, Settings.pathToOutput+Settings.outputFilename);
        long finish = System.currentTimeMillis();
        long timeConsumedMillis = (finish - start)/1000;
        System.out.println("Время работы программы = "+timeConsumedMillis+" cекунд");
    }
}
