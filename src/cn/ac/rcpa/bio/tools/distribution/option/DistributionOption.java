/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.4</a>, using an
 * XML Schema.
 * $Id: DistributionOption.java,v 1.2 2006/10/02 01:56:04 sheng Exp $
 */

package cn.ac.rcpa.bio.tools.distribution.option;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Option used to do distribution statistic
 * 
 * @version $Revision: 1.2 $ $Date: 2006/10/02 01:56:04 $
**/
public class DistributionOption implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    private SourceFile _sourceFile;

    private java.lang.String _databaseType;

    private cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType _distributionType;

    private ClassificationInfo _classificationInfo;

    private ClassificationSet _classificationSet;

    private FilterByPeptide _filterByPeptide;

    private StatisticSet _statisticSet;

    private boolean _exportIndividualFractionResult;

    /**
     * keeps track of state for field:
     * _exportIndividualFractionResult
    **/
    private boolean _has_exportIndividualFractionResult;

    private boolean _modifiedPeptideOnly;

    /**
     * keeps track of state for field: _modifiedPeptideOnly
    **/
    private boolean _has_modifiedPeptideOnly;

    private java.lang.String _modifiedAminoacid;


      //----------------/
     //- Constructors -/
    //----------------/

    public DistributionOption() {
        super();
    } //-- cn.ac.rcpa.bio.tools.distribution.option.DistributionOption()


      //-----------/
     //- Methods -/
    //-----------/

    /**
    **/
    public void deleteExportIndividualFractionResult()
    {
        this._has_exportIndividualFractionResult= false;
    } //-- void deleteExportIndividualFractionResult() 

    /**
     * Returns the value of field 'classificationInfo'.
     * 
     * @return the value of field 'classificationInfo'.
    **/
    public ClassificationInfo getClassificationInfo()
    {
        return this._classificationInfo;
    } //-- ClassificationInfo getClassificationInfo() 

    /**
     * Returns the value of field 'classificationSet'.
     * 
     * @return the value of field 'classificationSet'.
    **/
    public ClassificationSet getClassificationSet()
    {
        return this._classificationSet;
    } //-- ClassificationSet getClassificationSet() 

    /**
     * Returns the value of field 'databaseType'.
     * 
     * @return the value of field 'databaseType'.
    **/
    public java.lang.String getDatabaseType()
    {
        return this._databaseType;
    } //-- java.lang.String getDatabaseType() 

    /**
     * Returns the value of field 'distributionType'.
     * 
     * @return the value of field 'distributionType'.
    **/
    public cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType getDistributionType()
    {
        return this._distributionType;
    } //-- cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType getDistributionType() 

    /**
     * Returns the value of field 'exportIndividualFractionResult'.
     * 
     * @return the value of field 'exportIndividualFractionResult'.
    **/
    public boolean getExportIndividualFractionResult()
    {
        return this._exportIndividualFractionResult;
    } //-- boolean getExportIndividualFractionResult() 

    /**
     * Returns the value of field 'filterByPeptide'.
     * 
     * @return the value of field 'filterByPeptide'.
    **/
    public FilterByPeptide getFilterByPeptide()
    {
        return this._filterByPeptide;
    } //-- FilterByPeptide getFilterByPeptide() 

    /**
     * Returns the value of field 'modifiedAminoacid'.
     * 
     * @return the value of field 'modifiedAminoacid'.
    **/
    public java.lang.String getModifiedAminoacid()
    {
        return this._modifiedAminoacid;
    } //-- java.lang.String getModifiedAminoacid() 

    /**
     * Returns the value of field 'modifiedPeptideOnly'.
     * 
     * @return the value of field 'modifiedPeptideOnly'.
    **/
    public boolean getModifiedPeptideOnly()
    {
        return this._modifiedPeptideOnly;
    } //-- boolean getModifiedPeptideOnly() 

    /**
     * Returns the value of field 'sourceFile'.
     * 
     * @return the value of field 'sourceFile'.
    **/
    public SourceFile getSourceFile()
    {
        return this._sourceFile;
    } //-- SourceFile getSourceFile() 

    /**
     * Returns the value of field 'statisticSet'.
     * 
     * @return the value of field 'statisticSet'.
    **/
    public StatisticSet getStatisticSet()
    {
        return this._statisticSet;
    } //-- StatisticSet getStatisticSet() 

    /**
    **/
    public boolean hasExportIndividualFractionResult()
    {
        return this._has_exportIndividualFractionResult;
    } //-- boolean hasExportIndividualFractionResult() 

    /**
    **/
    public boolean hasModifiedPeptideOnly()
    {
        return this._has_modifiedPeptideOnly;
    } //-- boolean hasModifiedPeptideOnly() 

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
     * Sets the value of field 'classificationInfo'.
     * 
     * @param classificationInfo the value of field
     * 'classificationInfo'.
    **/
    public void setClassificationInfo(ClassificationInfo classificationInfo)
    {
        this._classificationInfo = classificationInfo;
    } //-- void setClassificationInfo(ClassificationInfo) 

    /**
     * Sets the value of field 'classificationSet'.
     * 
     * @param classificationSet the value of field
     * 'classificationSet'.
    **/
    public void setClassificationSet(ClassificationSet classificationSet)
    {
        this._classificationSet = classificationSet;
    } //-- void setClassificationSet(ClassificationSet) 

    /**
     * Sets the value of field 'databaseType'.
     * 
     * @param databaseType the value of field 'databaseType'.
    **/
    public void setDatabaseType(java.lang.String databaseType)
    {
        this._databaseType = databaseType;
    } //-- void setDatabaseType(java.lang.String) 

    /**
     * Sets the value of field 'distributionType'.
     * 
     * @param distributionType the value of field 'distributionType'
    **/
    public void setDistributionType(cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType distributionType)
    {
        this._distributionType = distributionType;
    } //-- void setDistributionType(cn.ac.rcpa.bio.tools.distribution.option.types.DistributionType) 

    /**
     * Sets the value of field 'exportIndividualFractionResult'.
     * 
     * @param exportIndividualFractionResult the value of field
     * 'exportIndividualFractionResult'.
    **/
    public void setExportIndividualFractionResult(boolean exportIndividualFractionResult)
    {
        this._exportIndividualFractionResult = exportIndividualFractionResult;
        this._has_exportIndividualFractionResult = true;
    } //-- void setExportIndividualFractionResult(boolean) 

    /**
     * Sets the value of field 'filterByPeptide'.
     * 
     * @param filterByPeptide the value of field 'filterByPeptide'.
    **/
    public void setFilterByPeptide(FilterByPeptide filterByPeptide)
    {
        this._filterByPeptide = filterByPeptide;
    } //-- void setFilterByPeptide(FilterByPeptide) 

    /**
     * Sets the value of field 'modifiedAminoacid'.
     * 
     * @param modifiedAminoacid the value of field
     * 'modifiedAminoacid'.
    **/
    public void setModifiedAminoacid(java.lang.String modifiedAminoacid)
    {
        this._modifiedAminoacid = modifiedAminoacid;
    } //-- void setModifiedAminoacid(java.lang.String) 

    /**
     * Sets the value of field 'modifiedPeptideOnly'.
     * 
     * @param modifiedPeptideOnly the value of field
     * 'modifiedPeptideOnly'.
    **/
    public void setModifiedPeptideOnly(boolean modifiedPeptideOnly)
    {
        this._modifiedPeptideOnly = modifiedPeptideOnly;
        this._has_modifiedPeptideOnly = true;
    } //-- void setModifiedPeptideOnly(boolean) 

    /**
     * Sets the value of field 'sourceFile'.
     * 
     * @param sourceFile the value of field 'sourceFile'.
    **/
    public void setSourceFile(SourceFile sourceFile)
    {
        this._sourceFile = sourceFile;
    } //-- void setSourceFile(SourceFile) 

    /**
     * Sets the value of field 'statisticSet'.
     * 
     * @param statisticSet the value of field 'statisticSet'.
    **/
    public void setStatisticSet(StatisticSet statisticSet)
    {
        this._statisticSet = statisticSet;
    } //-- void setStatisticSet(StatisticSet) 

    /**
     * 
     * 
     * @param reader
    **/
    public static cn.ac.rcpa.bio.tools.distribution.option.DistributionOption unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (cn.ac.rcpa.bio.tools.distribution.option.DistributionOption) Unmarshaller.unmarshal(cn.ac.rcpa.bio.tools.distribution.option.DistributionOption.class, reader);
    } //-- cn.ac.rcpa.bio.tools.distribution.option.DistributionOption unmarshal(java.io.Reader) 

    /**
    **/
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
