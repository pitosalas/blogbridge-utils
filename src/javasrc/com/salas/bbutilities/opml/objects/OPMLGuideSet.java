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
// $Id: OPMLGuideSet.java,v 1.2 2006/01/24 12:25:51 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import java.util.Date;

/**
 * Contains information about guides and some other.
 */
public class OPMLGuideSet
{
    private final OPMLGuide[]   guides;
    private final String        title;
    private final Date          dateModified;

    /**
     * Creates guide set.
     *
     * @param aTitle        guide set title.
     * @param aGuides       guides in the set.
     * @param aDateModified date this set was modified.
     */
    public OPMLGuideSet(String aTitle, OPMLGuide[] aGuides, Date aDateModified)
    {
        title = aTitle;
        guides = aGuides;
        dateModified = aDateModified;
    }

    /**
     * Returns the list of guides in the set.
     *
     * @return guides.
     */
    public OPMLGuide[] getGuides()
    {
        return guides;
    }

    /**
     * Returns the title of the set.
     *
     * @return title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Returns the modification date.
     *
     * @return date.
     */
    public Date getDateModified()
    {
        return dateModified;
    }
}
