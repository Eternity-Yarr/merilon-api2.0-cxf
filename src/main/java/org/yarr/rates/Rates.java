package org.yarr.rates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

/**
 * 04.08.2014 at 14:30
 * Rates of exchange-rates project
 *
 * @author Dmitry V. (savraz [at] gmail.com)
 */
public class Rates
{
	final private static Logger log = LoggerFactory.getLogger(Rates.class);
	final private static String URL = "http://www.cbr.ru/scripts/XML_daily.asp";
	private CBRResponse cbr;

	public Rates()
	{
		try
		{
			String xml = fetchPage();
			cbr = parseResponse(xml);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private String fetchPage() throws Exception
	{
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

		String ret;
		URLConnection con = new URL(URL).openConnection();
		if (((HttpURLConnection)con).getResponseCode() == 200)
			try
			(
				InputStream is = con.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "cp1251");
				BufferedReader br = new BufferedReader(isr)
			)
			{
				String in;
				StringBuilder sb = new StringBuilder(50000);
				while((in = br.readLine()) != null)
					sb.append(in);
				ret = sb.toString();
			}
		else
			throw new IOException("Connection failed");

		return ret;
	}

	private CBRResponse parseResponse(String response) throws Exception
	{
		Mapper<CBRResponse> m = new Mapper<>(CBRResponse.class);
		CBRResponse ret = null;
		if(response != null)
			ret = m.asPOJO(new StringReader(response));

		return ret;
	}

	public double getRateOf(String code)
	{
		double ret = 0.0;
		ValuteNode vn = cbr.getValute(code);
		if(vn != null)
			ret = vn.getValue().doubleValue();

		return ret;
	}

	public ValuteNode getValute(String code)
	{
		return cbr.getValute(code);
	}
}
