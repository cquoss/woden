package org.apache.woden.tool.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Matthew J. Duftler <duftler@us.ibm.com>
 */
public class Utils
{
  public static OutputStream getOutputStream(String root,
                                             String name,
                                             boolean overwrite,
                                             boolean verbose)
                                               throws IOException
  {
    if (root != null)
    {
      File directory = new File(root);

      if (!directory.exists())
      {
        if (!directory.mkdirs())
        {
          throw new IOException("Failed to create directory '" + root + "'.");
        }
        else if (verbose)
        {
          System.out.println("Created directory '" +
                             directory.getAbsolutePath() + "'.");
        }
      }
    }

    File file = new File(root, name);
    String absolutePath = file.getAbsolutePath();

    if (file.exists())
    {
      if (!overwrite)
      {
        throw new IOException("File '" + absolutePath + "' already exists. " +
                              "Please remove it or enable the " +
                              "-overwrite option.");
      }
      else
      {
        file.delete();

        if (verbose)
        {
          System.out.println("Deleted file '" + absolutePath + "'.");
        }
      }
    }

    if (verbose)
    {
      System.out.println("Created file '" + absolutePath + "'.");
    }

    return new FileOutputStream(absolutePath);
  }
}
