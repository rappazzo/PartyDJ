package org.cmc.music.metadata;

import org.cmc.music.fs.SongFilenameParser;
import org.cmc.music.myid3.ID3Tag;
import org.cmc.music.myid3.TagFormat;

public class MusicMetadataSet
{
	public final ID3Tag.V1 id3v1Raw;
	public final ID3Tag.V2 id3v2Raw;
	public final IMusicMetadata id3v1Clean;
	public final IMusicMetadata id3v2Clean;
	public final IMusicMetadata filename;
	public final IMusicMetadata merged;

	private MusicMetadataSet(ID3Tag.V1 id3_v1_raw, ID3Tag.V2 id3_v2_raw,
			IMusicMetadata id3_v1_clean, IMusicMetadata id3_v2_clean,
			String file_name, String folder_name)
	{
		this.id3v1Raw = id3_v1_raw;
		this.id3v2Raw = id3_v2_raw;
		this.id3v1Clean = id3_v1_clean;
		this.id3v2Clean = id3_v2_clean;
		this.filename = SongFilenameParser
				.parseFilename(file_name, folder_name);
		this.merged = merge(id3v1Clean, id3v2Clean, filename);
	}

	public IMusicMetadata getSimplified()
	{
		return new MusicMetadata(merged);
	}

	public static final String newline = System.getProperty("line.separator");

	public String toString()
	{
		StringBuffer result = new StringBuffer();

		result.append("{ID3TagSet. ");

		result.append(newline);
		result.append("v1_raw: " + id3v1Raw);
		result.append(newline);
		result.append("v2_raw: " + id3v2Raw);
		result.append(newline);
		result.append("v1: " + id3v1Clean);
		result.append(newline);
		result.append("v2: " + id3v2Clean);
		result.append(newline);
		result.append("filename: " + filename);
		result.append(newline);
		result.append("merged: " + merged);
		result.append(newline);

		result.append(" }");

		return result.toString();
	}

	private static final void merge(IMusicMetadata dst, IMusicMetadata src)
	{
		if (src == null)
			return;

		dst.mergeValuesIfMissing(src);
	}

	private static final IMusicMetadata merge(IMusicMetadata id3v1Clean,
			IMusicMetadata id3v2Clean, IMusicMetadata filename)
	{
		IMusicMetadata result = new MusicMetadata("merged");

		merge(result, id3v2Clean);
		merge(result, id3v1Clean);
		merge(result, filename);

		return result;
	}

	private static final TagFormat utils = new TagFormat();

	public static final MusicMetadataSet factoryMethod(ID3Tag.V1 id3_v1_raw,
			ID3Tag.V2 id3_v2_raw, String filename, String folder_name)
	{
		IMusicMetadata id3_v1_clean = id3_v1_raw == null ? null : utils
				.process(id3_v1_raw.values);
		IMusicMetadata id3_v2_clean = id3_v2_raw == null ? null : utils
				.process(id3_v2_raw.values);

		return new MusicMetadataSet(id3_v1_raw, id3_v2_raw, id3_v1_clean,
				id3_v2_clean, filename, folder_name);
	}

}
