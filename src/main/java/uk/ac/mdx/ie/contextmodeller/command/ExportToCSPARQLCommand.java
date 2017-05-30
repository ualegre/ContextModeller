/*
 * Copyright 2015 The ContextModeller Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.mdx.ie.contextmodeller.command;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;
import uk.ac.mdx.ie.contextmodeller.io.AbstractModelWriter;
import uk.ac.mdx.ie.contextmodeller.io.CSPARQLWriter;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

/**
 * Implementation of the IModuleContextualCommand interface. <br>
 * The module contextual commands are displayed in the contextual menu and in
 * the specific toolbar of each module property page. <br>
 * The developer may inherit the DefaultModuleContextualCommand class which
 * contains a default standard contextual command implementation.
 *
 */
public class ExportToCSPARQLCommand extends DefaultModuleCommandHandler {

	private ILogService logService;

	/**
	 * Constructor.
	 */
	public ExportToCSPARQLCommand() {
		super();
		// services logs
		this.logService = ContextModellerModule.getInstance().getModuleContext().getModelioServices().getLogService();
	}

	/**
	 * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#accept(java.util.List,
	 *      org.modelio.api.module.IModule)
	 */
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		// Check that there is only one selected element
		return selectedElements.size() == 1;
	}

	/**
	 * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#actionPerformed(java.util.List,
	 *      org.modelio.api.module.IModule)
	 */
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {

		this.logService
				.info("HelloWorldCommand - actionPerformed(...)");

		ModelElement modelelt = (ModelElement) selectedElements.get(0);
		// MessageDialog.openInformation(null, "Hello", modelelt.getName());

		FileDialog dialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		dialog.setFilterNames(new String[] { "Text Files", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.txt", "*.*" }); // Windows
		// wild
		// cards
		dialog.setFilterPath(System.getProperty("user.home") + "/Desktop"); // Windows
																			// path
		dialog.setFileName("rules.txt");
		String fileLocation = dialog.open();

		AbstractModelWriter csparqlGen = new CSPARQLWriter();

		if (! modelelt.isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_MODEL)) {
			MessageDialog.openError(null, "Error", "Element is not a Context Model");
			return;
		}
		File newFile = new File(fileLocation);

		if (newFile.exists()) {
			if (! MessageDialog.openConfirm(null, "Confirm Export", "File already exists.\nDo you want to replace it?\n" )) {
				return;
			}
		}

		csparqlGen.setModel( (Package) modelelt.getCompositionOwner());
		csparqlGen.writeToFile(newFile);
		MessageDialog.openInformation(null, "Model Exported", "Model exported to C-SPARQL at:\n" + fileLocation);

	}

}
