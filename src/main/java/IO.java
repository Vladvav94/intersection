import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IO {

    public static List<String> readCsvList(String path) {
        BufferedReader in;
        List<String> cellList = new ArrayList<String>();
        java.lang.String buf;
        try{
            in = new BufferedReader(new FileReader(path));
            while ((buf = in.readLine()) != null)
                cellList.add(buf);
            in.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
        return cellList;
    }

    public static void OutPut(ArrayList<MyPoint> point, ArrayList<String> cnt_output, String path)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        for(int i =0;i<cnt_output.size();i++) {
            String info = cnt_output.get(i);
            writer.println(info+"; "+ point.get(i).y+"; "+point.get(i).x);
        }
        writer.close();
    }
}

