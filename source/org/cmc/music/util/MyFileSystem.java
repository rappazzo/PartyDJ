/*
 * Written By Charles M. Chen
 * charlesmchen@gmail.com
 * Created on Mar 1, 2005
 * 
 * 
 * 
 *
 */

package org.cmc.music.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author charles
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class MyFileSystem
{
	public static final String file_seperator = System
			.getProperty("file.separator");

	public static final MyComparator kFILE_BY_DATE = new MyComparator() {
		public int compare(Object o1, Object o2)
		{
			File f1 = (File) o1;
			File f2 = (File) o2;
			if (f1.lastModified() > f2.lastModified())
				return 1;
			if (f1.lastModified() < f2.lastModified())
				return -1;
			return 0;
		}
	};

	public String getExtension(File file)
	{
		if (file == null)
			return null;

		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index < 0)
			return null;
		return name.substring(index + 1).toLowerCase();
	}

	public String getExtensionNonNull(File file)
	{
		String ext = getExtension(file);
		if (ext == null)
			return "";
		return ext;
	}

	public String getFilenameWithoutExtension(File file)
	{
		if (file == null)
			return null;

		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index < 0)
			return name;
		return name.substring(0, index);
	}

	public void delete(File file)
	{
		if (file.isFile())
		{
			file.delete();
		} else if (file.isDirectory())
		{
			File files[] = file.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
					delete(files[i]);
			}
			file.delete();
		}
	}

	public interface RenamingFilter
	{
		public String filter(String s);
	}

	public void copy_to_folder(File src, File dst) throws IOException
	{
		copy_to_folder(src, dst, null);
	}

	public void copy_to_folder(File src, File dst, RenamingFilter filter)
			throws IOException
	{
		if (src.isFile())
		{
			String old_name = src.getName();
			if (filter != null)
				old_name = filter.filter(old_name);

			if (old_name != null)
			{
				File new_file = new File(dst, old_name);
				new_file.getParentFile().mkdirs();
				new FileIO().copyToFile(src, new_file);
			}
		} else if (src.isDirectory())
		{
			String old_name = src.getName();
			if (filter != null)
				old_name = filter.filter(old_name);

			if (old_name != null)
			{
				File new_folder = new File(dst, old_name);

				File files[] = src.listFiles();
				if (files != null)
				{
					for (int i = 0; i < files.length; i++)
						copy_to_folder(files[i], new_folder);
				}
			}
			// file.delete();
		}
	}

	public void copy_contents_to_folder(File src, File dst) throws IOException
	{
		copy_contents_to_folder(src, dst, null);
	}

	public void copy_contents_to_folder(File src, File dst,
			RenamingFilter filter) throws IOException
	{
		if (src.exists() && src.isDirectory())
		{
			String old_name = src.getName();
			if (filter != null)
				old_name = filter.filter(old_name);

			if (old_name != null)
			{
				File files[] = src.listFiles();
				if (files != null)
				{
					for (int i = 0; i < files.length; i++)
						copy_to_folder(files[i], dst);
				}
			}
		}
	}

	public long getByteCount(Vector v)
	{
		long result = 0;

		for (int i = 0; i < v.size(); i++)
		{
			File file = (File) v.get(i);
			if (file.isFile())
				result += file.length();
		}

		return result;
	}

	public File getNewestFile(Vector v)
	{
		File newest = null;
		long mod = Long.MIN_VALUE;
		for (int i = 0; i < v.size(); i++)
		{
			File file = (File) v.get(i);
			if (file.lastModified() > mod)
			{
				mod = file.lastModified();
				newest = file;
			}
		}
		return newest;
	}

	public Vector getChildrenFiles(File file)
	{
		return getChildren(file, false, true);
	}

	public Vector getChildrenFolders(File file)
	{
		return getChildren(file, true, false);
	}

	public Vector getChildrenFilesAndFolders(File file)
	{
		return getChildren(file, true, true);
	}

	private Vector getChildren(File file, boolean include_folders,
			boolean include_files)
	{
		Vector result = new Vector();

		if (null == file)
			return null;
		if (!file.exists())
			return null;

		if (file.isFile())
		{
			if (include_files)
				result.add(file);
		} else if (file.isDirectory())
		{
			File files[] = file.listFiles();
			if (null != files)
			{
				Arrays.sort(files);
				for (int i = 0; i < files.length; i++)
				{
					Vector v = (getChildren(files[i], include_folders,
							include_files));
					if (v != null)
						result.addAll(v);
				}
			}

			if (include_folders)
				result.add(file);
		}

		return result;
	}

	public Vector filterFoldersOnly(Vector v)
	{
		Vector result = new Vector();

		for (int i = 0; i < v.size(); i++)
		{
			File file = (File) v.get(i);
			if ((file.exists()) && (file.isDirectory()))
				result.add(file);
		}

		return result;
	}

	public Vector filterFilesOnly(Vector v)
	{
		Vector result = new Vector();

		for (int i = 0; i < v.size(); i++)
		{
			File file = (File) v.get(i);
			if ((file.exists()) && (file.isFile()))
				result.add(file);
		}

		return result;
	}

	public interface BackupFilter
	{
		public boolean copy(File file);

		public boolean delete(File file);
	}

	public void backup(File src, File dst, boolean debug)
	{
		backup(src, dst, null, debug);
	}

	public void backup(
	// Listener listener, boolean debug, boolean nice,
			// boolean root, boolean reverse,
			File src, File dst, BackupFilter filter, boolean debug)
	{
		try
		{
			if ((null != filter) && (!filter.copy(src)))
				return;

			if (src.isFile())
			{
				if (dst.exists())
				{
					// delete(
					// // debug, nice, reverse,
					// dst);
					if (!dst.isFile())
					{
						if ((null != filter) && (!filter.delete(dst)))
							return;

						System.out.println(
						// (mode.delete_obstruction()
								// ? ""
								// : "not ")
								// +
								"deleting obstruction: "
										+ dst.getAbsolutePath());

						// if (mode.delete_obstruction())
						// {
						if (!debug)
							delete(
							// debug, nice, reverse,
							dst);
						// }
					} else if (
					// mode.overwrite_existing() &&
					(src.lastModified() > dst.lastModified()))
					{
						if ((null != filter) && (!filter.delete(dst)))
							return;

						System.out.println("deleting non-matching: "
								+ dst.getAbsolutePath());
						if (!debug)
							delete(
							// debug, nice, reverse,
							dst);

					}
				}
				if (!dst.exists())
				{
					System.out
							.println("copying file: " + dst.getAbsolutePath());
					if (!debug)
					{
						dst.getParentFile().mkdirs();
						copy_to_folder(src, dst.getParentFile());
						// check_nice(nice);
					}
				}
			} else if (src.isDirectory())
			{
				{
					if (dst.exists() && !dst.isDirectory())
					{
						if ((null != filter) && (!filter.delete(dst)))
							return;

						System.out.println(
						// (mode.delete_obstruction()
								// // ? ""
								// // : "not ")
								// // +
								"deleting obstructing file: "
										+ dst.getAbsolutePath());

						// if (mode.delete_obstruction())
						if (!debug)
						{
							delete(
							// debug, nice, reverse,
							dst);
						}
					}
					if (!dst.exists())
					{
						System.out.println("making dir: "
								+ dst.getAbsolutePath());
						if (!debug)
							dst.mkdirs();
					}
				}
				{ // step one, copy over...
					File files[] = src.listFiles();
					// sort(files, reverse);
					Arrays.sort(files, MyComparator.kFileName);

					for (int i = 0; i < files.length; i++)
					{
						File child_src = files[i];
						File child_dst = new File(dst, child_src.getName());
						backup(
						// listener, debug, nice, false, reverse,
								child_src, child_dst, filter, debug);

						// if (root)
						// {
						// if (listener != null)
						// {
						// listener.log("copy " + child_src.getName());
						//
						// listener.setProgress(i, files.length);
						// }
						// }

					}
				}
				// if (mode.delete_missing())
				{ // step two, delete extraneous...
					if (dst.exists() && dst.isDirectory())
					{
						File files[] = dst.listFiles();
						// sort(files, reverse);
						Arrays.sort(files, MyComparator.kFileName);

						for (int i = 0; i < files.length; i++)
						{
							File child_dst = files[i];
							File child_src = new File(src, child_dst.getName());
							if (!child_src.exists())
							{
								if ((null != filter)
										&& (!filter.delete(child_dst)))
									return;

								System.out.println("deleting obsolete file: "
										+ child_dst.getAbsolutePath());
								if (!debug)
									delete(
									// debug, nice, reverse,
									child_dst);
								// child_dst.delete();
							}
							// if (root)
							// {
							// if (listener != null)
							// {
							// listener.log("delete "
							// + child_src.getName());
							//
							// listener.setProgress(i, files.length);
							// }
							// }
						}
					}
				}
			}
		} catch (Exception e)
		{
			Debug.debug(e);
		}

	}

}