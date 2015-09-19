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

package uk.ac.mdx.ie.contextmodeller.io;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.util.ModelUtils;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class CSPARQLWriter extends AbstractModelWriter {

	@Override
	public void write(Package model) {
		// TODO Auto-generated method stub

	}

	@Override
	public String writeToString() {
		StringBuilder result = new StringBuilder();

		List<MObject> rules = getRules();

		for (MObject rule : rules) {
			generateCSPARQLQuery(result, rule);
		}

		return result.toString();
	}

	private static void generateCSPARQLQuery(StringBuilder result, MObject rule) {

		MObject relatedSource;
		MObject relatedState;

		relatedSource = getRuleSource(rule);
		relatedState = getRuleState(rule);

		//We cannot proceed without a source and state
		if (relatedSource == null || relatedState == null) {
			return;
		}


		result.append("REGISTER QUERY " + relatedSource.getName() + "_query AS \n");

		generateCSPARQLPrefixes(result, relatedSource);

	}

    private static void generateCSPARQLPrefixes(StringBuilder result,
			MObject relatedSource) {


    	String prefixStrings = ModelUtils.getTaggedValue("Source_ont", (ModelElement) relatedSource);

    	String[] prefixes = prefixStrings.split(" . ");

    	for (String prefix : prefixes) {
    		prefix = prefix.trim();

    		result.append("PREFIX " + prefix + " \n");
    	}

	}

	private static MObject getRuleState(MObject rule) {

    	EList<AssociationEnd> ends = ((Class)rule).getOwnedEnd();

    	for (AssociationEnd end: ends) {
    		return end.getTarget();
    	}

    	return null;

	}

    private static MObject getRuleSource(MObject rule) {

    	EList<AssociationEnd> ends  = ((Class) rule).getTargetingEnd();

    	for (AssociationEnd end : ends) {

    		return end.getSource();

    	}

    	return null;
	}

	private List<MObject> getRules() {

		List<MObject> rules = new ArrayList<MObject>();


		List<MObject> ownedClasses = (List<MObject>) model.getCompositionChildren();

		for (MObject ownedClass : ownedClasses) {

			if (((ModelElement) ownedClass).isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_RULE)) {
				rules.add(ownedClass);
			}

		}

		return rules;
	}

}
