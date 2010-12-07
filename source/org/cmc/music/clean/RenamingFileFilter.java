/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.cmc.music.clean;

/**
 * @author charles
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.io.File;

import javax.swing.JOptionPane;

import org.cmc.music.util.FileComparator;
import org.cmc.music.util.FileFilter;

public abstract class RenamingFileFilter implements FileFilter
{

	protected boolean isMusicFile(File file)
	{
		if (file.isDirectory())
			return false;

		String s = file.getName().toLowerCase();
		return (s.endsWith(".mp3") | s.endsWith(".ogg"));
	}

	protected abstract String getNewName2(File file, String s);

	private void doRenaming(File file, String new_name)
	{

		System.out.println("renaming: '" + file.getName() + "' to '" + new_name
				+ "' at: " + file.getParent());

		File file2 = (new File(file.getParentFile(), new_name));

		if (file.getName().toLowerCase().equals(file2.getName().toLowerCase()))
		{
			try
			{
				File temp = new File(file.getParentFile(), file.getName()
						+ ".tmp");

				/*
				 * File.createTempFile( file2.getName(), ".tmp",
				 * file.getParentFile());
				 */

				// System.out.println("temp: " + temp.getAbsolutePath());
				if (!file.renameTo(temp))
					System.out.println("rename failed file->temp: "
							+ temp.getAbsolutePath());
				if (!temp.renameTo(file2))
					System.out.println("rename failed temp->file2: "
							+ temp.getAbsolutePath());
			} catch (Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else
		{
			if (file2.exists())
			{
				System.out.println("could not rename: already exists");
				FileComparator fc = new FileComparator();
				boolean exact = fc.compare(file, file2);
				if (exact)
				{
					String options[] = { "Delete A", "Delete B", "Ignore", };
					String msg = "\n" + "Identical files found in same folder:"
							+ "\n" + "" + "\n" + "A: '"
							+ file.getAbsolutePath() + "'" + "\n" + "B: '"
							+ file2.getAbsolutePath() + "'" + "\n" + "" + "\n"
							+ "";
					int choice = JOptionPane.showOptionDialog(null, msg,
							"Duplicate found", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options,
							options[2]);
					{
						if (choice == 0)
						{
							System.out.println("delete left");
							file.delete();
						} else if (choice == 1)
						{
							System.out.println("delete right");
							file2.delete();
						}
					}
				}
			} else
				file.renameTo(file2);
		}

	}

	public boolean process(File file)
	{
		if (!file.isDirectory() && !isMusicFile(file))
			return false;

		final String name = file.getName();
		String name2 = null;
		if (file.isDirectory())
		{
			name2 = getNewName2(file, name);
		} else
		{
			String ext = getExtension(name);
			String stripped = stripExtension(name);
			name2 = getNewName2(file, stripped);
			if (name2 != null)
				name2 = name2 + ext.toLowerCase();
		}

		// Debug.debug("name", name);
		// Debug.debug("name2", name2);
		// Debug.debug();

		if ((name2 != null) && (name2.length() > 0) && (!name.equals(name2)))
		{
			doRenaming(file, name2);
			return true;
		}
		return false;
	}

	protected String getExtension(String s)
	{
		int index = s.lastIndexOf('.');
		if (index < 0)
			return null;

		return s.substring(index);
	}

	protected String getExtensionNonNull(String s)
	{
		String result = getExtension(s);
		if (result == null)
			return "";
		return result;
	}

	protected String stripExtension(String s)
	{
		int index = s.lastIndexOf('.');
		if (index < 0)
			return s;

		return s.substring(0, index);
	}

	protected String toTitleCase(String s)
	{
		StringBuffer result = new StringBuffer();
		char prev = 0;
		// if (s.startsWith("The Jackson 5 - I'Ll Be There"))
		// System.out.println(s);

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			// char next = 0;
			// if ((i + 1) < s.length())
			// next = s.charAt(i + 1);

			if (Character.isLetter(c))
			{
				if (i == 0)
					result.append(Character.toUpperCase(c));
				else if ((prev == '\''))
				{
					if (i > 1 && Character.isLetter(s.charAt(i - 2)))
						result.append(Character.toLowerCase(c));
					else
						result.append(Character.toUpperCase(c));
				} else if (!Character.isLetter(prev))
					result.append(Character.toUpperCase(c));
				else
					result.append(Character.toLowerCase(c));
			} else
				result.append(c);
			prev = c;
		}

		// Debug.debug("toTitleCase. src: " + s + ", dst: " + result);

		return result.toString();
	}

	// public static void main(String args[])
	// {
	// String s = "SinéAd O'connor";
	// {
	//
	// Debug.debug("toTitleCase", new MusicOrganizerFilter()
	// .toTitleCase(s));
	// Debug.debug("getNewName2", new MusicOrganizerFilter()
	// .getNewName2(s));
	// }
	// // new MusicOrganizerFilter().toTitleCase("SinéAd O'connor");
	// }

	protected String insertSpacesBeforeCaps(String s)
	{
		StringBuffer result = new StringBuffer();

		char prev = 0;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);

			if (Character.isLetter(c) && (i > 0) && Character.isLetter(prev)
					&& Character.isLowerCase(prev) && Character.isUpperCase(c))
				result.append(' ');

			result.append(c);

			prev = c;
		}

		return result.toString();
	}
}