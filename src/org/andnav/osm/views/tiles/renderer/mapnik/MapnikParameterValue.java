package org.andnav.osm.views.tiles.renderer.mapnik;

public abstract class MapnikParameterValue {

	public enum MapnikParameterValueType
	{
		DOUBLE,
		INTEGER,
		STRING
	}
	
	public MapnikParameterValueType mType;
	protected Object mValue;
	
	public MapnikParameterValue(MapnikParameterValueType type, Object value)
	{
		mType = type;
		mValue = value;
	}
	
	public abstract Object getValue();
	
	public abstract void setValue(Object v);

	public abstract String toString();
	
	public MapnikParameterValue add(MapnikParameterValue other)
	{
		if (this.mType == MapnikParameterValueType.INTEGER)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterIntValue((Integer)this.mValue + (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Integer)this.mValue + (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else if (this.mType == MapnikParameterValueType.DOUBLE)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterDoubleValue((Double)this.mValue + (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Double)this.mValue + (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else /* this.mType is a string */
			throw new IllegalArgumentException();
	}
	
	public MapnikParameterValue subtract(MapnikParameterValue other)
	{
		if (this.mType == MapnikParameterValueType.INTEGER)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterIntValue((Integer)this.mValue - (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Integer)this.mValue - (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else if (this.mType == MapnikParameterValueType.DOUBLE)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterDoubleValue((Double)this.mValue - (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Double)this.mValue - (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else /* this.mType is a string */
			throw new IllegalArgumentException();
	}
	
	public MapnikParameterValue multiply(MapnikParameterValue other)
	{
		if (this.mType == MapnikParameterValueType.INTEGER)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterIntValue((Integer)this.mValue * (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Integer)this.mValue * (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else if (this.mType == MapnikParameterValueType.DOUBLE)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterDoubleValue((Double)this.mValue * (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Double)this.mValue * (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else /* this.mType is a string */
			throw new IllegalArgumentException();
	}
	
	public MapnikParameterValue divide(MapnikParameterValue other)
	{
		if (this.mType == MapnikParameterValueType.INTEGER)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterIntValue((Integer)this.mValue / (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Integer)this.mValue / (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else if (this.mType == MapnikParameterValueType.DOUBLE)
		{
			if (other.mType == MapnikParameterValueType.INTEGER)
				return new MapnikParameterDoubleValue((Double)this.mValue / (Integer)other.mValue);
			else if (other.mType == MapnikParameterValueType.DOUBLE)
				return new MapnikParameterDoubleValue((Double)this.mValue / (Double)other.mValue);
			else
				throw new IllegalArgumentException();
		}
		else /* this.mType is a string */
			throw new IllegalArgumentException();
	}
}
