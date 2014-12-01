/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: FilterByPeptide.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
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
public class FilterByPeptide implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private cn.ac.rcpa.bio.tools.distribution.option.types.FilterType _filterType;

    private int _from;

    /**
     * keeps track of state for field: _from
    **/
    private boolean _has_from;

    private int _to;

    /**
     * keeps track of state for field: _to
    **/
    private boolean _has_to;

    private int _step;

    /**
     * keeps track of state for field: _step
    **/
    private boolean _has_step;


      //----------------/
     //- Constructors -/
    //----------------/

    public FilterByPeptide() {
        super();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'filterType'.
     * 
     * @return the value of field 'filterType'.
    **/
    public cn.ac.rcpa.bio.tools.distribution.option.types.FilterType getFilterType()
    {
        return this._filterType;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.FilterType getFilterType() 

    /**
     * Returns the value of field 'from'.
     * 
     * @return the value of field 'from'.
    **/
    public int getFrom()
    {
        return this._from;
    } //-- int getFrom() 

    /**
     * Returns the value of field 'step'.
     * 
     * @return the value of field 'step'.
    **/
    public int getStep()
    {
        return this._step;
    } //-- int getStep() 

    /**
     * Returns the value of field 'to'.
     * 
     * @return the value of field 'to'.
    **/
    public int getTo()
    {
        return this._to;
    } //-- int getTo() 

    /**
    **/
    public boolean hasFrom()
    {
        return this._has_from;
    } //-- boolean hasFrom() 

    /**
    **/
    public boolean hasStep()
    {
        return this._has_step;
    } //-- boolean hasStep() 

    /**
    **/
    public boolean hasTo()
    {
        return this._has_to;
    } //-- boolean hasTo() 

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
     * Sets the value of field 'filterType'.
     * 
     * @param filterType the value of field 'filterType'.
    **/
    public void setFilterType(cn.ac.rcpa.bio.tools.distribution.option.types.FilterType filterType)
    {
        this._filterType = filterType;
    } //-- void setFilterType(cn.ac.rcpa.bio.tools.distribution.option.types.FilterType) 

    /**
     * Sets the value of field 'from'.
     * 
     * @param from the value of field 'from'.
    **/
    public void setFrom(int from)
    {
        this._from = from;
        this._has_from = true;
    } //-- void setFrom(int) 

    /**
     * Sets the value of field 'step'.
     * 
     * @param step the value of field 'step'.
    **/
    public void setStep(int step)
    {
        this._step = step;
        this._has_step = true;
    } //-- void setStep(int) 

    /**
     * Sets the value of field 'to'.
     * 
     * @param to the value of field 'to'.
    **/
    public void setTo(int to)
    {
        this._to = to;
        this._has_to = true;
    } //-- void setTo(int) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.FilterByPeptide unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
