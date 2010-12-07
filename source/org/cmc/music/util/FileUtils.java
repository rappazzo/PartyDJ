package org.cmc.music.util;

import java.io.IOException;
import java.io.InputStream;

import org.cmc.music.myid3.id3v1.MyID3v1Constants;

public abstract class FileUtils implements MyID3v1Constants
{

	public static final byte[] readArray(InputStream is, int length)
			throws IOException
	{
		byte result[] = new byte[length];
		int total = 0;
		while (total < length)
		{
			int read = is.read(result, total, length - total);
			if (read < 0)
				throw new IOException("bad read");
			total += read;
		}
		return result;
	}

}
