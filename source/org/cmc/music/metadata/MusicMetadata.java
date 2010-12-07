/*
 * Written By Charles M. Chen 
 * 
 * Created on Jan 22, 2006
 *
 */

package org.cmc.music.metadata;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.cmc.music.common.ID3v1Genre;
import org.cmc.music.util.Debug;
import org.cmc.music.util.SimpleMap;

/**
 * A collection of metadata values, possibly read from a single tag (ie. ID3v1
 * or ID3v2)
 * 
 * @see org.cmc.music.myid3.metadata.MusicMetadataSet
 */
public class MusicMetadata extends SimpleMap implements MusicMetadataConstants,
		IMusicMetadata
{
	public final String name;

	public MusicMetadata(String name)
	{
		this.name = name;
	}

	public MusicMetadata(MusicMetadata other)
	{
		this.name = other.name;
		putAll(other);
	}

	public MusicMetadata(IMusicMetadata other)
	{
		this.name = other.getMetadataName();
		putAll(other.getRawValues());
	}

	public MusicMetadata(String name, IMusicMetadata other)
	{
		this.name = name;
		putAll(other.getRawValues());
	}

	public final String getMetadataName()
	{
		return name;
	}

	public static final MusicMetadata createEmptyMetadata()
	{
		return new MusicMetadata("New Metadata");
	}

	public boolean hasBasicInfo()
	{
		if (null == getArtist())
			return false;
		if (null == getSongTitle())
			return false;
		if (null == getAlbum())
			return false;
		if (null == getTrackNumberDescription())
			return false;

		return true;
	}

	private Number getNumber(Object key)
	{
		Object result = get(key);
		if (result == null)
			return null;
		if (!(result instanceof Number))
			Debug.debug("Unexpected type(" + key + ")", result + " ("
					+ Debug.getType(result) + ")");
		return (Number) result;
	}

	private Boolean getBoolean(Object key)
	{
		Object result = get(key);
		if (result == null)
			return null;
		if (!(result instanceof Boolean))
			Debug.debug("Unexpected type(" + key + ")", result + " ("
					+ Debug.getType(result) + ")");
		return (Boolean) result;
	}

	private String getString(Object key)
	{
		Object result = get(key);
		if (result == null)
			return null;
		if (!(result instanceof String))
			Debug.debug("Unexpected type(" + key + ")", result + " ("
					+ Debug.getType(result) + ")");
		return (String) result;
	}

	private Vector getVector(Object key)
	{
		Object result = get(key);
		if (result == null)
			return null;
		if (!(result instanceof Vector))
			Debug.debug("Unexpected type(" + key + ")", result + " ("
					+ Debug.getType(result) + ")");
		return (Vector) result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getSongTitle()
	 */
	public String getSongTitle()
	{
		return getString(KEY_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getArtist()
	 */
	public String getArtist()
	{
		return getString(KEY_ARTIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getAlbum()
	 */
	public String getAlbum()
	{
		return getString(KEY_ALBUM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getYear()
	 */
	public Number getYear()
	{
		return getNumber(KEY_YEAR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getTrackNumber()
	 */
	public Number getTrackNumberNumeric()
	{
		return getNumber(KEY_TRACK_NUMBER_NUMERIC);
	}

	public Number getTrackCount()
	{
		return getNumber(KEY_TRACK_COUNT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getTrackNumber()
	 */
	public String getTrackNumberDescription()
	{
		return getString(KEY_TRACK_NUMBER_DESCRIPTION);
	}

	public String getTrackNumberFormatted()
	{
		Number trackNumber = getNumber(KEY_TRACK_NUMBER_NUMERIC);
		if (null != trackNumber)
		{
			NumberFormat nf = NumberFormat.getIntegerInstance();
			nf.setMinimumIntegerDigits(2);
			nf.setGroupingUsed(false);
			return nf.format(trackNumber);
		}
		if (this.containsKey(KEY_TRACK_NUMBER_DESCRIPTION))
			return getString(KEY_TRACK_NUMBER_DESCRIPTION);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getGenre()
	 */
	public String getGenreName()
	{
		return getString(KEY_GENRE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getGenreID()
	 */
	public Number getGenreID()
	{
		return getNumber(KEY_GENRE_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getDurationSeconds()
	 */
	public Number getDurationSeconds()
	{
		return getNumber(KEY_DURATION_SECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getComposer()
	 */
	public String getComposer()
	{
		return getString(KEY_COMPOSER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getProducerArtist()
	 */
	// public String getProducerArtist()
	// {
	// return getString(KEY_ALBUM_ARTIST);
	// }
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.cmc.music.common.IMusicMetadata#getComposer2()
	// */
	// public String getComposer2()
	// {
	// return getString(KEY_COMPOSER_2);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearSongTitle()
	 */
	public void clearSongTitle()
	{
		remove(KEY_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearArtist()
	 */
	public void clearArtist()
	{
		remove(KEY_ARTIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearAlbum()
	 */
	public void clearAlbum()
	{
		remove(KEY_ALBUM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearYear()
	 */
	public void clearYear()
	{
		remove(KEY_YEAR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearTrackNumber()
	 */
	public void clearTrackNumber()
	{
		remove(KEY_TRACK_NUMBER_NUMERIC);
		remove(KEY_TRACK_NUMBER_DESCRIPTION);
	}

	public void clearTrackCount()
	{
		remove(KEY_TRACK_COUNT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearGenre()
	 */
	public void clearGenre()
	{
		remove(KEY_GENRE_NAME);
		remove(KEY_GENRE_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearDurationSeconds()
	 */
	public void clearDurationSeconds()
	{
		remove(KEY_DURATION_SECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearComposer()
	 */
	public void clearComposer()
	{
		remove(KEY_COMPOSER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearProducerArtist()
	 */
	// public void clearProducerArtist()
	// {
	// remove(KEY_ALBUM_ARTIST);
	// }
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.cmc.music.common.IMusicMetadata#clearComposer2()
	// */
	// public void clearComposer2()
	// {
	// remove(KEY_COMPOSER_2);
	// }
	//
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearFeaturingList()
	 */
	public void clearFeaturingList()
	{
		remove(KEY_FEATURING_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmc.music.common.IMusicMetadata#setFeaturingList(java.util.Vector)
	 */
	public void setFeaturingList(Vector v)
	{
		put(KEY_FEATURING_LIST, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getFeaturingList()
	 */
	public Vector getFeaturingList()
	{
		return getVector(KEY_FEATURING_LIST);
	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearFeaturingList()
	 */
	public void clearPictures()
	{
		remove(KEY_PICTURES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmc.music.common.IMusicMetadata#setFeaturingList(java.util.Vector)
	 */
	public void setPictures(Vector v)
	{
		put(KEY_PICTURES, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getFeaturingList()
	 */
	public Vector getPictures()
	{
		Vector result = getVector(KEY_PICTURES);
		if (result == null)
			result = new Vector();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getFeaturingList()
	 */
	public void addPicture(ImageData image)
	{
		Vector v = getVector(KEY_PICTURES);
		if (null == v)
			v = new Vector();
		v.add(image);
		put(KEY_PICTURES, v);
	}

	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setSongTitle(java.lang.String)
	 */
	public void setSongTitle(String s)
	{
		put(KEY_TITLE, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setArtist(java.lang.String)
	 */
	public void setArtist(String s)
	{
		put(KEY_ARTIST, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setAlbum(java.lang.String)
	 */
	public void setAlbum(String s)
	{
		put(KEY_ALBUM, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setYear(java.lang.String)
	 */
	public void setYear(Number s)
	{
		put(KEY_YEAR, s);
	}

	public void setTrackNumberNumeric(Number value)
	{
		put(KEY_TRACK_NUMBER_NUMERIC, value);
		String description = value == null ? null : "" + value;
		put(KEY_TRACK_NUMBER_DESCRIPTION, description);
	}

	public void setTrackNumberDescription(String description)
	{
		Number value = null;
		try
		{
			value = Integer.valueOf(description);
			description = "" + value;
		} catch (NumberFormatException e)
		{
			// ignore;
		}
		put(KEY_TRACK_NUMBER_NUMERIC, value);
		put(KEY_TRACK_NUMBER_DESCRIPTION, description);
	}

	public void setTrackNumber(Number value, String description)
	{
		put(KEY_TRACK_NUMBER_NUMERIC, value);
		put(KEY_TRACK_NUMBER_DESCRIPTION, description);
	}

	public void setTrackCount(Number value)
	{
		put(KEY_TRACK_COUNT, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setGenre(java.lang.String)
	 */
	public void setGenreName(String name)
	{
		Number id = ID3v1Genre.getIDForName(name);
		put(KEY_GENRE_NAME, name);
		put(KEY_GENRE_ID, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setGenre(java.lang.String)
	 */
	public void setGenreID(Number id)
	{
		String name = ID3v1Genre.getNameForID(id);
		put(KEY_GENRE_NAME, name);
		put(KEY_GENRE_ID, id);
	}

	public void setGenre(String name, Number id)
	{
		put(KEY_GENRE_NAME, name);
		put(KEY_GENRE_ID, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmc.music.common.IMusicMetadata#setDurationSeconds(java.lang.String)
	 */
	public void setDurationSeconds(Number s)
	{
		put(KEY_DURATION_SECONDS, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setComposer(java.lang.String)
	 */
	public void setComposer(String s)
	{
		put(KEY_COMPOSER, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cmc.music.common.IMusicMetadata#setProducerArtist(java.lang.String)
	 */
	// public void setProducerArtist(String s)
	// {
	// put(KEY_ALBUM_ARTIST, s);
	// }
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.cmc.music.common.IMusicMetadata#setComposer2(java.lang.String)
	// */
	// public void setComposer2(String s)
	// {
	// put(KEY_COMPOSER_2, s);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getProducer()
	 */
	public String getProducer()
	{
		return getString(KEY_PRODUCER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#setProducer(java.lang.String)
	 */
	public void setProducer(String s)
	{
		put(KEY_PRODUCER, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearProducer()
	 */
	public void clearProducer()
	{
		remove(KEY_PRODUCER);
	}

	public String toString()
	{
		StringBuffer result = new StringBuffer();

		result.append("{ ");

		Vector keys = new Vector(keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++)
		{
			Object key = keys.get(i);
			Object value = get(key);

			if (i > 0)
				result.append(", ");
			result.append(key + ": " + value);
		}

		result.append(" }");

		return result.toString();
	}

	public final Map getRawValues()
	{
		return new Hashtable(this);

	}

	// public final String getDescription()
	// {
	// Vector keys = new Vector(keySet());
	// Collections.sort(keys);
	// for (int i = 0; i < keys.size(); i++)
	// {
	// Object key = keys.get(i);
	// Object value = get(key);
	// attr_panel.add(JFactory.newJLabel("<HTML><B>" + key
	// + ": </b>", JLabel.RIGHT));
	// attr_panel.add(JFactory.newJLabel(value.toString(),
	// JLabel.LEFT));
	// }
	// }

	public final void mergeValuesIfMissing(IMusicMetadata other)
	{
		Map rawValues = other.getRawValues();
		Vector keys = new Vector(rawValues.keySet());
		for (int i = 0; i < keys.size(); i++)
		{
			Object key = keys.get(i);
			if (containsKey(key))
				continue;
			put(key, rawValues.get(key));
		}
	}

	public void setIsSoundtrack(Boolean value)
	{
		put(KEY_SOUNDTRACK, value);
	}

	public Boolean getIsSoundtrack()
	{
		return getBoolean(KEY_SOUNDTRACK);
	}

	public void clearIsSoundtrack()
	{
		remove(KEY_SOUNDTRACK);
	}

	public void setIsAcapella(Boolean value)
	{
		put(KEY_ACAPELLA, value);
	}

	public Boolean getIsAcapella()
	{
		return getBoolean(KEY_ACAPELLA);
	}

	public void clearIsAcapella()
	{
		remove(KEY_ACAPELLA);
	}

	public void setIsCompilation(Boolean value)
	{
		put(KEY_COMPILATION, value);
	}

	public Boolean getIsCompilation()
	{
		return getBoolean(KEY_COMPILATION);
	}

	public void clearIsCompilation()
	{
		remove(KEY_COMPILATION);
	}

	public void setDiscNumber(Number value)
	{
		put(KEY_DISC_NUMBER, value);
	}

	public Number getDiscNumber()
	{
		return getNumber(KEY_DISC_NUMBER);
	}

	public void clearDiscNumber()
	{
		remove(KEY_DISC_NUMBER);
	}

	public void setEngineer(String value)
	{
		put(KEY_ENGINEER, value);
	}

	public String getEngineer()
	{
		return getString(KEY_ENGINEER);
	}

	public void clearEngineer()
	{
		remove(KEY_ENGINEER);
	}

	public void setPublisher(String value)
	{
		put(KEY_PUBLISHER, value);
	}

	public String getPublisher()
	{
		return getString(KEY_PUBLISHER);
	}

	public void clearPublisher()
	{
		remove(KEY_PUBLISHER);
	}

	public void setConductor(String value)
	{
		put(KEY_CONDUCTOR, value);
	}

	public String getConductor()
	{
		return getString(KEY_CONDUCTOR);
	}

	public void clearConductor()
	{
		remove(KEY_CONDUCTOR);
	}

	public void setBand(String value)
	{
		put(KEY_BAND, value);
	}

	public String getBand()
	{
		return getString(KEY_BAND);
	}

	public void clearBand()
	{
		remove(KEY_BAND);
	}

	public void setMixArtist(String value)
	{
		put(KEY_MIX_ARTIST, value);
	}

	public String getMixArtist()
	{
		return getString(KEY_MIX_ARTIST);
	}

	public void clearMixArtist()
	{
		remove(KEY_MIX_ARTIST);
	}

	public void setLyricist(String value)
	{
		put(KEY_LYRICIST, value);
	}

	public String getLyricist()
	{
		return getString(KEY_LYRICIST);
	}

	public void clearLyricist()
	{
		remove(KEY_LYRICIST);
	}

	public void setEncodedBy(String value)
	{
		put(KEY_ENCODED_BY, value);
	}

	public String getEncodedBy()
	{
		return getString(KEY_ENCODED_BY);
	}

	public void clearEncodedBy()
	{
		remove(KEY_ENCODED_BY);
	}

	public void setEncoderSettings(String value)
	{
		put(KEY_ENCODER_SETTINGS, value);
	}

	public String getEncoderSettings()
	{
		return getString(KEY_ENCODER_SETTINGS);
	}

	public void clearEncoderSettings()
	{
		remove(KEY_ENCODER_SETTINGS);
	}

	public void setMediaType(String value)
	{
		put(KEY_MEDIA_TYPE, value);
	}

	public String getMediaType()
	{
		return getString(KEY_MEDIA_TYPE);
	}

	public void clearMediaType()
	{
		remove(KEY_MEDIA_TYPE);
	}

	public void setFileType(String value)
	{
		put(KEY_FILE_TYPE, value);
	}

	public String getFileType()
	{
		return getString(KEY_FILE_TYPE);
	}

	public void clearFileType()
	{
		remove(KEY_FILE_TYPE);
	}

	public Number getPartOfSetIndex()
	{
		return getNumber(KEY_PART_OF_SET_INDEX);
	}

	public void clearPartOfSetIndex()
	{
		remove(KEY_PART_OF_SET_INDEX);
	}

	public void setPartOfSetIndex(Number s)
	{
		put(KEY_PART_OF_SET_INDEX, s);
	}

	public Number getPartOfSetCount()
	{
		return getNumber(KEY_PART_OF_SET_COUNT);
	}

	public void clearPartOfSetCount()
	{
		remove(KEY_PART_OF_SET_COUNT);
	}

	public void setPartOfSetCount(Number s)
	{
		put(KEY_PART_OF_SET_COUNT, s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#clearComment()
	 */
	public void clearComments()
	{
		remove(KEY_COMMENT_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cmc.music.common.IMusicMetadata#getComment()
	 */

	public List getComments()
	{

		Vector result = getVector(KEY_COMMENT_LIST);
		if (result == null)
			result = new Vector();
		return result;
	}

	public void addComment(String value)
	{

		Vector values = getVector(KEY_COMMENT_LIST);
		if (values == null)
		{
			values = new Vector();
			put(KEY_COMMENT_LIST, values);
		}
		values.add(value);
	}

	public void setComments(List values)
	{
		put(KEY_COMMENT_LIST, values);
	}

	public void clearUnknownUserTextValues()
	{
		remove(KEY_UNKNOWN_USER_TEXT_VALUES);
	}

	public List getUnknownUserTextValues()
	{

		Vector result = getVector(KEY_UNKNOWN_USER_TEXT_VALUES);
		if (result == null)
			result = new Vector();
		return result;
	}

	public void addUnknownUserTextValue(UnknownUserTextValue value)
	{

		Vector values = getVector(KEY_UNKNOWN_USER_TEXT_VALUES);
		if (values == null)
		{
			values = new Vector();
			put(KEY_UNKNOWN_USER_TEXT_VALUES, values);
		}
		values.add(value);
	}

	public void setUnknownUserTextValues(List values)
	{
		put(KEY_UNKNOWN_USER_TEXT_VALUES, values);
	}

}
