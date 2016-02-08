package gtbx

import com.vividsolutions.jts.geom.{Envelope, GeometryFactory, Polygon}

import scala.math._

import scala.math.{Pi => π}

/**
  * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Mathematics
  *
  * Created by willtemperley@gmail.com on 02-Feb-16.
  */
object GoogleTile {

  val fact = new GeometryFactory()

  def tileToLonLat(xtile: Int, ytile: Int, zoom: Int): (Double, Double) = {

    val n = pow(2, zoom)
    val lon_deg = xtile / n * 360.0 - 180.0
    val lat_rad = atan(sinh(π * (1 - 2 * ytile / n)))
    val lat_deg = lat_rad * 180.0 / π

    (lon_deg, lat_deg)
  }

  def getTilePolygon(xtile: Int, ytile: Int, zoom: Int): Polygon = {

    val tl = tileToLonLat(xtile, ytile, zoom)
    val br = tileToLonLat(xtile + 1, ytile + 1, zoom)

    val env = new Envelope()
    env.expandToInclude(tl._1, tl._2)
    env.expandToInclude(br._1, br._2)

    fact.toGeometry(env).asInstanceOf[Polygon]
  }

  def getTileNumber(lat: Double, lon: Double, zoom: Int): (Int, Int) = {
    var xtile: Int = Math.floor((lon + 180) / 360 * (1 << zoom)).toInt
    var ytile: Int = Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom)).toInt
    if (xtile < 0) xtile = 0
    if (xtile >= (1 << zoom)) xtile = ((1 << zoom) - 1)
    if (ytile < 0) ytile = 0
    if (ytile >= (1 << zoom)) ytile = ((1 << zoom) - 1)
    println("" + zoom + "/" + xtile + "/" + ytile)

    (xtile, ytile)
  }

//  def num2deg(xtile: Int, ytile: Int, zoom: Int): Unit = {
//
//  n = 2.0 ** zoom
//  lon_deg = xtile / n * 360.0 - 180.0
//  lat_rad = math.atan(math.sinh(math.pi * (1 - 2 * ytile / n)))
//  lat_deg = math.degrees(lat_rad)
//  return (lat_deg, lon_deg)
//  }
}
