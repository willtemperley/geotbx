package gtbx.tile; /**
 *    Copyright (C) 2009, 2010 
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details. [http://www.gnu.org/licenses]
 *    
 *    @author Nicky Sandhu
 *    
 */

/**
 * TMS Global Geodetic Profile ---------------------------
 * 
 * Functions necessary for generation of global tiles in Plate Carre projection,
 * EPSG:4326, "unprojected profile".
 * 
 * Such tiles are compatible with Google Earth (as any other EPSG:4326 rasters)
 * and you can overlay the tiles on top of OpenLayers base map.
 * 
 * Pixel and tile coordinates are in TMS notation (origin [0,0] in bottom-left).
 * 
 * What coordinate conversions do we need for TMS Global Geodetic tiles?
 * 
 * Global Geodetic tiles are using geodetic coordinates (latitude,longitude)
 * directly as planar coordinates XY (it is also called Unprojected or Plate
 * Carre). We need only scaling to pixel pyramid and cutting to tiles. Pyramid
 * has on top level two tiles, so it is not square but rectangle. Area
 * [-180,-90,180,90] is scaled to 512x256 pixels. TMS has coordinate origin (for
 * pixels and tiles) in bottom-left corner. Rasters are in EPSG:4326 and
 * therefore are compatible with Google Earth.
 * 
 * LatLon <-> Pixels <-> Tiles
 * 
 * WGS84 coordinates Pixels in pyramid Tiles in pyramid lat/lon XY pixels Z zoom
 * XYZ from TMS EPSG:4326 .----. ---- / \ <-> /--------/ <-> TMS \ /
 * /--------------/ ----- /--------------------/ WMS, KML Web Clients, Google
 * Earth TileMapService
 * 
 * @author nsandhu
 * 
 */
public class GlobalGeodetic {
	private final int tileSize;

	public GlobalGeodetic() {
		tileSize = GlobalMercator.TILE_SIZE;
	}

	/**
	 * Converts lat/lon to pixel coordinates in given zoom of the EPSG:4326
	 * pyramid
	 * 
	 * @return
	 */
	public int[] LatLonToPixels(double lat, double lon, int zoom) {
		double res = 180 / 256.0 / Math.pow(2, zoom);
		int px = (int) ((180 + lat) / res);
		int py = (int) ((90 + lon) / res);
		return new int[] { px, py };
	}

	/**
	 * Returns coordinates of the tile covering region in pixel coordinates
	 * 
	 * @return
	 */
	public int[] PixelsToTile(int px, int py) {
		int tx = (int) (Math.ceil(px / ((float) tileSize) - 1));
		int ty = (int) (Math.ceil(py / ((float) tileSize) - 1));
		return new int[] { tx, ty };
	}

	/**
	 * Resolution (arc/pixel) for given zoom level (measured at Equator)
	 * 
	 * @param zoom
	 * @return
	 */
	public double Resolution(int zoom) {
		return 180 / 256.0 / Math.pow(2, zoom);
	}

	/**
	 * Returns bounds of the given tile
	 * 
	 * @param tx
	 * @param ty
	 * @param zoom
	 * @return
	 */
	public int[] TileBounds(int tx, int ty, int zoom) {
		double res = 180 / 256.0 / Math.pow(2, zoom);
		return new int[] { (int) (tx * 256 * res - 180),
				(int) (ty * 256 * res - 90),
				(int) ((tx + 1) * 256 * res - 180),
				(int) ((ty + 1) * 256 * res - 90) };
	}

}
