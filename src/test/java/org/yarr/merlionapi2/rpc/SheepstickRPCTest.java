package org.yarr.merlionapi2.rpc;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yarr.merlionapi2.directory.ItemsRepository;
import org.yarr.merlionapi2.model.*;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.BitrixService;
import org.yarr.merlionapi2.service.ConfigService;
import org.yarr.merlionapi2.service.RateService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class SheepstickRPCTest
{
    static SheepstickRPC sheepstick;
    static BindService bs;
    static BitrixService bs2;
    static ItemsRepository ir;
    static RateService rs;
    static ConfigService cs;

    @BeforeClass
    public static void setUp() {
        bs = Mockito.mock(BindService.class);
        bs2 = Mockito.mock(BitrixService.class);
        ir = Mockito.mock(ItemsRepository.class);
        rs = Mockito.mock(RateService.class);
        cs = Mockito.mock(ConfigService.class);
        sheepstick = new SheepstickRPC(bs, bs2, ir, cs);
        when(cs.merlionSupplierId()).thenReturn(12345);
        when(cs.valudeAddedPercent()).thenReturn(5);
        when(rs.usd2rub(anyDouble())).thenAnswer(
                inv -> (Double) inv.getArguments()[0] * 50);

    }

    @Test(enabled = false)
    public void convert()  throws Exception{

        File f = new File("./data/tracked_catalogs.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn  = mapper.readTree(f);
        List<JsonNode> ids = jn.findValues("id");
        Set<String> set = ids.stream()
                .map(JsonNode::getTextValue)
                .collect(Collectors.toSet());
        String json = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(new TrackedNodes(set));
        FileUtils.writeStringToFile(new File("./data/tracked_catalog_ids.json"), json);
    }

    @Test
    public void testCompareAndSetPriceNotInStock() throws Exception
    {
        Bond b = new Bond("MRLID", "MRLCATID", "12345");
        Item i = new Item("MRLID", "MRLCATID", "SOMECODE", "Some name", "Very branded");
        StockItem s = new StockItem(70, 5, "12345");
        StockAndItem si = new StockAndItem("MRLID", i, s);

        when(bs2.alreadyInStock(anyString(), eq(12345))).thenReturn(Optional.of(Boolean.FALSE));
        ArgumentCaptor<Long> priceCaptor = ArgumentCaptor.forClass(Long.class);

        sheepstick.compareAndSetPrice(new HashMap<>(), b, si).accept(60L);

        verify(bs2).setPriceById(anyString(), priceCaptor.capture());
        assertEquals(priceCaptor.getValue(), new Long(74), "Price correctly set");
    }

    @Test
    public void testCompareAndSetPriceInStock() throws Exception
    {
        Bond b = new Bond("MRLID", "MRLCATID", "12345");
        Item i = new Item("MRLID", "MRLCATID", "SOMECODE", "Some name", "Very branded");
        StockItem s = new StockItem(50, 5, "12345");
        StockAndItem si = new StockAndItem("MRLID", i, s);

        when(bs2.alreadyInStock(anyString(), eq(12345))).thenReturn(Optional.of(Boolean.TRUE));

        sheepstick.compareAndSetPrice(new HashMap<>(), b, si).accept(60L);

        verify(bs2, times(0)).setPriceById(anyString(), anyLong());
    }

}