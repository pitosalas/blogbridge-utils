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
// $Id: TestVersionUtils.java,v 1.1 2005/08/30 08:09:34 spyromus Exp $
//

package com.salas.bbutilities;

import junit.framework.TestCase;

/**
 * This suite contains tests for <code>VersionUtils</code> unit.
 */
public class TestVersionUtils extends TestCase
{
    /**
     * @see com.salas.bbutilities.VersionUtils#toVersion
     */
    public void testToVersion()
    {
        assertEquals(0, VersionUtils.toVersion(null));
        assertEquals(0, VersionUtils.toVersion(""));
        assertEquals(0, VersionUtils.toVersion("a"));
        assertEquals(0, VersionUtils.toVersion("1.a"));
        assertEquals(0, VersionUtils.toVersion("1.1.a"));

        assertEquals(1 << 16, VersionUtils.toVersion("1"));
        assertEquals(((1 << 8) | 10) << 8, VersionUtils.toVersion("1.10"));
        assertEquals((((1 << 8) | 10) << 8) | 9, VersionUtils.toVersion("1.10.9"));
        assertEquals((((1 << 8) | 10) << 8) | 9, VersionUtils.toVersion("1.10.9.1"));
    }
}
