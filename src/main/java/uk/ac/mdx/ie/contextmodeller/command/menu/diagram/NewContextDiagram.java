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

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.infrastructure.Profile;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;
import uk.ac.mdx.ie.contextmodeller.util.CMFactory;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class NewContextDiagram extends NewDiagram {

	@Override
	protected StaticDiagram createDiagram(List<MObject> elements, IModelingSession session) {

		StaticDiagram diagram = CMFactory.createContextDiagram(elements, session, Utils.CONTEXT_MODEL);

		diagram = (StaticDiagram) addStyle(diagram, Utils.CONTEXT_MODELLER);

		return diagram;

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
