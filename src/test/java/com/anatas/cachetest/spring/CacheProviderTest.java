package com.anatas.cachetest.spring;

import com.anatas.cachetest.spring.CacheProvider;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

@RunWith(Arquillian.class)
public class CacheProviderTest
{
   @Inject
   private CacheProvider cacheprovider;

   @Deployment
   public static JavaArchive createDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(CacheProvider.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Test
   public void testIsDeployed()
   {
      Assert.assertNotNull(cacheprovider);
   }
}
