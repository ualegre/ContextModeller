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

public class AggregationGroup {

	public static final int AND_GROUP = 0;
	public static final int OR_GROUP = 1;
	public static final int XOR_GROUP = 2;

	int id;
	int grouptype;
	String aggcontextstate;
	String[] contextstates;

	public static String encode(AggregationGroup group) {
		StringBuilder str = new StringBuilder();
		str.append(String.valueOf(group.id));
		str.append(",");
		str.append(String.valueOf(group.grouptype));
		str.append(",");
		str.append(group.aggcontextstate);
		str.append(",[");
		int size = group.contextstates.length;
		for(int i=0; i<size;i++) {
			if (i >0) {
				str.append(",");
			}
			str.append(group.contextstates[i]);
		}
		str.append("]");
		return str.toString();
	}

	public static AggregationGroup decode(String str) {
		AggregationGroup group = new AggregationGroup();
		String[] sections = str.split(",");
		if (sections.length<4) {
			return null;
		}
		group.id = Integer.parseInt(sections[0]);
		group.grouptype = Integer.parseInt(sections[1]);
		group.aggcontextstate = sections[2];
		group.contextstates = str.substring(str.indexOf("[") + 1, str.length() - 1).split(",");
		return group;
	}
}
