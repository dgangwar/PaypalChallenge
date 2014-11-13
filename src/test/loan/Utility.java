package test.loan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Utility 
{
	Properties prop ;
	String PropertyFilePath = "D:\\Android\\AndroidTest\\PaypalChallange\\Locator.properties";
	private DesiredCapabilities capabilities;
	private static ChromeDriverService service;
	
	public WebDriver getBrowserDriver(String Browsername) throws IOException
	{
		if(Browsername=="firefox")
		{
			capabilities = DesiredCapabilities.firefox();
			capabilities.setBrowserName("PaypalTestFirefox");
			FirefoxProfile profile = new FirefoxProfile();
			profile.setAcceptUntrustedCertificates(true);
			profile.setPreference("applicationCacheEnabled", false);
			profile.setPreference("webStorageEnabled", false);
			return (new FirefoxDriver(profile));
		}
		
		if(Browsername=="Chrome")
		{
			capabilities = DesiredCapabilities.chrome();
			capabilities.setBrowserName("PaypalTestChrome");
			service = new ChromeDriverService.Builder().usingDriverExecutable(new File("C:\\Users\\dgangwar.ORADEV\\Downloads\\chromedriver_win32_2.3\\chromedriver.exe")).usingAnyFreePort().build();
	        service.start();
	        return new RemoteWebDriver(service.getUrl(),capabilities);
		}
		if(Browsername=="IE")
		{
			capabilities = DesiredCapabilities.firefox();
			capabilities.setBrowserName("PaypalTest");
			return (new FirefoxDriver(capabilities));
		}
		return null;
	}
	public void stopChromeService()
	{
		service.stop();
	}

	//Open and Verify the Load form page UI
	  public void openLoanFormpage(WebDriver driver)
		  {
			  driver.navigate().to(getProperty("AppUrl"));
			  driver.manage().window().maximize();
			  Assert.assertTrue(driver.findElement(By.xpath(getProperty("LoanHeader"))).isDisplayed() && driver.findElement(By.id(getProperty("TextBox_ID_Amount"))).isDisplayed() && driver.findElement(By.id(getProperty("TextBox_ID_LoanAPR"))).isDisplayed()
			  && driver.findElement(By.id(getProperty("TextBox_ID_LoanTerm"))).isDisplayed() && driver.findElement(By.xpath(getProperty("Button_Submit"))).isDisplayed());
			  Assert.assertTrue(getProperty("loanamount_placeholder").equals(driver.findElement(By.id("loanAmount")).getAttribute("placeholder")) &&
					  getProperty("loanInterestrate_placeholder").equals(driver.findElement(By.id("loanAPR")).getAttribute("placeholder")) &&
					  getProperty("loanTerm_placeholder").equals(driver.findElement(By.id("loanTerm")).getAttribute("placeholder")) );
		  
		  }
	  
	//Enter Loan Details
	  public void enterLoanDetails(WebDriver driver,String Amt, String Interest, String Term)
		  {
		  	  
			  driver.findElement(By.id("loanAmount")).sendKeys(Amt);
			  driver.findElement(By.id("loanAPR")).sendKeys(Interest);
			  driver.findElement(By.id("loanTerm")).sendKeys(Term);
			  driver.findElement(By.xpath(getProperty("Button_Submit"))).submit();
			  driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		  }
	  
		  
	//Verified UI of Schedule page
		public void amortizationSchedulePage(WebDriver driver)
		  {			  waitforamortizationPage(driver, "Amortization Schedule");
					  Assert.assertTrue(driver.findElement(By.xpath(getProperty("Principal_Amount_Input"))).isDisplayed() &&
					  driver.findElement(By.xpath(getProperty("RateOfIntersest_Input"))).isDisplayed() &&
					  driver.findElement(By.xpath(getProperty("Term_Input"))).isDisplayed() &&
					  driver.findElement(By.xpath(getProperty("No_Column"))).isDisplayed() &&
					  driver.findElement(By.xpath(getProperty("EMI_Column"))).isDisplayed() &&
					  driver.findElement(By.xpath(getProperty("MonthlyInterest_Column"))).isDisplayed() &&
					  driver.findElement(By.xpath(getProperty("OutstandingPrincipal_Column"))).isDisplayed());
		  }
	
		  
		 //Wait for amortizationPage to open by using Title
	  public void waitforamortizationPage(WebDriver driver,String title)
		  {	  WebDriverWait wait = new WebDriverWait(driver, 10);
			  wait.until(ExpectedConditions.titleIs(title));
		  }
	  
	  //waitforWrongValueAlert
	  public boolean waitforWrongValueAlert(WebDriver driver)
	  {	
		 try{
		  Alert alert = driver.switchTo().alert();
		  alert.accept();}
		 catch(NoAlertPresentException exception)
		 { return false;}
	  	return true;
	  }
	  
	  //Return Property value
	  public String getProperty(String key)
	  {
		  prop = loadProperty(PropertyFilePath,prop);
		  return prop.getProperty(key);
	  }
	  
	  public Map<Integer, ArrayList<String>> storeTableData(WebDriver driver)
	  {
		  Map<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
		  
		  int Row    = driver.findElements(By.xpath(getProperty("Table_ROW"))).size();
		  int Column = driver.findElements(By.xpath(getProperty("Table_Header"))).size();
		  for(int i=1; i<=Row; i++)
		  {
			  ArrayList<String> list = new ArrayList<String>(); 
			  for (int j = 2; j <=Column; j++) 
			  {
				  if(i==1)
				  {
				   list.add(driver.findElement(By.xpath("//table/tbody/tr/td["+j+"]")).getText());
				  // System.out.println(list);
				   } else
				  {
				   list.add(driver.findElement(By.xpath("//table/tbody/tr["+i+"]/td["+j+"]")).getText());				   
				  }
				}
			  //System.out.println(list);
			  map.put(i, list);
			 //list.clear();
		  }
		  return map;
	  }
	  
	  public  Properties loadProperty(String filePath, Properties property)
	  {
		  property = new Properties();
		   try {		  
			  property.load(new FileInputStream(new File(filePath)));
			 	} 
		catch (IOException e) {
			e.printStackTrace();
		}	 
		   return property;
	  }
	 //Format value till two decimal point
	  public static double roundToTwoDecimals(double value)
		{
			DecimalFormat dnf = new DecimalFormat( "00.00" );		
			return new Double(dnf.format(value)).doubleValue();
		}
		
	  //Check Int or Double value
		public static String checkIntorDouble(Double d)
		{
			int i = d.intValue();
			if (i != d) 
			{
			  //System.err.println("User entered a Double number.");
			  return d+"";
			} else 
			{
			  //System.err.println("User entered an integer.");
			 return i+"";
			}
		
		}
		
		//Generate Schedule Table
		public HashMap<Integer, ArrayList<String>> scheduleTableDataGenerator(double currentPrincipal,  double yearlyInterestRate, double noOfTermMonths )
		{
			HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
					
		  noOfTermMonths= noOfTermMonths*12;
		  double monthlyInterestRate = yearlyInterestRate/(12*100);
		  double monthlyPayment = roundToTwoDecimals(currentPrincipal*(monthlyInterestRate/(1-Math.pow(1/(1+monthlyInterestRate),noOfTermMonths))));
		  double currentMonthlyInterest = 0;
		  double monthlyPrincipalPayment = 0;
		  double totalInterestPayment = 0;
		  int counter =1;
		  ArrayList<String> monthlyLoanData;
		  while(currentPrincipal >=0)
		  {
		  	monthlyLoanData = new ArrayList<String>();
		  	if(currentPrincipal>monthlyPayment)
		  		monthlyLoanData.add(checkIntorDouble(roundToTwoDecimals(currentPrincipal)));
		      else	
		    	  monthlyLoanData.add(checkIntorDouble(roundToTwoDecimals(monthlyPayment)));
		      monthlyLoanData.add(checkIntorDouble(roundToTwoDecimals(currentMonthlyInterest)));
		      monthlyLoanData.add(checkIntorDouble(roundToTwoDecimals(currentPrincipal)));
		      //System.out.println("monthlyPayment:" +checkIntorDouble(new Double(roundToTwoDecimals(monthlyPayment)))+" currentMonthlyInterest:"+roundToTwoDecimals(currentMonthlyInterest)+" currentPrincipal:"+roundToTwoDecimals(currentPrincipal));
		      //H=P*J
		      //currentMonthlyInterest = roundToTwoDecimals(currentPrincipal * monthlyInterestRate);	 
		      currentMonthlyInterest = roundToTwoDecimals(currentPrincipal * monthlyInterestRate);	
		      totalInterestPayment = totalInterestPayment + currentMonthlyInterest;
		      //C=M-H
		      monthlyPrincipalPayment = monthlyPayment - currentMonthlyInterest;
		      //Q=P-C
		      currentPrincipal = currentPrincipal-monthlyPrincipalPayment;
		      map.put(counter, monthlyLoanData);
		      counter++;
		  }
		  return map;
		}
	
}
