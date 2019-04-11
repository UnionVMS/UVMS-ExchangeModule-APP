package eu.europa.ec.fisheries.uvms.exchange.rest;


import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;


@ArquillianSuiteDeployment
public abstract class BuildExchangeRestTestDeployment {

    final static Logger LOG = LoggerFactory.getLogger(BuildExchangeRestTestDeployment.class);

    @Deployment(name = "exchangeservice", order = 2)
    public static Archive<?> createDeployment() {
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "exchangerest.war");

        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
        testWar.addAsLibraries(files);

        files = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.exchange:exchange-service").withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.exchange.rest");

        testWar.addAsWebInfResource("META-INF/ejb-jar.xml");
        testWar.addAsResource("META-INF/persistence.xml", "persistence.xml");
        testWar.addAsResource("META-INF/beans.xml", "beans.xml");

        testWar.deleteClass(AssetModuleMock.class);
        testWar.deleteClass(UnionVMSMock.class);

        testWar.delete("/WEB-INF/web.xml");
        testWar.addAsWebInfResource("mock-web.xml", "web.xml");

        return testWar;
    }

    @Deployment(name = "uvms", order = 1)
    public static Archive<?> createMocks() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "unionvms.war");

        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.asset:asset-client",
                        "eu.europa.ec.fisheries.uvms.commons:uvms-commons-message")
                .withTransitivity().asFile();
        testWar.addAsLibraries(files);


        testWar.addClass(AssetModuleMock.class);
        testWar.addClass(UnionVMSMock.class);

        return testWar;
    }

    protected WebTarget getWebTarget() {

        //ObjectMapper objectMapper = new ObjectMapper();
        Client client = ClientBuilder.newClient();
        //client.register(new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
        //return client.target("http://localhost:28080/test/rest");
        return client.target("http://localhost:8080/exchangerest/rest");
    }
}
