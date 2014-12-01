/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: DistributionType.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Protein distribution or peptide distribution
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:04 $
**/
public class DistributionType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The Protein type
    **/
    public static final int PROTEIN_TYPE = 0;

    /**
     * The instance of the Protein type
    **/
    public static final DistributionType PROTEIN = new DistributionType(PROTEIN_TYPE, "Protein");

    /**
     * The Peptide type
    **/
    public static final int PEPTIDE_TYPE = 1;

    /**
     * The instance of the Peptide type
    **/
    public static final DistributionType PEPTIDE = new DistributionType(PEPTIDE_TYPE, "Peptide");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private DistributionType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * DistributionType
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this DistributionType
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
        members.put("Protein", PROTEIN);
        members.put("Peptide", PEPTIDE);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this DistributionType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new DistributionType based on the given String
     * value.
     * 
     * @param string
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid DistributionType";
            throw new IllegalArgumentException(err);
        }
        return (DistributionType) obj;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType valueOf(java.lang.String) 

}
