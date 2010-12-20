package org.andnav.osm.tileprovider.modules;

import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;

public interface IPreferredRenderChangedReceiver {
	/**
	 * Indicates that the supplied renderer is now requested to be the preferred
	 * renderer. An implementor should adjust its mode of operation based on the
	 * characteristics of the renderer, but it's completely up to the
	 * implementation on how much to respond to this information. Depending on
	 * the way the tile provider operates, it may choose to change the size of
	 * tiles it produces, the output format, or where it obtain tiles from.
	 * 
	 * @return true if implementation uses a data connection, false otherwise
	 */
	public abstract void onPreferredRendererChanged(
			IOpenStreetMapRendererInfo renderer);
}
