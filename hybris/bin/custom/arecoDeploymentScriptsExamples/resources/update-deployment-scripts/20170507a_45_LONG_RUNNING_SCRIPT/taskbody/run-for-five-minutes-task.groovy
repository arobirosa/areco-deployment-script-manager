import org.apache.log4j.Logger
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageService

for (int minutes = 1; minutes <= 5; minutes++) {
    Logger.getLogger(ScriptingLanguageService.class).info("The long running script will sleep for one minute")
    try {
        Thread.sleep(60 * 1000)
    } catch (InterruptedException ie) {
        Logger.getLogger(ScriptingLanguageService.class).error("The long running script has interrupted", ie)
    }
}
Logger.getLogger(ScriptingLanguageService.class).info("The long running script has finished")
