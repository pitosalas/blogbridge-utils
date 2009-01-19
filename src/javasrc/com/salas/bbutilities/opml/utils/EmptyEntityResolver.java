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
// $Id: EmptyEntityResolver.java,v 1.1 2005/08/05 11:19:37 spyromus Exp $
//

package com.salas.bbutilities.opml.utils;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.StringReader;

/**
 * Resolver which resolves nothing all the time.
 */
public class EmptyEntityResolver implements EntityResolver
{
    /** Instance of resolver. */
    public static final EmptyEntityResolver INSTANCE = new EmptyEntityResolver();

    /**
     * Hidden singleton constructor.
     */
    private EmptyEntityResolver()
    {
    }

    /**
     * Allow the application to resolve external entities.
     *
     * <p>The Parser will call this method before opening any external
     * entity except the top-level document entity (including the
     * external DTD subset, external entities referenced within the
     * DTD, and external entities referenced within the document
     * element): the application may request that the parser resolve
     * the entity itself, that it use an alternative URI, or that it
     * use an entirely different input source.</p>
     *
     * <p>If the system identifier is a URL, the SAX parser must
     * resolve it fully before reporting it to the application.</p>
     *
     * @param publicId The public identifier of the external entity
     *        being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity
     *        being referenced.
     *
     * @return An InputSource object describing the new input source,
     *         or null to request that the parser open a regular
     *         URI connection to the system identifier.
     */
    public InputSource resolveEntity(String publicId, String systemId)
    {
        return new InputSource(new StringReader("".intern()));
    }
}
