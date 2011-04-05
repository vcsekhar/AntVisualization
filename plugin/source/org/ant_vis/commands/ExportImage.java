package org.ant_vis.commands;

import java.io.File;

import org.ant_vis.main.Activator;
import org.ant_vis.views.AntView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ExportImage extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
         Activator.debug("ExportImage.execute");

        // locate the view and perform action
        final AntView antView = AntView.getInstance(event);
        if (antView == null) {
            Activator.debug("AntView not found");
            return null;

        }
        
        if (antView.hasImage()) {
            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            while (true) {
                // prompt user for export location
                final FileDialog exportDialog = new FileDialog(shell, SWT.SAVE);
                exportDialog.setText("Export image as:");
                final String[] filterExt = { "*.png" };
                exportDialog.setFilterExtensions(filterExt);
                final String exportPath = exportDialog.open();
                Activator.debug("ExportImage.execute: path: " + exportPath);
                if (exportPath == null) {
                    break;
                }
                // obtain confirmation if file already exists
                final File exportFile = new File(exportPath);
                if (exportFile.exists()) {
                    final MessageBox exportConfirmation = new MessageBox(exportDialog.getParent(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
                    exportConfirmation.setText("Confirm Overwrite");
                    exportConfirmation.setMessage(exportFile + " already exists. Do you want to replace it?");
                    if (exportConfirmation.open() != SWT.YES) {
                        continue;
                    }
                }
                antView.exportImage(exportPath);
                break;
            }
        }

        return null;
    }

}
