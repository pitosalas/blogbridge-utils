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
// $Id: SearchOPMLFeed.java,v 1.11 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Search Feed information holder.
 */
public class SearchOPMLFeed extends DefaultOPMLFeed
{
    private String  query;

    private boolean dedupEnabled = false;
    private int     dedupFrom = -1;
    private int     dedupTo = -1;

    /**
     * Creates holder.
     *
     * @param aTitle    title of feed.
     * @param aQuery    query string.
     * @param aLimit    articles (purge) limit.
     * @param aRating   rating of the feed.
     * @param aViewType view type.
     * @param aViewModeEnabled   custom view mode state.
     * @param aViewMode          custom view mode.
     * @param aAscendingSorting  ascending sorting override flag.
     */
    public SearchOPMLFeed(String aTitle, String aQuery, int aLimit, int aRating, int aViewType,
                          boolean aViewModeEnabled, int aViewMode, Boolean aAscendingSorting)
    {
        super(aTitle, aLimit, aRating, null, null, aViewType, aViewModeEnabled, aViewMode, aAscendingSorting);

        query = aQuery;
    }

    /**
     * Returns remove duplicates flag.
     *
     * @return remove duplicates flag.
     */
    public boolean isDedupEnabled()
    {
        return dedupEnabled;
    }

    /**
     * Sets remove duplicates flag.
     *
     * @param flag remove duplicates flag.
     */
    public void setDedupEnabled(boolean flag)
    {
        dedupEnabled = flag;
    }

    /**
     * Returns the index of the first dedup word.
     *
     * @return the index.
     */
    public int getDedupFrom()
    {
        return dedupFrom;
    }

    /**
     * Sets the index of the first dedup word.
     *
     * @param index the index.
     */
    public void setDedupFrom(int index)
    {
        dedupFrom = index;
    }

    /**
     * Returns the index of the last dedup word.
     *
     * @return the index.
     */
    public int getDedupTo()
    {
        return dedupTo;
    }

    /**
     * Sets the index of the last dedup word.
     *
     * @param index the index.
     */
    public void setDedupTo(int index)
    {
        dedupTo = index;
    }

    /**
     * Returns search feed query string.
     *
     * @return query string.
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * Writes data to outline.
     *
     * @param outline        outline.
     * @param bbns           BB namespace.
     * @param extendedExport TRUE to write extended data.
     */
    public void write(Element outline, Namespace bbns, boolean extendedExport)
    {
        outline.setAttribute(ATTR_FEED_TYPE, TYPE_SEARCH);

        // It's better to have TEXT attribute only, but we maintain backward compatibility
        // with versions up to 2.3 by saving the same title to TITLE attribute as well.
        outline.setAttribute(ATTR_FEED_TEXT, getTitle() == null ? "" : getTitle());
        outline.setAttribute(ATTR_FEED_TITLE, outline.getAttributeValue(ATTR_FEED_TEXT));

        outline.setAttribute(ATTR_FEED_QUERY, query, bbns);
        writeCommon(outline, bbns, extendedExport);

        writeIfSet(outline, ATTR_FEED_DEDUP_ENABLED, bbns, dedupEnabled);
        writeIfSet(outline, ATTR_FEED_DEDUP_FROM, bbns, dedupFrom, -1);
        writeIfSet(outline, ATTR_FEED_DEDUP_TO, bbns, dedupTo, -1);
    }

    /**
     * Compares this object to the other.
     *
     * @param o other object to compare to.
     *
     * @return TRUE if objects are equal.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final SearchOPMLFeed searchOPMLFeed = (SearchOPMLFeed)o;

        if (dedupEnabled != searchOPMLFeed.dedupEnabled) return false;
        if (dedupFrom != searchOPMLFeed.dedupFrom) return false;
        if (dedupTo != searchOPMLFeed.dedupTo) return false;

        return !(query != null
            ? !query.equals(searchOPMLFeed.query) 
            : searchOPMLFeed.query != null);
    }

    /**
     * Returns the hash code of this object.
     *
     * @return hash code of this object.
     */
    public int hashCode()
    {
        int result = super.hashCode();
        result = 29 * result + (query != null ? query.hashCode() : 0);
        return result;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation of the object.
     */
    public String toString()
    {
        return super.toString() +
            ", query=" + query +
            ", dedupEnabled=" + dedupEnabled +
            ", dedupFrom=" + dedupFrom +
            ", dedupTo=" + dedupTo;
    }
}
