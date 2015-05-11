package org.yarr.rates;

import org.yarr.rates.adapters.ValueAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 04.08.2014 at 13:03
 * Valute of exchange-rates project
 *
 * @author Dmitry V. (savraz [at] gmail.com)
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ValuteNode
{
	private String id;
	private int numCode;
	private String charCode;
	private int nominal;
	private String name;
	private Number value;
	private ValuteNode(){}

	@XmlAttribute(name = "ID")
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	@XmlElement(name = "NumCode")
	public int getNumCode()
	{
		return numCode;
	}
	public void setNumCode(int numCode)
	{
		this.numCode = numCode;
	}
	@XmlElement(name = "CharCode")
	public String getCharCode()
	{
		return charCode;
	}
	public void setCharCode(String charCode)
	{
		this.charCode = charCode;
	}
	@XmlElement(name = "Nominal")
	public int getNominal()
	{
		return nominal;
	}
	public void setNominal(int nominal)
	{
		this.nominal = nominal;
	}
	@XmlElement(name = "Name")
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	@XmlElement(name = "Value")
	@XmlJavaTypeAdapter(ValueAdapter.class)
	public Number getValue()
	{
		return value;
	}
	public void setValue(Number value)
	{
		this.value = value;
	}
	@Override
	public String toString()
	{
		return "ValuteNode{" +
			"charCode='" + charCode + '\'' +
			", nominal=" + nominal +
			", name='" + name + '\'' +
			", value=" + value +
			'}';
	}
}
