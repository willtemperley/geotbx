package gtbx;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Created by willtemperley@gmail.com on 29-Jan-16.
 */
public class TileTransformer {


    private MathTransform mathTransform;
    CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);

    public static String UTM_38S = "EPSG:32738";
    public static String MERCATOR_EPSG = "EPSG:3857";
    public static String GEODETIC_EPSG = "EPSG:4326";

    private CoordinateReferenceSystem sourceCRS;
    private CoordinateReferenceSystem destCRS;

    public TileTransformer(String sourceEPSG, String destEPSG)  {
        try {
            this.sourceCRS = factory.createCoordinateReferenceSystem(sourceEPSG);
            this.destCRS = factory.createCoordinateReferenceSystem(destEPSG);
            this.mathTransform = CRS.findMathTransform(sourceCRS, destCRS);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }

    public CoordinateReferenceSystem getSourceCRS() throws FactoryException {
        if (sourceCRS == null){
            sourceCRS = factory.createCoordinateReferenceSystem(MERCATOR_EPSG);
        }
        return sourceCRS;
    }

    public CoordinateReferenceSystem getDestCRS() throws FactoryException {
        if (destCRS == null){
            destCRS = factory.createCoordinateReferenceSystem(MERCATOR_EPSG);
        }
        return destCRS;
    }

    private CoordinateReferenceSystem getCRS(String s) throws FactoryException {
        return factory.createCoordinateReferenceSystem(s);
    }


    public MathTransform getMathTransform() {

        return mathTransform;
    }

    public Point transform(double[] sceneOrig) throws FactoryException, TransformException {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point origin = geometryFactory.createPoint(new Coordinate(sceneOrig[0], sceneOrig[1]));
        Geometry targetGeometry = JTS.transform(origin, mathTransform);
        return (Point) targetGeometry;
    }

    public static String getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
        if (xtile < 0)
            xtile=0;
        if (xtile >= (1<<zoom))
            xtile=((1<<zoom)-1);
        if (ytile < 0)
            ytile=0;
        if (ytile >= (1<<zoom))
            ytile=((1<<zoom)-1);
        return("" + zoom + "/" + xtile + "/" + ytile);
    }


}
