/**
 * Copyright 2014 Antonio Robirosa

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.areco.ant.cobertura;

/**
 * It flushes the cobertura data to the disk. It doesn't have any direct dependency with the cobertura library,
 * in case they aren't present.
 *
 * @author arobirosa
 * 
 */

public class FlushCobertura {

	public static int main(String[] parameters) {
		try {
			String className = "net.sourceforge.cobertura.coveragedata.ProjectData";
			String methodName = "saveGlobalProjectData";
			Class saveClass = Class.forName(className);
			java.lang.reflect.Method saveMethod = saveClass.getDeclaredMethod(methodName, new Class[0]);
			saveMethod.invoke(null, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace(); //TODO Use log4j.
			return 1; //We ignore any exception.
		}
		return 0;
	}
}
