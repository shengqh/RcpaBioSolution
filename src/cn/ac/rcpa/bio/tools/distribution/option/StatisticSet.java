/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: StatisticSet.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
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
public class StatisticSet implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.ArrayList _rangeValueList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StatisticSet() {
        super();
        _rangeValueList = new ArrayList();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.StatisticSet()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vRangeValue
    **/
    public void addRangeValue(double vRangeValue)
        throws java.lang.IndexOutOfBoundsException
    {
        _rangeValueList.add(new Double(vRangeValue));
    } //-- void addRangeValue(double) 

    /**
     * 
     * 
     * @param index
     * @param vRangeValue
    **/
    public void addRangeValue(int index, double vRangeValue)
        throws java.lang.IndexOutOfBoundsException
    {
        _rangeValueList.add(index, new Double(vRangeValue));
    } //-- void addRangeValue(int, double) 

    /**
    **/
    public void clearRangeValue()
    {
        _rangeValueList.clear();
    } //-- void clearRangeValue() 

    /**
    **/
    public java.util.Enumeration enumerateRangeValue()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_rangeValueList.iterator());
    } //-- java.util.Enumeration enumerateRangeValue() 

    /**
     * 
     * 
     * @param index
    **/
    public double getRangeValue(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _rangeValueList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return ((Double)_rangeValueList.get(index)).doubleValue();
    } //-- double getRangeValue(int) 

    /**
    **/
    public double[] getRangeValue()
    {
        int size = _rangeValueList.size();
        double[] mArray = new double[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = ((Double)_rangeValueList.get(index)).doubleValue();
        }
        return mArray;
    } //-- double[] getRangeValue() 

    /**
    **/
    public int getRangeValueCount()
    {
        return _rangeValueList.size();
    } //-- int getRangeValueCount() 

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
     * @param vRangeValue
    **/
    public boolean removeRangeValue(double vRangeValue)
    {
        boolean removed = _rangeValueList.remove(new Double(vRangeValue));
        return removed;
    } //-- boolean removeRangeValue(double) 

    /**
     * 
     * 
     * @param index
     * @param vRangeValue
    **/
    public void setRangeValue(int index, double vRangeValue)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _rangeValueList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _rangeValueList.set(index, new Double(vRangeValue));
    } //-- void setRangeValue(int, double) 

    /**
     * 
     * 
     * @param rangeValueArray
    **/
    public void setRangeValue(double[] rangeValueArray)
    {
        //-- copy array
        _rangeValueList.clear();
        for (int i = 0; i < rangeValueArray.length; i++) {
            _rangeValueList.add(new Double(rangeValueArray[i]));
        }
    } //-- void setRangeValue(double) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.StatisticSet unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.StatisticSet) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.StatisticSet.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.StatisticSet unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
