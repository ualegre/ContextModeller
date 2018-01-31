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

package uk.ac.mdx.ie.contextmodeller.propertypage;

import org.modelio.api.module.propertiesPage.IModulePropertyTable;
import org.modelio.metamodel.uml.infrastructure.ModelElement;

import uk.ac.mdx.ie.contextmodeller.util.ModelUtils;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class ContextRulePropertyPage implements IPropertyContent {

	@Override
	public void changeProperty(ModelElement element, int row, String value) {

		switch (row) {
			case 1: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_predicate",
					value, element);
					break;
			case 2: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_logicalEvals",
					value, element);
					break;

			case 3: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_method1", value, element);
					break;

			case 4: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_triple1", value, element);
					break;

			case 5: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_methodExpr1", value, element);
					break;

			case 6: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_method2", value, element);
					break;

			case 7: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_triple2", value, element);
					break;

			case 8: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_methodExpr2", value, element);
					break;

			case 9: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_method3", value, element);
					break;

			case 10: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_triple3", value, element);
					break;

			case 11: ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Rule_methodExpr3", value, element);
					break;
		}

	}

	@Override
	public void update(ModelElement element, IModulePropertyTable table) {

		table.addProperty("Predicate", ModelUtils.getTaggedValue("Rule_predicate",element));
		table.addProperty("Filter",
				ModelUtils.getTaggedValue("Rule_logicalEvals", element));

		String method = "Rule_method";
		String methodtriples = "Rule_triple";
		String methodExpr = "Rule_methodExpr";

		String f_method = ModelUtils.getTaggedValue("Rule_method1",element);
		

		table.addProperty("Method", f_method);
		table.addProperty("Method Triple Var", ModelUtils.getTaggedValue("Rule_triple1",element));
		table.addProperty("Method Result Expression", ModelUtils.getTaggedValue("Rule_methodExpr1", element));

		String value = f_method;
		for (int i=2;i<4;i++) {

			StringBuilder newMethod = new StringBuilder(method);
			StringBuilder newMethodTriples = new StringBuilder(methodtriples);
			StringBuilder newMethodExpr = new StringBuilder(methodExpr);

			String index = String.valueOf(i);


			if (! value.isEmpty()) {
				newMethod.append(index);
				newMethodTriples.append(index);
				newMethodExpr.append(index);

				value = ModelUtils.getTaggedValue(newMethod.toString(), element);

				table.addProperty("Method " + index, value);
				table.addProperty("Method " + index + " Triple Var", ModelUtils.getTaggedValue(newMethodTriples.toString(), element));
				table.addProperty("Method " + index + " Result Expression", ModelUtils.getTaggedValue(newMethodExpr.toString(), element));

			} else {
				return;
			}
		}


	}

}
