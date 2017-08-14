Name: Sanju Kurubara Budi Hall Hiriyanna Gowda
UNCC Id: 800953525

Name: Ashwini Kadam 
UNCC Id: 800967986

Below steps are for executing Distance vector routing protocol on set of routers in the same host.

Step 1: There are 4 java files which need to be compiled. Execute below command
	javac *.java
Step 2: Open n command prompts, where n is number of routers(input files). n command prompts acts as n different routers and distance vectors can be tracked in these command prompts
	Execute below command in all of the n command prompts
	java RouterManager <port_number> <file_name> 
	where, <port_number> should be same in all command prompts and <file_name> should be different input files
	Output will be like below.

Sample output:
------------------ Output Number 6--------------------
Shortest path a-a: the next hop is - and the cost is 0.0
Shortest path a-b: the next hop is b and the cost is 4.0
Shortest path a-c: the next hop is b and the cost is 5.0

Implementation also supports changes in link cost and hence recursive-update problem that involves routing loops
Step 1: while the code is running, change the cost of links in input files. Code automatically handles changes in cost.
	Output after change in link is as below

Sample output:
Link State Change Found!
Link State Change Fixed!
------------------ Output Number 8-------------------- 
Shortest path a-a: the next hop is - and the cost is 0.0 
Shortest path a-b: the next hop is b and the cost is 60.0 
Shortest path a-c: the next hop is b and the cost is 5.0  


Ending step: Kill the process (ctrl-c) in command prompt to stop calculating distance vector for that router.