import com.clearspring.analytics.util.Pair;
import com.vividsolutions.jts.geom.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

/**
 * Created by vlad on 03.07.17.
 */


public class Calculate {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Calculate.class);

    public static MyPoint shapeIntersection(ArrayList<BS> arrayBS,
                                             Pair<ArrayList<ArrayList<String>>,ArrayList<String>> cellList_cnt){
        MyPoint centersPointsArray = new MyPoint();
        ArrayList<MyPoint> minimalConvexHullPointsArray = new ArrayList<MyPoint>();
        ArrayList<MyPoint> pointsArray = new ArrayList<MyPoint>();
            pointsArray = check_if_intersection_exist(arrayBS,cellList_cnt);
            if(pointsArray != null){
                minimalConvexHullPointsArray = FindMinimalConvexHull(pointsArray);

                //centersPointsArray = getCentersOldAlgorithm(minimalConvexHullPointsArray);
                centersPointsArray  = Calculate(minimalConvexHullPointsArray);
            }else{
                centersPointsArray = null;
            }

        return centersPointsArray;
    }

    public static void findArrayNeedAndCalcCenter(ArrayList<BS> tmp_BS,
                                                  Pair<ArrayList<ArrayList<String>>,ArrayList<String>> cellList_cnt)
            throws FileNotFoundException, UnsupportedEncodingException {
        ArrayList<BS> arrayBS = new ArrayList<BS>();
        ArrayList<String> cnt_output = new ArrayList<String>();
        Geometry tmp = null;
        ArrayList<MyPoint> centersPointsArray = new ArrayList<MyPoint>();
        for(int i = 0; i<cellList_cnt.left.size();i++){
            for(int j = 0 ;j<tmp_BS.size(); j++){
                if(cellList_cnt.left.get(i).contains(tmp_BS.get(j).getCellLac()))
                        arrayBS.add(tmp_BS.get(j));

            }
            if(arrayBS.size()>0) {
                MyPoint tmp_centr = (shapeIntersection(arrayBS, cellList_cnt));
                if(tmp_centr != null){
                    cnt_output.add(cellList_cnt.right.get(i));
                    centersPointsArray.add(tmp_centr);
                }


            }
            arrayBS.clear();
        }
        IO.OutPut(centersPointsArray, cnt_output);
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
            (ArrayList<BS> needBS,Pair<ArrayList<ArrayList<String>>,ArrayList<String>> cellList_cnt) {
        ArrayList<MyPoint> pointsArray = new ArrayList<MyPoint>();

        MyPoint tmpPoint;
        //ArrayList<BS> needBS = new ArrayList<BS>();


//        for (int j = 0; j < Settings.lacSellIdArray[i].length; j++) {
//            for (int w = 0; w < arrayBS.size(); w++) {
//                if (arrayBS.get(w).cellLac.equals(Settings.lacSellIdArray[i][j])) {
//                    needBS.add(arrayBS.get(w));
//                }
//            }
//        }
        if (needBS.size() == 0) {
            String info="";
            for(int j=0; j< cellList_cnt.left.size(); j++)
                info+= cellList_cnt.left.get(j)+" ";
            log.info("Intersection is empty "+"cnt: "+cellList_cnt.right+" cellId#lacId:"+info);
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
                        for(int log_index=0; log_index< cellList_cnt.left.size(); log_index++)
                            info+= cellList_cnt.left.get(log_index)+" ";
                        log.error("TopologyException union"+"cnt: "+cellList_cnt.right+" cellId#lacId:"+info);
                        continue;
                    }
                    needBS.get(j).setCoverage(DouglasPeuckerSimplifier.simplify(tmp, 0.0000000001));
                    needBS.remove(k);
                    k--;
                }
            }
        }
        Coordinate[] coordinatesArray = null;
        Geometry intersectLines = null;


            intersect = needBS.get(0).getCoverage();
            for (int j = 1; j < needBS.size(); j++) {
                try {
                    tmp = intersect.intersection(needBS.get(j).getCoverage());
                   // tmp = EnhancedPrecisionOp.intersection(intersect, needBS.get(j).coverage);
                }
                catch (TopologyException ex) {
                    IsValidOp isValidOp = new IsValidOp(tmp);
                    System.err.println("Geometry is invalid: " + isValidOp.
                            getValidationError());
                    ex.printStackTrace();
                    String info="";
                    for(int log_index=0; log_index< cellList_cnt.left.size(); log_index++)
                        info+= cellList_cnt.left.get(log_index)+" ";
                    log.error("TopologyException intersect "+"cnt: "+cellList_cnt.right+" cellId#lacId:"+info);
                    continue;
                }
                intersect = DouglasPeuckerSimplifier.simplify(tmp, 0.000001);

            }
                coordinatesArray = intersect.getCoordinates();





        if (coordinatesArray.length == 0) {
            String info="";
            for(int j=0; j< cellList_cnt.left.size(); j++)
                info+= cellList_cnt.left.get(j)+" ";
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

