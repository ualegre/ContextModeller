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

import org.eclipse.emf.common.util.EList;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.Dependency;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.TagParameter;
import org.modelio.metamodel.uml.infrastructure.TagType;
import org.modelio.metamodel.uml.infrastructure.TaggedValue;

import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;

public class ModelUtils {

	public static void addValue(String modulename, String name, String values,
			ModelElement element) {

		boolean exist = false;
		EList<TaggedValue> tagElements = element.getTag();
		TaggedValue tvFound = null;

		if (!tagElements.isEmpty()) {
			for (TaggedValue tag : tagElements) {
				TagType type = tag.getDefinition();
				String tagname = type.getName();

				if (tagname.equals(name)) {
					exist = true;

					tvFound = tag;
				}
			}

		}

		if (!exist) {
			try {
				TaggedValue v = Modelio.getInstance().getModelingSession()
						.getModel()
						.createTaggedValue(modulename, name, element);
				element.getTag().add(v);
				if (!v.getDefinition().getParamNumber().equals("0")) {
					setTaggedValue(name, element, values);
				}

			} catch (Exception e) {
				ContextModellerModule.logger.error(e);
			}

		} else if ((tvFound != null)
				&& (tvFound.getDefinition().getParamNumber().equals("0"))) {
			tvFound.delete();
		} else {
			setTaggedValue(name, element, values);
		}

	}

	public static boolean hasTaggedValue(String tagtype, ModelElement _element) {

		for (TaggedValue tag : _element.getTag()) {
			TagType type = tag.getDefinition();
			String tagname = type.getName();
			if (tagname.equals(tagtype)) {
				return true;
			}
		}

		return false;

	}

	public static String getTaggedValue(String tagtype, ModelElement element) {
		for (TaggedValue tag : element.getTag()) {
			TagType type = tag.getDefinition();
			String tagname = type.getName();

			if (tagname.equals(tagtype)) {
				EList<TagParameter> actuals = tag.getActual();
				if ((actuals != null) && (actuals.size() > 0)) {
					return actuals.get(0).getValue();
				}
				return "";
			}
		}

		return "";
	}

	public static void setTaggedValue(String name, ModelElement elt,
			String value) {
		EList<TaggedValue> tagElements = elt.getTag();
		IUmlModel model = Modelio.getInstance().getModelingSession().getModel();

		if (!tagElements.isEmpty()) {
			for (TaggedValue tag : tagElements) {
				String tagname = tag.getDefinition().getName();
				if (tagname.equals(name)) {
					TagParameter firstElt = null;
					List actuals = tag.getActual();
					if ((actuals != null) && (actuals.size() > 0)) {
						firstElt = (TagParameter) actuals.get(0);
					} else {
						firstElt = model.createTagParameter();
						tag.getActual().add(firstElt);
					}

					if (((value.equals("false")) && (tag.getDefinition()
							.getParamNumber().equals("0")))
							|| ((value.equals("")) && (tag.getDefinition()
									.getParamNumber().equals("1"))))
						tag.delete();
					else
						firstElt.setValue(value);
				}
			}
		}
	}

	public static void setTaggedValue(TaggedValue tvFound, ModelElement elt,
			String value, ModelElement related, String modulelink,
			String stereotypeLink) {
		IUmlModel model = Modelio.getInstance().getModelingSession().getModel();
		ArrayList<Dependency> linksList = new ArrayList(
				elt.getDependsOnDependency());
		for (Dependency existingLinks : linksList) {
			if (existingLinks.isStereotyped(modulelink, stereotypeLink)) {
				existingLinks.delete();
			}
		}

		TagParameter firstElt = null;
		EList<TagParameter> actuals = tvFound.getActual();
		if ((actuals != null) && (actuals.size() > 0)) {
			firstElt = actuals.get(0);
		} else {
			firstElt = model.createTagParameter();
			tvFound.getActual().add(firstElt);
		}

		if (value.equals("false")) {
			tvFound.delete();
		} else {
			firstElt.setValue(value);
			try {
				model.createDependency(elt, related, modulelink, stereotypeLink);
			} catch (Exception e) {
				ContextModellerModule.logger.error(e);
			}
		}
	}


	public static ArrayList<RDFTriple> getRDFTriples(String rdf) {
		ArrayList<RDFTriple> triples = new ArrayList<>();

		String[] rdfs = rdf.split("\\.");

		for (String triple : rdfs) {
			triples.add(new RDFTriple(triple));
		}

		return triples;

	}

	public static RDFTriple getRDFTripleForVar(String rdf, String var) {

		return getRDFTripleForVar(getRDFTriples(rdf), var);

	}

	public static RDFTriple getRDFTripleForVar(List<RDFTriple> triples, String var) {

		RDFTriple result = null;

		for (RDFTriple triple : triples) {
			if (triple.containsVariable(var)) {
				result = triple;
			}
		}

		return result;

	}

	public static RDFTriple getRDFTripleForVars(String rdf, String[] var) {

		return getRDFTripleForVars(getRDFTriples(rdf), var);

	}

	public static RDFTriple getRDFTripleForVars(ArrayList<RDFTriple> triples, String[] var) {

		RDFTriple result = null;

		for (RDFTriple triple : triples) {
			if (triple.containsVariables(var)) {
				result = triple;
			}
		}

		return result;

	}

}
