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
// $Id: DataOPMLFeed.java,v 1.1 2007/04/30 13:26:35 spyromus Exp $
//

package com.salas.bbutilities.opml.objects;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Abstract data OPML feed class taking care of the data feed
 * properties.
 */
public abstract class DataOPMLFeed extends DefaultOPMLFeed
{
    private Long updatePeriod;

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
     */
    public DataOPMLFeed(String aTitle, int aLimit, int aRating, String aReadArticlesKeys, String aPinnedArticlesKeys,
                        int aViewType, boolean aViewModeEnabled, int aViewMode, Boolean aAscendingSorting)
    {
        super(aTitle, aLimit, aRating, aReadArticlesKeys, aPinnedArticlesKeys, aViewType, aViewModeEnabled, aViewMode,
                aAscendingSorting);
    }

    /**
     * Gets the update period.
     *
     * @return period.
     */
    public Long getUpdatePeriod()
    {
        return updatePeriod;
    }

    /**
     * Sets the update period.
     *
     * @param updatePeriod period.
     */
    public void setUpdatePeriod(Long updatePeriod)
    {
        this.updatePeriod = updatePeriod;
    }

    @Override
    public void write(Element outline, Namespace bbns, boolean extendedExport)
    {
        super.write(outline, bbns, extendedExport);

        if (extendedExport)
        {
            if (updatePeriod != null && updatePeriod > 0) outline.setAttribute(ATTR_FEED_UPDATE_PERIOD,
                    Long.toString(updatePeriod), bbns);
        }
    }

    /**
     * Compares two data feed objects.
     *
     * @param o other object.
     *
     * @return <code>TRUE</code> if equal.
     */
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DataOPMLFeed that = (DataOPMLFeed)o;

        return !(updatePeriod != null
                ? !updatePeriod.equals(that.updatePeriod) 
                : that.updatePeriod != null);
    }

    @Override
    public String toString()
    {
        return super.toString() + ", updatePeriod=" + updatePeriod;
    }
}
