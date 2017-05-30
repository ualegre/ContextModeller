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

public class SourceRuleAssociationPropertyPage implements IPropertyContent {

	@Override
	public void changeProperty(ModelElement element, int row, String value) {

		if (row == 1) {
			ModelUtils.addValue(Utils.CONTEXT_MODELLER, "SR_every", value,
					element);
		} else if (row == 2) {
			ModelUtils.addValue(Utils.CONTEXT_MODELLER, "SR_for", value,
					element);
		}

	}

	@Override
	public void update(ModelElement element, IModulePropertyTable table) {

		table.addProperty("Every",
				ModelUtils.getTaggedValue("SR_every", element));
		table.addProperty("For",
				ModelUtils.getTaggedValue("SR_for", element));

	}

}
