package transaction;

import java.rmi.*;
import java.util.*;

/** 
 * Transaction Manager for the Distributed Travel Reservation System.
 * 
 * Description: toy implementation of the TM
 */

public class TransactionManagerImpl
    extends java.rmi.server.UnicastRemoteObject
    implements TransactionManager {
    
	Hashtable active_list;
	int xidCounter;
	
	   protected ResourceManager rmFlights = null;
	    protected ResourceManager rmRooms = null;
	    protected ResourceManager rmCars = null;
	    protected ResourceManager rmCustomers = null;
	
	
	
    public static void main(String args[]) {
	System.setSecurityManager(new RMISecurityManager());

	String rmiPort = System.getProperty("rmiPort");
	if (rmiPort == null) {
	    rmiPort = "";
	} else if (!rmiPort.equals("")) {
	    rmiPort = "//:" + rmiPort + "/";
	}

	try {
	    TransactionManagerImpl obj = new TransactionManagerImpl();
	    Naming.rebind(rmiPort + TransactionManager.RMIName, obj);
	    System.out.println("TM bound");
	} 
	catch (Exception e) {
	    System.err.println("TM not bound:" + e);
	    System.exit(1);
	}
    }
    
    
    public TransactionManagerImpl() throws RemoteException {
    	
    	active_list=new Hashtable();
    	xidCounter=1;
    	
    }

    
    public int start() 
	throws RemoteException
	{
    	int xid=xidCounter++;
    	Vector<String> v=new Vector<String> ();
    	active_list.put(xid, v);
    	return xid;
    	
	}
    
    
    
    public boolean enlist(int xid, String rmiName)
    throws RemoteException
    {
    	
    	if(!active_list.containsKey(xid))
    	{
    		return false;
    	}
    	
    	try
    	{
    	Vector <String> vs=(Vector <String>)active_list.get(xid);
    	vs.add(rmiName);
    	active_list.put(xid, vs);
    	
    	
    	String rmiPort = System.getProperty("rmiPort");
    	if (rmiPort == null) {
    	    rmiPort = "";
    	} else if (!rmiPort.equals("")) {
    	    rmiPort = "//:" + rmiPort + "/";
    	}
    	
    	
    	if(rmiName.equals("RMFlights"))
    	{
    	 rmFlights =
    			(ResourceManager)Naming.lookup(rmiPort +
    						       ResourceManager.RMINameFlights);
    		    System.out.println("WC bound to RMFlights");
    	}
    		    
    	if(rmiName.equals("RMRooms"))
    	{
    		    rmRooms =
    			(ResourceManager)Naming.lookup(rmiPort +
    						       ResourceManager.RMINameRooms);
    		    System.out.println("WC bound to RMRooms");
    	}
    	
    	if(rmiName.equals("RMCars"))
    	{    	
    		    rmCars =
    			(ResourceManager)Naming.lookup(rmiPort +
    						       ResourceManager.RMINameCars);
    		    System.out.println("WC bound to RMCars");
    	}
    	
    	if(rmiName.equals("RMCustomers"))
    	{
    		    rmCustomers =
    			(ResourceManager)Naming.lookup(rmiPort +
    						       ResourceManager.RMINameCustomers);
    		    System.out.println("WC bound to RMCustomers");
    	}
    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return true;
    }
    
    
    
    public boolean commit(int xid) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException
	 {
    	if(!active_list.containsKey(xid))
    	{
    		return false;
    	}
    	
    	Vector <String> v1= (Vector <String>)active_list.get(xid);
    	
    	Iterator it=v1.iterator();
    	
    	while(it.hasNext())
    	{
    		String rName=(String)it.next();
    		
    		if(rName.equals("RMFlights"))
    		{
    			if(rmFlights.flight_commit(xid))
    			{
    				System.out.println("Flights Committed");
    			}
    			else
    			{	System.out.println("Flight issue");
    				return false;
    			}
    		}
    		
    		
    		if(rName.equals("RMRooms"))
    		{
    			if(rmRooms.hotel_commit(xid))
    			{
    				System.out.println("Hotels Committed");
    			}
    			else
    			{       System.out.println("Room issue");
    				return false;
    			}
    			
    		}
    		
    		if(rName.equals("RMCars"))
    		{	
    			if(rmCars.car_commit(xid))
    			{
    				System.out.println("Cars Committed");
    			}
    			else
    			{       System.out.println("car issue");
    				return false;
    			}
    			
    		}
    		
    		if(rName.equals("RMCustomers"))
    		{
    			if(rmCustomers.customer_commit(xid))
    			{
    				System.out.println("Customers Committed");
    			}
    			else
    			{
    				return false;
    			}
    		}
    	
    	}
    	return true;
	 }
    
    
    
    public void abort(int xid) 
	throws RemoteException, 
	       InvalidTransactionException
	{
    	if(!active_list.containsKey(xid))
    	{
    		System.out.print("Inactive transaction");
    	}
    	
    	Vector <String> v1= (Vector <String>)active_list.get(xid);
    	
    	Iterator it=v1.iterator();
    	
    	while(it.hasNext())
    	{
    		String rName=(String)it.next();
    		
    		if(rName.equals("RMFlights"))
    		{
    			rmFlights.flight_abort(xid);
    		}
    		    		
    		if(rName.equals("RMRooms"))
    		{
    			rmRooms.hotel_abort(xid);	
    		}
    		
    		if(rName.equals("RMCars"))
    		{	
    			rmCars.car_abort(xid);
    		}
    		
    		if(rName.equals("RMCustomers"))
    		{
    			rmCustomers.customer_abort(xid);
    		}
    	
    	}
    	
	}    
    
    
        
    
    public boolean dieNow() 
	throws RemoteException {
	System.exit(1);
	return true; // We won't ever get here since we exited above;
	             // but we still need it to please the compiler.
    }

}

