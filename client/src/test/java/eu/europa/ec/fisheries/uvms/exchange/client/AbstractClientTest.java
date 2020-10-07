/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.exchange.client;

import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@ArquillianSuiteDeployment
public abstract class AbstractClientTest {

    @Deployment(name = "normal", order = 2)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "exchange.war");
        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                    .resolve("eu.europa.ec.fisheries.uvms.exchange:exchange-module:jar:classes:?")
                    .withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.exchange.client");

        testWar.addAsResource("beans.xml", "META-INF/beans.xml");

        testWar.addClass(UnionVMSMock.class);
        testWar.addClass(ConfigServiceMock.class);

        return testWar;
    }
}