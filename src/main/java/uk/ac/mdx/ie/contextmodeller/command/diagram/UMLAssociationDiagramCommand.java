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

import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.diagram.IDiagramGraphic;
import org.modelio.api.modelio.diagram.IDiagramHandle;
import org.modelio.api.modelio.diagram.IDiagramLink;
import org.modelio.api.modelio.diagram.ILinkPath;
import org.modelio.api.modelio.diagram.InvalidSourcePointException;
import org.modelio.api.modelio.diagram.tools.DefaultLinkTool;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.metamodel.uml.statik.Association;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Classifier;
import org.modelio.vcore.smkernel.mapi.MObject;

import uk.ac.mdx.ie.contextmodeller.i18n.I18nMessageService;
import uk.ac.mdx.ie.contextmodeller.impl.ContextModellerModule;
import uk.ac.mdx.ie.contextmodeller.util.ModelUtils;
import uk.ac.mdx.ie.contextmodeller.util.Utils;

public class UMLAssociationDiagramCommand extends DefaultLinkTool {
	@Override
	public boolean acceptFirstElement(IDiagramHandle arg0, IDiagramGraphic arg1) {
		if ((arg1 != null) && (arg1.getElement() != null)) {
			MObject element = arg1.getElement();
			return (element.getStatus().isModifiable())
					&& ((element instanceof Classifier));
		}
		return false;
	}

	@Override
	public boolean acceptSecondElement(IDiagramHandle arg0,
			IDiagramGraphic arg1, IDiagramGraphic arg2) {
		return (arg2 != null) && (arg2.getElement() != null)
				&& ((arg2.getElement() instanceof Classifier));
	}

	@Override
	public void actionPerformed(IDiagramHandle representation,
			IDiagramGraphic source, IDiagramGraphic destination,
			IDiagramLink.LinkRouterKind kind, ILinkPath path) {
		IModelingSession session = Modelio.getInstance().getModelingSession();
		IUmlModel model = session.getModel();
		try {
			ITransaction transaction = session
					.createTransaction(I18nMessageService.getString(
							"Info.Session.Create", new String[] { "" }));
			Throwable localThrowable3 = null;
			try {
				Classifier c_source = (Classifier) source.getElement();
				Classifier c_destination = (Classifier) destination
						.getElement();
				int assocType = 0;
				if (c_source.isStereotyped(Utils.CONTEXT_MODELLER,
						Utils.CONTEXT_SOURCE)) {
					if (!c_destination.isStereotyped(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_RULE)) {
						return;
					} else {
						assocType = 1;
					}
				} else if (c_source.isStereotyped(Utils.CONTEXT_MODELLER,
						Utils.CONTEXT_RULE)) {
					if (!c_destination.isStereotyped(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_STATE)) {
						return;
					} else {
						assocType = 2;
					}
				} else if (c_source.isStereotyped(Utils.CONTEXT_MODELLER,
						Utils.CONTEXT_STATE)) {
					if (!c_destination.isStereotyped(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_STATE)) {
						return;
					} else {
						assocType = 3;
					}
				}

				Association association = model.createAssociation();
				AssociationEnd endSource = model.createAssociationEnd();
				AssociationEnd endTarget = model.createAssociationEnd();

				String name = "";
				String endMulMin = "";

				if (assocType == 1) {
					association.addStereotype(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_SR_ASSOCIATION);

					name = "\n Every: \nFor: \n ";
				} else if (assocType == 2) {
					association.addStereotype(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_RS_ASSOCIATION);

					endMulMin = "Rule Undefined";
				} else if (assocType == 3) {
					association.addStereotype(Utils.CONTEXT_MODELLER,
							Utils.CONTEXT_SS_ASSOCIATION);

					name = "AND";
					ModelUtils.addValue(Utils.CONTEXT_MODELLER, "Aggr_type",
							name, association);
				}

				association.setName(name);


				// endSource.setName(c_destination.getName().toLowerCase());
				endSource.setMultiplicityMin("");
				endSource.setMultiplicityMax("");
				endSource.setSource(c_source);
				endSource.setTarget(c_destination);

				endTarget.setMultiplicityMin(endMulMin);
				endTarget.setMultiplicityMax("");
				endTarget.setSource(c_destination);
				endTarget.setTarget(c_source);

				association.getEnd().add(endSource);
				association.getEnd().add(endTarget);

				endSource.setOpposite(endTarget);
				endTarget.setOpposite(endSource);

				endSource.setNavigable(true);
				endTarget.setNavigable(false);

				List<IDiagramGraphic> graphics = representation.unmask(
						association, 0, 0);
				for (IDiagramGraphic graphic : graphics) {
					if ((graphic instanceof IDiagramLink)) {
						IDiagramLink link = (IDiagramLink) graphic;
						link.setRouterKind(kind);
						link.setPath(path);
					}
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
		} catch (InvalidSourcePointException e) {
			ContextModellerModule.logger.error(e);
		} catch (Exception e) {
			ContextModellerModule.logger.error(e);
		}
	}
}
