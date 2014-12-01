/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: ClassificationType.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Classification type
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:04 $
**/
public class ClassificationType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The MW type
    **/
    public static final int MW_TYPE = 0;

    /**
     * The instance of the MW type
    **/
    public static final ClassificationType MW = new ClassificationType(MW_TYPE, "MW");

    /**
     * The PI type
    **/
    public static final int PI_TYPE = 1;

    /**
     * The instance of the PI type
    **/
    public static final ClassificationType PI = new ClassificationType(PI_TYPE, "PI");

    /**
     * The ABUNDANCE type
    **/
    public static final int ABUNDANCE_TYPE = 2;

    /**
     * The instance of the ABUNDANCE type
    **/
    public static final ClassificationType ABUNDANCE = new ClassificationType(ABUNDANCE_TYPE, "ABUNDANCE");

    /**
     * The METHOD type
    **/
    public static final int METHOD_TYPE = 3;

    /**
     * The instance of the METHOD type
    **/
    public static final ClassificationType METHOD = new ClassificationType(METHOD_TYPE, "METHOD");

    /**
     * The OTHER type
    **/
    public static final int OTHER_TYPE = 4;

    /**
     * The instance of the OTHER type
    **/
    public static final ClassificationType OTHER = new ClassificationType(OTHER_TYPE, "OTHER");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private ClassificationType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * ClassificationType
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this ClassificationType
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
        members.put("MW", MW);
        members.put("PI", PI);
        members.put("ABUNDANCE", ABUNDANCE);
        members.put("METHOD", METHOD);
        members.put("OTHER", OTHER);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this ClassificationType
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new ClassificationType based on the given String
     * value.
     * 
     * @param string
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid ClassificationType";
            throw new IllegalArgumentException(err);
        }
        return (ClassificationType) obj;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.ClassificationType valueOf(java.lang.String) 

}
