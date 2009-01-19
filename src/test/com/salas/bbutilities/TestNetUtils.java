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
// $Id: TestNetUtils.java,v 1.1 2005/11/03 12:41:29 spyromus Exp $
//

package com.salas.bbutilities;

import junit.framework.TestCase;

/**
 * This suite contains tests for <code>NetUtils</code> unit.
 */
public class TestNetUtils extends TestCase
{
    /**
     * Tests fixing various feed URL's.
     */
    public void testFixFeedURL()
    {
        assertNull("NULL should be returned for NULL.", NetUtils.fixFeedURL(null));

        assertNull("NULL should be returned for empty string.", NetUtils.fixFeedURL(""));
        assertNull("NULL should be returned for empty string.", NetUtils.fixFeedURL(" "));

        assertEquals("We respect relative links.",
            "a", NetUtils.fixFeedURL(" a "));

        assertEquals("Protocol should be preserved.",
            "https://a", NetUtils.fixFeedURL(" https://a "));

        assertEquals("There's FEED: without HTTP.",
            "http://a", NetUtils.fixFeedURL("feed://a"));

        assertEquals("There's HTTP with FEED.",
            "http://a", NetUtils.fixFeedURL("feed:http://a"));

        assertEquals("There's HTTP with FEED.",
            "http://a", NetUtils.fixFeedURL("feed://http://a"));

        assertEquals("There's HTTP with FEED.",
            "http://a", NetUtils.fixFeedURL("feed:feed://http://a"));
    }
}
