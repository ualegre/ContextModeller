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

package uk.ac.mdx.ie.contextmodeller.impl;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.event.IModelChangeEvent;
import org.modelio.api.modelio.model.event.IModelChangeHandler;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Note;
import org.modelio.metamodel.uml.infrastructure.TagParameter;
import org.modelio.metamodel.uml.infrastructure.TaggedValue;
import org.modelio.metamodel.uml.statik.Association;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.util.ModelUtils;
import uk.ac.mdx.ie.contextmodeller.util.RDFTriple;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class ContextModellerModelChangeHandler implements IModelChangeHandler {

	@Override
	public void handleModelChange(IModelingSession session,
			IModelChangeEvent event) {

		Set<MObject> addedElements = event.getCreationEvents();

		for (MObject element : addedElements) {

			if (element instanceof TaggedValue) {
				updateStereotypeInformation((TaggedValue) element);
			}

		}

		Set<MObject> updatedElements = event.getUpdateEvents();

		for (MObject element : updatedElements) {

			if (element instanceof TagParameter) {
				updateStereotypeInformation(((TagParameter) element)
						.getAnnoted());
			}

			if (element instanceof Note) {

			}
		}

	}

	private static void updateStereotypeInformation(TaggedValue property) {

		ModelElement element = property.getAnnoted();

		if (element.isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_SOURCE)) {
			updateContextSourceInformation((Class) element);
		} else if (element.isStereotyped(Utils.CONTEXT_MODELLER,
				Utils.CONTEXT_RULE)) {
			updateContextRuleInformation((Class) element);
		} else if (element.isStereotyped(Utils.CONTEXT_MODELLER,
				Utils.CONTEXT_SS_ASSOCIATION)) {
			updateContextStateRelationships((Association) element);
		} else if (element.isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_SR_ASSOCIATION)) {
			updateContextSourceRelationships((Association) element);
		}

	}

	private static void updateContextSourceRelationships(Association element) {
		String astream= ModelUtils.getTaggedValue("SR_stream", element);
		String aevery = ModelUtils.getTaggedValue("SR_every", element);
		String afor = ModelUtils.getTaggedValue("SR_for", element);

		StringBuilder value = new StringBuilder();

		if (! astream.isEmpty()) {
			value.append("Stream: ");
			value.append(astream + "\n");
		}
		if (! aevery.isEmpty()) {
			value.append("Every: ");
			value.append(aevery + "\n");
		}
		if (! afor.isEmpty()) {
			value.append("For: ");
			value.append(afor);
		}


		element.setName(value.toString());

	}

	private static void updateContextRuleInformation(Class contextRule) {

		EList<AssociationEnd> assEnds = contextRule.getOwnedEnd();

		if (assEnds.isEmpty()) {
			MessageDialog.openError(new Shell(), "Error",
					"Rule needs associating with a State first");
			return;
		}

		Association assoc = assEnds.get(0).getAssociation();

		StringBuilder methodinfo = new StringBuilder();
		StringBuilder ruleText = new StringBuilder();

		String method = "Rule_method";
		String methodtriples = "Rule_triple";
		String methodExpr = "Rule_methodExpr";

		for (int i=1;i<4;i++) {

			StringBuilder newMethod = new StringBuilder(method);
			StringBuilder newMethodTriples = new StringBuilder(methodtriples);
			StringBuilder newMethodExpr = new StringBuilder(methodExpr);

			String index = String.valueOf(i);

			newMethod.append(index);

			String methodValue = ModelUtils.getTaggedValue(newMethod.toString(), contextRule);

			if (! methodValue.isEmpty()) {
				newMethodTriples.append(index);
				newMethodExpr.append(index);
				String methodTriplesValue = ModelUtils.getTaggedValue(newMethodTriples.toString(), contextRule);
				String methodExprValue = ModelUtils.getTaggedValue(newMethodExpr.toString(), contextRule);

				generateRuleMethodSubString(methodinfo, methodValue, methodTriplesValue, methodExprValue);
			}

		}

		if (methodinfo.length()!=0) {
			ruleText.append(methodinfo);
		}

		String logicalRule = ModelUtils.getTaggedValue("Rule_logicalEvals",
				contextRule);

		if (! logicalRule.isEmpty()) {

			if (methodinfo.length()!=0) {
				ruleText.append(", ");
			}

			ruleText.append(logicalRule);
		}

		EList<AssociationEnd> ends = assoc.getEnd();

		for (AssociationEnd end : ends) {
			if (!end.getMultiplicityMin().isEmpty()) {
				end.setMultiplicityMin(ruleText.toString());
			}
		}
	}

	private static void generateRuleMethodSubString(StringBuilder methodinfo,
			String methodValue, String methodTriplesValue,
			String methodExprValue) {

		StringBuilder ruleMethod = new StringBuilder();

		ruleMethod.append(methodValue);

		if (! methodTriplesValue.isEmpty()) {
			ruleMethod.append(" WHERE ");
			ruleMethod.append(methodTriplesValue);
		}

		if ( ! methodExprValue.isEmpty()) {
			ruleMethod.insert(0, "(");
			ruleMethod.append(")");
			ruleMethod.append(methodExprValue);
		}

		if (methodinfo.length()>0) {
			//might need link break here.
			methodinfo.append(", ");
		}

		methodinfo.append(ruleMethod);

	}

	private static void updateContextStateRelationships(
			Association stateRelationship) {

		String aggType = ModelUtils.getTaggedValue("Aggr_type",
				stateRelationship);

		if (aggType.isEmpty()) {
			aggType = "AND";
			ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Aggr_type",
					aggType, stateRelationship);
		}

		stateRelationship.setName(aggType);
	}

	private static void updateContextSourceInformation(Class contextSource) {

		Note sourceText = contextSource.getDescriptor().get(0);
		StringBuilder str = new StringBuilder();
		// Lets start with ontologies
		String ont = ModelUtils.getTaggedValue("Source_ont", contextSource);
		str.append("ont:- " + ont);
		str.append("\n\n");
		// Next ontological data
		String data = ModelUtils.getTaggedValue("Source_data", contextSource);

		List<RDFTriple> dataRDF = ModelUtils.getRDFTriples(data);
		String dataString = "data:-";
		str.append(dataString);

		for (RDFTriple rdf : dataRDF) {
			str.append(rdf.toString());
			str.append(" . \n");
		}

		sourceText.setContent(str.toString());

	}
}
