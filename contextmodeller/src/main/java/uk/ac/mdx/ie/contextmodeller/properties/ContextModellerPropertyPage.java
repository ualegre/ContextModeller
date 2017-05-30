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

package uk.ac.mdx.ie.contextmodeller.properties;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.propertiesPage.AbstractModulePropertyPage;
import org.modelio.api.module.propertiesPage.IModulePropertyTable;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.propertypage.ContextModellerPropertyManager;

public class ContextModellerPropertyPage extends AbstractModulePropertyPage {

	public ContextModellerPropertyPage(IModule mdac, String name, String label,
			String bitmap) {
		super(mdac, name, label, bitmap);
	}

	@Override
	public void changeProperty(List<MObject> selectedElements, int row,
			String value) {
		if ((selectedElements != null) && (selectedElements.size() > 0)
				&& ((selectedElements.get(0) instanceof ModelElement))) {
			ModelElement element = (ModelElement) selectedElements.get(0);

			ContextModellerPropertyManager cMPage = new ContextModellerPropertyManager();
			cMPage.changeProperty(element, row, value);
		}

	}

	@Override
	public void update(List<MObject> selectedElements,
			IModulePropertyTable table) {
		if ((selectedElements != null) && (selectedElements.size() > 0)
				&& (selectedElements.get(0) != null)
				&& ((selectedElements.get(0) instanceof ModelElement))) {
			ModelElement element = (ModelElement) selectedElements.get(0);

			ContextModellerPropertyManager cMPage = new ContextModellerPropertyManager();
			cMPage.update(element, table);
		}

	}

}
