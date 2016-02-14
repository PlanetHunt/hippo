/**
 * 
 */
package de.netsat.orekit.matlab;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

/**
 * @author philipbangert
 * 
 */
public class MatlabInterface {
	//public static final String MATLAB_PATH = "F:\\Additional Programs\\MATLAB\\R2013a\\bin\\matlab.exe";
	public static final String MATLAB_PATH = "/usr/local/bin/matlab";
	//public static final String MATLAB_PATH = "D:\\Programs\\MATLAB\\R2015a\\bin\\matlab.exe";
	protected String matlabPath;
	protected MatlabProxy proxy;
	
	public MatlabInterface() throws MatlabConnectionException, MatlabInvocationException {
		this(MATLAB_PATH, null);
	}
	
	public MatlabInterface(String matlabPath, String initScript) throws MatlabConnectionException, MatlabInvocationException {
		this.matlabPath = matlabPath;
		init(initScript);
	}
	
	/**
	 * Is called automatically in the constructor - no need to call it again
	 */
	public MatlabInterface init(String initScript) throws MatlabConnectionException, MatlabInvocationException {
		// Create a proxy, which we will use to control MATLAB
		MatlabProxyFactoryOptions builder = new matlabcontrol.MatlabProxyFactoryOptions.Builder().setMatlabLocation(matlabPath)
				.setUsePreviouslyControlledSession(true)
				.setHidden(false).build();
		MatlabProxyFactory factory = new MatlabProxyFactory(builder);
		proxy = factory.getProxy();
		if(initScript != null)
			feval(initScript);
		return this;
	}
	
	/**
	 * Run script remotely
	 * @throws MatlabInvocationException
	 */
	public void eval(String script) throws MatlabInvocationException {
		proxy.eval(script);
	}
	
	/**
	 * Run script remotely
	 * @throws MatlabInvocationException
	 */
	public void feval(String script, Object... params) throws MatlabInvocationException {
		proxy.feval(script, params);
	}
	
	/** Call remote script which returns the given number of arguments
	 */
	public Object[] returningEval(String script, int retArgs) throws MatlabInvocationException {
		return proxy.returningEval(script, retArgs);
	}
	
	/** Call remote script which returns the given number of arguments
	 */
	public Object[] returningFeval(String script, int retArgs, Object... params) throws MatlabInvocationException {
		return proxy.returningFeval(script, retArgs, params);
	}
	
	/** Shold be called when the matlab interface is no more needed
	 */
	public void dispose() {
		// Disconnect the proxy from MATLAB
		if(proxy != null)
			proxy.disconnect();
	}
	
	/**
	 * Returns the proxy
	 * @return
	 */
	public MatlabProxy getProxy() {
		return proxy;
	}
}
