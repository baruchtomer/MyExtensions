+ Node {
	setArray { arg args;
		server.sendMsg(15, nodeID, *(args.asOSCArgArray));  //"/n_set"
	}
}
			