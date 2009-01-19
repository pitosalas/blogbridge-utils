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
// $Id: ImporterException.java,v 1.2 2006/02/27 09:09:18 spyromus Exp $
//

package com.salas.bbutilities.opml;

import java.io.IOException;

/**
 * OPML import operation exception. This exception can be of several types.
 */
public final class ImporterException extends Exception
{
    /** URL is incorrectly formed. */
    public static final int TYPE_MALFORMED_URL = 0;

    /** Content of resource is unparsable. */
    public static final int TYPE_PARSING = 1;

    /** Error during IO operation. */
    public static final int TYPE_IO = 2;

    private static final String[] TYPES;

    private int type;

    static
    {
        TYPES = new String[3];
        TYPES[TYPE_MALFORMED_URL] = "Bad or malformed URL specified.";
        TYPES[TYPE_PARSING] = "Format of data is incorrect.";
        TYPES[TYPE_IO] = "Cannot download resource.";
    }

    /**
     * Creates new exception object.
     *
     * @param type    type of exception (see <code>TYPE_xxxx</code>)
     * @param message description message.
     */
    private ImporterException(int type, String message)
    {
        this(type, message, null);
    }

    /**
     * Creates new exception object.
     *
     * @param type    type of exception (see <code>TYPE_xxxx</code>)
     * @param message description message.
     * @param cause   original exception cause.
     */
    private ImporterException(int type, String message, Throwable cause)
    {
        super(message, cause);
        this.type = type;
    }

    /**
     * Returns type of exception.
     *
     * @return the type.
     */
    public int getType()
    {
        return type;
    }

    /**
     * Returns string representation of a type.
     *
     * @param ex exception to convert to readable string.
     *
     * @return description string.
     */
    public static String getStringType(ImporterException ex)
    {
        return ex.getType() != TYPE_PARSING ? TYPES[ex.getType()] : ex.getMessage();
    }

    /**
     * Constructs exception object of <code>TYPE_MALFORMED_URL</code> type.
     *
     * @param message description message.
     *
     * @return exception object.
     */
    public static ImporterException malformedUrl(String message)
    {
        return new ImporterException(TYPE_MALFORMED_URL, message);
    }

    /**
     * Constructs exception object of <code>TYPE_PARSING</code> type.
     *
     * @param message description message.
     *
     * @return exception object.
     */
    public static ImporterException parsing(String message)
    {
        return new ImporterException(TYPE_PARSING, message);
    }

    /**
     * Constructs exception object of <code>TYPE_IO</code> type.
     *
     * @param e exception.
     *
     * @return exception object.
     */
    public static ImporterException io(IOException e)
    {
        return new ImporterException(TYPE_IO, e.getMessage(), e);
    }
}
