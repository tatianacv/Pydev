/**
 * Copyright (c) 2014 by Brainwy Software LTDA. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package org.python.pydev.shared_ui.editor;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.python.pydev.overview_ruler.MinimapOverviewRulerPreferencesPage;
import org.python.pydev.overview_ruler.StyledTextWithoutVerticalBar;
import org.python.pydev.shared_core.log.Log;

public abstract class BaseSourceViewer extends ProjectionViewer implements ITextViewerExtensionAutoEditions {

    private boolean autoEditionsEnabled = true;

    public BaseSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
            boolean showAnnotationsOverview, int styles, ITabWidthProvider tabWidthProvider) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);

        VerticalIndentGuidesPainter verticalLinesPainter = new VerticalIndentGuidesPainter(
                getIndentGuide(tabWidthProvider));
        StyledText styledText = this.getTextWidget();
        verticalLinesPainter.setStyledText(styledText);
        styledText.addPaintListener(verticalLinesPainter);
        styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
    }

    @Override
    public boolean getAutoEditionsEnabled() {
        return autoEditionsEnabled;
    }

    @Override
    public void setAutoEditionsEnabled(boolean b) {
        this.autoEditionsEnabled = b;
    }

    @Override
    protected Layout createLayout() {
        //Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=438641
        return new RulerLayout(GAP_SIZE_1) {
            @Override
            protected void layout(Composite composite, boolean flushCache) {
                StyledText textWidget = getTextWidget();
                if (textWidget == null) {
                    Log.log("Error: textWidget is already null. SourceViewer: " + BaseSourceViewer.this + " control: "
                            + BaseSourceViewer.this.getControl());
                    return;
                }
                super.layout(composite, flushCache);
            }
        };
    }

    protected IVerticalLinesIndentGuideComputer getIndentGuide(ITabWidthProvider tabWidthProvider) {
        return new TextVerticalLinesIndentGuide(tabWidthProvider);
    }

    @Override
    protected StyledText createTextWidget(Composite parent, int styles) {
        StyledTextWithoutVerticalBar styledText = new StyledTextWithoutVerticalBar(parent, styles);

        if (!MinimapOverviewRulerPreferencesPage.getShowVerticalScrollbar()) {
            ScrollBar verticalBar = styledText.getVerticalBar();
            if (verticalBar != null) {
                verticalBar.setVisible(false);
            }
        }

        if (!MinimapOverviewRulerPreferencesPage.getShowHorizontalScrollbar()) {
            ScrollBar horizontalBar = styledText.getHorizontalBar();
            if (horizontalBar != null) {
                horizontalBar.setVisible(false);
            }
        }

        return styledText;
    }

}
