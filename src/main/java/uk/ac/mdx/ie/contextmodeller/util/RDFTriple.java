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

import java.util.HashMap;

public class RDFTriple {

	private String mSubject;
	private String mPredicate;
	private String mObject;
	private HashMap<String, String> mVariables = new HashMap<String, String>();


	public RDFTriple (String subject, String predicate, String object) {

		mSubject = subject;
		mPredicate = predicate;
		mObject = object;

		checkIfVariable("subject", subject);
		checkIfVariable("predicate", predicate);
		checkIfVariable("object", object);

	}

	public RDFTriple (String triple) {

		triple = triple.trim();

		String[] components = triple.split("\\s+");

		if (components.length==3) {
			mSubject = components[0];
			mPredicate = components[1];
			mObject = components[2];

			checkIfVariable("subject", mSubject);
			checkIfVariable("predicate", mPredicate);
			checkIfVariable("object", mObject);
		}

	}

	private void checkIfVariable(String part, String str) {
		if (str.startsWith("?")) {
			mVariables.put(part, str);
		} else {
			mVariables.remove(part);
		}

	}

	public String getSubject() {
		return mSubject;
	}

	public void setSubject(String sub) {
		mSubject = sub;
		checkIfVariable("subject", sub);
	}

	public String getPredicate() {
		return mPredicate;
	}

	public void setPredicate(String pred) {
		mPredicate = pred;
		checkIfVariable("predicate", pred);
	}

	public String getObject() {
		return mObject;
	}

	public void setObject(String obj) {
		mObject = obj;
		checkIfVariable("object", obj);
	}

	public boolean containsVariable(String str) {return mVariables.containsValue(str);}

	public boolean containsVariables(String[] str) {

		return false;


	}

	@Override
	public String toString() {
		return mSubject + " " + mPredicate + " " + mObject;
	}

	@Override
	public RDFTriple clone() {
		return new RDFTriple(mSubject, mPredicate, mObject);
	}

}
