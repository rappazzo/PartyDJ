/*
 * Written By Charles M. Chen 
 * 
 * Created on Jan 1, 2006
 *
 */

package org.cmc.music.metadata;

public interface MusicMetadataConstants
{
	public static final String KEY_USER = "User";
	// public static final String KEY_DATA_SOURCE_NAME = "Data Source Name";
	// public static final String KEY_DATA_SOURCE_ID = "Data Source Id";
	public static final String KEY_DATA_SOURCE = "Data Source";
	public static final String KEY_FEATURING_LIST = "Featuring List";
	public static final String KEY_CONDUCTOR = "Conductor";
	public static final String KEY_BAND = "Band";
	public static final String KEY_MIX_ARTIST = "Mix Artist";
	public static final String KEY_LYRICIST = "Lyricist";
	public static final String KEY_PUBLISHER = "Publisher";
	public static final String KEY_ENGINEER = "Engineer";
	public static final String KEY_PRODUCER = "Producer";
	// public static final String KEY_MIXARTIST = "Mix Artist";
	// public static final String KEY_MIXARTIST = "Mix Artist";
	public static final String KEY_TITLE = "Title";
	public static final String KEY_ARTIST = "Artist";
	public static final String KEY_ALBUM = "Album";
	// TODO: make sure type is properly enforced in all references.
	public static final String KEY_YEAR = "Year";
	public static final String KEY_COMMENT_LIST = "Comments";
	// TODO: make sure type is properly enforced in all references.
	public static final String KEY_TRACK_NUMBER_DESCRIPTION = "Track Number";
	// TODO: make sure type is properly enforced in all references.
	public static final String KEY_TRACK_NUMBER_NUMERIC = "Track Number Numeric";
	public static final String KEY_DISC_NUMBER = "Disc Number";
	// public static final String KEY_Track_Number = "Track Number";
	public static final String KEY_TRACK_COUNT = "Track Count";
	// TODO: make sure type is properly enforced in all references.
	public static final String KEY_GENRE_NAME = "Genre";
	// TODO: make sure type is properly enforced in all references.
	public static final String KEY_GENRE_ID = "Genre Id";
	public static final String KEY_DURATION_SECONDS = "Duration Seconds";
	public static final String KEY_COMPOSER = "Composer";
	// public static final String KEY_ALBUM_ARTIST = "Album Artist";
	// public static final String KEY_COMPOSER_2 = "Composer 2";
	public static final String KEY_COMPILATION = "Compilation";
	public static final String KEY_SOUNDTRACK = "Soundtrack";
	public static final String KEY_LABEL = "Label";
	public static final String KEY_ACAPELLA = "Acapella";
	// public static final String KEY_LANGUAGE = "Language";
	// public static final String KEY_LANGUAGE = "Language";
	// public static final String KEY_LANGUAGE = "Language";
	public static final String KEY_PICTURES = "Pictures";
	public static final String KEY_ENCODED_BY = "Encoded By";
	public static final String KEY_ENCODER_SETTINGS = "Encoded Settings";
	public static final String KEY_MEDIA_TYPE = "Media Type";
	public static final String KEY_FILE_TYPE = "File Type";

	public static final String KEY_PART_OF_SET_INDEX = "Part Of Set Index";
	public static final String KEY_PART_OF_SET_COUNT = "Part Of Set Count";
	public static final String KEY_UNKNOWN_USER_TEXT_VALUES = "Unknown User Text Values";

	public static final String KEYS[] = { //
	KEY_USER, //
			KEY_DATA_SOURCE, //
			KEY_FEATURING_LIST, //
			KEY_CONDUCTOR, //
			KEY_BAND, //
			KEY_MIX_ARTIST, //
			KEY_LYRICIST, //
			KEY_PUBLISHER, //
			KEY_ENGINEER, //
			KEY_PRODUCER, //
			KEY_TITLE, //
			KEY_ARTIST, //
			KEY_ALBUM, //
			KEY_YEAR, //
			KEY_COMMENT_LIST, //
			KEY_TRACK_NUMBER_DESCRIPTION, //
			KEY_DISC_NUMBER, //
			KEY_TRACK_COUNT, //
			KEY_GENRE_NAME, //
			KEY_GENRE_ID, //
			KEY_DURATION_SECONDS, //
			KEY_COMPOSER, //
			// KEY_ALBUM_ARTIST, //
			// KEY_COMPOSER_2, //
			KEY_COMPILATION, //
			KEY_SOUNDTRACK, //
			KEY_LABEL, //
			KEY_ACAPELLA, //
			// KEY_LANGUAGE, //
			KEY_PICTURES, //
			KEY_ENCODED_BY,//
			KEY_ENCODER_SETTINGS, //
			KEY_MEDIA_TYPE, //
			KEY_FILE_TYPE, //
			KEY_PART_OF_SET_INDEX, //
			KEY_PART_OF_SET_COUNT, //
			KEY_UNKNOWN_USER_TEXT_VALUES, };

	public static final String INTERNAL_KEYS[] = { //
	KEY_PRODUCER, //
			KEY_COMPILATION, //
			KEY_SOUNDTRACK, //
			KEY_ACAPELLA, //
	};

}
