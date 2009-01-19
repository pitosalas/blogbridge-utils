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
// $Id: ExporterOld.java,v 1.2 2006/02/28 10:42:22 spyromus Exp $
//

package com.salas.bbutilities.opml.export;

import com.salas.bbutilities.opml.objects.*;
import org.jdom.Element;

import java.util.List;

/**
 * Exporter of information into OPML format.
 */
public class ExporterOld extends AbstractExporter
{
    /**
     * Creates exporter.
     *
     * @param aExtendedExport exporter.
     */
    public ExporterOld(boolean aExtendedExport)
    {
        super(aExtendedExport);
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

        outline.setAttribute("text", guide.getTitle());
        writeIfNotNull(outline, "icon", guide.getIcon());

        if (extendedExport)
        {
            if (guide.isPublishingEnabled()) outline.setAttribute("pubEnabled", "true");
            writeIfNotNull(outline, "pubTitle", guide.getPublishingTitle());
            writeIfNotNull(outline, "pubTags", guide.getPublishingTags());
        }

        // Write reading lists
        OPMLReadingList[] readingLists = guide.getReadingLists();
        for (int i = 0; i < readingLists.length; i++)
        {
            OPMLReadingList list = readingLists[i];
            Element listOutline = new Element("outline");
            outline.addContent(listOutline);

            write(listOutline, list);
        }

        // Write feeds
        List feedsArray = guide.getFeeds();
        for (int i = 0; i < feedsArray.size(); i++)
        {
            DefaultOPMLFeed defaultFeed = (DefaultOPMLFeed)feedsArray.get(i);
            Element feedOutline = new Element("outline");
            outline.addContent(feedOutline);

            write(feedOutline, defaultFeed);
        }
    }

    /**
     * Writes list data to the outline.
     *
     * @param outline   outline to write list data to.
     * @param list      reading list.
     */
    public void write(Element outline, OPMLReadingList list)
    {
        outline.setAttribute(ATTR_FEED_TYPE, "list");
        outline.setAttribute(ATTR_READING_LIST_TITLE, list.getTitle());
        outline.setAttribute(ATTR_READING_LIST_URL, list.getURL());

        List feeds = list.getFeeds();
        for (int i = 0; i < feeds.size(); i++)
        {
            DefaultOPMLFeed defaultFeed = (DefaultOPMLFeed)feeds.get(i);
            Element feedOutline = new Element("outline");
            outline.addContent(feedOutline);

            write(feedOutline, defaultFeed);
        }
    }

    /**
     * Writes data to outline.
     *
     * @param outline   outline.
     * @param feed      feed.
     */
    public void write(Element outline, DirectOPMLFeed feed)
    {
        writeDefault(outline, feed);

        outline.setAttribute(ATTR_FEED_XML_URL, feed.getXmlURL());
        writeIfNotNull(outline, ATTR_FEED_HTML_URL, feed.getHtmlURL());

        writeIfNotNull(outline, ATTR_FEED_CUSTOM_TITLE, feed.getCustomTitle());
        writeIfNotNull(outline, ATTR_FEED_CUSTOM_CREATOR, feed.getCustomCreator());
        writeIfNotNull(outline, ATTR_FEED_CUSTOM_DESCRIPTION, feed.getCustomDescription());

        writeIfNotNull(outline, ATTR_TAGS, feed.getTags());
        writeIfNotNull(outline, ATTR_TAGS_DESCRIPTION, feed.getTagsDescription());
        writeIfNotNull(outline, ATTR_TAGS_EXTENDED, feed.getTagsExtended());

        if (extendedExport && feed.isDisabled()) outline.setAttribute(ATTR_DISABLED, "true");
    }

    /**
     * Writes data to outline.
     *
     * @param outline   outline.
     * @param feed      feed.
     */
    public void write(Element outline, QueryOPMLFeed feed)
    {
        writeDefault(outline, feed);

        // It's better to have TEXT attribute only, but we maintain backward compatibility
        // with versions up to 2.3 by saving the same title to TITLE attribute as well.
        outline.setAttribute(ATTR_FEED_TITLE, outline.getAttributeValue(ATTR_FEED_TEXT));

        outline.setAttribute(ATTR_FEED_QUERY_TYPE, Integer.toString(feed.getQueryType()));
        outline.setAttribute(ATTR_FEED_QUERY_PARAM, feed.getQueryParam());
        writeIfNotNull(outline, ATTR_FEED_XML_URL, feed.getXmlURL());
    }

    /**
     * Writes data to outline.
     *
     * @param outline   outline.
     * @param feed      search feed.
     */
    public void write(Element outline, SearchOPMLFeed feed)
    {
        outline.setAttribute(ATTR_FEED_TYPE, TYPE_SEARCH);

        // It's better to have TEXT attribute only, but we maintain backward compatibility
        // with versions up to 2.3 by saving the same title to TITLE attribute as well.
        outline.setAttribute(ATTR_FEED_TEXT, feed.getTitle() == null ? "" : feed.getTitle());
        outline.setAttribute(ATTR_FEED_TITLE, outline.getAttributeValue(ATTR_FEED_TEXT));

        outline.setAttribute(ATTR_FEED_QUERY, feed.getQuery());
        writeIfSet(outline, ATTR_FEED_RATING, feed.getRating(), -1);
        writeIfSet(outline, ATTR_FEED_LIMIT, feed.getLimit(), -1);
    }

    /**
     * Writes default feed. Should never be called because of the overrides.
     *
     * @param outline   outline.
     * @param feed      feed.
     */
    public void write(Element outline, DefaultOPMLFeed feed)
    {
        if (feed instanceof DirectOPMLFeed)
        {
            write(outline, (DirectOPMLFeed)feed);
        } else if (feed instanceof SearchOPMLFeed)
        {
            write(outline, (SearchOPMLFeed)feed);
        } else if (feed instanceof QueryOPMLFeed)
        {
            write(outline, (QueryOPMLFeed)feed);
        }
    }

    /**
     * Writes data to outline.
     *
     * @param outline   outline.
     * @param feed      default feed.
     */
    public void writeDefault(Element outline, DefaultOPMLFeed feed)
    {
        outline.setAttribute(ATTR_FEED_TYPE, TYPE_RSS);
        outline.setAttribute(ATTR_FEED_TEXT, feed.getTitle() == null ? "" : feed.getTitle());
        writeIfSet(outline, ATTR_FEED_RATING, feed.getRating(), -1);

        if (extendedExport)
        {
            if (isNotEmpty(feed.getReadArticlesKeys()))
            {
                outline.setAttribute(ATTR_FEED_READ_ARTICLES, feed.getReadArticlesKeys());
            }

            writeIfSet(outline, ATTR_FEED_LIMIT, feed.getLimit(), -1);
            if (feed.getViewType() != -1)
            {
                outline.setAttribute(ATTR_FEED_VIEW_TYPE, Integer.toString(feed.getViewType()));
            }
        }
    }

    /**
     * Returns <code>TRUE</code> if the string is not empty.
     *
     * @param str string.
     *
     * @return <code>TRUE</code> if the string is not empty.
     */
    private static boolean isNotEmpty(String str)
    {
        return str != null && str.trim().length() > 0;
    }

    /**
     * Writes the attribute only if the value is different from unset value.
     *
     * @param aFeedOutline  outline to write to.
     * @param aName         name of attribute.
     * @param aValue        value.
     * @param aUnsetValue   unset value.
     */
    protected static void writeIfSet(Element aFeedOutline, String aName, int aValue,
        int aUnsetValue)
    {
        if (aValue != aUnsetValue)
        {
            aFeedOutline.setAttribute(aName, Integer.toString(aValue));
        }
    }

    /**
     * Writes attribute value if the value is not <code>NULL</code>.
     *
     * @param outline   outline element.
     * @param attr      attribute name.
     * @param value     attribute value.
     */
    private static void writeIfNotNull(Element outline, String attr, String value)
    {
        if (value != null) outline.setAttribute(attr, value);
    }
}
