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
		List<MObject> atomicRules = getRules();
		List<MObject> aggRules = getAggrRules();

		StringBuilder result = new StringBuilder((atomicRules.size() * 150) + (aggRules.size() * 50));

		for (MObject rule : atomicRules) {
			generateCSPARQLQuery(result, rule);
		}

		result.append(System.lineSeparator());

		for (MObject rule : aggRules) {
			generateAggregateRule(result, rule);
		}

		return result.toString();
	}

	private static void generateAggregateRule(StringBuilder result, MObject rule) {

		boolean start = true;

		EList<AssociationEnd> ends  = ((Class) rule).getTargetingEnd();

		for (AssociationEnd end : ends) {
			MObject literal = end.getSource();

			if (start) {
				start = false;
			} else {
				String nodeType = ModelUtils.getTaggedValue("Aggr_type", end);
				String nodeTypeStr = "and";

				if (nodeType.equalsIgnoreCase("not")) {
					nodeTypeStr = "not";
				}


				result.append(nodeTypeStr);
				result.append(" ");
			}

			result.append(literal.getName());
			result.append(" ");
		}

		result.append("iff ");
		result.append(rule.getName());
		result.append(System.lineSeparator());

	}

	private List<MObject> getAggrRules() {

		List<MObject> rules = new ArrayList<MObject>();


		List<? extends MObject> ownedClasses =  model.getCompositionChildren();

		for (MObject ownedClass : ownedClasses) {

			if (((ModelElement) ownedClass).isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_STATE)) {
				EList<AssociationEnd> ends  = ((Class) ownedClass).getTargetingEnd();
				boolean agg = true;

				if (ends.isEmpty()) {
					agg = false;
				}

		    	for (AssociationEnd end : ends) {

		    		MObject assocObject = end.getSource();

		    		if (! ((ModelElement) assocObject).isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_STATE)) {
		    			agg = false;
		    		}
		    	}

		    	if (agg) {
		    		rules.add(ownedClass);
		    	}
			}
		}

		return rules;
	}

	private static void generateCSPARQLQuery(StringBuilder result, MObject rule) {

		List<MObject> relatedSources;
		MObject relatedState;

		relatedSources = getRuleSources(rule);
		relatedState = getRuleState(rule);

		//We cannot proceed without a source and state
		if (relatedSources.isEmpty() || relatedState == null) {
			return;
		}


		result.append("REGISTER QUERY " + relatedState.getName() + "_query AS ");
		result.append(System.lineSeparator());

		generateCSPARQLPrefixes(result, relatedSources);
		String firstSourceName = relatedSources.get(0).getName();
		result.append("CONSTRUCT { ex:" + firstSourceName.toLowerCase()
				+ " <http://ie.cs.mdx.ac.uk/POSEIDON/context/is> \"" + relatedState.getName() + "\"} ");
		result.append(System.lineSeparator());

		result.append("FROM STREAM <http://poseidon-project.org/context-stream> [RANGE " + generateCSPARQLRange(rule) + "] ");
		result.append(System.lineSeparator());

		result.append("WHERE { ");
		result.append(System.lineSeparator());

		List<RDFTriple> sourceRDF = new ArrayList<RDFTriple>();

		for (MObject relatedSource : relatedSources) {
			String triples = ModelUtils.getTaggedValue("Source_data", (ModelElement) relatedSource);
			sourceRDF.addAll(ModelUtils.getRDFTriples(triples));
		}

		for (RDFTriple rdf : sourceRDF) {
			result.append(rdf.toString());
			result.append(" . ");
			result.append(System.lineSeparator());
		}

		HashMap<String, String> subresexp = generateCSPARQLSubqueries(result, sourceRDF, rule);

		generateCSPARQLFilters(result, rule, subresexp);

		result.append("}");
		result.append(System.lineSeparator());
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
        		result.append(" && ");

        	}

        	int l = result.length();
        	result.replace(l-3, l, " ) ");
        	result.append(System.lineSeparator());
    	}

    	String logExp = ModelUtils.getTaggedValue("Rule_logicalEvals", (ModelElement) rule);

    	if (! logExp.isEmpty()) {
    		String[] logExps = logExp.split("\\,");

    		for (String exp : logExps) {
    			result.append("FILTER ( ");
        		result.append(exp.trim());
        		result.append(" ) ");
        		result.append(System.lineSeparator());
    		}
    	}

	}

	private static HashMap<String, String> generateCSPARQLSubqueries(StringBuilder result,
			List<RDFTriple> sourceTriples, MObject rule) {

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
				List<RDFTriple> queryRelatedTriples = new ArrayList<RDFTriple>();

				//Might have several value to variables
				String[] methodTripleValues = methodTriplesValue.split(",");

				for (String str: methodTripleValues) {
					str.trim();
					String[] strs = str.split("=");

					if (strs.length == 2) {
						String var = strs[0].trim();
						//methodTripleValueMap.put(var, strs[1].trim());
						RDFTriple triple = ModelUtils.getRDFTripleForVar(sourceTriples, var);
						if (triple != null) {
							queryRelatedTriples.add(triple);
						}

					}
				}

				if (queryRelatedTriples.isEmpty()) {
					queryRelatedTriples.addAll(sourceTriples);
				}


				//RDFTriple queryRelatedTriple = ModelUtils.getRDFTripleForVar(sourceTriples, methodTriplesValue);

				result.append("{");
				result.append(System.lineSeparator());
				result.append("SELECT ");

				if (! methodExprValue.isEmpty()) {

					subqueryResultExp.put(subqueryResultText, methodExprValue);

					result.append(" (");
					result.append(methodValue);
					result.append(" AS ");
					result.append(subqueryResultText);
					result.append(") ");
					result.append(System.lineSeparator());
					result.append("WHERE { ");

					for (RDFTriple rdftriple : queryRelatedTriples) {
						result.append(rdftriple.getSubject());
						result.append(" ");
						result.append(rdftriple.getPredicate());
						result.append(" ");
						result.append(rdftriple.getObject());
						result.append(" . ");
						result.append(System.lineSeparator());
					}

					if (! methodTriplesValue.isEmpty()) {
						result.append("FILTER( ");
						result.append(methodTriplesValue);
						result.append(" ) ");
						result.append(System.lineSeparator());
						result.append("}");
						result.append(System.lineSeparator());
					}

				}

				result.append("}");
				result.append(System.lineSeparator());
			}

		}

		return subqueryResultExp;

	}



	private static String generateCSPARQLRange(MObject rule) {
		boolean triplesRange = false;
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

			String rangeCase = rangefor.toLowerCase();

			if (rangeCase.contains("triples")) {
				result.append(triplesRange(rangeCase));
				triplesRange = true;
			} else {
				result.append(rangeCase);
			}
		}

		if(! rangeevery.isEmpty()) {

			String rangeCase = rangeevery.toLowerCase();

			if (rangeCase.contains("triples")) {

				if (! triplesRange ) {
					result.append(triplesRange(rangeCase));
				}

			} else {
				result.append(" STEP ");
				result.append(rangeCase);
			}
		}

		return result.toString();
	}

	private static String triplesRange(String str) {

		StringBuilder result = new StringBuilder();
		result.append("TRIPLES ");
		result.append(str.replaceAll("[^\\D]", ""));

		return result.toString();
	}

	private static void generateCSPARQLPrefixes(StringBuilder result,
			List<MObject> relatedSources) {


		for (MObject relatedSource : relatedSources) {

			String prefixStrings = ModelUtils.getTaggedValue("Source_ont", (ModelElement) relatedSource);

	    	String[] prefixes = prefixStrings.split(" . ");

	    	for (String prefix : prefixes) {

	    		if (! prefix.isEmpty()) {
	    			prefix = prefix.trim();

	        		result.append("PREFIX ");
	        		result.append(prefix);
	        		result.append(" ");
	        		result.append(System.lineSeparator());
	    		}
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

    private static List<MObject> getRuleSources(MObject rule) {

    	List<MObject> sources = new ArrayList<MObject>();
    	EList<AssociationEnd> ends  = ((Class) rule).getTargetingEnd();

    	for (AssociationEnd end : ends) {
    		sources.add(end.getSource());
    	}

    	return sources;
	}

	private List<MObject> getRules() {

		List<MObject> rules = new ArrayList<MObject>();


		List<? extends MObject> ownedClasses = model.getCompositionChildren();

		for (MObject ownedClass : ownedClasses) {

			if (((ModelElement) ownedClass).isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_RULE)) {
				rules.add(ownedClass);
			}

		}

		return rules;
	}

}
