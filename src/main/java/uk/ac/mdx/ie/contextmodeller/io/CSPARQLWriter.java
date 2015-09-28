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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Association;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.util.ModelUtils;
import uk.ac.mdx.ie.contextmodeller.util.RDFTriple;
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


		result.append("REGISTER QUERY " + relatedState.getName() + "_query AS ");
		result.append(System.lineSeparator());

		generateCSPARQLPrefixes(result, relatedSource);

		result.append("CONSTRUCT { + <http://ie.cs.mdx.ac.uk/POSEIDON/context/is> \"" + relatedState.getName() + "\"} ");
		result.append(System.lineSeparator());

		result.append("FROM STREAM <http://poseidon-project.org/context-stream> [RANGE " + generateCSPARQLRange(rule) + "] ");
		result.append(System.lineSeparator());

		result.append("WHERE { ");
		result.append(ModelUtils.getTaggedValue("Source_data", (ModelElement) relatedSource));
		result.append(" ");
		result.append(System.lineSeparator());

		HashMap<String, String> subresexp = generateCSPARQLSubqueries(result, relatedSource, rule);

		generateCSPARQLFilters(result, rule, subresexp);

		result.append("}");
		result.append(System.lineSeparator());
	}

    private static void generateCSPARQLFilters(StringBuilder result,
			MObject rule, HashMap<String, String> subresexp) {

    	if (! subresexp.isEmpty()) {
    		result.append("FILTER ( ");

        	for(Entry<String, String> subquery : subresexp.entrySet()) {
        		result.append(subquery.getKey());
        		result.append(" ");
        		result.append(subquery.getValue());
        		result.append(" &&");

        	}

        	int l = result.length();
        	result.replace(l-3, l, " ) ");
        	result.append(System.lineSeparator());
    	}

    	String logExp = ModelUtils.getTaggedValue("Rule_logicalEvals", (ModelElement) rule);

    	if (! logExp.isEmpty()) {

    		result.append("FILTER ( ");
    		result.append(logExp);
    		result.append(" ) ");
    		result.append(System.lineSeparator());

    	}

	}

	private static HashMap<String, String> generateCSPARQLSubqueries(StringBuilder result,
			MObject relatedSource, MObject rule) {

    	String method = "Rule_method";
		String methodtriples = "Rule_triple";
		String methodExpr = "Rule_methodExpr";
		String subqueryResult = "subqres_";
		HashMap<String, String> subqueryResultExp = new HashMap<>();

		for (int i=1;i<4;i++) {

			StringBuilder newMethod = new StringBuilder(method);
			StringBuilder newMethodTriples = new StringBuilder(methodtriples);
			StringBuilder newMethodExpr = new StringBuilder(methodExpr);
			StringBuilder newSubqueryResult = new StringBuilder(subqueryResult);

			String index = String.valueOf(i);

			newMethod.append(index);

			String methodValue = ModelUtils.getTaggedValue(newMethod.toString(), (ModelElement) rule);

			if (! methodValue.isEmpty()) {
				newMethodTriples.append(index);
				newMethodExpr.append(index);
				String methodTriplesValue = ModelUtils.getTaggedValue(newMethodTriples.toString(), (ModelElement) rule);
				String methodExprValue = ModelUtils.getTaggedValue(newMethodExpr.toString(), (ModelElement) rule);
				newSubqueryResult.append(index);
				String subqueryResultText = newSubqueryResult.toString();

				String sourceTriplesText = ModelUtils.getTaggedValue("Source_data", (ModelElement) relatedSource);
				ArrayList<RDFTriple> sourceTriples = ModelUtils.getRDFTriples(sourceTriplesText);

				RDFTriple queryRelatedTriple = ModelUtils.getRDFTripleForVar(sourceTriples, methodTriplesValue);

				result.append("{");
				result.append(System.lineSeparator());
				result.append("SELECT ");

				if (! methodExprValue.isEmpty()) {

					subqueryResultExp.put(subqueryResultText, methodExprValue);

					result.append(" (");
					result.append(methodValue);
					result.append(" AS ");
					result.append(subqueryResultText);
					result.append(") WHERE { ");
					result.append(queryRelatedTriple.getSubject());
					result.append(" ");
					result.append(queryRelatedTriple.getPredicate());
					result.append(" ");
					result.append(queryRelatedTriple.getObject());
					result.append(" . ");
					result.append(System.lineSeparator());
					result.append("FILTER( ");
					result.append(methodTriplesValue);
					result.append(" ) ");
					result.append(System.lineSeparator());

				}

				result.append("}\n");

			}

		}

		return subqueryResultExp;

	}



	private static String generateCSPARQLRange(MObject rule) {
		StringBuilder result = new StringBuilder();

		EList<AssociationEnd> ends = ((Class) rule).getTargetingEnd();

		String rangeevery = null;
		String rangefor = null;

		for(AssociationEnd end : ends) {

			Association rangeinfo = end.getAssociation();

			rangeevery = ModelUtils.getTaggedValue("SR_every", rangeinfo);
			rangefor = ModelUtils.getTaggedValue("SR_for", rangeinfo);
		}

		if (! rangefor.isEmpty()) {
			result.append(rangefor);
		}

		if(! rangeevery.isEmpty()) {
			result.append(" STEP ");
			result.append(rangeevery);
		}

		return result.toString();
	}

	private static void generateCSPARQLPrefixes(StringBuilder result,
			MObject relatedSource) {


    	String prefixStrings = ModelUtils.getTaggedValue("Source_ont", (ModelElement) relatedSource);

    	String[] prefixes = prefixStrings.split(" . ");

    	for (String prefix : prefixes) {

    		if (! prefix.isEmpty()) {
    			prefix = prefix.trim();

        		result.append("PREFIX " + prefix + " ");
        		result.append(System.lineSeparator());
    		}
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
