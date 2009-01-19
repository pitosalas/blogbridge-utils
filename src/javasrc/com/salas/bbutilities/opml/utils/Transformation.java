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
// $Id: Transformation.java,v 1.2 2006/05/16 09:31:23 spyromus Exp $
//

package com.salas.bbutilities.opml.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import java.io.StringWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

/**
 * Transformation utilities.
 */
public final class Transformation
{
    private static final Logger LOG = Logger.getLogger(Transformation.class.getName());

    /** Hidden utility class constructor. */
    private Transformation()
    {
    }

    /**
     * Saves XML document to string.
     *
     * @param aDocument document.
     *
     * @return string.
     */
    public static String documentToString(Document aDocument)
    {
        String result = null;

        StringWriter sw = new StringWriter();
        try
        {
            XMLOutputter xo = new XMLOutputter(Format.getRawFormat());
            xo.output(aDocument, sw);
            sw.flush();
            result = sw.getBuffer().toString();
        } catch (IOException e)
        {
            LOG.log(Level.SEVERE, "Unable to store guides document to string.", e);
        } finally
        {
            try
            {
                sw.close();
            } catch (IOException e)
            {
                // Nothing to do here.
            }
        }

        return result;
    }

    /**
     * Converts names of all attributes to lower case.
     *
     * @param element element to lower case attributes of.
     *
     * @throws NullPointerException if element is not specified.
     */
    public static void lowercaseAttributes(Element element)
    {
        List attributesList = element.getAttributes();
        Attribute[] attributes = (Attribute[])attributesList.toArray(
            new Attribute[attributesList.size()]);

        for (int i = 0; i < attributes.length; i++)
        {
            Attribute attribute = attributes[i];
            String name = attribute.getName();
            if (name != null) attribute.setName(name.toLowerCase());
        }
    }
}
