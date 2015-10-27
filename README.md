DeadManCellPhone
================

Projects for dead man cell phone

TCPServer and IPCServer run on the computer running your cues.

TCPServer and IPCServer compile into runnable jars

Order of startup:
  1. TCPServer
  2. Turn on phone app (ServiceExample1), wait for connection to occur
  3. Whenever you want the phone to ring/stop ringing - run the IPCServer via the command


Notes about the project: Some of the coding here may appear sloppy but in actuality it was written this way for a reason. The entire goal of this app is to stay alive at all costs, therefore it runs several processes to ensure that android doesn't kill it while it is still listening for ring commands.

Also, right now the connection IP address is hardcoded into the android application.


I know it's not well documented, maybe someday I'll get back around to making this more developer friendly.
Questions about the project (email me): hufcat05@gmail.com
