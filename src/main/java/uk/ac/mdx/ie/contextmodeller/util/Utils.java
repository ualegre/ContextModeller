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

package uk.ac.mdx.ie.contextmodeller.util;

import java.util.ArrayList;
import java.util.List;

import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.ModelTree;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;

public class Utils {

	public static final String CONTEXT_SOURCE = "ContextSource";
	public static final String CONTEXT_MODELLER = "ContextModeller";
	public static final String CONTEXT_RULE = "ContextRule";
	public static final String CONTEXT_STATE = "ContextState";
	public static final String CONTEXT_MODEL = "ContextModel";
	public static final String CONTEXT_SR_ASSOCIATION = "ContextSource-Rule-Association";
	public static final String CONTEXT_RS_ASSOCIATION = "ContextRule-State-Association";
	public static final String CONTEXT_SS_ASSOCIATION = "ContextState-State-Association";

	public static void setUMLFreeName(ModelElement element, String testedName) {
		element.setName("");
		int i = 1;
		String extension = "";
		Boolean find = Boolean.valueOf(true);
		while (find.booleanValue()) {
			find = Boolean.valueOf(false);
			for (MObject sub : element.getCompositionOwner()
					.getCompositionChildren()) {
				if (sub.getName().equals(testedName + extension)) {
					extension = String.valueOf(i);
					i++;
					find = Boolean.valueOf(true);
					break;
				}
			}
		}
		element.setName(testedName + extension);
	}

	public static String getFreeName(ModelElement parent, String type, int nb) {
		ArrayList<ModelElement> children = null;
		ModelTree element;
		StringBuffer testedName = new StringBuffer(type);
		if (nb != 0)
			if ((type == "ContextModelDiagram")
					|| (type == "InternalContextModelDiagram")
					|| (type == "ParametricDiagram")) {
				testedName.append(" (" + nb + ")");
			} else
				testedName.append(nb);

		if ((type == "ContextModelDiagram")
				|| (type == "InternalContextModelDiagram")
				|| (type == "ParametricDiagram")) {
			element = (ModelTree) parent;
			children = new ArrayList<ModelElement>(element.getProduct(StaticDiagram.class));
		} else if (type == "View") {
			element = (ModelTree) parent;
			children = new ArrayList<ModelElement>(element.getOwnedElement(Package.class));
		} else if ((type == "ContextSourceStereotype")) {
			element = (ModelTree) parent;
			children = new ArrayList<ModelElement>(element.getOwnedElement(Class.class));
		}

		if (children != null) {
			for (ModelElement child : children) {
				if (child.getName().equals(testedName.toString())) {
					return getFreeName(parent, type, nb + 1);
				}
			}

		}

		return testedName.toString();
	}

	public static boolean accept(MObject selectedElement) {
		IUmlModel model = ContextModellerModule.getInstance().getModuleContext().getModelingSession().getModel();

		for (MObject libRoot : model.getLibraryRoots()) {
			if (selectedElement.equals(libRoot)) {
				return false;
			}

		}

		for (MObject modelRoot : model.getModelRoots()) {
			if (selectedElement.equals(modelRoot)) {
				return false;
			}
		}

		return !selectedElement.equals(model);
	}

	public static List<Stereotype> computePropertyList(ModelElement element) {
		List<Stereotype> result = new ArrayList<Stereotype>();
		int i = 0;

		for (Stereotype ster : element.getExtension()) {
			if ((ster.getOwner().getOwnerModule().getName()
					.equals("ContextModeller")) && (!result.contains(ster))) {
				result.add(ster);

				Stereotype parent = ster.getParent();
				while ((parent != null) && (!result.contains(parent))) {
					result.add(i, parent);
					ster = parent;
					parent = ster.getParent();
				}
				i = result.size();
			}

		}

		return result;
	}

}
