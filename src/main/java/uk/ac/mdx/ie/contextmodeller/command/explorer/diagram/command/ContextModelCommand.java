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

package uk.ac.mdx.ie.contextmodeller.command.explorer.diagram.command;

import java.util.List;

import org.modelio.api.diagram.IDiagramHandle;
import org.modelio.api.diagram.IDiagramService;
import org.modelio.api.diagram.dg.IDiagramDG;
import org.modelio.api.diagram.style.IStyleHandle;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.ITransaction;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.factory.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Profile;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.i18n.I18nMessageService;
import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class ContextModelCommand extends DefaultModuleCommandHandler {

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		Modelio modelio = Modelio.getInstance();
		IModelingSession session = modelio.getModelingSession();
		StaticDiagram diagram = null;
		String name = I18nMessageService
				.getString("Ui.Command.BlockDiagramExplorerCommand.Label");
		try {
			ITransaction transaction = session
					.createTransaction(I18nMessageService.getString(
							"Info.Session.Create", new String[] { "Activity" }));
			Throwable localThrowable3 = null;
			try {
				diagram = session.getModel().createStaticDiagram(name,
						(ModelElement) selectedElements.get(0),
						Utils.CONTEXT_MODELLER, Utils.CONTEXT_MODEL);
				Utils.setUMLFreeName(diagram, name);

				if (diagram != null) {
					IDiagramService ds = Modelio.getInstance()
							.getDiagramService();
					IDiagramHandle handler = ds.getDiagramHandle(diagram);
					IDiagramDG dg = handler.getDiagramNode();

					for (IStyleHandle style : ds.listStyles()) {
						if (style.getName().equals("contextmodeller")) {
							dg.setStyle(style);
							break;
						}
					}

					handler.save();
					handler.close();

					Modelio.getInstance().getEditionService()
							.openEditor(diagram);
				}

				transaction.commit();
			} catch (Throwable localThrowable1) {
				localThrowable3 = localThrowable1;
				throw localThrowable1;
			} finally {
				if (transaction != null)
					if (localThrowable3 != null)
						try {
							transaction.close();
						} catch (Throwable localThrowable2) {
							localThrowable3.addSuppressed(localThrowable2);
						}
					else
						transaction.close();
			}
		} catch (ExtensionNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {

		ContextModellerModule.logger.error("ContextModelCommand:accept()");
		if ((selectedElements != null) && (selectedElements.size() == 1)) {
			MObject selectedElt = selectedElements.get(0);
			if (selectedElt != null)
				if ((!(selectedElt instanceof Package))
						|| ((selectedElt instanceof Profile))) {
					if (!(selectedElt instanceof Class))
						return selectedElt.getStatus().isModifiable();
					if (!((Class) selectedElt).isStereotyped(
							Utils.CONTEXT_MODELLER, Utils.CONTEXT_SOURCE))
						return selectedElt.getStatus().isModifiable();
				}
		}
		return false;
	}

}
