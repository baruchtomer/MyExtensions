JoystickMapper {
	var <defaultPath, <controls, <buttons;

	*new { arg defaultPath = '/joy';
		^super
		.newCopyArgs(defaultPath)
		.init()
	}

	init {

		controls = Dictionary.new;
		buttons = Array.newClear(24);

		this.postInfo;
		SystemClock.sched(0.5,{this.setup});

	}

	postInfo {
		''.postln;
		('Joystick:').postln;
		(Char.tab ++ 'controls - ' ++ controls.size).postln;
		(Char.tab ++ 'buttons - ' ++ buttons.size).postln;
		''.postln;
	}

	//detectInPort{^(MIDIClient.sources.detect({arg item;
	//	item.device.find("nanoKONTROL2").notNil}) !? _.uid ? 0);}


	createControl { arg key;
		var name = \knob ++ '_' ++ key;
		controls.add(key -> JoyKnob(name, defaultPath, key));

	}

	createButton { arg index, key;
		var name = \button ++ '_' ++ index;
		buttons = buttons.put(index, JoyButton(name, defaultPath, key));
	}



	setup {

		this.createControl('x');
		this.createControl('y');
		this.createControl('phi');
		this.createControl('throt');
		this.createControl('mode');


		this.createButton(0, 'fire');
		this.createButton(1, 'fire2');
		this.createButton(2, 'b3');
		this.createButton(3, 'b4');
		this.createButton(4, 'b5');
		this.createButton(5, 'b6');
		this.createButton(6, 'b7');
		this.createButton(7, 'b8');
		this.createButton(8, 'b9');
		this.createButton(9, 'b10');
		this.createButton(10, 'b11');
		this.createButton(11, 'b12');


	}


}