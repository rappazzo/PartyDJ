package org.cmc.music.metadata;

public final class UnknownUserTextValue
{
	public UnknownUserTextValue(String key, String value)
	{
		super();
		this.key = key;
		this.value = value;
	}

	public final String key, value;

	public int hashCode()
	{
		return key.hashCode() ^ value.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof UnknownUserTextValue))
			return false;
		UnknownUserTextValue other = (UnknownUserTextValue) obj;
		return this.key.equals(other.key) && this.value.equals(other.value);
	}
}