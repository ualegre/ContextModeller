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

import java.util.List;

import org.modelio.api.model.IMetamodelExtensions;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.propertiesPage.IModulePropertyTable;
import org.modelio.metamodel.Metamodel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Association;
import org.modelio.metamodel.uml.statik.Class;

import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class ContextModellerPropertyManager implements IPropertyContent {

	@Override
	public void changeProperty(ModelElement paramModelElement, int paramInt,
			String paramString) {

		List<Stereotype> sterList = Utils
				.computePropertyList(paramModelElement);

		for (Stereotype ster : sterList) {

			IPropertyContent propertypage = getPropertyPage(ster);

			if (propertypage != null) {
				propertypage.changeProperty(paramModelElement, paramInt,
						paramString);
				// paramInt -= ster.getDefinedTagType().size();
				propertypage = null;
			}
		}
	}

	@Override
	public void update(ModelElement paramModelElement,
			IModulePropertyTable paramIModulePropertyTable) {

		List<Stereotype> sterList = Utils
				.computePropertyList(paramModelElement);

		for (Stereotype ster : sterList) {

			IPropertyContent propertypage = getPropertyPage(ster);

			if (propertypage != null) {
				propertypage.update(paramModelElement,
						paramIModulePropertyTable);
				propertypage = null;
			}
		}

	}

	public static IPropertyContent getPropertyPage(Stereotype ster) {

		IPropertyContent propertypage = null;
		IMetamodelExtensions extensions = Modelio.getInstance()
				.getModelingSession().getMetamodelExtensions();

		if (ster.equals(extensions.getStereotype(Utils.CONTEXT_MODELLER,
				Utils.CONTEXT_SOURCE, Metamodel.getMClass(Class.class)))) {
			propertypage = new ContextSourcePropertyPage();
		} else if (ster.equals(extensions.getStereotype(Utils.CONTEXT_MODELLER,
				Utils.CONTEXT_RULE, Metamodel.getMClass(Class.class)))) {
			propertypage = new ContextRulePropertyPage();
		} else if (ster.equals(extensions.getStereotype(Utils.CONTEXT_MODELLER,
				Utils.CONTEXT_SS_ASSOCIATION,
				Metamodel.getMClass(Association.class)))) {
			propertypage = new ContextAggregationPropertyPage();
		} else if (ster.equals(extensions.getStereotype(Utils.CONTEXT_MODELLER,
				Utils.CONTEXT_SR_ASSOCIATION,
				Metamodel.getMClass(Association.class)))) {
			propertypage = new SourceRuleAssociationPropertyPage();
		}

		return propertypage;

	}

}
