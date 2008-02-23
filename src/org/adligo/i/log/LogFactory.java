package org.adligo.i.log;

import java.util.Hashtable;

public class LogFactory {
	public static final String LOG_FACTORY_IMPL = "log_factory_class";
	
	public static Log getLog(Class clazz) {
		return instance.getLog(clazz);
	}
	
	protected static final I_LogFactory instance = createInstance();
	
	/**
	 * @return
	 */
	protected static I_LogFactory createInstance() {
		I_LogFactory toRet = null;
		
		try {
			Hashtable adligoLogProps = LoadProperties.loadProperties("/adligo_log.properties", true);		
			String logClass = (String) adligoLogProps.get(LOG_FACTORY_IMPL);
			if (logClass != null) {
				
					toRet = createLogFactoryAdaptorFromClassName(logClass);
				
			}
		} catch (Exception x) {
			//x.printStackTrace();
			// this will recover, no need to log it
		}
		
		if (toRet == null) {
			try {
				String clazzName = System.getProperty(LOG_FACTORY_IMPL);
				toRet = createLogFactoryAdaptorFromClassName(clazzName);
			} catch (Exception x) {
				//do nothing
			}
		}
		/*
		System.out.println("found " + toRet + " from System property " +
					Registry.LOG_IMPLEMTAION_CLASS);
					*/
		if (toRet == null) {
			try {
				/*
				 * j2ME doesn't have javax naming packages 
				 */
				String prop = System.getProperty("microedition.configuration");
				//System.out.println("wtf its '" + prop + "'" );
				if (null == prop) {
					/*
					javax.naming.InitialContext context = new javax.naming.InitialContext();
					toRet = (I_LogFactoryAdaptor) context.lookup(
							Registry.LOG_IMPLEMTAION_CLASS);
							*/
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		
		if (toRet == null) {
			System.err.print("Wasn't able to locate a user configured " +
					LOG_FACTORY_IMPL + " Falling back to default " +
					AdligoLogFactory.class.getName());
			toRet = new AdligoLogFactory();
		}
		return toRet;
	}
	

	
	/**
	 * Do a class.ForName and create a instance
	 * @param toRet
	 * @param clazzName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected static I_LogFactory createLogFactoryAdaptorFromClassName(
			String clazzName) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		I_LogFactory toRet = null;
		if (clazzName != null) {
			//System.out.println("attempting instaniate " + clazzName);
			Class clazz = Class.forName(clazzName);
			//System.out.println("got clazz " + clazz);
			toRet = (I_LogFactory) clazz.newInstance();
			//System.out.println("got instance " + toRet);
		}
		return toRet;
	}

}
