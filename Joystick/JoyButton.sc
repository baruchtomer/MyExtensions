JoyButton {
	var <name, <key1, <key2, <value=false, <type='momentary';
	var <def, <>func127, <>func0;

	*new { arg name = \ctrl, key1, key2;
		^super
		.newCopyArgs(name, key1, key2)
		.init()
	}


	init {
		func127 = {};
		func0 = {};
		def = OSCdef(name, { arg msg;
			if (msg[1]==key2) {
				if (type=='momentary') {
					value=(msg[2]>0);
					if (value) { func127.value } { func0.value };
				}
				{
					if (msg[2]>0) {
						value=(value!=true);
						if (value) { func127.value } { func0.value };
					}
				}
			};
		}, key1);
		def.permanent = true;
	}

	set { arg press, unpress, setType;
		if (setType!=nil) {
			if (setType=='toggle') {type='toggle'} {type='momentary'};
		};
		func127 = press;
		func0 = unpress;
		^this
	}
}