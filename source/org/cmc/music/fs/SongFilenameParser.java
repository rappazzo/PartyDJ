/**
 * 
 */
package org.cmc.music.fs;

import java.io.File;

import org.cmc.music.clean.MetadataCleanup;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.myid3.TagFormat;
import org.cmc.music.util.Debug;
import org.cmc.music.util.TextUtils;

public abstract class SongFilenameParser
{

	private static final TagFormat utils = new TagFormat();

	private static final MetadataCleanup nameRectifier = new MetadataCleanup();

	public static final ParsedFilename parseFolder(File file)
	{
		return parseFolder(file.getName());
	}

	public static final ParsedFilename parseFolder(String s)
	{
		ParsedFilename result = new ParsedFilename(s);

		int hyphen_count = TextUtils.split(s, "-").length - 1;
		if (hyphen_count != 1)
			return result;

		String artist = s.substring(0, s.indexOf('-'));
		String album = s.substring(s.indexOf('-') + 1);

		artist = nameRectifier.rectifyArtist(artist);
		album = nameRectifier.rectifyAlbum(album);

		if (artist == null && album == null)
			return result;

		result.setArtist(artist);
		result.setAlbum(album);

		return result;
	}

	public static final boolean isTrackNumber(String s)
	{
		if (s == null)
			return false;

		return s.matches(" *[a-zA-Z]?[0-9]+ *");
	}

	public static final MusicMetadata parseFilename(String fileName,
			String folderName)
	{
		if (fileName == null)
			return null;

		if (!fileName.toLowerCase().endsWith(".mp3"))
			return null;
		fileName = fileName.substring(0, fileName.length() - 4);

		String artist;
		String songTitle;
		String trackNumberDescription;

		String splits[] = fileName.split("-");

		if (splits.length == 2)
		{
			if (isTrackNumber(splits[0]))
			{
				artist = null;
				trackNumberDescription = splits[0].trim();
				songTitle = utils.processSongTitle(splits[1]);
			} else if (isTrackNumber(splits[1]))
			{
				artist = null;
				songTitle = utils.processSongTitle(splits[0]);
				trackNumberDescription = splits[1].trim();
			} else
			{
				artist = utils.processArtist(splits[0]);
				songTitle = utils.processSongTitle(splits[1]);
				trackNumberDescription = null;
			}
		} else if (splits.length == 3)
		{
			if (isTrackNumber(splits[0]))
			{
				trackNumberDescription = splits[0].trim();
				artist = utils.processArtist(splits[1]);
				songTitle = utils.processSongTitle(splits[2]);
			} else if (isTrackNumber(splits[1]))
			{
				artist = utils.processArtist(splits[0]);
				trackNumberDescription = splits[1].trim();
				songTitle = utils.processSongTitle(splits[2]);
			} else
				return null;
		} else
			return null;

		// Debug.debug("artist", artist);
		// Debug.debug("song_title", song_title);

		if (isTrackNumber(artist))
		{
			// Debug.debug("bad artist", artist);
			return null;
		}

		if (isTrackNumber(songTitle))
		{
			// Debug.debug("bad song_title", song_title);
			return null;
		}

		// Debug.debug("song_title", song_title);

		if (folderName != null && folderName.endsWith("(!)"))
			folderName = folderName.substring(0, folderName.length() - 3);

		String VARIOUS_ARTISTS = "Various Artists";
		String album = null;
		if (folderName != null && !folderName.startsWith("@"))
		{
			if (artist != null)
			{
				if (folderName.toLowerCase().startsWith(
						VARIOUS_ARTISTS.toLowerCase()))
					folderName = folderName.substring(VARIOUS_ARTISTS.length());
				else if (folderName.toLowerCase().startsWith(
						artist.toLowerCase()))
					folderName = folderName.substring(artist.length());
				else if (folderName.toLowerCase()
						.endsWith(artist.toLowerCase()))
					folderName = folderName.substring(0, folderName.length()
							- artist.length());
				else
				{
					// Debug.debug("bad folderName", folderName);
					return null;
				}

				// Debug.debug("folderName", folderName);

				album = utils.processAlbum(folderName);
			} else
			{
				int first_hyphen = folderName.indexOf('-');
				int last_hyphen = folderName.lastIndexOf('-');
				if (first_hyphen >= 0 && first_hyphen == last_hyphen)
				{
					artist = utils.processArtist(folderName.substring(0,
							first_hyphen));
					album = utils.processAlbum(folderName
							.substring(first_hyphen + 1));
				} else
					return null;
			}

		}

		// Debug.debug("artist", artist);
		// Debug.debug("song_title", song_title);

		if (artist == null)
			return null;
		if (artist.equalsIgnoreCase(VARIOUS_ARTISTS))
			artist = null;

		MusicMetadata result = new MusicMetadata("filename");

		result.setAlbum(album);
		result.setArtist(artist);
		result.setSongTitle(songTitle);
		if (trackNumberDescription != null)
			result.setTrackNumberDescription(trackNumberDescription);
		// result.getTrackNumber()

		return result;
	}
}