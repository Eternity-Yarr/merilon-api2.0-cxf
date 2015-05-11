package org.yarr.rates;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.Reader;

/**
 * 04.08.2014 at 13:03
 * Mapper of exchange-rates project
 *
 * @author Dmitry V. (savraz [at] gmail.com)
 */
public class Mapper<T>
{
	private JAXBContext context;

	public Mapper(Class<T> clazz) throws JAXBException
	{
		context = JAXBContext.newInstance(clazz);
	}

	public T asPOJO(Reader xml) throws JAXBException
	{
		Unmarshaller um = context.createUnmarshaller();
		Object o;
		try
		{
			o = um.unmarshal(xml);
		}
		catch(IllegalArgumentException e)
		{
			throw new JAXBException(e);
		}

		//noinspection unchecked
		@SuppressWarnings("Unchecked")
		T t = (T)o;

		return t;
	}

	public T asPOJO(InputStream xml) throws JAXBException
	{
		Unmarshaller um = context.createUnmarshaller();
		Object o;
		try
		{
			o = um.unmarshal(xml);
		}
		catch(IllegalArgumentException e)
		{
			throw new JAXBException(e);
		}

		//noinspection unchecked
		@SuppressWarnings("Unchecked")
		T t = (T)o;

		return t;
	}
}
