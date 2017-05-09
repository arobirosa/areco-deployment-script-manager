import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.task.TaskService
import de.hybris.platform.task.model.ScriptingTaskModel

ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService", ModelService.class)
TaskService taskService = (TaskService) Registry.getApplicationContext().getBean(TaskService.class)

ScriptingTaskModel longRunningTask = modelService.create(ScriptingTaskModel.class)
longRunningTask.setScriptURI("classpath://update-deployment-scripts/20170507e_45_LONG_RUNNING_SCRIPT/taskbody/run-for-three-minutes-task.groovy")
taskService.scheduleTask(longRunningTask)
Thread.sleep(10 * 1000) //Wait for the task to be scheduled
return "OK"
