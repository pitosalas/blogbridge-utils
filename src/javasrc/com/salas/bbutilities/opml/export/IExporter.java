package com.salas.bbutilities.opml.export;

import org.jdom.Document;
import com.salas.bbutilities.opml.objects.OPMLGuide;
import com.salas.bbutilities.opml.objects.OPMLGuideSet;

/**
 * 
 */
public interface IExporter
{
    Document export(OPMLGuide guide);

    Document export(OPMLGuide[] guides, String aTitle);

    Document export(OPMLGuideSet set);
}
