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
// $Id: FormatConstants.java,v 1.18 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Namespace;

/**
 * Names of all tags and attributes in OPML resources.
 */
public interface FormatConstants
{
    String TYPE_RSS                     = "rss";

    String TAG_BODY                     = "body";
    String TAG_ROOT                     = "opml";
    String TAG_HEAD                     = "head";
    String TAG_HEAD_TITLE               = "title";
    String TAG_HEAD_DATE_MODIFIED       = "dateModified";
    String TAG_OUTLINE                  = "outline";

    String ATTR_OPML_VERSION            = "version";

    // Basic feed
    String ATTR_FEED_TYPE               = "type";
    String ATTR_FEED_TITLE              = "title";
    String ATTR_FEED_TEXT               = "text";
    String ATTR_FEED_RATING             = "rating";
    String ATTR_FEED_LIMIT              = "limit";
    String ATTR_FEED_READ_ARTICLES      = "readArticles";
    String ATTR_FEED_PINNED_ARTICLES    = "pinnedArticles";
    String ATTR_FEED_XML_URL            = "xmlUrl";
    String ATTR_FEED_VIEW_TYPE          = "viewtype";
    String ATTR_FEED_VIEW_MODE_ENABLED  = "viewModeEnabled";
    String ATTR_FEED_VIEW_MODE          = "viewMode";
    String ATTR_FEED_DEDUP_ENABLED      = "dedupEnabled";
    String ATTR_FEED_DEDUP_FROM         = "dedupFrom";
    String ATTR_FEED_DEDUP_TO           = "dedupTo";
    String ATTR_FEED_ASCENDING_SORTING  = "ascendingSorting";
    String ATTR_FEED_UPDATE_PERIOD      = "updatePeriod";

    // Direct feed
    String ATTR_FEED_HTML_URL           = "htmlUrl";
    String ATTR_FEED_CUSTOM_TITLE       = "customTitle";
    String ATTR_FEED_CUSTOM_CREATOR     = "customCreator";
    String ATTR_FEED_CUSTOM_DESCRIPTION = "customDescription";

    // Query feed
    String ATTR_FEED_QUERY_TYPE         = "queryType";
    String ATTR_FEED_QUERY_PARAM        = "queryParam";

    // Search feed
    String ATTR_FEED_QUERY              = "query";

    // Reading list
    String ATTR_READING_LIST_TITLE      = "text";
    String ATTR_READING_LIST_URL        = "xmlUrl";
    String DATE_FORMAT                  = "EEE, d MMM yyyy HH:mm:ss z";

    // Guide
    String ATTR_GUIDE_TITLE             = "text";
    String ATTR_GUIDE_ICON              = "icon";
    String ATTR_GUIDE_PUB_ENABLED       = "pubEnabled";
    String ATTR_GUIDE_PUB_TITLE         = "pubTitle";
    String ATTR_GUIDE_PUB_TAGS          = "pubTags";
    String ATTR_GUIDE_PUB_PUBLIC        = "pubPublic";
    String ATTR_GUIDE_PUB_RATING        = "pubRating";
    String ATTR_GUIDE_AUTO_FEEDS_DISCOVERY = "autoFeedsDiscovery";
    String ATTR_GUIDE_NOTIFICATIONS_ALLOWED = "notificationsAllowed";

    // Tags
    String ATTR_TAGS                    = "tags";
    String ATTR_TAGS_DESCRIPTION        = "tagsDescription";
    String ATTR_TAGS_EXTENDED           = "tagsExtended";

    // Disabled flag
    String ATTR_DISABLED                = "disabled";

    /** Type of outline. */
    String TYPE_SEARCH                  = "search";

    /** Private BB namespace prefix. */
    String BB_NS_PREFIX                 = "bb";
    /** Private BB namespace URI. */
    String BB_NS_URI                    = "http://blogbridge.com/ns/2006/opml";
    /** Private BB namespace. */
    Namespace BB_NAMESPACE              = Namespace.getNamespace(BB_NS_PREFIX, BB_NS_URI);
}
