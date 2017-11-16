OneInstancePlayer {
	var <name, <>synthdef, <>playFunc, <args, <>stopFunc, <>minTime, <>safeTime;
	var <>synth;
	var lastTime=0, safeFlag=true;

	*new { |name, synthdef, playFunc, args, stopFunc, minTime = 0.02, safeTime = 0.01|
		^super
		.newCopyArgs(name, synthdef, playFunc, args, stopFunc, minTime, safeTime)
		.init()
	}

	init {
		if (synthdef.notNil) {SynthDef(name, synthdef).add};
		playFunc = playFunc ? {|name, args| Synth(name, args.getPairs);};
		stopFunc = stopFunc ? {|synth| synth.set(\gate, 0)};
		("args:"++args).postln;
		args = args ? ();
	}

	set { |key, val|
		args[key]=val;
	}

	setFunc {|playFunc_, stopFunc_|
		playFunc = playFunc_ ? playFunc;
		stopFunc = stopFunc_ ? stopFunc;
	}

	play {arg ... args_;
		var msg = Dictionary.newFrom(args_);
		var currentTime = Date.getDate.rawSeconds;
		args_.postln;
		if ((safeFlag) && (currentTime - lastTime >= minTime)) {
			stopFunc.value(synth);
			synth = playFunc.value(name, args.putPairs(msg));
			lastTime=Date.getDate.rawSeconds;
		}
	}


	stop {
		var currentTime = Date.getDate.rawSeconds, return = false;
		if ((currentTime - lastTime) >= safeTime) {
			stopFunc.value(synth);
			synth = nil;
			return = true;
		} {
			safeFlag = false;
			{
				(safeTime - (currentTime - lastTime)).yield;
				stopFunc.value(synth);
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




