package transaction;

import java.rmi.*;
import java.util.*;

/** 
 * Workflow Controller for the Distributed Travel Reservation System.
 * 
 * Description: toy implementation of the WC.  In the real
 * implementation, the WC should forward calls to either RM or TM,
 * instead of doing the things itself.
 */





public class WorkflowControllerImpl
    extends java.rmi.server.UnicastRemoteObject
    implements WorkflowController {

    protected int flightcounter, flightprice, carscounter, carsprice, roomscounter, roomsprice; 
    protected int xidCounter;
    
    Hashtable Transaction;
    
    protected ResourceManager rmFlights = null;
    protected ResourceManager rmRooms = null;
    protected ResourceManager rmCars = null;
    protected ResourceManager rmCustomers = null;
    protected TransactionManager tm = null;

    public static void main(String args[]) {
System.setSecurityManager(new RMISecurityManager());

String rmiPort = System.getProperty("rmiPort");
if (rmiPort == null) {
   rmiPort = "";
} else if (!rmiPort.equals("")) {
   rmiPort = "//:" + rmiPort + "/";
}

try {
   WorkflowControllerImpl obj = new WorkflowControllerImpl();
   Naming.rebind(rmiPort + WorkflowController.RMIName, obj);
   System.out.println("WC bound");
}
catch (Exception e) {
   System.err.println("WC not bound:" + e);
   System.exit(1);
}
    }
    
    
    public WorkflowControllerImpl() throws RemoteException {
flightcounter = 0;
flightprice = 0;
carscounter = 0;
carsprice = 0;
roomscounter = 0;
roomsprice = 0;
flightprice = 0;

xidCounter = 1;

Transaction =new Hashtable();

while (!reconnect()) {
   // would be better to sleep a while
} 
}



    
    
    // TRANSACTION INTERFACE
public int start()
throws RemoteException {

	int xid=0;
	try
	{
    xid=tm.start();
    Transaction.put(xid, xid);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
return xid;
}



public boolean commit(int xid)
throws RemoteException, 
      TransactionAbortedException, 
      InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{	System.out.println("fails here ");
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	
	if(tm.commit(xid))
	{	System.out.println("Hit here");
		return true;
	}
	return false;
}



public void abort(int xid)
throws RemoteException, 
               InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	tm.abort(xid);
return;
}


    // ADMINISTRATIVE INTERFACE
public boolean addFlight(int xid, String flightNum, int numSeats, int price) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
rmFlights.insert_trans(xid,ResourceManager.RMINameFlights);
	
if(rmFlights.addFlight(xid,flightNum,numSeats,price))
{
return true;
}
return false;
}



public boolean deleteFlight(int xid, String flightNum)
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
	
	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	rmFlights.insert_trans(xid,ResourceManager.RMINameFlights);
	
     if(rmFlights.deleteFlight(xid,flightNum))
     {
    	 return true;
     }
     return false;
}


public int queryFlight(int xid, String flightNum)
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
	
	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	rmFlights.insert_trans(xid,ResourceManager.RMINameFlights);
	
     flightcounter=rmFlights.queryFlight(xid,flightNum);
     
     return flightcounter;
}

 public int queryFlightPrice(int xid, String flightNum)
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
	
		if(!Transaction.containsKey(xid))
		{
			throw new InvalidTransactionException(xid,"@#$");
		}
	 
	 rmFlights.insert_trans(xid,ResourceManager.RMINameFlights);
	 
     flightprice=rmFlights.queryFlightPrice(xid,flightNum);
     
     return flightprice;
}

 
 public boolean reserveFlight(int xid, String custName, String flightNum) 
    throws RemoteException, 
          TransactionAbortedException,
          InvalidTransactionException {
 
		if(!Transaction.containsKey(xid))
		{
			throw new InvalidTransactionException(xid,"@#$");
		}
	 
	 rmFlights.insert_trans(xid,ResourceManager.RMINameFlights);
	 
	 if(rmFlights.reserveFlight(xid, custName, flightNum))
         {
		 return true;
         }

    return false;
}
    


 
 //HOTELS
 
 
 public boolean addRooms(int xid, String location, int numRooms, int price) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
	 
		if(!Transaction.containsKey(xid))
		{
			throw new InvalidTransactionException(xid,"@#$");
		}
	 
rmRooms.insert_trans(xid,ResourceManager.RMINameRooms);
	 
if(rmRooms.addRooms(xid,location,numRooms,price))
{
	return true;
}

return false;
}

 
 
public boolean deleteRooms(int xid, String location, int numRooms) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
rmRooms.insert_trans(xid,ResourceManager.RMINameRooms);
	
if(rmRooms.deleteRooms(xid,location,numRooms))
{
return true;
}


return false;
}
    
    
public int queryRooms(int xid, String location)
    throws RemoteException, 
          TransactionAbortedException,
          InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
rmRooms.insert_trans(xid,ResourceManager.RMINameRooms);
	  	
   roomscounter=rmRooms.queryRooms(xid,location);
         
return roomscounter;
}



public int queryRoomsPrice(int xid, String location)
    throws RemoteException, 
          TransactionAbortedException,
          InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	rmRooms.insert_trans(xid,ResourceManager.RMINameRooms);
	
        roomsprice=rmRooms.queryRoomsPrice(xid,location);

        return roomsprice;

}
    
public boolean reserveRoom(int xid, String custName, String location) 
        throws RemoteException, 
              TransactionAbortedException,
              InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	rmRooms.insert_trans(xid,ResourceManager.RMINameRooms);
	
    if(rmRooms.reserveRoom(xid, custName, location))
    {
    	return true;
    }
    
    return false;
}

    

        //CARS

public boolean addCars(int xid, String location, int numCars, int price) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
	
	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	
	rmCars.insert_trans(xid,ResourceManager.RMINameCars);
	
	if(rmCars.addCars(xid,location,numCars,price))
	{
		return true;
	}
	
     return false;
}




public boolean deleteCars(int xid, String location, int numCars) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
	
	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
rmCars.insert_trans(xid,ResourceManager.RMINameCars);
	
if(rmCars.deleteCars(xid,location,numCars))
{
return true;
}

return false;

}



public int queryCars(int xid, String location)
    throws RemoteException, 
          TransactionAbortedException,
          InvalidTransactionException {
	
	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	rmCars.insert_trans(xid,ResourceManager.RMINameCars);
	
         carscounter=rmCars.queryCars(xid,location);
         
    return carscounter;
}


 public int queryCarsPrice(int xid, String location)
    throws RemoteException, 
          TransactionAbortedException,
          InvalidTransactionException {
		if(!Transaction.containsKey(xid))
		{
			throw new InvalidTransactionException(xid,"@#$");
		}
	 
	 
	 rmCars.insert_trans(xid,ResourceManager.RMINameCars);
	 
       carsprice=rmCars.queryCarsPrice(xid,location);
      
       return carsprice;
}
    

 public boolean reserveCar(int xid, String custName, String location) 
throws RemoteException, 
   TransactionAbortedException,
   InvalidTransactionException {
	 
		if(!Transaction.containsKey(xid))
		{
			throw new InvalidTransactionException(xid,"@#$");
		}
	 
	 rmCars.insert_trans(xid,ResourceManager.RMINameCars);
	 
if(rmCars.reserveCar(xid, custName, location))
{
return true;
}

return false;
}

 
 
 
 //CUSTOMERS
    
    public boolean newCustomer(int xid, String custName) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
    	
    	if(!Transaction.containsKey(xid))
    	{
    		throw new InvalidTransactionException(xid,"@#$");
    	}
    	
    	rmCustomers.insert_trans(xid, ResourceManager.RMINameCustomers);
    	
     if(rmCustomers.newCustomer(xid,custName))
     {
    	 return true;
     }
    	
     return false;
}

    
public boolean deleteCustomer(int xid, String custName) 
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {

	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	rmCustomers.insert_trans(xid, ResourceManager.RMINameCustomers);
	
	if(rmCustomers.deleteCustomer(xid,custName))
	{
     return true;
	}
 
	return false;
        
}

  
    
    
    public int queryCustomerBill(int xid, String custName)
throws RemoteException, 
      TransactionAbortedException,
      InvalidTransactionException {
    	
    	if(!Transaction.containsKey(xid))
    	{
    		throw new InvalidTransactionException(xid,"@#$");
    	}
    	
    	rmFlights.insert_trans(xid, ResourceManager.RMINameFlights);
    	rmCars.insert_trans(xid, ResourceManager.RMINameRooms);
    	rmRooms.insert_trans(xid, ResourceManager.RMINameCars);
    	rmCustomers.insert_trans(xid, ResourceManager.RMINameCustomers);
    	
   int Customerbill=rmFlights.queryCustomer_Flight_Bill(xid, custName) + rmCars.queryCustomer_Car_Bill(xid, custName) + rmRooms.queryCustomer_Room_Bill(xid, custName);

   return Customerbill;
}


    // RESERVATION INTERFACE
    
 
public boolean reserveItinerary(int xid, String custName, List flightNumList, String location, boolean needCar, boolean needRoom)
        throws RemoteException,
TransactionAbortedException,
InvalidTransactionException {
	
	if(!Transaction.containsKey(xid))
	{
		throw new InvalidTransactionException(xid,"@#$");
	}
	
	Iterator it1=flightNumList.iterator();
	Iterator it2=flightNumList.iterator();
	
	if(flightNumList.size()>0)
	{
	rmFlights.insert_trans(xid, ResourceManager.RMINameFlights);
	}
	
	
	while(it1.hasNext())
	{
		String flnum=(String)it1.next();
		
		if(!rmFlights.can_reserveFlight(xid, custName, flnum))
		{
			return false;
		}
		
	}
	
	if(needCar)
	{
		
		rmCars.insert_trans(xid, ResourceManager.RMINameCars);
		
		if(!rmCars.can_reserveCar(xid, custName, location))
		{
			return false;
		}
	}
	
	
	if(needRoom)
	{
		
		rmRooms.insert_trans(xid, ResourceManager.RMINameRooms);
		
		if(!rmRooms.can_reserveRoom(xid, custName, location))
		{
			return false;
		}
	}
	
	
	
	
	while(it2.hasNext())
	{
		String fnum= (String)it2.next();
		
		if(!rmFlights.reserveFlight(xid, custName, fnum))
		{
			return false;
		}
	}
	
	
	if(needCar)
	{
		
		if(!rmCars.reserveCar(xid, custName, location))
		{
			return false;
		}
	}
	
	
	if(needRoom)
	{
		
		if(!rmRooms.reserveRoom(xid, custName, location))
		{
			return false;
		}
	}
	
	
return true;

}

    // TECHNICAL/TESTING INTERFACE
    public boolean reconnect()
throws RemoteException {
String rmiPort = System.getProperty("rmiPort");
if (rmiPort == null) {
   rmiPort = "";
} else if (!rmiPort.equals("")) {
   rmiPort = "//:" + rmiPort + "/";
}

try {
   rmFlights =
(ResourceManager)Naming.lookup(rmiPort +
      ResourceManager.RMINameFlights);
   System.out.println("WC bound to RMFlights");
   rmRooms =
(ResourceManager)Naming.lookup(rmiPort +
      ResourceManager.RMINameRooms);
   System.out.println("WC bound to RMRooms");
   rmCars =
(ResourceManager)Naming.lookup(rmiPort +
      ResourceManager.RMINameCars);
   System.out.println("WC bound to RMCars");
   rmCustomers =
(ResourceManager)Naming.lookup(rmiPort +
      ResourceManager.RMINameCustomers);
   System.out.println("WC bound to RMCustomers");
   tm =
(TransactionManager)Naming.lookup(rmiPort +
 TransactionManager.RMIName);
   System.out.println("WC bound to TM");
} 
catch (Exception e) {
   System.err.println("WC cannot bind to some component:" + e);
   return false;
}

try {
   if (rmFlights.reconnect() && rmRooms.reconnect() &&
rmCars.reconnect() && rmCustomers.reconnect()) {
return true;
   }
} catch (Exception e) {
   System.err.println("Some RM cannot reconnect:" + e);
   return false;
}

return false;
    }

    public boolean dieNow(String who)
throws RemoteException {
if (who.equals(TransactionManager.RMIName) ||
   who.equals("ALL")) {
   try {
tm.dieNow();
   } catch (RemoteException e) {}
}
if (who.equals(ResourceManager.RMINameFlights) ||
   who.equals("ALL")) {
   try {
rmFlights.dieNow();
   } catch (RemoteException e) {}
}
if (who.equals(ResourceManager.RMINameRooms) ||
   who.equals("ALL")) {
   try {
rmRooms.dieNow();
   } catch (RemoteException e) {}
}
if (who.equals(ResourceManager.RMINameCars) ||
   who.equals("ALL")) {
   try {
rmCars.dieNow();
   } catch (RemoteException e) {}
}
if (who.equals(ResourceManager.RMINameCustomers) ||
   who.equals("ALL")) {
   try {
rmCustomers.dieNow();
   } catch (RemoteException e) {}
}
if (who.equals(WorkflowController.RMIName) ||
   who.equals("ALL")) {
   System.exit(1);
}
return true;
    }
    public boolean dieRMAfterEnlist(String who)
throws RemoteException {
return true;
    }
    public boolean dieRMBeforePrepare(String who)
throws RemoteException {
return true;
    }
    public boolean dieRMAfterPrepare(String who)
throws RemoteException {
return true;
    }
    public boolean dieTMBeforeCommit()
throws RemoteException {
return true;
    }
    public boolean dieTMAfterCommit()
throws RemoteException {
return true;
    }
    public boolean dieRMBeforeCommit(String who)
throws RemoteException {
return true;
    }
    public boolean dieRMBeforeAbort(String who)
throws RemoteException {
return true;
    }
}
