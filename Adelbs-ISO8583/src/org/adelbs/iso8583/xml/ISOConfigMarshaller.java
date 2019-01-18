package org.adelbs.iso8583.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.adelbs.iso8583.vo.ISOConfigVO;

/**
 * Specialized class to marshal {@link ISOConfigVO} objects into XML data.
 */
public class ISOConfigMarshaller {
	//singleton implementation
	private static ISOConfigMarshaller instance;
	private final Marshaller jaxbMarshaller;
	private final Unmarshaller jaxbUnmarshaller;
	
	
	private ISOConfigMarshaller() throws ISOConfigMarshallerException{
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(ISOConfigVO.class);
			this.jaxbMarshaller = jaxbContext.createMarshaller();
			this.jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
						
			this.jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			throw new ISOConfigMarshallerException(e);
		}
		
	}
	
	/**
	 * Get a new  instance of the {@link ISOConfigMarshaller}
	 * @return
	 * @throws ISOConfigMarshallerException 
	 */
	public static synchronized ISOConfigMarshaller creatMarshaller() throws ISOConfigMarshallerException{
		if(instance == null){
			instance = new ISOConfigMarshaller();
		}
		return instance;
	}

	/**
	 * Convert a {@link ISOConfigVO} into a structured XML string
	 * @param configTreeNode TreeNode to be converted into XML
	 * @return XML as String, representing the tree node
	 * @throws ISOConfigMarshallerException An exception will be thrown if any error occurs during the marshaling of the tree node
	 */
	public String marshal(final ISOConfigVO isoConfigVO) throws ISOConfigMarshallerException{
		final StringWriter xmlWriter = new StringWriter();
		try {
			this.jaxbMarshaller.marshal(isoConfigVO, xmlWriter);
		} catch (JAXBException e) {
			throw new ISOConfigMarshallerException(e);
		}
		return xmlWriter.toString();
	}
	
	/**
	 * Convert a representation of a ISO Config as XML to a {@link ISOConfigVO}.
	 * @param isoConfigXML XML Structured as a ISO Config.
	 * @return an instance of {@link ISOConfigVO} populated with data from the XML
	 * @throws ISOConfigMarshallerException
	 */
	public ISOConfigVO unmarshal(final String isoConfigXML) throws ISOConfigMarshallerException{
		final InputStream stream = new ByteArrayInputStream(isoConfigXML.getBytes(StandardCharsets.UTF_8));
		try {
			return (ISOConfigVO) this.jaxbUnmarshaller.unmarshal(stream);
		} catch (JAXBException e) {
			throw new ISOConfigMarshallerException(e);
		}
	}
}