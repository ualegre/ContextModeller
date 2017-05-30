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

package uk.ac.mdx.ie.contextmodeller.command.explorer;

import java.util.List;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.infrastructure.Profile;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.i18n.I18nMessageService;
import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;
import uk.ac.mdx.ie.contextmodeller.util.CMFactory;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class ContextRuleExplorerCommand extends DefaultModuleCommandHandler {

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {

		IModelingSession session = ContextModellerModule.getInstance().getModuleContext().getModelingSession();
		ITransaction transaction = session.createTransaction(I18nMessageService
				.getString("Info.Session.Create", new String[] { "Activity" }));
		Throwable localThrowable3 = null;
		try {
			for (MObject element : selectedElements) {
				CMFactory.createAndAddSource(element);
			}
		} catch (Throwable localThrowable5) {
			localThrowable3 = localThrowable5;
			throw localThrowable5;
		} finally {
			if (transaction != null)
				if (localThrowable3 != null)
					try {
						transaction.close();
					} catch (Throwable localThrowable2) {
						localThrowable3.addSuppressed(localThrowable2);
					}
				else
					transaction.close();
		}
	}

	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		if (super.accept(selectedElements, module)) {
			return (selectedElements.size() > 0)
					&& (Utils.accept(selectedElements.get(0)));
		}
		return false;
	}

	@Override
	public boolean isActiveFor(List<MObject> selectedElements, IModule module) {
		MObject selectedElt = selectedElements.get(0);
		if ((!(selectedElt instanceof Profile))
				&& (!(selectedElt instanceof IModule))) {
			if ((selectedElt instanceof Class)) {
				if (((Class) selectedElt).isStereotyped("ContextModeller",
						"ContextSource"))
					;
			}
		} else
			return false;

		return selectedElt.getStatus().isModifiable();
	}

}
