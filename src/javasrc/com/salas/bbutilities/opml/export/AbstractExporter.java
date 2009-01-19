// BlogBridge -- RSS feed reader, manager, and web based service
// Copyright (C) 2002-2006 by R. Pito Salas
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
// $Id: AbstractExporter.java,v 1.3 2007/01/17 12:08:25 spyromus Exp $
//

package com.salas.bbutilities.opml.export;

import com.salas.bbutilities.opml.objects.FormatConstants;
import com.salas.bbutilities.opml.objects.OPMLGuide;
import com.salas.bbutilities.opml.objects.OPMLGuideSet;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Abstract exporter implementation taking care of the basic stuff.
 */
abstract class AbstractExporter implements FormatConstants, IExporter
{
    protected static final String OPML_VERSION = "1.1";
    protected static String generator;

    /** When doing extended export extra information gets into the output. */
    protected final boolean extendedExport;

    /**
     * Creates exporter.
     *
     * @param aExtendedExport <code>TRUE</code> export in extended format.
     */
    public AbstractExporter(boolean aExtendedExport)
    {
        extendedExport = aExtendedExport;
    }

    /**
     * Sets generator info to put in the document.
     *
     * @param aGenerator info.
     */
    public static void setGenerator(String aGenerator)
    {
        AbstractExporter.generator = aGenerator;
    }

    /**
     * Persists guide information into OPML-formed stream.
     *
     * @param guide guide to write.
     *
     * @return xml document with exported information.
     */
    public Document export(OPMLGuide guide)
    {
        return export(new OPMLGuideSet(guide.getTitle(), new OPMLGuide[] { guide }, null));
    }

    /**
     * Exports guides to named document.
     *
     * @param guides    guides.
     * @param aTitle    name of the document.
     *
     * @return document.
     */
    public Document export(OPMLGuide[] guides, String aTitle)
    {
        return export(new OPMLGuideSet(aTitle, guides, null));
    }

    /**
     * Exports the set of guides.
     *
     * @param set guides set.
     *
     * @return document.
     */
    public Document export(OPMLGuideSet set)
    {
        Document doc = new Document();
        Element opml = writeOpmlRoot(doc);

        writeHead(opml, set.getTitle(), set.getDateModified());

        Element body = new Element(TAG_BODY);
        opml.addContent(body);
        writeGuides(body, set.getGuides());

        return doc;
    }

    /**
     * Writes root element with version information.
     *
     * @param doc document.
     *
     * @return root element.
     */
    protected Element writeOpmlRoot(Document doc)
    {
        if (AbstractExporter.generator != null)
        {
            String date = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date());
            doc.addContent(new Comment(" " + AbstractExporter.generator.trim() + " on " + date + " "));
        }

        final Element opml = new Element(TAG_ROOT);
        opml.setAttribute(ATTR_OPML_VERSION, AbstractExporter.OPML_VERSION);
        doc.setRootElement(opml);

        return opml;
    }

    /**
     * Writes title to document.
     *
     * @param opml          root element to put head/title section in.
     * @param title         title to write.
     * @param dateModified  date of modification.
     */
    protected void writeHead(Element opml, String title, Date dateModified)
    {
        Element head = new Element(TAG_HEAD);
        opml.addContent(head);

        Element titleE = new Element(TAG_HEAD_TITLE);
        head.addContent(titleE);
        titleE.setText(title);

        if (dateModified != null)
        {
            Element dateModifiedE = new Element(TAG_HEAD_DATE_MODIFIED);
            head.addContent(dateModifiedE);
            dateModifiedE.setText(new SimpleDateFormat(DATE_FORMAT, Locale.US).format(dateModified));
        }
    }

    /**
     * Writes list of guides to the list.
     *
     * @param body body element.
     * @param guides list of guides.
     */
    void writeGuides(Element body, OPMLGuide[] guides)
    {
        for (OPMLGuide guide : guides) writeGuide(body, guide);
    }

    /**
     * Writes guide data to the outline.
     *
     * @param body body root.
     * @param guide guide to write.
     */
    abstract void writeGuide(Element body, OPMLGuide guide);
}
