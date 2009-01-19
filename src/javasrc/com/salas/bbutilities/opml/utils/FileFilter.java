// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002, 2003, 2004 by R. Pito Salas
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software Foundation;
// either version 2 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program;
// if not, write to the Free Software Foundation, Inc., 59 Temple Place,
// Suite 330, Boston, MA 02111-1307 USA
//
// Contact: R. Pito Salas
// mailto:pitosalas@users.sourceforge.net
// More information: about BlogBridge
// http://www.blogbridge.com
// http://sourceforge.net/projects/blogbridge
//
// $Id: FileFilter.java,v 1.1 2005/08/05 11:19:37 spyromus Exp $
//

package com.salas.bbutilities.opml.utils;

import java.io.File;

/**
 * Filer for OPML - file selection dialogs.
 */
public final class FileFilter extends javax.swing.filechooser.FileFilter
{
    private static final FileFilter INSTANCE = new FileFilter();

    private FileFilter()
    {
        // private constructor for singleton
    }

    /**
     * Returns instance of filter.
     *
     * @return instance.
     */
    public static FileFilter getInstance()
    {
        return INSTANCE;
    }

    /**
     * Whether the given file is accepted by this filter.
     *
     * @param f file to check.
     *
     * @return TRUE if file is accepted.
     */
    public boolean accept(File f)
    {
        boolean accept = false;

        if (f != null)
        {
            String name = f.getName().toLowerCase();
            accept = f.isDirectory() || name.endsWith(".opml") || name.endsWith(".xml");
        }

        return accept;
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     *
     * @see javax.swing.filechooser.FileView#getName
     */
    public String getDescription()
    {
        return "OPML Outline";
    }
}
