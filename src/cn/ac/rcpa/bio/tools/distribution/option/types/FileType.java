/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: FileType.java,v 1.2 2006/10/02 01:56:05 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Source file type
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:05 $
**/
public class FileType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The BuildSummary type
    **/
    public static final int BUILDSUMMARY_TYPE = 0;

    /**
     * The instance of the BuildSummary type
    **/
    public static final FileType BUILDSUMMARY = new FileType(BUILDSUMMARY_TYPE, "BuildSummary");

    /**
     * The MzIdent type
    **/
    public static final int MZIDENT_TYPE = 1;

    /**
     * The instance of the MzIdent type
    **/
    public static final FileType MZIDENT = new FileType(MZIDENT_TYPE, "MzIdent");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private FileType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.FileType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of FileType
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this FileType
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
        members.put("BuildSummary", BUILDSUMMARY);
        members.put("MzIdent", MZIDENT);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this FileType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new FileType based on the given String value.
     * 
     * @param string
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.types.FileType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid FileType";
            throw new IllegalArgumentException(err);
        }
        return (FileType) obj;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.FileType valueOf(java.lang.String) 

}
