
import javafx.util.Pair;
//WKT,Class,SiteID,SectorID,SectorUID,BTSName,Technology,CellID,Band,LAC,RAC,BSIC,BCC,NCC,SAC,TAC,eNodeB,Longitude,Latitude,Azimuth,Height,Tilt,Address
public class Settings {
    public static String outputFilename = "cnt_array_of_centre_point.csv";
    public static String pathToOutput = "output/";
    public static String path = "resources/coverage/";
    public static String home2 = "resources/home2.csv";
    public static String pathToSellList = "resources/home2_cell_lists.csv";
    public static String[] inputs={"11_50_20151112_G_900_BS_6.csv","11_50_20151112_G_1800_BS_6.csv",
            "11_50_20151112_L_800_BS_6.csv", "11_50_20151112_L_1800_BS_6.csv","11_50_20151112_L_2600_BS_6.csv",
            "11_50_20151112_U_900_3036_BS_6.csv", "11_50_20151112_U_2100_10788_BS_6.csv",
            "11_50_20151112_U_2100_10813_BS_6.csv", "11_50_20151112_U_2100_10836_BS_6.csv"};
    //public static String[] inputs={"11_50_20151112_L_2600_BS_6.csv"};

    public static String[] cntArray = {"9672157505","9652779131"};
    public static String[][] lacSellIdArray = {{"27677#15490","27677#26563","27677#26565","27677#54273",
            "27685#12314","770742#3","770742#4","770742#6"},
            {"770714#1","770714#3","770714#4","770714#6"}};

    public static int WKT = 0;
    public static int CELLID = 7;
    public static int LACID = 9;
    public static int CNT = 0;
    public static int LISTCELLLACID = 1;

    //public static Pair<String[], String[][]> pair =
      //      new Pair<String[], String [][]>(cntArray,lacSellIdArray);

}
//(".*")[,;](.*?),
//$1;$2;