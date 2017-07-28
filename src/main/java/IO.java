import java.io.*;
import java.util.ArrayList;

import com.clearspring.analytics.util.Pair;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;


public class IO {
    public static Pair<ArrayList<ArrayList<String>>,ArrayList<String>> readCellList() {
        BufferedReader in;
        String buf;
        ArrayList<ArrayList<String>> cellList = new ArrayList<ArrayList<String>>();
        ArrayList<String> cnt = new ArrayList<String>();
        ArrayList<String> tmp;
        int tmp_index = 0;

        try{
            in = new BufferedReader(new FileReader(Settings.pathToSellList));
            while ((buf = in.readLine()) != null) {
                tmp_index++;
                String[] cntString = buf.trim().split(";", -1);
                String[] strings = cntString[Settings.LISTCELLLACID].trim().split(",", -1);
                tmp = new ArrayList<String>();
               cnt.add(cntString[Settings.CNT]);
                for(int i=0; i<strings.length;i++) {
                    tmp.add(strings[i]);
                    // if (lacSellIdArray.containsKey(strings[i]))
                    //   pointsArray.get(pointsArray.size() - 1).add(lacSellIdArray.get(strings[i]));
                }
                cellList.add(tmp);
            }
            in.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
        return new Pair<ArrayList<ArrayList<String>>,ArrayList<String>>(cellList,cnt);
    }


    public static ArrayList<BS> reader() {
        BufferedReader in;
        String buf;
        ArrayList<BS> arrayBS = new ArrayList<BS>();
        WKTReader wkt = new WKTReader();
            for (int i = 0; i < Settings.inputs.length; i++) {
                try {
                    in = new BufferedReader(new FileReader(Settings.path + Settings.inputs[i]));
                    while ((buf = in.readLine()) != null) {
                        String[] strings = buf.trim().split(";", -1);
                            BS tmp = new BS();
                            try {
                                tmp.setCoverage(DouglasPeuckerSimplifier.simplify(wkt.read(strings[Settings.WKT]), 0.000001));
                                tmp.setFileIndex(i);
                                tmp.setCellLac(strings[Settings.LACID] + "#" + strings[Settings.CELLID]);
                                arrayBS.add(tmp);
                            } catch (ParseException ex) {
                                System.out.println("WKT parsing problem");
                            }
                        // System.out.println(i+" "+strings[Settings.LACID] + "#" + strings[Settings.CELLID]);
                    }
                    in.close();
                } catch(IOException ex){
                    System.out.println(ex);
                }
            }
        return arrayBS;
    }
    /*
    public static void  check_result() throws FileNotFoundException, UnsupportedEncodingException {
        BufferedReader in;
        String buf;
        ArrayList<Pair<Double, Double[]>> cnt_coord = new ArrayList<Pair<Double, Double[]>>();
        ArrayList<Pair<Double, Double[]>> result = new ArrayList<Pair<Double, Double[]>>();
        int tmp_index = 0;
        Pair<Double, Double[]> tmp;
        Double[] tmp_array;
        try{
            in = new BufferedReader(new FileReader(Settings.home2));
            ArrayList<ArrayList<String>> cellList = new ArrayList<ArrayList<String>>();
            while ((buf = in.readLine()) != null) {
                String[] string = buf.trim().split(";");
                tmp_array = new Double[2];
                tmp_array[0] = Double.parseDouble(string[1]);
                tmp_array[1] = Double.parseDouble(string[2]);
                tmp = new Pair<Double, Double[]>(Double.parseDouble(string[0]),tmp_array);
                cnt_coord.add(tmp);
            }
            in.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }

        try{
            in = new BufferedReader(new FileReader(Settings.pathToOutput+Settings.outputFilename));
            ArrayList<ArrayList<String>> cellList = new ArrayList<ArrayList<String>>();
            tmp_array = new Double[2];
            while ((buf = in.readLine()) != null) {
                String[] string = buf.trim().split(";");
                tmp_array = new Double[2];
                tmp_array[0] = Double.parseDouble(string[1]);
                tmp_array[1] = Double.parseDouble(string[2]);
                tmp = new Pair<Double, Double[]>(Double.parseDouble(string[0]),tmp_array);
                result.add(tmp);
            }
            in.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
        ArrayList<Pair<Double,Double>> cnt_dist = new ArrayList<Pair<Double, Double>>();
        Pair<Double,Double> tmp_cnt_dist;
        for(int i = 0; i<result.size();i++){
            for(int j=0; j<cnt_coord.size(); j++){
                if(result.get(i).fst.equals(cnt_coord.get(j).fst)){
//                    System.out.println(result.get(i).snd[1]+" "+ cnt_coord.get(j).snd[1]);
//                    System.out.println(result.get(i).snd[0]+" "+ cnt_coord.get(j).snd[0]);
                    double tmp1,tmp2,tmp3;
                    tmp1 = Math.sin(result.get(i).snd[0] - cnt_coord.get(j).snd[0])/2;
                    tmp2 = Math.cos(result.get(i).snd[0])*Math.cos(cnt_coord.get(i).snd[0]);
                    tmp3 =  Math.sin(result.get(i).snd[1] - cnt_coord.get(j).snd[1])/2;
                    double dist = 2*Math.asin(Math.sqrt(tmp1*tmp1)+tmp2*tmp3*tmp3);

//                    double dist = 2*Math.asin(Math.sqrt(Math.sin((Math.abs(result.get(i).snd[0] - cnt_coord.get(j).snd[0]))/2)*
//                            ((Math.abs(result.get(i).snd[0] - cnt_coord.get(j).snd[0]))/2))+
//                                    Math.cos(result.get(i).snd[0])*Math.cos(cnt_coord.get(i).snd[0])*
//                                    Math.sqrt(Math.sin((Math.abs(result.get(i).snd[1] - cnt_coord.get(j).snd[1]))/2)*
//                                    ((Math.abs(result.get(i).snd[1] - cnt_coord.get(j).snd[1]))/2)));
                    tmp_cnt_dist = new Pair<Double, Double>(result.get(i).fst,dist);
                    cnt_dist.add(tmp_cnt_dist);

                }
            }
        }
        for(int i = 0; i<cnt_dist.size();i++){
            System.out.println(String.format("%.10f",cnt_dist.get(i).snd));
        }
    }
    */
    public static void OutPut(ArrayList<MyPoint> point, ArrayList<String> cnt_output)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(Settings.pathToOutput+Settings.outputFilename, "UTF-8");
        for(int i =0;i<cnt_output.size();i++) {
            String info = cnt_output.get(i);
            //info+="";
            // for(int i=0; i< cellList.size()-1; i++)
            //       info+= cellList.get(i)+", ";
            // info+=cellList.get(cellList.size()-1);
            writer.println(info+"; "+ point.get(i).y+"; "+point.get(i).x);
        }
        writer.close();
    }
}

