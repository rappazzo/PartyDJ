package org.cmc.music.myid3.id3v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cmc.music.common.ID3FrameType;
import org.cmc.music.common.ID3ReadException;
import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataConstants;
import org.cmc.music.metadata.UnknownUserTextValue;
import org.cmc.music.myid3.MyID3Listener;

public abstract class ID3v2FrameTranslation implements MusicMetadataConstants
{

	/*
	 * Responsible for translating N metadata keys to N id3v2 frames.
	 * 
	 * One key may map to more than one frame; more than one key may map to one
	 * frame.
	 */
	private static abstract class Translator
	{

		/*
		 * Translate (if found), a set of 0-N related keys from the metadata,
		 * adding frames to the vector.
		 * 
		 * Remove translated keys from the metadata.
		 */
		public abstract void translateMetadataToFrames(IMusicMetadata metadata,
				Vector frames, IFrameFactory frameFactory) throws IOException;

		/*
		 * Translate a set of 1-N related frames to 1-N metadata keys.
		 * 
		 * Remove translated frames from the vector.
		 */
		public abstract void translateFramesToMetadata(MyID3Listener listener,
				boolean strict, IMusicMetadata metadata, Vector frames)
				throws ID3ReadException;

		protected final MyID3v2FrameText findAndRemoveUniqueTextFrame(
				MyID3Listener listener, boolean strict, Vector frames,
				ID3FrameType frameType) throws ID3ReadException
		{
			List translated = new ArrayList();
			for (int i = 0; i < frames.size(); i++)
			{
				MyID3v2Frame frame = (MyID3v2Frame) frames.get(i);
				if (frameType.matches(frame.frameID))
				{
					if (translated.size() > 0)
					{
						if (strict)
							throw new ID3ReadException(
									"Unexpected duplicate frame: "
											+ frame.frameID);
						else if (listener != null)
							listener.log("Unexpected duplicate frame",
									frame.frameID);
					}
					MyID3v2FrameText textFrame = (MyID3v2FrameText) frame;
					translated.add(frame);
				}
			}
			frames.removeAll(translated);
			if (translated.size() > 0)
				return (MyID3v2FrameText) translated.get(0);
			return null;
		}

		protected final List findAndRemoveFrames(MyID3Listener listener,
				boolean strict, Vector frames, ID3FrameType frameType)
				throws ID3ReadException
		{
			List translated = new ArrayList();
			for (int i = 0; i < frames.size(); i++)
			{
				MyID3v2Frame frame = (MyID3v2Frame) frames.get(i);
				if (frameType.matches(frame.frameID))
					translated.add(frame);
			}
			frames.removeAll(translated);
			return translated;
		}
	}

	/*
	 * Maps a String value from a single text frame to a single metadata key.
	 * 
	 * Frame should only appear once in tag.
	 */
	private static abstract class SimpleTranslator extends Translator
	{
		protected abstract void setMetadataValue(MyID3Listener listener,
				boolean strict, IMusicMetadata values, String value)
				throws ID3ReadException;

		protected abstract String getAndClearMetadataValue(IMusicMetadata values);

		protected abstract ID3FrameType getFrameType();

		public final void translateMetadataToFrames(IMusicMetadata metadata,
				Vector frames, IFrameFactory frameFactory) throws IOException
		{
			String value = getAndClearMetadataValue(metadata);
			if (value == null)
				return;

			ID3v2OutputFrame frame = frameFactory.createTextFrame(
					getFrameType(), value);
			frames.add(frame);
		}

		public final void translateFramesToMetadata(MyID3Listener listener,
				boolean strict, IMusicMetadata metadata, Vector frames)
				throws ID3ReadException
		{
			MyID3v2FrameText textFrame = findAndRemoveUniqueTextFrame(listener,
					strict, frames, getFrameType());

			if (null != textFrame)
				setMetadataValue(listener, strict, metadata, textFrame.value);
		}

	}

	private static final Translator TRANSLATORS[] = {

	new Translator() {

		public void translateMetadataToFrames(IMusicMetadata metadata,
				Vector frames, IFrameFactory frameFactory) throws IOException
		{
			List pictures = metadata.getPictures();
			for (int i = 0; i < pictures.size(); i++)
			{
				ImageData picture = (ImageData) pictures.get(i);
				ID3v2OutputFrame frame = frameFactory
						.createPictureFrame(picture);
				frames.add(frame);
			}
			metadata.clearPictures();
		}

		public void translateFramesToMetadata(MyID3Listener listener,
				boolean strict, IMusicMetadata metadata, Vector frames)
				throws ID3ReadException
		{
			List found = findAndRemoveFrames(listener, strict, frames,
					ID3FrameType.PICTURE);

			for (int i = 0; i < found.size(); i++)
			{
				MyID3v2FrameImage frame = (MyID3v2FrameImage) found.get(i);
				ImageData imageData = frame.getImageData();
				metadata.addPicture(imageData);
			}
		}

	}, //	

	new Translator() {

		public void translateMetadataToFrames(IMusicMetadata metadata,
				Vector frames, IFrameFactory frameFactory) throws IOException
		{
			List comments = metadata.getComments();
			for (int i = 0; i < comments.size(); i++)
			{
				String comment = (String) comments.get(i);
				ID3v2OutputFrame frame = frameFactory
						.createCommentFrame(comment);
				frames.add(frame);
			}
			metadata.clearComments();
		}

		public void translateFramesToMetadata(MyID3Listener listener,
				boolean strict, IMusicMetadata metadata, Vector frames)
				throws ID3ReadException
		{
			List found = findAndRemoveFrames(listener, strict, frames,
					ID3FrameType.COMMENT);

			for (int i = 0; i < found.size(); i++)
			{
				MyID3v2FrameText textFrame = (MyID3v2FrameText) found.get(i);
				metadata.addComment(textFrame.value);
			}
		}

	}, //	

	new SimpleTranslator() {

		protected void setMetadataValue(MyID3Listener listener, boolean strict,
				IMusicMetadata values, String value)
		{
			values.setComposer(value);
		}

		protected String getAndClearMetadataValue(IMusicMetadata values)
		{
			String result = values.getComposer();
			values.clearComposer();
			return result;
		}

		protected ID3FrameType getFrameType()
		{
			return ID3FrameType.COMPOSER;
		}

	}, //	

	new SimpleTranslator() {

		protected void setMetadataValue(MyID3Listener listener, boolean strict,
				IMusicMetadata values, String value)
		{
			values.setAlbum(value);
		}

		protected String getAndClearMetadataValue(IMusicMetadata values)
		{
			String result = values.getAlbum();
			values.clearAlbum();
			return result;
		}

		protected ID3FrameType getFrameType()
		{
			return ID3FrameType.ALBUM;
		}

	}, //	

	new SimpleTranslator() {

		protected void setMetadataValue(MyID3Listener listener, boolean strict,
				IMusicMetadata values, String value)
		{
			values.setArtist(value);
		}

		protected String getAndClearMetadataValue(IMusicMetadata values)
		{
			String result = values.getArtist();
			values.clearArtist();
			return result;
		}

		protected ID3FrameType getFrameType()
		{
			return ID3FrameType.ARTIST;
		}

	}, //	

	new SimpleTranslator() {

		protected void setMetadataValue(MyID3Listener listener, boolean strict,
				IMusicMetadata values, String value)
		{
			values.setSongTitle(value);
		}

		protected String getAndClearMetadataValue(IMusicMetadata values)
		{
			String result = values.getSongTitle();
			values.clearSongTitle();
			return result;
		}

		protected ID3FrameType getFrameType()
		{
			return ID3FrameType.TITLE;
		}

	}, //	

	// new SimpleTranslator() {
			// protected void setMetadataValue(MyID3Listener listener, boolean
			// strict, IMusicMetadata values,
			// String value)
			// {
			// values.setEngineer(value);
			// }
			//
			// protected String getAndClearMetadataValue(IMusicMetadata values)
			// {
			// String result = values.getEngineer();
			// values.clearEngineer();
			// return result;
			// }
			//
			// protected ID3FrameType getFrameType()
			// {
			// return ID3FrameType.ENGINEER;
			// }
			// }, //

			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setPublisher(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getPublisher();
					values.clearPublisher();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.PUBLISHER;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setConductor(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getConductor();
					values.clearConductor();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.CONDUCTOR;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setBand(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getBand();
					values.clearBand();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.BAND;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setMixArtist(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getMixArtist();
					values.clearMixArtist();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.MIX_ARTIST;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setLyricist(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getLyricist();
					values.clearLyricist();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.LYRICIST;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setEncodedBy(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getEncodedBy();
					values.clearEncodedBy();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.ENCODED_BY;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setEncoderSettings(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getEncoderSettings();
					values.clearEncoderSettings();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.ENCODER_SETTINGS;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setMediaType(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getMediaType();
					values.clearMediaType();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.MEDIA_TYPE;
				}
			}, //
			new SimpleTranslator() {
				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
				{
					values.setFileType(value);
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					String result = values.getFileType();
					values.clearFileType();
					return result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.FILE_TYPE;
				}
			}, //

			new Translator() {

				public final void translateMetadataToFrames(
						IMusicMetadata metadata, Vector frames,
						IFrameFactory frameFactory) throws IOException
				{
					String genreString = "";

					if (null != metadata.getGenreName())
						genreString = metadata.getGenreName();
					if (null != metadata.getGenreID())
						genreString = "(" + metadata.getGenreID() + ")"
								+ genreString;

					metadata.clearGenre();

					if (genreString.length() > 0)
					{
						ID3v2OutputFrame frame = frameFactory.createTextFrame(
								ID3FrameType.CONTENTTYPE, genreString);
						frames.add(frame);
					}
				}

				public final void translateFramesToMetadata(
						MyID3Listener listener, boolean strict,
						IMusicMetadata metadata, Vector frames)
						throws ID3ReadException
				{
					MyID3v2FrameText textFrame = findAndRemoveUniqueTextFrame(
							listener, strict, frames, ID3FrameType.CONTENTTYPE);

					if (null == textFrame)
						return;

					// TODO: actually, TCON are of form
					// (1)(2)refinement...
					// should catch or at least warn of refinements, multiples
					// values

					String value = textFrame.value;
					// try
					// {
					if (value == null || value.trim().length() < 1)
						return;

					value = value.trim();

					Number genreID = null;
					String genreName = null;

					String genreIdParenRegex = "^\\((\\d+)\\)";
					Pattern genreIdParenPattern = Pattern
							.compile(genreIdParenRegex);

					while (true)
					{
						Matcher m = genreIdParenPattern.matcher(value);
						if (!m.find())
							break;

						// discard genre ids after first
						if (null == genreID)
						{
							try
							{
								String genreIDString = m.group(1);
								genreID = Integer.valueOf(genreIDString);
							} catch (NumberFormatException e)
							{
								if (strict)
									throw new ID3ReadException(
											"Invalid genre value: " + value);
								else if (null != listener)
									listener.log("Invalid genre value", value);
							}
						}
						value = value.substring(m.group(0).length());
					}

					String genreIdOnlyRegex = "^\\d+$";
					Pattern genreIdOnlyPattern = Pattern
							.compile(genreIdOnlyRegex);

					Matcher m = genreIdOnlyPattern.matcher(value);
					if (m.find())
					{
						try
						{
							String genreIDString = m.group(0);
							genreID = Integer.valueOf(genreIDString);
						} catch (NumberFormatException e)
						{
							if (strict)
								throw new ID3ReadException(
										"Invalid genre value: " + value);
							else if (null != listener)
								listener.log("Invalid genre value", value);
						}
						value = "";
					}

					// RE genreIdRegExp = new RE("^\\((\\d+)\\)");
					//
					// // Debug.debug("id_only", id_only);
					// while (genreIdRegExp.match(value))
					// {
					// // discard genre ids after first
					// if (null == genreID)
					// {
					// genreID = genreIdRegExp.group(1);
					// }
					//
					// value = value.substring(genreIdRegExp.group(0)
					// .length());
					// }

					// boolean id_only = new RE("^\\(\\d+\\)").match(value);
					//
					// Number genreID = null;
					// String genreName = null;
					//
					// // Debug.debug("id_only", id_only);
					// if (id_only)
					// {
					// int index = value.indexOf(')');
					// String number = value.substring(1, index);
					// // Debug.debug("number", number);
					//
					// number = number.trim();
					// if (isNumber(number))
					// {
					// genreID = new Integer(number);
					// if (genreID.intValue() != 0)
					// genreName = ID3v1Genre
					// .getNameForID(genreID);
					//
					// value = value.substring(index + 1);
					// }
					// } else
					// {
					// boolean numeric_only = new RE("^\\d+$")
					// .match(value);
					// // Debug.debug("numeric_only", numeric_only);
					// if (numeric_only)
					// {
					// genreID = new Integer(value);
					// if (genreID.intValue() != 0)
					// genreName = ID3v1Genre
					// .getNameForID(genreID);
					//
					// value = "";
					// // Debug.debug("id", id);
					// }
					// }

					if (value.length() > 0)
						genreName = value;
					if (null != genreName && genreID != null)
						metadata.setGenre(genreName, genreID);
					else if (null != genreName)
						metadata.setGenreName(genreName);
					else if (null != genreID)
						metadata.setGenreID(genreID);
					// } catch (NumberFormatException e)
					// {
					// Debug.debug(e);
					// }
				}
			}, //

			new SimpleTranslator() {

				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
						throws ID3ReadException
				{
					try
					{
						Number year = Integer.valueOf(value);
						values.setYear(year);
					} catch (NumberFormatException e)
					{
						if (strict)
							throw new ID3ReadException("Invalid year value: "
									+ value);
						else if (null != listener)
							listener.log("Invalid year value", value);
					}
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					Number result = values.getYear();
					values.clearYear();
					return result == null ? null : "" + result;
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.YEAR;
				}

			}, //	

			new Translator() {

				public final void translateMetadataToFrames(
						IMusicMetadata metadata, Vector frames,
						IFrameFactory frameFactory) throws IOException
				{
					Object trackCount = metadata.getTrackCount();
					Object trackNumber = metadata.getTrackNumberDescription();

					if (trackCount != null || trackNumber != null)
					{
						String value = "";
						if (trackNumber != null)
							value += trackNumber.toString();
						if (trackCount != null)
						{
							value += "/";
							value += trackCount.toString();
						}

						frames.add(frameFactory.createTextFrame(
								ID3FrameType.TRACKNUM, value));
					}

					metadata.clearTrackCount();
					metadata.clearTrackNumber();
				}

				public final void translateFramesToMetadata(
						MyID3Listener listener, boolean strict,
						IMusicMetadata metadata, Vector frames)
						throws ID3ReadException
				{
					MyID3v2FrameText textFrame = findAndRemoveUniqueTextFrame(
							listener, strict, frames, ID3FrameType.TRACKNUM);

					if (null == textFrame)
						return;

					String value = textFrame.value;
					if (value == null || value.trim().length() < 1)
						return;

					value = value.trim();

					String trackNumberDescription = null;
					Number trackCount = null;

					String justTrackCountRegex = "^/(\\d+)$";
					Pattern justTrackCountPattern = Pattern
							.compile(justTrackCountRegex);
					Matcher justTrackCountPatternMatcher = justTrackCountPattern
							.matcher(value);

					String trackNumberAndCountRegex = "^(\\w+)/(\\d+)$";
					Pattern trackNumberAndCountPattern = Pattern
							.compile(trackNumberAndCountRegex);
					Matcher trackNumberAndCountPatternMatcher = trackNumberAndCountPattern
							.matcher(value);

					if (justTrackCountPatternMatcher.find())
					{
						try
						{
							String trackCountString = justTrackCountPatternMatcher
									.group(1);
							trackCount = Integer.valueOf(trackCountString);
						} catch (NumberFormatException e)
						{
							if (strict)
								throw new ID3ReadException(
										"Invalid track number value: " + value);
							else if (null != listener)
								listener.log("Invalid track number value",
										value);
						}
					} else if (trackNumberAndCountPatternMatcher.find())
					{
						try
						{
							trackNumberDescription = trackNumberAndCountPatternMatcher
									.group(1);
							String trackCountString = trackNumberAndCountPatternMatcher
									.group(2);
							trackCount = Integer.valueOf(trackCountString);
						} catch (NumberFormatException e)
						{
							if (strict)
								throw new ID3ReadException(
										"Invalid track number value: " + value);
							else if (null != listener)
								listener.log("Invalid track number value",
										value);
						}
					} else
						trackNumberDescription = value;

					metadata.setTrackCount(trackCount);
					metadata.setTrackNumberDescription(trackNumberDescription);
				}
			}, //

			new Translator() {

				public final void translateMetadataToFrames(
						IMusicMetadata metadata, Vector frames,
						IFrameFactory frameFactory) throws IOException
				{
					Number partOfSetIndex = metadata.getPartOfSetIndex();
					Number partOfSetCount = metadata.getPartOfSetCount();

					if (partOfSetIndex != null || partOfSetCount != null)
					{
						String value = "";
						if (partOfSetIndex != null)
							value += partOfSetIndex.toString();
						if (partOfSetCount != null)
						{
							value += "/";
							value += partOfSetCount.toString();
						}

						frames.add(frameFactory.createTextFrame(
								ID3FrameType.PARTINSET, value));
					}

					metadata.clearPartOfSetIndex();
					metadata.clearPartOfSetCount();
				}

				public final void translateFramesToMetadata(
						MyID3Listener listener, boolean strict,
						IMusicMetadata metadata, Vector frames)
						throws ID3ReadException
				{
					MyID3v2FrameText textFrame = findAndRemoveUniqueTextFrame(
							listener, strict, frames, ID3FrameType.PARTINSET);

					if (null == textFrame)
						return;

					String value = textFrame.value;
					if (value == null || value.trim().length() < 1)
						return;

					value = value.trim();

					Number partOfSetIndex = null;
					Number partOfSetCount = null;

					String justPartOfSetIndexRegex = "^(\\d+)/?$";
					Pattern justPartOfSetIndexPattern = Pattern
							.compile(justPartOfSetIndexRegex);
					Matcher justPartOfSetIndexPatternMatcher = justPartOfSetIndexPattern
							.matcher(value);

					String partOfSetIndexAndCountRegex = "^(\\d*)/(\\d+)$";
					Pattern partOfSetIndexAndCountPattern = Pattern
							.compile(partOfSetIndexAndCountRegex);
					Matcher partOfSetIndexAndCountPatternMatcher = partOfSetIndexAndCountPattern
							.matcher(value);

					if (justPartOfSetIndexPatternMatcher.find())
					{
						try
						{
							String partOfSetIndexString = justPartOfSetIndexPatternMatcher
									.group(1);
							partOfSetIndex = Integer
									.valueOf(partOfSetIndexString);
						} catch (NumberFormatException e)
						{
							if (strict)
								throw new ID3ReadException(
										"Invalid part of set value: " + value);
							else if (null != listener)
								listener
										.log("Invalid part of set value", value);
						}
					} else if (partOfSetIndexAndCountPatternMatcher.find())
					{
						try
						{
							String partOfSetIndexString = partOfSetIndexAndCountPatternMatcher
									.group(1);
							partOfSetIndex = Integer
									.valueOf(partOfSetIndexString);
						} catch (NumberFormatException e)
						{
							if (strict)
								throw new ID3ReadException(
										"Invalid part of set value: " + value);
							else if (null != listener)
								listener
										.log("Invalid part of set value", value);
						}
						try
						{
							String partOfSetCountString = partOfSetIndexAndCountPatternMatcher
									.group(2);
							partOfSetCount = Integer
									.valueOf(partOfSetCountString);
						} catch (NumberFormatException e)
						{
							if (strict)
								throw new ID3ReadException(
										"Invalid part of set value: " + value);
							else if (null != listener)
								listener
										.log("Invalid part of set value", value);
						}
					} else
					{
						if (strict)
							throw new ID3ReadException(
									"Invalid part of set value: " + value);
						else if (null != listener)
							listener.log("Invalid part of set value", value);
					}

					metadata.setPartOfSetIndex(partOfSetIndex);
					metadata.setPartOfSetCount(partOfSetCount);
				}
			}, //

			new SimpleTranslator() {

				protected void setMetadataValue(MyID3Listener listener,
						boolean strict, IMusicMetadata values, String value)
						throws ID3ReadException
				{
					try
					{
						Long year = Long.valueOf(value);
						// ms to seconds
						year = new Long(year.longValue() / 1000);
						values.setDurationSeconds(year);
					} catch (NumberFormatException e)
					{
						if (strict)
							throw new ID3ReadException(
									"Invalid duration value: " + value);
						else if (null != listener)
							listener.log("Invalid duration value", value);
					}
				}

				protected String getAndClearMetadataValue(IMusicMetadata values)
				{
					Number result = values.getDurationSeconds();
					values.clearDurationSeconds();
					return result == null ? null : ""
							+ (result.longValue() * 1000);
				}

				protected ID3FrameType getFrameType()
				{
					return ID3FrameType.SONGLEN;
				}

			}, //	

			new Translator() {

				public void translateMetadataToFrames(IMusicMetadata metadata,
						Vector frames, IFrameFactory frameFactory)
						throws IOException
				{
					String engineer = metadata.getEngineer();
					if (null != engineer)
						frames.add(frameFactory.createUserTextFrame("engineer",
								engineer));
					metadata.clearEngineer();

					List unknownUserTextValues = metadata
							.getUnknownUserTextValues();
					for (int i = 0; i < unknownUserTextValues.size(); i++)
					{
						UnknownUserTextValue value = (UnknownUserTextValue) unknownUserTextValues
								.get(i);
						ID3v2OutputFrame frame = frameFactory
								.createUserTextFrame(value.key, value.value);
						frames.add(frame);
					}
					metadata.clearUnknownUserTextValues();
				}

				public void translateFramesToMetadata(MyID3Listener listener,
						boolean strict, IMusicMetadata metadata, Vector frames)
						throws ID3ReadException
				{
					List found = findAndRemoveFrames(listener, strict, frames,
							ID3FrameType.USERTEXT);

					for (int i = 0; i < found.size(); i++)
					{
						MyID3v2FrameText textFrame = (MyID3v2FrameText) found
								.get(i);

						if (textFrame.value.equals("engineer"))
							metadata.setEngineer(textFrame.value2);
						else
							metadata
									.addUnknownUserTextValue(new UnknownUserTextValue(
											textFrame.value, textFrame.value2));
					}
				}

			}, //	

	};

	// private static final Map KEY_TO_FRAME_TYPE_MAP = new HashMap();
	// private static final Vector IGNORED_FRAME_TYPES = new Vector();
	// static
	// {
	// for (int i = 0; i < TRANSLATORS.length; i++)
	// {
	// Translator handler = TRANSLATORS[i];
	//
	// Object key = handler.getMetadataKey();
	// if (key != null)
	// KEY_TO_FRAME_TYPE_MAP.put(key, handler.getFrameType());
	// else
	// IGNORED_FRAME_TYPES.add(handler.getFrameType());
	// }
	// }
	// private static final Vector IGNORED_METADATA_KEYS = new Vector();
	// static
	// {
	// IGNORED_METADATA_KEYS.add(KEY_COMPILATION);
	// IGNORED_METADATA_KEYS.add(KEY_SOUNDTRACK);
	// IGNORED_METADATA_KEYS.add(KEY_ACAPELLA);
	// IGNORED_METADATA_KEYS.add(KEY_PRODUCER);
	// }
	//
	// public static final boolean isIgnoredMetadataKey(String key)
	// {
	// return IGNORED_METADATA_KEYS.contains(key);
	// }
	//
	// public static final ID3FrameType getID3FrameType(Object key)
	// {
	// if (key.equals(KEY_PICTURES))
	// return ID3FrameType.PICTURE;
	//
	// return (ID3FrameType) KEY_TO_FRAME_TYPE_MAP.get(key);
	// }
	//
	// public static final boolean isIgnoredID3FrameType(ID3FrameType
	// frame_type)
	// {
	// return IGNORED_FRAME_TYPES.contains(frame_type);
	// }

	public static final MusicMetadata translateFramesToMetadata(
			MyID3Listener listener, boolean strict, Vector frames)
			throws ID3ReadException
	{
		// if (frames == null)
		// return null;
		//
		MusicMetadata result = new MusicMetadata("id3v2");

		for (int i = 0; i < TRANSLATORS.length; i++)
		{
			Translator translator = TRANSLATORS[i];

			translator.translateFramesToMetadata(listener, strict, result,
					frames);
		}

		for (int i = 0; i < frames.size(); i++)
		{
			MyID3v2Frame frame = (MyID3v2Frame) frames.get(i);

			if (strict)
				throw new ID3ReadException("Could not translate frame: "
						+ frame.frameID);
			else if (null != listener)
				listener.log("Could not translate frame", frame.frameID);
		}

		return result;
	}

	public static final List translateMetadataToFrames(MyID3Listener listener,
			boolean strict, IMusicMetadata metadata, IFrameFactory frameFactory)
			throws ID3WriteException, IOException
	{
		Vector frames = new Vector();

		// make a local copy; we're going to clear keys as we translate them.
		metadata = new MusicMetadata(metadata);

		for (int i = 0; i < TRANSLATORS.length; i++)
		{
			Translator translator = TRANSLATORS[i];

			translator
					.translateMetadataToFrames(metadata, frames, frameFactory);
		}

		Map untranslated = metadata.getRawValues();
		List untranslatedKeys = new ArrayList(untranslated.keySet());
		for (int i = 0; i < untranslatedKeys.size(); i++)
		{
			String key = (String) untranslatedKeys.get(i);

			if (strict)
				throw new ID3WriteException(
						"Could not translate metadata key: " + key);
			else if (null != listener)
				listener.log("Could not translate metadata key", key);
		}

		return frames;
	}

}
