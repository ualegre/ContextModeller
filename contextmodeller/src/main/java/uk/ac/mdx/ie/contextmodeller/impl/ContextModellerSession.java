package uk.ac.mdx.ie.contextmodeller.impl;

import java.io.File;
import java.util.Map;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.event.IModelChangeHandler;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.api.module.lifecycle.DefaultModuleLifeCycleHandler;
import org.modelio.api.module.lifecycle.ModuleException;
import org.modelio.vbasic.version.Version;


/**
 * Implementation of the IModuleLifeCycleHandler interface.
 * <br>This default implementation may be inherited by the module developers in order to simplify the code writing of the module session.
 */
public class ContextModellerSession extends DefaultModuleLifeCycleHandler {

	/**
	 * Constructor.
	 * @param module the Module this session is instanciated for.
	 */
	private ContextModellerModelChangeHandler modelChangeHandler;
	
	public ContextModellerSession(ContextModellerModule module) {
		super(module);
		this.module = module;
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#start()
	 */
	@Override
	public boolean start() throws ModuleException {
		// get the version of the module
		Version moduleVersion = this.module.getVersion();

		// get the Modelio log service
		ILogService logService = this.module.getModuleContext().getLogService();

		String message = "Start of " + this.module.getName() + " " + moduleVersion;
		logService.info(message);
		
		IModelingSession session = this.module.getModuleContext().getModelingSession();
		modelChangeHandler = new ContextModellerModelChangeHandler();
		session.addModelHandler((IModelChangeHandler) modelChangeHandler);

		registerStyle();
		
		return super.start();
	}
	
    public void registerStyle() {

	    String path = this.getStyle("cm.style");
	    File style = new File(path);
	    this.module.getModuleContext().getModelioServices().getDiagramService().registerStyle("cm.style", "default",
	    		style);
    }
    
    public String getStyle(String styleName) {
	return this.module.getModuleContext().getConfiguration().getModuleResourcesPath() + File.separator + "res"
		+ File.separator + "style" + File.separator + styleName;
    }

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#stop()
	 */
	@Override
	public void stop() throws ModuleException {
		super.stop();
	}

	public static boolean install(String modelioPath, String mdaPath) throws ModuleException {
		return DefaultModuleLifeCycleHandler.install(modelioPath, mdaPath);
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#select()
	 */
	@Override
	public boolean select() throws ModuleException {
		return super.select();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#unselect()
	 */
	@Override
	public void unselect() throws ModuleException {
		super.unselect();
	}

	/**
	 * @see org.modelio.api.module.DefaultModuleLifeCycleHandler#upgrade(org.modelio.api.modelio.Version, java.util.Map)
	 */
	@Override
	public void upgrade(Version oldVersion, Map<String, String> oldParameters) throws ModuleException {
		super.upgrade(oldVersion, oldParameters);
	}
}
