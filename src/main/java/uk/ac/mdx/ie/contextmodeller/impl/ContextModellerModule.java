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

package uk.ac.mdx.ie.contextmodeller.impl;

import org.modelio.api.modelio.Modelio;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.AbstractJavaModule;
import org.modelio.api.module.context.configuration.IModuleAPIConfiguration;
import org.modelio.api.module.context.configuration.IModuleUserConfiguration;
import org.modelio.api.module.lifecycle.IModuleLifeCycleHandler;
import org.modelio.api.module.parameter.IParameterEditionModel;
import org.modelio.metamodel.mda.ModuleComponent;

/**
 * Implementation of the IModule interface. <br>
 * All Modelio java modules should inherit from this class.
 *
 */
public class ContextModellerModule extends AbstractJavaModule {

	private ContextModellerPeerModule peerModule = null;

	private ContextModellerSession session = null;
	public static CMLogger logger;

	@Override
	public ContextModellerPeerModule getPeerModule() {
		return this.peerModule;
	}



	/**
	 * Method automatically called just after the creation of the module.
	 * <p>
	 * <p>
	 * The module is automatically instanciated at the beginning of the MDA
	 * lifecycle and constructor implementation is not accessible to the module
	 * developer.
	 * <p>
	 * <p>
	 * The <code>init</code> method allows the developer to execute the desired
	 * initialization code at this step. For example, this is the perfect place
	 * to register any IViewpoint this module provides.
	 *
	 *
	 * @see org.modelio.api.module.AbstractJavaModule#init()
	 */
	@Override
	public void init() {
		// Add the module initialization code
		super.init();
	}

	/**
	 * Method automatically called just before the disposal of the module.
	 * <p>
	 * <p>
	 *
	 *
	 * The <code>uninit</code> method allows the developer to execute the
	 * desired un-initialization code at this step. For example, if IViewpoints
	 * have been registered in the {@link #init()} method, this method is the
	 * perfect place to remove them.
	 * <p>
	 * <p>
	 *
	 * This method should never be called by the developer because it is already
	 * invoked by the tool.
	 *
	 * @see org.modelio.api.module.AbstractJavaModule#uninit()
	 */
	@Override
	public void uninit() {
		// Add the module un-initialization code
		super.uninit();
	}

	/**
	 * Builds a new module.
	 * <p>
	 * <p>
	 * This constructor must not be called by the user. It is automatically
	 * invoked by Modelio when the module is installed, selected or started.
	 *
	 * @param modelingSession
	 *            the modeling session this module is deployed into.
	 * @param model
	 *            the model part of this module.
	 * @param moduleConfiguration
	 *            the module configuration, to get and set parameter values from
	 *            the module itself.
	 * @param peerConfiguration
	 *            the peer module configuration, to get and set parameter values
	 *            from another module.
	 */
	public ContextModellerModule(IModelingSession modelingSession,
			ModuleComponent moduleComponent,
			IModuleUserConfiguration moduleConfiguration,
			IModuleAPIConfiguration peerConfiguration) {
		super(modelingSession, moduleComponent, moduleConfiguration);
		this.session = new ContextModellerSession(this);
		this.peerModule = new ContextModellerPeerModule(this, peerConfiguration);
		this.peerModule.init();
		logger = new CMLogger(Modelio.getInstance().getLogService(), this);
	}

	/**
	 * @see org.modelio.api.module.AbstractJavaModule#getParametersEditionModel()
	 */
	@Override
	public IParameterEditionModel getParametersEditionModel() {
		if (this.parameterEditionModel == null) {
			this.parameterEditionModel = super.getParametersEditionModel();
		}
		return this.parameterEditionModel;
	}

	@Override
	public String getModuleImagePath() {
		return "/res/icons/module_16.png";
	}



	@Override
	public IModuleLifeCycleHandler getLifeCycleHandler() {
		// TODO Auto-generated method stub
		return session;
	}

}
