
/**
 * ReferentialIntegrityEngineFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.referentialintegrity;

public class ReferentialIntegrityEngineFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.ReferentialIntegrityEngineFault faultMessage;
    
    public ReferentialIntegrityEngineFault() {
        super("ReferentialIntegrityEngineFault");
    }
           
    public ReferentialIntegrityEngineFault(java.lang.String s) {
       super(s);
    }
    
    public ReferentialIntegrityEngineFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.ReferentialIntegrityEngineFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.ReferentialIntegrityEngineFault getFaultMessage(){
       return faultMessage;
    }
}
    