
/**
 * QueryParameterFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.ideaconsult.iuclidws.query;

public class QueryParameterFault extends java.lang.Exception{
    
    private org.ideaconsult.iuclidws.types.Types.QueryParameterFault faultMessage;
    
    public QueryParameterFault() {
        super("QueryParameterFault");
    }
           
    public QueryParameterFault(java.lang.String s) {
       super(s);
    }
    
    public QueryParameterFault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.ideaconsult.iuclidws.types.Types.QueryParameterFault msg){
       faultMessage = msg;
    }
    
    public org.ideaconsult.iuclidws.types.Types.QueryParameterFault getFaultMessage(){
       return faultMessage;
    }
}
    