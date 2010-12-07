/*
 * Written By Charles M. Chen 
 * 
 * Created on Jan 1, 2006
 *
 */

package org.cmc.music.myid3.id3v1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.cmc.music.common.ID3v1Genre;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataConstants;
import org.cmc.music.myid3.ID3Tag;
import org.cmc.music.myid3.MyID3Listener;
import org.cmc.music.util.Debug;
import org.cmc.music.util.FileUtils;

public class MyID3v1 implements MusicMetadataConstants, MyID3v1Constants
{

	public byte[] toTag(MyID3Listener listener, IMusicMetadata values,
			boolean strict) throws UnsupportedEncodingException
	{
		byte result[] = new byte[ID3_V1_TAG_LENGTH];

		int index = 0;
		result[index++] = 0x54; // T
		result[index++] = 0x41; // A
		result[index++] = 0x47; // G

		writeField(result, index, 30, values.getSongTitle());
		index += 30;

		writeField(result, index, 30, values.getArtist());
		index += 30;

		writeField(result, index, 30, values.getAlbum());
		index += 30;

		{
			Number value = values.getYear();
			writeField(result, index, 4, value == null ? null : "" + value);
			index += 4;
		}

		Number trackNumber = null;
		{
			Number value = values.getTrackNumberNumeric();

			if (value != null && value.intValue() >= 0
					&& value.intValue() < 256)
				trackNumber = value;
		}

		String comment = null;
		if (values.getComments().size() > 0)
			comment = (String) values.getComments().get(0);

		// TODO: should we ignore 0x00 and 0xff track numbers?
		if (trackNumber == null)
		{
			writeField(result, index, 30, comment);
			index += 30;
		} else
		{
			writeField(result, index, 28, comment);
			index += 28;

			result[index++] = 0;
			result[index++] = (byte) trackNumber.intValue();
		}

		{
			Object o = values.getGenreID();
			if (o == null)
				o = values.getGenreName();

			if (o != null && (o instanceof String))
			{
				String genre_name = (String) o;
				Number genre_id = ID3v1Genre.getIDForName(genre_name);
				if (genre_id != null)
				{
					o = genre_id;
					// Debug.debug("fixed genre", genre_name);
					// Debug.debug("fixed genre", genre_id);
					// Debug.dumpStack();
				}
			}

			if (o != null && !(o instanceof Number))
			{
				if (null != listener)
					listener.log("Discarding invalid genre in ID3v1 tag", o
							+ " (" + Debug.getType(o) + ")");
				// Debug.dumpStack();
			} else
			{
				Number value = (Number) o;

				if (value != null && value.intValue() >= 0
						&& value.intValue() < 80)
					result[index++] = (byte) value.intValue();
				else
					result[index++] = 0;
			}
		}

		// Debug.debug("index", index);

		return result;
	}

	private void writeField(byte bytes[], int start, int max_length, String s)
			throws UnsupportedEncodingException
	{
		if (s == null)
		{
			for (int i = 0; i < max_length; i++)
				bytes[i + start] = 0;
			return;
		}

		byte value[] = s.getBytes(DEFAULT_CHAR_ENCODING);
		int count = Math.min(value.length, max_length);
		for (int i = 0; i < count; i++)
			bytes[i + start] = value[i];
		for (int i = count; i < max_length; i++)
			bytes[i + start] = 0;
	}

	// private boolean isValidIso8859(byte bytes[], int start, int length)
	// {
	// for (int i = start; i < start + length; i++)
	// {
	// int value = 0xff & bytes[i];
	// if (value >= 0x20 && value <= 0x7E)
	// ;
	// else if (value >= 0xA0 && value <= 0xFF)
	// ;
	// else
	// {
	// Debug.debug("bad byte[" + i + "/" + length + "]: " + value
	// + " (0x" + Integer.toHexString(value) + "");
	// return false;
	// }
	// }
	// return true;
	// }

	private String getField(MyID3Listener listener, byte bytes[], int start,
			int length)
	{
		for (int i = start; i < start + length; i++)
		{
			if (bytes[i] == 0)
			{
				length = i - start;
				break;
			}
		}
		// if (null != listener)
		// listener
		// .log("isValidIso8859", isValidIso8859(bytes, start, length));

		if (length > 0)
		{
			try
			{
				String result = new String(bytes, start, length,
						DEFAULT_CHAR_ENCODING);
				result = result.trim();
				if (result.length() < 1)
					return null;
				return result;
			} catch (Throwable e)
			{
				Debug.debug(e);
			}
		}

		return null;
	}

	public IMusicMetadata parseTags(byte bytes[], boolean strict)
	{
		return parseTags(null, bytes, strict);
	}

	public IMusicMetadata parseTags(MyID3Listener listener, byte bytes[],
			boolean strict)
	{
		IMusicMetadata result = new MusicMetadata("ID3v1");

		int counter = 3;
		String title = getField(listener, bytes, counter, 30);
		counter += 30;
		result.setSongTitle(title);
		if (null != listener)
			listener.logWithLength("id3v1 title", title);

		String artist = getField(listener, bytes, counter, 30);
		counter += 30;
		result.setArtist(artist);
		if (null != listener)
			listener.logWithLength("id3v1 artist", artist);

		String album = getField(listener, bytes, counter, 30);
		counter += 30;
		result.setAlbum(album);
		if (null != listener)
			listener.logWithLength("id3v1 album", album);

		String yearString = getField(listener, bytes, counter, 4);
		counter += 4;
		Number year = null;
		try
		{
			if (null != yearString)
				year = Integer.valueOf(yearString);
		} catch (NumberFormatException e)
		{
			// ignore
		}
		result.setYear(year);
		if (null != listener)
		{
			listener.logWithLength("id3v1 year", yearString);
			if (null != yearString)
				listener.log("id3v1 year", year);
		}

		String comment = getField(listener, bytes, counter, 30);
		counter += 30;
		if (null != comment)
			result.addComment(comment);
		if (null != listener)
			listener.logWithLength("id3v1 comment", comment);

		if (bytes[counter - 2] == 0 && bytes[counter - 1] != 0)
		{
			int trackNumber = 0xff & bytes[counter - 1];
			// TODO: should we ignore 0x00 and 0xff track numbers?
			result.setTrackNumberNumeric(new Integer(trackNumber));

			if (null != listener)
				listener.log("id3v1 trackNumber: " + trackNumber);
		}

		int genre = 0xff & bytes[counter];
		if (genre < 80 && genre > 0)
		{
			result.setGenreID(new Integer(genre));
			result.setGenreName(ID3v1Genre.getNameForID(new Integer(genre)));

			if (null != listener)
				listener.log("id3v1 genre: " + genre);
		}

		if (null != listener)
			listener.log();

		return result;
	}

	public boolean hasID3v1(File file) throws IOException
	{
		if (file == null || !file.exists())
			return false;

		long length = file.length();

		if (length < ID3_V1_TAG_LENGTH)
			return false;

		byte bytes[];
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			is.skip(length - ID3_V1_TAG_LENGTH);

			bytes = FileUtils.readArray(is, ID3_V1_TAG_LENGTH);
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

		if (bytes[0] != 'T')
			return false;
		if (bytes[1] != 'A')
			return false;
		if (bytes[2] != 'G')
			return false;

		return true;
	}

	public ID3Tag readID3v1(File file, boolean strict) throws IOException
	{
		return readID3v1(null, file, strict);
	}

	public ID3Tag.V1 readID3v1(MyID3Listener listener, File file, boolean strict)
			throws IOException
	{
		if (file == null || !file.exists())
			return null;

		long length = file.length();

		if (length < ID3_V1_TAG_LENGTH)
			return null;

		byte bytes[];
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			is = new BufferedInputStream(is);

			is.skip(length - ID3_V1_TAG_LENGTH);

			bytes = FileUtils.readArray(is, ID3_V1_TAG_LENGTH);
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

		if (bytes[0] != 'T')
			return null;
		if (bytes[1] != 'A')
			return null;
		if (bytes[2] != 'G')
			return null;

		if (null != listener)
			listener.log("ID3v1 tag found.");

		IMusicMetadata tags = new MyID3v1().parseTags(listener, bytes, strict);

		return new ID3Tag.V1(bytes, tags);
	}

}
