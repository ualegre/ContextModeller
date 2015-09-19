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

import org.modelio.api.log.ILogService;

public class CMLogger {

	private ILogService logService;
	private ContextModellerModule module;

	public CMLogger(ILogService service, ContextModellerModule mod) {
		logService = service;
		module = mod;
	}

	public void info(String msg) {
		this.logService.info(this.module, msg);
	}

	public void warning(String msg) {
		this.logService.warning(this.module, msg);
	}

	public void error(String msg) {
		this.logService.error(this.module, msg);
	}

	public void info(Throwable e) {
		this.logService.info(this.module, e);
	}

	public void warning(Throwable e) {
		this.logService.warning(this.module, e);
	}

	public void error(Throwable e) {
		this.logService.error(this.module, e);
	}

}
