package de.frittenburger.movievocabulary.process.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.frittenburger.movievocabulary.process.interfaces.Processor;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorService;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorServiceType;
import de.frittenburger.movievocabulary.process.interfaces.ProcessorTask;
import de.frittenburger.movievocabulary.process.model.ProcessorTaskStatus;
import de.frittenburger.movievocabulary.process.model.TaskStatus;

@Service
public class ProcessorImpl implements Processor {

	private static final Logger logger = LogManager.getLogger(ProcessorImpl.class);
	
	private final List<ProcessorTask> queue = new ArrayList<>();
	private final Map<ProcessorServiceType,ProcessorService> services = new HashMap<>();

	private final Thread converter;
	private boolean runnable = true;
	
	@Autowired
	private SrtToTextProcessorServiceImpl srtToTextProcessorService;

	@Autowired
	private TranslateProcessorServiceImpl translateProcessorService;

	
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
					
					
					//no task in queue
					if(task == null) continue;
					
					ProcessorServiceType serviceType = task.getService();
					if(!services.containsKey(serviceType))
						throw new RuntimeException("No Service found for "+task.getName()+" in "+services);
					
					
					logger.info("Start {} {}",ix,task);
					try {
						
					
						task.setProcessorTaskStatus(ProcessorTaskStatus.RUNNING);
						services.get(serviceType).run(task);
						task.setProcessorTaskStatus(ProcessorTaskStatus.FINISHED);

					} catch (Exception e) {
						task.setProcessorTaskStatus(ProcessorTaskStatus.FAILED);
						logger.error(e);
					}
					logger.info("Stop {} {}",ix,task);

				}
			}
		};
	}
	
	
	@PostConstruct
	public void start()
	{
		
		//init
		services.put(ProcessorServiceType.SrtToText, srtToTextProcessorService);
		services.put(ProcessorServiceType.Translate, translateProcessorService);

		converter.start();
		logger.info("Processor started");

	}
	
	@Override
	public void enqueue(ProcessorTask task) {
		
		
		synchronized(queue) {
			task.setProcessorTaskStatus(ProcessorTaskStatus.QUEUED);
			queue.add(task);
		}

	}


	@Override
	public List<TaskStatus> list() {
		List<TaskStatus> taskstatus = new ArrayList<>();
		synchronized(queue) {

			for(int i = 0;i < queue.size();i++)
			{
				ProcessorTask task = queue.get(i);
				TaskStatus status = new TaskStatus();
				taskstatus.add(status);
				status.setName(task.getName()+"#"+i);
				status.setRef("null");
				if(task instanceof ConverterTask) 
					status.setRef(ConverterTask.class.cast(task).getFile().getPath());
				if(task instanceof TranslateTask)
					status.setRef(TranslateTask.class.cast(task).getFile().getPath());
				status.setStatus(task.getProcessorTaskStatus().toString());
				status.setProgress(task.getProgress());
			}
		}
		
		return taskstatus;
	}

}
