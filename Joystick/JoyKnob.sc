JoyKnob {
	var  <name, <key1, <key2;
	var <ctrl, <def, <>func, <value;

	*new { arg name = \ctrl, key1, key2;
		^super
		.newCopyArgs(name, key1, key2)
		.init()
	}


	init {
		func = {};
		value=0.5;
		def = OSCdef(name, { arg msg;
			if (msg[1]==key2) {
				value=msg[2];
				func.value(value, key2);
			};
		}, key1);
		def.permanent = true;
	}

	set { arg funcOn, init;
		if (init!=nil) {value=init};
		func=funcOn;
	}
}