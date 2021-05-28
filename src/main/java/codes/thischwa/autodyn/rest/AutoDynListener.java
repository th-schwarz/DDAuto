package codes.thischwa.autodyn.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * The main application listener.
 * <p>Job: it triggers the reading and validation of the application data.
 */
@Component
public class AutoDynListener implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private Context context;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		context.readAndValidateData();	
	}
}
