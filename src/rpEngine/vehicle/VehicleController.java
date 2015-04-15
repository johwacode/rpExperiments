package rpEngine.vehicle;


public abstract class VehicleController{
	private Vehicle vehicle;
	private String playerName; 
	
	public VehicleController(String driverName){
		playerName = driverName;
	}
	
	public void setVehicle(Vehicle vehicle){
		this.vehicle = vehicle;
	}
}
