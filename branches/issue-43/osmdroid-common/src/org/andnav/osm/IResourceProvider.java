package org.andnav.osm;

public interface IResourceProvider {

	/**
	 * Get the string resource for the specified key.
	 * @param pKey the resource key
	 * @return the localized string
	 */
	public String getString(final Resource pKey);
}
