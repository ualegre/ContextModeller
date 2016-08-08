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

package uk.ac.mdx.ie.contextmodeller.command.menu.element;

import java.util.List;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.Profile;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.util.CMFactory;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class NewContextState extends NewModelElement {


	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		if (super.accept(selectedElements, module)) {
			return (selectedElements.size() > 0)
					&& (Utils.accept(selectedElements.get(0)));
		}
		return false;
	}

	@Override
	public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
		MObject selectedElt = selectedElements.get(0);
		if ((!(selectedElt instanceof Profile))
				&& (!(selectedElt instanceof IModule))) {
			if ((selectedElt instanceof Class)) {
				if (((Class) selectedElt).isStereotyped(Utils.CONTEXT_MODELLER,
						Utils.CONTEXT_SOURCE));
			}
		} else
			return false;

		return selectedElt.getStatus().isModifiable();
	}

	@Override
	protected void createModelElement(List<MObject> elements, IModelingSession module) {
		MObject element = elements.get(0);

		if (element != null) {
			CMFactory.createAndAddState((Package) element);
		}
	}

}
