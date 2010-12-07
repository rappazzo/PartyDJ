package org.cmc.music.myid3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.cmc.music.common.ID3ReadException;
import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.id3v1.MyID3v1;
import org.cmc.music.myid3.id3v1.MyID3v1Constants;
import org.cmc.music.myid3.id3v2.MyID3v2;
import org.cmc.music.myid3.id3v2.MyID3v2Write;
import org.cmc.music.util.Debug;

/**
 * The primary interface to the MyID3 library.
 * <p>
 * Almost all of the MyID3 library's core functionality can be accessed through
 * it's methods.
 * <p>
 * See the source of the SampleUsage class and other classes in the
 * org.cmc.music.myid3.examples package for examples.
 * 
 * @see org.cmc.music.myid3.examples.SampleUsage
 */
public class MyID3 implements MyID3v1Constants
{
	/**
	 * Write MP3 file with specific metadata, drawing song data from an existing
	 * mp3 file.
	 * <p>
	 * 
	 * @param file
	 *            File to read non-metadata (ie. song data) from. Will be
	 *            overwritten with new mp3 file.
	 * @param set
	 *            MusicMetadataSet, usually read from mp3 file.
	 * @param values
	 *            IMusicMetadata, a specific group of values to write.
	 * @see MusicMetadataSet, IMusicMetadata
	 */
	public void update(File file, MusicMetadataSet set, IMusicMetadata values)
			throws UnsupportedEncodingException, IOException, ID3WriteException
	{
		File temp = null;
		try
		{
			temp = File.createTempFile(file.getName(), ".tmp", file
					.getParentFile());
			write(file, temp, set, values);
			temp.setLastModified(file.lastModified());
			file.delete();
			temp.renameTo(file);
		} finally
		{

		}
	}

	/**
	 * Write MP3 file with specific metadata, drawing song data from an existing
	 * mp3 file.
	 * <p>
	 * 
	 * @param file
	 *            File to read non-metadata (ie. song data) from. Will be
	 *            overwritten with new mp3 file.
	 * @param set
	 *            MusicMetadataSet, usually read from mp3 file.
	 * @param values
	 *            IMusicMetadata, a specific group of values to write.
	 * @param filter
	 *            MyID3v2Write.Filter, can be used to prevent ID3v2 frames from
	 *            written on a case-by-case basis.
	 * @param listener
	 *            MyID3Listener, observer of the write process.
	 * @see MusicMetadataSet, IMusicMetadata, MyID3Listener, MyID3v2Write.Filter
	 */
	public void update(File file, MusicMetadataSet set, IMusicMetadata values,
			MyID3v2Write.Filter filter, MyID3Listener listener)
			throws UnsupportedEncodingException, IOException, ID3WriteException
	{

		File temp = null;
		try
		{
			temp = File.createTempFile(file.getName(), ".tmp", file
					.getParentFile());

			write(file, temp, set, values, filter, listener);
			temp.setLastModified(file.lastModified());
			file.delete();
			temp.renameTo(file);
		} catch (UnsupportedEncodingException e)
		{
			if (temp != null && temp.exists() && file.exists())
				temp.delete();
			throw e;
		} catch (IOException e)
		{
			if (temp != null && temp.exists() && file.exists())
				temp.delete();
			throw e;
		} catch (ID3WriteException e)
		{
			if (temp != null && temp.exists() && file.exists())
				temp.delete();
			throw e;
		}
	}

	/**
	 * Write MP3 file with specific metadata, drawing song data from an existing
	 * mp3 file.
	 * <p>
	 * 
	 * @param src
	 *            File to read non-metadata (ie. song data) from.
	 * @param dst
	 *            File to overwrite with new mp3 file.
	 * @param set
	 *            MusicMetadataSet, usually read from mp3 file.
	 * @param values
	 *            IMusicMetadata, a specific group of values to write.
	 * @see MusicMetadataSet, IMusicMetadata
	 */
	public void write(File src, File dst, MusicMetadataSet set,
			IMusicMetadata values) throws UnsupportedEncodingException,
			IOException, ID3WriteException
	{
		write(src, dst, set, values, null, null);
	}

	/**
	 * Write MP3 file with specific metadata, drawing song data from an existing
	 * mp3 file.
	 * <p>
	 * 
	 * @param src
	 *            File to read non-metadata (ie. song data) from.
	 * @param dst
	 *            File to overwrite with new mp3 file.
	 * @param set
	 *            MusicMetadataSet, usually read from mp3 file.
	 * @param values
	 *            IMusicMetadata, a specific group of values to write.
	 * @param listener
	 *            MyID3Listener, observer of the write process.
	 * @see MusicMetadataSet, IMusicMetadata, MyID3Listener
	 */
	public void write(File src, File dst, MusicMetadataSet set,
			IMusicMetadata values, MyID3Listener listener)
			throws UnsupportedEncodingException, IOException, ID3WriteException
	{
		write(src, dst, set, values, null, listener);
	}

	/**
	 * Write MP3 file with specific metadata, drawing song data from an existing
	 * mp3 file.
	 * <p>
	 * 
	 * @param src
	 *            File to read non-metadata (ie. song data) from.
	 * @param dst
	 *            File to overwrite with new mp3 file.
	 * @param set
	 *            MusicMetadataSet, usually read from mp3 file.
	 * @param values
	 *            IMusicMetadata, a specific group of values to write.
	 * @param filter
	 *            MyID3v2Write.Filter, can be used to prevent ID3v2 frames from
	 *            written on a case-by-case basis.
	 * @param listener
	 *            MyID3Listener, observer of the write process.
	 * @see MusicMetadataSet, IMusicMetadata, MyID3Listener, MyID3v2Write.Filter
	 */
	public void write(File src, File dst, MusicMetadataSet set,
			IMusicMetadata values, MyID3v2Write.Filter filter,
			MyID3Listener listener) throws UnsupportedEncodingException,
			IOException, ID3WriteException
	{
		if (values == null)
			throw new IOException(Debug.getDebug("missing values", values));

		if (listener != null)
			listener.log();

		byte id3v1Tag[] = new MyID3v1().toTag(listener, values, strict);
		if (listener != null)
			listener.log("writing id3v1Tag", id3v1Tag == null ? "null" : ""
					+ id3v1Tag.length);

		byte id3v2TailTag[] = new MyID3v2Write().toTag(listener, filter, set,
				values, strict);
		if (listener != null)
			listener.log("writing id3v2TailTag", id3v2TailTag == null ? "null"
					: "" + id3v2TailTag.length);

		write(src, dst, id3v1Tag, id3v2TailTag, id3v2TailTag);

		if (listener != null)
			listener.log();
	}

	/**
	 * Removes all ID3v1 and ID3v2 tags from an mp3 file.
	 * <p>
	 * 
	 * @param src
	 *            File to read non-metadata (ie. song data) from.
	 * @param dst
	 *            File to overwrite with new mp3 file.
	 */
	public void removeTags(File src, File dst)
			throws UnsupportedEncodingException, IOException, ID3WriteException
	{
		byte id3v1Tag[] = null;
		byte id3v2HeadTag[] = null;
		byte id3v2TailTag[] = null;

		write(src, dst, id3v1Tag, id3v2HeadTag, id3v2TailTag);
	}

	/**
	 * Removes all ID3v1 and ID3v2 tags from an mp3 file.
	 * <p>
	 * 
	 * @param src
	 *            File to read non-metadata (ie. song data) from.
	 * @param dst
	 *            File to overwrite with new mp3 file.
	 */
	public void rewriteTags(File src, File dst)
			throws UnsupportedEncodingException, IOException, ID3WriteException
	{
		byte id3v1Tag[] = null;
		ID3Tag tag = new MyID3v1().readID3v1(src, strict);
		if (null != tag)
			id3v1Tag = tag.bytes;

		byte id3v2HeadTag[] = new MyID3v2().readID3v2Head(src, strict);

		boolean hasId3v1 = id3v1Tag != null;
		byte id3v2TailTag[] = new MyID3v2()
				.readID3v2Tail(src, hasId3v1, strict);

		write(src, dst, id3v1Tag, id3v2HeadTag, id3v2TailTag);
	}

	private boolean strict = false;

	/**
	 * Configures the library to not write ID3v1 tags.
	 */
	public void setStrict()
	{
		strict = true;
	}

	private boolean skipId3v1 = false;

	/**
	 * Configures the library to not write ID3v1 tags.
	 */
	public void setSkipId3v1()
	{
		skipId3v1 = true;
	}

	private boolean skipId3v2 = false;

	/**
	 * Configures the library to not write ID3v2 tags.
	 */
	public void setSkipId3v2()
	{
		skipId3v2 = true;
	}

	private boolean skipId3v2Head = false;

	/**
	 * Configures the library to not write ID3v2 head tags.
	 */
	public void setSkipId3v2Head()
	{
		skipId3v2Head = true;
	}

	private boolean skipId3v2Tail = false;

	/**
	 * Configures the library to not write ID3v2 tail tags.
	 */
	public void setSkipId3v2Tail()
	{
		skipId3v2Tail = true;
	}

	private void write(File src, File dst, byte id3v1Tag[],
			byte id3v2HeadTag[], byte id3v2TailTag[]) throws IOException
	{
		if (src == null || !src.exists())
			throw new IOException(Debug.getDebug("missing src", src));

		if (!src.getName().toLowerCase().endsWith(".mp3"))
			throw new IOException(Debug.getDebug("src not mp3", src));

		if (dst == null)
			throw new IOException(Debug.getDebug("missing dst", dst));

		if (dst.exists())
		{
			dst.delete();
			if (dst.exists())
				throw new IOException(Debug.getDebug("could not delete dst",
						dst));
		}

		boolean hasId3v1 = new MyID3v1().hasID3v1(src);

		long id3v1Length = hasId3v1 ? ID3_V1_TAG_LENGTH : 0;
		long id3v2HeadLength = new MyID3v2().findID3v2HeadLength(src);
		long id3v2TailLength = new MyID3v2().findID3v2TailLength(src, hasId3v1);

		OutputStream os = null;
		InputStream is = null;
		try
		{
			dst.getParentFile().mkdirs();
			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);

			if (!skipId3v2Head && !skipId3v2 && id3v2HeadTag != null)
				os.write(id3v2HeadTag);

			is = new FileInputStream(src);
			is = new BufferedInputStream(is);

			is.skip(id3v2HeadLength);

			long total_to_read = src.length();
			total_to_read -= id3v1Length;
			total_to_read -= id3v2HeadLength;
			total_to_read -= id3v2TailLength;

			byte buffer[] = new byte[1024];
			long total_read = 0;
			while (total_read < total_to_read)
			{
				int remainder = (int) (total_to_read - total_read);
				int readSize = Math.min(buffer.length, remainder);
				int read = is.read(buffer, 0, readSize);
				if (read <= 0)
					throw new IOException("unexpected EOF");

				os.write(buffer, 0, read);
				total_read += read;
			}

			if (!skipId3v2Tail && !skipId3v2 && id3v2TailTag != null)
				os.write(id3v2TailTag);
			if (!skipId3v1 && id3v1Tag != null)
				os.write(id3v1Tag);
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (Throwable e)
			{
				Debug.debug(e);
			}
			try
			{
				if (os != null)
					os.close();
			} catch (Throwable e)
			{
				Debug.debug(e);
			}
		}
	}

	/**
	 * Reads all metadata (ID3v1 & ID3v2) from MP3 file.
	 * <p>
	 * 
	 * @param file
	 *            File to read metadata (ie. song data) from.
	 * @return MusicMetadataSet, a set of IMusicMetadata value collections.
	 * @see MusicMetadataSet, IMusicMetadata
	 */
	public MusicMetadataSet read(File file) throws IOException,
			ID3ReadException
	{
		return read(file, null);
	}

	/**
	 * Reads all metadata (ID3v1 & ID3v2) from MP3 file.
	 * <p>
	 * 
	 * @param file
	 *            File to read metadata (ie. song data) from.
	 * @param listener
	 *            MyID3Listener, an observer.
	 * @return MusicMetadataSet, a set of IMusicMetadata value collections.
	 * @see MusicMetadataSet, IMusicMetadata
	 */
	public MusicMetadataSet read(File file, MyID3Listener listener)
			throws IOException, ID3ReadException
	{
		try
		{
			if (file == null || !file.exists())
				return null;

			if (!file.getName().toLowerCase().endsWith(".mp3"))
				return null;

			ID3Tag.V1 id3v1 = new MyID3v1().readID3v1(listener, file, strict);
			ID3Tag.V2 id3v2 = new MyID3v2().readID3v2(listener, file,
					id3v1 != null, strict);

			MusicMetadataSet result = MusicMetadataSet.factoryMethod(id3v1,
					id3v2, file.getName(), file.getParentFile().getName());

			return result;
		} catch (Error e)
		{
			Debug.debug("file", file);
			throw e;
		} catch (IOException e)
		{
			Debug.debug("file", file);
			throw e;
		}
	}

}
