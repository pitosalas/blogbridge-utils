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
// $Id: TestImporter.java,v 1.37 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml;

import com.salas.bbutilities.opml.objects.*;
import com.salas.bbutilities.opml.utils.Transformation;
import junit.framework.TestCase;
import org.jdom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This suite contains tests for <code>Importer</code> unit.
 * @noinspection JavaDoc
 */
public class TestImporter extends TestCase
{
    private static final String DATA_ROOT = "/src/test-data/opml/";
    private static final int MULTILEVEL_2_GUIDE_COUNT = 3;
    private static final int CONVERT_TO_SINGLE_CHANNELS_TOTAL = 6;

    private Importer importer;

    protected void setUp() throws Exception
    {
        importer = new Importer();
    }

    /**
     * @see Importer#processSingle
     */
    public void testProcessSingle()
    {
        checkInvalidCases(true);

        try
        {
            OPMLGuideSet guideSet = importer.process(getUrl("flat.opml"), true);
            assertEquals("title", guideSet.getTitle());

            OPMLGuide[] guides = guideSet.getGuides();
            assertEquals(1, guides.length);

            OPMLGuide guide = guides[0];
            List channels = guide.getFeeds();
            assertEquals("title", guide.getTitle());
            assertEquals(4, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");
            checkChannel((DirectOPMLFeed)channels.get(1), "title2", "xmlUrl2", "htmlUrl2");
            QueryOPMLFeed feed;
            feed = (QueryOPMLFeed)channels.get(2);
            assertEquals("Wrong title.", "QueryFeed", feed.getTitle());
            assertEquals("Wrong query type.", 1, feed.getQueryType());
            assertEquals("Wrong parameter.", "a b", feed.getQueryParam());
            feed = (QueryOPMLFeed)channels.get(3);
            assertEquals("Wrong title.", "QueryFeed2", feed.getTitle());
            assertEquals("Wrong query type.", 2, feed.getQueryType());
            assertEquals("Wrong parameter.", "a b", feed.getQueryParam());

            guideSet = importer.process(getUrl("multilevel-1.opml"), true);
            guides = guideSet.getGuides();
            assertEquals(1, guides.length);

            guide = guides[0];
            channels = guide.getFeeds();
            assertEquals("title", guide.getTitle());
            assertEquals(2, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title2", "xmlUrl2", null);
            checkChannel((DirectOPMLFeed)channels.get(1), "title1", "xmlUrl1", "htmlUrl1");
        } catch (ImporterException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * @see Importer#processMultiple
     */
    public void testProcessMultiple()
    {
        checkInvalidCases(true);

        try
        {
            // Flat
            OPMLGuideSet guideSet = importer.process(getUrl("flat.opml"), false);
            OPMLGuide[] guides = guideSet.getGuides();
            assertEquals(1, guides.length);

            OPMLGuide guide = guides[0];
            List channels = guide.getFeeds();
            assertEquals("title", guide.getTitle());
            assertEquals(4, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");
            checkChannel((DirectOPMLFeed)channels.get(1), "title2", "xmlUrl2", "htmlUrl2");
            QueryOPMLFeed feed;
            feed = (QueryOPMLFeed)channels.get(2);
            assertEquals("Wrong title.", "QueryFeed", feed.getTitle());
            assertEquals("Wrong query type.", 1, feed.getQueryType());
            assertEquals("Wrong parameter.", "a b", feed.getQueryParam());
            assertEquals("Wrong rating.", -1, feed.getRating());
            feed = (QueryOPMLFeed)channels.get(3);
            assertEquals("Wrong rating.", 2, feed.getRating());

            // Multi-level 1
            guideSet = importer.process(getUrl("multilevel-1.opml"), false);
            guides = guideSet.getGuides();
            assertEquals(2, guides.length);

            guide = guides[0];
            channels = guide.getFeeds();
            assertEquals("title", guide.getTitle());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title2", "xmlUrl2", null);

            guide = guides[1];
            channels = guide.getFeeds();
            assertEquals("guide1", guide.getTitle());
            assertEquals("default", guide.getIcon());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");

            // Multi-level 1
            guides = importer.process(getUrl("multilevel-2.opml"), false).getGuides();
            assertEquals(MULTILEVEL_2_GUIDE_COUNT, guides.length);

            guide = guides[0];
            channels = guide.getFeeds();
            assertEquals("guide0", guide.getTitle());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title4", "xmlUrl2", "htmlUrl2");

            guide = guides[1];
            channels = guide.getFeeds();
            assertEquals("guide1", guide.getTitle());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");

            guide = guides[2];
            channels = guide.getFeeds();
            assertEquals("guide2", guide.getTitle());
            assertEquals(2, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title2", "xmlUrl1", null);
            checkChannel((DirectOPMLFeed)channels.get(1), "title3", "xmlUrl1", "htmlUrl1");
        } catch (ImporterException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * @see Importer#processFromString
     */
    public void testProcessFromString()
    {
        try
        {
            // Flat
            OPMLGuideSet guideSet = importer.processFromString(readFile("flat.opml"), false);
            OPMLGuide[] guides = guideSet.getGuides();
            assertEquals(1, guides.length);

            OPMLGuide guide = guides[0];
            List channels = guide.getFeeds();
            assertEquals("title", guide.getTitle());
            assertEquals(4, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");
            checkChannel((DirectOPMLFeed)channels.get(1), "title2", "xmlUrl2", "htmlUrl2");
            QueryOPMLFeed feed = (QueryOPMLFeed)channels.get(2);
            assertEquals("Wrong title.", "QueryFeed", feed.getTitle());
            assertEquals("Wrong query type.", 1, feed.getQueryType());
            assertEquals("Wrong parameter.", "a b", feed.getQueryParam());

            // Multi-level 1
            guides = importer.processFromString(readFile("multilevel-1.opml"), false).getGuides();
            assertEquals(2, guides.length);

            guide = guides[0];
            channels = guide.getFeeds();
            assertEquals("title", guide.getTitle());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title2", "xmlUrl2", null);

            guide = guides[1];
            channels = guide.getFeeds();
            assertEquals("guide1", guide.getTitle());
            assertEquals("default", guide.getIcon());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");

            // Multi-level 1
            guides = importer.processFromString(readFile("multilevel-2.opml"), false).getGuides();
            assertEquals(MULTILEVEL_2_GUIDE_COUNT, guides.length);

            guide = guides[0];
            channels = guide.getFeeds();
            assertEquals("guide0", guide.getTitle());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title4", "xmlUrl2", "htmlUrl2");

            guide = guides[1];
            channels = guide.getFeeds();
            assertEquals("guide1", guide.getTitle());
            assertEquals(1, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title1", "xmlUrl1", "htmlUrl1");

            guide = guides[2];
            channels = guide.getFeeds();
            assertEquals("guide2", guide.getTitle());
            assertEquals(2, channels.size());
            checkChannel((DirectOPMLFeed)channels.get(0), "title2", "xmlUrl1", null);
            checkChannel((DirectOPMLFeed)channels.get(1), "title3", "xmlUrl1", "htmlUrl1");
        } catch (ImporterException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * @see Importer#convertToSingle
     */
    public void testConvertToSingle1()
    {
        OPMLGuide[] guides;
        ArrayList<DefaultOPMLFeed> al;

        guides = new OPMLGuide[]
        {
            new OPMLGuide("0", null, false, null, null, false, 0, false, false, false),
            new OPMLGuide("1", "icon", false, null, null, false, 1, false, false, false),
            new OPMLGuide("2", "icon2", false, null, null, false, 2, false, false, false)
        };

        al = new ArrayList<DefaultOPMLFeed>();
        al.add(new DirectOPMLFeed("0_0", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        al.add(new DirectOPMLFeed("0_1", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        guides[0].setFeeds(al);

        al = new ArrayList<DefaultOPMLFeed>();
        al.add(new DirectOPMLFeed("1_0", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        al.add(new DirectOPMLFeed("1_1", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        guides[1].setFeeds(al);

        al = new ArrayList<DefaultOPMLFeed>();
        al.add(new DirectOPMLFeed("2_0", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        al.add(new DirectOPMLFeed("2_1", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        guides[2].setFeeds(al);

        final OPMLGuide guide = Importer.convertToSingle(guides);
        final List channels = guide.getFeeds();

        assertEquals("0", guide.getTitle());
        assertEquals(CONVERT_TO_SINGLE_CHANNELS_TOTAL, channels.size());
        checkChannel((DirectOPMLFeed)channels.get(0), "0_0", "xml", "html");
        checkChannel((DirectOPMLFeed)channels.get(1), "0_1", "xml", "html");
        checkChannel((DirectOPMLFeed)channels.get(2), "1_0", "xml", "html");
        checkChannel((DirectOPMLFeed)channels.get(3), "1_1", "xml", "html");
        checkChannel((DirectOPMLFeed)channels.get(4), "2_0", "xml", "html");
        checkChannel((DirectOPMLFeed)channels.get(5), "2_1", "xml", "html");
    }

    /**
     * @see Importer#convertToSingle
     */
    public void testConvertToSingle2()
    {
        OPMLGuide[] guides;
        ArrayList<DefaultOPMLFeed> al;

        guides = new OPMLGuide[]
        {
            new OPMLGuide("0", null, false, null, null, false, 0, false, false, false),
            new OPMLGuide("1", "icon", false, null, null, false, 1, false, false, false),
        };

        al = new ArrayList<DefaultOPMLFeed>();
        al.add(new DirectOPMLFeed("1_0", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        al.add(new DirectOPMLFeed("1_1", "xml", "html", 0, null, null, -1, null, null, null, null, null, null, false, 1, false, 1, null));
        guides[1].setFeeds(al);

        final OPMLGuide guide = Importer.convertToSingle(guides);
        final List channels = guide.getFeeds();

        assertEquals("1", guide.getTitle());
        assertEquals("icon", guide.getIcon());
        assertEquals(2, channels.size());
        checkChannel((DirectOPMLFeed)channels.get(0), "1_0", "xml", "html");
        checkChannel((DirectOPMLFeed)channels.get(1), "1_1", "xml", "html");
    }

    /**
     * Tests converting the set to to flat one-guide look.
     */
    public void testConvertToSingleSet()
    {
        Date now = new Date();
        OPMLGuideSet set = new OPMLGuideSet("a", new OPMLGuide[0], now);
        OPMLGuideSet newSet = Importer.convertToSingle(set);

        assertEquals("a", newSet.getTitle());
        assertEquals(now, newSet.getDateModified());
        assertEquals("No guides is converted to no guides.", 0, newSet.getGuides().length);

        // One guide
        OPMLGuide guide = new OPMLGuide("b", "c", false, "d", "e", false, 0, false, false, false);
        set = new OPMLGuideSet("a", new OPMLGuide[] { guide }, now);
        newSet = Importer.convertToSingle(set);

        assertEquals("a", newSet.getTitle());
        assertEquals(now, newSet.getDateModified());
        assertEquals(1, newSet.getGuides().length);
        assertTrue(guide == newSet.getGuides()[0]);
    }

    /**
     * Checks all possible invalid cases.
     *
     * @param isSingleMode TRUE if in single-guide mode.
     */
    private void checkInvalidCases(boolean isSingleMode)
    {
        // Incorrect URL's
        checkIncorrectUrl("");
        checkIncorrectUrl("file/abc");

        try
        {
            importer.process(getUrl("invalid-1.opml"), isSingleMode);
            fail("Parsing exception should be thrown. Not an OPML file.");
        } catch (ImporterException e)
        {
            // Normal behavior
            assertEquals(ImporterException.TYPE_PARSING, e.getType());
        }

        try
        {
            importer.process(getUrl("invalid-2.opml"), isSingleMode);
            fail("Parsing exception should be thrown. File is empty.");
        } catch (ImporterException e)
        {
            // Normal behavior
            assertEquals(ImporterException.TYPE_PARSING, e.getType());
        }

        try
        {
            importer.process(getUrl("invalid-3.opml"), isSingleMode);
            fail("Parsing exception should be thrown. File is empty.");
        } catch (ImporterException e)
        {
            // Normal behavior
            assertEquals(ImporterException.TYPE_PARSING, e.getType());
        }

        try
        {
            importer.process(getUrl("invalid-4.opml"), isSingleMode);
            fail("Parsing exception should be thrown. Version is not supported.");
        } catch (ImporterException e)
        {
            // Normal behavior
            assertEquals(ImporterException.TYPE_PARSING, e.getType());
        }

        try
        {
            importer.process(getUrl("invalid-5.opml"), isSingleMode);
            fail("Parsing exception should be thrown. Body is missing.");
        } catch (ImporterException e)
        {
            // Normal behavior
            assertEquals(ImporterException.TYPE_PARSING, e.getType());
        }
    }

    /**
     * Tests how guides are parsed.
     */
    public void testExtendedParsingGuides()
    {
        Element outline;

        // <outline title="1">...</outline>
        outline = new Element("outline");
        outline.setAttribute("title", "1");

        assertEquals(Importer.OUTLINE_TYPE_GUIDE, importer.getOutlineType(outline));
    }

    /**
     * Tests how links to external guides are parsed.
     */
    public void testExtendedParsingGuidesLinks()
    {
        Element outline;

        // <outline type="opml" url="1"/>
        outline = new Element("outline");
        outline.setAttribute("type", "opml");
        outline.setAttribute("url", "1");

        assertEquals(Importer.OUTLINE_TYPE_GUIDE_LINK, importer.getOutlineType(outline));

        // <outline type="opml" xmlUrl="1"/>
        outline = new Element("outline");
        outline.setAttribute("type", "opml");
        outline.setAttribute("xmlUrl", "1");

        assertEquals(Importer.OUTLINE_TYPE_GUIDE_LINK, importer.getOutlineType(outline));

        // <outline type="lmpo" url="1.opml"/>
        outline = new Element("outline");
        outline.setAttribute("type", "lmpo");
        outline.setAttribute("xmlUrl", "1.opml");

        assertEquals("No title.", Importer.OUTLINE_TYPE_INVALID, importer.getOutlineType(outline));

        // <outline type="include" url="1.xml"/>
        outline = new Element("outline");
        outline.setAttribute("type", "include");
        outline.setAttribute("xmlUrl", "1.xml");

        assertEquals(Importer.OUTLINE_TYPE_GUIDE_LINK, importer.getOutlineType(outline));
    }

    /**
     * Tests how regular blogs are parsed.
     */
    public void testExtendedParsingRss()
    {
        Element outline;

        // <outline title="1" url="1"/>
        outline = new Element("outline");
        outline.setAttribute("title", "1");
        outline.setAttribute("url", "1.xml");

        assertEquals(Importer.OUTLINE_TYPE_RSS_LINK, importer.getOutlineType(outline));

        // <outline type="rss" title="1" url="1"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "1");
        outline.setAttribute("url", "1");

        assertEquals(Importer.OUTLINE_TYPE_RSS_LINK, importer.getOutlineType(outline));

        // <outline type="rss" text="2" url="2"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("text", "2");
        outline.setAttribute("url", "2");

        assertEquals(Importer.OUTLINE_TYPE_RSS_LINK, importer.getOutlineType(outline));

        // <outline type="rss" text="3" xmlurl="3"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("text", "3");
        outline.setAttribute("xmlurl", "3");

        assertEquals(Importer.OUTLINE_TYPE_RSS_LINK, importer.getOutlineType(outline));

        // <outline type="rss" title="4" xmlurl="4"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "4");
        outline.setAttribute("xmlurl", "4");

        assertEquals(Importer.OUTLINE_TYPE_RSS_LINK, importer.getOutlineType(outline));

        // <outline type="ssr" title="5" xmlurl="5.xml"/>
        outline = new Element("outline");
        outline.setAttribute("type", "ssr");
        outline.setAttribute("title", "5");
        outline.setAttribute("xmlurl", "5.xml");

        assertEquals(Importer.OUTLINE_TYPE_INVALID, importer.getOutlineType(outline));
    }

    /**
     * Tests how invalid outlines detected.
     */
    public void testExtendedParsingInvalid()
    {
        Element outline;

        // <outline type="rss" title="1" url="1"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss1");
        outline.setAttribute("title", "1");
        outline.setAttribute("url", "1");

        assertEquals(Importer.OUTLINE_TYPE_INVALID, importer.getOutlineType(outline));
    }

    /**
     * Tests how feeds with empty title and specified text are parsed.
     */
    public void testParsingNoTitle()
    {
        Element outline;

        // <outline type="rss" title="" url="http://some"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "");
        outline.setAttribute("url", "http://some");

        DirectOPMLFeed feed = Importer.createFeed(outline, null);
        assertEquals("", feed.getTitle());

        // <outline type="rss" title="" text="title" url="http://some"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "");
        outline.setAttribute("text", "title");
        outline.setAttribute("url", "http://some");

        feed = Importer.createFeed(outline, null);
        assertEquals("title", feed.getTitle());
        assertEquals(-1, feed.getRating());
    }

    /**
     * Tests detecting query feeds.
     */
    public void testGetOutlineTypeQueryFeed()
    {
        Element outline;

        // <outline type="rss" title="1" querytype="2" queryparam="a b c"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "1");
        outline.setAttribute("querytype", "2");
        outline.setAttribute("queryparam", "a b c");

        assertEquals(Importer.OUTLINE_TYPE_QUERY_FEED, importer.getOutlineType(outline));

        // <outline type="rss" title="1" querytype="2" queryparam="a b c" xmlurl="file://some"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "1");
        outline.setAttribute("querytype", "2");
        outline.setAttribute("queryparam", "a b c");
        outline.setAttribute("xmlurl", "file://some");

        assertEquals(Importer.OUTLINE_TYPE_QUERY_FEED, importer.getOutlineType(outline));
    }

    /**
     * Tests detecting search feed outlines.
     */
    public void testGetOutlineTypeSearchFeed()
    {
        Element outline;

        // <outline type="search" title="test" query="a"/>
        outline = new Element("outline");
        outline.setAttribute("type", "search");
        outline.setAttribute("text", "test");
        outline.setAttribute("query", "a");

        assertEquals(Importer.OUTLINE_TYPE_SEARCH_FEED, importer.getOutlineType(outline));
    }

    /**
     * Tests detecting search feed outlines.
     */
    public void testGetOutlineTypeSearchFeedBBNS()
    {
        Element outline;

        // <outline type="search" text="test" bb:query="a"/>
        outline = new Element("outline");
        outline.setAttribute("type", "search");
        outline.setAttribute("text", "test");
        outline.setAttribute("query", "a", FormatConstants.BB_NAMESPACE);

        assertEquals(Importer.OUTLINE_TYPE_SEARCH_FEED, importer.getOutlineType(outline));
    }

    /**
     * Tests recreation of search feed from OPML.
     */
    public void testCreateSearchFeed()
    {
        Element outline;

        // <outline type="search" text="test" query="a" limit="1" rating="2" viewtype="1" viewmodeenabled="true" viemmode="2"/>
        outline = new Element("outline");
        outline.setAttribute("type", "search");
        outline.setAttribute("text", "test");
        outline.setAttribute("query", "a");
        outline.setAttribute("limit", "1");
        outline.setAttribute("rating", "2");
        outline.setAttribute("viewtype", "1");
        outline.setAttribute("viewmodeenabled", "true");
        outline.setAttribute("viewmode", "2");
        outline.setAttribute("ascendingsorting", "true");

        SearchOPMLFeed searchFeed = Importer.createSearchFeed(outline, null);
        SearchOPMLFeed match = new SearchOPMLFeed("test", "a", 1, 2, 1, true, 2, true);
        assertEquals(match, searchFeed);
    }

    /**
     * Tests recreation of search feed from OPML.
     */
    public void testCreateSearchFeedBBNS()
    {
        Element outline;

        // <outline type="search" text="test" bb:query="a" bb:limit="1" bb:rating="2"/>
        outline = new Element("outline");
        outline.setAttribute("type", "search");
        outline.setAttribute("text", "test");
        outline.setAttribute("query", "a", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("limit", "1", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("rating", "2", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("viewtype", "1", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("viewmodeenabled", "true", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("viewmode", "2", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("dedupenabled", "true", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("dedupfrom", "5", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("dedupto", "6", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("ascendingsorting", "false", FormatConstants.BB_NAMESPACE);

        SearchOPMLFeed searchFeed = Importer.createSearchFeed(outline,FormatConstants.BB_NAMESPACE);
        SearchOPMLFeed match = new SearchOPMLFeed("test", "a", 1, 2, 1, true, 2, false);
        match.setDedupEnabled(true);
        match.setDedupFrom(5);
        match.setDedupTo(6);
        assertEquals(match, searchFeed);
    }

    /**
     * Tests recreation of query feed from OPML.
     */
    public void testCreateQueryFeedBBNS()
    {
        Element outline;

        // <outline type="search" text="test" bb:query="a"/>
        outline = new Element("outline");
        outline.setAttribute("type", "search");
        outline.setAttribute("text", "test");
        outline.setAttribute("querytype", "2", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("queryparam", "a b c", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("limit", "1", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("rating", "2", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("viewtype", "1", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("viewmodeenabled", "true", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("viewmode", "2", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("dedupenabled", "true", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("dedupfrom", "5", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("dedupto", "6", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("updateperiod", "1", FormatConstants.BB_NAMESPACE);

        QueryOPMLFeed queryFeed = Importer.createQueryFeed(outline, FormatConstants.BB_NAMESPACE);
        QueryOPMLFeed match = new QueryOPMLFeed("test", 2, "a b c", null, null, null, 1, 2, 1, true, 2, null);
        match.setDedupEnabled(true);
        match.setDedupFrom(5);
        match.setDedupTo(6);
        match.setUpdatePeriod(1l);
        assertEquals(match, queryFeed);
    }

    /**
     * Tests deserialization of custom fields (customTitle, customCreator, customDescription).
     */
    public void testParsingOfCustomFields()
    {
        Element outline;

        // <outline type="rss" title="5" xmlurl="5.xml"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "5");
        outline.setAttribute("xmlurl", "5.xml");

        DirectOPMLFeed channel = Importer.createFeed(outline, null);
        assertNotNull(channel);
        assertEquals("5", channel.getTitle());
        assertNull(channel.getCustomTitle());
        assertNull(channel.getCustomCreator());
        assertNull(channel.getCustomDescription());

        // <outline type="rss" title="5" xmlurl="5.xml" customtitle="5" customcreator="5"
        //  customdescription="5"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "5");
        outline.setAttribute("xmlurl", "5.xml");
        outline.setAttribute("customtitle", "5");
        outline.setAttribute("customcreator", "5");
        outline.setAttribute("customdescription", "5");

        channel = Importer.createFeed(outline, null);
        assertNotNull(channel);
        assertEquals("5", channel.getCustomTitle());
        assertEquals("5", channel.getCustomCreator());
        assertEquals("5", channel.getCustomDescription());
    }

    /**
     * Tests deserialization of custom fields (customTitle, customCreator, customDescription).
     */
    public void testParsingOfCustomFieldsBBNS()
    {
        Element outline;

        // <outline type="rss" title="5" xmlurl="5.xml"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "5");
        outline.setAttribute("xmlurl", "5.xml");

        DirectOPMLFeed channel = Importer.createFeed(outline, FormatConstants.BB_NAMESPACE);
        assertNotNull(channel);
        assertEquals("5", channel.getTitle());
        assertNull(channel.getCustomTitle());
        assertNull(channel.getCustomCreator());
        assertNull(channel.getCustomDescription());

        // <outline type="rss" title="5" xmlurl="5.xml" customtitle="5" customcreator="5"
        //  customdescription="5"/>
        outline = new Element("outline");
        outline.setAttribute("type", "rss");
        outline.setAttribute("title", "5");
        outline.setAttribute("xmlurl", "5.xml");
        outline.setAttribute("customtitle", "5", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("customcreator", "5", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("customdescription", "5", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("updateperiod", "1", FormatConstants.BB_NAMESPACE);

        channel = Importer.createFeed(outline, FormatConstants.BB_NAMESPACE);
        assertNotNull(channel);
        assertEquals("5", channel.getCustomTitle());
        assertEquals("5", channel.getCustomCreator());
        assertEquals("5", channel.getCustomDescription());
        assertEquals(new Long(1l), channel.getUpdatePeriod());
    }

    /**
     * Verifies lowercasing of the attributes names.
     */
    public void testLowercaseAttributes()
    {
        Element someElement = new Element("outline");
        someElement.setAttribute("test", "is best");
        someElement.setAttribute("ReSt", "is test");

        Transformation.lowercaseAttributes(someElement);

        assertEquals("Wrong number of attributes", 2, someElement.getAttributes().size());
        assertEquals("is best", someElement.getAttributeValue("test"));
        assertEquals("is test", someElement.getAttributeValue("rest"));
    }

    /** Returns URL of the file. */
    private URL getUrl(String fileName)
    {
        URL url = null;
        try
        {
            url = new URL("file:" + System.getProperty("user.dir") + DATA_ROOT + fileName);
            assertNotNull("URL not found for file: " + fileName, url);
        } catch (MalformedURLException e)
        {
            fail("Failed to create URL for file: " + fileName);
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Check of single channel.
     *
     * @param channel channel to check.
     * @param title   supposed title.
     * @param xmlUrl  supposed XML URL.
     * @param htmlUrl supposed HTML URL.
     */
    private void checkChannel(DirectOPMLFeed channel, String title, String xmlUrl, String htmlUrl)
    {
        assertEquals(title, channel.getTitle());
        assertEquals(xmlUrl, channel.getXmlURL());
        assertEquals(htmlUrl, channel.getHtmlURL());
    }

    /**
     * Ensures that provided URL is incorrect.
     *
     * @param urlString URL.
     */
    private void checkIncorrectUrl(final String urlString)
    {
        try
        {
            importer.process(urlString, true);
            fail("Exception should be thrown. URL is incorrect.");
        } catch (ImporterException e)
        {
            assertEquals(e.getType(), ImporterException.TYPE_MALFORMED_URL);
        }
    }

    /**
     * Reads whole file into single string.
     *
     * @param filename name of the file.
     * @return string.
     */
    private String readFile(String filename)
    {
        String result = null;
        URL file = getUrl(filename);

        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(file.openStream()));
            StringBuffer buf = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) buf.append(line);
            result = buf.toString();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null) br.close();
            } catch (IOException e)
            {
                // Nothing to do here
            }
        }

        return result;
    }

    /**
     * Tests different modes of fetching titles.
     */
    public void testFetchTitle()
    {
        Element outline = new Element("outline");

        assertNull("No title or text defined.", Importer.fetchTitle(outline));

        outline.setAttribute("title", "");
        assertEquals("Title is set to empty string.", "", Importer.fetchTitle(outline));

        outline.setAttribute("text", "text");
        assertEquals("Text attr has better value than empty title.",
            "text", Importer.fetchTitle(outline));

        outline.setAttribute("title", "title");
        assertEquals("Dispite the text attr has value we take title as it is defined.",
            "title", Importer.fetchTitle(outline));

        outline.removeAttribute("title");
        outline.setAttribute("text", "");
        assertEquals("Title attr is not set and text attr is empty.",
            "", Importer.fetchTitle(outline));
    }

    /**
     * Tests detecting feeds.
     */
    public void testDetectingDirectFeeds()
    {
        Element outline;

         // <outline title="2RSS" xmlUrl="http://www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "http://www.2rss.com/rss20.php");

        int type = importer.getOutlineType(outline);
        assertEquals("Should be direct feed.", Importer.OUTLINE_TYPE_RSS_LINK, type);
    }

    /**
     * Tests detecting direct feeds with URL's, like this:
     * <ul>
     *  <li><code>feed://something/</code></li>
     *  <li><code>feed:http://something/</code></li>
     *  <li><code>feed://http://something/</code></li>
     * </ul>
     */
    public void testDetectingDirectFeedsWithFeedPrefix()
    {
        Element outline;

         // <outline title="2RSS" xmlUrl="feed://www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "feed://www.2rss.com/rss20.php");

        int type = importer.getOutlineType(outline);
        assertEquals("Should be direct feed.", Importer.OUTLINE_TYPE_RSS_LINK, type);

         // <outline title="2RSS" xmlUrl="feed:http//www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "feed:http//www.2rss.com/rss20.php");

        type = importer.getOutlineType(outline);
        assertEquals("Should be direct feed.", Importer.OUTLINE_TYPE_RSS_LINK, type);

         // <outline title="2RSS" xmlUrl="feed://http//www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "feed://http//www.2rss.com/rss20.php");

        type = importer.getOutlineType(outline);
        assertEquals("Should be direct feed.", Importer.OUTLINE_TYPE_RSS_LINK, type);
    }

    /**
     * Detecting of OPML links.
     *
     * WARNING: This goes against the spec, but we do it to support resources, like
     *          http://toptensources.com/TopTenSources/Directory.aspx?display=list2.opml
     */
    public void testDetectOPMLLinkExtra()
    {
        Element outline;

         // <outline title="ext" type="link" url="http://localhost/outline.aspx?display=a.opml"/>
        outline = new Element("outline");
        outline.setAttribute("title", "ext");
        outline.setAttribute("type", "link");
        outline.setAttribute("url", "http://localhost/outline.aspx?disply=a.opml");

        int type = importer.getOutlineType(outline);
        assertEquals("Should be external outline.", Importer.OUTLINE_TYPE_GUIDE_LINK, type);
    }

    /**
     * Detecting of OPML links.
     */
    public void testDetectOPMLLink()
    {
        Element outline;

         // <outline title="ext" type="link" xmlUrl="http://localhost/outline.opml" />
        outline = new Element("outline");
        outline.setAttribute("title", "ext");
        outline.setAttribute("type", "link");
        outline.setAttribute("xmlurl", "http://localhost/outline.opml");

        int type = importer.getOutlineType(outline);
        assertEquals("Should be external outline.", Importer.OUTLINE_TYPE_GUIDE_LINK, type);

         // <outline title="ext" type="link" url="http://localhost/outline.opml" />
        outline = new Element("outline");
        outline.setAttribute("title", "ext");
        outline.setAttribute("type", "link");
        outline.setAttribute("url", "http://localhost/outline.opml");

        type = importer.getOutlineType(outline);
        assertEquals("Should be external outline.", Importer.OUTLINE_TYPE_GUIDE_LINK, type);

         // <outline title="ext" type="link" url="http://localhost/outline.opml?somequery=1#a" />
        outline = new Element("outline");
        outline.setAttribute("title", "ext");
        outline.setAttribute("type", "link");
        outline.setAttribute("url", "http://localhost/outline.opml?somequery=1#a");

        type = importer.getOutlineType(outline);
        assertEquals("Should be external outline.", Importer.OUTLINE_TYPE_GUIDE_LINK, type);

        // <outline title="ext" type="link" xmlUrl="http://localhost/outline.xml" />
       outline = new Element("outline");
       outline.setAttribute("title", "ext");
       outline.setAttribute("type", "link");
       outline.setAttribute("xmlurl", "http://localhost/outline.xml");

       type = importer.getOutlineType(outline);
       assertEquals("Should be external outline.", Importer.OUTLINE_TYPE_INVALID, type);
    }

    /**
     * Tests importing direct feeds with URL's, like this:
     * <ul>
     *  <li><code>feed://something/</code></li>
     *  <li><code>feed:http://something/</code></li>
     *  <li><code>feed://http://something/</code></li>
     * </ul>
     */
    public void testImportingDirectFeedsWithFeedPrefix()
    {
        Element outline;
        DirectOPMLFeed feed;

         // <outline title="2RSS" xmlUrl="feed://www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "feed://www.2rss.com/rss20.php");

        feed = Importer.createFeed(outline, null);
        assertNotNull(feed);
        assertEquals("http://www.2rss.com/rss20.php", feed.getXmlURL());

         // <outline title="2RSS" xmlUrl="feed:http//www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "feed:http://www.2rss.com/rss20.php");

        feed = Importer.createFeed(outline, null);
        assertNotNull(feed);
        assertEquals("http://www.2rss.com/rss20.php", feed.getXmlURL());

         // <outline title="2RSS" xmlUrl="feed://http//www.2rss.com/rss20.php" />
        outline = new Element("outline");
        outline.setAttribute("title", "2RSS");
        outline.setAttribute("xmlurl", "feed://http://www.2rss.com/rss20.php");

        feed = Importer.createFeed(outline, null);
        assertNotNull(feed);
        assertEquals("http://www.2rss.com/rss20.php", feed.getXmlURL());
    }

    // Reading Lists -------------------------------------------------------------------------------

    /**
     * Importing empty Reading list.
     */
    public void testRLImportEmptyReadingList()
    {
        OPMLGuide guide = new OPMLGuide("", "", false, null, null, false, 0, false, false, false);

        Element readingListOutline = new Element("outline");
        readingListOutline.setAttribute("type", "list");
        readingListOutline.setAttribute("text", "a");
        readingListOutline.setAttribute("xmlurl", "file://readingList");

        importer.parseReadingList(readingListOutline, guide);

        OPMLReadingList[] lists = guide.getReadingLists();
        assertEquals(1, lists.length);
        assertEquals("a", lists[0].getTitle());
        assertEquals("file://readingList", lists[0].getURL());
    }

    /**
     * Importing reading list with feeds.
     */
    public void testRLImportReadingList()
    {
        OPMLGuide guide = new OPMLGuide("", "", false, null, null, false, 0, false, false, false);

        Element readingListOutline = new Element("outline");
        readingListOutline.setAttribute("type", "list");
        readingListOutline.setAttribute("text", "a");
        readingListOutline.setAttribute("xmlurl", "file://readingList");

        Element feedOutline1 = new Element("outline");
        readingListOutline.addContent(feedOutline1);
        feedOutline1.setAttribute("type", "rss");
        feedOutline1.setAttribute("text", "1");
        feedOutline1.setAttribute("xmlUrl", "file://test");
        Element feedOutline2 = new Element("outline");
        readingListOutline.addContent(feedOutline2);
        feedOutline2.setAttribute("type", "rss");
        feedOutline2.setAttribute("text", "2");
        feedOutline2.setAttribute("xmlurl", "file://test2");

        importer.parseReadingList(readingListOutline, guide);

        OPMLReadingList[] lists = guide.getReadingLists();
        assertEquals(1, lists.length);

        OPMLReadingList list = lists[0];
        List feeds = list.getFeeds();
        assertEquals(2, feeds.size());
    }

    /**
     * Tests detecting reading list.
     */
    public void testRLDetect()
    {
        Element readingListOutline = new Element("outline");
        readingListOutline.setAttribute("type", "list");
        readingListOutline.setAttribute("text", "a");
        readingListOutline.setAttribute("xmlurl", "file://readingList");

        int type = importer.getOutlineType(readingListOutline);
        assertEquals(Importer.OUTLINE_TYPE_READING_LIST, type);
    }

    /**
     * Tests collecting objects for guide.
     */
    public void testRLCollectObjects()
    {
        OPMLGuide guide = new OPMLGuide("", "", false, null, null, false, 0, false, false, false);
        Element guideOutline = new Element("outline");

        Element readingListOutline = new Element("outline");
        guideOutline.addContent(readingListOutline);
        readingListOutline.setAttribute("type", "list");
        readingListOutline.setAttribute("text", "a");
        readingListOutline.setAttribute("xmlurl", "file://readingList");

        Element feedOutline1 = new Element("outline");
        readingListOutline.addContent(feedOutline1);
        feedOutline1.setAttribute("type", "rss");
        feedOutline1.setAttribute("text", "1");
        feedOutline1.setAttribute("xmlUrl", "file://test");
        Element feedOutline2 = new Element("outline");
        readingListOutline.addContent(feedOutline2);
        feedOutline2.setAttribute("type", "rss");
        feedOutline2.setAttribute("text", "2");
        feedOutline2.setAttribute("xmlUrl", "file://test2");

        importer.collectObjects(guideOutline, guide);

        OPMLReadingList[] lists = guide.getReadingLists();
        assertEquals(1, lists.length);
        assertEquals(2, lists[0].getFeeds().size());
    }

    /**
     * Tests sample OPML parsing.
     */
    public void testRLFromString()
        throws ImporterException
    {
        String opml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<opml version=\"1.1\"><head><title>BlogBridge Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Guide\" icon=\"cg.23.icon\">" +
                "<outline type=\"list\" text=\"ReadingList\" xmlUrl=\"file://link\">" +
                    "<outline type=\"rss\" text=\"Feed\" viewtype=\"0\" xmlUrl=\"http://lh/rss/akma.xml\" htmlUrl=\"http://akma.disseminary.org/\" />" +
                "</outline>" +
                "<outline type=\"rss\" text=\"Alex test\" viewtype=\"0\" xmlUrl=\"http://lh/rss/b.xml\" htmlUrl=\"http://localhost/\" />" +
            "</outline>" +
            "</body></opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(1, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals(1, guide.getReadingLists().length);
        assertEquals(1, guide.getFeeds().size());
    }

    /**
     * Flattening the list with one top-level guide and various sub-guides.
     */
    public void testCleverFlattening()
        throws ImporterException
    {
        String opml =
            "<opml version=\"1.1\"><head><title>Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Subscriptions\">" +
                "<outline text=\"Guide 1\">" +
                    "<outline type=\"rss\" text=\"Feed 1\" xmlUrl=\"http://xml\" htmlUrl=\"http://localhost/\" />" +
                "</outline>" +
                "<outline type=\"rss\" text=\"Feed 2\" xmlUrl=\"http://xml2\" htmlUrl=\"http://localhost/\" />" +
            "</outline>" +
            "</body></opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(2, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals("Subscriptions", guide.getTitle());
        assertEquals(1, guide.getFeeds().size());

        OPMLGuide guide2 = guides[1];
        assertEquals("Guide 1", guide2.getTitle());
        assertEquals(1, guide.getFeeds().size());
    }

    /**
     * When there are two guides on the top level, no clever flattening required.
     */
    public void testCleverNonFlattening()
        throws ImporterException
    {
        String opml =
            "<opml version=\"1.1\"><head><title>Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Subscriptions\">" +
                "<outline text=\"Guide 1\">" +
                    "<outline type=\"rss\" text=\"Feed 1\" xmlUrl=\"http://xml\" htmlUrl=\"http://localhost/\" />" +
                "</outline>" +
                "<outline type=\"rss\" text=\"Feed 2\" xmlUrl=\"http://xml2\" htmlUrl=\"http://localhost/\" />" +
            "</outline>" +
            "<outline text=\"Subscriptions 2\">" +
                "<outline type=\"rss\" text=\"Feed 3\" xmlUrl=\"http://xml3\" htmlUrl=\"http://localhost/\" />" +
            "</outline>" +
            "</body></opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(2, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals("Subscriptions", guide.getTitle());
        assertEquals(2, guide.getFeeds().size());

        OPMLGuide guide2 = guides[1];
        assertEquals("Subscriptions 2", guide2.getTitle());
        assertEquals(1, guide2.getFeeds().size());
    }

    /**
     * Importing state of publication.
     */
    public void testImportingPublicationState()
        throws ImporterException
    {
        String opml =
            "<opml version=\"1.1\"><head><title>BlogBridge Feeds</title></head>" +
            "<body>" +
                "<outline text=\"Feeds\" icon=\"cg.default.icon\" pubEnabled=\"true\" pubTitle=\"Super List\" pubTags=\"a b c\" pubPublic=\"true\" pubRating=\"1\">" +
                    "<outline type=\"rss\" text=\"noizZze\" readArticles=\"777937b4\" viewtype=\"0\" xmlUrl=\"http://feeds.feedburner.com/noizzze\" htmlUrl=\"http://blog.noizeramp.com\" />" +
                "</outline>" +
            "</body>" +
            "</opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(1, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals("Feeds", guide.getTitle());
        assertEquals(1, guide.getFeeds().size());
        assertTrue(guide.isPublishingEnabled());
        assertTrue(guide.isPublishingPublic());
        assertEquals("Super List", guide.getPublishingTitle());
        assertEquals("a b c", guide.getPublishingTags());
        assertEquals(1, guide.getPublishingRating());
    }

    /**
     * Tests importing publishing information.
     */
    public void testPublishingInfo()
    {
        Element outline;
        OPMLGuide guide;

        // <outline text="guide" pubEnabled="true" pubTitle="a" pubTags="b,c"/>
        outline = new Element("outline");
        outline.setAttribute("text", "guide");

        // We enter them lowcase because they will be lowercased before parsing
        outline.setAttribute("pubenabled", "true");
        outline.setAttribute("pubtitle", "a");
        outline.setAttribute("pubtags", "b,c");

        guide = importer.createGuide(outline);
        assertTrue(guide.isPublishingEnabled());
        assertEquals("a", guide.getPublishingTitle());
        assertEquals("b,c", guide.getPublishingTags());
        assertEquals(0, guide.getPublishingRating());

        // <outline text="guide"/>
        outline = new Element("outline");
        outline.setAttribute("text", "guide");

        guide = importer.createGuide(outline);
        assertFalse(guide.isPublishingEnabled());
        assertNull(guide.getPublishingTitle());
        assertNull(guide.getPublishingTags());
    }

    /**
     * Tests importing publishing information.
     */
    public void testPublishingInfoBBNS()
    {
        Element outline;
        OPMLGuide guide;

        // <outline text="guide" bb:pubEnabled="true" bb:pubTitle="a" bb:pubTags="b,c"/>
        outline = new Element("outline");
        outline.setAttribute("text", "guide");

        // We enter them lowcase because they will be lowercased before parsing
        outline.setAttribute("pubenabled", "true", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("pubtitle", "a", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("pubtags", "b,c", FormatConstants.BB_NAMESPACE);
        outline.setAttribute("pubpublic", "true", FormatConstants.BB_NAMESPACE);

        importer.setBbNs(FormatConstants.BB_NAMESPACE);
        guide = importer.createGuide(outline);
        assertTrue(guide.isPublishingEnabled());
        assertTrue(guide.isPublishingPublic());
        assertEquals("a", guide.getPublishingTitle());
        assertEquals("b,c", guide.getPublishingTags());

        // <outline text="guide"/>
        outline = new Element("outline");
        outline.setAttribute("text", "guide");

        guide = importer.createGuide(outline);
        assertFalse(guide.isPublishingEnabled());
        assertFalse(guide.isPublishingPublic());
        assertNull(guide.getPublishingTitle());
        assertNull(guide.getPublishingTags());
    }

    /**
     * Ensures that the method has public modifier and it's non-static.
     */
    public void testCreateReaderForURLDefinition()
        throws NoSuchMethodException
    {
        Method crfu = Importer.class.getMethod("createReaderForURL", URL.class);
        assertEquals("Method should be public and non-static.",
            Modifier.PUBLIC, crfu.getModifiers());
    }

    /**
     * Tests importing old format.
     */
    public void testImportingOldFormat()
        throws ImporterException
    {
        String opml =
            "<opml version=\"1.1\"><head><title>Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Subscriptions\">" +
                "<outline type=\"rss\" text=\"Feed 1\" xmlUrl=\"http://xml\" htmlUrl=\"http://localhost/\" rating=\"1\"/>" +
            "</outline>" +
            "</body></opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(1, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals("Subscriptions", guide.getTitle());
        assertEquals(1, guide.getFeeds().size());

        DirectOPMLFeed feed = (DirectOPMLFeed)guide.getFeeds().get(0);
        assertEquals(1, feed.getRating());
    }

    /**
     * Tests importing format 1.
     */
    public void testImportingFormat1()
        throws ImporterException
    {
        String opml =
            "<bb:opml version=\"1.1\" xmlns:bb=\"http://blogbridge.com/ns/2006/opml\"><head><title>Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Subscriptions\">" +
                "<outline type=\"rss\" text=\"Feed 1\" xmlUrl=\"http://xml\" htmlUrl=\"http://localhost/\" bb:rating=\"1\"/>" +
            "</outline>" +
            "</body></bb:opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(1, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals("Subscriptions", guide.getTitle());
        assertEquals(1, guide.getFeeds().size());
        assertFalse(guide.isNotificationsAllowed());

        DirectOPMLFeed feed = (DirectOPMLFeed)guide.getFeeds().get(0);
        assertEquals(1, feed.getRating());
    }

    /**
     * Tests importing format 2.
     */
    public void testImportingFormat2()
        throws ImporterException
    {
        String opml =
            "<opml version=\"1.1\" xmlns:bb=\"http://blogbridge.com/ns/2006/opml\"><head><title>Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Subscriptions\" bb:notificationsAllowed=\"true\">" +
                "<outline type=\"rss\" text=\"Feed 1\" xmlUrl=\"http://xml\" htmlUrl=\"http://localhost/\" bb:rating=\"1\" bb:readArticles=\"aaa\" bb:pinnedArticles=\"bbb\"/>" +
            "</outline>" +
            "</body></opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(1, guides.length);

        OPMLGuide guide = guides[0];
        assertEquals("Subscriptions", guide.getTitle());
        assertEquals(1, guide.getFeeds().size());
        assertTrue(guide.isNotificationsAllowed());

        DirectOPMLFeed feed = (DirectOPMLFeed)guide.getFeeds().get(0);
        assertEquals(1, feed.getRating());
        assertEquals("aaa", feed.getReadArticlesKeys());
        assertEquals("bbb", feed.getPinnedArticlesKeys());
    }

    /**
     * Tests importing OPML 2.0.
     */
    public void testImportingOPML2_0()
        throws ImporterException
    {
        String opml =
            "<opml version=\"2.0\" xmlns:bb=\"http://blogbridge.com/ns/2006/opml\"><head><title>Feeds</title></head>" +
            "<body>" +
            "<outline text=\"Subscriptions\" bb:notificationsAllowed=\"true\">" +
                "<outline type=\"rss\" text=\"Feed 1\" xmlUrl=\"http://xml\" htmlUrl=\"http://localhost/\" " +
                    "bb:rating=\"1\" bb:readArticles=\"aaa\" bb:pinnedArticles=\"bbb\"/>" +
            "</outline>" +
            "</body></opml>";

        OPMLGuide[] guides = importer.processFromString(opml, false).getGuides();
        assertEquals(1, guides.length);
    }
}
