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
// $Id: TestDefaultOPMLFeed.java,v 1.6 2007/04/18 13:26:54 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import junit.framework.TestCase;
import org.jdom.Element;

/**
 * This suite contains tests for <code>DefaultOPMLFeed</code> unit.
 */
public class TestDefaultOPMLFeed extends TestCase
{
    /**
     * Tests storing empty titles.
     */
    public void testStoringEmptyTitles()
    {
        Element outline = new Element("ouline");
        DefaultOPMLFeed feed = new DefaultOPMLFeed(null, 1, 1, null, null, -1, false, -1, null, 0) {};
        feed.write(outline, null, false);

        assertEquals("", outline.getAttributeValue("text"));
    }
}
