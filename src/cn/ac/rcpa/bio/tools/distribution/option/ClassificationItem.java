/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: ClassificationItem.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.ArrayList;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:04 $
**/
public class ClassificationItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.lang.String _classifiedName;

    private java.util.ArrayList _experimentNameList;

    private double _experimentValue;

    /**
     * keeps track of state for field: _experimentValue
    **/
    private boolean _has_experimentValue;


      //----------------/
     //- Constructors -/
    //----------------/

    public ClassificationItem() {
        super();
        _experimentNameList = new ArrayList();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vExperimentName
    **/
    public void addExperimentName(java.lang.String vExperimentName)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentNameList.add(vExperimentName);
    } //-- void addExperimentName(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vExperimentName
    **/
    public void addExperimentName(int index, java.lang.String vExperimentName)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentNameList.add(index, vExperimentName);
    } //-- void addExperimentName(int, java.lang.String) 

    /**
    **/
    public void clearExperimentName()
    {
        _experimentNameList.clear();
    } //-- void clearExperimentName() 

    /**
    **/
    public void deleteExperimentValue()
    {
        this._has_experimentValue= false;
    } //-- void deleteExperimentValue() 

    /**
    **/
    public java.util.Enumeration enumerateExperimentName()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_experimentNameList.iterator());
    } //-- java.util.Enumeration enumerateExperimentName() 

    /**
     * Returns the value of field 'classifiedName'.
     * 
     * @return the value of field 'classifiedName'.
    **/
    public java.lang.String getClassifiedName()
    {
        return this._classifiedName;
    } //-- java.lang.String getClassifiedName() 

    /**
     * 
     * 
     * @param index
    **/
    public java.lang.String getExperimentName(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentNameList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (String)_experimentNameList.get(index);
    } //-- java.lang.String getExperimentName(int) 

    /**
    **/
    public java.lang.String[] getExperimentName()
    {
        int size = _experimentNameList.size();
        java.lang.String[] mArray = new java.lang.String[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (String)_experimentNameList.get(index);
        }
        return mArray;
    } //-- java.lang.String[] getExperimentName() 

    /**
    **/
    public int getExperimentNameCount()
    {
        return _experimentNameList.size();
    } //-- int getExperimentNameCount() 

    /**
     * Returns the value of field 'experimentValue'.
     * 
     * @return the value of field 'experimentValue'.
    **/
    public double getExperimentValue()
    {
        return this._experimentValue;
    } //-- double getExperimentValue() 

    /**
    **/
    public boolean hasExperimentValue()
    {
        return this._has_experimentValue;
    } //-- boolean hasExperimentValue() 

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
     * 
     * 
     * @param vExperimentName
    **/
    public boolean removeExperimentName(java.lang.String vExperimentName)
    {
        boolean removed = _experimentNameList.remove(vExperimentName);
        return removed;
    } //-- boolean removeExperimentName(java.lang.String) 

    /**
     * Sets the value of field 'classifiedName'.
     * 
     * @param classifiedName the value of field 'classifiedName'.
    **/
    public void setClassifiedName(java.lang.String classifiedName)
    {
        this._classifiedName = classifiedName;
    } //-- void setClassifiedName(java.lang.String) 

    /**
     * 
     * 
     * @param index
     * @param vExperimentName
    **/
    public void setExperimentName(int index, java.lang.String vExperimentName)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentNameList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _experimentNameList.set(index, vExperimentName);
    } //-- void setExperimentName(int, java.lang.String) 

    /**
     * 
     * 
     * @param experimentNameArray
    **/
    public void setExperimentName(java.lang.String[] experimentNameArray)
    {
        //-- copy array
        _experimentNameList.clear();
        for (int i = 0; i < experimentNameArray.length; i++) {
            _experimentNameList.add(experimentNameArray[i]);
        }
    } //-- void setExperimentName(java.lang.String) 

    /**
     * Sets the value of field 'experimentValue'.
     * 
     * @param experimentValue the value of field 'experimentValue'.
    **/
    public void setExperimentValue(double experimentValue)
    {
        this._experimentValue = experimentValue;
        this._has_experimentValue = true;
    } //-- void setExperimentValue(double) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
