OneInstancePlayer {
	var <name, <synthdef, <>func, <args, <>stopfunc, <>minTime, <>safeTime;
	var <>synth;
	var lastTime=0, safeFlag=true;

	*new { |name, synthdef, func, args, stopfunc, minTime = 0.02, safeTime = 0.01|
		^super
		.newCopyArgs(name, synthdef, func, args, stopfunc, minTime, safeTime)
		.init()
	}

	init {
		SynthDef(name, synthdef).add;
		func = func ? {|name, args| Synth(name, args.getPairs);};
		stopfunc = stopfunc ? {|synth| synth.set(\gate, 0)};
		("args:"++args).postln;
		args = args ? ();
	}

	set { |key, val|
		args[key]=val;
	}

	play {arg ... args_;
		var msg = Dictionary.newFrom(args_);
		var currentTime = Date.getDate.rawSeconds;
		args_.postln;
		if ((safeFlag) && (currentTime - lastTime >= minTime)) {
			stopfunc.value(synth);
			synth = func.value(name, args.putPairs(msg));
			lastTime=Date.getDate.rawSeconds;
		}
	}


	stop {
		var currentTime = Date.getDate.rawSeconds, return = false;
		if ((currentTime - lastTime) >= safeTime) {
			stopfunc.value(synth);
			synth = nil;
			return = true;
		} {
			safeFlag = false;
			{
				(safeTime - (currentTime - lastTime)).yield;
				stopfunc.value(synth);
				synth = nil;
				safeFlag = true;
			}.fork;
			return = false;
		};
		return.postln;
		^return
	}

	free {
		synth.free;
		synth.stop;
	}
}




