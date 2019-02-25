package launch;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;





public class Main {

    public static void main(String[] args) throws Exception {

        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        

        tomcat.setPort(Integer.valueOf(webPort));

        Context context = tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        
        WebappLoader solrLoader = new WebappLoader(Main.class.getClassLoader()); context.setLoader(solrLoader); 
        		
        //System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        /*
        JobKey jobKeyA = new JobKey("update", "group1");
        JobDetail jobA = JobBuilder.newJob(CronJob.class).withIdentity(jobKeyA).build();

        Trigger trigger1 = TriggerBuilder
                .newTrigger()
                .withIdentity("identity", "group1")
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * 1 *")) //every monday at 00:00 o'clock
                .withSchedule(CronScheduleBuilder.cronSchedule("0 00 18 6 9 ? 2014")) 
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();

        scheduler.start();
        scheduler.scheduleJob(jobA, trigger1);*/

        tomcat.start();
        tomcat.getServer().await();  
    	
        
        
    }
    
}
