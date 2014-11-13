package test.loan;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import test.loan.Utility;

public class PayPalTest {
	private WebDriver driver;
	private Utility utility;

	@BeforeTest
	public void beforeTest() throws IOException {
		utility = new Utility();
		driver = utility.getBrowserDriver("firefox");
		driver.manage().deleteAllCookies();
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
		
	}
	@DataProvider(name="Wrongdata")
	public Object[][] dataProviderNegativeScenario()
	{
		return new Object[][]{{"0","1","1"},{"1","0","1"},{"1","1","0"},{"-1","1","1"},{"1","-1","1"},{"1","1","-1"},{"@#$","1","1"},{"1","ADad","1"},{"1","1","awdjwk"},{"","1","3"},{"1","1",""},{"1","","2"}};
	}
	
	@Test(dataProvider="Wrongdata")
	public void testforNegativeValues(String Loan, String Rate, String Term) {
		utility.openLoanFormpage(driver);
		utility.enterLoanDetails(driver, Loan, Rate, Term);
		AssertJUnit.assertTrue(utility.waitforWrongValueAlert(driver));
		}
	
	
	@DataProvider(name="Validdata")
	public Object[][] dataProviderValidScenario()
	{
		return new Object[][]{{".1","1","1"},{"10",".1","1"},{"10","1",".5"},{"1000","10","10"},{"10000","20","15"}};
	}
	
	@Test(dataProvider="Validdata")
	public void testamortizationSchedulePage(String Loan, String Rate, String Term) {
		utility.openLoanFormpage(driver);
		utility.enterLoanDetails(driver, Loan, Rate, Term);
		utility.amortizationSchedulePage(driver);
		AssertJUnit.assertEquals(utility.storeTableData(driver),utility.scheduleTableDataGenerator(Double.parseDouble(Loan), Double.parseDouble(Rate), Double.parseDouble(Term)));
		}

	
	
	
}
