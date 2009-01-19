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
// $Id: DirectOPMLFeed.java,v 1.10 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Direct feed information holder.
 */
public class DirectOPMLFeed extends DataOPMLFeed
{
    private final String xmlURL;
    private final String htmlURL;

    private final String customTitle;
    private final String customCreator;
    private final String customDescription;

    private final String tags;
    private final String tagsDescription;
    private final String tagsExtended;

    private final boolean disabled;

    /**
     * Creates new feed.
     *
     * @param aTitle             title.
     * @param aXmlURL            URL to XML resource.
     * @param aHtmlURL           URL to HTML page.
     * @param aRating            rating of the feed.
     * @param aReadArticlesKeys  comma-delimetered list of articles keys.
     * @param aPinnedArticlesKeys keys of pinned articles.
     * @param aLimit             limit.
     * @param aCustomTitle       custom title.
     * @param aCustomCreator     custom creator.
     * @param aCustomDescription custom description.
     * @param aTags              list of tags.
     * @param aTagsDescription   tags description.
     * @param aTagsExtended      tags extended description.
     * @param aDisabled          <code>TRUE</code> when feed is disabled.
     * @param aViewType          view type.
     * @param aViewModeEnabled   custom view mode state.
     * @param aViewMode          custom view mode.
     * @param aAscendingSorting  ascending sorting override flag.
     */
    public DirectOPMLFeed(String aTitle, String aXmlURL, String aHtmlURL, int aRating,
                          String aReadArticlesKeys, String aPinnedArticlesKeys, int aLimit,
                          String aCustomTitle, String aCustomCreator, String aCustomDescription,
                          String aTags, String aTagsDescription, String aTagsExtended,
                          boolean aDisabled, int aViewType, boolean aViewModeEnabled, int aViewMode,
                          Boolean aAscendingSorting
    )
    {
        super(aTitle, aLimit, aRating, aReadArticlesKeys, aPinnedArticlesKeys, aViewType, aViewModeEnabled, aViewMode,
            aAscendingSorting);

        xmlURL = aXmlURL;
        htmlURL = aHtmlURL;
        customTitle = aCustomTitle;
        customCreator = aCustomCreator;
        customDescription = aCustomDescription;

        tags = aTags;
        tagsDescription = aTagsDescription;
        tagsExtended = aTagsExtended;

        disabled = aDisabled;
    }

    /**
     * Returns URL to XML resource.
     *
     * @return URL.
     */
    public String getXmlURL()
    {
        return xmlURL;
    }

    /**
     * Returns URL to HTML page.
     *
     * @return URL.
     */
    public String getHtmlURL()
    {
        return htmlURL;
    }

    /**
     * Returns custom title.
     *
     * @return custom title.
     */
    public String getCustomTitle()
    {
        return customTitle;
    }

    /**
     * Returns custom creator.
     *
     * @return custom creator.
     */
    public String getCustomCreator()
    {
        return customCreator;
    }

    /**
     * Returns custom description.
     *
     * @return custom description.
     */
    public String getCustomDescription()
    {
        return customDescription;
    }

    /**
     * Returns the list of user tags.
     *
     * @return user tags or <code>NULL</code> if not set.
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Returns tags description.
     *
     * @return tags description or <code>NULL</code> if not set.
     */
    public String getTagsDescription()
    {
        return tagsDescription;
    }

    /**
     * Returns extended tags description.
     *
     * @return extended tags description.
     */
    public String getTagsExtended()
    {
        return tagsExtended;
    }

    /**
     * Returns feed disableness state.
     *
     * @return state.
     */
    public boolean isDisabled()
    {
        return disabled;
    }

    /**
     * Writes data to outline.
     *
     * @param outline        outline.
     * @param bbns           BB namespace;
     * @param extendedExport TRUE to write extended data.
     */
    public void write(Element outline, Namespace bbns, boolean extendedExport)
    {
        super.write(outline, bbns, extendedExport);

        outline.setAttribute(ATTR_FEED_XML_URL, xmlURL);
        writeIfSet(outline, ATTR_FEED_HTML_URL, null, htmlURL);

        writeIfSet(outline, ATTR_FEED_CUSTOM_TITLE, bbns, customTitle);
        writeIfSet(outline, ATTR_FEED_CUSTOM_CREATOR, bbns, customCreator);
        writeIfSet(outline, ATTR_FEED_CUSTOM_DESCRIPTION, bbns, customDescription);

        writeIfSet(outline, ATTR_TAGS, bbns, tags);
        writeIfSet(outline, ATTR_TAGS_DESCRIPTION, bbns, tagsDescription);
        writeIfSet(outline, ATTR_TAGS_EXTENDED, bbns, tagsExtended);

        writeIfSet(outline, ATTR_DISABLED, bbns, extendedExport && disabled);
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

        final DirectOPMLFeed directFeed = (DirectOPMLFeed)o;

        if (customCreator != null
            ? !customCreator.equals(directFeed.customCreator)
            : directFeed.customCreator != null) return false;

        if (customDescription != null
            ? !customDescription.equals(directFeed.customDescription)
            : directFeed.customDescription != null) return false;

        if (customTitle != null
            ? !customTitle.equals(directFeed.customTitle)
            : directFeed.customTitle != null) return false;

        if (htmlURL != null
            ? !htmlURL.equals(directFeed.htmlURL)
            : directFeed.htmlURL != null) return false;

        if (xmlURL != null
            ? !xmlURL.equals(directFeed.xmlURL)
            : directFeed.xmlURL != null) return false;

        if (tags != null
            ? !tags.equals(directFeed.tags)
            : directFeed.tags != null) return false;

        return !(tagsDescription != null
            ? !tagsDescription.equals(directFeed.tagsDescription)
            : directFeed.tagsDescription != null);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation of the object.
     */
    public String toString()
    {
        return super.toString() + ", xmlURL=" + xmlURL + ", htmlURL=" + htmlURL +
            ", customCreator=" + customCreator + ", customDescription=" + customDescription +
            ", customTitle=" + customTitle + ", userTags=" + tags +
            ", tagsDescription=" + tagsDescription + ", disabled=" + disabled;
    }
}
