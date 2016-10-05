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

package uk.ac.mdx.ie.contextmodeller.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextStateGroup {

	public HashMap<String, String> mStates;
	public List<StateTransition> mTransitions;
	public String mStateVariable;
	public String mGroupName;
	public String mInstanceName;
	public boolean isAtomic;
	public String mInit;


	public ContextStateGroup(boolean atomic, String groupname, int varnumber) {

		isAtomic = atomic;

		mStates = new HashMap<String, String>();
		mTransitions = new ArrayList<StateTransition>();
		groupname = groupname.toLowerCase();


		mGroupName = groupname.substring(0, 1).toUpperCase();
		mGroupName += groupname.substring(1);

		mInstanceName = mGroupName + "Instance";
		mStateVariable = "c" + String.valueOf(varnumber);
	}


	public void addContextState(String stateName, int number) {

		String id = "id" + String.valueOf(number);

		int size = mStates.size();

		int i = 0;

		for (Map.Entry<String, String> entry : mStates.entrySet()) {

			String varnumber = String.valueOf(i);

			StateTransition trans = new StateTransition();

			trans.source = id;
			trans.target = entry.getValue();

			if (entry.getKey().equals("UNKNOWN")) {
				trans.synch = "deactivate?";
			} else {
				//trans.synch = "newData?newAtomicContext!";
				trans.synch = "newData?";
			}

			trans.assign = mStateVariable + " = " + number;

			mTransitions.add(trans);

			trans = new StateTransition();

			trans.target = entry.getValue();
			trans.source = id;
			//trans.synch = "newData?newAtomicContext!";
			trans.synch = "newData?";
			trans.assign = mStateVariable + " = " + String.valueOf(size);

			mTransitions.add(trans);

		}

		mStates.put(stateName, id);


	}

}
