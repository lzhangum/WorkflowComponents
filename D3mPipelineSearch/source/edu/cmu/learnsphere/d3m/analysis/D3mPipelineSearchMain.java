package edu.cmu.learnsphere.d3m.analysis;

import java.io.File;

import edu.cmu.pslc.datashop.workflows.AbstractComponent;

/**
 * Workflow component: template source for a component
 */
public class D3mPipelineSearchMain extends AbstractComponent {


    /**
     * Main method.
     * @param args the arguments
     */
    public static void main(String[] args) {

        D3mPipelineSearchMain tool = new D3mPipelineSearchMain();
        tool.startComponent(args);
    }

    /**
     * Constructor.
     */
    public D3mPipelineSearchMain() {
        super();
    }

    @Override
    protected void processOptions() {
        logger.info("Processing Options");

        // The addMetaData* methods make the meta data available to downstream components.

	// Add input meta-data (headers) to output file.
	this.addMetaDataFromInput("d3m-dataset", 0, 0, ".*");
	// Add additional meta-data to output file.
	this.addMetaData("d3m-dataset", 0, META_DATA_LABEL, "label0", 0, "New Column Name");

    }

    @Override
    protected void parseOptions() {


    }

    /**
     * Processes the input file(s) and option(s) to generate inputs to next component(s).
     */
    @Override
    protected void runComponent() {

	// Run the program...
	File outputDirectory = this.runExternal();


	File outputFile0 = new File(outputDirectory.getAbsolutePath() + "/output.txt");

		this.addOutputFile(outputFile0, 0, 0, "text");


        System.out.println(this.getOutput());

    }
}