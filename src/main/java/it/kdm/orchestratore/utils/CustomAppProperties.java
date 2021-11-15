package it.kdm.orchestratore.utils;

import it.kdm.doctoolkit.zookeeper.ApplicationProperties;

public class CustomAppProperties {
	/*private static final Logger logger = LoggerFactory.getLogger(CustomAppProperties.class);
	private static Properties props =null;
	
	private static void getPropertiesInstance() throws IOException {
		logger.info("initConfig method getPropertiesInstance");
		InputStream file =null;
		String sep = System.getProperty("file.separator");
		String pathDirectory = System.getProperty("user.home") + sep + "bpm-config";
		String filename = "custom-app.properties";


		File f = Utils.getConfigHome();
		
		if (!f.exists()) {
			FileUtils.forceMkdir(f);
		}
		
		File pathFile = new File(f, filename);
		// First try loading from the current directory
        try {
			try {
                file = new FileInputStream(pathFile);
			}catch(FileNotFoundException e){
				logger.info("method getPropertiesInstance: file non esiste nella directory" + System.getProperty("user.home"));
                logger.info("method getPropertiesInstance: initConfig copy...");
                logger.info("file non esiste nella directory"+ System.getProperty("user.home"));
                try(InputStream is = CustomAppProperties.class.getResourceAsStream("/"+filename)) {
                    FileUtils.copyInputStreamToFile(is, pathFile);
                }
                file = new FileInputStream(pathFile);
                logger.info("method getPropertiesInstance: end copy...");

			}

			props = new Properties();
			props.load(file);
		} catch ( Exception e ) {
			logger.error("end method getPropertiesInstance with exception:"+e.getMessage());
			throw new RuntimeException(e.getMessage());
		} finally {
            if (file != null) {
                file.close();
            }
        }
		logger.info("end method getPropertiesInstance");
	}*/
	
	public static String getBasePath(){
		return getParam("custom-apps.path","/root/bpm-config/custom-apps/");
	}
	
	public static String getParam(String key,String defaultValue) {
        return ApplicationProperties.getInstance("custom-app.properties").getPropertyByKey(key, defaultValue);
	}


}
