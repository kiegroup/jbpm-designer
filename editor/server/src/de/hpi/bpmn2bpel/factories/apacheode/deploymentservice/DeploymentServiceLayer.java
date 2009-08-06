package de.hpi.bpmn2bpel.factories.apacheode.deploymentservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;

//import lu.ses_astra.goldeneye.stationcontroller.exceptions.DeploymentFailException;
//import lu.ses_astra.goldeneye.stationcontroller.exceptions.InvalidConfigurationException;
//import lu.ses_astra.goldeneye.stationcontroller.exceptions.UndeploymentFailException;
//import lu.ses_astra.goldeneye.stationcontroller.models.EWSDL;
//import lu.ses_astra.goldeneye.stationcontroller.models.SystemConfiguration;
//import lu.ses_astra.goldeneye.stationcontroller.models.XWSDL;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub.Base64Binary;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub.DeployUnit;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub.DeploymentService;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub.DeploymentServicePortType;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub.Package;

import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.log4j.Logger;

public class DeploymentServiceLayer {
	
	private static Logger logger = Logger.getLogger(DeploymentServiceLayer.class);
	
	private DeploymentServicePortType deploymentService = null;
	private String serviceUrl = null;
	
	private static final QName SERVICE_NAME = new QName("http://www.apache.org/ode/deployapi", "DeploymentService");
	
	/**
	 * System-Configuration 
	 */
	private DatabaseConfiguration config;
	
	/**
	 * Deploy a test case. 
	 * @param bpel
	 * 		Bpel process file
	 * @param processWsdl
	 * 		Wsdl for the bpel process
	 * @param deploymentDescriptor
	 * 		Deployment descriptor for the hole test case
	 * @param xwsdlToEwsdlMapping
	 * 		Wsdls for all Webservices they will be invoked
	 * @return
	 * @throws DeploymentFailException
	 * @throws MalformedURLException 
	 * @throws InvalidConfigurationException 
	 */
	public DeployUnit deploy(String token, String bpel, String processWsdl,
			String deploymentDescriptor, HashMap<String, String> wsdls) {
		
		this.connect();
		
		byte[] zippedBytes = zipToByteArray(bpel, processWsdl, 
				deploymentDescriptor, wsdls);
		
		Base64Binary binary = new Base64Binary();
		binary.setValue(zippedBytes);
		binary.setContentType("application/zip");

		Package p = new Package();
		p.setZip(binary);

		try {
			return deploymentService.deploy(token, p);
		} catch (Exception e) {
			logger.error("Unable to deploy zip-file", e);
		}
		return null;
	}	
	
	/**
	 * @param serviceName 
	 * Create zip file from all files contained in the {@link ExecutionPlan}.
	 * After creating the zip file it will be transformed in a byte array.
	 * 
	 * @param bpel
	 * @param processWsdl
	 * @param deploymentDescriptor
	 * @param xwsdlToEwsdlMapping
	 * 
	 * @return the byte array representation of the zip file
	 * 
	 * @throws
	 */
	private byte[] zipToByteArray(String bpel, String processWsdl,
			String deploymentDescriptor, HashMap<String,String> wsdls) {
		
//		String proxyUrl = config.getString(SystemConfiguration.STATION_PROXY_URL_PROPERTY);
		/* test url */
//		String proxyUrl = "http://172.16.22.41:8081/StationController/service-proxy";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		try {
			/* open a new zip file stream */
			zos.putNextEntry(new ZipEntry("process.bpel"));
			/* fill byte representation from necessary file in zip file */
			zos.write(bpel.getBytes());
			/*
			 * close zip file stream and positions the stream for writing the
			 * next entry.
			 */
			zos.closeEntry();

			zos.putNextEntry(new ZipEntry(
					"InvokeProcess.wsdl"));
			zos.write(processWsdl.getBytes());
			zos.closeEntry();

			zos.putNextEntry(new ZipEntry("deploy.xml"));
			zos.write(deploymentDescriptor.getBytes());
			zos.closeEntry();

			/* create zip file entry for every wsdl */
			
			for (String serviceName : wsdls.keySet()) {
				
				String wsdl = wsdls.get(serviceName);
				
				String wsdlFileName = serviceName + ".wsdl";
				zos.putNextEntry(new ZipEntry(wsdlFileName));
				zos.write(wsdl.getBytes());
				zos.closeEntry();
			}

			zos.close();

			zos.flush();
		} catch (ZipException ze) {
			logger.error("ZIP format error has occurred", ze);
		} catch (IOException ioe) {
			logger.error("An I/O error occurs", ioe);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
		}

		return baos.toByteArray();
	}

	/**
	 * Undeploy a bpel process.	
	 * 
	 * @param packageName
	 * @return
	 * @throws UndeploymentFailException
	 * @throws InvalidConfigurationException 
	 * @throws MalformedURLException 
	 */
	public boolean undeploy(QName packageName) {
		
		this.connect();
		/*
		 * ODE cannot handle the QName with namespace which is returned by the
		 * deploy operation. (April 2009)
		 * 
		 * For this reason, we have to create a new QName that only contains the
		 * localPart.
		 */
		QName odePackageName = new QName(packageName.getLocalPart());

		logger.info("The package " + packageName + " will be undeployed.");
		try {
			boolean response = deploymentService.undeploy(odePackageName);
			logger.info("Successfully undeployed " + packageName);
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
		
	/**
	 * create a connection to the service
	 * @throws InvalidConfigurationException 
	 * @throws MalformedURLException 
	 */	
	protected void connect() {		
		
//		String url = config.getString(
//				SystemConfiguration.ODE_DEPLOYMENTSERVICE_WSDL_URL_PROPERTY);
		
		/* Test url*/
		String url = "http://localhost:8080/ode/processes/DeploymentService?wsdl";
		
		/* Check whether the configuration changed since the last call */
		if (deploymentService == null || !url.equals(this.serviceUrl)) {
						
			try {
				DeploymentService service = new DeploymentService(new URL(url), SERVICE_NAME);
				
				deploymentService = service.getDeploymentServiceSOAP11PortHttp();
				this.serviceUrl = url;
				
			} catch (MalformedURLException e) {
				logger.error("Invalid url", e);
//				throw new InvalidConfigurationException("Invalid url",
//						SystemConfiguration.ODE_DEPLOYMENTSERVICE_WSDL_URL_PROPERTY, e);
			}
		}
	}
	
	/* getter & setter */
	
	public void setConfig(DatabaseConfiguration config) {
		this.config = config;
	}
}
