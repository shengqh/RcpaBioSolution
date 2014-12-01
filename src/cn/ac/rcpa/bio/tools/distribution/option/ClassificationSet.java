/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: ClassificationSet.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
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
public class ClassificationSet implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private java.util.ArrayList _classificationItemList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ClassificationSet() {
        super();
        _classificationItemList = new ArrayList();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vClassificationItem
    **/
    public void addClassificationItem(ClassificationItem vClassificationItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _classificationItemList.add(vClassificationItem);
    } //-- void addClassificationItem(ClassificationItem) 

    /**
     * 
     * 
     * @param index
     * @param vClassificationItem
    **/
    public void addClassificationItem(int index, ClassificationItem vClassificationItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _classificationItemList.add(index, vClassificationItem);
    } //-- void addClassificationItem(int, ClassificationItem) 

    /**
    **/
    public void clearClassificationItem()
    {
        _classificationItemList.clear();
    } //-- void clearClassificationItem() 

    /**
    **/
    public java.util.Enumeration enumerateClassificationItem()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_classificationItemList.iterator());
    } //-- java.util.Enumeration enumerateClassificationItem() 

    /**
     * 
     * 
     * @param index
    **/
    public ClassificationItem getClassificationItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _classificationItemList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (ClassificationItem) _classificationItemList.get(index);
    } //-- ClassificationItem getClassificationItem(int) 

    /**
    **/
    public ClassificationItem[] getClassificationItem()
    {
        int size = _classificationItemList.size();
        ClassificationItem[] mArray = new ClassificationItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (ClassificationItem) _classificationItemList.get(index);
        }
        return mArray;
    } //-- ClassificationItem[] getClassificationItem() 

    /**
    **/
    public int getClassificationItemCount()
    {
        return _classificationItemList.size();
    } //-- int getClassificationItemCount() 

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
     * @param vClassificationItem
    **/
    public boolean removeClassificationItem(ClassificationItem vClassificationItem)
    {
        boolean removed = _classificationItemList.remove(vClassificationItem);
        return removed;
    } //-- boolean removeClassificationItem(ClassificationItem) 

    /**
     * 
     * 
     * @param index
     * @param vClassificationItem
    **/
    public void setClassificationItem(int index, ClassificationItem vClassificationItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _classificationItemList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _classificationItemList.set(index, vClassificationItem);
    } //-- void setClassificationItem(int, ClassificationItem) 

    /**
     * 
     * 
     * @param classificationItemArray
    **/
    public void setClassificationItem(ClassificationItem[] classificationItemArray)
    {
        //-- copy array
        _classificationItemList.clear();
        for (int i = 0; i < classificationItemArray.length; i++) {
            _classificationItemList.add(classificationItemArray[i]);
        }
    } //-- void setClassificationItem(ClassificationItem) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.ClassificationSet unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
