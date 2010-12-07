package org.cmc.music.myid3.id3v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.cmc.music.common.ID3FrameType;
import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3Listener;
import org.cmc.music.util.Debug;

public class MyID3v2Write implements MyID3v2Constants
{

	private final int id3v2_version = 3;

	private byte[] getHeaderFooter(int body_length, boolean is_footer)
			throws ID3WriteException
	{
		byte result[] = new byte[10];

		int index = 0;
		if (is_footer)
		{
			result[index++] = 0x33; // 3
			result[index++] = 0x44; // D
			result[index++] = 0x49; // I
		} else
		{
			result[index++] = 0x49; // I
			result[index++] = 0x44; // D
			result[index++] = 0x33; // 3
		}

		if (id3v2_version == 4)
			result[index++] = 0x04; // version
		else if (id3v2_version == 3)
			result[index++] = 0x03; // version
		else
			throw new ID3WriteException("id3v2_version: " + id3v2_version);

		result[index++] = 0x00;

		int flags = 0; // charles
		if (id3v2_version == 4)
			flags |= HEADER_FLAG_ID3v24_FOOTER_PRESENT;
		else if (id3v2_version == 3)
		{
		} else
			throw new ID3WriteException("id3v2_version: " + id3v2_version);

		result[index++] = (byte) flags;

		writeSynchSafeInt(result, index, body_length);

		return result;
	}

	private final void writeSynchSafeInt(byte bytes[], int start, int value)
			throws ID3WriteException
	{
		bytes[start + 3] = (byte) (value & 0x7f);
		value >>= 7;
		bytes[start + 2] = (byte) (value & 0x7f);
		value >>= 7;
		bytes[start + 1] = (byte) (value & 0x7f);
		value >>= 7;
		bytes[start + 0] = (byte) (value & 0x7f);

		value >>= 7;
		if (value != 0)
			throw new ID3WriteException("Value to large for synch safe int: "
					+ value);
	}

	private ID3v2OutputFrame toFrame(String longFrameID, Number frameOrder,
			String value1, String value2) throws UnsupportedEncodingException,
			IOException
	{
		if (longFrameID.startsWith("T"))
		{
			return toFrameText(longFrameID, frameOrder, value1, value2);
		} else if (longFrameID.equals("COMM"))
		{
			return toFrameCOMM(longFrameID, frameOrder, value1);
		} else
		{
			// TODO: should we throw an exception here?
			Debug.debug();
			Debug.debug("frame_type.long_id", longFrameID);
			Debug.debug("not text");
			Debug.dumpStack();
			return null;
		}
	}

	private static boolean canEncodeStringInISO(String s)
			throws UnsupportedEncodingException
	{
		byte bytes[] = s.getBytes(CHAR_ENCODING_ISO);
		String check1 = new String(bytes, CHAR_ENCODING_ISO);

		return check1.equals(s);
	}

	private static byte[] encodeString(String s, boolean use_iso)
			throws UnsupportedEncodingException, IOException
	{
		if (use_iso)
			return s.getBytes(CHAR_ENCODING_ISO);
		else
		{
			byte bytes[] = s.getBytes(CHAR_ENCODING_UTF_16);

			// Windows Media Player can't handle UTF-16, big-endian.
			// switch to UTF-16, little-endian.
			if (((0xff & bytes[0]) == 0xFE) && ((0xff & bytes[1]) == 0xFF))
			{
				// manually switch UTF 16 byte order
				for (int i = 0; i < bytes.length; i += 2)
				{
					byte temp = bytes[i];
					bytes[i] = bytes[i + 1];
					bytes[i + 1] = temp;
				}
			}
			return bytes;
		}
	}

	// TODO: convert Object params to String params.
	private static ID3v2OutputFrame toFrameText(String longFrameID,
			Number frameOrder, String value1, String value2)
			throws UnsupportedEncodingException, IOException
	{
		boolean use_iso = canEncodeStringInISO(value1);
		if (value2 != null)
			use_iso &= canEncodeStringInISO(value2);

		int char_encoding_code = use_iso ? CHAR_ENCODING_CODE_ISO_8859_1
				: CHAR_ENCODING_CODE_UTF_16_WITH_BOM;
		// : CHAR_ENCODING_CODE_UTF_8;

		byte string_1_bytes[] = encodeString(value1, use_iso);
		byte string_2_bytes[] = null;
		if (value2 != null)
			string_2_bytes = encodeString(value2, use_iso);

		// Debug.debug("use_iso", use_iso);
		// Debug.debug("string_1_bytes", string_1_bytes);
		// Debug.debug("s2", s2);

		// int frame_length = kFRAME_HEADER_LENGTH + string_bytes.length + 1;
		int result_length = string_1_bytes.length + 1;
		if (string_2_bytes != null)
			result_length += string_2_bytes.length + 1;
		byte result[] = new byte[result_length];
		int index = 0;

		result[index++] = (byte) char_encoding_code;
		System.arraycopy(string_1_bytes, 0, result, index,
				string_1_bytes.length);
		index += string_1_bytes.length;

		if (string_2_bytes != null)
		{
			result[index++] = (byte) char_encoding_code;
			System.arraycopy(string_2_bytes, 0, result, index,
					string_2_bytes.length);
		}

		return new ID3v2OutputFrame(longFrameID, frameOrder, result);
	}

	private static ID3v2OutputFrame toFrameImage(String longFrameID,
			Number frameOrder, ImageData imageData)
	// byte imageData[], String mimeType, String description,
			// int pictureType)
			throws UnsupportedEncodingException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		boolean use_iso = canEncodeStringInISO(imageData.description);

		int char_encoding_code = use_iso ? CHAR_ENCODING_CODE_ISO_8859_1
				: CHAR_ENCODING_CODE_UTF_16_WITH_BOM;

		baos.write(char_encoding_code);

		byte mimeTypeBytes[] = encodeString(imageData.mimeType, true);
		// Debug.debug("mimeType", mimeType);
		// Debug.debug("mimeTypeBytes", mimeTypeBytes);
		baos.write(mimeTypeBytes);
		baos.write(0);

		baos.write(0xff & imageData.pictureType);

		byte descriptionBytes[] = encodeString(imageData.description, use_iso);
		baos.write(descriptionBytes);
		// Debug.debug("description", description);
		// Debug.debug("descriptionBytes", descriptionBytes);
		baos.write(0);

		baos.write(imageData.imageData);

		byte frameBytes[] = baos.toByteArray();

		// Debug.debug("frameBytes", frameBytes);

		return new ID3v2OutputFrame(longFrameID, frameOrder, frameBytes);
	}

	private static class FrameFactory implements IFrameFactory
	{
		public ID3v2OutputFrame createUserTextFrame(String value1, String value2)
				throws UnsupportedEncodingException, IOException
		{
			ID3FrameType frameType = ID3FrameType.USERTEXT;
			String longFrameID = frameType.longID;
			Number frameOrder = frameType.getFrameOrder();
			return toFrameText(longFrameID, frameOrder, value1, value2);
		}

		public ID3v2OutputFrame createCommentFrame(String value)
				throws UnsupportedEncodingException, IOException
		{
			ID3FrameType frameType = ID3FrameType.COMMENT;
			String longFrameID = frameType.longID;
			Number frameOrder = frameType.getFrameOrder();
			return toFrameCOMM(longFrameID, frameOrder, value);
		}

		public ID3v2OutputFrame createPictureFrame(ImageData imageData)
				throws UnsupportedEncodingException, IOException
		{
			ID3FrameType frameType = ID3FrameType.PICTURE;
			String longFrameID = frameType.longID;
			Number frameOrder = frameType.getFrameOrder();
			return toFrameImage(longFrameID, frameOrder, imageData);
		}

		public ID3v2OutputFrame createTextFrame(ID3FrameType frameType,
				String value) throws UnsupportedEncodingException, IOException
		{
			return createTextFrame(frameType, value, null);
		}

		public ID3v2OutputFrame createTextFrame(ID3FrameType frameType,
				String value1, String value2)
				throws UnsupportedEncodingException, IOException
		{
			String longFrameID = frameType.longID;
			Number frameOrder = frameType.getFrameOrder();
			return toFrameText(longFrameID, frameOrder, value1, value2);
		}
	}

	// TODO: convert Object params to String params.
	private static ID3v2OutputFrame toFrameCOMM(String longFrameID,
			Number frameOrder, String value)
			throws UnsupportedEncodingException, IOException
	{
		String s;
		if (value instanceof String)
			s = (String) value;
		else
		{
			Debug
					.debug("Bad value ", value + " (" + Debug.getType(value)
							+ ")");
			Debug.dumpStack();
			return null;
		}

		boolean use_iso = canEncodeStringInISO(s);

		int char_encoding_code = use_iso ? CHAR_ENCODING_CODE_ISO_8859_1
				: CHAR_ENCODING_CODE_UTF_16_WITH_BOM;
		// : CHAR_ENCODING_CODE_UTF_8;

		byte string_bytes[] = encodeString(s, use_iso);

		// int frame_length = kFRAME_HEADER_LENGTH + string_bytes.length + 1;
		int result_length = string_bytes.length + 1 + 3 + 1;

		byte result[] = new byte[result_length];
		int index = 0;

		result[index++] = (byte) char_encoding_code;
		result[index++] = (byte) 0; // language
		result[index++] = (byte) 0; // language
		result[index++] = (byte) 0; // language

		// summary
		result[index++] = (byte) 0; // divider

		System.arraycopy(string_bytes, 0, result, index, string_bytes.length);

		return new ID3v2OutputFrame(longFrameID, frameOrder, result);
	}

	private List toFrames(MyID3Listener listener, boolean strict,
			IMusicMetadata metadata) throws UnsupportedEncodingException,
			IOException, ID3WriteException
	{
		// make a local copy.
		metadata = new MusicMetadata(metadata);

		IFrameFactory frameFactory = new FrameFactory();
		return ID3v2FrameTranslation.translateMetadataToFrames(listener,
				strict, metadata, frameFactory);
	}

	private static final Comparator FRAME_SORTER = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			ID3v2OutputFrame f1 = (ID3v2OutputFrame) o1;
			ID3v2OutputFrame f2 = (ID3v2OutputFrame) o2;

			int fo1 = f1.frameOrder.intValue();
			int fo2 = f2.frameOrder.intValue();
			if (fo1 != fo2)
				return fo1 - fo2;

			return f1.longFrameID.compareTo(f2.longFrameID);
		}
	};

	public interface Filter
	{
		public boolean filter(String frameid);
	}

	private byte[] writeFrames(Filter filter, List frames)
			throws ID3WriteException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Collections.sort(frames, FRAME_SORTER);

		for (int i = 0; i < frames.size(); i++)
		{
			ID3v2OutputFrame frame = (ID3v2OutputFrame) frames.get(i);

			String frame_id = frame.longFrameID;
			if (frame_id.length() != 4)
				throw new ID3WriteException("frame_id has bad length: "
						+ frame_id + " (" + frame_id.length() + ")");

			if (filter != null && filter.filter(frame_id))
			{
				continue;
			}

			// baos.write(frame_id );
			baos.write((byte) frame_id.charAt(0));
			baos.write((byte) frame_id.charAt(1));
			baos.write((byte) frame_id.charAt(2));
			baos.write((byte) frame_id.charAt(3));

			int length = frame.bytes.length;

			if (id3v2_version == 4)
			{
				baos.write((byte) (0x7f & (length >> 21)));
				baos.write((byte) (0x7f & (length >> 14)));
				baos.write((byte) (0x7f & (length >> 7)));
				baos.write((byte) (0x7f & (length)));
			} else if (id3v2_version == 3)
			{
				baos.write((byte) (0xff & (length >> 24)));
				baos.write((byte) (0xff & (length >> 16)));
				baos.write((byte) (0xff & (length >> 8)));
				baos.write((byte) (0xff & (length)));
			} else
				throw new ID3WriteException("id3v2_version: " + id3v2_version);

			// int flags = frame.flags;
			int flags = 0;
			if (id3v2_version == 4)
			{
				if (frame.flags.getTagAlterPreservation())
					flags |= FRAME_FLAG_ID3v24_TAG_ALTER_PRESERVATION;
				if (frame.flags.getFileAlterPreservation())
					flags |= FRAME_FLAG_ID3v24_FILE_ALTER_PRESERVATION;
				if (frame.flags.getReadOnly())
					flags |= FRAME_FLAG_ID3v24_READ_ONLY;
				if (frame.flags.getGroupingIdentity())
					flags |= FRAME_FLAG_ID3v24_GROUPING_IDENTITY;
				if (frame.flags.getCompression())
					flags |= FRAME_FLAG_ID3v24_COMPRESSION;
				if (frame.flags.getEncryption())
					flags |= FRAME_FLAG_ID3v24_ENCRYPTION;
				if (frame.flags.getUnsynchronisation())
					flags |= FRAME_FLAG_ID3v24_UNSYNCHRONISATION;
				if (frame.flags.getDataLengthIndicator())
					flags |= FRAME_FLAG_ID3v24_DATA_LENGTH_INDICATOR;
			} else if (id3v2_version == 3)
			{
				if (frame.flags.getTagAlterPreservation())
					flags |= FRAME_FLAG_ID3v23_TAG_ALTER_PRESERVATION;
				if (frame.flags.getFileAlterPreservation())
					flags |= FRAME_FLAG_ID3v23_FILE_ALTER_PRESERVATION;
				if (frame.flags.getReadOnly())
					flags |= FRAME_FLAG_ID3v23_READ_ONLY;
				if (frame.flags.getGroupingIdentity())
					flags |= FRAME_FLAG_ID3v23_GROUPING_IDENTITY;
				if (frame.flags.getCompression())
					flags |= FRAME_FLAG_ID3v23_COMPRESSION;
				if (frame.flags.getEncryption())
					flags |= FRAME_FLAG_ID3v23_ENCRYPTION;
			} else
				throw new ID3WriteException("id3v2_version: " + id3v2_version);

			baos.write((byte) (0xff & (flags >> 8)));
			baos.write((byte) (0xff & (flags)));

			baos.write(frame.bytes);
		}

		return baos.toByteArray();
	}

	private void checkTags(MyID3Listener listener, MusicMetadataSet set,
			List frames, boolean strict) throws UnsupportedEncodingException,
			IOException, ID3WriteException
	{
		if (set == null || set.id3v2Raw == null)
			return;

		Vector old_frames = set.id3v2Raw.frames;
		if (old_frames == null)
			return;

		Vector new_frame_ids = new Vector();
		for (int i = 0; i < frames.size(); i++)
		{
			ID3v2OutputFrame frame = (ID3v2OutputFrame) frames.get(i);

			new_frame_ids.add(frame.longFrameID);
		}

		Vector final_frame_ids = new Vector(new_frame_ids);
		for (int i = 0; i < old_frames.size(); i++)
		{
			MyID3v2Frame oldFrame = (MyID3v2Frame) old_frames.get(i);

			String longFrameID;
			Number frameOrder;
			{
				ID3FrameType frame_type = ID3FrameType.get(oldFrame.frameID);
				if (frame_type != null)
				{
					longFrameID = frame_type.longID;
					frameOrder = frame_type.getFrameOrder();
				} else if (oldFrame.frameID.length() == 4)
				{
					longFrameID = oldFrame.frameID;
					frameOrder = ID3FrameType.DEFAULT_FRAME_ORDER;
				} else
				{
					if (strict)
						throw new ID3WriteException("unknown frame type: "
								+ oldFrame.frameID);
					else if (null != listener)
					{
						listener.log("unknown frame type", oldFrame.frameID);
						listener.log("unknown old_tag", oldFrame);
						listener.log(Debug.getStackTrace());
					}

					continue;
				}
			}

			if (new_frame_ids.contains(longFrameID))
				continue;

			if (null != listener)
				listener.log("adding missing frame", longFrameID);

			if (oldFrame instanceof MyID3v2FrameText)
			{
				MyID3v2FrameText text_frame = (MyID3v2FrameText) oldFrame;

				// ID3FrameType frame_type =
				// ID3FrameType.get(old_frame.frame_id);
				if (null != listener)
				{
					listener.log("text_frame", text_frame);
					listener.log("frame_type", longFrameID);
				}

				ID3v2OutputFrame frame = toFrame(longFrameID, frameOrder,
						text_frame.value, text_frame.value2);

				if (frame != null)
				{
					frames.add(frame);
					final_frame_ids.add(frame.longFrameID);
				} else
				{
					if (strict)
						throw new ID3WriteException("Couldn't write frame: "
								+ longFrameID);
					else if (null != listener)
					{
						listener.log("Couldn't write frame", longFrameID);
						listener.log(Debug.getStackTrace());
					}
				}
			} else if (oldFrame instanceof MyID3v2FrameImage)
			{
				MyID3v2FrameImage imageFrame = (MyID3v2FrameImage) oldFrame;

				if (null != listener)
				{
					listener.log("imageFrame", imageFrame);
					listener.log("frame_type", longFrameID);
				}

				ID3v2OutputFrame frame = toFrameImage(longFrameID, frameOrder,
						imageFrame.getImageData());

				frames.add(frame);
				final_frame_ids.add(frame.longFrameID);
			} else
			{
				MyID3v2FrameData data = (MyID3v2FrameData) oldFrame;
				if (data.flags.getTagAlterPreservation())
					continue;
				// if(data.flags.getTagAlterPreservation())
				// continue;
				if (data.frameID.length() == 4)
				{
					// if(data.flags.getCompression() ||
					// data.flags.getUnsynchronisation() || )
					// int flags = data.flags.flags;
					ID3v2OutputFrame frame = new ID3v2OutputFrame(data.frameID,
							data.dataBytes, data.flags);
					frames.add(frame);
					final_frame_ids.add(frame.longFrameID);
					continue;
				}

				if (strict)
					throw new ID3WriteException(
							"Couldn't preserve data frame: " + data.frameID);
				else if (null != listener)
				{
					listener.log("Couldn't preserve data frame", data.frameID);
					listener.log(Debug.getStackTrace());
				}
			}
			// if()
		}
		// Debug.debug("final_frame_ids", final_frame_ids);
	}

	public byte[] toTag(MyID3Listener listener, MusicMetadataSet set,
			IMusicMetadata values, boolean strict) throws Exception
	{
		return toTag(listener, null, set, values, strict);
	}

	public byte[] toTag(MyID3Listener listener, Filter filter,
			MusicMetadataSet set, IMusicMetadata values, boolean strict)
			throws UnsupportedEncodingException, IOException, ID3WriteException
	{
		// Debug.debug("raw values ", values);

		List frames = toFrames(listener, strict, values);

		// Debug.debug("raw frames", frames);

		checkTags(listener, set, frames, strict);

		if (null != listener)
		{
			for (int i = 0; i < frames.size(); i++)
			{
				ID3v2OutputFrame frame = (ID3v2OutputFrame) frames.get(i);
				listener.log("frame", frame.longFrameID);
			}
		}
		// Debug.debug("checked frames", frames);

		byte frame_bytes[] = writeFrames(filter, frames);

		// Debug.debug("frame_bytes", frame_bytes);

		byte extended_header[] = {};
		byte padding[] = {};

		int body_length = extended_header.length + frame_bytes.length
				+ padding.length;

		byte header[] = getHeaderFooter(body_length, false);

		// Debug.debug("body_length", body_length);
		// Debug.debug("header", header);
		byte footer[];
		if (id3v2_version == 4)
			footer = getHeaderFooter(body_length, true);
		else if (id3v2_version == 3)
			footer = null;
		else
			throw new ID3WriteException("id3v2_version: " + id3v2_version);

		// Debug.debug("footer", footer);

		int resultLength = header.length + extended_header.length
				+ frame_bytes.length + padding.length;
		if (footer != null)
			resultLength += footer.length;
		byte result[] = new byte[resultLength];

		int index = 0;
		System.arraycopy(header, 0, result, index, header.length);
		index += header.length;
		System.arraycopy(frame_bytes, 0, result, index, frame_bytes.length);
		if (footer != null)
		{
			index += frame_bytes.length;
			System.arraycopy(footer, 0, result, index, footer.length);
		}
		// index += footer.length;

		return result;
	}
}
