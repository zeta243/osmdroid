package org.andnav.osm.tileprovider.renderer;

import org.andnav.osm.ResourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenStreetMapRendererFactory {

	private static final Logger logger = LoggerFactory.getLogger(OpenStreetMapRendererFactory.class);

	/**
	 * Get the renderer with the specified name.
	 *
	 * @param aRendererId
	 * @return the renderer
	 * @throws IllegalArgumentException
	 *             if renderer not found
	 */
	public static IOpenStreetMapRendererInfo getRenderer(String aName)
			throws IllegalArgumentException {
		for (IOpenStreetMapRendererInfo renderer : mRenderers) {
			// TODO perhaps we should ignore case and white space
			if (renderer.name().equals(aName)) {
				return renderer;
			}
		}
		throw new IllegalArgumentException("No such renderer: " + aName);
	}

	/**
	 * Get the renderer at the specified position.
	 *
	 * @param aOrdinal
	 * @return the renderer
	 * @throws IllegalArgumentException
	 *             if renderer not found
	 */
	public static IOpenStreetMapRendererInfo getRenderer(int aOrdinal)
			throws IllegalArgumentException {
		for (IOpenStreetMapRendererInfo renderer : mRenderers) {
			if (renderer.ordinal() == aOrdinal) {
				return renderer;
			}
		}
		throw new IllegalArgumentException("No renderer at position: " + aOrdinal);
	}

	public static IOpenStreetMapRendererInfo[] getRenderers() {
		return mRenderers;
	}

	public static final OpenStreetMapOnlineTileRendererBase OSMARENDER = new XYRenderer(
			"Osmarender", ResourceProxy.string.osmarender, 0, 17, 256, ".png",
			"http://tah.openstreetmap.org/Tiles/tile/");

	public static final OpenStreetMapOnlineTileRendererBase MAPNIK = new XYRenderer(
			"Mapnik", ResourceProxy.string.mapnik, 0, 18, 256, ".png",
			"http://tile.openstreetmap.org/");

	public static final OpenStreetMapOnlineTileRendererBase CYCLEMAP = new XYRenderer(
			"CycleMap", ResourceProxy.string.cyclemap, 0, 17, 256, ".png",
			"http://a.andy.sandbox.cloudmade.com/tiles/cycle/",
			"http://b.andy.sandbox.cloudmade.com/tiles/cycle/",
			"http://c.andy.sandbox.cloudmade.com/tiles/cycle/");

	public static final OpenStreetMapOnlineTileRendererBase PUBLIC_TRANSPORT = new XYRenderer(
			"OSMPublicTransport", ResourceProxy.string.public_transport, 0, 17,
			256, ".png", "http://tile.xn--pnvkarte-m4a.de/tilegen/");

	public static final OpenStreetMapOnlineTileRendererBase BASE = new XYRenderer(
			"Base", ResourceProxy.string.base, 4, 17, 256, ".png",
			"http://topo.openstreetmap.de/base/");

	public static final OpenStreetMapOnlineTileRendererBase TOPO = new XYRenderer(
			"Topo", ResourceProxy.string.topo, 4, 17, 256, ".png",
			"http://topo.openstreetmap.de/topo/");

	public static final OpenStreetMapOnlineTileRendererBase HILLS = new XYRenderer(
			"Hills", ResourceProxy.string.hills, 8, 17, 256, ".png",
			"http://topo.geofabrik.de/hills/");

	public static final OpenStreetMapOnlineTileRendererBase CLOUDMADESTANDARDTILES = new CloudmadeRenderer(
			"CloudMadeStandardTiles",
			ResourceProxy.string.cloudmade_standard, 0, 18, 256, ".png",
			"http://a.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://b.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://c.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s");

	// FYI - This renderer has a tileSize of "6"
	public static final OpenStreetMapOnlineTileRendererBase CLOUDMADESMALLTILES = new CloudmadeRenderer(
			"CloudMadeSmallTiles",
			ResourceProxy.string.cloudmade_small, 0, 21, 64, ".png",
			"http://a.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://b.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s",
			"http://c.tile.cloudmade.com/%s/%d/%d/%d/%d/%d%s?token=%s");

	public static final OpenStreetMapOnlineTileRendererBase DEFAULT_RENDERER = MAPNIK;

	// The following renderers are overlays, not standalone map views.
	// They are therefore not in mRenderers.

	public static final OpenStreetMapOnlineTileRendererBase FIETS_OVERLAY_NL = new XYRenderer(
			"Fiets", ResourceProxy.string.fiets_nl, 3, 16, 256, ".png",
			"http://overlay.openstreetmap.nl/openfietskaart-overlay/");

	public static final OpenStreetMapOnlineTileRendererBase BASE_OVERLAY_NL = new XYRenderer(
			"BaseNL", ResourceProxy.string.base_nl, 0, 18, 256, ".png",
			"http://overlay.openstreetmap.nl/basemap/");

	public static final OpenStreetMapOnlineTileRendererBase ROADS_OVERLAY_NL = new XYRenderer(
			"RoadsNL", ResourceProxy.string.roads_nl, 0, 18, 256, ".png",
			"http://overlay.openstreetmap.nl/roads/");

	// FIXME the whole point of this implementation is that the list of
	// renderers should be extensible,
	// so that means making it possible to have a bigger or smaller list of
	// renderers
	// - there's a number of ways of doing that
	private static IOpenStreetMapRendererInfo[] mRenderers = new IOpenStreetMapRendererInfo[] {
			OSMARENDER, MAPNIK, CYCLEMAP, PUBLIC_TRANSPORT, BASE, TOPO, HILLS,
			CLOUDMADESTANDARDTILES, CLOUDMADESMALLTILES };
}
