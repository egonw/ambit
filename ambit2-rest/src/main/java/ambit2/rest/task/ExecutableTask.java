package ambit2.rest.task;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.restlet.resource.ResourceException;

import ambit2.rest.task.Task.TaskStatus;

public class ExecutableTask<USERID> extends FutureTask<TaskResult> {
	protected Task<TaskResult,USERID> task;
	
	public Task<TaskResult, USERID> getTask() {
		return task;
	}
	public void setTask(Task<TaskResult, USERID> task) {
		this.task = task;
	}
	public ExecutableTask(Callable<TaskResult> callable,Task<TaskResult,USERID> task) {
		super(callable);
		this.task = task;
	}
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		task.setStatus(TaskStatus.Cancelled);
		task.setTimeCompleted(System.currentTimeMillis());
		task = null;
		return super.cancel(mayInterruptIfRunning);
	}
	@Override
	protected void done() {
		try {
			TaskResult ref = get(100, TimeUnit.MILLISECONDS);
			task.setTimeCompleted(System.currentTimeMillis());
			task.setStatus(TaskStatus.Completed);
			task.setUri(ref);
			
			try {
				task.setPolicy();
			} catch (Exception x) {
				task.setPolicyError(x);
			}
			
		} catch (TimeoutException x) {
		} catch (ExecutionException x) {
			task.setStatus(TaskStatus.Error);
			Throwable err = x.getCause()==null?x:x.getCause();
			if (err instanceof ResourceException) task.setError((ResourceException) err);
			else task.setError(new ResourceException(err));
		} catch (InterruptedException x) {
			task.setError(null);
			task.setStatus(TaskStatus.Cancelled);
		} catch (CancellationException x) {
			task.setError(null);
			task.setStatus(TaskStatus.Cancelled);
		}		
		task = null;
		super.done();
	}
	@Override
	protected void set(TaskResult v) {
		super.set(v);
		task.setUri(v);
	}
	@Override
	public void run() {
		task.setStatus(TaskStatus.Running);
		super.run();
	}
	
	@Override
	protected void setException(Throwable x) {
		super.setException(x);
		task.setStatus(TaskStatus.Error);
		Throwable err = x.getCause()==null?x:x.getCause();
		if (err instanceof ResourceException) task.setError((ResourceException) err);
		else task.setError(new ResourceException(err));
	}
	/*
	public synchronized void update()  {
		
		try {
			if (future!=null) {
				Reference ref = future.get(100, TimeUnit.MILLISECONDS);
				future = null;
				completed = System.currentTimeMillis();
				status = TaskStatus.Completed;
				setUri(ref);
			}
		} catch (TimeoutException x) {
		} catch (ExecutionException x) {
			Throwable err = x.getCause()==null?x:x.getCause();
			if (err instanceof ResourceException) error = (ResourceException) err;
			else error = new ResourceException(err);
			status = TaskStatus.Error;
		} catch (InterruptedException x) {
			error = null;
			status = TaskStatus.Cancelled;
		} catch (CancellationException x) {
			error = null;
			status = TaskStatus.Cancelled;
		}
	}
	*/
	@Override
	public String toString() {
		
		return super.toString();
	}
}
