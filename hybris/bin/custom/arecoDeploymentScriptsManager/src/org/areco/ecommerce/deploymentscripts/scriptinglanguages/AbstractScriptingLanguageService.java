package org.areco.ecommerce.deploymentscripts.scriptinglanguages;

import org.apache.log4j.Logger;

/**
 * Useful methods for the services.
 *
 * Created by arobirosa on 04.09.15.
 */
public abstract class AbstractScriptingLanguageService implements ScriptingLanguageService {

  private static final Logger LOG = Logger.getLogger(AbstractScriptingLanguageService.class);

  /**
   * It executes and compiles the given script.
   *
   * @param code
   * @throws ScriptingLanguageExecutionException
   */
  @Override
  public void executeScript(final String code) throws ScriptingLanguageExecutionException {
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException("The parameter code cannot be null");
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Running the code '" + code + "'.");
    }
    checkSuccessfulResult(compileAndExecute(code));
  }

  private void checkSuccessfulResult(final Object anObject) throws ScriptingLanguageExecutionException {
    if (anObject instanceof String && "OK".equalsIgnoreCase((String) anObject)) {
      return;
    }
    throw new ScriptingLanguageExecutionException("The code didn't return the string 'OK' but '"
                                                    + anObject + "'. Please check if there was an error.");
  }

  protected abstract Object compileAndExecute(String code) throws ScriptingLanguageExecutionException;

}
