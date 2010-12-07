/**
 * 
 */
package org.cmc.music.myid3.id3v2;

import org.cmc.music.common.ID3FrameType;

public class ID3v2OutputFrame
{
	public final String longFrameID;
	public final Number frameOrder;
	public final byte bytes[];
	public final ID3v2FrameFlags flags;

	public ID3v2OutputFrame(String longFrameID, byte[] bytes)
	{
		this(longFrameID, ID3FrameType.DEFAULT_FRAME_ORDER, bytes,
				new ID3v2FrameFlags());
	}

	public ID3v2OutputFrame(String longFrameID, byte[] bytes,
			final ID3v2FrameFlags flags)
	{
		this(longFrameID, ID3FrameType.DEFAULT_FRAME_ORDER, bytes, flags);
	}

	public ID3v2OutputFrame(String longFrameID, Number frame_order, byte[] bytes)
	{
		this(longFrameID, frame_order, bytes, new ID3v2FrameFlags());
	}

	public ID3v2OutputFrame(String longFrameID, Number frame_order,
			byte[] bytes, final ID3v2FrameFlags flags)
	{
		this.longFrameID = longFrameID;
		this.frameOrder = frame_order;
		// this.frame_type = frame_type;
		this.bytes = bytes;
		this.flags = flags;
	}

	public String toString()
	{
		return "[frame: " + longFrameID + ": " + bytes.length + "]";
	}
}