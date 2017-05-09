import org.apache.log4j.Logger
import org.areco.ecommerce.deploymentscripts.scriptinglanguages.ScriptingLanguageService

for (int minutes = 1; minutes <= 5; minutes++) {
    Logger.getLogger(ScriptingLanguageService.class).fatal("The long running script will sleep for one minute")
    Thread.sleep(60 * 1000)
}
Logger.getLogger(ScriptingLanguageService.class).fatal("The long running script has finished")
