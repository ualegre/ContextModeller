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

import java.util.List;

import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.metamodel.diagrams.ClassDiagram;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.i18n.I18nMessageService;

public class CMFactory {

	public static StaticDiagram createContextDiagram(List<MObject> elements, IModelingSession session,
			String stereotypeName) {

		String name = I18nMessageService.getString("ContextModelTool.label");

		Stereotype stereotype = session.getMetamodelExtensions().getStereotype(stereotypeName,
				Modelio.getInstance().getMetamodelService().getMetamodel().getMClass(ClassDiagram.class));

		MObject element = elements.get(0);

		if (element != null) {
			StaticDiagram diagram;
			diagram = session.getModel().createStaticDiagram(name, (ModelElement) element, stereotype);
			Utils.setUMLFreeName(diagram, Utils.CONTEXT_MODEL);
			return diagram;

		} else {
			return null;
		}
	}

	public static Class createAndAddSource(MObject owner) {
		if (owner instanceof Package) {
			return createAndAddSource((Package) owner);
		}

		if (owner instanceof Class) {
			return createAndAddSource((Class) owner);
		}

		return null;
	}

	private static Class createAndAddSource(Class owner) {

		try {
			Class result = Modelio
					.getInstance()
					.getModelingSession()
					.getModel()
					.createClass("", owner, Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_SOURCE);
			Utils.setUMLFreeName(result, Utils.CONTEXT_SOURCE);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Class createAndAddSource(Package owner) {

		try {
			Class result = Modelio
					.getInstance()
					.getModelingSession()
					.getModel()
					.createClass("", owner, Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_SOURCE);
			Utils.setUMLFreeName(result, Utils.CONTEXT_SOURCE);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Class createAndAddRule(Package owner) {

		try {
			Class result = Modelio
					.getInstance()
					.getModelingSession()
					.getModel()
					.createClass("", owner, Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_RULE);
			Utils.setUMLFreeName(result, Utils.CONTEXT_RULE);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Class createAndAddState(Package element) {
		try {
			Class result = Modelio
					.getInstance()
					.getModelingSession()
					.getModel()
					.createClass(Utils.CONTEXT_STATE, element,
							Utils.CONTEXT_MODELLER, Utils.CONTEXT_STATE);
			Utils.setUMLFreeName(result, Utils.CONTEXT_STATE);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
