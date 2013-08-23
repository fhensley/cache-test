package com.anatas.cachetest.view;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.anatas.cachetest.model.Colour;

@RunWith(Arquillian.class)
public class ColourBeanTest {
    @Inject
    private ColourBean colourbean;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar").addClasses(ColourBean.class, Colour.class).addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testIsDeployed() {
        Assert.assertNotNull(colourbean);
    }
}
