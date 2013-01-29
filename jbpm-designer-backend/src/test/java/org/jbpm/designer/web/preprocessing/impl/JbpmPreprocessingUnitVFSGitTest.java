package org.jbpm.designer.web.preprocessing.impl;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestIDiagramProfile;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.*;
import org.kie.commons.java.nio.file.FileSystem;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JbpmPreprocessingUnitVFSGitTest {

    // TODO change it to generic independent path
    private static final String REPOSITORY_ROOT = "designer-playground";
    private static final String VFS_REPOSITORY_ROOT = "git://" + REPOSITORY_ROOT;
    private static final String USERNAME = "guvnorngtestuser1";
    private static final String PASSWORD = "test1234";
    private static final String ORIGIN_URL      = "https://github.com/mswiderski/designer-playground.git";
    private static final String FETCH_COMMAND = "?fetch";
    private JbpmProfileImpl profile;

    private static String gitLocalClone = System.getProperty("java.io.tmpdir") + "git-repo";
    private static Map<String, String> env = new HashMap<String, String>();

    private static int counter = -100;
    private FileSystem fileSystem;
    private VFSFileSystemProducer producer = new VFSFileSystemProducer();

    @BeforeClass
    public static void prepare() {

        env.put( "username", USERNAME );
        env.put( "password", PASSWORD );
        env.put( "origin", ORIGIN_URL );
        env.put( "fetch.cmd", FETCH_COMMAND );
        System.setProperty("org.kie.nio.git.dir", gitLocalClone);
    }

    @AfterClass
    public static void cleanup() {
        System.clearProperty("org.kie.nio.git.dir");
    }

    @Before
    public void setup() {
        profile = new JbpmProfileImpl();
        profile.setRepositoryId("vfs");
        profile.setRepositoryRoot(VFS_REPOSITORY_ROOT + counter);
        profile.setRepositoryGlobalDir("/global");

        fileSystem = producer.produceFileSystem(profile, env);
    }

    private void deleteFiles(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteFiles(file);
            }
            file.delete();
        }
    }

    @After
    public void teardown() {
        File repo = new File(gitLocalClone);
        if(repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
        repo = new File(".niogit");
        if(repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
        counter++;
    }
    @Test
    public void testProprocess() {
        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        //prepare folders that will be used
        repository.createDirectory("/myprocesses");
        repository.createDirectory("/global");

        // prepare process asset that will be used to preprocess
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("process")
                .location("/myprocesses");
        String uniqueId = repository.createAsset(builder.getAsset());

        // create instance of preprocessing unit
        JbpmPreprocessingUnit preprocessingUnitVFS = new JbpmPreprocessingUnit(new TestServletContext());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);

        // run preprocess
        preprocessingUnitVFS.preprocess(new TestHttpServletRequest(params), null, new TestIDiagramProfile(repository), null);

        // validate results
        Collection<Asset> globalAssets = repository.listAssets("/global");
        assertNotNull(globalAssets);
        assertEquals(30, globalAssets.size());
        repository.assetExists("/global/backboneformsinclude.fw");
        repository.assetExists("/global/backbonejsinclude.fw");
        repository.assetExists("/global/cancelbutton.fw");
        repository.assetExists("/global/checkbox.fw");
        repository.assetExists("/global/customeditors.json");
        repository.assetExists("/global/div.fw");
        repository.assetExists("/global/dropdownmenu.fw");
        repository.assetExists("/global/fieldset.fw");
        repository.assetExists("/global/form.fw");
        repository.assetExists("/global/handlebarsinclude.fw");
        repository.assetExists("/global/htmlbasepage.fw");
        repository.assetExists("/global/image.fw");
        repository.assetExists("/global/jqueryinclude.fw");
        repository.assetExists("/global/jquerymobileinclude.fw");
        repository.assetExists("/global/link.fw");
        repository.assetExists("/global/mobilebasepage.fw");
        repository.assetExists("/global/orderedlist.fw");
        repository.assetExists("/global/passwordfield.fw");
        repository.assetExists("/global/radiobutton.fw");
        repository.assetExists("/global/script.fw");
        repository.assetExists("/global/submitbutton.fw");
        repository.assetExists("/global/table.fw");
        repository.assetExists("/global/textarea.fw");
        repository.assetExists("/global/textfield.fw");
        repository.assetExists("/global/themes.json");
        repository.assetExists("/global/unorderedlist.fw");
        repository.assetExists("/global/defaultemailicon.gif");
        repository.assetExists("/global/defaultlogicon.gif");
        repository.assetExists("/global/defaultservicenodeicon.png");
        repository.assetExists("/global/.gitignore");

        Collection<Asset> defaultStuff = repository.listAssets("/myprocesses");
        assertNotNull(defaultStuff);
        assertEquals(3, defaultStuff.size());
        repository.assetExists("/myprocesses/WorkDefinitions.wid");
        // this is the process asset that was created for the test but let's check it anyway
        repository.assetExists("/myprocesses/process.bpmn2");
        repository.assetExists("/myprocesses/.gitignore");

    }
}
