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
// $Id: TestExporterOld.java,v 1.5 2006/06/28 12:15:49 spyromus Exp $
//

package com.salas.bbutilities.opml.export;

import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.Document;
import com.salas.bbutilities.opml.objects.OPMLGuide;
import com.salas.bbutilities.opml.objects.FormatConstants;
import com.salas.bbutilities.opml.objects.OPMLGuideSet;
import com.salas.bbutilities.opml.utils.Transformation;

import java.util.regex.Pattern;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This suite contains tests for <code>Exporter</code> unit.
 */
public class TestExporterOld extends TestCase
{
    private ExporterOld exp;

    protected void setUp() throws Exception
    {
        exp = new ExporterOld(true);
    }

    /**
     * @see Exporter#writeHead
     */
    public void testWriteTitle()
    {
        final Element opml = new Element("opml");
        exp.writeHead(opml, "&'\"<>", null);

        final Element head = opml.getChild("head");
        assertNotNull(head);

        final Element title = head.getChild("title");
        assertNotNull(title);
        assertEquals("&'\"<>", title.getText());
    }

    /**
     * @see Exporter#writeHead
     */
    public void testWriteDateModified()
    {
        long now = System.currentTimeMillis();
        String nowS = new SimpleDateFormat(Exporter.DATE_FORMAT).format(new Date(now));

        final Element opml = new Element("opml");
        exp.writeHead(opml, "&'\"<>", new Date(now));

        final Element head = opml.getChild("head");
        assertNotNull(head);

        final Element date = head.getChild(FormatConstants.TAG_HEAD_DATE_MODIFIED);
        assertNotNull(date);
        assertEquals(nowS, date.getText());
    }

    /**
     * Sets including generator name into the document as the top-level comment.
     */
    public void testGenerated()
    {
        Exporter.setGenerator(" Test  ");
        Document doc = exp.export(new OPMLGuide("a", "b", false, null, null, false, 0, false, false, false));
        String str = Transformation.documentToString(doc);
        String date = new SimpleDateFormat(Exporter.DATE_FORMAT).format(new Date());
        Pattern pat = Pattern.compile("^<\\?[^>]+>[^<]+<!-- Test on " + date + " -->");
        assertTrue(str, pat.matcher(str).find());
    }

    /**
     * Verifies consistency of guides set exporting.
     */
    public void testExportSet()
    {
        Date now = new Date();
        OPMLGuide[] guides = new OPMLGuide[]
            {
                new OPMLGuide("a", "b", true, "a", "b", false, 0, true, true, true),
                new OPMLGuide("c", "d", false, null, null, false, 1, false, false, false)
            };
        OPMLGuideSet set = new OPMLGuideSet("e", guides, now);

        Document doc = exp.export(set);
        Element root = doc.getRootElement();
        Element head = root.getChild("head");
        assertEquals("e", head.getChildTextTrim("title"));
        SimpleDateFormat fmt = new SimpleDateFormat(Exporter.DATE_FORMAT);
        assertEquals(fmt.format(now), head.getChildTextTrim(Exporter.TAG_HEAD_DATE_MODIFIED));

        Element body = root.getChild("body");
        assertEquals(2, body.getChildren().size());
    }
}
