/*
 * Copyright 2016 The ContextModeller Project
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.emf.common.util.EList;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.mdx.ie.contextmodeller.util.ContextStateGroup;
import uk.ac.mdx.ie.contextmodeller.util.StateTransition;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class UPPAALWriter extends AbstractModelWriter {

	private HashMap<String, ContextStateGroup> mStateGroups = new HashMap<String, ContextStateGroup>();
	private int uppaalConstructID = 2;
	private int uppaalTemplateID = 2;


	@Override
	public void write(Package model) {
		// TODO Auto-generated method stub

	}

	private static List<MObject> separateAggregateContexts(List<MObject> elements) {

		List<MObject> aggr = new ArrayList<MObject>();

		for (MObject ownedClass : elements) {

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
		    		aggr.add(ownedClass);
		    	}
			}
		}

		return aggr;
	}

	private void generateContextStateGroups() {

		List<MObject> ownedClasses = (List<MObject>) model.getCompositionChildren();

		List<MObject> aggrContexts = separateAggregateContexts(ownedClasses);

		ownedClasses.removeAll(aggrContexts);

		generateContextStateGroup(ownedClasses, true);
		generateContextStateGroup(aggrContexts, false);

	}

	private void generateContextStateGroup(List<MObject> elements, boolean isAtomic) {


		for (MObject ownedClass : elements) {

			ModelElement element = (ModelElement) ownedClass;

			if (element.isStereotyped(Utils.CONTEXT_MODELLER, Utils.CONTEXT_STATE)) {
				String contextname = ownedClass.getName();
				String contextGroup = contextname.substring(0, contextname.indexOf("_"));
				String contextState = contextname.substring(contextGroup.length() + 1);

				ContextStateGroup contextStateGroup = mStateGroups.get(contextGroup);

				if (contextStateGroup == null) {
					contextStateGroup = new ContextStateGroup(isAtomic, contextGroup, uppaalTemplateID);
					mStateGroups.put(contextGroup, contextStateGroup);
					contextStateGroup.addContextState("UNKNOWN", uppaalConstructID);
					contextStateGroup.mInit = "id" + String.valueOf(uppaalConstructID);
					uppaalConstructID++;
					uppaalTemplateID++;
				}

				contextStateGroup.addContextState(contextState, uppaalConstructID);
				uppaalConstructID++;
			}

		}
	}

	@Override
	public String writeToString() {

		StringWriter writer = new StringWriter();

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Uppaal Team//DTD Flat System 1.1//EN");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd");

			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("nta");
			doc.appendChild(root);

			generateContextStateGroups();

			//Global Declarations
			String contextVariables = "// Place global declarations here.";
			contextVariables += generateGlobalVariables();

			//StringBuilder sb = new StringBuilder();
			//addHeader(sb);
			root.appendChild(addDeclaration(doc, contextVariables));

			root.appendChild(addContextReceiver(doc));

			generateTemplates(doc, root);

			Element sys = doc.createElement("system");
			sys.setTextContent(generateSystemDeclaration(null));
			root.appendChild(sys);

			transformer.transform(new DOMSource(doc), new javax.xml.transform.stream.StreamResult(writer));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

		return writer.toString().replace("&#13;", " ");
	}

	private void generateTemplates(Document doc, Element root) {

		for (ContextStateGroup group : mStateGroups.values()) {
			Element template = doc.createElement("template");

			Element name = doc.createElement("name");
			name.setAttribute("x", "5");
			name.setAttribute("y", "5");
			name.setTextContent(group.mGroupName);
			template.appendChild(name);

			Element parameter = doc.createElement("parameter");
			//parameter.setTextContent("urgent chan &newData, urgent chan &deactivate, urgent chan &newAtomicContext");
			parameter.setTextContent("urgent chan &newData, urgent chan &deactivate");
			template.appendChild(parameter);

			for (Map.Entry<String, String> entry : group.mStates.entrySet()) {

				Element location = doc.createElement("location");
				location.setAttribute("id", entry.getValue());

				name = doc.createElement("name");
				name.setAttribute("x", "5");
				name.setAttribute("y", "5");
				name.setTextContent(entry.getKey());
				location.appendChild(name);
				template.appendChild(location);

			}

			Element init = doc.createElement("init");
			init.setAttribute("ref", group.mInit);
			template.appendChild(init);

			for (StateTransition statechange : group.mTransitions) {

				Element transition = doc.createElement("transition");

				Element transource = doc.createElement("source");
				transource.setAttribute("ref", statechange.source);
				transition.appendChild(transource);

				Element trantarget = doc.createElement("target");
				trantarget.setAttribute("ref", statechange.target);
				transition.appendChild(trantarget);

				Element label = doc.createElement("label");
				label.setAttribute("kind", "synchronisation");
				//label.setAttribute("x", "synchronisation");
				//label.setAttribute("y", "synchronisation");
				label.setTextContent(statechange.synch);
				transition.appendChild(label);

				label = doc.createElement("label");
				label.setAttribute("kind", "assignment");
				//label.setAttribute("x", "synchronisation");
				//label.setAttribute("y", "synchronisation");
				label.setTextContent(statechange.assign);
				transition.appendChild(label);

				template.appendChild(transition);

			}

			root.appendChild(template);

		}
	}

	private String generateGlobalVariables() {

		StringBuilder sb = new StringBuilder(mStateGroups.size() * 10);

		for (ContextStateGroup group : mStateGroups.values()) {
			sb.append(System.lineSeparator());
			sb.append("int ");
			sb.append(group.mStateVariable);
			sb.append(" = 0;");
		}

		return sb.toString();
	}

	private void generateQueryFile() {



	}

	private Element addContextReceiver(Document doc) {

		Element contextReceiver = doc.createElement("template");

		Element name = doc.createElement("name");
		name.setAttribute("x", "5");
		name.setAttribute("y", "5");
		name.setTextContent("ContextReceiver");
		contextReceiver.appendChild(name);

		Element parameter = doc.createElement("parameter");
		parameter.setTextContent("urgent chan &newData, urgent chan &deactivate");
		contextReceiver.appendChild(parameter);

		Element declarations = addDeclaration(doc, "// Place local declarations here.");
		contextReceiver.appendChild(declarations);

		//state one
		Element location = doc.createElement("location");
		location.setAttribute("id", "id0");
		location.setAttribute("x", "232");
		location.setAttribute("y", "-88");

		name = doc.createElement("name");
		name.setAttribute("x", "222");
		name.setAttribute("y", "-118");
		name.setTextContent("Active");
		location.appendChild(name);
		contextReceiver.appendChild(location);

		//state two
		location = doc.createElement("location");
		location.setAttribute("id", "id1");
		location.setAttribute("x", "232");
		location.setAttribute("y", "72");

		name = doc.createElement("name");
		name.setAttribute("x", "222");
		name.setAttribute("y", "42");
		name.setTextContent("Inactive");
		location.appendChild(name);
		contextReceiver.appendChild(location);

		//state transitions
		Element init = doc.createElement("init");
		init.setAttribute("ref", "id1");
		contextReceiver.appendChild(init);

		Element transition = doc.createElement("transition");

		//transition source
		Element transource = doc.createElement("source");
		transource.setAttribute("ref", "id0");
		transition.appendChild(transource);

		//transition target
		Element trantarget = doc.createElement("target");
		trantarget.setAttribute("ref", "id1");
		transition.appendChild(trantarget);

		Element label = doc.createElement("label");
		label.setAttribute("kind", "synchronisation");
		label.setAttribute("x", "280");
		label.setAttribute("y", "-16");
		label.setTextContent("deactivate!");
		transition.appendChild(label);

		Element nail = doc.createElement("nail");
		nail.setAttribute("x", "222");
		nail.setAttribute("y", "-8");
		transition.appendChild(nail);

		contextReceiver.appendChild(transition);


		//state transitions
		transition = doc.createElement("transition");

		//transition source
		transource = doc.createElement("source");
		transource.setAttribute("ref", "id0");
		transition.appendChild(transource);

		//transition target
		trantarget = doc.createElement("target");
		trantarget.setAttribute("ref", "id0");
		transition.appendChild(trantarget);

		label = doc.createElement("label");
		label.setAttribute("kind", "synchronisation");
		label.setAttribute("x", "280");
		label.setAttribute("y", "-176");
		label.setTextContent("newData!");
		transition.appendChild(label);

		nail = doc.createElement("nail");
		nail.setAttribute("x", "288");
		nail.setAttribute("y", "-152");
		transition.appendChild(nail);

		nail = doc.createElement("nail");
		nail.setAttribute("x", "184");
		nail.setAttribute("y", "-152");
		transition.appendChild(nail);
		contextReceiver.appendChild(transition);


		//state transitions
		transition = doc.createElement("transition");

		//transition source
		transource = doc.createElement("source");
		transource.setAttribute("ref", "id1");
		transition.appendChild(transource);

		//transition target
		trantarget = doc.createElement("target");
		trantarget.setAttribute("ref", "id0");
		transition.appendChild(trantarget);

		nail = doc.createElement("nail");
		nail.setAttribute("x", "176");
		nail.setAttribute("y", "-8");
		transition.appendChild(nail);
		contextReceiver.appendChild(transition);

		return contextReceiver;
	}

	private Element addDeclaration(Document doc, String declarations) {

		Element declaration = doc.createElement("declaration");

		declaration.appendChild(doc.createTextNode(declarations));

		return declaration;
	}

	private Element addSelfTransition(Document doc, String ref, String label) {

		Element transition = doc.createElement("transition");


		return transition;
	}

	private Element addTransition(Document doc, Element source, Element target) {

		Element transition = doc.createElement("transition");

		//transition source
		Element transource = doc.createElement("source");
		transource.setAttribute("ref", source.getAttribute("ref"));
		transition.appendChild(transource);

		//transition target
	    Element trantarget = doc.createElement("target");
		trantarget.setAttribute("ref", target.getAttribute("ref"));
		transition.appendChild(trantarget);


		Element nail = doc.createElement("nail");
		nail.setAttribute("x", "176");
		nail.setAttribute("y", "-8");
		transition.appendChild(nail);

		return transition;

	}

	private String generateSystemDeclaration(StringBuilder sb) {

		if (sb == null) {
			sb = new StringBuilder();
		}

		sb.append("urgent chan newData1;");
		sb.append(System.lineSeparator());
		sb.append("urgent chan deactivate1;");
		//sb.append(System.lineSeparator());
		//sb.append("urgent chan newAtomicContext1;");
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());

		//Template instantiation
		sb.append("// Place template instantiations here.");
		sb.append(System.lineSeparator());
		sb.append("Receiver = ContextReceiver(newData1, deactivate1);");
		sb.append(System.lineSeparator());

		for (ContextStateGroup group : mStateGroups.values()) {
			sb.append(group.mInstanceName);
			sb.append(" = ");
			sb.append(group.mGroupName);

			if (group.isAtomic) {
				//sb.append("(newData1, deactivate1, newAtomicContext1);");
				sb.append("(newData1, deactivate1);");
			} else {
				//sb.append("(newAtomicContext1);");
				sb.append("(newData1, deactivate1);");
			}

			sb.append("\n");
		}

		sb.append("\n");

		//Last instance list
		sb.append("// List one or more processes to be composed into a system.");
		sb.append("\n");
		sb.append("system Receiver,");

		for (ContextStateGroup group : mStateGroups.values()) {

			sb.append(" ");
			sb.append(group.mInstanceName);
			sb.append(",");
		}

		int lastchar = sb.length();
		sb.replace(lastchar -1, lastchar, ";");

		return sb.toString();
	}

}
