package org.yarr.rates.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 04.08.2014 at 13:32
 * DateAdapter of exchange-rates project
 *
 * @author Dmitry V. (savraz [at] gmail.com)
 */
public class DateAdapter extends XmlAdapter<String, Date>
{
	private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
	@Override
	public Date unmarshal(String v) throws Exception
	{
		return df.parse(v);
	}
	@Override
	public String marshal(Date v) throws Exception
	{
		return df.format(v);
	}
}
