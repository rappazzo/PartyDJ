package org.cmc.music.myid3.id3v2;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.cmc.music.common.ID3ReadException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.myid3.ID3Tag;
import org.cmc.music.myid3.MyID3Listener;
import org.cmc.music.myid3.id3v1.MyID3v1Constants;
import org.cmc.music.util.Debug;
import org.cmc.music.util.FileUtils;

public class MyID3v2 implements MyID3v1Constants
{

	private static final int ID3v2_HEADER_LENGTH = 10;

	public byte[] readID3v2Head(File file, boolean strict) throws IOException
	{

		if (file == null || !file.exists())
			return null;

		long length = file.length();

		if (length < ID3v2_HEADER_LENGTH)
			return null;

		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			byte header[];
			header = FileUtils.readArray(is, ID3v2_HEADER_LENGTH);

			if (header[0] != 0x49) // I
				return null;
			if (header[1] != 0x44) // D
				return null;
			if (header[2] != 0x33) // 3
				return null;

			int flags = header[5];
			boolean has_footer = (flags & (1 << 4)) > 0;

			Number tagLength = MyID3v2Read.readSynchsafeInt(header, 6);
			if (tagLength == null)
				return null;

			int bodyLength = tagLength.intValue();
			if (has_footer)
				bodyLength += ID3v2_HEADER_LENGTH;

			if (ID3v2_HEADER_LENGTH + bodyLength > length)
				return null;

			byte body[] = FileUtils.readArray(is, bodyLength);

			byte result[] = new byte[header.length + body.length];

			System.arraycopy(header, 0, result, 0, header.length);
			System.arraycopy(body, 0, result, header.length, body.length);

			return result;
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				Debug.debug(e);
			}
		}
	}

	public long findID3v2HeadLength(File file) throws IOException
	{
		if (file == null || !file.exists())
			return 0;

		long length = file.length();

		if (length < ID3v2_HEADER_LENGTH)
			return 0;

		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			byte header[];
			header = FileUtils.readArray(is, ID3v2_HEADER_LENGTH);

			if (header[0] != 0x49) // I
				return 0;
			if (header[1] != 0x44) // D
				return 0;
			if (header[2] != 0x33) // 3
				return 0;

			int flags = header[5];
			boolean has_footer = (flags & (1 << 4)) > 0;

			Number tagLength = MyID3v2Read.readSynchsafeInt(header, 6);
			if (tagLength == null)
				return 0;

			int totalLength = ID3v2_HEADER_LENGTH + tagLength.intValue();
			if (has_footer)
				totalLength += ID3v2_HEADER_LENGTH;

			return totalLength;
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				Debug.debug(e);
			}
		}
	}

	public int findID3v2TailLength(File file, boolean hasId3v1)
			throws IOException
	{
		if (file == null || !file.exists())
			return 0;

		long length = file.length();

		int index = hasId3v1 ? ID3_V1_TAG_LENGTH : 0;
		index += ID3v2_HEADER_LENGTH;

		if (index > length)
			return 0;

		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			is.skip(length - index);

			byte footer[];
			footer = FileUtils.readArray(is, ID3v2_HEADER_LENGTH);

			if (footer[0] != 0x33) // 3
				return 0;
			if (footer[1] != 0x44) // D
				return 0;
			if (footer[2] != 0x49) // I
				return 0;

			Number tagLength = MyID3v2Read.readSynchsafeInt(footer, 6);
			if (tagLength == null)
				return 0;

			int totalLength = ID3v2_HEADER_LENGTH + ID3v2_HEADER_LENGTH
					+ tagLength.intValue();

			return totalLength;
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				Debug.debug(e);
			}
		}
	}

	public byte[] readID3v2Tail(File file, boolean hasId3v1, boolean strict)
			throws IOException
	{
		if (file == null || !file.exists())
			return null;

		long length = file.length();

		int index = hasId3v1 ? ID3_V1_TAG_LENGTH : 0;
		index += ID3v2_HEADER_LENGTH;

		if (index > length)
			return null;

		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			is.skip(length - index);

			byte footer[];
			footer = FileUtils.readArray(is, ID3v2_HEADER_LENGTH);

			if (footer[2] != 0x33) // 3
				return null;
			if (footer[1] != 0x44) // D
				return null;
			if (footer[0] != 0x49) // I
				return null;

			Number tagLength = MyID3v2Read.readSynchsafeInt(footer, 6);
			if (tagLength == null)
				return null;

			int bodyLength = tagLength.intValue();
			if (index + bodyLength > length)
				return null;

			is.close();
			is = null;

			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			long skip = length;
			skip -= ID3v2_HEADER_LENGTH;
			skip -= bodyLength;
			skip -= ID3v2_HEADER_LENGTH;
			if (hasId3v1)
				skip -= ID3_V1_TAG_LENGTH;
			is.skip(skip);

			byte header_and_body[] = FileUtils.readArray(is,
					ID3v2_HEADER_LENGTH + bodyLength + ID3v2_HEADER_LENGTH);

			byte result[] = header_and_body;

			return result;
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				Debug.debug(e);
			}
		}
	}

	public ID3Tag.V2 readID3v2(MyID3Listener listener, byte bytes[],
			boolean strict) throws IOException, ID3ReadException
	{
		MyID3v2Read parser = new MyID3v2Read(listener,
				new ByteArrayInputStream(bytes), false);
		while (!parser.isComplete())
		{
			parser.iteration();
		}
		if (parser.isError())
		{
			if (listener != null)
				listener.log("id3v2 error", parser.getErrorMessage());

			parser.dump();
			return null;
		}

		if (!parser.hasTags())
			return null;

		Vector frames = parser.getTags();

		// Debug.debug("tags" , tags.toString());

		IMusicMetadata metadata = ID3v2FrameTranslation
				.translateFramesToMetadata(listener, strict, frames);

		// IMusicMetadata values = ID3v2DataMapping.translateFrames(listener,
		// strict, tags);

		byte version_major = parser.getVersionMajor();
		byte version_minor = parser.getVersionMinor();

		if (null != listener)
			listener.log();

		return new ID3Tag.V2(version_major, version_minor, bytes, metadata,
				frames);
	}

	public ID3Tag.V2 readID3v2(MyID3Listener listener, File file,
			boolean hasId3v1, boolean strict) throws IOException,
			ID3ReadException
	{
		if (file == null || !file.exists())
			return null;

		byte bytes[] = null;
		bytes = readID3v2Tail(file, hasId3v1, strict);
		if (bytes == null)
			bytes = readID3v2Head(file, strict);

		if (bytes == null)
			return null;

		if (null != listener)
			listener.log("ID3v2 tag found: " + bytes.length + " bytes");

		return readID3v2(listener, bytes, strict);
	}

}
