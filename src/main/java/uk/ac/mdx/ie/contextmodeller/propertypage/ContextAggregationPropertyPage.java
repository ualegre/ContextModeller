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

public class ContextAggregationPropertyPage implements IPropertyContent {

	@Override
	public void changeProperty(ModelElement element, int row, String value) {

		/*MObject e = element;

		List<Classifier> parent = (List<Classifier>) e.getCompositionOwner().getCompositionOwner().getCompositionOwner().getCompositionChildren();

		for (Classifier p : parent) {
			if (p.isStereotyped(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_MODEL)) {
				List<MObject> obs = (List<MObject>) p.getCompositionChildren();
				System.out.println(obs);
			}

		}*/

		if (row == 1) {
			ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Aggr_type", value,
					element);
		}

		if (row == 2) {
			ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Aggr_temporal", value,
					element);
		}

	}

	@Override
	public void update(ModelElement element, IModulePropertyTable table) {

		table.addProperty("Aggregation Operator",
				ModelUtils.getTaggedValue("Aggr_type", element));
		table.addProperty("Aggregation Temporal Constraints",
				ModelUtils.getTaggedValue("Aggr_temporal", element));

	}

}
