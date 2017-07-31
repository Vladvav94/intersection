import com.clearspring.analytics.util.Pair;
import com.vividsolutions.jts.geom.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;


public class Calculate {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Calculate.class);

    public static MyPoint shapeIntersection(ArrayList<BS> arrayBS, ArrayList<ArrayList<String>> cellList,
                                            ArrayList<String> cnt){
        MyPoint centersPointsArray = new MyPoint();
        ArrayList<MyPoint> minimalConvexHullPointsArray = new ArrayList<MyPoint>();
        ArrayList<MyPoint> pointsArray = new ArrayList<MyPoint>();
            pointsArray = check_if_intersection_exist(arrayBS,cellList, cnt);
            if(pointsArray != null){
                minimalConvexHullPointsArray = FindMinimalConvexHull(pointsArray);

                //centersPointsArray = getCentersOldAlgorithm(minimalConvexHullPointsArray);
                centersPointsArray  = Calculate(minimalConvexHullPointsArray);
            }else{
                centersPointsArray = null;
            }

        return centersPointsArray;
    }

    public static Pair<ArrayList<MyPoint>,ArrayList<String>> findArrayNeedAndCalcCenter(ArrayList<ArrayList<String>> tmp_BS,
                                                  List<String> cellList_cnt)
            throws FileNotFoundException, UnsupportedEncodingException {


        ArrayList<BS> arrayBSAll = new ArrayList<BS>();
        ArrayList<ArrayList<String>> cellList = new ArrayList<ArrayList<String>>();
        ArrayList<String> cnt = new ArrayList<String>();
        ArrayList<String> tmp_list;

        for(int i = 0; i<cellList_cnt.size();i++){
            String[] cntString = cellList_cnt.get(i).trim().split(";", -1);
            String[] strings = cntString[Settings.LISTCELLLACID].trim().split(",", -1);
            tmp_list = new ArrayList<String>();
            cnt.add(cntString[Settings.CNT]);
            for(int j=0; j<strings.length;j++)
                tmp_list.add(strings[j]);
            cellList.add(tmp_list);
        }

        WKTReader wkt = new WKTReader();
            for (int i = 0; i < tmp_BS.size(); i++) {
                for (int j = 0; j < tmp_BS.get(i).size(); j++) {
                    String[] strings = tmp_BS.get(i).get(j).trim().split(";", -1);
                    BS tmp_bs = new BS();
                    try {
                        Geometry tmp_isValid = wkt.read(strings[Settings.WKT]);
                        if(!tmp_isValid.isValid()) {
                            tmp_bs.setCoverage(DouglasPeuckerSimplifier.simplify(tmp_isValid, 0.000001));
                            tmp_bs.setFileIndex(i);
                            tmp_bs.setCellLac(strings[Settings.LACID] + "#" + strings[Settings.CELLID]);
                            arrayBSAll.add(tmp_bs);
                        }
                    } catch (ParseException ex) {
                        System.out.println("WKT parsing problem");
                    }
                }
            }


        ArrayList<BS> arrayBS = new ArrayList<BS>();
        ArrayList<String> cnt_output = new ArrayList<String>();
        Geometry tmp = null;
        ArrayList<MyPoint> centersPointsArray = new ArrayList<MyPoint>();
        for(int i = 0; i<cellList.size();i++){
            for(int j = 0 ;j<arrayBSAll.size(); j++){
                if(cellList.get(i).contains(arrayBSAll.get(j).getCellLac()))
                    arrayBS.add(arrayBSAll.get(j));
            }
            if(arrayBS.size()>0) {
                MyPoint tmp_centr = (shapeIntersection(arrayBS, cellList, cnt));
                if(tmp_centr != null){
                    cnt_output.add(cnt.get(i));
                    centersPointsArray.add(tmp_centr);
                }


            }
            arrayBS.clear();
        }
        return new Pair<ArrayList<MyPoint>,ArrayList<String>>(centersPointsArray, cnt_output);
    }

    public static ArrayList<MyPoint> FindMinimalConvexHull(ArrayList<MyPoint> pointsArray) {
        int n = pointsArray.size();
        int[] Index = new int[n];
        int tmp;
        for (int i = 0; i < n; i++) {
            Index[i] = i;
            if(pointsArray.get(Index[i]).x < pointsArray.get(Index[0]).x)
            {
                tmp = Index[0];
                Index[0] = Index[i];
                Index[i] = tmp;
            }

        }
        MyPoint.R = pointsArray.get(Index[0]);
        for(int i =1; i < n-1; i++)
        {
            for(int j =i+1; j < n; j++)
            {
                if(MyPoint.rotate(pointsArray.get(Index[i]),pointsArray.get(Index[j]))<0)
                {
                    tmp = Index[i];
                    Index[i] = Index[j];
                    Index[j] = tmp;
                }


            }
        }
        ArrayList<Integer> S = new ArrayList<Integer>();

        S.add(Index[0]);
        S.add(Index[1]);
        for(int i =2; i < n; i++)
        {
            MyPoint.R = pointsArray.get(S.get(S.size()-2));
            while(MyPoint.rotate(pointsArray.get(S.get(S.size()-1)), pointsArray.get(Index[i]))<0)
                S.remove(S.size()-1);
            S.add(Index[i]);
        }

        ArrayList<MyPoint> MinimalConvexHull = new ArrayList<MyPoint>();
        for(int i=0; i<S.size(); i++)
            MinimalConvexHull.add(pointsArray.get(S.get(i)));
        return MinimalConvexHull;

    }

    public static MyPoint getCentersOldAlgorithm(ArrayList<MyPoint> pointsArray) {
        MyPoint tmp = new MyPoint();
            tmp.x = 0;
            tmp.y = 0;
            for(int j=0;j<pointsArray.size(); j++)
            {
                tmp.x += pointsArray.get(j).x;
                tmp.y += pointsArray.get(j).y;
            }
            tmp.x/=pointsArray.size();
            tmp.y/=pointsArray.size();


        return tmp;
    }
    public static  ArrayList<MyPoint> check_if_intersection_exist
            (ArrayList<BS> needBS, ArrayList<ArrayList<String>> cellList, ArrayList<String> cnt) {
        ArrayList<MyPoint> pointsArray = new ArrayList<MyPoint>();

        MyPoint tmpPoint;
        if (needBS.size() == 0) {
            String info="";
            for(int j=0; j< cellList.size(); j++)
                info+= cellList.get(j)+" ";
            log.info("Intersection is empty "+"cnt: "+cnt+" cellId#lacId:"+info);
            return null;
        }
        Geometry intersect;
        Geometry tmp = null;
        for (int j = 0; j < needBS.size(); j++) {
            for (int k = j + 1; k < needBS.size(); k++) {
                if (needBS.get(j).getFileIndex() == needBS.get(k).getFileIndex()) {
                    try {
                        //tmp = needBS.get(j).coverage.union(needBS.get(k).coverage);
                         tmp = EnhancedPrecisionOp.union(needBS.get(j).getCoverage(), needBS.get(k).getCoverage());
                    }catch (TopologyException ex){

                        IsValidOp isValidOp = new IsValidOp(tmp);
                        System.err.println("Geometry is invalid: " + isValidOp.
                                getValidationError());
                        ex.printStackTrace();
                        String info="";
                        for(int log_index=0; log_index< cellList.size(); log_index++)
                            info+= cellList.get(log_index)+" ";
                        log.error("TopologyException union"+"cnt: "+cnt+" cellId#lacId:"+info);
                        continue;
                    }
                    if(!tmp.isValid()){
                        needBS.get(j).setCoverage(DouglasPeuckerSimplifier.simplify(tmp, 0.0000000001));
                        needBS.remove(k);
                        k--;
                    }

                }
            }
        }
        Coordinate[] coordinatesArray = null;
        Geometry intersectLines = null;


            intersect = needBS.get(0).getCoverage();
            for (int j = 1; j < needBS.size(); j++) {
                try {
                   // tmp = intersect.intersection(needBS.get(j).getCoverage());
                    tmp = EnhancedPrecisionOp.intersection(intersect, needBS.get(j).getCoverage());
                }
                catch (TopologyException ex) {
                    IsValidOp isValidOp = new IsValidOp(tmp);
                    System.err.println("Geometry is invalid: " + isValidOp.
                            getValidationError());
                    ex.printStackTrace();
                    String info="";
                    for(int log_index=0; log_index< cellList.size(); log_index++)
                        info+= cellList.get(log_index)+" ";
                    log.error("TopologyException intersect "+"cnt: "+cnt+" cellId#lacId:"+info);
                    continue;
                }
                if(!tmp.isValid())
                     intersect = DouglasPeuckerSimplifier.simplify(tmp, 0.000001);

            }
                coordinatesArray = intersect.getCoordinates();





        if (coordinatesArray.length == 0) {
            String info="";
            for(int j=0; j< cellList.size(); j++)
                info+= cellList.get(j)+" ";
            //log.info("Intersection is empty "+"cnt: "+IO.cnt+" cellId#lacId:"+info);
            return null;
        } else {
            for (int k = 0; k < coordinatesArray.length; k++) {
                tmpPoint = new MyPoint();
                tmpPoint.x = coordinatesArray[k].x;
                tmpPoint.y = coordinatesArray[k].y;
                pointsArray.add(tmpPoint);
            }
            needBS.clear();
        }
        return pointsArray;
    }

    public static ArrayList<MyPoint> getCenters(ArrayList<ArrayList<MyPoint>> pointsArray) {
        ArrayList<MyPoint> centersPointsArray = new ArrayList<MyPoint>();
        for(int i = 0; i< pointsArray.size();i++)
        {
            centersPointsArray.add(Calculate(pointsArray.get(i)));
        }
        return centersPointsArray;
    }

    public static MyPoint Calculate(ArrayList<MyPoint> pointsArray) {
        if(pointsArray.size() > 2) {
            return Calculate(Triangulation(FindMinimalConvexHull(pointsArray)));
        }
        else if (pointsArray.size() == 2) {
            return new MyPoint((pointsArray.get(0).x + pointsArray.get(1).x)/2, (pointsArray.get(0).y + pointsArray.get(1).y)/2);
        }
        else {
            return pointsArray.get(0);
        }
    }

    public static ArrayList<MyPoint> Triangulation(ArrayList<MyPoint> pointsArray) {
        ArrayList<MyPoint> centerMass = new ArrayList<MyPoint>();
        for(int i=1;i<pointsArray.size()-1;i++){
            MyPoint tmp = new MyPoint((pointsArray.get(0).x + pointsArray.get(i).x + pointsArray.get(i+1).x)/3, (pointsArray.get(0).y + pointsArray.get(i).y + pointsArray.get(i+1).y)/3);
            centerMass.add(tmp);
        }
        return centerMass;
    }
}

