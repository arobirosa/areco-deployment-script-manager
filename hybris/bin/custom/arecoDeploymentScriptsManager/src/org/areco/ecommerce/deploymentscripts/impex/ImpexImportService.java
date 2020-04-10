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
package org.areco.ecommerce.deploymentscripts.impex;

import java.io.File;

/**
 * This service is responsible for importing the impex files of the deployment scripts.
 * 
 * @author arobirosa
 * 
 */
public interface ImpexImportService {
    /**
     * Imports the given file and returns an importResult. It throws an exception if there was an error and the caller has to manage it.
     * 
     * @param impexFile Required
     * @throws ImpexImportException If any error during the import process
     */
    void importImpexFile(File impexFile) throws ImpexImportException;
}
