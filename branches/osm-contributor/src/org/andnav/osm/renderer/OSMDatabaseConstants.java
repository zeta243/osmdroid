package org.andnav.osm.renderer;

public interface OSMDatabaseConstants {
		public static final String DATABASE_NAME = "osm_db";
		public static final int DATABASE_VERSION = 1;

		// Nodes - ie the actual "points" on the map.
		public static final String T_NODE = "nodes";
		public static final String T_NODE_ID        = "_id";
		public static final String T_NODE_LATITUDE  = "latitude";
		public static final String T_NODE_LONGITUDE = "longitude";
		
		// tags - ie the name/value attributes for the nodes
		public static final String T_NODE_TAG = "node_tags";
		public static final String T_NODE_TAG_NODE  = "node_id"; // References T_NODE(T_NODE_ID)
		public static final String T_NODE_TAG_KEY   = "key";
		public static final String T_NODE_TAG_VALUE = "value";
		
		// paths - make up the actual roads etc.
		public static final String T_PATH = "paths";
		public static final String T_PATH_ID = "_id";
		
		// list of nodes in each path
		public static final String T_PATH_NODE = "path_nodes";
		public static final String T_PATH_NODE_PATH = "path_id"; // References T_PATH(T_PATH_ID)
		public static final String T_PATH_NODE_NODE = "node_id"; // References T_NODE(T_NODE_ID)
		
		// tags for each path - ie name/value attributes for the paths
		public static final String T_PATH_TAG = "path_tags";
		public static final String T_PATH_TAG_PATH  = "path_id"; // References T_PATH(T_PATH_ID)
		public static final String T_PATH_TAG_KEY   = "key";
		public static final String T_PATH_TAG_VALUE = "value";
		
		// to make it easy to find which nodes are on a given map tile
		public static final String T_TILE = "tiles";
		public static final String T_TILE_ID      = "_id";
		public static final String T_TILE_ZOOM    = "zoom_level";
		public static final String T_TILE_LAT_IDX = "latitude_index";
		public static final String T_TILE_LON_IDX = "longitude_index";
		
		// List of nodes within each tile
		public static final String T_TILE_NODES = "tile_nodes";
		public static final String T_TILE_NODES_TILE = "tile_id"; // References T_TILE(T_TILE_ID)
		public static final String T_TILE_NODES_NODE = "node_id"; // References T_NODES(T_NODE_ID)
}
