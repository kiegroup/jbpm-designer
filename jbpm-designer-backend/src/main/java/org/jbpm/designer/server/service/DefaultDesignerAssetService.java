package org.jbpm.designer.server.service;

import java.io.StringWriter;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import com.google.gwt.user.client.Window;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.AssetNotFoundException;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.kie.commons.validation.Preconditions;
import org.jbpm.designer.server.service.MockServletContext;
import org.jbpm.designer.service.DesignerAssetService;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.jbpm.designer.web.server.ServletUtil;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

/**
 * [manstis] Default implementation
 */
@Service
@ApplicationScoped
public class DefaultDesignerAssetService implements DesignerAssetService {

//    @Inject
//    private VFSService vfs;

    @Inject
    private Repository repository;

    @Override
    public String loadJsonModel( final Path path ) {

        Preconditions.checkNotNull( "path",
                path );
        try {
            //Get the XML
            final String bpmn2 = (String) repository.loadAsset(path.toURI().toString()).getAssetContent();
    //        final String bpmn2 = vfs.readAllString( path );

            //Mock a ServletContext (as the profile definitions are now stored on the classpath)
            final ServletContext context = new MockServletContext();

            //Get the default profile handler (hard coded for now, could be a parameter)
            final IDiagramProfile profile = new JbpmProfileImpl( context );

            //Convert to JSON
            DroolsPackageImpl.init();
            final String json = profile.createUnmarshaller().parseModel( bpmn2,
                    profile,
                    "" );
            return json;
        } catch (AssetNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void saveJsonModel( final Path path,
                               final String jsonModel ) {
        Preconditions.checkNotNull( "path",
                path );
        Preconditions.checkNotNull( "jsonModel",
                jsonModel );

        //Mock a ServletContext (as the profile definitions are now stored on the classpath)
        final ServletContext context = new MockServletContext();

        //Get the default profile handler (hard coded for now, could be a parameter)
        final IDiagramProfile profile = new JbpmProfileImpl( context );

        //Convert to XML
        final String bpmn2 = profile.createMarshaller().parseModel( jsonModel,
                "" );

        //Write XML back
//        vfs.write( path,
//                bpmn2 );

        try {
            Asset asset = repository.loadAsset(path.toURI().toString());
            AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(asset);
            builder.content(bpmn2);

            repository.updateAsset(builder.getAsset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
