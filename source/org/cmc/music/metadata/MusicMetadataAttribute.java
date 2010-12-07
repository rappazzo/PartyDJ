/*
 * Written By Charles M. Chen 
 * 
 * Created on Jan 22, 2006
 *
 */

package org.cmc.music.metadata;

import org.cmc.music.myid3.TagFormat;

public abstract class MusicMetadataAttribute implements MusicMetadataConstants
{
	public abstract String getName();

	public abstract Object rectifyValue(Object o);

	public abstract Object getValue(IMusicMetadata metadata);

	public abstract void setValue(IMusicMetadata metadata, Object value);

	public static final MusicMetadataAttribute ARTIST = new MusicMetadataAttribute() {
		public String getName()
		{
			return "Artist";
		}

		public Object rectifyValue(Object o)
		{
			return new TagFormat().processArtist((String) o);
		}

		public Object getValue(IMusicMetadata metadata)
		{
			return metadata.getArtist();
		}

		public void setValue(IMusicMetadata metadata, Object value)
		{
			metadata.setArtist((String) value);
		}
	};

	public static final MusicMetadataAttribute ALBUM = new MusicMetadataAttribute() {
		public String getName()
		{
			return "Album";
		}

		public Object rectifyValue(Object o)
		{
			return new TagFormat().processAlbum((String) o);
		}

		public Object getValue(IMusicMetadata metadata)
		{
			return metadata.getAlbum();
		}

		public void setValue(IMusicMetadata metadata, Object value)
		{
			metadata.setAlbum((String) value);
		}
	};

	public static final MusicMetadataAttribute SONG_TITLE = new MusicMetadataAttribute() {
		public String getName()
		{
			return "Song Title";
		}

		public Object rectifyValue(Object o)
		{
			return new TagFormat().processSongTitle((String) o);
		}

		public Object getValue(IMusicMetadata metadata)
		{
			return metadata.getSongTitle();
		}

		public void setValue(IMusicMetadata metadata, Object value)
		{
			metadata.setSongTitle((String) value);
		}
	};

	public static final MusicMetadataAttribute TRACK_NUMBER = new MusicMetadataAttribute() {
		public String getName()
		{
			return "Track Number";
		}

		public Object rectifyValue(Object o)
		{
			return o;
			// return new TagFormat().processSongTitle((String) o);
		}

		public Object getValue(IMusicMetadata metadata)
		{
			return metadata.getTrackNumberDescription();
		}

		public void setValue(IMusicMetadata metadata, Object value)
		{
			metadata.setTrackNumberDescription((String) value);
		}
	};

	public static final MusicMetadataAttribute HAS_PICTURE = new MusicMetadataAttribute() {
		public String getName()
		{
			return "Has Picture";
		}

		public Object rectifyValue(Object o)
		{
			return o;
			// return new TagFormat().processSongTitle((String) o);
		}

		public Object getValue(IMusicMetadata metadata)
		{
			return new Boolean(metadata.getPictures().size() > 0);
		}

		public void setValue(IMusicMetadata metadata, Object value)
		{
			// metadata.setTrackNumber((Number) value);
		}
	};

}
