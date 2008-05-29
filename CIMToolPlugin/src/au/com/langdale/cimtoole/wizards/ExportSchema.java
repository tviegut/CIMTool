/*
 * This software is Copyright 2005,2006,2007,2008 Langdale Consultants.
 * Langdale Consultants can be contacted at: http://www.langdale.com.au
 */
package au.com.langdale.cimtoole.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import au.com.langdale.cimtoole.project.Info;
import au.com.langdale.cimtoole.project.Task;
import au.com.langdale.ui.builder.FurnishedWizard;

public class ExportSchema extends FurnishedWizard implements IExportWizard {
	
	private SchemaExportPage main = new SchemaExportPage();
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Export Schema"); 
		setNeedsProgressMonitor(true);
		main.setTitle(getWindowTitle());
		main.setDescription("Export the merged schema as OWL.");
		main.setSelected(selection);
	}
	
	@Override
    public void addPages() {
        addPage(main);        
    }
	
	class InternalSchemaTask implements IWorkspaceRunnable {
		IProject project = main.getProject();

		public void run(IProgressMonitor monitor) throws CoreException {
			project.setPersistentProperty(Info.MERGED_SCHEMA_PATH, SchemaExportPage.SCHEMA);
			project.setPersistentProperty(Info.PROFILE_NAMESPACE, main.getNamespace());
			Info.getSchemaFolder(project).touch(null);
		}
	}
	
	@Override
	public boolean performFinish() {
		IWorkspaceRunnable task;
		if( main.isInternal()) 
			task = new InternalSchemaTask();
		else
			task = Task.exportSchema(main.getProject(), main.getPathname(), main.getNamespace());
		return run(task, null);
	}
}