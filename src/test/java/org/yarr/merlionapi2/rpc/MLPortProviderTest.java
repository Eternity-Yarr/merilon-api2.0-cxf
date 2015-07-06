package org.yarr.merlionapi2.rpc;

import https.api_merlion_com.dl.mlservice2.CatalogResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yarr.merlionapi2.ApplicationConfiguration;
import org.yarr.merlionapi2.MLPortProvider;

import java.util.List;

@ContextConfiguration(classes = {ApplicationConfiguration.class})
public class MLPortProviderTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MLPortProvider mlPortProvider;

    @Test
    public void test() {
        List<CatalogResult> resultList = mlPortProvider.get().getCatalog("B10202").getItem();
        Assert.assertTrue(resultList.size() > 0);
    }
}
