package org.cmc.music.util;

import java.io.File;

//import org.cmc.shared.util.Debug; import com.exe4j.*;

public class FSTraversal
{

	public static final int kFILES = 1;
	public static final int kFOLDERS = 2;
	public static final int kFILES_AND_FOLDERS = 3;
	public static final int kALL = 4;

	public interface Visitor
	{
		public boolean visit(File file, double progress_estimate);
	}

	public boolean traverseFiles(File file, Visitor visitor)
	{

		return traverse(file, kFILES, visitor);
	}

	public boolean traverseFolders(File file, Visitor visitor)
	{

		return traverse(file, kFOLDERS, visitor);
	}

	public boolean traverseAll(File file, Visitor visitor)
	{

		return traverse(file, kFILES_AND_FOLDERS, visitor);
	}

	public boolean traverse(File file, int mode, Visitor visitor)
	{
		return traverse(file, mode, visitor, 0, 1);
	}

	private boolean traverse(File file, int mode, Visitor visitor,
			double estimate, double estimate_increment)
	{

		if (file.isFile())
		{
			if ((mode == kFILES) || (mode == kFILES_AND_FOLDERS)
					|| (mode == kALL))
			{
				if (!visitor.visit(file, estimate))
					return false;
			}
		} else if (file.isDirectory())
		{
			File files[] = file.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					File child = files[i];

					// new FSTraversal().traverse(files[i], mode, visitor,
					// estimate * i / files.length);
					// Debug.debug("estimate: " + estimate + ", i: " + i
					// + ", files.length: " + files.length + ", result: "
					// + (estimate * i / files.length));
					if (!traverse(child, mode, visitor, estimate
							+ estimate_increment * i / files.length,
							estimate_increment / files.length))
						return false;
				}
			}

			if ((mode == kFOLDERS) || (mode == kFILES_AND_FOLDERS)
					|| (mode == kALL))
			{
				if (!visitor.visit(file, estimate))
					return false;
			}
		} else
		{
			if (mode == kALL)
			{
				if (!visitor.visit(file, estimate))
					return false;
			}
		}

		return true;
	}

}