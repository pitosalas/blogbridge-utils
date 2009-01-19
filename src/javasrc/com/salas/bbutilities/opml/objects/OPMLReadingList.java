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
// $Id: OPMLReadingList.java,v 1.5 2008/03/17 16:07:25 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

import java.util.List;
import java.util.ArrayList;

/**
 * Reading list object.
 */
public class OPMLReadingList implements FormatConstants
{
    private final String title;
    private final String url;

    private List<DirectOPMLFeed> feeds;

    /**
     * Creates a reading list.
     *
     * @param aTitle    title.
     * @param aUrl      URL.
     */
    public OPMLReadingList(String aTitle, String aUrl)
    {
        title = aTitle;
        url = aUrl;
        feeds = new ArrayList<DirectOPMLFeed>();
    }

    /**
     * Returns title.
     *
     * @return title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Returns URL.
     *
     * @return URL.
     */
    public String getURL()
    {
        return url;
    }

    /**
     * Sets feeds all at once.
     *
     * @param aFeeds feeds.
     */
    public void setFeeds(List<DirectOPMLFeed> aFeeds)
    {
        feeds = aFeeds;
    }

    /**
     * Returns feeds.
     *
     * @return feeds list.
     */
    public List getFeeds()
    {
        return feeds;
    }

    /**
     * Writes list data to the outline.
     *
     * @param outline           outline to write list data to.
     * @param bbns              BB namespace.
     * @param extendedExport    <code>TRUE</code> to write extended information.
     */
    public void write(Element outline, Namespace bbns, boolean extendedExport)
    {
        outline.setAttribute(ATTR_FEED_TYPE, "list");
        outline.setAttribute(ATTR_READING_LIST_TITLE, title);
        outline.setAttribute(ATTR_READING_LIST_URL, url);

        for (DefaultOPMLFeed feed : feeds)
        {
            if (extendedExport || feed instanceof DirectOPMLFeed)
            {
                Element feedOutline = new Element("outline");
                outline.addContent(feedOutline);
                feed.write(feedOutline, bbns, extendedExport);
            }
        }
    }

    /**
     * Returns <code>TRUE</code> if objects are equal.
     *
     * @param o other object.
     *
     * @return <code>TRUE</code> if equal.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OPMLReadingList that = (OPMLReadingList)o;

        return url.equals(that.url);
    }

    /**
     * Calculates hash code.
     *
     * @return hash code.
     */
    public int hashCode()
    {
        return url.hashCode();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString()
    {
        return "ReadingList: title=" + title +
            ", url=" + url +
            ", feeds=" + feeds;
    }
}
