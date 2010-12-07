package org.cmc.music.metadata;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public interface IMusicMetadata
{
	public String getMetadataName();

	public boolean hasBasicInfo();

	public Map getRawValues();

	public void mergeValuesIfMissing(IMusicMetadata other);

	// accessor methods

	public String getSongTitle();

	public String getArtist();

	public String getAlbum();

	public Number getYear();

	public Number getTrackNumberNumeric();

	public String getTrackNumberDescription();

	public String getTrackNumberFormatted();

	public Number getTrackCount();

	public String getGenreName();

	public Number getGenreID();

	public Number getDurationSeconds();

	public String getComposer();

	// public String getProducerArtist();

	// public String getComposer2();

	public void clearSongTitle();

	public void clearArtist();

	public void clearAlbum();

	public void clearYear();

	public void clearTrackNumber();

	public void clearTrackCount();

	public void clearGenre();

	public void clearDurationSeconds();

	public void clearComposer();

	// public void clearProducerArtist();

	// public void clearComposer2();

	public void clearFeaturingList();

	public void setFeaturingList(Vector v);

	public Vector getFeaturingList();

	public void setSongTitle(String value);

	public void setArtist(String value);

	public void setAlbum(String value);

	public void setYear(Number value);

	public List getComments();

	public void clearComments();

	public void addComment(String value);

	public void setComments(List values);

	public void setTrackCount(Number value);

	public void setTrackNumberNumeric(Number value);

	public void setTrackNumberDescription(String description);

	public void setTrackNumber(Number value, String description);

	public void setGenreName(String value);

	public void setGenreID(Number value);

	public void setGenre(String name, Number id);

	public void setDurationSeconds(Number value);

	public void setComposer(String value);

	// public void setProducerArtist(String value);

	// public void setComposer2(String value);

	public String getProducer();

	public void setProducer(String value);

	public void clearProducer();

	public void clearPictures();

	public void setPictures(Vector v);

	public Vector getPictures();

	public void addPicture(ImageData image);

	public void setIsSoundtrack(Boolean value);

	public Boolean getIsSoundtrack();

	public void clearIsSoundtrack();

	public void setIsAcapella(Boolean value);

	public Boolean getIsAcapella();

	public void clearIsAcapella();

	public void setIsCompilation(Boolean value);

	public Boolean getIsCompilation();

	public void clearIsCompilation();

	public void setDiscNumber(Number value);

	public Number getDiscNumber();

	public void clearDiscNumber();

	public void setEngineer(String value);

	public String getEngineer();

	public void clearEngineer();

	public void setPublisher(String value);

	public String getPublisher();

	public void clearPublisher();

	public void setConductor(String value);

	public String getConductor();

	public void clearConductor();

	public void setBand(String value);

	public String getBand();

	public void clearBand();

	public void setMixArtist(String value);

	public String getMixArtist();

	public void clearMixArtist();

	public void setLyricist(String value);

	public String getLyricist();

	public void clearLyricist();

	public void setEncodedBy(String value);

	public String getEncodedBy();

	public void clearEncodedBy();

	public void setEncoderSettings(String value);

	public String getEncoderSettings();

	public void clearEncoderSettings();

	public void setMediaType(String value);

	public String getMediaType();

	public void clearMediaType();

	public void setFileType(String value);

	public String getFileType();

	public void clearFileType();

	public Number getPartOfSetIndex();

	public void clearPartOfSetIndex();

	public void setPartOfSetIndex(Number s);

	public Number getPartOfSetCount();

	public void clearPartOfSetCount();

	public void setPartOfSetCount(Number s);

	public void clearUnknownUserTextValues();

	public List getUnknownUserTextValues();

	public void addUnknownUserTextValue(UnknownUserTextValue value);

	public void setUnknownUserTextValues(List values);

}