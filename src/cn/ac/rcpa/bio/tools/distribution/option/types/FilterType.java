/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: FilterType.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Filter classification item
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:04 $
**/
public class FilterType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The UniquePeptideCount type
    **/
    public static final int UNIQUEPEPTIDECOUNT_TYPE = 0;

    /**
     * The instance of the UniquePeptideCount type
    **/
    public static final FilterType UNIQUEPEPTIDECOUNT = new FilterType(UNIQUEPEPTIDECOUNT_TYPE, "UniquePeptideCount");

    /**
     * The PeptideCount type
    **/
    public static final int PEPTIDECOUNT_TYPE = 1;

    /**
     * The instance of the PeptideCount type
    **/
    public static final FilterType PEPTIDECOUNT = new FilterType(PEPTIDECOUNT_TYPE, "PeptideCount");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private FilterType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.FilterType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of FilterTyp
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this FilterType
    **/
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
    **/
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("UniquePeptideCount", UNIQUEPEPTIDECOUNT);
        members.put("PeptideCount", PEPTIDECOUNT);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this FilterType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new FilterType based on the given String value.
     * 
     * @param string
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.types.FilterType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid FilterType";
            throw new IllegalArgumentException(err);
        }
        return (FilterType) obj;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.FilterType valueOf(java.lang.String) 

}
