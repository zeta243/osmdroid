package org.andnav.osm;

import android.content.Context;

public class ResourceProvider implements IResourceProvider {

	private final Context mContext;
	
	public ResourceProvider(final Context pContext) {
		super();
		mContext = pContext;
	}

	@Override
	public String getString(final Resource pKey) {
		switch(pKey) {
			case base: return mContext.getString(R.string.base);
			case osmarender: return mContext.getString(R.string.osmarender);
			case mapnik: return mContext.getString(R.string.mapnik);
			case cyclemap: return mContext.getString(R.string.cyclemap);
			case openareal_sat: return mContext.getString(R.string.openareal_sat);
			case topo: return mContext.getString(R.string.topo);
			case hills: return mContext.getString(R.string.hills);
			case cloudmade_small: return mContext.getString(R.string.cloudmade_small);
			case cloudmade_standard: return mContext.getString(R.string.cloudmade_standard);
			default: throw new IllegalArgumentException("No resource for " + pKey);
		}
	}
}
