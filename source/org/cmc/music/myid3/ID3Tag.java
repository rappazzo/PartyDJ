package org.cmc.music.myid3;

import java.util.Vector;

import org.cmc.music.metadata.IMusicMetadata;

public abstract class ID3Tag
{
	public static final int TAG_TYPE_ID3_V1 = 1;
	public static final int TAG_TYPE_ID3_V2 = 2;

	public final int tagType;
	public final byte bytes[];
	public final IMusicMetadata values;

	public ID3Tag(int tag_type, byte[] bytes, IMusicMetadata values)
	{
		this.tagType = tag_type;
		this.bytes = bytes;
		this.values = values;
	}

	public static class V1 extends ID3Tag
	{
		public V1(byte[] bytes, IMusicMetadata values)
		{
			super(ID3Tag.TAG_TYPE_ID3_V1, bytes, values);
		}
	}

	public static class V2 extends ID3Tag
	{
		public final Vector frames;
		public final byte versionMajor;
		public final byte versionMinor;

		public V2(final byte versionMajor, final byte versionMinor,
				byte[] bytes, IMusicMetadata values, final Vector frames)
		{
			super(ID3Tag.TAG_TYPE_ID3_V2, bytes, values);

			this.versionMajor = versionMajor;
			this.versionMinor = versionMinor;
			this.frames = frames;
		}

	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();

		result.append("{ID3Tag. ");

		result.append("values: " + values);

		result.append(" }");

		return result.toString();
	}

}
