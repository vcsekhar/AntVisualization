package org.ant_vis.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ant_vis.main.Activator;
import org.ant_vis.model.AntDepModel;
import org.ant_vis.model.AntFigureProvider;
import org.ant_vis.model.AntProvider;
import org.ant_vis.model.AntTarget;
import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.model.AntModel;
import org.eclipse.ant.internal.ui.model.AntTargetNode;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

/**
 * Provides the Ant visualization part.
 * 
 */
public class AntView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.ant_vis.views.AntView";

    // instance variables
    private IMemento memento;
    private final static String LAYOUT_NAME_KEY = "LAYOUT_NAME";

    private IFile xmlFile;
    private AntDepModel antDepModel;

    // visual components
    private Label fileLabel;
    private AntGraphViewer antGraphViewer;
    private Graph graph;
    private Viewport viewport;
    @SuppressWarnings("restriction")
    private ZoomManager zoomManager;
    private Point canvasSize;
    private String layoutName;

    // useful constants
    private final double zoomScale = 1.05;
    private final double canvasScale = 1.10;
    private final double moveScale = 0.03;

    private AntEditorTracker antEditorTracker;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @SuppressWarnings("restriction")
    @Override
    public void createPartControl(final Composite parent) {
        Activator.debug("AntView.createPartControl");

        // make the entity model
        antDepModel = new AntDepModel();

        // make visual components
        final GridLayout gridLayout = new GridLayout();
        parent.setLayout(gridLayout);

        fileLabel = new Label(parent, SWT.NONE);

        final GridData labelGridData = new GridData();
        labelGridData.horizontalAlignment = GridData.FILL;
        labelGridData.grabExcessHorizontalSpace = true;
        fileLabel.setLayoutData(labelGridData);

        final GridData graphGridData = new GridData();
        graphGridData.horizontalAlignment = GridData.FILL;
        graphGridData.verticalAlignment = GridData.FILL;
        graphGridData.grabExcessHorizontalSpace = true;
        graphGridData.grabExcessVerticalSpace = true;

        // construct the graph viewer and extract bits of it
        antGraphViewer = new AntGraphViewer(parent, SWT.NONE);
        graph = antGraphViewer.getGraphControl();
        graph.setLayoutData(graphGridData);
        viewport = graph.getViewport();
        zoomManager = new ZoomManager(graph.getRootLayer(), viewport);

        // restore persisted state
        if (memento != null) {
            layoutName = memento.getString(LAYOUT_NAME_KEY);
        }
        if (layoutName == null) {
            layoutName = AntGraphViewer.DEFAULT_LAYOUT_NAME;
        }
        Activator.debug("AntView.createPartControl: layoutName: " + layoutName);

        // register that we're providing the view input context
        final IContextService contextService = (IContextService) getSite().getService(IContextService.class);
        contextService.activateContext(ID);

        // add context menu
        final MenuManager menuMgr = new MenuManager();
        menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        getSite().registerContextMenu(menuMgr, antGraphViewer);

        final Control control = antGraphViewer.getControl();
        final Menu menu = menuMgr.createContextMenu(control);
        control.setMenu(menu);

        // add mouse event handler
        graph.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                openSelectedNode();
            }
        });

        // take focus
        graph.setFocus();

        // build the graph
        antGraphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
        antGraphViewer.setLabelProvider(new AntFigureProvider(antDepModel, antGraphViewer));
        antGraphViewer.setContentProvider(new AntProvider());
        antGraphViewer.setLayoutName(layoutName);

        // register for Ant editor events
        antEditorTracker = new AntEditorTracker(this, memento);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        graph.setFocus();
    }

    @Override
    public void dispose() {
        if (antEditorTracker != null) {
            antEditorTracker.close();
        }
        super.dispose();
    }

    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
        memento.putString(LAYOUT_NAME_KEY, layoutName);
        antEditorTracker.saveState(memento);
    }

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException {
        super.init(site, memento);
        this.memento = memento;
    }

    /**
     * @param xmlFile
     */
    public void showFile(final IFile xmlFile) {
        this.xmlFile = xmlFile;
        reload();
    }

    public void reload() {
        if (xmlFile == null) {
            return;
        }
        fileLabel.setText(xmlFile.getProject().getName() + " : " + xmlFile.getProjectRelativePath().toPortableString());
        InputStream xmlStream = null;
        try {
            xmlStream = xmlFile.getContents();
            antDepModel.parseXML(xmlStream);
        } catch (final Exception e) {
            // report problem
            final String message = "Exception raised";
            final IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, e);
            StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
            return;
        } finally {
            try {
                xmlStream.close();
            } catch (IOException e) {
            }
        }
        populateGraph();
    }

    private void populateGraph() {
        antGraphViewer.setInput(antDepModel);
    }

    public void redraw() {
        if (xmlFile == null) {
            return;
        }
        graph.applyLayout();
    }

    @SuppressWarnings("restriction")
    public void zoomIn() {
        zoomManager.setZoom(zoomManager.getZoom() * zoomScale);

    }

    @SuppressWarnings("restriction")
    public void zoomOut() {
        zoomManager.setZoom(zoomManager.getZoom() / zoomScale);
    }

    public void scaleCanvas(final int xSign, final int ySign) {
        if (canvasSize == null) {
            canvasSize = graph.getSize();
            if (canvasSize == null) {
                return;
            }
        }

        final int minSize = 100;
        if (xSign != 0) {
            final int xNew = (int) ((xSign > 0) ? canvasSize.x * canvasScale : canvasSize.x / canvasScale);
            if (xNew > minSize) {
                canvasSize.x = xNew;
            }
        }
        if (ySign != 0) {
            final int yNew = (int) ((ySign > 0) ? canvasSize.y * canvasScale : canvasSize.y / canvasScale);
            if (yNew > minSize) {
                canvasSize.y = yNew;
            }
        }
        Activator.debug("canvasSize: " + canvasSize);
        graph.setPreferredSize(canvasSize.x, canvasSize.y);
        graph.applyLayout();
    }

    public void moveCanvas(final int xSign, final int ySign) {
        final RangeModel xModel = viewport.getHorizontalRangeModel();
        final RangeModel yModel = viewport.getVerticalRangeModel();

        final int xMax = xModel.getMaximum() - xModel.getExtent();
        final int yMax = yModel.getMaximum() - yModel.getExtent();

        final int xOld = xModel.getValue();
        final int yOld = yModel.getValue();

        final int xDelta = Math.max((int) (xMax * moveScale), 1);
        final int yDelta = Math.max((int) (yMax * moveScale), 1);

        final int xNew = Math.min(Math.max(xOld + xSign * xDelta, 0), xMax);
        final int yNew = Math.min(Math.max(yOld + (-ySign) * yDelta, 0), yMax);
        viewport.setViewLocation(xNew, yNew);
    }

    public void deleteSelection() {
        antDepModel.deleteSelection(antGraphViewer.getLastSelection());
        antGraphViewer.deleteSelection();
        // graph.applyLayout();
    }

    public void descriptionsToggle() {
        antDepModel.toggleDescriptions();
        populateGraph();
    }

    @SuppressWarnings("restriction")
    public void openSelectedNode() {
        if (xmlFile == null) {
            return;
        }

        // get first selected target if present
        final Set<AntTarget> selectedTargets = antGraphViewer.getLastSelection().getTargets();
        final AntTarget antTarget = selectedTargets.isEmpty() ? null : selectedTargets.iterator().next();

        try {
            final Map<String, Object> attributes = new HashMap<String, Object>();
            // map.put(IWorkbenchPage.EDITOR_ID_ATTR,
            // "org.eclipse.ui.DefaultTextEditor");
            attributes.put(IMarker.LINE_NUMBER, new Integer(40));
            final IMarker marker = xmlFile.createMarker(IMarker.TEXT);
            marker.setAttributes(attributes);
            final IWorkbenchPage page = getSite().getPage();
            final IEditorPart editor = IDE.openEditor(page, marker);
            if (editor instanceof AntEditor && antTarget != null) {
                final AntEditor antEditor = (AntEditor) editor;
                final AntModel antModel = antEditor.getAntModel();
                final AntTargetNode antTargetNode = antModel.getTargetNode(antTarget.getName());
                antEditor.setSelection(antTargetNode, true);
            }
            marker.delete();
        } catch (final CoreException e) {
            e.printStackTrace();
        }
    }

    public void trackFile(final IFile antFile) {
        if (!antFile.equals(xmlFile)) {
            showFile(antFile);
        }
    }

    public void untrackFile(final IFile antFile) {
        if (antFile.equals(xmlFile)) {
            clear();
        }
    }

    public void clear() {
        antDepModel.clear();
        fileLabel.setText("");
        populateGraph();
        xmlFile = null;
    }

    public void setLinkedState(final boolean linkedState) {
        antEditorTracker.setLinkedState(linkedState);
    }

    public void setLayoutName(final String layoutName) {
        this.layoutName = layoutName;
        antGraphViewer.setLayoutName(layoutName);
    }

    public boolean hasImage() {
        return (xmlFile != null);
    }

    public void exportImage(final String exportPath) {
        Assert.isNotNull(xmlFile);
        final Image image = getImage();
        final ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { image.getImageData() };
        loader.save(exportPath, SWT.IMAGE_PNG);
    }

    public void copyImage() {
        final Image image = getImage();
        final ImageTransfer imageTransfer = ImageTransfer.getInstance();
        final Clipboard clipboard = new Clipboard(Display.getDefault());
        clipboard.setContents(new Object[] { image.getImageData() }, new Transfer[] { imageTransfer });
    }

    private Image getImage() {
        final Dimension size = graph.getContents().getSize();
        final Image image = new Image(null, size.width, size.height);
        final GC gc = new GC(image);
        final SWTGraphics swtGraphics = new SWTGraphics(gc);
        final org.eclipse.draw2d.geometry.Point viewLocation = viewport.getViewLocation();
        final Rectangle bounds = graph.getContents().getBounds();
        swtGraphics.translate(-1 * bounds.x + viewLocation.x, -1 * bounds.y + viewLocation.y);
        viewport.paint(swtGraphics);
        gc.copyArea(image, 0, 0);
        gc.dispose();
        return image;
    }

    /**
     * Useful utility for event handlers to get this view.
     * 
     * @param event
     * @return
     * @throws ExecutionException 
     */
    public static AntView getInstance(final ExecutionEvent event) throws ExecutionException {
        final IWorkbenchPage workbenchPage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
        IViewPart findView = null; 
        try {
            findView = workbenchPage.showView(ID);
        } catch (final PartInitException e) {
            throw new ExecutionException("Could not create AntView", e);
        }
        Assert.isNotNull(findView);
        return (AntView) findView;
    }

}