package org.yarr.merlionapi2.rpc;

import https.api_merlion_com.dl.mlservice3.ArrayOfString;
import https.api_merlion_com.dl.mlservice3.CatalogResult;
import https.api_merlion_com.dl.mlservice3.ItemsAvailResult;
import https.api_merlion_com.dl.mlservice3.ItemsResult;
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

    @Test(enabled = false) // integration suite should be made separately
    public void test() {
        List<CatalogResult> resultList = mlPortProvider.get().getCatalog("B10202").getItem();
        Assert.assertTrue(resultList.size() > 0);
    }

    @Test(enabled = false)
    public void testRetrieveItems() {
        ArrayOfString ids = new ArrayOfString();
        ids.getItem().add("931772");
        ids.getItem().add("931786");
        List<ItemsAvailResult>  itemsAvail = mlPortProvider.get().getItemsAvail("", "ДОСТАВКА", "06-05-15", "", ids).getItem();
        List<ItemsResult> items = mlPortProvider.get().getItems("", ids, "ДОСТАВКА", 0, 10000, "").getItem();
        Assert.assertTrue(itemsAvail.size() == 2);
        Assert.assertTrue(items.size() == 2);
    }
}
