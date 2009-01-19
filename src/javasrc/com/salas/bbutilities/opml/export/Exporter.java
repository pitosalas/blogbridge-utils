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
// $Id: Exporter.java,v 1.5 2006/03/09 15:25:34 spyromus Exp $
//

package com.salas.bbutilities.opml.export;

import com.salas.bbutilities.opml.objects.OPMLGuide;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Exporter of information into OPML format.
 */
public class Exporter extends AbstractExporter
{
    /**
     * Creates exporter.
     *
     * @param aExtendedExport exporter.
     */
    public Exporter(boolean aExtendedExport)
    {
        super(aExtendedExport);
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
        Element root = super.writeOpmlRoot(doc);
        root.addNamespaceDeclaration(BB_NAMESPACE);

        return root;
    }

    /**
     * Writes guide data to the outline.
     *
     * @param body body root.
     * @param guide guide to write.
     */
    void writeGuide(Element body, OPMLGuide guide)
    {
        Element outline = new Element(TAG_OUTLINE);
        body.addContent(outline);
        guide.write(outline, BB_NAMESPACE, extendedExport);
    }
}