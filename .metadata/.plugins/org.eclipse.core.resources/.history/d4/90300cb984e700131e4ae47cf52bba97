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
				Thread.sleep(10000);
				String[] args = {"","off","heartbeat"};
				System.out.println("sending heartbeat");
				if (!sendServer.sendMessage(args)){
					break;
				}
			}
		} catch (InterruptedException ex){
			
		}
		
	}
	

}
