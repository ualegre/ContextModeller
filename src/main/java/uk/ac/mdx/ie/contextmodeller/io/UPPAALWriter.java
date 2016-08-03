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

import java.util.ArrayList;

import org.modelio.metamodel.uml.statik.Package;

public class UPPAALWriter extends AbstractModelWriter {

	private ArrayList<String> mModelInstanceNames = new ArrayList<String>();

	@Override
	public void write(Package model) {
		// TODO Auto-generated method stub

	}

	@Override
	public String writeToString() {

		StringBuilder sb = new StringBuilder();
		addHeader(sb);
		addGlobalDeclarations(sb, null);

		generateQueryFile();

		return sb.toString();
	}

	private void generateQueryFile() {



	}

	private StringBuilder addGlobalDeclarations(StringBuilder sb, String declarations) {

		if (sb == null) {
			sb = new StringBuilder();
		}

		if (declarations == null) {
			declarations = new String();
		}

		if (declarations.isEmpty()) {
			sb.append("// Place global declarations here.");
		} else {
			sb.append(declarations);
		}

		return sb;
	}

	private StringBuilder addSystemDeclarations(StringBuilder sb, String declarations, boolean append) {

		if (sb == null) {
			sb = new StringBuilder();
		}

		sb.append("<system>");

		sb.append("// List one or more processes to be composed into a system.");
		sb.append(System.lineSeparator());
		sb.append("system ");

		int numOfItems = mModelInstanceNames.size();

		for (int i=0; i < numOfItems; i++) {
			if (i>0) {
				sb.append(", ");
			}

			sb.append(mModelInstanceNames.get(i));
		}

		sb.append(";");


		sb.append("</system>");
		return sb;
	}

	private StringBuilder addHeader(StringBuilder sb) {

		if (sb == null) {
			sb = new StringBuilder();
		}

		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd'>");
		sb.append("<nta>");
		return sb;
	}

}
