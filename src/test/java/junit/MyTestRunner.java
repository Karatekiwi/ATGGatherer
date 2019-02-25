package junit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestCategorizer.class, TestWikiParser.class, TestCategories.class })
public class MyTestRunner {

    @BeforeClass 
    public static void setUpClass() {      
        

    }

    @AfterClass 
    public static void tearDownClass() { 
        
    }

}