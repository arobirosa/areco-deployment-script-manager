import org.slf4j.LoggerFactory;

for (int times = 1; times <= 6; times++) {
    LoggerFactory.getLogger(this.getClass()).error("The long running script will sleep for 5 seconds")
    try {
        Thread.sleep(5 * 1000)
    } catch (InterruptedException ie) {
        LoggerFactory.getLogger(this.getClass()).error("The long running script has interrupted", ie)
    }
}
LoggerFactory.getLogger(this.getClass()).error("The long running script has finished")
return "OK"
