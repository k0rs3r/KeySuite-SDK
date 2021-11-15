package it.kdm.orchestratore.startup;

import java.io.File;

import it.kdm.doctoolkit.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileSystemCheck {

	private static final Logger logger = LoggerFactory.getLogger(FileSystemCheck.class);

	public void init() {
		logger.info("initConfig method initConfig");

		try {

			logger.info("initConfig file system check procedure");
			
			String sep = System.getProperty("file.separator");
			
			
			// Creazione delle directories per AppBPM
			File f1 = new File(Utils.getConfigHome(), "upload");
			
			if (!f1.exists()) {
				FileUtils.forceMkdir(f1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();  
			logger.error("Starting bpm-server error.",e);
		}

		logger.info("method initConfig end");
	}

}
