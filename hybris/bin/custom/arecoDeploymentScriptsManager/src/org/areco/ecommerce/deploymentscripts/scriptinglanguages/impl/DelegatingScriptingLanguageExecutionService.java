package org.areco.ecommerce.deploymentscripts.scriptinglanguages.impl;

import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionException;
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

/**
 * This implementation delegates the execution on Hybris' services
 * <p>
 * Created by arobirosa on 04.09.15.
 */
@Service
@Scope("tenant")
public class DelegatingScriptingLanguageExecutionService implements ScriptingLanguageExecutionService {

    @Autowired
    private ScriptingLanguagesService scriptingLanguagesService;

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingScriptingLanguageExecutionService.class);
    private static final String OK_RETURN_VALUE = "OK";

    /**
     * It executes and compiles the given script.
     *
     * @param scriptFile Required
     * @throws ScriptingLanguageExecutionException
     */
    @Override
    public void executeScript(final File scriptFile) throws ScriptingLanguageExecutionException {
        Objects.requireNonNull(scriptFile);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Running the script file '{}", scriptFile.toPath());
        }

        checkSuccessfulResult(runScript(scriptFile));
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    // To avoid interruptions during the update running system, we catch all exceptions
    private Object runScript(final File scriptFile) throws ScriptingLanguageExecutionException {
        final ScriptExecutable executable = this.scriptingLanguagesService.getExecutableByURI(scriptFile.toPath().toAbsolutePath().toUri().toString());
        try {
            return executable.execute().getScriptResult();
        } catch (final Exception e) {
            throw new ScriptingLanguageExecutionException("There was an error executing the code: " + e.getLocalizedMessage(), e);
        }
    }

    private void checkSuccessfulResult(final Object anObject) throws ScriptingLanguageExecutionException {
        if (anObject instanceof String && OK_RETURN_VALUE.equalsIgnoreCase((String) anObject)) {
            return;
        }
        throw new ScriptingLanguageExecutionException("The code didn't return the string '" + OK_RETURN_VALUE + "' but '"
                + anObject + "'. Please check if there was an error.");
    }


}
