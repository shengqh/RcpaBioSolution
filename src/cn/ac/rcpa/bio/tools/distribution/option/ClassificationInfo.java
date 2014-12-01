/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: ClassificationInfo.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
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
public class ClassificationInfo implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType _classificationType;

    private java.lang.String _classificationPrinciple;


      //----------------/
     //- Constructors -/
    //----------------/

    public ClassificationInfo() {
        super();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'classificationPrinciple'.
     * 
     * @return the value of field 'classificationPrinciple'.
    **/
    public java.lang.String getClassificationPrinciple()
    {
        return this._classificationPrinciple;
    } //-- java.lang.String getClassificationPrinciple() 

    /**
     * Returns the value of field 'classificationType'.
     * 
     * @return the value of field 'classificationType'.
    **/
    public cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType getClassificationType()
    {
        return this._classificationType;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType getClassificationType() 

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
     * Sets the value of field 'classificationPrinciple'.
     * 
     * @param classificationPrinciple the value of field
     * 'classificationPrinciple'.
    **/
    public void setClassificationPrinciple(java.lang.String classificationPrinciple)
    {
        this._classificationPrinciple = classificationPrinciple;
    } //-- void setClassificationPrinciple(java.lang.String) 

    /**
     * Sets the value of field 'classificationType'.
     * 
     * @param classificationType the value of field
     * 'classificationType'.
    **/
    public void setClassificationType(cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType classificationType)
    {
        this._classificationType = classificationType;
    } //-- void setClassificationType(cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.ClassificationInfo unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
