package org.cmc.music.myid3.examples;

import java.io.File;
import java.io.IOException;

import org.cmc.music.common.ID3ReadException;
import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.cmc.music.myid3.MyID3Listener;
import org.cmc.music.myid3.id3v2.MyID3v2Write;
import org.cmc.music.util.Debug;

public class ListenerAndFilterExample
{

	public void listenerExample(File mp3File) throws IOException,
			ID3WriteException, ID3ReadException
	{
		/*
		 * The listener receives log message as the library parses and writes
		 * the mp3 file.
		 * 
		 * It writes them to the console using the Debug class.
		 */
		MyID3Listener listener = new MyID3Listener() {
			public void log()
			{
				Debug.debug();
			}

			public void log(String s)
			{
				Debug.debug("log: " + s);
			}
		};

		MusicMetadataSet srcSet = new MyID3().read(mp3File, listener);

		Debug.debug("srcSet", srcSet);

		String name = mp3File.getName();
		if (name.toLowerCase().endsWith(".mp3"))
			name = name.substring(0, name.length() - 4);
		name += ".1.mp3";
		File temp = new File(mp3File.getParentFile(), name);

		MyID3v2Write.Filter filter = new MyID3v2Write.Filter() {
			public boolean filter(String frameid)
			{
				/*
				 * Only write "song title" and "album name" frames.
				 * 
				 * Discard all others (such as "artist name").
				 */
				if (frameid.equals("TIT2"))
					return false;
				if (frameid.equals("TALB"))
					return false;

				return true;
			}
		};

		IMusicMetadata metadata = srcSet.merged;
		new MyID3().write(mp3File, temp, srcSet, metadata, filter, listener);

		MusicMetadataSet temp_set = new MyID3().read(temp, listener);
	}

}
