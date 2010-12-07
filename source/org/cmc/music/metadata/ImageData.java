/**
 * 
 */
package org.cmc.music.metadata;

public final class ImageData extends Object
{
	public final byte imageData[];
	public final String mimeType;
	public final String description;
	public final int pictureType;

	public ImageData(final byte imageData[], final String mimeType,
			final String description, final int pictureType)
	{
		this.imageData = imageData;
		this.mimeType = mimeType;
		this.description = description;
		this.pictureType = pictureType;
	}

	public int hashCode()
	{
		return imageData.hashCode() ^ mimeType.hashCode()
				^ description.hashCode() ^ pictureType;
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof ImageData))
			return false;
		ImageData other = (ImageData) obj;
		if (this.pictureType != other.pictureType)
			return false;
		if (!this.mimeType.equals(other.mimeType))
			return false;
		if (!this.description.equals(other.description))
			return false;
		if (this.imageData.length != other.imageData.length)
			return false;
		for (int i = 0; i < this.imageData.length; i++)
			if (this.imageData[i] != other.imageData[i])
				return false;
		return true;
	}

}