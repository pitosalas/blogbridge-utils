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
// $Id: OPMLGuide.java,v 1.13 2008/03/17 13:02:52 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * Imported guide.
 */
public class OPMLGuide implements FormatConstants
{
    private final String title;
    private final String icon;
    private final List<OPMLReadingList> readingLists;

    private final boolean publishingEnabled;
    private final String publishingTitle;
    private final String publishingTags;
    private final boolean publishingPublic;
    private final int publishingRating;
    private final boolean notificationsAllowed;

    private final boolean autoFeedsDiscovery;

    private List<DefaultOPMLFeed> feedsArray;

    /**
     * Creates new guide.
     *
     * @param aTitle                title.
     * @param anIcon                icon.
     * @param aPublishingEnabled    publishing enableness flag.
     * @param aPublishingTitle      publishing title.
     * @param aPublishingTags       publishing tags.
     * @param aPublishingPublic     TRUE to do public publishing.
     * @param aPublishingRating     minimum rating for feed to be published.
     * @param aAutoFeedsDiscovery   automatic feeds discovery flag.
     * @param aNotificationsAllowed TRUE to allow notifications of events in this guide.
     */
    public OPMLGuide(String aTitle, String anIcon, boolean aPublishingEnabled,
                     String aPublishingTitle, String aPublishingTags,
                     boolean aPublishingPublic, int aPublishingRating,
                     boolean aAutoFeedsDiscovery, boolean aNotificationsAllowed)
    {
        publishingEnabled = aPublishingEnabled;
        publishingTags = aPublishingTags;
        publishingTitle = aPublishingTitle;
        publishingPublic = aPublishingPublic;
        publishingRating = aPublishingRating;
        notificationsAllowed = aNotificationsAllowed;

        title = aTitle;
        icon = anIcon;

        autoFeedsDiscovery = aAutoFeedsDiscovery;

        feedsArray = new ArrayList<DefaultOPMLFeed>(0);
        readingLists = new ArrayList<OPMLReadingList>(0);
    }

    /**
     * Sets the list of feeds all-at-once.
     *
     * @param feeds list of feeds.
     */
    public void setFeeds(ArrayList<DefaultOPMLFeed> feeds)
    {
        if (feeds != null)
        {
            feedsArray = feeds;
        } else
        {
            feedsArray = new ArrayList<DefaultOPMLFeed>(0);
        }
    }

    /**
     * Returns title of guide.
     *
     * @return title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Returns list of feeds.
     *
     * @return list of feeds.
     */
    public List<DefaultOPMLFeed> getFeeds()
    {
        return feedsArray;
    }

    /**
     * Returns associated icon key.
     *
     * @return key.
     */
    public String getIcon()
    {
        return icon;
    }

    /**
     * Returns <code>TRUE</code> if publishing is enabled.
     *
     * @return <code>TRUE</code> if publishing is enabled.
     */
    public boolean isPublishingEnabled()
    {
        return publishingEnabled;
    }

    /**
     * Returns <code>TRUE</code> if publishing is set to public.
     *
     * @return <code>TRUE</code> if publishing is set to public.
     */
    public boolean isPublishingPublic()
    {
        return publishingPublic;
    }

    /**
     * Returns the publishing title.
     *
     * @return title.
     */
    public String getPublishingTitle()
    {
        return publishingTitle;
    }

    /**
     * Returns the publishing tags.
     *
     * @return tags.
     */
    public String getPublishingTags()
    {
        return publishingTags;
    }

    /**
     * Returns minimum rating for a feed to be published.
     *
     * @return minimum rating.
     */
    public int getPublishingRating()
    {
        return publishingRating;
    }

    /**
     * Returns <code>TRUE</code> if automatic feeds discovery is enabled for this guide.
     *
     * @return <code>TRUE</code> if automatic feeds discovery is enabled for this guide.
     */
    public boolean isAutoFeedsDiscovery()
    {
        return autoFeedsDiscovery;
    }

    /**
     * Returns <code>TRUE</code> if notifications are allowed in this guide.
     *
     * @return <code>TRUE</code> if notifications are allowed in this guide.
     */
    public boolean isNotificationsAllowed()
    {
        return notificationsAllowed;
    }

    /**
     * Adds reading list.
     *
     * @param aList reading list.
     */
    public void add(OPMLReadingList aList)
    {
        readingLists.add(aList);
    }

    /**
     * Returns reading lists assigned to this guide.
     *
     * @return reading lists.
     */
    public OPMLReadingList[] getReadingLists()
    {
        return readingLists.toArray(new OPMLReadingList[readingLists.size()]);
    }

    /**
     * Writes guide data to the outline.
     *
     * @param outline           outline to write guide data to.
     * @param bbns              BB namespace.
     * @param extendedExport    <code>TRUE</code> to write extended information.
     */
    public void write(Element outline, Namespace bbns, boolean extendedExport)
    {
        outline.setAttribute(ATTR_GUIDE_TITLE, title);
        if (icon != null) outline.setAttribute(ATTR_GUIDE_ICON, icon, bbns);

        if (extendedExport)
        {
            if (publishingEnabled) outline.setAttribute(ATTR_GUIDE_PUB_ENABLED, "true", bbns);
            if (publishingTitle != null) outline.setAttribute(ATTR_GUIDE_PUB_TITLE, publishingTitle, bbns);
            if (publishingTags != null) outline.setAttribute(ATTR_GUIDE_PUB_TAGS, publishingTags, bbns);
            if (publishingPublic) outline.setAttribute(ATTR_GUIDE_PUB_PUBLIC, "true", bbns);
            if (notificationsAllowed) outline.setAttribute(ATTR_GUIDE_NOTIFICATIONS_ALLOWED, "true", bbns);
            if (autoFeedsDiscovery) outline.setAttribute(ATTR_GUIDE_AUTO_FEEDS_DISCOVERY, "true", bbns);

            outline.setAttribute(ATTR_GUIDE_PUB_RATING, Integer.toString(publishingRating), bbns);
        }

        for (OPMLReadingList list : readingLists)
        {
            Element listOutline = new Element("outline");
            outline.addContent(listOutline);
            list.write(listOutline, bbns, extendedExport);
        }

        for (DefaultOPMLFeed feed : feedsArray)
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

        final OPMLGuide opmlGuide = (OPMLGuide)o;

        if (notificationsAllowed != opmlGuide.notificationsAllowed) return false;
        if (autoFeedsDiscovery != opmlGuide.autoFeedsDiscovery) return false;
        if (publishingEnabled != opmlGuide.publishingEnabled) return false;
        if (publishingPublic != opmlGuide.publishingPublic) return false;
        if (publishingRating != opmlGuide.publishingRating) return false;
        if (!icon.equals(opmlGuide.icon)) return false;
        if (publishingTags != null ? !publishingTags.equals(opmlGuide.publishingTags) : opmlGuide.publishingTags != null)
            return false;
        if (publishingTitle != null ? !publishingTitle.equals(opmlGuide.publishingTitle) : opmlGuide.publishingTitle != null)
            return false;
        if (!title.equals(opmlGuide.title)) return false;

        return true;
    }

    /**
     * Returns the hash code of this object.
     *
     * @return hash code of this object.
     */
    public int hashCode()
    {
        return 29 * title.hashCode() + icon.hashCode();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return string representation of the object.
     */
    public String toString()
    {
        return "Guide: title=" + title +
            ", icon=" + icon +
            ", autoFeedsDiscovery=" + autoFeedsDiscovery +
            ", notificationsAllowed=" + notificationsAllowed +
            ", feeds=" + feedsArray.size() +
            ", publishingEnabled=" + publishingEnabled +
            ", publishingTitle=" + publishingTitle +
            ", publishingTags=" + publishingTags +
            ", publishingPublic=" + publishingPublic +
            ", readingLists=" + readingLists.size();
    }
}
