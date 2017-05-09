import de.hybris.platform.core.Registry
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.task.TaskService
import de.hybris.platform.task.model.ScriptingTaskModel

ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService", ModelService.class)
TaskService taskService = (TaskService) Registry.getApplicationContext().getBean(TaskService.class)

ScriptingTaskModel longRunningTask = modelService.create(ScriptingTaskModel.class)
longRunningTask.setScriptURI("classpath://update-deployment-scripts/20170507_45_LONG_RUNNING_SCRIPT/taskbody/run-for-five-minutes-task.groovy")
taskService.scheduleTask(longRunningTask)
return "ERROR"
