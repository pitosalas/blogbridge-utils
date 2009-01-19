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
// $Id: VersionUtils.java,v 1.1 2005/08/30 08:09:34 spyromus Exp $
//

package com.salas.bbutilities;

import java.util.StringTokenizer;

/**
 * Version utilities.
 */
public final class VersionUtils
{
    /**
     * Hidden singleton constructor.
     */
    private VersionUtils()
    {
    }

    /**
     * Converts version string to version number. Versions are accepted in formats:
     * <ul>
     *  <li><b>X</b> - single number which is equal to <b>X.0.0</b>.</li>
     *  <li><b>X.Y</b> - two-number version which is equal to <b>X.Y.0</b>.</li>
     *  <li><b>X.Y.Z</b> - three-number version.</li>
     * </ul>
     *
     * @param versionString version string.
     *
     * @return version number <code>(((x << 8) | y) << 8) | z</code>.
     */
    public static long toVersion(String versionString)
    {
        if (versionString == null) return 0;

        int x, y, z, tokens;
        StringTokenizer tok;

        x = 0;
        y = 0;
        z = 0;

        tok = new StringTokenizer(versionString, ".");
        tokens = tok.countTokens();

        try
        {
            if (tokens > 0) x = Integer.parseInt(tok.nextToken());
            if (tokens > 1) y = Integer.parseInt(tok.nextToken());
            if (tokens > 2) z = Integer.parseInt(tok.nextToken());
        } catch (NumberFormatException e)
        {
            // number format exception is a bad thing
            x = 0;
            y = 0;
        }

        return (((x << 8) | y) << 8) | z;
    }

    /**
     * Compares two versions in X.Y.Z format.
     *
     * @param first     first version.
     * @param second    second version.
     *
     * @return -1 if first < second, 0 if first == second and 1 if first > second.
     */
    public static int versionCompare(String first, String second)
    {
        if (first == null || second == null)
            throw new IllegalArgumentException("2 versions needed");

        long version1 = toVersion(first);
        long version2 = toVersion(second);

        int result = 0;
        if (version1 < version2)
        {
            result = -1;
        } else if (version1 > version2)
        {
            result = 1;
        }

        return result;
    }
}
