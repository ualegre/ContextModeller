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

import java.util.Map;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.api.module.lifecycle.DefaultModuleLifeCycleHandler;
import org.modelio.api.module.lifecycle.ModuleException;
import org.modelio.vbasic.version.Version;



/**
 * Implementation of the IModuleLifeCycleHandler interface.
 * <br>This default implementation may be inherited by the module developers in order to simplify the code writing of the module session.
 */
public class ContextModellerSession extends DefaultModuleLifeCycleHandler {

	private ContextModellerModelChangeHandler modelChangeHandler;

	public ContextModellerSession(IModule module) {
		super(module);
	}


	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#start()
	 */
	@Override
	public boolean start() throws ModuleException {
		// get the version of the module
		Version moduleVersion = this.module.getVersion();

		// get the Modelio log service
		ILogService logService = this.module.getModuleContext().getLogService();

		String message = "Start of " + this.module.getName() + " " + moduleVersion;
		logService.info(message);

		IModelingSession session = this.module.getModelingSession();
		modelChangeHandler = new ContextModellerModelChangeHandler();

		session.addModelHandler(modelChangeHandler);

		return super.start();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#stop()
	 */
	@Override
	public void stop() throws ModuleException {
		super.stop();
	}

	public static boolean install(String modelioPath, String mdaPath) throws ModuleException {
		return DefaultModuleLifeCycleHandler.install(modelioPath, mdaPath);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#select()
	 */
	@Override
	public boolean select() throws ModuleException {
		return super.select();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#unselect()
	 */
	@Override
	public void unselect() throws ModuleException {
		super.unselect();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#upgrade(org.modelio.api.modelio.Version, java.util.Map)
	 */
	@Override
	public void upgrade(Version oldVersion, Map<String, String> oldParameters) throws ModuleException {
		super.upgrade(oldVersion, oldParameters);
	}

}
