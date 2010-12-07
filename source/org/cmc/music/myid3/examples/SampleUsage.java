package org.cmc.music.myid3.examples;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.cmc.music.common.ID3ReadException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.cmc.music.myid3.id3v2.MyID3v2Frame;
import org.cmc.music.util.Debug;
import org.cmc.music.util.MyFileSystem;

public class SampleUsage
{

	/*
	 * Example of how to use the simple interface to ID3 data.
	 * 
	 * The simple interface hides the differences between version 1 and 2 tags,
	 * and presents values as simple Java values.
	 */
	public void readID3Simple(File mp3File) throws IOException,
			ID3ReadException
	{
		if (!mp3File.getName().toLowerCase().endsWith(".mp3"))
			return;

		Debug.debug();
		Debug.debug("file", mp3File);

		MusicMetadataSet src_set = new MyID3().read(mp3File);

		if (src_set == null)
		{
			Debug.debug("No id3 metadata found.");
			return;
		}

		/*
		 * MusicMetadataSet aggregates the metadata parsed from ID3v1 and ID3v2
		 * tags, each whose data is represented by a MusicMetadata object.
		 * 
		 * It also contains the "merged" MusicMetadata object, which tries to
		 * cherrypick the "best" values from each source.
		 * 
		 * The "merged" MusicMetadata's values are also "cleaned," ie. bad
		 * values are discarded.
		 */
		Debug.debug("src_set", src_set); // dump all info.
		String artist = src_set.merged.getArtist();
		Debug.debug("artist", artist);
		String album = src_set.merged.getAlbum();
		Debug.debug("album", album);
		String songTitle = src_set.merged.getSongTitle();
		Debug.debug("songTitle", songTitle);
	}

	/*
	 * Example of how to specifically look at ID3v2 tag frames.
	 * 
	 * This is not recommended; the "simple" interface is much easier to use.
	 */
	public void readID3v2Frames(File mp3File) throws IOException,
			ID3ReadException
	{
		if (!mp3File.getName().toLowerCase().endsWith(".mp3"))
			return;

		Debug.debug();
		Debug.debug("file", mp3File);

		MusicMetadataSet src_set = new MyID3().read(mp3File);

		if (src_set == null)
		{
			Debug.debug("No id3 metadata found.");
			return;
		}

		Vector id3v2_frames = src_set.id3v2Raw.frames; // 
		if (id3v2_frames.size() > 1)
		{
			MyID3v2Frame first_frame = (MyID3v2Frame) id3v2_frames.get(0);
			String frame_frame_id = first_frame.frameID;
			byte frame_frame_bytes[] = first_frame.dataBytes;
			Debug.debug("\t" + "frame_frame_id", frame_frame_id);
			Debug.debug("\t" + "frame_frame_bytes", frame_frame_bytes);
		}
	}

	public void readMP3Metadata(File mp3File) throws IOException,
			ID3ReadException
	{
		if (!mp3File.getName().toLowerCase().endsWith(".mp3"))
			return;

		Debug.debug();
		Debug.debug("file", mp3File);

		MusicMetadataSet src_set = new MyID3().read(mp3File);

		if (src_set == null)
		{
			Debug.debug("No id3 metadata found.");
			return;
		}

		/*
		 * MusicMetadataSet aggregates the metadata parsed from ID3v1 and ID3v2
		 * tags, each whose data is represented by a MusicMetadata object.
		 * 
		 * It also contains the "merged" MusicMetadata object, which tries to
		 * cherrypick the "best" values from each source.
		 */
		Debug.debug("src_set", src_set); // dump all info.
		Debug.debug("src_set", src_set.merged.getArtist());
		Debug.debug("src_set", src_set.merged.getAlbum());
		Debug.debug("src_set", src_set.merged.getSongTitle());

		/*
		 * Although the "merged" MusicMetadata is usually best, you can also
		 * directly access the metadata from each tag.
		 * 
		 * This includes the "raw" and "clean" versions.
		 */
		String id3v1_artist = src_set.id3v1Raw.values.getArtist();
		String id3v1_artist_clean = src_set.id3v1Clean.getArtist();
		String id3v2_artist = src_set.id3v2Raw.values.getArtist();
		String id3v2_artist_clean = src_set.id3v2Clean.getArtist();

		byte id3v1_tag_bytes[] = src_set.id3v1Raw.bytes; // tag bytes
		byte id3v2_tag_bytes[] = src_set.id3v2Raw.bytes; // tag bytes

		Vector id3v2_frames = src_set.id3v2Raw.frames; // 
		if (id3v2_frames.size() > 1)
		{
			MyID3v2Frame first_frame = (MyID3v2Frame) id3v2_frames.get(0);
			String frame_frame_id = first_frame.frameID;
			byte frame_frame_bytes[] = first_frame.dataBytes;
		}
	}

	public static void fixMP3Files(File srcFolder, File dstFolder)
			throws Exception
	{
		Vector srcs = new MyFileSystem().getChildrenFiles(srcFolder);

		for (int i = 0; i < srcs.size(); i++)
		{
			File src = (File) srcs.get(i);
			if (!src.getName().toLowerCase().endsWith(".mp3"))
				continue;

			Debug.debug();
			Debug.debug("file", src);

			File dst = new File(dstFolder, src.getName());

			MusicMetadataSet src_set = new MyID3().read(src);

			if (src_set == null)
			{
				System.out.println("No id3 metadata found.");
				continue;
			}

			Debug.debug("src_set", src_set); // dump all info.
			Debug.debug("src_set", src_set.merged.getArtist());
			Debug.debug("src_set", src_set.merged.getAlbum());
			Debug.debug("src_set", src_set.merged.getSongTitle());

			String id3v1_artist = src_set.id3v1Raw.values.getArtist();
			String id3v2_artist = src_set.id3v2Raw.values.getArtist();

			byte id3v1_tag_bytes[] = src_set.id3v1Raw.bytes; // tag bytes
			byte id3v2_tag_bytes[] = src_set.id3v2Raw.bytes; // tag bytes

			Vector id3v2_frames = src_set.id3v2Raw.frames; // 
			if (id3v2_frames.size() > 1)
			{
				MyID3v2Frame first_frame = (MyID3v2Frame) id3v2_frames.get(0);
				String frame_frame_id = first_frame.frameID;
				byte frame_frame_bytes[] = first_frame.dataBytes;
			}

			new MyID3().write(src, dst, src_set, src_set.merged);
			if (dst.exists())
			{
				MusicMetadataSet dst_set = new MyID3().read(dst);
				Debug.debug("dst_set", dst_set);

				String src_s = src_set.merged.toString();
				String dst_s = dst_set.merged.toString();
				if (!src_s.equals(dst_s))
				{
					Debug.debug("mismatch src", src_s);
					Debug.debug("mismatch dst", dst_s);
					Debug.dumpStack();
				}
			}
		}
	}

	/*
	 * Example of how to set a field value (artist, in this case) to an mp3's
	 * ID3 tags.
	 * 
	 * This will add tags if they don't already exist.
	 * 
	 * If the DO already exist, other field values are kept.
	 * 
	 * Note that you must have a src and dst file. If you wish to update the
	 * file, use a temporary file as your dst, and replace the src file with the
	 * temp.
	 */
	public static void setFieldExample(File srcFile, File dstFile, String artist)
			throws Exception
	{
		// Since this is an example, I do no checking of parameters.

		MusicMetadataSet src_set = new MyID3().read(srcFile);

		IMusicMetadata metadataToWrite;
		if (src_set != null)
		{
			// The source file DID have an ID3v1 or ID3v2 tag (or both).
			// We'll update those values.
			metadataToWrite = src_set.merged;
		} else
		{
			// The file did not have an ID3v1 or ID3v2 tag, so
			// we need to add new tag(s).
			metadataToWrite = MusicMetadata.createEmptyMetadata();
		}

		// here we set or update the artist field.
		metadataToWrite.setArtist(artist);

		new MyID3().write(srcFile, dstFile, src_set, metadataToWrite);
	}

}
