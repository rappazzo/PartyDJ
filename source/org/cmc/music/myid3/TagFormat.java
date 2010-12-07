package org.cmc.music.myid3;

import org.cmc.music.clean.MetadataCleanup;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;

public class TagFormat
{
	private static final MetadataCleanup rectifier = new MetadataCleanup();

	public String processArtist(String s)
	{
		return rectifier.rectifyArtist(s);
	}

	public String processAlbum(String s)
	{
		return rectifier.rectifyAlbum(s);
	}

	public String processSongTitle(String s)
	{
		return rectifier.rectifySongTitle(s);
	}

	public IMusicMetadata process(IMusicMetadata src)
	{
		IMusicMetadata result = new MusicMetadata(src.getMetadataName()
				+ " clean", src);

		{
			String s = src.getArtist();
			// Debug.debug("before", s);
			s = processArtist(s);
			// Debug.debug("after", s);
			result.setArtist(s);
		}
		{
			String s = src.getAlbum();
			// Debug.debug("before album", s);
			s = processAlbum(s);
			// Debug.debug("after album", s);
			result.setAlbum(s);
		}
		{
			String s = src.getSongTitle();
			// Debug.debug("before", s);
			s = processSongTitle(s);
			// Debug.debug("after", s);
			result.setSongTitle(s);
		}

		return result;
	}

}
