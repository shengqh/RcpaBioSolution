/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: SourceFile.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:04 $
**/
public class SourceFile implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _fileName;

    private java.lang.String _fileType;


      //----------------/
     //- Constructors -/
    //----------------/

    public SourceFile() {
        super();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.SourceFile()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'fileName'.
     * 
     * @return the value of field 'fileName'.
    **/
    public java.lang.String getFileName()
    {
        return this._fileName;
    } //-- java.lang.String getFileName() 

    /**
     * Returns the value of field 'fileType'.
     * 
     * @return the value of field 'fileType'.
    **/
    public java.lang.String getFileType()
    {
        return this._fileType;
    } //-- java.lang.String getFileType() 

    /**
    **/
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * 
     * 
     * @param out
    **/
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * 
     * 
     * @param handler
    **/
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Sets the value of field 'fileName'.
     * 
     * @param fileName the value of field 'fileName'.
    **/
    public void setFileName(java.lang.String fileName)
    {
        this._fileName = fileName;
    } //-- void setFileName(java.lang.String) 

    /**
     * Sets the value of field 'fileType'.
     * 
     * @param fileType the value of field 'fileType'.
    **/
    public void setFileType(java.lang.String fileType)
    {
        this._fileType = fileType;
    } //-- void setFileType(java.lang.String) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.SourceFile unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.SourceFile) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.SourceFile.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.SourceFile unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
