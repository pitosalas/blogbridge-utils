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
// $Id: QueryOPMLFeed.java,v 1.11 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Query Feed information holder.
 */
public class QueryOPMLFeed extends DataOPMLFeed
{
    private int     queryType;
    private String  queryParam;
    private String  xmlURL;

    private boolean dedupEnabled = false;
    private int     dedupFrom = -1;
    private int     dedupTo = -1;

    /**
     * Creates holder.
     *
     * @param aTitle            title of feed.
     * @param aQueryType        type of query.
     * @param aQueryParam       parameter for query.
     * @param aXmlURL           XML URL corresponding to this query.
     * @param aReadArticlesKeys comma-separated list of read articles' simple match keys.
     * @param aPinnedArticlesKeys keys of pinned articles.
     * @param aLimit            articles (purge) limit.
     * @param aRating           rating of the feed.
     * @param aViewType         view type.
     * @param aViewModeEnabled  custom view mode state.
     * @param aViewMode         custom view mode.
     * @param aAscendingSorting ascending sorting override flag.
     * @param aHandlingType     handling type.
     */
    public QueryOPMLFeed(String aTitle, int aQueryType, String aQueryParam, String aXmlURL,
        String aReadArticlesKeys, String aPinnedArticlesKeys, int aLimit, int aRating,
        int aViewType, boolean aViewModeEnabled, int aViewMode, Boolean aAscendingSorting,
        int aHandlingType)
    {
        super(aTitle, aLimit, aRating, aReadArticlesKeys, aPinnedArticlesKeys,
            aViewType, aViewModeEnabled, aViewMode, aAscendingSorting, aHandlingType);

        queryType = aQueryType;
        queryParam = aQueryParam;
        xmlURL = aXmlURL;
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
     * Returns the feed query type.
     *
     * @return query type.
     */
    public int getQueryType()
    {
        return queryType;
    }

    /**
     * Returns the parameter.
     *
     * @return parameter.
     */
    public String getQueryParam()
    {
        return queryParam;
    }

    /**
     * Returns XML URL corresponding to this query.
     *
     * @return XML URL corresponding to this query.
     */
    public String getXmlURL()
    {
        return xmlURL;
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
        super.write(outline, bbns, extendedExport);

        // It's better to have TEXT attribute only, but we maintain backward compatibility
        // with versions up to 2.3 by saving the same title to TITLE attribute as well.
        outline.setAttribute(ATTR_FEED_TITLE, outline.getAttributeValue(ATTR_FEED_TEXT));

        outline.setAttribute(ATTR_FEED_QUERY_TYPE, Integer.toString(queryType), bbns);
        outline.setAttribute(ATTR_FEED_QUERY_PARAM, queryParam, bbns);
        writeIfSet(outline, ATTR_FEED_XML_URL, null, xmlURL);

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

        final QueryOPMLFeed queryFeed = (QueryOPMLFeed)o;

        if (queryType != queryFeed.queryType) return false;

        if (queryParam != null
            ? !queryParam.equals(queryFeed.queryParam)
            : queryFeed.queryParam != null) return false;

        if (dedupEnabled != queryFeed.dedupEnabled) return false;
        if (dedupFrom != queryFeed.dedupFrom) return false;
        if (dedupTo != queryFeed.dedupTo) return false;

        return !(xmlURL != null
            ? !xmlURL.equals(queryFeed.xmlURL)
            : queryFeed.xmlURL != null);
    }

    /**
     * Returns the hash code of this object.
     *
     * @return hash code of this object.
     */
    public int hashCode()
    {
        int result = super.hashCode();
        result = 29 * result + queryType;
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
            ", queryType=" + queryType +
            ", queryParam=" + queryParam +
            ", dedupEnabled=" + dedupEnabled +
            ", dedupFrom=" + dedupFrom +
            ", dedupTo=" + dedupTo;
    }
}
