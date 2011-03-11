package transaction;

import java.rmi.*;

/** 
 * Interface for the Resource Manager of the Distributed Travel
 * Reservation System.
 * <p>
 * Unlike WorkflowController.java, you are supposed to make changes
 * to this file.
 */

public interface ResourceManager extends Remote {

    public boolean reconnect() 
	throws RemoteException;

    public boolean dieNow() 
	throws RemoteException;


    /** The RMI names a ResourceManager binds to. */
    public static final String RMINameFlights = "RMFlights";
    public static final String RMINameRooms = "RMRooms";
    public static final String RMINameCars = "RMCars";
    public static final String RMINameCustomers = "RMCustomers";
    
    
    public void insert_trans(int xid, String rmiName)
	throws RemoteException, 
    TransactionAbortedException,
    InvalidTransactionException;
    
    
   //RM_FLIGHTS
     
    public boolean addFlight(int xid, String flightNum, int numSeats, int price) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    public boolean deleteFlight(int xid, String flightNum) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    
    public int queryFlight(int xid, String flightNum) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 


    public int queryFlightPrice(int xid, String flightNum) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    public boolean can_reserveFlight(int xid, String custName, String flightNum)
    throws RemoteException, TransactionAbortedException,
    InvalidTransactionException;
    
    public boolean reserveFlight(int xid, String custName, String flightNum) 
	throws RemoteException,
	       TransactionAbortedException,
	       InvalidTransactionException;
    
    public int queryCustomer_Flight_Bill(int xid, String custName)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException;
    
    public boolean unreserve_flights(int xid, String custName)
    throws RemoteException, 
    TransactionAbortedException,
    InvalidTransactionException;
    
    public boolean flight_commit(int xid)
    throws RemoteException, 
           TransactionAbortedException, 
           InvalidTransactionException;
    
    public void flight_abort(int xid)
    throws RemoteException, 
               InvalidTransactionException;
    
    
    
    //RM_hOTELS
    
    public boolean addRooms(int xid, String location, int numRooms, int price) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException;
    
    public boolean deleteRooms(int xid, String location, int numRooms) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    public int queryRooms(int xid, String location)
	throws RemoteException,
	       TransactionAbortedException,
	       InvalidTransactionException;

    public int queryRoomsPrice(int xid, String location) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    public boolean can_reserveRoom(int xid, String custName, String location)
    throws RemoteException, TransactionAbortedException,
    InvalidTransactionException;
    
    public boolean reserveRoom(int xid, String custName, String location) 
	throws RemoteException,
	       TransactionAbortedException,
	       InvalidTransactionException;
    
    public int queryCustomer_Room_Bill(int xid, String custName)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException;
    
    public boolean unreserve_rooms(int xid, String custName)
    throws RemoteException, 
    TransactionAbortedException,
    InvalidTransactionException;
    

    public boolean hotel_commit(int xid)
    throws RemoteException, 
           TransactionAbortedException, 
           InvalidTransactionException;
    
    public void hotel_abort(int xid)
    throws RemoteException, 
               InvalidTransactionException;
    
    
    //rm_cars

        public boolean addCars(int xid, String location, int numCars, int price) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    public boolean deleteCars(int xid, String location, int numCars) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
  

    public int queryCars(int xid, String location) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 


    public int queryCarsPrice(int xid, String location) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException; 
    
    public boolean can_reserveCar(int xid, String custName, String location)
    throws RemoteException, TransactionAbortedException,
    InvalidTransactionException;

    
    public boolean reserveCar(int xid, String custName, String location) 
	throws RemoteException, 
	       TransactionAbortedException,
	       InvalidTransactionException;
    
    public int queryCustomer_Car_Bill(int xid, String custName)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException;
    
    public boolean unreserve_cars(int xid, String custName)
    throws RemoteException, 
    TransactionAbortedException,
    InvalidTransactionException;
    
    public boolean car_commit(int xid)
    throws RemoteException, 
           TransactionAbortedException, 
           InvalidTransactionException;
    
    public void car_abort(int xid)
    throws RemoteException, 
               InvalidTransactionException;
    
    
    //rm_customer
    
    public boolean newCustomer(int xid, String custName) 
	throws RemoteException,
	       TransactionAbortedException,
	       InvalidTransactionException; 
   
    public boolean deleteCustomer(int xid, String custName) 
	throws RemoteException,
	       TransactionAbortedException,
	       InvalidTransactionException; 


    public boolean customer_commit(int xid)
    throws RemoteException, 
           TransactionAbortedException, 
           InvalidTransactionException;
    
    public void customer_abort(int xid)
    throws RemoteException, 
               InvalidTransactionException;
  
}

