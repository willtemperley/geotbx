package s2

import java.awt.Dimension
import java.awt.image.RenderedImage
import java.io.{IOException, File}

import com.vividsolutions.jts.geom._
import  s2.shp.ShapeWriter
import com.vividsolutions.jts.io.WKTReader
import it.jrc.GenericReaderPlugIn
import org.esa.s2tbx.dataio.s2.{S2SpatialResolution, Sentinel2ProductReader}
import org.esa.s2tbx.dataio.s2.l1c.Sentinel2L1CProductReader
import org.esa.s2tbx.dataio.s2.ortho.S2OrthoSceneLayout
import org.esa.snap.core.datamodel.{Band, Product}
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import org.opengis.referencing.FactoryException

/**
  * Created by willtemperley@gmail.com on 01-Feb-16.
  */
object Tiler {

  val granuleName = "E:/S2/S2A/GRANULE/S2A_OPER_MSI_L1C_TL_MTI__20160110T104330_A002877_T38KMU_N02.01/S2A_OPER_MTD_L1C_TL_MTI__20160110T104330_A002877_T38KMU.xml"
  val folderName = "E:/S2/S2A/S2A_OPER_MTD_SAFL1C_PDMC_20160110T172038_R020_V20160110T071720_20160110T071720.xml"
  val utm2wgs84: TileTransformer = new TileTransformer(TileTransformer.UTM_38S, TileTransformer.GEODETIC_EPSG)
//  val wgs84transformer: TileTransformer = new TileTransformer(TileTransformer.GEODETIC_EPSG, TileTransformer.UTM_38S)

  def main(args: Array[String]): Unit = {

    val zoomLevel: Int = 8

    val productReader: Sentinel2ProductReader = new Sentinel2L1CProductReader(new GenericReaderPlugIn, Sentinel2ProductReader.ProductInterpretation.RESOLUTION_MULTI, TileTransformer.UTM_38S)
    val f: File = new File(folderName)

    val s2SceneDescription: S2OrthoSceneLayout = productReader.getSceneDescription(f)
    val orig: Array[Double] = s2SceneDescription.getSceneOrigin
    val dims: Dimension = s2SceneDescription.getSceneDimension(S2SpatialResolution.R10M)
    val br: Array[Double] = Array[Double](orig(0) + (dims.getWidth * 10), orig(1) - (dims.getHeight * 10))
    val product: Product = productReader.getMosaicProduct(f)
    val bands: Array[Band] = product.getBands
    val band2: Band = bands(1)

//    val x: RenderedImage = band2.getSourceImage
//    val pt1 = utm2wgs84.transform(Array(orig(1), orig(0)))

    val pt1 = utm2wgs84.transform(orig)
//    val topLeftTile: Array[Int] = globalMercator.GoogleTile(pt1.getY, pt1.getX, level)
    val topLeftTile  = GoogleTile.getTileNumber(pt1.getY, pt1.getX, zoomLevel)
    val pt2 = utm2wgs84.transform(br)
//    val bottomRightTile: Array[Int] = globalMercator.GoogleTile(pt2.getY, pt2.getX, level)
    val bottomRightTile = GoogleTile.getTileNumber(pt2.getY, pt2.getX, zoomLevel)

    debugPoints(pt1, pt2)

    //Todo: does this always work? may depend on tile system. Order of tiles doesn't matter.
    val sw = new ShapeWriter(geomType = GeomType.Polygon, schemaDef = "tileid:String")

    for (i <- topLeftTile._1 to bottomRightTile._1) {
      //Google and OSM use image-style coordinates
      for (j <- topLeftTile._2 to bottomRightTile._2) {
        println(i + ":" + j)
        sw.addFeature(GoogleTile.getTilePolygon(i, j, zoomLevel), Seq(getTileId((i,j), zoomLevel)))
      }
    }

    sw.write("E:/s2-tile/tiles.shp")
  }

  def getTileId(ij: (Int, Int), zoom: Int) = ij._1 + "/" + ij._2 + "/" + zoom

  def debugPoints(a: Point, b: Point): Unit = {
    val swp = new ShapeWriter(geomType = GeomType.Point, schemaDef = "descr:String")
    swp.addFeature(a, Seq("origin"))
    swp.addFeature(b, Seq("bottom right"))
    swp.write("E:/s2-tile/points.shp")
  }

//  def utm38towgs84(orig: Array[Double]): Point = {
//    val factory = CRS.getAuthorityFactory(true)
//    val sourceCRS = factory.createCoordinateReferenceSystem("EPSG:32738")
//    val destCRS = factory.createCoordinateReferenceSystem("EPSG:4326")
//    val mathTransform = CRS.findMathTransform(sourceCRS, destCRS)
//
//    val geometryFactory: GeometryFactory = new GeometryFactory
//    val origin: Point = geometryFactory.createPoint(new Coordinate(orig(0), orig(1)))
//    val targetGeometry: Geometry = JTS.transform(origin, mathTransform)
//    targetGeometry.asInstanceOf[Point]
//  }

}
