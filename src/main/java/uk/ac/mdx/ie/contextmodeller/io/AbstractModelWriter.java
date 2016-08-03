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

package uk.ac.mdx.ie.contextmodeller.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.modelio.metamodel.uml.statik.Package;




public abstract class AbstractModelWriter {

	protected Package model;
	protected File mFile;


	public void setModel(Package package1) {
		model = package1;
	}

	public abstract void write(Package model);

	public void writeToFile(File file) {
		FileOutputStream output = null;
		try {
			if (!file.exists()) file.createNewFile();
			mFile = file;
			output = new FileOutputStream(file);
			output.write(writeToString().getBytes());
			output.flush();
		} catch (IOException e) {
			System.out.println("IOException " + e.toString());
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				System.out.println("IOException " + e.toString());
			}
		}
	}

	public abstract String writeToString();




}
