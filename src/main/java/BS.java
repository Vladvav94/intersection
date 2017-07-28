
import com.vividsolutions.jts.geom.Geometry;

public class BS {
    private Geometry coverage;
    private int fileIndex;
    private String cellLac;

    public void setCoverage(Geometry coverage){this.coverage = coverage;}
    public void setFileIndex(int fileIndex){this.fileIndex = fileIndex;}
    public void setCellLac(String cellLac){this.cellLac = cellLac;}
    public Geometry getCoverage(){return this.coverage;}
    public int getFileIndex(){return this.fileIndex;}
    public String getCellLac(){return this.cellLac;}
}
