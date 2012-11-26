package org.jbpm.designer.test.web.util;

import java.io.*;
import java.util.*;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.*;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.PomEquippedResolveStageBaseImpl;
import org.jbpm.designer.Base64EncodingUtil;

public class ArquillianDeploymentUtil {

    private static final String guvnorVersion = "5.5.0-SNAPSHOT";
    private static final String jbossAS7LaunchPropName = "jbossas-7";

    private static final String guvnorWarName =  "drools-guvnor.war";
    private static final String designerWarName = "designer.war";
    
    private static Properties launchProps;
    static {
        try {
            launchProps = new Properties();
            InputStream arquillianLaunchFile = ArquillianDeploymentUtil.class.getResourceAsStream("/arquillian.launch");
            launchProps.load(arquillianLaunchFile);
        } catch (IOException e) {
            // do nada
        }
    }

    public static WebArchive createGuvnorWar() {
        return createGuvnorWar(guvnorVersion);
    }
    
    public static WebArchive createGuvnorWar(String guvnorVersion) {
        WebArchive war = null;
        if (launchProps.containsKey(jbossAS7LaunchPropName)) {
            // dependencies
            File[] guvnorAs7WarFiles = Maven.resolver()
                    .resolve("org.drools:guvnor-distribution-wars:war:jboss-as-7.0:" + guvnorVersion).offline()
                    .withoutTransitivity().as(File.class);
            // Import/deploy the guvnor jboss AS 7 war
            war = ShrinkWrap.create(ZipImporter.class, guvnorWarName).importFrom(guvnorAs7WarFiles[0]).as(WebArchive.class);
        } else if (launchProps.containsKey("glassfish-embedded")) {
            throw new RuntimeException("Glassfish emedded test not yet implemented!");
        } else {
            launchProps.list(System.out);
            throw new RuntimeException("No property could be found specifying which arquillian container to start.");
        }

        return war;
    }
    
    public static WebArchive createDesignerWar() {
        File[] dependencyFiles = resolveRuntimeDependenciesOffLine("designer/guvnor-servlets-pom.xml");

        WebArchive war = null;
        if (launchProps.containsKey(jbossAS7LaunchPropName)) {
            war = ShrinkWrap
                    .create(WebArchive.class, designerWarName)
                    // classes -- do not add "org.jbpm.designer.assets" yet
                    .addPackages(true, "org.jbpm.designer.bpmn2")
                    .addPackages(true, "org.jbpm.designer.server")
                    .addPackages(true, "org.jbpm.designer.stencilset")
                    .addPackages(true, "org.jbpm.designer.taskforms")
                    .addPackages(true, "org.jbpm.designer.web")
                    .addClass(Base64EncodingUtil.class)
                    // web config
                    .addAsWebInfResource("WEB-INF/web.xml", "designer/guvnor-servlets-web.xml")
                    .addAsWebInfResource("WEB-INF/jboss-web.xml", "jboss-web.xml")
                    // persistence
                    // .addAsResource("persistence.xml",
                    // "META-INF/persistence.xml")
                    // persistence: ds [jboss specific (because it's not
                    // embedded.. :( )]
                    // .addAsWebInfResource("jbossas-ds.xml")
                    .addAsLibraries(dependencyFiles);
        } else if (launchProps.containsKey("glassfish-embedded")) {
            throw new RuntimeException("Glassfish embedded test not yet implemented!");
        } else {
            launchProps.list(System.out);
            throw new RuntimeException("No property could be found specifying which arquillian container to start.");
        }

        return war;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static File[] resolveRuntimeDependenciesOffLine(String pomXmlResourcePath) {
    
        PomEquippedResolveStageBaseImpl pers = (PomEquippedResolveStageBaseImpl) Maven.resolver().loadPomFromClassLoaderResource(
                pomXmlResourcePath);
    
        ScopeType[] runtmeScopes = new ScopeType[] { ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.RUNTIME, ScopeType.SYSTEM };
        addRuntimeDependencies(pers, runtmeScopes);
    
        // 1. pers.resolve calls .createStrategyStage();
        // 2. .offline() fails unless we call addRuntimeDependencies(...) above
        MavenStrategyStageBase mssb = pers.resolve().offline();
    
        ResolutionStrategy runtimeStrategy = new AcceptScopesStrategy(runtmeScopes);
        File[] dependencyFiles = mssb.using(runtimeStrategy).as(File.class);
    
        return dependencyFiles;
    }

    @SuppressWarnings("rawtypes")
    private static void addRuntimeDependencies(PomEquippedResolveStageBaseImpl pers, ScopeType[] runtimeScopes) {
        // Get all declared dependencies
        final MavenWorkingSession session = pers.getMavenWorkingSession();
        final List<MavenDependency> dependencies = new ArrayList<MavenDependency>(session.getDeclaredDependencies());
    
        // Filter by scope
        final MavenResolutionFilter preResolutionFilter = new ScopeFilter(runtimeScopes);
    
        // For all declared dependencies which pass the filter, add 'em to the
        // set of dependencies to be resolved for this request
        ArrayList<MavenDependency> EMPTY_LIST = new ArrayList<MavenDependency>(0);
        for (final MavenDependency candidate : dependencies) {
            if (preResolutionFilter.accepts(candidate, EMPTY_LIST)) {
                session.getDependenciesForResolution().add(candidate);
            }
        }
    }
}
