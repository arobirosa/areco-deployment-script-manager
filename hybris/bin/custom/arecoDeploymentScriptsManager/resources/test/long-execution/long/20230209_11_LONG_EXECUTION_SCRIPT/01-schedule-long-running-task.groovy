import org.apache.log4j.Logger

for (int times = 1; times <= 6; times++) {
    Logger.getLogger(this.getClass()).error("The long running script will sleep for 5 seconds")
    try {
        Thread.sleep(5 * 1000)
    } catch (InterruptedException ie) {
        Logger.getLogger(this.getClass()).error("The long running script has interrupted", ie)
    }
}
Logger.getLogger(this.getClass()).error("The long running script has finished")
return "OK"