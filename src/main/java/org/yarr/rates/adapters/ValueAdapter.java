package org.yarr.rates.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * 04.08.2014 at 13:26
 * ValueAdapter of exchange-rates project
 *
 * @author Dmitry V. (savraz [at] gmail.com)
 */
public class ValueAdapter extends XmlAdapter<String, Number>
{
	static DecimalFormat df = new DecimalFormat();
	static
	{
		DecimalFormatSymbols dcf = new DecimalFormatSymbols();
		dcf.setDecimalSeparator(',');
		df.setDecimalFormatSymbols(dcf);
	}

	@Override
	public Number unmarshal(String v) throws Exception
	{
		return df.parse(v);
	}
	@Override
	public String marshal(Number v) throws Exception
	{
		return df.format(v);
	}
}
