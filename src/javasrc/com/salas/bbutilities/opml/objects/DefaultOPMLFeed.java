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
// $Id: DefaultOPMLFeed.java,v 1.9 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Default implementation for any feed.
 */
public abstract class DefaultOPMLFeed implements FormatConstants
{
    private final String  title;
    private final int     rating;
    private final String  readArticlesKeys;
    private final String  pinnedArticlesKeys;
    private final int     limit;
    private final int     viewType;
    private final boolean viewModeEnabled;
    private final int     viewMode;
    private final Boolean ascendingSorting;
    private final int     handlingType;

    /**
     * Creates default feed.
     *
     * @param aTitle              feed title.
     * @param aLimit              articles limit.
     * @param aRating             rating.
     * @param aReadArticlesKeys   read articles' keys.
     * @param aPinnedArticlesKeys keys of pinned articles.
     * @param aViewType           view type.
     * @param aViewModeEnabled    <code>TRUE</code> if custom view mode is enabled.
     * @param aViewMode           custom view mode.
     * @param aAscendingSorting   ascending sorting override flag.
     * @param aHandlingType       handling type.
     */
    public DefaultOPMLFeed(String aTitle, int aLimit, int aRating, String aReadArticlesKeys,
                           String aPinnedArticlesKeys, int aViewType, boolean aViewModeEnabled,
                           int aViewMode, Boolean aAscendingSorting, int aHandlingType)
    {
        title               = aTitle;
        limit               = aLimit;
        rating              = aRating;
        readArticlesKeys    = aReadArticlesKeys;
        pinnedArticlesKeys  = aPinnedArticlesKeys;
        viewType            = aViewType;
        viewModeEnabled     = aViewModeEnabled;
        viewMode            = aViewMode;
        ascendingSorting    = aAscendingSorting;
        handlingType        = aHandlingType;
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
     * Returns rating of the feed.
     *
     * @return rating.
     */
    public int getRating()
    {
        return rating;
    }

    /**
     * Returns list of keys of read articles.
     *
     * @return list of keys.
     */
    public String getReadArticlesKeys()
    {
        return readArticlesKeys;
    }

    /**
     * Returns the list of keys of pinned articles.
     *
     * @return list of keys.
     */
    public String getPinnedArticlesKeys()
    {
        return pinnedArticlesKeys;
    }

    /**
     * Returns purge limit.
     *
     * @return purge limit.
     */
    public int getLimit()
    {
        return limit;
    }

    /**
     * Returns the type.
     *
     * @return type.
     */
    public int getViewType()
    {
        return viewType;
    }

    /**
     * Returns <code>TRUE</code> if custom view mode is enabled.
     *
     * @return <code>TRUE</code> if custom view mode is enabled.
     */
    public boolean isViewModeEnabled()
    {
        return viewModeEnabled;
    }

    /**
     * Returns custom view mode.
     *
     * @return custom view mode.
     */
    public int getViewMode()
    {
        return viewMode;
    }

    /**
     * returns ascending sorting flag.
     *
     * @return flag.
     */
    public Boolean getAscendingSorting()
    {
        return ascendingSorting;
    }

    /**
     * Returns the handling type.
     *
     * @return the handling type.
     */
    public int getHandlingType()
    {
        return handlingType;
    }
    
    /**
     * Writes data to outline.
     *
     * @param outline           outline.
     * @param bbns              BB namespace.
     * @param extendedExport    TRUE to write extended data.
     */
    public void write(Element outline, Namespace bbns, boolean extendedExport)
    {
        outline.setAttribute(ATTR_FEED_TYPE, TYPE_RSS);
        outline.setAttribute(ATTR_FEED_TEXT, title == null ? "" : title);

        if (extendedExport)
        {
            writeIfSet(outline, ATTR_FEED_READ_ARTICLES, bbns, readArticlesKeys);
            writeIfSet(outline, ATTR_FEED_PINNED_ARTICLES, bbns, pinnedArticlesKeys);
        }

        writeCommon(outline, bbns, extendedExport);
    }

    /**
     * Writes some common properties to outline. It's a part of {@link #write} method,
     * so don't bother calling it again.
     *
     * @param outline           outline.
     * @param bbns              BB namespace.
     * @param extendedExport    TRUE to write extended data.
     */
    protected void writeCommon(Element outline, Namespace bbns, boolean extendedExport)
    {
        writeIfSet(outline, ATTR_FEED_RATING, bbns, rating, -1);
        if (extendedExport)
        {
            writeIfSet(outline, ATTR_FEED_LIMIT, bbns,  limit, -1);
            writeIfSet(outline, ATTR_FEED_VIEW_TYPE, bbns, viewType, -1);
            writeIfSet(outline, ATTR_FEED_VIEW_MODE_ENABLED, bbns, viewModeEnabled);
            writeIfSet(outline, ATTR_FEED_VIEW_MODE, bbns, viewMode, -1);

            if (ascendingSorting != null)
            {
                outline.setAttribute(ATTR_FEED_ASCENDING_SORTING, ascendingSorting ? "true" : "false", bbns);
            }
            
            writeIfSet(outline, ATTR_FEED_HANDLING_TYPE, bbns, handlingType, -1);
        }
    }

    /**
     * Writes the attribute only if the value is different from unset value.
     *
     * @param aFeedOutline  outline to write to.
     * @param aName         name of attribute.
     * @param aBbNs         BB namespace.
     * @param aValue        value.
     * @param aUnsetValue   unset value.
     */
    protected static void writeIfSet(Element aFeedOutline, String aName, Namespace aBbNs,
        int aValue, int aUnsetValue)
    {
        if (aValue != aUnsetValue)
        {
            aFeedOutline.setAttribute(aName, Integer.toString(aValue), aBbNs);
        }
    }

    /**
     * Writes the attribute only if the value is different from unset value.
     *
     * @param aFeedOutline  outline to write to.
     * @param aName         name of attribute.
     * @param aBbNs         BB namespace.
     * @param aValue        value.
     */
    protected static void writeIfSet(Element aFeedOutline, String aName, Namespace aBbNs,
        boolean aValue)
    {
        if (aValue) aFeedOutline.setAttribute(aName, "true", aBbNs);
    }

    /**
     * Writes the attribute only if the value is different from unset value.
     *
     * @param aFeedOutline  outline to write to.
     * @param aName         name of attribute.
     * @param aBbNs         BB namespace.
     * @param aValue        value.
     */
    protected static void writeIfSet(Element aFeedOutline, String aName, Namespace aBbNs,
        String aValue)
    {
        if (aValue != null && aValue.trim().length() != 0)
            aFeedOutline.setAttribute(aName, aValue.trim(), aBbNs);
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

        final DefaultOPMLFeed defaultFeed = (DefaultOPMLFeed)o;

        if (limit != defaultFeed.limit) return false;
        if (rating != defaultFeed.rating) return false;
        if (readArticlesKeys != null ? !readArticlesKeys.equals(defaultFeed.readArticlesKeys)
            : defaultFeed.readArticlesKeys != null) return false;
        if (pinnedArticlesKeys != null ? !pinnedArticlesKeys.equals(defaultFeed.pinnedArticlesKeys)
            : defaultFeed.pinnedArticlesKeys != null) return false;
        if (viewType != defaultFeed.viewType) return false;
        if (viewModeEnabled != defaultFeed.viewModeEnabled) return false;
        if (viewMode != defaultFeed.viewMode) return false;

        if (ascendingSorting != null
            ? !ascendingSorting.equals(defaultFeed.ascendingSorting)
            : defaultFeed.ascendingSorting != null) return false;

        return title.equals(defaultFeed.title);
    }

    /**
     * Returns the hash code of this object.
     *
     * @return hash code of this object.
     */
    public int hashCode()
    {
        int result;
        result = title.hashCode();
        result = 29 * result + (readArticlesKeys != null ? readArticlesKeys.hashCode() : 0);
        return result;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation of the object.
     */
    public String toString()
    {
        return "Feed: title=" + title +
            ", rating=" + rating +
            ", limit=" + limit +
            ", readArticlesKeys='" + readArticlesKeys +
            "', pinnedArticlesKeys='" + pinnedArticlesKeys +
            "', viewType=" + viewType +
            ", viewModeEnabled=" + viewModeEnabled +
            ", viewMode=" + viewMode +
            ", ascendingSorting=" + ascendingSorting;
    }
}
