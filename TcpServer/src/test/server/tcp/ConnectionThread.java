package test.server.tcp;


public class ConnectionThread implements Runnable{
	IPCServer sendServer;
	
	public ConnectionThread(){
		sendServer = new IPCServer();
	}
	
	@Override
	public void run() {
		try {	
			while (true){
				Thread.sleep(30000);
				String[] args = {"","off","heartbeat"};
				
				sendServer.main(args);
			}
		} catch (InterruptedException ex){
			
		}
		
	}
	

}