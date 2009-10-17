package com.salas.bbutilities.opml;

import com.salas.bbutilities.opml.export.Exporter;
import com.salas.bbutilities.opml.objects.*;
import com.salas.bbutilities.opml.utils.Transformation;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * This suite contains general module acceptance tests.
 */
public class TestGeneralAcceptance extends TestCase
{
    /**
     * Tests exporting and importing information.
     * @throws ImporterException in case of error.
     */
    public void testExportImport()
        throws ImporterException
    {
        // Create guide for export
        DirectOPMLFeed df1 = new DirectOPMLFeed("A", "B", "C", 1, "D", "L", 2, "E", "F", "G", "I", "J", "K", false, 1, false, 1, null, 0);
        QueryOPMLFeed  qf1 = new QueryOPMLFeed("H", 1, "I", "J", "K", "L", 2, 3, 1, true, 2, true, 0);
        SearchOPMLFeed sf1 = new SearchOPMLFeed("M", "N", 1, 2, 3, true, 1, false, 0);

        qf1.setUpdatePeriod(1l);

        ArrayList<DefaultOPMLFeed> feedsList = new ArrayList<DefaultOPMLFeed>(2);
        feedsList.add(df1);
        feedsList.add(qf1);
        feedsList.add(sf1);

        OPMLGuide guide = new OPMLGuide("A", "B", true, null, null, false, 1, false, false, false);
        guide.setFeeds(feedsList);

        // Export data
        Exporter exporter = new Exporter(true);
        String opml = Transformation.documentToString(exporter.export(guide));

        // Import data
        Importer importer = new Importer();
        OPMLGuide[] guides = importer.processFromString(opml, true).getGuides();

        assertEquals(1, guides.length);
        assertEquals(guide, guides[0]);
        assertEquals(df1, guides[0].getFeeds().get(0));
        assertEquals(qf1, guides[0].getFeeds().get(1));
        assertEquals(sf1, guides[0].getFeeds().get(2));
    }
}
