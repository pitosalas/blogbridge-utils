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
// $Id: NetUtils.java,v 1.1 2005/11/03 12:39:22 spyromus Exp $
//

package com.salas.bbutilities;

import java.util.regex.Pattern;

/**
 * The set of different network-related utils.
 */
public final class NetUtils
{
    /** Hidden utility class constructor. */
    private NetUtils()
    {
    }

    /**
     * Replaces <code>feed://</code> with <code>http://</code> if required.
     *
     * @param url source URL.
     *
     * @return modified URL.
     */
    public static String fixFeedURL(String url)
    {
        if (url != null)
        {
            url = url.trim();
            if (url.length() == 0)
            {
                url = null;
            } else if (url.startsWith("feed:"))
            {
                url = url.substring(5).replaceAll("^/+", "").trim();
                if (url.startsWith("feed:"))
                {
                    url = fixFeedURL(url);
                } else if (!url.startsWith("http:"))
                {
                    url = "http://" + url;
                }
            }
        }

        return url;
    }
}
