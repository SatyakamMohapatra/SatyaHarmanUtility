/*
 * @(#)TimerUtility.java      
 *
 * Copyright (c) 2002 Symphony Services. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Symphony Services, ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Symphony Services.
 *
 * SYMPHONY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SYMPHONY SERVICES SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version    3.2
 * @author     Kiran Kumar
 */


package util;

import java.util.Date;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/**
 * Utility that can be used to get execution time of a task.start the timer using the startTimer()
 * before starting the task and stop the timer using stopTimer() after the task has been completed.
 * then call getTimeSpent() to get the execution time in seconds
 */
public class TimerUtility 
{
	private long startTime;
	private long stopTime;
	private long pauseTime;
	private long timeBeforePause;
	private boolean isTimerStarted;
	private boolean isTimerPaused;
	private AppLinkLogger logger = LogManager.getLogger(IModules.SERVER, this.getClass().getName());
	private AppLinkLogger asLogger = LogManager.getLogger(IModules.ASCSCONNECTION,this.getClass().getName());
	//1 -debug,2-Info,3-warning,4-error,5-fatal
	int logLevel = 1;
	int asLogLevel = 2;
	/**
	 *  @param logLevel 1 -debug,2-Info,3-warning,4-error,5-fatal
	 */
	public TimerUtility(int logLevel)
	{
		this.logLevel = logLevel;
	}

	/**
	 * uses the default log level i.e 2-Info
	 *
	 */
	public TimerUtility()
	{
		this(2);
	}
	
	/**
	 * 
	 * @param logLevel 1 -debug,2-Info,3-warning,4-error,5-fatal
	 */
	public void setLogLevel(int logLevel)
	{
		this.logLevel = logLevel;
	}
	/**
	 * this starts the timer
	 */
	public void startTimer()
	{
		if(!isTimerStarted || isTimerPaused)
		{
			if(isTimerPaused)
			{
				timeBeforePause = pauseTime-startTime;
			}
			startTime = System.currentTimeMillis();
			isTimerStarted = true;
			isTimerPaused = false;
		}else
		{
			logMessage("Timer is already running");
		}
	}
	
	/**
	 * this starts the AS timer
	 */
	public void startASTimer()
	{
		if(!isTimerStarted || isTimerPaused)
		{
			if(isTimerPaused)
			{
				timeBeforePause = pauseTime-startTime;
			}
			startTime = System.currentTimeMillis();
			isTimerStarted = true;
			isTimerPaused = false;
		}else
		{
			logASMessage("Timer is already running");
		}
	}
	
	/**
	 * stops the timer
	 */
	public void stopTimer()
	{
		if(isTimerStarted)
		{
			if(isTimerPaused)
			{
				stopTime = pauseTime;	
			}else
			{
				stopTime = System.currentTimeMillis();
			}
			isTimerStarted = false;
			isTimerPaused = false;
		}else
		{
			logMessage("Start the timer to stop");
		}
	}
	
	/**
	 * stops the AS timer
	 */
	public void stopASTimer()
	{
		if(isTimerStarted)
		{
			if(isTimerPaused)
			{
				stopTime = pauseTime;	
			}else
			{
				stopTime = System.currentTimeMillis();
			}
			isTimerStarted = false;
			isTimerPaused = false;
		}else
		{
			logASMessage("Start the timer to stop");
		}
	}
	/**
	 * pauses the timer
	 */
	public void pauseTimer()
	{
		if(isTimerStarted && !isTimerPaused)
		{
			isTimerPaused = true;
			pauseTime = System.currentTimeMillis();
		}else
		{
			logMessage("Start the timer to pause");
		}
	}
	/**
	 * resets the timer..
	 */
	public void resetTimer()
	{
		isTimerStarted = false;
		startTime = 0;
		stopTime = 0;	
		startTimer();
	}
	/**
	 * returns the time spent in milli seconds
	 */
	//returns in milli sec;
	public double getTimeSpent()
	{
		long time =0;
		if(isTimerStarted)
		{
			time = System.currentTimeMillis()-startTime+timeBeforePause;
		}else
		{
			time = stopTime - startTime+timeBeforePause;
		}
		
		return time;
	}
	/**
	 * prints the start time ,stop time and the execution time
	 */
	public void printHistory()
	{
		logMessage("Start Time = "+new Date(startTime-timeBeforePause));
		logMessage("Stop Time = "+new Date(stopTime));	
		logMessage("Time Spent in Milli Seconds = "+getTimeSpent());	
	}

	public void printTimeSpentWithDesc(String desc)
	{
		logMessage("$$$$The following is the time spent for " + desc +" >> "+ getTimeSpent()+" milliseconds");
	}
	
	public void printTimeSpentForASResponse(String desc)
	{
		logASMessage(desc +" >> "+ getTimeSpent()+" milliseconds");
	}
	/*
	 * This method will be used for performance profiling.
	 */
	public void printTimeSpent(String desc)
	{
		logMessage("$$$$Time taken for " + desc +" >> "+ getTimeSpent()+" milliseconds");
	}

	public void printHistoryWithDesc(String desc)
	{
		logMessage("$$$$The following is the time spent for " + desc + " >>Start Time = >>"+new Date(startTime-timeBeforePause) + ">>Stop Time = >>"+new Date(stopTime) + " >>Time Spent in Milli Seconds = "+getTimeSpent()+"ms");
	}
	public static void main(String[] args)throws Exception 
	{
		TimerUtility timer = new TimerUtility();
		timer.startTimer();
		Thread.sleep(10000);
		timer.pauseTimer();
		Thread.sleep(10000);		
		timer.startTimer();
		Thread.sleep(10000);
		timer.stopTimer();
		timer.printHistory();
	}
	
 	private void logMessage(String message)
 	{
 		switch(logLevel)
 		{
 			case 1	: logger.debug(message, 5);break;
			case 2	: logger.info(message, 5);break;
			case 3	: logger.warn(message, 5);break;
			case 4	: logger.error(message, 5);break;
			case 5	: logger.fatal(message, 5);break;
			default : logger.info(message, 5);break;
 		}
 	}
 	
 	private void logASMessage(String message)
 	{
 		switch(asLogLevel)
 		{
 			case 1	: asLogger.debug(message, 5);break;
			case 2	: asLogger.info(message, 5);break;
			case 3	: asLogger.warn(message, 5);break;
			case 4	: asLogger.error(message, 5);break;
			case 5	: asLogger.fatal(message, 5);break;
			default : asLogger.info(message, 5);break;
 		}
 	}
}
