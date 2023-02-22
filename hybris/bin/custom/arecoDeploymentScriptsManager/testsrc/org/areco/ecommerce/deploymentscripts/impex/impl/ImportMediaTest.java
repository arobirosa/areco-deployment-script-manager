/**
 * Copyright 2014 Antonio Robirosa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.areco.ecommerce.deploymentscripts.impex.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.io.IOUtils;
import org.areco.ecommerce.deploymentscripts.core.AbstractWithConfigurationRestorationTest;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScript;
import org.areco.ecommerce.deploymentscripts.core.DeploymentScriptStarter;
import org.areco.ecommerce.deploymentscripts.testhelper.DeploymentScriptResultAsserter;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * It checks if impex scripts with medias are correctly imported.
 *
 * @author Antonio Robirosa <mailto:areco.manager@areko.consulting>
 */
public class ImportMediaTest extends AbstractWithConfigurationRestorationTest {
    private static final String RESOURCES_FOLDER = "/resources/test";
    public static final String EXPECTED_DATA = "INSERT_UPDATE User;uid[unique=true]";

    @Resource
    private DeploymentScriptStarter deploymentScriptStarter;

    @Resource
    private DeploymentScriptResultAsserter deploymentScriptResultAsserter;

    @Resource
    private MediaService mediaService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Test
    public void testLibraryHeaderImport() throws IOException {
        this.getDeploymentConfigurationSetter().setTestFolders(RESOURCES_FOLDER, "import-medias");
        final boolean wereThereErrors = this.deploymentScriptStarter.runAllPendingScripts();
        Assert.assertFalse("There were errors", wereThereErrors);
        this.deploymentScriptResultAsserter.assertSuccessfulResult("20150315_IMPORT_HEADER_LIBRARY");
        final MediaModel foundMedia = this.mediaService.getMedia(this.catalogVersionService.getCatalogVersion("Default", "Online"), "test_user_export");
        Assert.assertNotNull("The imported media wasn't found", foundMedia);

        try (final InputStream dataStream = this.mediaService.getStreamFromMedia(foundMedia)) {
            final String actualData = IOUtils.toString(dataStream, DeploymentScript.DEFAULT_FILE_ENCODING);
            Assert.assertEquals("The saved data of the media is wrong", EXPECTED_DATA, actualData);
        }
    }
}
