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
// $Id: Importer.java,v 1.34 2007/10/11 09:50:58 spyromus Exp $
//

package com.salas.bbutilities.opml;

import com.salas.bbutilities.NetUtils;
import com.salas.bbutilities.opml.objects.*;
import com.salas.bbutilities.opml.utils.EmptyEntityResolver;
import com.salas.bbutilities.opml.utils.Transformation;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Importer of OPML resources.
 */
public class Importer implements FormatConstants
{
    private static final Logger LOG = Logger.getLogger(Importer.class.getName());

    private static final int MAX_NESTING_LEVEL  = 2;

    static final int OUTLINE_TYPE_INVALID       = -1;
    static final int OUTLINE_TYPE_GUIDE         = 0;
    static final int OUTLINE_TYPE_RSS_LINK      = 1;
    static final int OUTLINE_TYPE_GUIDE_LINK    = 2;
    static final int OUTLINE_TYPE_QUERY_FEED    = 3;
    static final int OUTLINE_TYPE_SEARCH_FEED   = 4;
    static final int OUTLINE_TYPE_READING_LIST  = 5;

    private int currentNestingLevel;

    private boolean allowEmptyGuides = false;

    /** Private BB namespace. */
    private Namespace bbns;

    /**
     * Creates importer.
     */
    public Importer()
    {
        currentNestingLevel = 0;
    }

    /**
     * Sets the namespace.
     *
     * @param ns BB namespace.
     */
    void setBbNs(Namespace ns)
    {
        bbns = ns;
    }

    /**
     * Sets the state of flag showing if empty guides should be added to
     * the list on import.
     *
     * @param value TRUE to add empty guides.
     */
    public void setAllowEmptyGuides(boolean value)
    {
        this.allowEmptyGuides = value;
    }

    /**
     * Process resource at the specified URL.
     *
     * @param urlString         URL in string representation.
     * @param isSingleGuideMode TRUE to process in single-guide mode.
     *
     * @return the set with the list of guides taken from the resource and misc attributes.
     *         In the single-guide mode it will contain only one guide.
     *
     * @throws ImporterException in case of different errors.
     */
    public OPMLGuideSet process(String urlString, boolean isSingleGuideMode)
            throws ImporterException
    {
        final URL url;
        try
        {
            url = new URL(urlString);
        } catch (MalformedURLException e)
        {
            throw ImporterException.malformedUrl(e.getMessage());
        }

        return process(url, isSingleGuideMode);
    }

    /**
     * Process resource at the specified URL.
     *
     * @param url               URL of resource.
     * @param isSingleGuideMode TRUE to process in single-guide mode.
     *
     * @return the set with the list of guides taken from the resource and misc attributes.
     *         In the single-guide mode it will contain only one guide.
     *
     * @throws ImporterException in case of different errors.
     */
    public OPMLGuideSet process(URL url, boolean isSingleGuideMode)
            throws ImporterException
    {
        OPMLGuideSet guideSet;

        currentNestingLevel++;
        try
        {
            if (currentNestingLevel <= MAX_NESTING_LEVEL)
            {
                SAXBuilder builder = new SAXBuilder(false);

                // Turn off DTD loading
                builder.setEntityResolver(EmptyEntityResolver.INSTANCE);

                Document doc;
                try
                {
                    doc = builder.build(createReaderForURL(url));
                } catch (JDOMException e)
                {
                    throw ImporterException.parsing(e.getMessage());
                } catch (IOException e)
                {
                    throw ImporterException.io(e);
                }

                preprocessDocument(doc);

                final Element root = doc.getRootElement();
                validateFormat(root);

                // Lookup namespace (if NS isn't defined -- old format)
                setBbNs(root.getNamespace(FormatConstants.BB_NS_PREFIX));

                if (isSingleGuideMode)
                {
                    guideSet = processSingle(root);
                } else guideSet = processMultiple(root);
            } else
            {
                guideSet = new OPMLGuideSet(null, new OPMLGuide[0], null);
            }
        } finally
        {
            currentNestingLevel--;
        }

        return guideSet;
    }

    /**
     * Pre-processes the document before analyzing to straighten some
     * minor format issues from known providers.
     *
     * @param aDoc document which is just created from resource and
     *             will be parsed after this call finish. 
     */
    protected void preprocessDocument(Document aDoc)
    {
    }

    /**
     * Creates reader to use for reading data from stream. <i>Important note: this
     * method is intentionally made public and non-static to allow subclasses override
     * it in order to provide alternative readers.</i>
     *
     * @param url URL to read data from.
     *
     * @return reader object.
     *
     * @throws IOException if opening of stream for given URL fails.
     */
    public Reader createReaderForURL(URL url)
        throws IOException
    {
        return new InputStreamReader(url.openStream());
    }

    /**
     * Process OPML XML specified as string.
     *
     * @param opml              OPML resource.
     * @param isSingleGuideMode TRUE to process in single-guide mode.
     *
     * @return the set with the list of guides taken from the resource and misc attributes.
     *         In the single-guide mode it will contain only one guide.
     *
     * @throws ImporterException in case of any errors.
     */
    public OPMLGuideSet processFromString(String opml, boolean isSingleGuideMode)
            throws ImporterException
    {
        SAXBuilder builder = new SAXBuilder(false);

        // Turn off DTD loading
        builder.setEntityResolver(EmptyEntityResolver.INSTANCE);

        Document doc;
        try
        {
            doc = builder.build(new StringReader(opml));
        } catch (JDOMException e)
        {
            throw ImporterException.parsing(e.getMessage());
        } catch (IOException e)
        {
            throw ImporterException.io(e);
        }

        final Element root = doc.getRootElement();
        validateFormat(root);

        // Lookup namespace (if NS isn't defined -- old format)
        setBbNs(root.getNamespace(FormatConstants.BB_NS_PREFIX));

        return isSingleGuideMode
                ? processSingle(root)
                : processMultiple(root);
    }

    /**
     * Checks if we have OPML document and this document of supported version.
     *
     * @param root root element of the document.
     *
     * @throws ImporterException when something wrong with this document.
     */
    private static void validateFormat(final Element root)
            throws ImporterException
    {
        final String rootName = root.getName().toLowerCase();
        if (!rootName.equals("opml"))
        {
            throw ImporterException.parsing("Not an OPML resource.");
        }

        // head/title and body elements should be present
        final Element body = root.getChild("body");
        if (body == null)
        {
            throw ImporterException.parsing("Incorrect format.");
        }
    }

    /**
     * Proceses document and returns single guide which should be imported.
     *
     * @param root root element.
     *
     * @return imported guide with feeds.
     */
    private OPMLGuideSet processSingle(Element root)
    {
        return convertToSingle(processMultiple(root));
    }

    /**
     * Converts guides set with multiple guides to the guide with one.
     *
     * @param set   original set.
     *
     * @return the converted set.
     */
    static OPMLGuideSet convertToSingle(OPMLGuideSet set)
    {
        if (set == null) return null;

        OPMLGuide opmlGuide = convertToSingle(set.getGuides());
        OPMLGuide[] guides = opmlGuide == null ? new OPMLGuide[0] : new OPMLGuide[] { opmlGuide };

        return new OPMLGuideSet(set.getTitle(), guides, set.getDateModified());
    }

    /**
     * Converts list of guides into single guide by merging all feeds.
     *
     * @param guides list of guides.
     *
     * @return single guide.
     */
    static OPMLGuide convertToSingle(OPMLGuide[] guides)
    {
        OPMLGuide guide = null;

        if (guides.length > 1)
        {
            // When we have two guides with first having 0 feeds then
            // most probably we have our own export. We should take the second, otherwise
            // perform full process.
            if (guides.length == 2 && guides[0].getFeeds().size() == 0)
            {
                guide = guides[1];
            } else
            {
                guide = guides[0];

                ArrayList<DefaultOPMLFeed> feedsA = new ArrayList<DefaultOPMLFeed>();
                for (OPMLGuide guide1 : guides) feedsA.addAll(guide1.getFeeds());

                guide.setFeeds(feedsA);
            }
        } else if (guides.length > 0)
        {
            guide = guides[0];
        }

        return guide;
    }

    /**
     * Returns query feed created from outline.
     *
     * @param outline outline element.
     * @param bbns    BB namespace.
     *
     * @return feed.
     */
    static QueryOPMLFeed createQueryFeed(Element outline, Namespace bbns)
    {
        String title = fetchTitle(outline);
        String readArticles = getAttributeValue(outline, ATTR_FEED_READ_ARTICLES, bbns);
        String pinnedArticles = getAttributeValue(outline, ATTR_FEED_PINNED_ARTICLES, bbns);
        String parameter = getAttributeValue(outline, ATTR_FEED_QUERY_PARAM, bbns);
        if (parameter == null) parameter = getAttributeValue(outline, "keywords", bbns);
        String xmlURL = getAttributeValue(outline, ATTR_FEED_XML_URL, null);

        int queryType = getIntAttributeValue(outline, ATTR_FEED_QUERY_TYPE, bbns, -1);
        int purgeLimit = getIntAttributeValue(outline, ATTR_FEED_LIMIT, bbns, -1);
        int rating = getIntAttributeValue(outline, ATTR_FEED_RATING, bbns, -1);

        int viewType = getIntAttributeValue(outline, ATTR_FEED_VIEW_TYPE, bbns, -1);
        boolean viewModeEnabled = getBooleanAttributeValue(outline, ATTR_FEED_VIEW_MODE_ENABLED, bbns, false);
        int viewMode = getIntAttributeValue(outline, ATTR_FEED_VIEW_MODE, bbns, -1);

        boolean dedupEnabled = getBooleanAttributeValue(outline, ATTR_FEED_DEDUP_ENABLED, bbns, false);
        int dedupFrom = getIntAttributeValue(outline, ATTR_FEED_DEDUP_FROM, bbns, -1);
        int dedupTo = getIntAttributeValue(outline, ATTR_FEED_DEDUP_TO, bbns, -1);

        Boolean ascendingSorting = getAscendingSortingAttributeValue(outline, bbns);

        // Create a feed
        QueryOPMLFeed feed = new QueryOPMLFeed(title, queryType, parameter, xmlURL, readArticles,
            pinnedArticles, purgeLimit, rating, viewType, viewModeEnabled, viewMode, ascendingSorting);
        feed.setDedupEnabled(dedupEnabled);
        feed.setDedupFrom(dedupFrom);
        feed.setDedupTo(dedupTo);

        fillUpdatePeriod(feed, outline, bbns);

        return feed;
    }

    /**
     * Fills the update period property.
     *
     * @param feed      feed.
     * @param outline   outline.
     * @param bbns      BB namespace.
     */
    private static void fillUpdatePeriod(DataOPMLFeed feed, Element outline, Namespace bbns)
    {
        long p = getLongAttributeValue(outline, ATTR_FEED_UPDATE_PERIOD, bbns, -1);
        feed.setUpdatePeriod(p > 0 ? p : null);
    }

    /**
     * Returns search feed created from outline.
     *
     * @param outline outline element.
     * @param bbns    BB namespace.
     *
     * @return feed.
     */
    static SearchOPMLFeed createSearchFeed(Element outline, Namespace bbns)
    {
        String title = fetchTitle(outline);
        String query = getAttributeValue(outline, ATTR_FEED_QUERY, bbns);
        int purgeLimit = getIntAttributeValue(outline, ATTR_FEED_LIMIT, bbns, -1);
        int rating = getIntAttributeValue(outline, ATTR_FEED_RATING, bbns, -1);

        int viewType = getIntAttributeValue(outline, FormatConstants.ATTR_FEED_VIEW_TYPE, bbns, -1);
        boolean viewModeEnabled = getBooleanAttributeValue(outline, ATTR_FEED_VIEW_MODE_ENABLED, bbns, false);
        int viewMode = getIntAttributeValue(outline, ATTR_FEED_VIEW_MODE, bbns, -1);

        boolean dedupEnabled = getBooleanAttributeValue(outline, ATTR_FEED_DEDUP_ENABLED, bbns, false);
        int dedupFrom = getIntAttributeValue(outline, ATTR_FEED_DEDUP_FROM, bbns, -1);
        int dedupTo = getIntAttributeValue(outline, ATTR_FEED_DEDUP_TO, bbns, -1);

        Boolean ascendingSorting = getAscendingSortingAttributeValue(outline, bbns);

        SearchOPMLFeed feed = new SearchOPMLFeed(title, query, purgeLimit, rating, viewType, viewModeEnabled, viewMode,
            ascendingSorting);
        feed.setDedupEnabled(dedupEnabled);
        feed.setDedupFrom(dedupFrom);
        feed.setDedupTo(dedupTo);

        return feed;
    }

    /**
     * Returns the value of the ascending sorting attribute.
     *
     * @param outline outline.
     * @param bbns    namespace.
     *
     * @return value.
     */
    private static Boolean getAscendingSortingAttributeValue(Element outline, Namespace bbns)
    {
        String as = getAttributeValue(outline, ATTR_FEED_ASCENDING_SORTING, bbns);
        return as == null ? null : Boolean.valueOf(as);
    }

    /**
     * Reads data from outline attributes and creates feed. Note that no validations included.
     *
     * @param outline outline element from the DOM.
     * @param bbns    BB namespace.
     *
     * @return OPML feed.
     */
    static DirectOPMLFeed createFeed(Element outline, Namespace bbns)
    {
        String title = fetchTitle(outline);
        String xmlUrl = NetUtils.fixFeedURL(getOutlineUrl(outline));
        String htmlUrl = getAttributeValue(outline, ATTR_FEED_HTML_URL, null);
        String readArticles = getAttributeValue(outline, ATTR_FEED_READ_ARTICLES, bbns);
        String pinnedArticles = getAttributeValue(outline, ATTR_FEED_PINNED_ARTICLES, bbns);

        String customTitle = getAttributeValue(outline, ATTR_FEED_CUSTOM_TITLE, bbns);
        String customCreator = getAttributeValue(outline, ATTR_FEED_CUSTOM_CREATOR, bbns);
        String customDescription = getAttributeValue(outline, ATTR_FEED_CUSTOM_DESCRIPTION, bbns);
        String tags = getAttributeValue(outline, ATTR_TAGS, bbns);
        String tagsDescription = getAttributeValue(outline, ATTR_TAGS_DESCRIPTION, bbns);
        String tagsExtended = getAttributeValue(outline, ATTR_TAGS_EXTENDED, bbns);

        boolean disabled = getBooleanAttributeValue(outline, ATTR_DISABLED, bbns, false);

        if (isEmpty(htmlUrl)) htmlUrl = null;

        int rating = getIntAttributeValue(outline, ATTR_FEED_RATING, bbns, -1);
        int purgeLimit = getIntAttributeValue(outline, ATTR_FEED_LIMIT, bbns, -1);

        int viewType = getIntAttributeValue(outline, ATTR_FEED_VIEW_TYPE, bbns, -1);
        boolean viewModeEnabled = getBooleanAttributeValue(outline, ATTR_FEED_VIEW_MODE_ENABLED, bbns, false);
        int viewMode = getIntAttributeValue(outline, ATTR_FEED_VIEW_MODE, bbns, -1);

        Boolean ascendingSorting = getAscendingSortingAttributeValue(outline, bbns);

        DirectOPMLFeed feed = new DirectOPMLFeed(title, xmlUrl, htmlUrl, rating, readArticles, pinnedArticles,
                purgeLimit, customTitle, customCreator, customDescription, tags, tagsDescription, tagsExtended, disabled,
                viewType, viewModeEnabled, viewMode, ascendingSorting);

        fillUpdatePeriod(feed, outline, bbns);

        return feed;
    }

    /**
     * Looks for a title at the outline. First it checks the "title" attribute and then
     * looks for "text" attribute.
     *
     * @param outline outline to scan.
     *
     * @return title from the outline.
     */
    static String fetchTitle(Element outline)
    {
        String title = getAttributeValue(outline, ATTR_FEED_TITLE, null);

        if (isEmpty(title))
        {
            String text = getAttributeValue(outline, ATTR_FEED_TEXT, null);
            if (text != null) title = text;
        }

        return title;
    }

    /**
     * Reads integer attribute from the outline element.
     *
     * @param outline       outline element.
     * @param name          name of attribute.
     * @param bbns          BB namespace.
     * @param defaultValue  default value to put if not found or invalid format.
     *
     * @return value.
     */
    private static int getIntAttributeValue(Element outline, String name, Namespace bbns,
                                            int defaultValue)
    {
        int value = defaultValue;

        String string = getAttributeValue(outline, name, bbns);
        if (string != null)
        {
            try
            {
                value = Integer.parseInt(string);
            } catch (NumberFormatException e)
            {
                LOG.severe("Number format is incorrect for: " + name + " value: " + string);
            }
        }

        return value;
    }

    /**
     * Reads long attribute from the outline element.
     *
     * @param outline       outline element.
     * @param name          name of attribute.
     * @param bbns          BB namespace.
     * @param defaultValue  default value to put if not found or invalid format.
     *
     * @return value.
     */
    private static long getLongAttributeValue(Element outline, String name, Namespace bbns,
                                              long defaultValue)
    {
        long value = defaultValue;

        String string = getAttributeValue(outline, name, bbns);
        if (string != null)
        {
            try
            {
                value = Long.parseLong(string);
            } catch (NumberFormatException e)
            {
                LOG.severe("Number format is incorrect for: " + name + " value: " + string);
            }
        }

        return value;
    }

    /**
     * Reads boolean attribute from the outline element.
     *
     * @param outline       outline element.
     * @param name          name of attribute.
     * @param bbns          BB namespace.
     * @param defaultValue  default value to put if not found or invalid format.
     *
     * @return value.
     */
    private static boolean getBooleanAttributeValue(Element outline, String name, Namespace bbns,
                                                    boolean defaultValue)
    {
        String string = getAttributeValue(outline, name, bbns);
        return string == null ? defaultValue : "true".equalsIgnoreCase(string.trim());
    }

    /**
     * Returns the type of outline:
     * <ul>
     *  <li> <b>OUTLINE_TYPE_INVALID</b> - type set to know values, but some fields missing.</li>
     *  <li> <b>OUTLINE_TYPE_UNKNOWN</b> - type is present, but not recognized.</li>
     *  <li> <b>OUTLINE_TYPE_GUIDE</b> - embedded guide without link and type.</li>
     *  <li> <b>OUTLINE_TYPE_RSS_LINK</b> - link to RSS data.</li>
     *  <li> <b>OUTLINE_TYPE_GUIDE_LINK</b> - link to another nested OPML resource.</li>
     *  <li> <b>OUTLINE_TYPE_QUERY_FEED</b> - query feed with type and parameter.</li>
     *  <li> <b>OUTLINE_TYPE_SEARCH_FEED</b> - search feed with query string.</li>
     * </ul>
     *
     * @param outline   outline to analyze.
     *
     * @return type.
     */
    int getOutlineType(Element outline)
    {
        int type = -2;
        String typeAttr = outline.getAttributeValue(ATTR_FEED_TYPE);
        String outlineUrl = getOutlineUrl(outline);

        // search feed: type=search
        // query feed: type=rss && queryType && queryParam
        // sub-opml: type=opml || oulineUrl.ends(.opml) || (type=link && outline.ends(.opml)) || type=include
        // reading list: type=list
        // rss: type=rss || oulineUrl.outlineUrl.ends(.xml)

        if (typeAttr != null)
        {
            if ("search".equals(typeAttr))
            {
                type = OUTLINE_TYPE_SEARCH_FEED;
            } else if ("list".equals(typeAttr))
            {
                type = OUTLINE_TYPE_READING_LIST;
            } else if ("opml".equalsIgnoreCase(typeAttr) || "include".equalsIgnoreCase(typeAttr))
            {
                type = OUTLINE_TYPE_GUIDE_LINK;
            } else if ("rss".equalsIgnoreCase(typeAttr))
            {
                if (hasQueryFeedAttributes(outline, bbns))
                {
                    type = OUTLINE_TYPE_QUERY_FEED;
                } else if (hasURLAttribute(outline) && hasTitleAttirbute(outline))
                {
                    type = OUTLINE_TYPE_RSS_LINK;
                } else type = OUTLINE_TYPE_INVALID;
            } else if ("link".equalsIgnoreCase(typeAttr))
            {
                type = OUTLINE_TYPE_INVALID;

                try
                {
                    URL url = new URL(outlineUrl);
                    if (url.getPath().endsWith(".opml") || outlineUrl.endsWith(".opml"))
                    {
                        type = OUTLINE_TYPE_GUIDE_LINK;
                    }
                } catch (MalformedURLException e)
                {
                    // Nothing serious.
                }

            } else type = OUTLINE_TYPE_INVALID;
        }

        if (type == -2 && hasURLAttribute(outline))
        {
            if (hasTitleAttirbute(outline))
            {
                outlineUrl = outlineUrl.trim().toLowerCase();
                if (outlineUrl.endsWith(".opml"))
                {
                    type = OUTLINE_TYPE_GUIDE_LINK;
                } else
                {
                    type = OUTLINE_TYPE_RSS_LINK;
                }
            } else type = OUTLINE_TYPE_INVALID;
        }

        return type == -2 ? OUTLINE_TYPE_GUIDE : type;
    }

    private static boolean hasQueryFeedAttributes(Element aOutline, Namespace bbns)
    {
        return getAttribute(aOutline, "querytype", bbns) != null &&
           (getAttribute(aOutline, "queryparam", bbns) != null ||
            getAttribute(aOutline, "keywords", bbns) != null);
    }

    private static Attribute getAttribute(Element outline, String attr, Namespace ns)
    {
        return ns == null
            ? outline.getAttribute(attr.toLowerCase())
            : outline.getAttribute(attr.toLowerCase(), ns);
    }

    private static String getAttributeValue(Element outline, String attr, Namespace ns)
    {
        return ns == null
            ? outline.getAttributeValue(attr.toLowerCase())
            : outline.getAttributeValue(attr.toLowerCase(), ns);
    }

    /**
     * Returns <code>TRUE</code> if outline contains either "xmlurl" or "url" attribute.
     *
     * @param aOutline outline.
     *
     * @return <code>TRUE</code> if outline contains either "xmlurl" or "url" attribute.
     */
    private static boolean hasURLAttribute(Element aOutline)
    {
        return (getAttribute(aOutline, ATTR_FEED_XML_URL, null) != null ||
            aOutline.getAttribute("url") != null);
    }

    /**
     * Returns <code>TRUE</code> if outline contains either "title" or "text" attribute.
     *
     * @param aOutline outline.
     *
     * @return <code>TRUE</code> if outline contains either "title" or "text" attribute.
     */
    private static boolean hasTitleAttirbute(Element aOutline)
    {
        String titleAttr = aOutline.getAttributeValue(ATTR_FEED_TITLE);
        String textAttr = aOutline.getAttributeValue(ATTR_FEED_TEXT);

        return titleAttr != null || textAttr != null;
    }

    /**
     * Tries to get URL from outline. Questions 'xmlUrl' and 'url' attributes.
     *
     * @param aOutline outline to use.
     *
     * @return url or null.
     */
    private static String getOutlineUrl(Element aOutline)
    {
        String url = getAttributeValue(aOutline, ATTR_FEED_XML_URL, null);
        if (url == null) url = aOutline.getAttributeValue("url");

        return url;
    }

    /**
     * Processes OPML document and returns list of guides available for import.
     *
     * @param root root element.
     *
     * @return the set with the list of guides filled with feeds.
     */
    private OPMLGuideSet processMultiple(Element root)
    {
        Element body = root.getChild("body");
        List outlines = body.getChildren("outline");

        if (outlines.size() == 1 && getOutlineType((Element)outlines.get(0)) == OUTLINE_TYPE_GUIDE)
        {
            Element topLevelGuide = (Element)outlines.get(0);
            List children = topLevelGuide.getChildren("outline");

            // There should be no reading lists on the second level
            boolean readingListFound = false;
            boolean subGuidesFound = false;
            for (int i = 0; !readingListFound && i < children.size(); i++)
            {
                Element element = (Element)children.get(i);
                int outlineType = getOutlineType(element);
                readingListFound = outlineType == OUTLINE_TYPE_READING_LIST;
                subGuidesFound |= outlineType == OUTLINE_TYPE_GUIDE;
            }

            if (!readingListFound && subGuidesFound)
            {
                String guideTitle = topLevelGuide.getAttributeValue("text");
                if (guideTitle == null) guideTitle = topLevelGuide.getAttributeValue("title");

                if (guideTitle != null)
                {
                    Element head = root.getChild("head");
                    if (head == null)
                    {
                        head = new Element("head");
                        root.addContent(head);
                    }

                    Element title = head.getChild("title");
                    if (title == null)
                    {
                        title = new Element("title");
                        head.addContent(title);
                    }

                    title.setText(guideTitle);
                }

                for (int i = children.size() - 1; i >= 0; i--)
                {
                    Element element = (Element)children.get(i);
                    topLevelGuide.removeContent(element);
                    body.addContent(element);
                }

                body.removeContent(topLevelGuide);
            }
        }

        return processMultiple2(root);
    }

    private OPMLGuideSet processMultiple2(Element root)
    {
        String setTitle = getTitle(root);
        Date dateModified = getDateModified(root);

        final OPMLGuide rootGuide = new OPMLGuide(setTitle, null, false, null, null, false, 0, false, true, false);

        // In ideal situation we will always have guide-outlines as first level of body.
        // On practice we will not. So if we encounter feed-outline put it into rootGuide.
        // If we encounter non-comment outline parse it as guide.
        final List outlines = root.getChild("body").getChildren("outline");
        ArrayList<DefaultOPMLFeed> rootFeeds = new ArrayList<DefaultOPMLFeed>();
        ArrayList<OPMLGuide> guides = new ArrayList<OPMLGuide>();
        for (Object obj : outlines)
        {
            final Element outline = (Element)obj;
            Transformation.lowercaseAttributes(outline);
            int type = getOutlineType(outline);

            switch (type)
            {
                case OUTLINE_TYPE_INVALID:
                    LOG.fine("[" + setTitle + "] Unrecognized OPML tag detected: " + outline);
                    break;
                case OUTLINE_TYPE_READING_LIST:
                case OUTLINE_TYPE_GUIDE:
                    // If normal outline parse it as guide.
                    OPMLGuide guide = createGuide(outline);
                    if (allowEmptyGuides || guide.getFeeds().size() > 0 ||
                            guide.getReadingLists().length > 0)
                    {
                        guides.add(guide);
                    }
                    break;
                case OUTLINE_TYPE_GUIDE_LINK:
                    // Embedded external guide.
                    try
                    {
                        OPMLGuideSet guideSet = process(getOutlineUrl(outline), true);
                        guides.addAll(Arrays.asList(guideSet.getGuides()));
                    } catch (ImporterException e)
                    {
                        if (LOG.isLoggable(Level.WARNING))
                        {
                            LOG.log(Level.WARNING, "Problem with embedding external OPML.", e);
                        }
                    }
                    break;
                case OUTLINE_TYPE_RSS_LINK:
                    // Simple RSS link.
                    rootFeeds.add(createFeed(outline, bbns));
                    break;
                case OUTLINE_TYPE_QUERY_FEED:
                    // Simple RSS link.
                    rootFeeds.add(createQueryFeed(outline, bbns));
                    break;
                case OUTLINE_TYPE_SEARCH_FEED:
                    // Simple RSS link.
                    rootFeeds.add(createSearchFeed(outline, bbns));
                    break;
                default:
                    LOG.severe("Invalid type (" + type + ") for outline: " + outline);
                    break;
            }
        }

        // If root guide has some feeds or reading lists put it on top of the list
        rootGuide.setFeeds(rootFeeds);
        if (rootGuide.getFeeds().size() > 0 || rootGuide.getReadingLists().length > 0)
        {
            guides.add(0, rootGuide);
        }

        OPMLGuide[] opmlGuides = guides.toArray(new OPMLGuide[guides.size()]);
        return new OPMLGuideSet(setTitle, opmlGuides, dateModified);
    }

    /**
     * Returns title of root guide based on opml:head:title or 'Untitled' when no title set.
     *
     * @param root root element.
     *
     * @return title of root guide.
     */
    private static String getTitle(Element root)
    {
        String rootGuideTitle = "Untitled";

        Element head = root.getChild("head");
        if (head != null)
        {
            String titleE = head.getChildTextTrim("title");
            if (titleE != null) rootGuideTitle = titleE;
        }

        return rootGuideTitle;
    }

    /**
     * Makes an attempts to read the dateCreateModified tag value from the head section.
     *
     * @param root root element.
     *
     * @return the dateModified tag value or <code>NULL</code>.
     */
    private static Date getDateModified(Element root)
    {
        Date date = null;

        Element head = root.getChild(TAG_HEAD);
        if (head != null)
        {
            String dateModifiedS = head.getChildTextTrim(TAG_HEAD_DATE_MODIFIED);
            if (dateModifiedS != null)
            {
                try
                {
                    date = new SimpleDateFormat(DATE_FORMAT).parse(dateModifiedS);
                } catch (ParseException e)
                {
                    date = null;
                }
            }
        }

        return date;
    }

    /**
     * Parses give outline in order to create guide from it. Takes information from attributes
     * to define the guide and information from children to define feeds.
     *
     * @param outline element to start with.
     *
     * @return guide.
     */
    OPMLGuide createGuide(Element outline)
    {
        String title = fetchTitle(outline);
        if (title == null) title = "untitled";

        String icon = getAttributeValue(outline, ATTR_GUIDE_ICON, bbns);
        if (isEmpty(icon)) icon = null;

        final OPMLGuide guide = new OPMLGuide(title, icon,
            "true".equals(getAttributeValue(outline, ATTR_GUIDE_PUB_ENABLED, bbns)),
            getAttributeValue(outline, ATTR_GUIDE_PUB_TITLE, bbns),
            getAttributeValue(outline, ATTR_GUIDE_PUB_TAGS, bbns),
            "true".equals(getAttributeValue(outline, ATTR_GUIDE_PUB_PUBLIC, bbns)),
            getIntAttributeValue(outline, ATTR_GUIDE_PUB_RATING, bbns, 0),
            "true".equals(getAttributeValue(outline, ATTR_GUIDE_AUTO_FEEDS_DISCOVERY, bbns)),
            "true".equals(getAttributeValue(outline, ATTR_GUIDE_NOTIFICATIONS_ALLOWED, bbns)),
            "true".equals(getAttributeValue(outline, ATTR_GUIDE_MOBILE, bbns)));

        guide.setFeeds(new ArrayList<DefaultOPMLFeed>());
        collectObjects(outline, guide);

        return guide;
    }

    /**
     * Returns <code>TRUE</code> only if string is <code>NULL</code> or empty.
     *
     * @param string string to check.
     *
     * @return <code>TRUE</code> if <code>NULL</code> or empty.
     */
    private static boolean isEmpty(String string)
    {
        return (string == null || string.trim().length() == 0);
    }

    /**
     * Recusively collects feed outlines and reading lists.
     *
     * @param element       element to start from.
     * @param guide         guide to populate.
     */
    void collectObjects(Element element, OPMLGuide guide)
    {
        List<DefaultOPMLFeed> feedsArray = guide.getFeeds();
        List outlines = element.getChildren("outline");
        for (Object obj : outlines)
        {
            Element outline = (Element)obj;
            Transformation.lowercaseAttributes(outline);
            int type = getOutlineType(outline);

            switch (type)
            {
                case OUTLINE_TYPE_INVALID:
                    LOG.fine("Unrecognized OPML tag detected: " + outline);
                    break;
                case OUTLINE_TYPE_GUIDE:
                    // If normal outline parse it as guide.
                    collectObjects(outline, guide);
                    break;
                case OUTLINE_TYPE_READING_LIST:
                    parseReadingList(outline, guide);
                    break;
                case OUTLINE_TYPE_GUIDE_LINK:
                    // Embedded external guide.
                    try
                    {
                        OPMLGuideSet guideSet = process(getOutlineUrl(outline), true);
                        OPMLGuide[] embeddedGuides = guideSet.getGuides();
                        if (embeddedGuides.length > 0)
                        {
                            feedsArray.addAll(embeddedGuides[0].getFeeds());
                        }
                    } catch (ImporterException e)
                    {
                        if (LOG.isLoggable(Level.WARNING))
                        {
                            LOG.log(Level.WARNING, "Problem with embedding external OPML.", e);
                        }
                    }
                    break;
                case OUTLINE_TYPE_RSS_LINK:
                    // Simple RSS link.
                    feedsArray.add(createFeed(outline, bbns));
                    break;
                case OUTLINE_TYPE_QUERY_FEED:
                    // Simple RSS link.
                    feedsArray.add(createQueryFeed(outline, bbns));
                    break;
                case OUTLINE_TYPE_SEARCH_FEED:
                    // Simple RSS link.
                    feedsArray.add(createSearchFeed(outline, bbns));
                    break;
                default:
                    LOG.severe("Invalid type (" + type + ") for outline: " + outline);
                    break;
            }
        }
    }

    /**
     * Parses reading list outline.
     *
     * @param outline   outline.
     * @param guide     guide object to add reading list to.
     */
    void parseReadingList(Element outline, OPMLGuide guide)
    {
        String title = getAttributeValue(outline, ATTR_READING_LIST_TITLE, null);
        String url = getAttributeValue(outline, ATTR_READING_LIST_URL, null);

        if (title != null && url != null && url.trim().length() > 0)
        {
            OPMLReadingList list = new OPMLReadingList(title, url.trim());

            // Collect feeds and place them into the list
            OPMLGuide temp = new OPMLGuide("", "", false, null, null, false, 0, false, false, false);
            collectObjects(outline, temp);

            List<DefaultOPMLFeed> feeds = temp.getFeeds();
            List<DirectOPMLFeed> dfeeds = new ArrayList<DirectOPMLFeed>(feeds.size());
            for (DefaultOPMLFeed feed : feeds)
            {
                if (feed instanceof DirectOPMLFeed) dfeeds.add((DirectOPMLFeed)feed);
            }
            list.setFeeds(dfeeds);

            guide.add(list);
        }
    }
}
