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

package uk.ac.mdx.ie.contextmodeller.command.menu.diagram;

import java.util.List;

import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.diagram.IDiagramHandle;
import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.diagram.dg.IDiagramDG;
import org.modelio.api.modelio.diagram.style.IStyleHandle;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.metamodel.diagrams.AbstractDiagram;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.i18n.I18nMessageService;

public abstract class NewDiagram extends DefaultModuleCommandHandler{

	@Override
	public void actionPerformed(List<MObject> elements, IModule arg1) {
		IModelingSession session = Modelio.getInstance().getModelingSession();
		ITransaction transaction = session
			.createTransaction(I18nMessageService.getString("Info.Session.Create", new String[] { "Activity" }));

		StaticDiagram diagram = createDiagram(elements, session);

		if (null != diagram)
		    Modelio.getInstance().getEditionService().openEditor(diagram);
		transaction.commit();

	}

	protected abstract StaticDiagram createDiagram(List<MObject> elements, IModelingSession module);

	protected static AbstractDiagram addStyle(AbstractDiagram diagram, String name) {
		if (diagram != null) {
			IDiagramService ds = Modelio.getInstance()
					.getDiagramService();
			IDiagramHandle handler = ds.getDiagramHandle(diagram);
			IDiagramDG dg = handler.getDiagramNode();

			for (IStyleHandle style : ds.listStyles()) {
				if (style.getName().equals(name)) {
					dg.setStyle(style);
					break;
				}
			}

			handler.save();
			handler.close();

		}

		return diagram;
	}

	@Override
	public abstract boolean accept(List<MObject> elements, IModule module);


}
