package de.frittenburger.movievocabulary.process.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.frittenburger.movievocabulary.process.interfaces.Processor;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;

public class ProcessorImpl implements Processor {

	private static final Logger logger = LogManager.getLogger(ProcessorImpl.class);

	private final List<ProcessorTask> queue = new ArrayList<>();
	private final Thread converter;
	private boolean runnable = true;
	
	public ProcessorImpl()
	{
		converter = new Thread(){

			@Override
			public void run()
			{
				int ix = 0;
				while(runnable)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error(e);
					}
					ProcessorTask task = null;
					synchronized(queue)
					{
						if(ix < queue.size()) 
						{
							task = queue.get(ix++);
						}
					}
					
					if(task == null) continue;
					
					logger.info("Start {} Converting Id={}",ix,task.getName());
					try {
						task.run();
					} catch (Exception e) {
						logger.error(e);
					}
					logger.info("Stop {} Converting Id={}",ix,task.getName());

				}
			}
		};
		converter.start();
	}
	
	@Override
	public void enqueue(ProcessorTask task) {
		synchronized(queue) {
			queue.add(task);
		}

	}

}
