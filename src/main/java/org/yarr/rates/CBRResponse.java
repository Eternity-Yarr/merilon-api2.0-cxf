package org.yarr.rates;

import org.yarr.rates.adapters.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 04.08.2014 at 13:01
 * CBRResponse of exchange-rates project
 *
 * @author Dmitry V. (savraz [at] gmail.com)
 */
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.NONE)
public class CBRResponse
{
	private Date date;
	private String name;
	private Map<String, ValuteNode> valuteMap = null;
	private List<ValuteNode> valute;
	private CBRResponse(){}

	@XmlAttribute(name ="Date")
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDate()
	{
		return new Date(date.getTime());
	}
	public void setDate(Date date)
	{
		if(date != null)
			this.date = new Date(date.getTime());
	}
	@XmlAttribute(name ="name")
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	@XmlElement(name ="Valute")
	public List<ValuteNode> getValute()
	{
		return valute;
	}
	public void setValute(List<ValuteNode> valutes)
	{
		this.valute = valutes;
	}
	private Map<String, ValuteNode> getValuteMap()
	{
		if(valuteMap == null)
		{
			valuteMap = new HashMap<>();
			for(ValuteNode v : getValute())
				valuteMap.put(v.getCharCode(), v);
		}

		return valuteMap;
	}
	public ValuteNode getValute(String code)
	{
		return getValuteMap().get(code);
	}
}
