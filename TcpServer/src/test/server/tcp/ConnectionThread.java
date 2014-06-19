package test.server.tcp;


public class ConnectionThread implements Runnable{
	IPCServer sendServer;
	TCPServer server;
	
	public ConnectionThread(TCPServer server){
		sendServer = new IPCServer();
		this.server = server;
	}
	
	@Override
	public void run() {
		try {	
			while (true){
				Thread.sleep(2000);
				String[] args = {"","stuff","heartbeat"};
				//System.out.println("sending heartbeat");
				server.sendMessage("heartbeat");
			}
		} catch (InterruptedException ex){
			
		}
		
	}
	

}
