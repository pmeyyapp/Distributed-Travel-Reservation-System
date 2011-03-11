package transaction;

import lockmgr.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/** 
 * Resource Manager for the Distributed Travel Reservation System.
 * 
 * Description: toy implementation of the RM
 */


class FLIGHTS implements java.io.Serializable
{
String flightNum;
int price;
int numSeats;
int numAvail;

public FLIGHTS()
{
flightNum="";
price=0;
numSeats=0;
numAvail=0;
}
}


 class HOTELS implements java.io.Serializable
{
String location;
int price;
int numRooms;
int numAvail;

public HOTELS()
{
location="";
price=0;
numRooms=0;
numAvail=0;
}
}

class CARS implements java.io.Serializable
{
String location;
int price;
int numCars;
int numAvail;

public CARS()
{
location="";
price=0;
numCars=0;
numAvail=0;
}
}

class CUSTOMERS implements java.io.Serializable
{
String custName;
public CUSTOMERS()
{
custName="";
}
}

class RESERVATIONS implements java.io.Serializable
{
String custName;
int resvType;
String resvKey;

public RESERVATIONS()
{
custName="";
resvType=0;
resvKey="";
}
}


class TRANSACTION implements java.io.Serializable
{
int xid;

Hashtable update_list,delete_list;

TRANSACTION()
{
    xid=0;
    update_list=new Hashtable();
    delete_list=new Hashtable();
}
}



public class ResourceManagerImpl
    extends java.rmi.server.UnicastRemoteObject
    implements ResourceManager {
    
	
	 public static Hashtable F_ht,H_ht,C_ht,CR_ht,ABORT_ht,trans,memData;
	 
	 public static Hashtable FCR_ht,HCR_ht,CCR_ht;
	 
	 public static Hashtable flightData,carData,hotelData;

     public static  FileInputStream if1;
     public static  FileInputStream if2;
     public static  FileInputStream if3;
     public static  FileInputStream if4;
     
     FileInputStream itemp;
     
     public static  FileOutputStream of1;
     public static FileOutputStream of2;
     public static FileOutputStream of3;
     public static FileOutputStream of4;
     
     public static FileOutputStream otemp;
    
    
     public static ObjectInputStream ob1;
     public static ObjectInputStream ob2;
     public static ObjectInputStream ob3;
     public static ObjectInputStream ob4;
    
     public static ObjectOutputStream obj1;
     public static ObjectOutputStream obj2;
     public static ObjectOutputStream obj3;
     public static ObjectOutputStream obj4;
    
    LockManager lck;
    
	
	
    protected String myRMIName = null; // Used to distinguish this RM from other RMs
    protected TransactionManager tm = null;

    
    
    
    public static void main(String args[]) {
	System.setSecurityManager(new RMISecurityManager());

	String rmiName = System.getProperty("rmiName");
	if (rmiName == null || rmiName.equals("")) {
	    System.err.println("No RMI name given");
	    System.exit(1);
	}

	String rmiPort = System.getProperty("rmiPort");
	if (rmiPort == null) {
	    rmiPort = "";
	} else if (!rmiPort.equals("")) {
	    rmiPort = "//:" + rmiPort + "/";
	}

	try {
	    ResourceManagerImpl obj = new ResourceManagerImpl(rmiName);
	    Naming.rebind(rmiPort + rmiName, obj);
	    System.out.println(rmiName + " bound");
	} 
	catch (Exception e) {
	    System.err.println(rmiName + " not bound:" + e);
	    System.exit(1);
	}
    }
    
    
    
    public ResourceManagerImpl(String rmiName) throws RemoteException {
	myRMIName = rmiName;

	while (!reconnect()) {
	    // would be better to sleep a while
	} 
	
	ABORT_ht=new Hashtable();
	
	if(rmiName.equals(RMINameFlights))
	{
		   if(!new File("data").exists())
		   {
			 new File("data").mkdir();
		   }
		   
		   F_ht= new Hashtable();
		   FCR_ht=new Hashtable();
		   trans=new Hashtable();
		   flightData=new Hashtable();
		   
		   try
		   {
			   
			   System.out.println("Here doood");
			   
			   if1=new FileInputStream("data/Flight_Table");
			   
			   System.out.println("Here dude");
			   
			   ob1 =new ObjectInputStream(if1);
			   
			   System.out.println("Here 2nd dude");
			   
			   flightData=(Hashtable)ob1.readObject();
			   
			   System.out.println("Here 3rrd dude");
			   
			   F_ht=(Hashtable)flightData.get("Flight");
			   FCR_ht=(Hashtable)flightData.get("F_Customer");
			   
			   System.out.println("Here 4th dude");
		   }
		   
		   catch(Exception e)
		   {
			   flightData.clear();
			   flightData.put("Flight", F_ht);
			   flightData.put("F_Customer", FCR_ht);
			   System.out.println( "HIt here" + flightData.size());
		   }
		   lck=new LockManager();
	}
	
	
	
	
	if(rmiName.equals(RMINameRooms))
	{
		   if(!new File("data").exists())
		   {
			 new File("data").mkdir();
		   }
		   
		   H_ht= new Hashtable();
		   HCR_ht=new Hashtable();
		   trans=new Hashtable();
		   hotelData=new Hashtable();
		   
		   try
		   {
			   if2=new FileInputStream("data/Hotel_Table");
			   ob2 =new ObjectInputStream(if2);
			   hotelData=(Hashtable)ob2.readObject();
			   H_ht=(Hashtable)hotelData.get("Hotel");
			   HCR_ht=(Hashtable)hotelData.get("H_Customer");
			   
		   }
		   
		   catch(Exception e)
		   {
			   hotelData.clear();
			   hotelData.put("Hotel", H_ht);
			   hotelData.put("H_Customer", HCR_ht);
		   }
		   lck=new LockManager();
		
	}
	
	
	if(rmiName.equals(RMINameCars))
	{
		   if(!new File("data").exists())
		   {
			 new File("data").mkdir();
		   }
		   
		   C_ht= new Hashtable();
		   CCR_ht=new Hashtable();
		   trans=new Hashtable();
		   carData=new Hashtable();
		   
		   try
		   {
			   if3=new FileInputStream("data/Car_Table");
			   ob3 =new ObjectInputStream(if3);
			   carData=(Hashtable)ob3.readObject();
			   C_ht=(Hashtable)carData.get("Car");
			   CCR_ht=(Hashtable)carData.get("C_Customer");
			   
		   }
		   
		   catch(Exception e)
		   {
			   carData.clear();
			   carData.put("Car", C_ht);
			   carData.put("C_Customer", CCR_ht);
		   }
		   lck=new LockManager();
		
	}
	
	
	if(rmiName.equals(RMINameCustomers))
	{
		   if(!new File("data").exists())
		   {
			 new File("data").mkdir();
		   }
		   
		   CR_ht= new Hashtable();
		   trans=new Hashtable();
		   
		   try
		   {
			   if4=new FileInputStream("data/Customer_Table");
			   ob4 =new ObjectInputStream(if4);
			   trans=new Hashtable();
			   CR_ht=(Hashtable)ob4.readObject();
					   
		   }
		   
		   catch(Exception e)
		   {
			   
		   }
		   lck=new LockManager();	
		
	}	
    }

    
    
    
    //ENLIST
    
    public void insert_trans(int xid, String rmiName)
    {
    	try
    	{
    	if(!trans.containsKey(xid))
    	{
    	TRANSACTION t=new TRANSACTION();
    	t.xid=xid;
    	
    	trans.put(xid,t);
    	System.out.println("Current transaction id " + xid);
    	tm.enlist(xid, rmiName);
    	}
    	}
    	
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	//call enlist here
    }
    
  
    //RM_Flights
    
    public boolean addFlight(int xid, String flightNum, int numSeats, int price) 
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {
    
    	System.out.println("TTTT");
   
    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
    if(!trans.containsKey(xid))
    {
    throw   new InvalidTransactionException(xid,"**");
    }

    TRANSACTION t=(TRANSACTION)trans.get(xid);
    
    try
    {
    if(F_ht.containsKey(flightNum))
    {
    if(lck.lock(xid, F_ht.get(flightNum).toString(), 1))
    {
    FLIGHTS f1=(FLIGHTS)F_ht.get(flightNum);	
    FLIGHTS fnew = new FLIGHTS();
    
    
    fnew.flightNum=flightNum;
    fnew.numSeats=f1.numSeats+numSeats;
    fnew.numAvail=f1.numAvail+numSeats;
    
    if(price>=0)
    {
    fnew.price=price;
    }
    else
    {
    	fnew.price=f1.price;
    }
    System.out.println("SEATS : " + f1.numSeats + "SEATS 1:" +numSeats);
    t.update_list.put(flightNum+"FLI", fnew);
    }
    }
    else
    {
    	   FLIGHTS f=new FLIGHTS();
    	    f.flightNum=flightNum;
    	    f.numSeats=numSeats;
    	    f.numAvail=numSeats;
    	    if(price>=0)
    	    {
    	    f.price=price;
    	    }
    	    
    	    F_ht.put(flightNum, f);
    	    lck.lock(xid, F_ht.get(flightNum).toString(), 1);
    	    
    	    t.update_list.put(flightNum+"FLI", f);
    }
    }
    
    catch(Exception e)
    {
        e.printStackTrace();
    }
    return true;
    }

    
    
    
    public boolean deleteFlight(int xid, String flightNum)

    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {
    	
    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
    	boolean no_reserve=false;
    	
    	if(!trans.containsKey(xid))
    	{
    		return false;
    	}
     	
        TRANSACTION t=(TRANSACTION)trans.get(xid);
        
    	if(!F_ht.containsKey(flightNum))
    	{
    		if(!t.update_list.containsKey(flightNum + "FLI"))
    		{
    		return false;
    		}
    	}
   
         Enumeration e1;
         Iterator it;
         Vector <RESERVATIONS> v1;
         RESERVATIONS r1;
           try
           {
        	  e1=FCR_ht.keys();
        	  while(e1.hasMoreElements() && no_reserve==false)
        	  {
        		  String cust=(String)e1.nextElement();
        		 v1=(Vector <RESERVATIONS>)FCR_ht.get(cust);
        		 if(lck.lock(xid, v1.toString(), 0))
        		 {
        		   System.out.println("inside cust table" + v1.size() + "  " + cust);
        		  it=v1.iterator();
        		 
        		  while(it.hasNext() && no_reserve==false)
        		  {
        			
        			  r1=(RESERVATIONS)it.next();
        			  System.out.println("inside res" + r1.resvType + r1.resvKey + flightNum);
        			  if(r1.resvType==1 && r1.resvKey.equals(flightNum))
        			  {
        				  no_reserve=true;
        			  }
        			  
        		  }
        		 }
        	  }
        	   
        	  
        	  
        	  if(no_reserve==false)
        	  {
        		  if(lck.lock(xid, F_ht.get(flightNum).toString(), 1))
        		  {
        			  FLIGHTS f5=(FLIGHTS)F_ht.get(flightNum);
        			  t.update_list.put(flightNum + "FLD", f5);       		  
        		  }
        	  }
        	  else
        	  {
        		  System.out.println("Reservations exist... Cannot be deleted");
        		  return false;
        	  }      	   
        	  
           }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return true;
  }
        
 
    
    
    
 public int queryFlight(int xid, String flightNum)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {
        int seats=0;

System.out.println("Here" + F_ht.size());


System.out.println("ABORT list" + ABORT_ht.size());

if(ABORT_ht.containsKey(xid))
{
	throw new TransactionAbortedException(xid,"**&");
}


if(!trans.containsKey(xid))
{
	System.out.print("hii");
	return 0;
}



TRANSACTION t=(TRANSACTION)trans.get(xid);

if(!F_ht.containsKey(flightNum))
{
	System.out.println("1st here " + F_ht.size());
	if(!t.update_list.containsKey(flightNum + "FLI"))
	{
		System.out.println("2nd here " + F_ht.size());
		return 0;
	}
}

    try

    {
       
    	System.out.println("t xid" + t.xid + xid);
    	
          if(t.update_list.containsKey(flightNum + "FLI"))
          {
              FLIGHTS f1=(FLIGHTS)t.update_list.get(flightNum + "FLI");
              seats=f1.numAvail;
          	
          }
          
          
          else
          {
        	  if(F_ht.containsKey(flightNum))
        	  {
            if(lck.lock(t.xid, F_ht.get(flightNum).toString(), 0))
            {
            	
                FLIGHTS f=(FLIGHTS)F_ht.get(flightNum);
                System.out.println("SEats ter" + f.numAvail);
                seats=f.numAvail;
                
            }        
         }
       else
       {
           return 0;

       }
       
    }
 
        
      
        
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
 
	System.out.println(seats);  
    return seats;
    }

    
    
    public int queryFlightPrice(int xid, String flightNum)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {
    	
    	int flightprice=0;
         try

            {
        	 
        	 if(ABORT_ht.containsKey(xid))
        	 {
        	 	throw new TransactionAbortedException(xid,"**&");
        	 }

                  TRANSACTION t=(TRANSACTION)trans.get(xid);
                
                  
                  if(t.update_list.containsKey(flightNum + "FLI"))
                  {
                  	 FLIGHTS f1=(FLIGHTS)t.update_list.get(flightNum + "FLI");
                       flightprice=f1.price;
                  }
                  
                  else
                  {
                  
                if(F_ht.containsKey(flightNum))
                {
                    if(lck.lock(t.xid, F_ht.get(flightNum).toString(), 0))
                    {
                        FLIGHTS f=(FLIGHTS)F_ht.get(flightNum);
                        flightprice=f.price;
                        
                    }        
                }
                
             
                
                else
                {
                	FLIGHTS c1=new FLIGHTS();
                    F_ht.put(flightNum, c1);
                    lck.lock(xid, F_ht.get(flightNum).toString(), 0);
                    return 0;

                }
                  }
                
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        
        return flightprice;
    }

    
    public boolean can_reserveFlight(int xid, String custName, String flightNum) 
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException
      {
        if(!trans.containsKey(xid))
        {
        	System.out.print("Invalid transaction");
        	return false;
        }
        
    	
        TRANSACTION t=(TRANSACTION)trans.get(xid);
        
            
    if(!F_ht.containsKey(flightNum))
    {
    	if(!t.update_list.containsKey(flightNum + "FLI"))
    	{
    		if(!t.update_list.containsKey(flightNum + "FLD"))
    		{
    			System.out.println("Invalid flight number");
    			return false;
    		}
    	}
    }
    	return true;
    	
}
    
    public boolean reserveFlight(int xid, String custName, String flightNum) 
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {

    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    if(!trans.containsKey(xid))
    {
    	System.out.print("Invalid transaction");
    	return false;
    }
    	
    TRANSACTION t=(TRANSACTION)trans.get(xid);
    
    if(!FCR_ht.containsKey(custName))
    {
    	Vector<RESERVATIONS> v5=new Vector<RESERVATIONS>();
    	FCR_ht.put(custName, v5);
    	
    	//t.update_list.put(custName + "CUS", v5);
     }
    
    
    

    
    if(!F_ht.containsKey(flightNum))
    {
    	if(!t.update_list.containsKey(flightNum + "FLI"))
    	{
    		if(!t.update_list.containsKey(flightNum + "FLD"))
    		{
    			System.out.println("Invalid flight number");
    			return false;
    		}
    	}
    }
    
    /*
    if(!CR_ht.containsKey(custName))
    {
    	if(!t.update_list.containsKey(custName+"CUS"))
    	{
    		System.out.println("Invalid customer");
        	return false;
    	}
    }
    */
    
    System.out.println("CUST" + custName  + "  Fligt" + flightNum);
    
    
    try
    {
    	if(t.update_list.containsKey(flightNum + "FLD"))
    	{
    		return false;
    	}
    	
    	else if(t.update_list.containsKey(flightNum + "FLI"))
    	{
    		FLIGHTS f3=(FLIGHTS)t.update_list.get(flightNum + "FLI");
    		if((f3.numAvail-1)>=0)
    		{
    			f3.numAvail--;
    			t.update_list.put(flightNum + "FLI", f3);
    		}
    	}
    	
    	else
    	{
    	if(lck.lock(xid, F_ht.get(flightNum).toString(), 0))
    	{
    		FLIGHTS f2=(FLIGHTS)F_ht.get(flightNum);
    		if((f2.numAvail-1)<0)
    		{
    			System.out.println("HIt here");
    		    
    			return false;
    		}
    	}
    	
    	if(lck.lock(xid, F_ht.get(flightNum).toString(),1))
    	{
    	FLIGHTS f1=(FLIGHTS)F_ht.get(flightNum);
    	FLIGHTS f2=new FLIGHTS();
    	f2.flightNum=f1.flightNum;
    	f2.numAvail=f1.numAvail;
    	f2.numSeats=f1.numSeats;
    	f2.price=f1.price;
    	
    	f2.numAvail--;
    	System.out.println("Num seats" + f2.numAvail);
    	t.update_list.put(flightNum+"FLI", f2);
    	}
    	}
    	
    	
    	//customer
    	
		Vector <RESERVATIONS> v1=new Vector <RESERVATIONS>();
		Vector <RESERVATIONS> v2=new Vector <RESERVATIONS>();
    	
		if(t.update_list.containsKey(custName + "CUS"))
		{

			v2=(Vector <RESERVATIONS>) t.update_list.get(custName+"CUS");
	    	
	    	RESERVATIONS r1=new RESERVATIONS();
	    	r1.custName=custName;
	    	r1.resvType=1;
	    	r1.resvKey=flightNum;
	    
	    	System.out.println("INi size" + v2.size());
	    	
	    	v2.add(r1);
	    	
	    	System.out.println("after size" + v2.size());
	    	
	    	t.update_list.put(custName + "CUS", v2);
		}
		
		else
		{

    		if(lck.lock(xid, FCR_ht.get(custName).toString(), 1))
    		{
    		v1=(Vector <RESERVATIONS>) FCR_ht.get(custName);
    		}
   
    		Vector <RESERVATIONS> v3=new Vector <RESERVATIONS>();
        	
    		Iterator it=v1.iterator();
    		
    		while(it.hasNext())
    		{
    			v3.add((RESERVATIONS)it.next());
    		}
    		
    	RESERVATIONS r1=new RESERVATIONS();
    	r1.custName=custName;
    	r1.resvType=1;
    	r1.resvKey=flightNum;
    	
    	v3.add(r1);
    	
    	System.out.println("first size" + v2.size());
    	
    	t.update_list.put(custName+"CUS", v3);
		}
    	
    }
    
    catch(Exception e)
    {
        e.printStackTrace();
    }
        return true;
    }

    
 public int queryCustomer_Flight_Bill(int xid, String custName)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {

    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
    	int counter=0;
    	if(!FCR_ht.containsKey(custName))
    	{
    		return 0;
    	}
    	
    	Vector <RESERVATIONS> v1=new Vector <RESERVATIONS> ();
    	
    	try
    	{
    	if(lck.lock(xid, FCR_ht.get(custName).toString(), 0))
    	{
    		v1=(Vector <RESERVATIONS>)FCR_ht.get(custName);
    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	
    	System.out.println("Size --?" + v1.size());
    	
    	Iterator it= v1.iterator();
    	
    	RESERVATIONS r=new RESERVATIONS();
    	
    	
    	try
    	{
    	while(it.hasNext())
    	{
    		r=(RESERVATIONS)it.next();
    		
    		System.out.println(r.resvType + "  " + r.resvKey);
    		
    		switch(r.resvType)
    		{
    		case 1:
    			if(lck.lock(xid, F_ht.get(r.resvKey).toString(), 0))
    			{
    			FLIGHTS f1=(FLIGHTS)F_ht.get(r.resvKey);
    			counter=counter+f1.price;
    			}
    			break;
    		}	
    	}
    	
    	}
    	
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return counter;
    }
 
 
 
 public boolean unreserve_flights(int xid, String custName)
 throws RemoteException, 
 TransactionAbortedException,
 InvalidTransactionException 
 
 {
	 TRANSACTION t=(TRANSACTION)trans.get(xid);
	 
	 if(!FCR_ht.containsKey(custName))
	 {
		 return false;
	 }
	 
	try
 	{
 		
 	if(t.update_list.containsKey(custName + "CUS"))
 	{
 		Vector <RESERVATIONS> v2= (Vector <RESERVATIONS>) t.update_list.get(custName + "CUS");
 		t.update_list.remove(custName + "CUS");
 		t.update_list.put(custName + "CUD", v2);	
 	}	
 	
 	else
 	{
 		if(lck.lock(xid, FCR_ht.get(custName).toString(), 1))
 		{
 		Vector<RESERVATIONS> v1= (Vector<RESERVATIONS>)FCR_ht.get(custName);
 		t.update_list.put(custName+"CUD", v1);
 		System.out.println("Customer deleted successfully");
 		
 		Iterator it1=v1.iterator();
 		RESERVATIONS r1;
 		
 		System.out.println("V1 SIZE" + v1.size());
 		
 		while(it1.hasNext())
 		{
 			r1=(RESERVATIONS)it1.next();
 			
 			switch(r1.resvType)
 			{
 			case 1:
 				if(lck.lock(xid, F_ht.get(r1.resvKey).toString(), 1))
 				{
 					FLIGHTS f1=(FLIGHTS)F_ht.get(r1.resvKey);
 					FLIGHTS f2=new FLIGHTS();
 					f2.flightNum=f1.flightNum;
 					f2.numAvail=f1.numAvail;
 					f2.numSeats=f1.numSeats;
 					f2.price=f1.price;
 					
 					f2.numAvail++;
 					t.update_list.put(r1.resvKey + "FLI", f2);
 				}
 			break;
 		
 			}
 		}
 		}
 		
 	}

 	}
 	catch(Exception e)
 	{
 		e.printStackTrace();
 	}
	 
	 return true;
 }

  
 
 
 public boolean flight_commit(int xid)

 throws RemoteException, 
        TransactionAbortedException, 
        InvalidTransactionException {
     
 	if(ABORT_ht.containsKey(xid))
 	{
 		throw new TransactionAbortedException(xid,"**&");
 	}

 	
 	
 TRANSACTION t=(TRANSACTION)trans.get(xid);
 
 try
 {
 	
 	/*
 	if(diebeforeFlag)
 	{
 		   of3=new FileOutputStream("data/Abort_List");
 	  	   obj3=new ObjectOutputStream(of3);

 	  	 
 	  	    System.out.println("Trans size" + trans.size());
 	  	    ABORT_ht=trans;
 	  	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
 	  	   obj3.writeObject(ABORT_ht);
 		System.exit(2);
 	}*/
 	
 
 if(!t.update_list.isEmpty())
 {
     try
     {
   Enumeration e1=t.update_list.keys();
      
     while(e1.hasMoreElements())
     {
         String k1=(String)e1.nextElement();
         int len=k1.length();
         String trail=k1.substring(len-3, len);
         String key=k1.substring(0, len-3);
         System.out.println(key + " " + trail);
         
         
     if(trail.equals("FLI"))
     {
     	FLIGHTS f1=(FLIGHTS)t.update_list.get(k1);
     	System.out.println("B4" + F_ht.size());	
         F_ht.put(key,f1);
	       System.out.println("Added");
	       System.out.println("Aft4" + F_ht.size());
     }
     
     if(trail.equals("FLD"))
     {
     	FLIGHTS f2=(FLIGHTS)t.update_list.get(k1);
     	System.out.println("B4" + F_ht.size());	
         F_ht.remove(f2);
	       System.out.println("Deleted");
	       System.out.println("Aft4" + F_ht.size());
     	
     }
     
         
     if(trail.equals("CUS"))
     {
     
     	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
     	System.out.println("B4" + FCR_ht.size());	
         FCR_ht.put(key,v1);
	       
     }
     
     
     if(trail.equals("CUD"))
     {
     
     	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
     	System.out.println("B4" + FCR_ht.size());	
         FCR_ht.remove(key);
	       
     }
     
     }
     }
    catch(Exception e)
    {
 	   e.printStackTrace();
    }
 
 }
 
 
 
 if(lck.unlockAll(t.xid))
 {

 	
	   of1=new FileOutputStream("data/Flight_Table");
	  // of2=new FileOutputStream("data/Flight_Table");
	//   of3=new FileOutputStream("data/Abort_List");
	   

	System.out.println("XID" + t.xid);
	   
	   obj1=new ObjectOutputStream(of1);
	//   obj2=new ObjectOutputStream(of2);
	//   obj3=new ObjectOutputStream(of3);

	   
	   System.out.println("Size " + flightData.size());
	   
	  obj1.writeObject(flightData);
	// obj2.writeObject(flightData);
	 //obj3.writeObject(ABORT_ht);
	 	

     otemp=of1;
     of1=of2;
     of2=otemp;
     
     /*
     itemp=if1;
     if1=if2;       
     if2=itemp;
  */
   
     
  t.update_list.clear();
  t.delete_list.clear();
  trans.remove(xid);

  /*
  if(dieafterFlag)
  {
 	   of3=new FileOutputStream("data/Abort_List");
	   obj3=new ObjectOutputStream(of3);

	 
	    System.out.println("Trans size" + trans.size());
	    ABORT_ht=trans;
	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	   obj3.writeObject(ABORT_ht);
  	System.exit(1);
  }
  */
 }
 
 }
 catch(Exception e)
 {
     e.printStackTrace();
 }
 
 
 System.out.println("Committing at RM_FLIGHTS");
 
 return true;
}
  
 
 public void flight_abort(int xid)
 throws RemoteException, 
            InvalidTransactionException {
 
 	try
		{
 		if(ABORT_ht.containsKey(xid))
 		{
 			throw new TransactionAbortedException(xid,"**&");
 		}

 		
 	if(lck.unlockAll(xid))
 	{
 		
 		trans.remove(xid);
 		
 		System.out.println("Transaction " + xid + " aborted");	
 	}
		}
 		catch (Exception e)
 		{
 			e.printStackTrace();
 		}
 		
 	return;
 }

    
    
    
    //RMHOTELS
    
    public boolean addRooms(int xid, String location, int numRooms, int price) 
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {

    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
        if(!trans.containsKey(xid))
        {
        throw   new InvalidTransactionException(xid,"**");
        }
    	
        TRANSACTION t=(TRANSACTION)trans.get(xid);
        
        
        try
        {
        	if(H_ht.containsKey(location))
        	{
            if(lck.lock (t.xid, H_ht.get(location).toString(),1))
            {
            	HOTELS hold=(HOTELS)H_ht.get(location);
                HOTELS h= new HOTELS();
                h.location=location;
                System.out.println(h.numRooms + "---" + numRooms + " -- New " + h.numRooms );
                h.numRooms=hold.numRooms+numRooms;
                
              
                h.numAvail=hold.numAvail+numRooms;
                
                if(price>=0)
        	    {
        	    h.price=price;
        	    }
                else
                {
                	h.price=hold.price;
                }
          
                t.update_list.put(location+"HOT",h);
            }
        	}
        	else
        	{
        		 HOTELS h=new HOTELS();
                 h.numRooms=numRooms;
                 if(price>=0)
                 {
                 h.price=price;
                 }
                 
                 h.location=location;
                 h.numAvail=numRooms;
                 
                 H_ht.put(location, h);
                 lck.lock(xid, H_ht.get(location).toString(), 1);
                 
                 t.update_list.put(location+"HOT",h);
        	}

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    
    
    public boolean deleteRooms(int xid, String location, int numRooms) 
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException
        {

    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
    	if(!trans.containsKey(xid))
    	{
    		return false;
    	}
    	
        TRANSACTION t=(TRANSACTION)trans.get(xid);
    	
    	if(!H_ht.containsKey(location))
    	{
    		if(!t.update_list.containsKey(location + "HOT"))
    		{
    			System.out.print("iNVALID LOCATION");
    			return false;
    		}
    		
    	}
    	
   
        
     
        try
        {
        	
       if(t.update_list.containsKey(location + "HOT"))
       {
    	
    	   HOTELS h=(HOTELS)t.update_list.get(location + "HOT");
    	   if((h.numAvail-numRooms)>=0 && (h.numRooms-numRooms)>=0)
    	   {
    		   h.numAvail=h.numAvail-numRooms;
    		   h.numRooms=h.numRooms-numRooms;
    	   }
    	   else
    	   {
    		   return false;
    	   }
    	   t.update_list.put(location + "HOT", h);
       }
        	
       else
       {
        	
        	
        if(lck.lock(xid, H_ht.get(location).toString(), 1))
        {
        HOTELS h=(HOTELS)H_ht.get(location);
        
        HOTELS hnew=new HOTELS();
        
        hnew.location=location;
        hnew.numRooms=h.numRooms;
        hnew.price=h.price;
        hnew.numAvail=h.numAvail;
        
        
        if((hnew.numRooms-numRooms)>=0  && (hnew.numAvail-numRooms)>=0)
        {
        	hnew.numRooms=hnew.numRooms-numRooms;
        	hnew.numAvail=hnew.numAvail-numRooms;
        }
        else
        {
        	System.out.println("Invalid operation");
        	return false;
        }
        
        	t.update_list.put(location+"HOT", hnew);
        }
        
       }
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return true;
  }

    
    
    public int queryRooms(int xid, String location)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {
         int rooms=0;
        try

        {
        	
        	if(ABORT_ht.containsKey(xid))
        	{
        		throw new TransactionAbortedException(xid,"**&");
        	}

        	
              TRANSACTION t=(TRANSACTION)trans.get(xid);
              
              
              if(t.update_list.containsKey(location + "HOT"))
              {
              	   HOTELS h1=(HOTELS)t.update_list.get(location + "HOT");
                     rooms=h1.numRooms;
              }
              
              else
              {
            
            if(H_ht.containsKey(location))

            {
                if(lck.lock(t.xid, H_ht.get(location).toString(), 0))
                {
                    HOTELS h=(HOTELS)H_ht.get(location);
                    rooms=h.numAvail;
                    
                }        
            }
            
     
            
            else
            {
            	HOTELS c1=new HOTELS();
                H_ht.put(location, c1);
                lck.lock(xid, H_ht.get(location).toString(), 0);
                return 0;

            }
          }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return rooms;
               
    }

    
    
    public int queryRoomsPrice(int xid, String location)
    throws RemoteException, 
           TransactionAbortedException,
           InvalidTransactionException {
    int roomsprice=0;    
        try

        {
        	if(ABORT_ht.containsKey(xid))
        	{
        		throw new TransactionAbortedException(xid,"**&");
        	}

        	
              TRANSACTION t=(TRANSACTION)trans.get(xid);
              
              
              if(t.update_list.containsKey(location + "HOT"))
              {
              	 HOTELS h1=(HOTELS)t.update_list.get(location + "HOT");
                   roomsprice=h1.price;
              }
              
              else
              {
            
            if(H_ht.containsKey(location))

            {
        		
            	try
            	{
                if(lck.lock(t.xid, H_ht.get(location).toString(), 0))
                {
                    HOTELS h=(HOTELS)H_ht.get(location);
                    roomsprice=h.price;  
                }      
            	}
            	
            	catch(Exception e)
            	{
                	//abort(xid);
            		throw new TransactionAbortedException(xid," ");
			         
            	}

            }
            
        
            else
            {
            	HOTELS c1=new HOTELS();
                H_ht.put(location, c1);
                lck.lock(xid, H_ht.get(location).toString(), 0);
                return 0;

            }
          }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    return roomsprice;
    }

    
    public boolean can_reserveRoom(int xid, String custName, String location)
	throws RemoteException, TransactionAbortedException,
	InvalidTransactionException 
	{
    	TRANSACTION t=(TRANSACTION)trans.get(xid);
        if(!H_ht.containsKey(location))
        {
        	if(!t.update_list.containsKey(location + "HOT"))
        	{
        	System.out.println("Invalid HOTEL");
        	return false;
        	}
       
        }
    	
    	return true;
	}

    
    
public boolean reserveRoom(int xid, String custName, String location)
	throws RemoteException, TransactionAbortedException,
	InvalidTransactionException {
// TODO Auto-generated method stub

if(ABORT_ht.containsKey(xid))
{
	throw new TransactionAbortedException(xid,"**&");
}


TRANSACTION t=(TRANSACTION)trans.get(xid);

if(!HCR_ht.containsKey(custName))
{
	Vector <RESERVATIONS> v7=new Vector<RESERVATIONS>();
	HCR_ht.put(custName, v7);
	
//	t.update_list.put(custName + "CUS", v7);
}


    
    if(!H_ht.containsKey(location))
    {
    	if(!t.update_list.containsKey(location + "HOT"))
    	{
    	System.out.println("Invalid HOTEL");
    	return false;
    	}
   
    }
    
    /*
    if(!CR_ht.containsKey(custName))
    {
    	if(!t.update_list.containsKey(custName))
    	{
    		System.out.println("Invalid customer");
        	return false;
    	}
    }
    */
    
    try
    {
    	//decreasing room count
    	if(t.update_list.containsKey(location+"HOT"))
    	{
    		HOTELS h3=(HOTELS)t.update_list.get(location + "HOT");
    		
    
    		if((h3.numAvail-1)>=0)
    		{
    			h3.numAvail--;
    			t.update_list.put(location + "HOT", h3);
   
    		}
    		else
    		{
    			return false;
    		}
    		
    	}
    	
    	else
    	{
    	if(lck.lock(xid, H_ht.get(location).toString(), 0))
    	{
    		HOTELS h2=(HOTELS)H_ht.get(location);
    		if((h2.numAvail-1)<0)
    		{
    			return false;
    		}
    	}
    	   	
    	if(lck.lock(xid, H_ht.get(location).toString(), 1))
    	{
    	HOTELS h1=(HOTELS)H_ht.get(location);
		HOTELS h2=new HOTELS();
		h2.location=h1.location;
		h2.numAvail=h1.numAvail;
		h2.numRooms=h1.numRooms;
		h2.price=h1.price;
		
    	h2.numAvail--;
    	t.update_list.put(location+"HOT", h2);
    	}
    	}
    	
    	
    	
    	//updating customer reservations
    	Vector <RESERVATIONS> v1=new Vector <RESERVATIONS>();
		Vector <RESERVATIONS> v2=new Vector <RESERVATIONS>();
    	
		if(t.update_list.containsKey(custName + "CUS"))
		{
			v2=(Vector <RESERVATIONS>) t.update_list.get(custName+"CUS");
	    	
	    	RESERVATIONS r1=new RESERVATIONS();
	    	r1.custName=custName;
	    	r1.resvType=2;
	    	r1.resvKey=location;
	    	
	    	v2.add(r1);
	    	
	    	t.update_list.put(custName+"CUS", v2);
		}
		
		else
		{
    		if(lck.lock(xid, HCR_ht.get(custName).toString(), 1))
    		{
    		v1=(Vector <RESERVATIONS>) HCR_ht.get(custName);
    		}
    	
    	Vector <RESERVATIONS> v3=new Vector <RESERVATIONS>();
		    
    	Iterator it=v1.iterator();
    	
    	while(it.hasNext())
    	{
    		v3.add((RESERVATIONS)it.next());
    	}
    	
    	RESERVATIONS r1=new RESERVATIONS();
    	r1.custName=custName;
    	r1.resvType=2;
    	r1.resvKey=location;
    	
    	v3.add(r1);
    	t.update_list.put(custName+"CUS", v3);
		}
    }
    
    catch(Exception e)
    {
        e.printStackTrace();
    }
        return true;
}


public int queryCustomer_Room_Bill(int xid, String custName)
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {

	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	int counter=0;
	if(!HCR_ht.containsKey(custName))
	{
		return 0;
	}
	
	Vector <RESERVATIONS> v1=new Vector <RESERVATIONS> ();
	
	try
	{
	if(lck.lock(xid, HCR_ht.get(custName).toString(), 0))
	{
		v1=(Vector <RESERVATIONS>)HCR_ht.get(custName);
	}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
	Iterator it= v1.iterator();
	
	RESERVATIONS r=new RESERVATIONS();
	
	
	try
	{
	while(it.hasNext())
	{
		r=(RESERVATIONS)it.next();
		
		System.out.println(r.resvType + "  " + r.resvKey);
		
		switch(r.resvType)
		{

		case 2:
			if(lck.lock(xid, H_ht.get(r.resvKey).toString(), 0))
			{
			HOTELS h1=(HOTELS)H_ht.get(r.resvKey);
			counter=counter+h1.price;
			}
			break;

		}	
	}
	
	}
	
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return counter;
}


public boolean unreserve_rooms(int xid, String custName)
throws RemoteException, 
TransactionAbortedException,
InvalidTransactionException 

{
	 TRANSACTION t=(TRANSACTION)trans.get(xid);
	 
	 if(!HCR_ht.containsKey(custName))
	 {
		 return false;
	 }
	 
	try
	{
		
	if(t.update_list.containsKey(custName + "CUS"))
	{
		Vector <RESERVATIONS> v2= (Vector <RESERVATIONS>) t.update_list.get(custName + "CUS");
		t.update_list.remove(custName + "CUS");
		t.update_list.put(custName + "CUD", v2);	
	}	
	
	else
	{
		if(lck.lock(xid, HCR_ht.get(custName).toString(), 1))
		{
		Vector<RESERVATIONS> v1= (Vector<RESERVATIONS>)HCR_ht.get(custName);
		t.update_list.put(custName+"CUD", v1);
		System.out.println("Customer deleted successfully");
		
		Iterator it1=v1.iterator();
		RESERVATIONS r1;
		
		System.out.println("V1 SIZE" + v1.size());
		
		while(it1.hasNext())
		{
			r1=(RESERVATIONS)it1.next();
			
			switch(r1.resvType)
			{
  			case 2:
				if(lck.lock(xid, H_ht.get(r1.resvKey).toString(), 1))
				{
					HOTELS h1=(HOTELS)H_ht.get(r1.resvKey);
					HOTELS h2=new HOTELS();
					
					h2.location=h1.location;
					h2.numAvail=h1.numAvail;
					h2.numRooms=h1.numRooms;
					h2.price=h1.price;
					
					h2.numAvail++;
					t.update_list.put(r1.resvKey, h2);
				}
			break;
			}
		}
		}
		
	}

	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	 
	 return true;
}


public boolean hotel_commit(int xid)

throws RemoteException, 
       TransactionAbortedException, 
       InvalidTransactionException {
    
	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	
TRANSACTION t=(TRANSACTION)trans.get(xid);

try
{
	
	/*
	if(diebeforeFlag)
	{
		   of3=new FileOutputStream("data/Abort_List");
	  	   obj3=new ObjectOutputStream(of3);

	  	 
	  	    System.out.println("Trans size" + trans.size());
	  	    ABORT_ht=trans;
	  	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	  	   obj3.writeObject(ABORT_ht);
		System.exit(2);
	}
	*/

if(!t.update_list.isEmpty())
{
    try
    {
Enumeration e1=t.update_list.keys();
     
    while(e1.hasMoreElements())
    {
        String k1=(String)e1.nextElement();
        int len=k1.length();
        String trail=k1.substring(len-3, len);
        String key=k1.substring(0, len-3);
        System.out.println(key + " " + trail);

    
    if(trail.equals("HOT"))
    {
    	HOTELS h1=(HOTELS)t.update_list.get(k1);
    	System.out.println("B4" + H_ht.size());	
        H_ht.put(key,h1);
       System.out.println("Added");
       System.out.println("Aft4" + H_ht.size());
    }    

    if(trail.equals("CUS"))
    {
    
    	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
    	System.out.println("B4" + HCR_ht.size());	
        HCR_ht.put(key,v1);
        System.out.println("Added");
        System.out.println("Aft4" + HCR_ht.size());
    
    }
    
    
    if(trail.equals("CUD"))
    {
    
    	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
    	System.out.println("B4" + HCR_ht.size());	
        HCR_ht.remove(key);
        System.out.println("Deleted");
        System.out.println("Aft4" + HCR_ht.size());
    
    }
    
    }
    }
   catch(Exception e)
   {
	   e.printStackTrace();
   }

}


if(lck.unlockAll(t.xid))
{

	
	   of1=new FileOutputStream("data/Hotel_Table");
	   of2=new FileOutputStream("data/Hotel_Table");
	   //of3=new FileOutputStream("data/Abort_List");
	   

	   
	   obj1=new ObjectOutputStream(of1);
	   obj2=new ObjectOutputStream(of2);
	 //  obj3=new ObjectOutputStream(of3);

	   
	  obj1.writeObject(hotelData);
	 obj2.writeObject(hotelData);
	// obj3.writeObject(ABORT_ht);
	 	





    otemp=of1;
    of1=of2;
    of2=otemp;
    
    /*
    itemp=if1;
    if1=if2;       
    if2=itemp;
*/
  
    
 t.update_list.clear();
 t.delete_list.clear();
 trans.remove(xid);

 /*
 if(dieafterFlag)
 {
	   of3=new FileOutputStream("data/Abort_List");
	   obj3=new ObjectOutputStream(of3);

	 
	    System.out.println("Trans size" + trans.size());
	    ABORT_ht=trans;
	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	   obj3.writeObject(ABORT_ht);
 	System.exit(1);
 }
 */
}




}
catch(Exception e)
{
    e.printStackTrace();
}


System.out.println("Committing RM_HOTELS");


/*
if(shutFlag)
{
	System.exit(5);
}
*/


return true;
}


public void hotel_abort(int xid)
throws RemoteException, 
           InvalidTransactionException {

	try
		{
		if(ABORT_ht.containsKey(xid))
		{
			throw new TransactionAbortedException(xid,"**&");
		}

		
	if(lck.unlockAll(xid))
	{
		
		trans.remove(xid);
		
		System.out.println("Transaction " + xid + " aborted");	
	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	return;
}

   
    
    
 //RMCars

public boolean addCars(int xid, String location, int numCars, int price) 
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {
    
    	
    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
        if(!trans.containsKey(xid))
        {
        throw   new InvalidTransactionException(xid,"**");
        }
    	
    TRANSACTION t=(TRANSACTION)trans.get(xid);
    
    

    
    try
    {
    	if(C_ht.containsKey(location))
    	{
        if( lck.lock (t.xid, C_ht.get(location).toString(),1))
        {
            CARS c=(CARS)C_ht.get(location);
     
            CARS cnew=new CARS();
            
            cnew.location=location;
            cnew.numCars=c.numCars+numCars;
            
            
            
            cnew.numAvail=c.numAvail+numCars;
            
            if(price>=0)
    	    {
    	    cnew.price=price;
    	    }
            else
            {
            	cnew.price=c.price;
            }
           
            
            
            t.update_list.put(location+"CAR",cnew);
        }
    	}
    	else
    	{
    		  CARS c=new CARS();
              c.numCars=numCars;
              if(price>=0)
              {
              c.price=price;
              }
              c.location=location;
              c.numAvail=numCars;
              
              
              C_ht.put(location, c);
              lck.lock(xid, C_ht.get(location).toString(), 1);
              
              t.update_list.put(location+"CAR",c);
    	}
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }

return true;
}

    
    
public boolean deleteCars(int xid, String location, int numCars) 
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {	

	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	if(!trans.containsKey(xid))
	{
		return false;
	}
	
    TRANSACTION t=(TRANSACTION)trans.get(xid);
	if(!C_ht.containsKey(location))
	{
		if(!t.update_list.containsKey(location + "CAR"))
		{
		return false;
		}
	}

    
 
    try
    {
    	
    if(t.update_list.containsKey(location + "CAR"))
    {
    	CARS c=(CARS)t.update_list.get(location + "CAR");
    	 if((c.numCars-numCars)>=0 && (c.numAvail-numCars)>=0)
         {
         	c.numCars=c.numCars-numCars;
         	c.numAvail=c.numAvail-numCars;
       
         	System.out.println("Now the car ct is " + c.numCars);
         }
         else
         {
         	System.out.println("Invalid operation");
         	return false;
         }
    	 
    	 t.update_list.put(location + "CAR", c);
    	
    }
    	
    	
    else
    {
    	
    	
    	
    if(lck.lock(xid, C_ht.get(location).toString(), 1))
    {
    CARS c=(CARS)C_ht.get(location);
    
    CARS cnew =new CARS();
    
    cnew.location=c.location;
    cnew.numCars=c.numCars;
    cnew.price=c.price;
    cnew.numAvail=c.numAvail;
    
    if((cnew.numCars-numCars)>=0 && (cnew.numAvail-numCars)>=0)
    {
    	cnew.numCars=cnew.numCars-numCars;
    	cnew.numAvail=cnew.numAvail-numCars;
  
    	System.out.println("Now the car ct is " + cnew.numCars);
    }
    else
    {
    	System.out.println("Invalid operation");
    	return false;
    }
  	t.update_list.put(location+"CAR", cnew);
    	
    }
    }
    
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    return true;
    }



public int queryCars(int xid, String location)
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {
     int cars=0;
    try

    {
    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

    	
          TRANSACTION t=(TRANSACTION)trans.get(xid);
        
          if(t.update_list.containsKey(location + "CAR"))
          {
              CARS c1=(CARS)t.update_list.get(location + "CAR");
              cars=c1.numAvail;
          }
          
          else
          {
          
        if(C_ht.containsKey(location))

        {
            if(lck.lock(t.xid, C_ht.get(location).toString(), 0))
            {
                CARS c=(CARS)C_ht.get(location);
                cars=c.numAvail;
                
            }        
        }
        
     
        else
        {
        	CARS c1=new CARS();
            C_ht.put(location, c1);
            lck.lock(xid, C_ht.get(location).toString(), 0);
        	return 0;
        }
      }
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    
return cars;
}



public int queryCarsPrice(int xid, String location)
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {
    
	int carsprice=0;
    try

    {
    	
    	if(ABORT_ht.containsKey(xid))
    	{
    		throw new TransactionAbortedException(xid,"**&");
    	}

          TRANSACTION t=(TRANSACTION)trans.get(xid);    
          
          if(t.update_list.containsKey(location + "CAR"))
          {
         	 CARS c1=(CARS)t.update_list.get(location + "CAR");
              carsprice=c1.price;
         	 
          }
          
          else
          {
        
         if(C_ht.containsKey(location))
        {
            if(lck.lock(t.xid, C_ht.get(location).toString(), 0))
            {
                CARS c=(CARS)C_ht.get(location);
                carsprice=c.price;
                
            }        
        }
        
    
        
        else
        {
        	CARS c1=new CARS();
            C_ht.put(location, c1);
            lck.lock(xid, C_ht.get(location).toString(), 0);
            return 0;

        }
       }
        
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }        
    
return carsprice;
}


public boolean can_reserveCar(int xid, String custName, String location)
throws RemoteException, TransactionAbortedException,
InvalidTransactionException
{
	TRANSACTION t=(TRANSACTION)trans.get(xid);

	if(!C_ht.containsKey(location))
	{
		if(!t.update_list.containsKey(location + "CAR"))
		{
		System.out.println("Invalid location");
		return false;
		}
	}
	
	return true;
}



public boolean reserveCar(int xid, String custName, String location)
throws RemoteException, TransactionAbortedException,
InvalidTransactionException {
// TODO Auto-generated method stub

if(ABORT_ht.containsKey(xid))
{
throw new TransactionAbortedException(xid,"**&");
}


TRANSACTION t=(TRANSACTION)trans.get(xid);

if(!C_ht.containsKey(location))
{
	if(!t.update_list.containsKey(location + "CAR"))
	{
	System.out.println("Invalid location");
	return false;
	}
}


if(!CCR_ht.containsKey(custName))
{
	Vector <RESERVATIONS> v6=new Vector <RESERVATIONS>();
	CCR_ht.put(custName, v6);
	
	//t.update_list.put(custName + "CUS", v6);
	
}


/*
if(!CR_ht.containsKey(custName))
{
	if(!t.update_list.containsKey(custName + "CUS"))
	{
		System.out.println("Invalid customer");
    	return false;
	}
}
*/

try
{
	//decreasing car count
	
	if(t.update_list.containsKey(location + "CAR"))
	{
		CARS c3=(CARS)t.update_list.get(location + "CAR");
		
		System.out.println("B44 " + c3.numAvail);
		if((c3.numAvail-1)>=0)
		{
			c3.numAvail--;
			System.out.println("AFF " + c3.numAvail);
			t.update_list.put(location + "CAR", c3);
		}
		else
		{
			return false;
		}

	}
	
	else
	{
		System.out.println("Got here");
	if(lck.lock(xid, C_ht.get(location).toString(), 0))
	{
		CARS c2=(CARS)C_ht.get(location);
		if((c2.numAvail-1)<0)
		{
			return false;
		}
	}
	

	if(lck.lock(xid, C_ht.get(location).toString(), 1))
	{	
	CARS c1=(CARS)C_ht.get(location);
	CARS c2=new CARS();
	c2.location=c1.location;
	c2.numAvail=c1.numAvail;
	c2.numCars=c1.numCars;
	c2.price=c1.price;
	
	c2.numAvail--;
	t.update_list.put(location+"CAR", c2);
	}
	}
	
	Vector <RESERVATIONS> v1=new Vector <RESERVATIONS>();
	Vector <RESERVATIONS> v2=new Vector <RESERVATIONS>();
	
	
	//updating customers
	
	if(t.update_list.containsKey(custName + "CUS"))
	{
		v2=(Vector <RESERVATIONS>) t.update_list.get(custName+"CUS");
    	
    	RESERVATIONS r1=new RESERVATIONS();
    	r1.custName=custName;
    	r1.resvType=3;
    	r1.resvKey=location;
    	
    	v2.add(r1);
    	
    	t.update_list.put(custName+"CUS", v2);
		
	}
	
	else
	{
  		if(lck.lock(xid, CCR_ht.get(custName).toString(), 1))
		{
		v1=(Vector <RESERVATIONS>) CCR_ht.get(custName);
		
		}
  	
  	Vector <RESERVATIONS> v3=new Vector<RESERVATIONS>();
  	
  	Iterator it=v1.iterator();
  	while(it.hasNext())
  	{
  	v3.add((RESERVATIONS)it.next());
  	}
  	System.out.println(" B4  Sizeof v1 :" + v1.size() + " Size of v3 : " + v3.size() );
  	RESERVATIONS r1=new RESERVATIONS();
	r1.custName=custName;
	r1.resvType=3;
	r1.resvKey=location;
	
	v3.add(r1);
	
	t.update_list.put(custName+"CUS", v3);
	System.out.println("AFTR Sizeof v1 :" + v1.size() + " Size of v3 : " + v3.size() );
	}
}

catch(Exception e)
{
    e.printStackTrace();
}
    return true;
}



public int queryCustomer_Car_Bill(int xid, String custName)
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {

	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	int counter=0;
	if(!CCR_ht.containsKey(custName))
	{
		return 0;
	}
	
	Vector <RESERVATIONS> v1=new Vector <RESERVATIONS> ();
	
	try
	{
	if(lck.lock(xid, CCR_ht.get(custName).toString(), 0))
	{
		v1=(Vector <RESERVATIONS>)CCR_ht.get(custName);
	}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
	Iterator it= v1.iterator();
	
	RESERVATIONS r=new RESERVATIONS();
	
	
	try
	{
	while(it.hasNext())
	{
		r=(RESERVATIONS)it.next();
		
		System.out.println(r.resvType + "  " + r.resvKey);
		
		switch(r.resvType)
		{

		case 3:
			if(lck.lock(xid, C_ht.get(r.resvKey).toString(), 0))
			{
			CARS c1=(CARS)C_ht.get(r.resvKey);
			counter=counter+c1.price;
			}
			break;
		}	
	}
	
	}
	
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return counter;
}



public boolean unreserve_cars(int xid, String custName)
throws RemoteException, 
TransactionAbortedException,
InvalidTransactionException 
{
	 TRANSACTION t=(TRANSACTION)trans.get(xid);
	 
	 if(!CCR_ht.containsKey(custName))
	 {
		 return false;
	 }
	 
	try
	{
		
	if(t.update_list.containsKey(custName + "CUS"))
	{
		Vector <RESERVATIONS> v2= (Vector <RESERVATIONS>) t.update_list.get(custName + "CUS");
		t.update_list.remove(custName + "CUS");
		t.update_list.put(custName + "CUD", v2);	
	}	
	
	else
	{
		if(lck.lock(xid, CCR_ht.get(custName).toString(), 1))
		{
		Vector<RESERVATIONS> v1= (Vector<RESERVATIONS>)CCR_ht.get(custName);
		t.update_list.put(custName+"CUD", v1);
		System.out.println("Customer deleted successfully");
		
		Iterator it1=v1.iterator();
		RESERVATIONS r1;
		
		System.out.println("V1 SIZE" + v1.size());
		
		while(it1.hasNext())
		{
			r1=(RESERVATIONS)it1.next();
			
			switch(r1.resvType)
			{
			case 3:
				if(lck.lock(xid, C_ht.get(r1.resvKey).toString(), 1))
				{
					System.out.println("oye");
					CARS c1=(CARS)C_ht.get(r1.resvKey);
					CARS c2=new CARS();
					
					c2.location=c1.location;
					c2.numAvail=c1.numAvail;
					c2.numCars=c1.numCars;
					c2.price=c1.price;
					
					c2.numAvail++;
					t.update_list.put(r1.resvKey, c2);
				}
			break;
			}
		}
		}
		
	}

	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	 
	 return true;
}



public boolean car_commit(int xid)

throws RemoteException, 
       TransactionAbortedException, 
       InvalidTransactionException {
    
	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	
TRANSACTION t=(TRANSACTION)trans.get(xid);

try
{
	/*
	
	if(diebeforeFlag)
	{
		   of3=new FileOutputStream("data/Abort_List");
	  	   obj3=new ObjectOutputStream(of3);

	  	 
	  	    System.out.println("Trans size" + trans.size());
	  	    ABORT_ht=trans;
	  	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	  	   obj3.writeObject(ABORT_ht);
		System.exit(2);
	}
	*/

if(!t.update_list.isEmpty())
{
    try
    {
Enumeration e1=t.update_list.keys();
     
    while(e1.hasMoreElements())
    {
        String k1=(String)e1.nextElement();
        int len=k1.length();
        String trail=k1.substring(len-3, len);
        String key=k1.substring(0, len-3);
        System.out.println(key + " " + trail);
        
    
    if(trail.equals("CAR"))
    {
    	CARS c1=(CARS)t.update_list.get(k1);
    	System.out.println("B4" + C_ht.size());	
        C_ht.put(key,c1);
       System.out.println("Added");
       System.out.println("Aft4" + C_ht.size());
    }  
        
    if(trail.equals("CUS"))
    {
    
    	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
    	System.out.println("B4" + CCR_ht.size());	
        CCR_ht.put(key,v1);
        System.out.println("Added");
        System.out.println("Aft4" + CCR_ht.size());
    
    }
    
    
    if(trail.equals("CUD"))
    {
    
    	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
    	System.out.println("B4" + CCR_ht.size());	
        CCR_ht.remove(key);
        System.out.println("Deleted");
        System.out.println("Aft4" + CCR_ht.size());
    
    }
    
    }
    }
   catch(Exception e)
   {
	   e.printStackTrace();
   }

}


if(lck.unlockAll(t.xid))
{

	
	   of1=new FileOutputStream("data/Car_Table");
	   of2=new FileOutputStream("data/Car_Table");
	 //  of3=new FileOutputStream("data/Abort_List");
	   

	   
	   obj1=new ObjectOutputStream(of1);
	   obj2=new ObjectOutputStream(of2);
	 //  obj3=new ObjectOutputStream(of3);

	   
	  obj1.writeObject(carData);
	 obj2.writeObject(carData);
	// obj3.writeObject(ABORT_ht);
	 	





    otemp=of1;
    of1=of2;
    of2=otemp;
    itemp=if1;
    if1=if2;       
    if2=itemp;
 
  
    
 t.update_list.clear();
 t.delete_list.clear();
 trans.remove(xid);

 /*
 
 if(dieafterFlag)
 {
	   of3=new FileOutputStream("data/Abort_List");
	   obj3=new ObjectOutputStream(of3);

	 
	    System.out.println("Trans size" + trans.size());
	    ABORT_ht=trans;
	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	   obj3.writeObject(ABORT_ht);
 	System.exit(1);
 }
*/ 
}




}
catch(Exception e)
{
    e.printStackTrace();
}


System.out.println("Committing RM_CARS");

/*
if(shutFlag)
{
	System.exit(5);
}
*/


return true;
}


public void car_abort(int xid)
throws RemoteException, 
           InvalidTransactionException {

	try
		{
		if(ABORT_ht.containsKey(xid))
		{
			throw new TransactionAbortedException(xid,"**&");
		}

		
	if(lck.unlockAll(xid))
	{
		
		trans.remove(xid);
		
		System.out.println("Transaction " + xid + " aborted");	
	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	return;
}

   




//RMCustomers

public boolean newCustomer(int xid, String custName) 
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {

	
	System.out.println("First");
	
	
	if(ABORT_ht.containsKey(xid))
	{
		System.out.println("First 1");
		throw new TransactionAbortedException(xid,"**&");
	}

	
    if(!trans.containsKey(xid))
    {
   	System.out.println("First 2");
    throw   new InvalidTransactionException(xid,"**");
    }
    
	
TRANSACTION t=(TRANSACTION)trans.get(xid);

if(CR_ht.containsKey(custName))
{
	
	System.out.println("Customer already exists");
	return true;
}
else
{
	System.out.println("Success");
	Vector <RESERVATIONS> v1=new Vector<RESERVATIONS>();
	t.update_list.put(custName+"CUS", v1);

}	
return true;
}

//DELETE CUSTOMER

public boolean deleteCustomer(int xid, String custName) 
throws RemoteException, 
       TransactionAbortedException,
       InvalidTransactionException {

	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	TRANSACTION t=(TRANSACTION)trans.get(xid);
	
	
	if(!CR_ht.containsKey(custName))
	{
		if(!t.update_list.containsKey(custName + "CUS"))
		{
			return false;
		}
	}
	
	try
	{
		
	if(t.update_list.containsKey(custName + "CUS"))
	{
		Vector <RESERVATIONS> v2= (Vector <RESERVATIONS>) t.update_list.get(custName + "CUS");
		t.update_list.remove(custName + "CUS");
		t.update_list.put(custName + "CUD", v2);
		
	}	
	
	else
	{
		if(lck.lock(xid, CR_ht.get(custName).toString(), 1))
		{
		Vector<RESERVATIONS> v1= (Vector<RESERVATIONS>)CR_ht.get(custName);
		t.update_list.put(custName+"CUD", v1);
		System.out.println("Customer deleted successfully");
		
		
		/*
		Iterator it1=v1.iterator();
		RESERVATIONS r1;
		
		System.out.println("V1 SIZE" + v1.size());
		
		while(it1.hasNext())
		{
			r1=(RESERVATIONS)it1.next();
			
			switch(r1.resvType)
			{
			case 1:
				if(lck.lock(xid, F_ht.get(r1.resvKey).toString(), 1))
				{
					FLIGHTS f1=(FLIGHTS)F_ht.get(r1.resvKey);
					FLIGHTS f2=f1;
					f2.numAvail++;
					t.update_list.put(r1.resvKey + "FLI", f2);
				}
			break;
			
			case 2:
				if(lck.lock(xid, H_ht.get(r1.resvKey).toString(), 1))
				{
					HOTELS h1=(HOTELS)H_ht.get(r1.resvKey);
					HOTELS h2=h1;
					h2.numAvail++;
					t.update_list.put(r1.resvKey, h2);
				}
			break;
			
			case 3:
				if(lck.lock(xid, C_ht.get(r1.resvKey).toString(), 1))
				{
					System.out.println("oye");
					CARS c1=(CARS)C_ht.get(r1.resvKey);
					CARS c2=c1;
					c2.numAvail++;
					t.update_list.put(r1.resvKey, c2);
				}
			break;
			}
			
			
		}*/	
		}
		
	}

	}
	catch(Exception e)
	{
		
	}
	return true;
	
}



public boolean customer_commit(int xid)
throws RemoteException, 
       TransactionAbortedException, 
       InvalidTransactionException {
    
	if(ABORT_ht.containsKey(xid))
	{
		throw new TransactionAbortedException(xid,"**&");
	}

	
	
TRANSACTION t=(TRANSACTION)trans.get(xid);

try
{
	
	/*
	if(diebeforeFlag)
	{
		   of3=new FileOutputStream("data/Abort_List");
	  	   obj3=new ObjectOutputStream(of3);

	  	 
	  	    System.out.println("Trans size" + trans.size());
	  	    ABORT_ht=trans;
	  	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	  	   obj3.writeObject(ABORT_ht);
		System.exit(2);
	}
	*/

if(!t.update_list.isEmpty())
{
    try
    {
Enumeration e1=t.update_list.keys();
     
    while(e1.hasMoreElements())
    {
        String k1=(String)e1.nextElement();
        int len=k1.length();
        String trail=k1.substring(len-3, len);
        String key=k1.substring(0, len-3);
        System.out.println(key + " " + trail);

    if(trail.equals("CUS"))
    {
    
    	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
    	System.out.println("B4" + CR_ht.size());	
        CR_ht.put(key,v1);
       // System.out.println("Added");
        //System.out.println("Aft4" + C_ht.size());
    
    }
    
    
    if(trail.equals("CUD"))
    {
    
    	Vector <RESERVATIONS> v1=(Vector <RESERVATIONS>) t.update_list.get(k1);
    	System.out.println("B4" + CR_ht.size());	
        CR_ht.remove(key);
   //     System.out.println("Deleted");
   //     System.out.println("Aft4" + C_ht.size());
    
    }
    
    }
    }
   catch(Exception e)
   {
	   e.printStackTrace();
   }

}



if(lck.unlockAll(t.xid))
{

	
	   of1=new FileOutputStream("data/Customer_Table");
	   of2=new FileOutputStream("data/Customer_Table");
	//   of3=new FileOutputStream("data/Abort_List");
	   

	   
	   obj1=new ObjectOutputStream(of1);
	   obj2=new ObjectOutputStream(of2);
	 //  obj3=new ObjectOutputStream(of3);

	   
	  obj1.writeObject(CR_ht);
	 obj2.writeObject(CR_ht);
//	 obj3.writeObject(ABORT_ht);
	 	





    otemp=of1;
    of1=of2;
    of2=otemp;
    
    /*
    itemp=if1;
    if1=if2;       
    if2=itemp;
 */
  
    
 t.update_list.clear();
 t.delete_list.clear();
 trans.remove(xid);

 
 /*
 if(dieafterFlag)
 {
	   of3=new FileOutputStream("data/Abort_List");
	   obj3=new ObjectOutputStream(of3);

	 
	    System.out.println("Trans size" + trans.size());
	    ABORT_ht=trans;
	   System.out.println("Sizes" + ABORT_ht.size() + trans.size());
	   obj3.writeObject(ABORT_ht);
 	System.exit(1);
 }
 */
}




}
catch(Exception e)
{
    e.printStackTrace();
}


System.out.println("Committing at RM_CUSTOMER");


/*
if(shutFlag)
{
	System.exit(5);
}
*/


return true;
}


public void customer_abort(int xid)
throws RemoteException, 
           InvalidTransactionException {
	try
		{
		if(ABORT_ht.containsKey(xid))
		{
			throw new TransactionAbortedException(xid,"**&");
		}

		
	if(lck.unlockAll(xid))
	{
		
		trans.remove(xid);
		
		System.out.println("Transaction " + xid + " aborted");	
	}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	return;
}

   
    

    public boolean reconnect()
	throws RemoteException {
	String rmiPort = System.getProperty("rmiPort");
	if (rmiPort == null) {
	    rmiPort = "";
	} else if (!rmiPort.equals("")) {
	    rmiPort = "//:" + rmiPort + "/";
	}

	try {
	    tm = (TransactionManager)Naming.lookup(rmiPort + TransactionManager.RMIName);
	    System.out.println(myRMIName + " bound to TM");
	} 
	catch (Exception e) {
	    System.err.println(myRMIName + " cannot bind to TM:" + e);
	    return false;
	}

	return true;
    }

    
    
    public boolean dieNow() 
	throws RemoteException {
	System.exit(1);
	return true; // We won't ever get here since we exited above;
	             // but we still need it to please the compiler.
    }
}

