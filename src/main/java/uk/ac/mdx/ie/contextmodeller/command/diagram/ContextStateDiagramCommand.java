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

package uk.ac.mdx.ie.contextmodeller.command.diagram;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.diagram.IDiagramGraphic;
import org.modelio.api.modelio.diagram.IDiagramHandle;
import org.modelio.api.modelio.diagram.IDiagramNode;
import org.modelio.api.modelio.diagram.tools.DefaultBoxTool;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.metamodel.diagrams.AbstractDiagram;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.i18n.I18nMessageService;
import uk.ac.mdx.ie.contextmodeller.util.CMFactory;

public class ContextStateDiagramCommand extends DefaultBoxTool {

	@Override
	public boolean acceptElement(IDiagramHandle representation,
			IDiagramGraphic target) {
		MObject element = target.getElement();

		if ((element instanceof AbstractDiagram)) {
			element = ((AbstractDiagram) element).getOrigin();
			// if ((element == null) ||
			// (!element
			// .getStatus().isModifiable()) || (!(element instanceof Package)))
			// if (!(element instanceof Class)) {
			// return ((Class)element).isStereotyped(Utils.CONTEXT_MODELLER,
			// Utils.CONTEXT_STATE);
			// }

		}

		return true;
	}

	@Override
	public void actionPerformed(IDiagramHandle representation,
			IDiagramGraphic target, Rectangle rect) {

		IModelingSession session = Modelio.getInstance().getModelingSession();
		ITransaction transaction = session.createTransaction(I18nMessageService
				.getString("Info.Session.Create", new String[] { "" }));
		Throwable localThrowable3 = null;
		try {
			MObject element = target.getElement();

			if ((element instanceof AbstractDiagram)) {
				element = ((AbstractDiagram) element).getOrigin();
			}

			Element state = CMFactory.createAndAddState((Package) element);
			List<?> graph = representation.unmask(state, rect.x, rect.y);

			if ((graph != null) && (graph.size() > 0)
					&& ((graph.get(0) instanceof IDiagramNode))) {
				IDiagramNode dnode = ((IDiagramNode) graph.get(0));
				dnode.setBounds(rect);
				dnode.setProperty("REPMODE", "IMAGE");
			}
			representation.save();
			representation.close();
			transaction.commit();
		} catch (Throwable localThrowable1) {
			localThrowable3 = localThrowable1;
			throw localThrowable1;
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

}
